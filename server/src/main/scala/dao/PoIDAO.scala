package dao

import javax.inject.Inject
import scalikejdbc._

import scala.collection.JavaConverters._
import scala.collection.immutable.List

class PoIDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def createPoIFromAPI(poiID: Long, longitude: Float, latitude: Float, priority: Float, tags: Option[String]): Option[Long] = {
    withSession(pool) { implicit session =>
      val questNumber:Long = poiID;
      sql"INSERT INTO public.poi (id, longitude, latitude, priority, tags) VALUES ($poiID,$longitude, $latitude, $priority,$tags) ON CONFLICT (id) DO NOTHING".executeUpdate().apply()
      Some(questNumber)
    }
  }


  def deletePoI(poiid: Long): Unit = {
    withSession(pool) { implicit session =>
      sql"DELETE FROM public.poi WHERE id = $poiid".execute().apply()
    }
  }

  def getPoIs(poiId: Long): Option[DAOPoI] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT id, longitude, latitude, priority, tags FROM public.poi WHERE id = $poiId".map { row =>
        DAOPoI(row.long("id"), row.float("longitude"), row.float("latitude"), row.float("priority"),row.stringOpt("tags"))
      }.first().apply()
    }
  }



}


case class DAOPoI(questID: Long, longitude: Float, latitude: Float, priority: Float, tags: Option[String])


