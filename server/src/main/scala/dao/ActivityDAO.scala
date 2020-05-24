package dao

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
      sql"INSERT INTO public.activity (userId, timestamp, activity_id, start, stop) VALUES ($user, $time, $activityId, true, false)".executeUpdate().apply()
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
}
