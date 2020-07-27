package dao

import java.util.UUID

import javax.inject.Inject
import scalikejdbc._

import scala.collection.immutable.List
import scala.collection.mutable

class DifficultyDAO @Inject()(val pool: ConnectionPool, userDAO: UserDAO)  extends SQLUtil{

  def setDifficulty(user: Int, difficulty:Int, group:Boolean): Unit = {
    withSession(pool) { implicit session =>
      sql"INSERT INTO public.difficulty (difficulty, userID, unlockedInGroup) VALUES ($difficulty, $user,$group)".executeUpdate().apply()
    }
  }

  def getDifficulty(user:Int):List[Int]={
    withSession(pool) { implicit session =>
      val difficulties: List[Int] =
      sql"SELECT difficulty FROM public.difficulty WHERE userID = $user ".map{ row =>
        row.int("difficulty")
      }.list().apply()
      difficulties
    }
  }


}
