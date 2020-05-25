package dao

import java.security.{MessageDigest, SecureRandom}
import java.util.UUID

import javax.inject.Inject
import org.joda.time.DateTime
import scalikejdbc._

class PositionDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def getPosition(userId: Int): Option[DAOPosition] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT id, longitude, latitude FROM public.position WHERE id = $userId ORDER BY timestamp DESC LIMIT 1".map { row =>
        DAOPosition(row.int("id"), row.float("longitude"), row.float("latitude"))
      }.first().apply()
    }
  }

  def setPosition(userId: Int ,lat: Float,long: Float): Unit = {
    withSession(pool) { implicit session =>
      sql"INSERT INTO public.position (id, longitude, latitude) VALUES ($userId, $long,$lat)".executeUpdate().apply()
    }
  }

  def getHistory(userId: Int, from: DateTime, to: DateTime): Seq[DAOPosition] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT longitude, latitude FROM public.position WHERE id = $userId AND timestamp between $from and $to ORDER BY timestamp ASC".map{ row =>
        DAOPosition(userId, row.float("longitude"), row.float("latitude"))
      }.list().apply()
    }
  }

}


case class DAOPosition(userID:Integer,longitude:Float,latitude:Float)