package dao

import javax.inject.Inject
import scalikejdbc._
import scala.collection.immutable.List
import scala.util.Random

class PoIDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def createPoI(poiID: Long, longitude: Float, latitude: Float, priority: Float, tags: Option[String]): Option[Long] = {
    withSession(pool) { implicit session =>
      val questID: Long = poiID;
      sql"INSERT INTO public.poi (id, longitude, latitude, priority, tags) VALUES ($poiID,$longitude, $latitude, $priority,$tags) ON CONFLICT (id) DO NOTHING".executeUpdate().apply()
      Some(questID)
    }
  }

  def getPoIs(long:Float, lat:Float): List[DAOPoI] = {
    withReadOnlySession(pool) { implicit session =>
      val pois: List[DAOPoI] = {
          val longitudemin = long - 1
          val longitudemax = long + 1
          val latitudemin = lat - 1
          val latitudemax = lat + 1
          sql"""SELECT id, longitude, latitude, priority, tags FROM public.poi WHERE (longitude BETWEEN $longitudemin AND $longitudemax) and (latitude BETWEEN $latitudemin AND $latitudemax) """.map(f = rowPoi =>
            DAOPoI(
              rowPoi.long("id"),
              rowPoi.float("longitude"),
              rowPoi.float("latitude"),
              rowPoi.float("priority"),
              rowPoi.stringOpt("tags"),
            )
          ).list().apply()
        }
      Random.shuffle(pois).take(34)
    }
  }


}


case class DAOPoI(poiID: Long, longitude: Float, latitude: Float, priority: Float, tags: Option[String])


