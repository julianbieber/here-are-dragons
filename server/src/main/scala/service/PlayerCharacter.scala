package service

import dao.{DungeonUnit, PlayerUnit}

case class PlayerCharacter(userId: Int, maxHealth: Int) {
  def toUnit: PlayerUnit = PlayerUnit(userId, maxHealth)
}
