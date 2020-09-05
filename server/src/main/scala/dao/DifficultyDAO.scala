package dao

import java.util.UUID

import javax.inject.Inject
import scalikejdbc._

import scala.collection.immutable.List
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class DifficultyDAO @Inject()(val pool: ConnectionPool)  extends SQLUtil{

  def setDifficulty(user: Int, difficulty:Int, group:Boolean,members:ListBuffer[Int]): Unit = {
    withSession(pool) { implicit session =>
      sql"INSERT INTO public.difficulty (difficulty, user_id, unlocked_in_group,group_members) VALUES ($difficulty, $user,$group,${members.toArray})".executeUpdate().apply()
    }
  }

  def getAvailableDifficulties(user:Int): Seq[DifficultyRow] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT id, user_id, group_members, difficulty FROM public.difficulty WHERE (user_id = $user or $user = any(group_members)) and dungeon = false ".map{ row =>
        row.int("difficulty")

        DifficultyRow(
          row.int("id"),
          row.int("user_id"),
          row.array("group_members").getArray.asInstanceOf[Array[Integer]].toSeq.map(_.intValue()),
          row.int("difficulty")
        )
      }.list().apply()
    }
  }

  def setDungeon(id: Int): Unit = {
    withSession(pool) { implicit session =>
      sql"update public.difficulty set dungeon = true where id = $id".executeUpdate().apply()
    }
  }

  def getDifficultyById(id: Int): Option[DifficultyRow] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT id, user_id, group_members, difficulty FROM public.difficulty WHERE id = $id".map{ row =>
        row.int("difficulty")

        DifficultyRow(
          row.int("id"),
          row.int("user_id"),
          row.array("group_members").getArray.asInstanceOf[Array[Integer]].toSeq.map(_.intValue()),
          row.int("difficulty")
        )
      }.first().apply()
    }
  }


}

case class DifficultyRow(id: Int, userId: Int, groupMembers: Seq[Int], difficulty: Int)
