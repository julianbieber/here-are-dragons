package dao

import java.sql.Timestamp

import javax.inject.Inject
import org.joda.time.DateTime
import scalikejdbc.{ConnectionPool, _}
import util.TimeUtil

class ActivityDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def startActivity(user: Int, activityType: String): Unit = {
    withSession(pool) { implicit session: DBSession =>
      val activityId = sql"SELECT id FROM public.activity_type WHERE name = $activityType".map { col =>
        col.int("id")
      }.first().apply().getOrElse(throw new RuntimeException(s"Unsupported activityType: $activityType"))
      val time: DateTime = TimeUtil.now
      val notProcessed = notProcessedForUser(user, activityId, time.plusMinutes(1)).sortBy(_.endTimestamp.get.getMillis)
      if (notProcessed.isEmpty) {
        sql"INSERT INTO public.activity (userId, start_timestamp, activity_id) VALUES ($user, $time, $activityId)".executeUpdate().apply()
      } else {
        val toDelete = notProcessed.tail
        val toUpdate = notProcessed.head
        sql"update public.activity set end_timestamp = null where userid = $user and start_timestamp = ${toUpdate.startTimestamp}".executeUpdate().apply()
        if (toDelete.nonEmpty) {
          sql"delete from public.activity where userid = {u} and start_timestamp = {t} and end_timestamp is not NULL".batchByName(toDelete.map { activity =>
            Seq("u" -> activity.user, "t" -> activity.startTimestamp)
          }: _*).apply()
        }
      }
    }
  }


  def stopActivity(user: Int, start: DateTime): Unit = {
    withSession(pool) { implicit session: DBSession =>
      val time: DateTime = TimeUtil.now
      sql"update public.activity set end_timestamp = $time where userid = $user and start_timestamp = $start".executeUpdate().apply()
    }
  }

  def getCurrentActivity(user: Int): Option[(String, DateTime)] = {
    withReadOnlySession(pool) { implicit session: DBSession =>
      sql"SELECT activity_id, start_timestamp FROM public.activity WHERE userid = $user and end_timestamp is NULL LIMIT 1"
        .map(c => (c.int("activity_id"), toDateTime(c.timestamp("start_timestamp").toZonedDateTime)))
        .first()
        .apply()
        .flatMap { case (activityId, start) =>
          sql"SELECT name FROM public.activity_type WHERE id = $activityId".map(_.string("name") -> start).first().apply()
        }
    }
  }

  def getNotProcessedActivities(): Seq[DAOActivity] = {
    withReadOnlySession(pool) { implicit session: DBSession =>
      notProcessedQuery
    }
  }

  def getAllActivities(): Seq[DAOActivity] = {
    withReadOnlySession(pool) { implicit session: DBSession =>
      sql"SELECT userid, activity_id, start_timestamp, end_timestamp, processed FROM public.activity"
        .map { row =>
          val startTimestamp = toDateTime(row.dateTime("start_timestamp"))
          val endTimestamp = toDateTime(row.dateTime("end_timestamp"))
          DAOActivity(row.int("userid"), row.int("activity_id"), startTimestamp, Some(endTimestamp), row.boolean("processed"))
        }.list().apply()
    }
  }

  private def notProcessedQuery(implicit session: DBSession): Seq[DAOActivity] = {
    val ninetySecondsAgo = TimeUtil.now.minusSeconds(90)
    sql"SELECT userid, activity_id, start_timestamp, end_timestamp FROM public.activity a WHERE a.processed = false and end_timestamp is not NULL and end_timestamp < $ninetySecondsAgo"
      .map { row =>
        val startTimestamp = toDateTime(row.dateTime("start_timestamp"))
        val endTimestamp = toDateTime(row.dateTime("end_timestamp"))
        DAOActivity(row.int("userid"), row.int("activity_id"), startTimestamp, Some(endTimestamp), false)
      }.list().apply()
  }

  private def notProcessedForUser(user: Int, activityId: Int, maxEnd: DateTime)(implicit session: DBSession): Seq[DAOActivity] = {
    sql"SELECT userid, start_timestamp, end_timestamp FROM public.activity WHERE userid = $user and activity_id = $activityId and processed = false and end_timestamp is not NULL and end_timestamp < $maxEnd"
      .map { row =>
        val startTimestamp = toDateTime(row.dateTime("start_timestamp"))
        val endTimestamp = toDateTime(row.dateTime("end_timestamp"))
        DAOActivity(row.int("userid"), activityId, startTimestamp, Some(endTimestamp), false)
      }.list().apply()
  }

  def setProcessed(activities: Seq[DAOActivity]): Unit = {
    withSession(pool) { implicit session: DBSession =>
      sql"UPDATE public.activity SET processed = true where userid = {u} and start_timestamp = {t}".batchByName(activities.map { activity =>
        Seq("u" -> activity.user, "t" -> activity.startTimestamp)
      }: _*).apply()
    }
  }
}

case class DAOActivity(user: Int, activity: Int, startTimestamp: DateTime, endTimestamp: Option[DateTime], processed: Boolean)
