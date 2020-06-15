package dao

import javax.inject.Inject
import model.Dungeon.SkillBar
import scalikejdbc.{ConnectionPool, DBSession, _}

class SkillbarDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def selectSkill(userId: Int, skill: Int): Unit = {
    withSession(pool){ implicit session =>
      getSkillBarQuery(userId).map{ currentSkillBar =>
        if (!currentSkillBar.selected.contains(skill) && currentSkillBar.unlocked.contains(skill)) {
          write(currentSkillBar.copy(selected = currentSkillBar.selected ++ Seq(skill)))
        }
      }
    }
  }

  def unselectSkill(userId: Int, skill: Int): Unit = {
    withSession(pool){ implicit session =>
      getSkillBarQuery(userId).map{ currentSkillBar =>
        if (currentSkillBar.selected.contains(skill) && currentSkillBar.unlocked.contains(skill)) {
          write(currentSkillBar.copy(selected = currentSkillBar.selected.filterNot(_ == skill)))
        }
      }
    }
  }

  def getSkillBar(userId: Int): Option[SkillBar] = {
    withReadOnlySession(pool) { implicit session =>
      getSkillBarQuery(userId)
    }
  }

  def unlock(userId: Int, skill: Int): Unit = {
    withSession(pool){ implicit session =>
      val currentSkillBar = getSkillBarQuery(userId).getOrElse(SkillBar(userId,Seq(), Seq()))
      if (!currentSkillBar.unlocked.contains(skill)) {
        write(currentSkillBar.copy(unlocked = currentSkillBar.unlocked ++ Seq(skill)))
      }
    }
  }

  private def getSkillBarQuery(userId: Int)(implicit session: DBSession): Option[SkillBar] = {
    sql"select selected, unlocked from skillbar where user_id = $userId".map{ row =>
      SkillBar(
        userId,
        row.array("selected").getArray().asInstanceOf[Array[Integer]].toSeq.map(_.toInt),
        row.array("unlocked").getArray().asInstanceOf[Array[Integer]].toSeq.map(_.toInt)
      )
    }.first().apply()
  }

  private def write(skillBar: SkillBar)(implicit session: DBSession): Unit = {
    sql"insert into public.skillbar (user_id, selected, unlocked) values (${skillBar.userId}, ${skillBar.selected.toArray}, ${skillBar.unlocked.toArray}) on conflict (user_id) DO UPDATE SET selected=excluded.selected, unlocked=excluded.unlocked".execute().apply()
  }


}
