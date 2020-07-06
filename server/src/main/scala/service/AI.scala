package service

import javax.inject.Inject
import model.Dungeon.SkillUsage

class AI @Inject()() {
  def decideAction(npc: NPC, dungeon: Dungeon): Option[SkillUsage] = {
    npc.skills.sortBy(_.damage).collect { case skill if skill.apCost <= npc.ap =>
      skill
    }.lastOption.flatMap { skill =>
      DungeonService.identifyTargetable(dungeon, skill, dungeon.findUnitById(npc.id)._2).headOption.map(target =>
        SkillUsage(target, skill)
      )
    }


  }

}
