package dao

import javax.inject.Inject
import scalikejdbc._

import scala.collection.immutable.List
import com.github.dmarcous.s2utils.geo.GeographyUtilities

import scala.collection.mutable.ListBuffer

class QuestDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def getActiveQuestInGroup(userIds: ListBuffer[Int],userId:Int):ActiveInGroup = {
    withSession(pool) {implicit  sessions =>
      val usersWithoutSelf = userIds.filterNot(_==userId)
      val activeQuestInGroup : Int =
        sql"""SELECT COUNT(*) FROM public.quest WHERE userID = any(${usersWithoutSelf.toArray}) AND activ = true """.map{countQuestsForUser =>
          countQuestsForUser.int(1)
        }.first().apply().getOrElse(0)
      ActiveInGroup(activeQuestInGroup>=1)
    }
  }


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

  def makeActive(questID: Long, userId: Int,difficulty:Int): Unit = {
    withSession(pool) { implicit session =>
      sql"UPDATE public.quest SET progres = ${0} WHERE activ = true AND userID = $userId".executeUpdate().apply()
      sql"UPDATE public.quest SET difficulty = ${0} WHERE activ = true AND userID = $userId".executeUpdate().apply()
      sql"UPDATE public.quest SET activ = false WHERE userID = $userId".executeUpdate().apply()
      sql"UPDATE public.quest SET activ = true WHERE id = $questID AND userID = $userId".executeUpdate().apply()
      sql"UPDATE public.quest SET difficulty = ${difficulty} WHERE activ = true AND userID = $userId".executeUpdate().apply()
    }
  }

  def makeUnActive(userId: Int): Unit = {
    withSession(pool) { implicit session =>
      sql"UPDATE public.quest SET activ = false,difficulty =0,progres =0 WHERE userID = $userId".executeUpdate().apply()
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


  def calculateDifficulty(longitude:Float, latitude:Float, questId:Long, userId:Int):Int = {
    withReadOnlySession(pool) { implicit session =>
      val quests: Array[Long] =
        sql"""SELECT ids FROM public.quest WHERE userID = $userId AND id=$questId """.map { rowQuest =>
          rowQuest.array("ids").getArray.asInstanceOf[Array[java.lang.Long]].toSeq.map(_.longValue())
        }.list().apply.flatten.toArray

      val positions: List[Position] =
        sql"""SELECT longitude, latitude FROM public.poi WHERE id = any(${quests})""".map { rowPoi =>
          Position(rowPoi.float("longitude").toDouble ,rowPoi.float("latitude").toDouble)
        }.list().apply()

      val difficulty = positions.foldLeft((0.0,Position(longitude,latitude))){case ((distance, point), nextPoint) =>
      distance + GeographyUtilities.haversineDistance(point.longitude, point.latitude, nextPoint.longitude, nextPoint.latitude) -> nextPoint}

      if((difficulty._1/50).toInt+1>100) 100
      else (difficulty._1/50).toInt+1
    }

  }



  def getListOfActivataibleQuestsNerby(longitude: Float, latitude: Float, distance: Float, userId: Int): List[DAOQuest] = {
    //distance wird in Metern übergeben und danach mit der Methode toDegree in GeoCoordinaten umgerechnet
    val distanceInDegree = toMeter(distance)
    val longitudemin = longitude - distanceInDegree;
    val longitudemax = longitude + distanceInDegree;  
    val latitudemin = latitude - distanceInDegree;
    val latitudemax = latitude + distanceInDegree;
    withReadOnlySession(pool) { implicit session =>
      val daoquests: List[DAOQuest] =
        sql"""SELECT id,ids,progres FROM public.quest WHERE userID = $userId """.map { rowQuest =>
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
      val activeQuestId :Long =
        sql"""SELECT id FROM public.quest WHERE userID = $userId AND activ = true""".map { rowQuest =>
          rowQuest.long("id")
        }.first().apply().getOrElse(0)
      val connectedQuests: Seq[Long] =
        sql"""SELECT ids FROM public.quest WHERE userID = $userId AND id =$activeQuestId""".map { rowQuest =>
          rowQuest.array("ids").getArray.asInstanceOf[Array[java.lang.Long]].map(_.longValue()).toSeq
      }.first().apply().getOrElse(List(0))
      val progress: Int =
        sql"""SELECT progres FROM public.quest WHERE userID = $userId AND id =$activeQuestId""".map { rowQuest =>
          rowQuest.int("progres")
        }.first().apply().getOrElse(0)
      val latlon: Option[Array[Float]] =
        sql"""SELECT longitude, latitude FROM public.poi WHERE (id = ${connectedQuests(progress)})""".map(rowPoi =>
          Array(rowPoi.float("latitude"), rowPoi.float("longitude"))
        ).first().apply()
      var result = List[DAOQuest]()
      for( i <-daoquests){
        if(i.questID==activeQuestId){
          val j= DAOQuest(i.questID,i.questIDs,latlon.get(1),latlon.get(0),i.priority,i.tag)
          result = j :: result
        }else{
          result = i :: result
        }
      }
      result
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
        }.first().apply().getOrElse(throw new RuntimeException("Keine Liste von QuestIds vorhanden"))
      val progress: Int =
        sql"""SELECT progres FROM public.quest WHERE userID = $userID AND id =$questID""".map { rowQuest =>
          rowQuest.int("progres")
        }.first().apply().getOrElse(throw new RuntimeException("Kein Progress von Quest vorhanden"))
      if(connectedQuests.length>progress) {
        val lonlat: Option[Array[Float]] =
          sql"""SELECT longitude, latitude FROM public.poi WHERE (id = ${connectedQuests(progress)})""".map(rowPoi =>
            Array(rowPoi.float("latitude"), rowPoi.float("longitude"))
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
        }.first().apply().getOrElse(throw new RuntimeException("keine aktive Quest vorhanden"))
      activeQuestId
    }
  }

  def getActiveQuest(userID:Int): Option[DAOQuest] ={
    withSession(pool) { implicit session =>
      val activeQuest :Option[DAOQuest] = {
          sql"""SELECT id,ids,progres FROM public.quest WHERE userID= $userID AND activ = true """.map { rowQuest =>
            sql"""SELECT longitude, latitude, priority, tags FROM public.poi  WHERE id = ${rowQuest.long("id")}""".map(rowPoi =>
              DAOQuest(
                rowQuest.long("id"),
                rowQuest.array("ids").getArray.asInstanceOf[Array[java.lang.Long]].toSeq.map(_.longValue()),
                rowPoi.float("longitude"),
                rowPoi.float("latitude"),
                rowPoi.float("priority"),
                rowPoi.stringOpt("tags")
              )
            ).first().apply()
          }.first().apply().flatten
      }
      activeQuest
    }
  }

  def setProgress(questId:Long,userId:Int):Unit ={
    withSession(pool) { implicit session =>
      val progress: Int =
        sql"""SELECT progres FROM public.quest WHERE userID = $userId AND id =$questId""".map { rowQuest =>
          rowQuest.int("progres")
        }.first().apply().getOrElse(throw new RuntimeException("kein Progress in Datenbank gefunden"))
      sql"UPDATE public.quest SET progres = ${progress+1} WHERE id = $questId AND userID = $userId".executeUpdate().apply()
    }
  }
}

case class DAOQuest(questID: Long,questIDs :Seq[Long], longitude: Float, latitude: Float,priority: Float, tag:Option[String])
case class Position(longitude: Double, latitude: Double)
case class ActiveInGroup(activ: Boolean)


