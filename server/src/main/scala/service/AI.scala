package service

import dao.SkillDAO
import javax.inject.Inject
import model.Dungeon.SkillUsage
import util._

class AI @Inject()() {
  def decideAction(npc: NPC, dungeon: Dungeon): Option[SkillUsage] = {
    try {
      if (leftOf(npc, dungeon).exists(_.isInstanceOf[Empty])) {
        Option(SkillUsage(dungeon.findUnitById(npc.id)._2 - 1, SkillDAO.skills.head))
      } else {
        val (skill, (target, _)) = npc.skills.collect { case skill if skill.apCost <= npc.ap && skill.remainingCoolDown == 0 =>
          skill
        }.map{ skill =>
          val bestTarget = DungeonService.identifyTargetable(dungeon, skill, dungeon.findUnitById(npc.id)._2).map{ targetable =>
            val score = dungeon.units(dungeon.currentLevel)(targetable) match {
              case targetNpc: NPC =>
                skill.attributesOffset.willPower * 5 + skill.attributesOffset.evasion * 15 + skill.attributesOffset.constitution * 10 * skill.attributesOffsetDuration - DamageCalc.apply(npc, targetNpc, skill)
              case playerUnit: PlayerUnit =>
                DamageCalc.apply(npc, playerUnit, skill) - (skill.attributesOffset.constitution * 10 + skill.attributesOffset.willPower * 5 + skill.attributesOffset.evasion * 15)
              case _: Empty => 0
            }

            targetable -> score
          }.maxBy(_._2)
          skill -> bestTarget
        }.shuffle.maxBy(_._2._2)

        Some(SkillUsage(target, skill))
      }
    } catch {
      case _: UnsupportedOperationException =>
        None
    }

  }

  def leftOf(npc: NPC, dungeon: Dungeon): Option[DungeonUnit] = {
    val position = dungeon.findUnitById(npc.id)._2
    if  (position >= 1) {
      Option(dungeon.units(dungeon.currentLevel)(position - 1))
    } else {
      None
    }
  }

}
