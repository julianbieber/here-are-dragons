package dao

import javax.inject.Inject
import scalikejdbc._

class TalentUnlockDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {
  def getUnlocks(userId: Int): Option[UnlockRow] = {
    withReadOnlySession(pool) { implicit session =>
      sql"select currently_unlocking, unlocked from public.talent_unlocks where user_id = $userId".map{ row =>
        UnlockRow(
          userId,
          row.intOpt("currently_unlocking"),
          row.array("unlocked").getArray.asInstanceOf[Array[Integer]].map(_.intValue()).toSeq
        )
      }.first().apply()
    }
  }

  def unlock(userId: Int): Unit = {
    withSession(pool) { implicit session =>
      sql"update public.talent_unlocks set unlocked = unlocked || currently_unlocking, currently_unlocking = NULL where user_id = $userId and currently_unlocking is not null".executeUpdate().apply()
    }
  }

  def startUnlocking(userId: Int, talent: Int): Unit = {
    withSession(pool) { implicit session =>
      sql"insert into public.talent_unlocks (user_id, currently_unlocking) VALUES ($userId, $talent) on conflict (user_id) do update set user_id = excluded.user_id, currently_unlocking = excluded.currently_unlocking".executeUpdate().apply()
    }
  }
}

case class UnlockRow(userId: Int, currentlyUnlocking: Option[Int], unlocked: Seq[Int])
