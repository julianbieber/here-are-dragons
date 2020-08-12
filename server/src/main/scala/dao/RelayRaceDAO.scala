package dao

import java.sql.Timestamp

import javax.inject.Inject
import org.joda.time.DateTime
import scalikejdbc.{ConnectionPool, _}
import util.TimeUtil

class RelayRaceDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def start(groupId: String, users: Seq[Int], activityType: String): DateTime = {
    withSession(pool) { implicit session: DBSession =>
      val activityId = sql"SELECT id FROM public.activity_type WHERE name = $activityType".map { col =>
        col.int("id")
      }.first().apply().getOrElse(throw new RuntimeException(s"Unsupported activityType: $activityType"))
      val time: DateTime = TimeUtil.now
      sql"INSERT INTO public.relay_race (group_id, users, start_timestamp, activity_id) VALUES ($groupId, ${users.toArray}, $time, $activityId)".executeUpdate().apply()
      time
    }
  }


  def stop(user: Int): Unit = {
    withSession(pool) { implicit session: DBSession =>
      val time: DateTime = TimeUtil.now
      sql"update public.relay_race set end_timestamp = $time where $user = any(users) and end_timestamp is null".executeUpdate().apply()
    }
  }

  def getNotProcessedRelayRaces(): Seq[RelayRaceRow] = {
    withReadOnlySession(pool) { implicit session: DBSession =>
      sql"SELECT group_id, users, activity_id, start_timestamp, end_timestamp FROM public.relay_race a WHERE a.processed = false and end_timestamp is not NULL"
        .map { row =>
          val startTimestamp = toDateTime(row.dateTime("start_timestamp"))
          val endTimestamp = toDateTime(row.dateTime("end_timestamp"))
          RelayRaceRow(
            row.string("group_id"),
            row.array("users").getArray.asInstanceOf[Array[Integer]].toSeq.map(_.intValue()),
            row.int("activity_id"),
            startTimestamp,
            Some(endTimestamp),
            false
          )
        }.list().apply()
    }
  }

  def setProcessed(group2Start: Seq[(String, DateTime)]): Unit = {
    withSession(pool) { implicit session: DBSession =>
      sql"UPDATE public.relay_race SET processed = true where group_id = {g} and start_timestamp = {t}".batchByName(group2Start.map {case (group, start) =>
        Seq("g" -> group, "t" -> start)
      }: _*).apply()
    }
  }
}

case class RelayRaceRow(groupId: String, users: Seq[Int], activity: Int, startTimestamp: DateTime, endTimestamp: Option[DateTime], processed: Boolean)
