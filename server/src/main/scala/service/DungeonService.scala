package service

import dao.{Dungeon, DungeonDAO}
import javax.inject.Inject

class DungeonService @Inject() () {

  def newSPDungeon(userId: Int, difficulty: Int, player: PlayerCharacter): (Int, Dungeon) = {
    val generator = Difficulty.generator(difficulty)
    DungeonDAO.newDungeon(userId, player, generator)
  }

}
