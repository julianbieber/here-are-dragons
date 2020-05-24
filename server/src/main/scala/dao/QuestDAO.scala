package dao

import java.security.{MessageDigest, SecureRandom}
import java.util.UUID

import io.github.nremond.SecureHash
import javax.inject.Inject
import model.Account.LoginResponse
import scalikejdbc._

class QuestDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def createQuest(longitude:Float,latitude:Float): Option[Int] = {
    withSession(pool) { implicit session =>
      sql"INSERT INTO public.quest (longitude, latitude) VALUES ($longitude, $latitude)".executeUpdate().apply()
      sql"SELECT id FROM public.quest WHERE longitude = $longitude and latitude = $latitude".map { col =>
        col.int("id")
      }.first().apply()
    }
  }

  def deleteQuest(questid: Integer): Unit = {
      withSession(pool) { implicit session =>
        sql"DELETE FROM public.quest WHERE id = $questid".execute().apply()
      }
  }


  def getQuests(questId:Integer): Option[DAOQuest] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT id, longitude, latitude FROM public.quest WHERE id = $questId".map { row =>
        DAOQuest(row.int("id"), row.float("longitude"), row.float("latitude"))
      }.first().apply()
    }
  }

}


case class DAOQuest(questID:Integer,longitude:Float,latitude:Float)
