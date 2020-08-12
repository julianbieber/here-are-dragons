package dao

import javax.inject.Inject
import scalikejdbc._

class GroupTalentUnlockDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {
  def getUnlocks(group: Seq[Int]): Option[GroupUnlockRow] = {
    withReadOnlySession(pool) { implicit session =>
      val groupIdentifier = group2Id(group)
      sql"select users, currently_unlocking, unlocked from public.group_talent_unlocks where users = $groupIdentifier".map{ row =>
        GroupUnlockRow(
          id2Group(row.string("users")),
          row.intOpt("currently_unlocking"),
          row.array("unlocked").getArray.asInstanceOf[Array[Integer]].map(_.intValue()).toSeq
        )
      }.first().apply()
    }
  }
  private def group2Id(group: Seq[Int]): String = group.sorted.mkString("-")
  private def id2Group(id: String): Seq[Int] = id.split("-").map(_.toInt)

  def getAllUnlocks(): Seq[GroupUnlockRow] = {
    withReadOnlySession(pool) { implicit session =>
      sql"select users, currently_unlocking, unlocked from public.group_talent_unlocks".map{ row =>
        GroupUnlockRow(
          id2Group(row.string("users")),
          row.intOpt("currently_unlocking"),
          row.array("unlocked").getArray.asInstanceOf[Array[Integer]].map(_.intValue()).toSeq
        )
      }.list().apply()
    }
  }

  def unlock(group: Seq[Int]): Unit = {
    withSession(pool) { implicit session =>
      val id = group2Id(group)
      sql"update public.group_talent_unlocks set unlocked = unlocked || currently_unlocking, currently_unlocking = NULL where users = $id and currently_unlocking is not null".executeUpdate().apply()
    }
  }

  def startUnlocking(group: Seq[Int], talent: Int): Unit = {
    withSession(pool) { implicit session =>
      val id = group2Id(group)
      sql"insert into public.group_talent_unlocks (users, currently_unlocking) VALUES ($id, $talent) on conflict (users) do update set users = excluded.users, currently_unlocking = excluded.currently_unlocking".executeUpdate().apply()
    }
  }
}

case class GroupUnlockRow(group: Seq[Int], currentlyUnlocking: Option[Int], unlocked: Seq[Int])
