package dao

import java.sql.Timestamp

import javax.inject.Inject
import org.joda.time.DateTime
import scalikejdbc.{ConnectionPool, _}
import util.TimeUtil

class ActivityDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  private val oneMinute = 60 * 1000

  def startActivity(user: Int, activityType: String): Unit = {
    withSession(pool) { implicit session: DBSession =>
      val activityId = sql"SELECT id FROM public.activity_type WHERE name = $activityType".map { col =>
        col.int("id")
      }.first().apply().getOrElse(throw new RuntimeException(s"Unsupported activityType: $activityType"))
      val time: DateTime = TimeUtil.now
      sql"INSERT INTO public.activity (userId, timestamp, activity_id, start, stop) VALUES ($user, $time, $activityId, true, false)".executeUpdate().apply()
    }
  }

  def combineActivities(): Unit = {
    withSession(pool) { implicit session: DBSession =>

      val mostRecentStopsPerUser = sql"select userid, timestamp, activity_id from public.activity where processed = false and stop = true;".map { row =>
        val user = row.int("userid")
        val timestamp = row.timestamp(2)
        val activity = row.int("activity_id")
        (user, timestamp, activity)
      }.list().apply().groupBy(v => v._1 -> v._3).mapValues(_.map(v => v._2).sortBy(_.getTime)).toSeq

      val mostRecentStartsPerUser = sql"select userid, timestamp, activity_id from public.activity where processed = false and start = true".map { row =>
        val user = row.int("userid")
        val timestamp = row.timestamp(2)
        val activity = row.int("activity_id")
        (user, timestamp, activity)
      }.list().apply().groupBy(v => v._1 -> v._3).mapValues(_.map(v => v._2).sortBy(_.getTime))

      val toDelete = mostRecentStopsPerUser.flatMap { case ((user, activity), stops) =>
        val starts = mostRecentStartsPerUser(user -> activity)
        val history: Seq[(Timestamp, Boolean)] = (stops.map(_ -> false) ++ starts.map(_ -> true)).sortBy(_._1.getTime)
        // Under the assumption that the oldest unprocessed activity for a user must be a start event
        history.takeRight(history.size - 1).grouped(2).collect { case Seq((t1, false), (t2, true)) =>
          if (t2.getTime - t1.getTime < oneMinute) {
            Seq(user -> t1, user -> t2)
          } else {
            Seq()
          }
        }.flatten
      }


      sql"delete from public.activity where userid = {u} and timestamp = {t}".batchByName(toDelete.map { case (user, t) =>
        Seq("u" -> user, "t" -> t)
      }: _*).apply()
    }
  }

  def stopActivity(user: Int, activityType: String): Unit = {
    withSession(pool) { implicit session: DBSession =>
      val activityId = sql"SELECT id FROM public.activity_type WHERE name = $activityType".map { col =>
        col.int("id")
      }.first().apply().getOrElse(throw new RuntimeException(s"Unsupported activityType: $activityType"))
      val time: DateTime = TimeUtil.now
      sql"INSERT INTO public.activity (userId, timestamp, activity_id, start, stop) VALUES ($user, $time, $activityId, false, true)".executeUpdate().apply()
    }
  }

  def getCurrentActivity(user: Int): Option[String] = {
    withReadOnlySession(pool) { implicit session: DBSession =>
      sql"SELECT activity_id, start FROM public.activity WHERE userid = $user ORDER BY timestamp DESC LIMIT 1"
        .map(c => (c.int("activity_id"), c.boolean("start")))
        .first()
        .apply()
        .flatMap { case (activityId, start) =>
          if (start) {
            sql"SELECT name FROM public.activity_type WHERE id = $activityId ".map(_.string("name")).first().apply()
          } else {
            None
          }
        }
    }
  }

  def getNotProcessedActivities(): Seq[DAOActivity] = {
    withReadOnlySession(pool) { implicit session: DBSession =>
      sql"SELECT userid, activity_id, timestamp, start, stop FROM public.activity a WHERE a.processed = false ORDER BY a.timestamp ASC"
        .map { row =>
          val timestamp = toDateTime(row.dateTime("timestamp"))
          DAOActivity(row.int("userid"), row.int("activity_id"), timestamp, row.boolean("start"), row.boolean("stop"))
        }.list().apply()
    }
  }

  def setProcessed(activities: Seq[DAOActivity]): Unit = {
    withSession(pool) { implicit session: DBSession =>
      sql"UPDATE public.activity SET processed = true where userid = {u} and timestamp = {t}".batchByName(activities.map { activity =>
        Seq("u" -> activity.user, "t" -> activity.timestamp)
      }: _*).apply()
    }
  }
}

case class DAOActivity(user: Int, activity: Int, timestamp: DateTime, start: Boolean, stop: Boolean)
