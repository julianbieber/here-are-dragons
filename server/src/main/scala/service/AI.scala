package service

import javax.inject.Inject
import model.Dungeon.SkillUsage

class AI @Inject()() {
  def decideAction(npc: NPC, dungeon: Dungeon): Option[SkillUsage] = {
    npc.skills.collect { case skill if skill.apCost <= npc.ap && skill.remainingCoolDown == 0 =>
      skill
    }.sortBy(_.damage).lastOption.flatMap { skill =>
      DungeonService.identifyTargetable(dungeon, skill, dungeon.findUnitById(npc.id)._2).headOption.map(target =>
        SkillUsage(target, skill)
      )
    }


  }

}
