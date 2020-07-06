package dao

import javax.inject.Inject
import scalikejdbc._

import scala.collection.JavaConverters._
import scala.collection.immutable.List

class QuestDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {


  def fillDatabaseWithQuestsFromPoIs(listOfPoIs:List[DAOPoI]): Unit = {
    withSession(pool) { implicit session =>
      for(poi <- listOfPoIs){
        sql"INSERT INTO public.quest (id) VALUES (${poi.poiID}) ON CONFLICT (id) DO NOTHING".executeUpdate().apply()
      }
    }
  }

  def makeQuestActive(questID: Long,userId :Int) : Unit = {
    withSession(pool) { implicit session =>
      sql"UPDATE public.quest SET active_user_ids = active_user_ids + $userId WHERE id = $questID AND idx(active_user_ids, $userId) = 0".executeUpdate().apply()
      sql"UPDATE public.quest SET activatable_user_ids = activatable_user_ids - $userId WHERE id = $questID".executeUpdate().apply()
    }
  }

  def makeQuestUnActive(questID: Long,userId :Int) : Unit = {
    withSession(pool) { implicit session =>
      sql"UPDATE public.quest SET active_user_ids = active_user_ids - $userId WHERE id = $questID ".executeUpdate().apply()
      sql"UPDATE public.quest SET activatable_user_ids = activatable_user_ids + $userId WHERE id = $questID AND idx(activatable_user_ids, $userId) = 0".executeUpdate().apply()
    }
  }

  def getActiveUsersForQuest(questID: Long) : Option[Array[Int]] = {
    withSession(pool) { implicit session =>
      sql"""SELECT active_user_ids FROM public.quest WHERE id = $questID""".map {row =>
        row.array("active_user_ids").getArray().asInstanceOf[Array[Integer]].map(_.intValue())
      }.first().apply()
    }
  }

  def getActivatableUserForQuest(questID: Long) : Option[Array[Int]] = {
    withSession(pool) { implicit session =>
      sql""" SELECT activatable_user_ids FROM public.quest WHERE id = $questID""".map { row =>
        row.array("activatable_user_ids").getArray().asInstanceOf[Array[Integer]].map(_.intValue())
      }.first().apply()
    }
  }

  def getQuestsFromDatabase(): List[DAOQuest] = {
    withSession(pool) { implicit session =>
      val daoquests: List[DAOQuest] =
        sql"""SELECT id FROM public.quest""".map(rowQuest =>
          sql"""SELECT longitude, latitude FROM public.poi WHERE id = ${rowQuest.long("id")}""".map(rowPoi =>
            DAOQuest(
              rowQuest.long("id"),
              rowPoi.float("longitude"),
              rowPoi.float("latitude"),
            )
          ).list.apply()
        ).list.apply().flatten
      daoquests
    }
  }

  def deleteQuest(questid: Long): Unit = {
    withSession(pool) { implicit session =>
      sql"DELETE FROM public.quest WHERE id = $questid".execute().apply()
    }
  }

  def getQuests(questId: Long): Option[DAOQuest] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT id, longitude, latitude FROM public.quest WHERE id = $questId".map { row =>
        DAOQuest(row.int("id"), row.float("longitude"), row.float("latitude"))
      }.first().apply()
    }
  }

  def toMeter(distanceFromPosition: Float): Float = distanceFromPosition / 11100f



  def getListOfActivataibleQuestsNerby(longitude: Float, latitude: Float, distanceFromPosition: Float,userId:Int): List[DAOQuest] = {
    //distanceFromPosition wird in Metern übergeben und danach mit der Methode toDegree in GeoCoordinaten
    //umgerechnet
    val distanceFromPositionInDegree = toMeter(distanceFromPosition)
    val longitudemin = longitude - distanceFromPositionInDegree;
    val longitudemax = longitude + distanceFromPositionInDegree;
    val latitudemin = latitude - distanceFromPositionInDegree;
    val latitudemax = latitude + distanceFromPositionInDegree;

    withReadOnlySession(pool) { implicit session =>
      val daoquests: List[DAOQuest] =
        sql"""SELECT id FROM public.quest WHERE idx(activatable_user_ids, $userId) <> 0 """.map { rowQuest =>
          println(rowQuest.long("id"))
          sql"""SELECT longitude, latitude FROM public.poi WHERE (longitude BETWEEN $longitudemin AND $longitudemax) and (latitude BETWEEN $latitudemin AND $latitudemax) AND (id = ${rowQuest.long("id")})""".map(rowPoi =>
            DAOQuest(
              rowQuest.long("id"),
              rowPoi.float("longitude"),
              rowPoi.float("latitude"),
            )
          ).list().apply()
        }.list().apply().flatten
      daoquests
    }
  }

}


case class DAOQuest(questID: Long, longitude: Float, latitude: Float)


