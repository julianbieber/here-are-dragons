package service

import dao.{Dungeon, NPC, PlayerUnit, SkillDAO, Status}

import scala.util.Random

object Difficulty {
  def generator(difficulty: Int): DungeonGenerator = DungeonGenerator(difficulty, () => healthMultiplier(difficulty))

  private def healthMultiplier(difficulty: Int) = difficulty.toFloat
}

case class DungeonGenerator(numberOfEnemies: Int, healthMultiplier: () => Float) {
  private val maxNPCPrefab = 0
  private val maxEmptyPrefab = 0
  private val baseHealth = 10

  def generate(userId: Int, players: Seq[PlayerUnit]): Dungeon = {
    val enemies = (0 to numberOfEnemies).map{ i =>
      NPC(players.size + i, maxNPCPrefab, (baseHealth * healthMultiplier()).toInt, Seq(SkillDAO.skills(0)), 5, 10, 2, Status.empty)
    }
    val units = players ++ enemies
    Dungeon(
      userId = Option(userId),
      groupId = None,
      units = units.toBuffer,
      currentTurn = 0,
      units.map(_.id)
    )
  }
}
