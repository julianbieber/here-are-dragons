package dao

import java.security.{MessageDigest, SecureRandom}
import java.util.UUID

import javax.inject.Inject
import scalikejdbc._

class PositionDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def getPosition(userId:Int): Option[DAOPosition] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT id, longitude, latitude FROM public.position WHERE id = $userId".map { row =>
        DAOPosition(row.int("id"), row.float("longitude"), row.float("latitude"))
      }.first().apply()
    }
  }

  def setPosition(userID: Integer,lat:Float,long:Float): Unit = {
    withSession(pool) { implicit session =>
      sql"INSERT INTO public.position (id, longitude, latitude) VALUES ($userID, $long,$lat) ON CONFLICT (id) DO UPDATE SET longitude=excluded.longitude,latitude =excluded.latitude".executeUpdate().apply()
    }
  }

}


case class DAOPosition(userID:Integer,longitude:Float,latitude:Float)