package service

import dao.{DungeonUnit, PlayerUnit}

case class PlayerCharacter(userId: Int, maxHealth: Int) {
  def toUnit: PlayerUnit = PlayerUnit(-1, userId, maxHealth, 5, 10, 2)
}
