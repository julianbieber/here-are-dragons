package dao

import javax.inject.Inject
import scalikejdbc._
import scala.collection.immutable.List

class QuestDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {


  def fillDatabaseFromPoIs(listOfPoIs: List[DAOPoI], userID: Int): Unit = {
    withSession(pool) { implicit session =>
      for (poi <- listOfPoIs) {
        val countQuest =
          sql"""SELECT COUNT(*) FROM public.quest WHERE (userID = $userID)""".map { countQuestsForUser =>
            countQuestsForUser.int(1)
          }.first().apply().get
        if (countQuest < 20) {
          sql"INSERT INTO public.quest (id,userID,activ) VALUES (${poi.poiID},$userID,false) ON CONFLICT (id,userID) DO NOTHING".executeUpdate().apply()
        }
      }
    }
  }

  def makeActive(questID: Long, userId: Int): Unit = {
    withSession(pool) { implicit session =>
      sql"UPDATE public.quest SET activ = true WHERE id = $questID AND userID = $userId".executeUpdate().apply()
    }
  }

  def makeUnActive(questID: Long, userId: Int): Unit = {
    withSession(pool) { implicit session =>
      sql"UPDATE public.quest SET activ = false WHERE id = $questID AND userID = $userId".executeUpdate().apply()
    }
  }

  def checkIfActive(questID: Long, userID: Int): Option[Boolean] = {
    withSession(pool) { implicit session =>
      sql"SELECT activ FROM public.quest WHERE id = $questID AND userID = $userID".map { row =>
        row.boolean("activ")
      }.first().apply()
    }
  }

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

  def toMeter(distanceFromPosition: Float): Float = distanceFromPosition / 11100f

  def getListOfActivataibleQuestsNerby(longitude: Float, latitude: Float, distance: Float, userId: Int): List[DAOQuest] = {
    //distance wird in Metern übergeben und danach mit der Methode toDegree in GeoCoordinaten umgerechnet
    val distanceInDegree = toMeter(distance)
    val longitudemin = longitude - distanceInDegree;
    val longitudemax = longitude + distanceInDegree;
    val latitudemin = latitude - distanceInDegree;
    val latitudemax = latitude + distanceInDegree;

    withReadOnlySession(pool) { implicit session =>
      val daoquests: List[DAOQuest] =
        sql"""SELECT id FROM public.quest WHERE userID = $userId """.map { rowQuest =>
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


