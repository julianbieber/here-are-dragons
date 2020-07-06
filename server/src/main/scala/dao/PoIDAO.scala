package dao

import javax.inject.Inject
import scalikejdbc._

import scala.collection.JavaConverters._
import scala.collection.immutable.List

class PoIDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def createPoI(poiID: Long, longitude: Float, latitude: Float, priority: Float, tags: Option[String]): Option[Long] = {
    withSession(pool) { implicit session =>
      val questID: Long = poiID;
      sql"INSERT INTO public.poi (id, longitude, latitude, priority, tags) VALUES ($poiID,$longitude, $latitude, $priority,$tags) ON CONFLICT (id) DO NOTHING".executeUpdate().apply()
      Some(questID)
    }
  }

  def getPoIs(): List[DAOPoI] = {
    withReadOnlySession(pool) { implicit session =>
      val pois: List[DAOPoI] =
        sql"""SELECT id, longitude, latitude, priority, tags FROM public.poi""".map(f = row =>
          DAOPoI(
            row.long("id"),
            row.float("longitude"),
            row.float("latitude"),
            row.float("priority"),
            row.stringOpt("tags"),
          )
        ).list.apply()
      pois
    }
  }
}


case class DAOPoI(poiID: Long, longitude: Float, latitude: Float, priority: Float, tags: Option[String])


