package dao

import javax.inject.Inject
import scalikejdbc._

import scala.collection.JavaConverters._
import scala.collection.immutable.List

class QuestDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {


  def fillDatabaseFromPoIs(listOfPoIs:List[DAOPoI], userID:Int): Unit = {
    withSession(pool) { implicit session =>
      for(poi <- listOfPoIs){
        sql"INSERT INTO public.quest (id,userID,activ) VALUES (${poi.poiID},$userID,false) ON CONFLICT (id) DO NOTHING".executeUpdate().apply()
      }
    }
  }

  def makeActive(questID: Long,userId :Int) : Unit = {
    withSession(pool) { implicit session =>
      sql"UPDATE public.quest SET activ = true WHERE id = $questID AND userId = $userId".executeUpdate().apply()
    }
  }
  def makeUnActive(questID: Long,userId :Int) : Unit = {
    withSession(pool) { implicit session =>
      sql"UPDATE public.quest SET activ = false WHERE id = $questID AND userId = $userId".executeUpdate().apply()
    }
  }

  def checkIfActive(questID: Long, userID: Int): Option[Boolean] = {
    withSession(pool) { implicit session =>
      sql"SELECT activ FROM publc.quest WHERE id = $questID AND userid = $userID".map{ row =>
        row.boolean("activ")
      }.first().apply()
    }
  }

 /* def getActiveUser(questID: Long) : Option[Array[Int]] = {
    withSession(pool) { implicit session =>
      sql"""SELECT active_user_ids FROM public.quest WHERE id = $questID""".map {row =>
        row.array("active_user_ids").getArray().asInstanceOf[Array[Integer]].map(_.intValue())
      }.first().apply()
    }
  }*/
/*
  def getActivatableUser(questID: Long) : Option[Array[Int]] = {
    withSession(pool) { implicit session =>
      sql""" SELECT activatable_user_ids FROM public.quest WHERE id = $questID""".map { row =>
        row.array("activatable_user_ids").getArray().asInstanceOf[Array[Integer]].map(_.intValue())
      }.first().apply()
    }
  }*/

  def getFromDatabase(): List[DAOQuest] = {
    withSession(pool) { implicit session =>
      val daoquests: List[DAOQuest] =
        sql"""SELECT DISTINCT id FROM public.quest""".map(rowQuest =>
          sql"""SELECT longitude, latitude, priority, tags FROM public.poi WHERE id = ${rowQuest.long("id")}""".map(rowPoi =>
            DAOQuest(
              rowQuest.long("id"),
              rowPoi.float("longitude"),
              rowPoi.float("latitude"),
              rowPoi.float("priority"),
              rowPoi.stringOpt("tags")
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

/*  def getQuests(questId: Long): Option[DAOQuest] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT id, longitude, latitude FROM public.quest WHERE id = $questId".map { row =>
        DAOQuest(row.int("id"), row.float("longitude"), row.float("latitude"))
      }.first().apply()
    }
  }*/

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
          sql"""SELECT longitude, latitude, priority, tags FROM public.poi WHERE (longitude BETWEEN $longitudemin AND $longitudemax) and (latitude BETWEEN $latitudemin AND $latitudemax) AND (id = ${rowQuest.long("id")})""".map(rowPoi =>
            DAOQuest(
              rowQuest.long("id"),
              rowPoi.float("longitude"),
              rowPoi.float("latitude"),
              rowPoi.float("priority"),
              rowPoi.stringOpt("tags")
            )
          ).list().apply()
        }.list().apply().flatten
      daoquests
    }
  }

}


case class DAOQuest(questID: Long, longitude: Float, latitude: Float,priority: Float, tag:Option[String])


