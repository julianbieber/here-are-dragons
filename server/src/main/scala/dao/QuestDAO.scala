package dao

import javax.inject.Inject
import scalikejdbc._
import scala.collection.immutable.List

class QuestDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {


  def fillDatabaseFromPoIs(listOfPoIs: List[DAOPoI], userID: Int): Unit = {
    withSession(pool) { implicit session =>

      if (countQuests(userID) < 20) {
        val connectedQuests: List[Seq[Long]] =
          sql"""SELECT ids FROM public.quest WHERE userID = $userID """.map { rowQuest =>
            rowQuest.array("ids").getArray.asInstanceOf[Array[java.lang.Long]].map(_.longValue()).toSeq
          }.list().apply()

        val help = countChains(connectedQuests)
        var iteratorPoIs = 0
        var stepInQuest = 0

        while (stepInQuest < 3) {
          while (help(stepInQuest) < (10 - (5 - stepInQuest) * stepInQuest) && listOfPoIs.length > iteratorPoIs + stepInQuest) {
            val ids: Array[Long] = {
              if (stepInQuest == 0) Array(listOfPoIs(iteratorPoIs).poiID)
              else if (stepInQuest == 1) Array(listOfPoIs(iteratorPoIs).poiID, listOfPoIs(iteratorPoIs + 1).poiID)
              else Array(listOfPoIs(iteratorPoIs).poiID, listOfPoIs(iteratorPoIs + 1).poiID, listOfPoIs(iteratorPoIs + 2).poiID)
            }
            sql"INSERT INTO public.quest (id,ids, userID,activ) VALUES (${listOfPoIs(iteratorPoIs).poiID},$ids,$userID,false) ON CONFLICT (id,userID) DO NOTHING".executeUpdate().apply()
            help(stepInQuest) = help(stepInQuest) + 1
            iteratorPoIs = iteratorPoIs + stepInQuest + 1
          }
          stepInQuest = stepInQuest + 1
        }
      }
    }
  }

  def countChains(lis:List[Seq[Long]]):Array[Int]={
    val result = Array(0,0,0)
    for(l <- lis) {
      val a = l.length;
      if(a==1) result(0)=result(0)+1
      if(a==2) result(1)=result(1)+1
      if(a==3) result(2)=result(2)+1
    }
    result
  }


  def countQuests(userID: Int)(implicit session: DBSession): Integer = {
    val countQuest =
      sql"""SELECT COUNT(*) FROM public.quest WHERE (userID = $userID)""".map { countQuestsForUser =>
        countQuestsForUser.int(1)
      }.first().apply().get
    countQuest
  }

  def makeActive(questID: Long, userId: Int): Unit = {
    withSession(pool) { implicit session =>
      sql"UPDATE public.quest SET activ = false WHERE userID = $userId".executeUpdate().apply()
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
        sql"""SELECT DISTINCT id,ids FROM public.quest""".map(rowQuest =>
          sql"""SELECT longitude, latitude, priority, tags FROM public.poi WHERE id = ${rowQuest.long("id")}""".map(rowPoi =>
            DAOQuest(
              rowQuest.long("id"),
              rowQuest.array("ids").getArray.asInstanceOf[Array[java.lang.Long]].toSeq.map(_.longValue()),
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

  def deleteQuest(questid: Long, userID: Int): Unit = {
    withSession(pool) { implicit session =>
      sql"DELETE FROM public.quest WHERE id = $questid AND userID = $userID".execute().apply()
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
        sql"""SELECT id,ids FROM public.quest WHERE userID = $userId """.map { rowQuest =>
          sql"""SELECT longitude, latitude, priority, tags FROM public.poi WHERE (longitude BETWEEN $longitudemin AND $longitudemax) and (latitude BETWEEN $latitudemin AND $latitudemax) AND (id = ${rowQuest.long("id")})""".map(rowPoi =>
            DAOQuest(
              rowQuest.long("id"),
              rowQuest.array("ids").getArray.asInstanceOf[Array[java.lang.Long]].toSeq.map(_.longValue()),
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

  def getOldest(userID: Int): Option[Long] = {
    withSession(pool) { implicit session =>
      sql"""SELECT id FROM public.quest WHERE userID =$userID AND activ = false ORDER BY timestamp ASC LIMIT 1""".map { row =>
        row.long("id")
      }.first.apply()
    }
  }

  def getPositionOfNextQuest( questID:Long, userID:Int):Array[Float] = {
    withSession(pool) { implicit session =>
      val connectedQuests: Seq[Long] =
        sql"""SELECT ids FROM public.quest WHERE userID = $userID AND id =$questID""".map { rowQuest =>
          rowQuest.array("ids").getArray.asInstanceOf[Array[java.lang.Long]].map(_.longValue()).toSeq
        }.first().apply().get
      val progress: Int =
        sql"""SELECT progres FROM public.quest WHERE userID = $userID AND id =$questID""".map { rowQuest =>
          rowQuest.int("progres")
        }.first().apply().get
      if(connectedQuests.length>progress) {
        val lonlat: Option[Array[Float]] =
          sql"""SELECT longitude, latitude FROM public.poi WHERE (id = ${connectedQuests(progress)})""".map(rowPoi =>
            Array(rowPoi.float("longitude"), rowPoi.float("latitude"))
          ).first().apply()
        lonlat.getOrElse(Array())
      }else{
        Array()
      }
    }
  }

  def getActiveQuestID(userID:Int): Long ={
    withSession(pool) { implicit session =>
      val activeQuestId :Long =
        sql"""SELECT id FROM public.quest WHERE userID = $userID AND activ = true""".map { rowQuest =>
          rowQuest.long("id")
        }.first().apply().get
      activeQuestId
    }
  }

}

case class DAOQuest(questID: Long,questIDs :Seq[Long], longitude: Float, latitude: Float,priority: Float, tag:Option[String])



