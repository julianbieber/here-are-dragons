package service

import dao.{Dungeon, NPC, PlayerUnit}

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
    val enemies = (0 to numberOfEnemies).map{ _ =>
      NPC(maxNPCPrefab, (baseHealth * healthMultiplier()).toInt, Seq(), 5, 10, 2)
    }

    Dungeon(
      userId = Option(userId),
      groupId = None,
      units = players ++ enemies,
      currentTurn = 0
    )
  }
}
