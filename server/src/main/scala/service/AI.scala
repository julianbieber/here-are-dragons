package service

import dao.{Dungeon, NPC}
import javax.inject.Inject
import model.Dungeon.{Skill, SkillUsage, Turn}

class AI @Inject() () {
  def decideAction(NPC: NPC, dungeon: Dungeon): Option[SkillUsage] = {
    None
  }

}
