package dao

import javax.inject.Inject
import scalikejdbc._

import scala.collection.JavaConverters._
import scala.collection.immutable.List

class QuestDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def createQuestFromAPI(questID: String, longitude: Float, latitude: Float): Option[Long] = {
    withSession(pool) { implicit session =>
      val questNumber:Long = questID.toLong;
      sql"INSERT INTO public.quest (id, longitude, latitude,erledigt) VALUES ($questNumber,$longitude, $latitude, false) ON CONFLICT (id) DO NOTHING".executeUpdate().apply()
      sql"SELECT id FROM public.quest WHERE longitude = $longitude and latitude = $latitude".map { col =>
        col.long("id")
      }.first().apply()
      Some(questNumber)
    }
  }

/*
  def createQuest(longitude: Float, latitude: Float): Option[Int] = {
    withSession(pool) { implicit session =>
      sql"INSERT INTO public.quest (longitude, latitude,erledigt) VALUES ($longitude, $latitude, false)".executeUpdate().apply()
      sql"SELECT id FROM public.quest WHERE longitude = $longitude and latitude = $latitude".map { col =>
        col.int("id")
      }.first().apply(SkillbarDAO
    }
  }*/

  def setQuestToErledigt(questID: Long): Unit = {
    withSession(pool) { implicit session =>
      sql"UPDATE public.quest SET erledigt = true WHERE id =$questID ".executeUpdate().apply()
    }
  }

  def getStatusErledigt(questID: Long): Option[Boolean] = {
    withSession(pool) { implicit session =>
      sql"SELECT erledigt FROM public.quest WHERE id = $questID".map { col =>
        col.boolean("erledigt")
      }.first().apply()
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

  def getListOfQuestsNerby(longitude: Float, latitude: Float, distanceFromPosition: Float): List[DAOQuest] = {
    //distanceFromPosition wird in Metern übergeben und danach mit der Methode toDegree in GeoCoordinaten
    //umgerechnet
    val distanceFromPositionInDegree = toMeter(distanceFromPosition)
    val longitudemin = longitude - distanceFromPositionInDegree;
    val longitudemax = longitude + distanceFromPositionInDegree;
    val latitudemin = latitude - distanceFromPositionInDegree;
    val latitudemax = latitude + distanceFromPositionInDegree;

    withReadOnlySession(pool) { implicit session =>
      val photoNodes: List[DAOQuest] =
        sql"""SELECT id,longitude,latitude,erledigt FROM public.quest WHERE longitude BETWEEN $longitudemin AND $longitudemax and latitude BETWEEN $latitudemin AND $latitudemax AND erledigt = false""".map(rs =>
          DAOQuest(
            rs.int("id"),
            rs.float("longitude"),
            rs.float("latitude"),
          )
        ).list.apply()
      photoNodes
    }
  }

}


case class DAOQuest(questID: Long, longitude: Float, latitude: Float)


