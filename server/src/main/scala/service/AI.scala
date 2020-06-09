package service

import dao.{Dungeon, DungeonUnit, Empty, NPC, PlayerUnit}
import javax.inject.Inject
import model.Dungeon.{Skill, Turn}

class AI @Inject() () {
  def decideTurn(dungeon: Dungeon): Turn = {
    val unit: DungeonUnit = dungeon.units(dungeon.currentTurn)
    unit match {
      case PlayerUnit(_, _, _, _, _) => Turn(0, Seq())
      case NPC(_, _, skils, ap, _, _) => npcDecision(skils, ap)
      case Empty(_) => Turn(0, Seq())
    }
  }

  def npcDecision(skills: Seq[Skill], availableAP: Int): Turn = {
    Turn(0, Seq())
  }
}
