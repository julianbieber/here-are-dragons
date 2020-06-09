package dao

import javax.inject.Inject
import scalikejdbc._
import service.PlayerCharacter

class CharacterDAO @Inject() (val pool: ConnectionPool) extends SQLUtil {
  def createCharacter(userId: Int): Unit = {
    withSession(pool) { implicit session =>
      sql"insert into public.player_characters (user_id, max_health) values ($userId, 100)".execute().apply()
    }
  }

  def getCharacter(userId: Int): PlayerCharacter = {
    withReadOnlySession(pool){ implicit session =>
      sql"select max_health from public.player_characters where user_id = $userId".map(row => PlayerCharacter(userId, row.int("max_health"))).first().apply().getOrElse(throw new RuntimeException(s"char does not exist for user: $userId"))
    }
  }
}
