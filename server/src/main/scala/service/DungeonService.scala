package service

import dao.DungeonDAO
import javax.inject.Inject
import model.Character.Attributes
import model.Dungeon.{Skill, SkillUsage}

class DungeonService @Inject()(ai: AI) {

  def newDungeon(userIds: Seq[Int], difficulty: Int, playerAttributes: Seq[Attributes], playerSkills: Seq[Seq[Skill]]): (Int, Dungeon) = {
    val generator = Difficulty.generator(difficulty, userIds.length)
    val dungeon = DungeonDAO.newDungeon(userIds, playerAttributes, playerSkills, generator)

    executeNPCS(dungeon._2)
    dungeon
  }

  def endTurn(unitId: Int, dungeon: Dungeon): Option[Dungeon] = {
    if (dungeon.isCurrentTurn(unitId)) {
      dungeon.provideAP(unitId)
      dungeon.moveTurnPointer()
      dungeon.applyStatuses()
      executeNPCS(dungeon)
      Option(dungeon)
    } else {
      None
    }
  }

  private def executeNPCS(dungeon: Dungeon): Unit = {
    while (!dungeon.currentTurnUnit.isInstanceOf[PlayerUnit] && dungeon.units.exists(_.isInstanceOf[PlayerUnit])) {
      dungeon.currentTurnUnit match {
        case npc: NPC =>
          var actionO = ai.decideAction(npc, dungeon)
          while (actionO.isDefined && dungeon.isAllowedToUse(npc.id, actionO.get)) {
            dungeon.applySkill(npc.id, actionO.get.skill, actionO.get.targetPosition)
            actionO = ai.decideAction(npc, dungeon)
          }
          dungeon.provideAP(npc.id)
        case _ =>
      }
      dungeon.moveTurnPointer()
      dungeon.applyStatuses()
    }
  }

  def applyAction(unitId: Int, skillUsage: SkillUsage, dungeon: Dungeon): Option[Dungeon] = {
    if (dungeon.isAllowedToUse(unitId, skillUsage)) {
      dungeon.applySkill(unitId, skillUsage.skill, skillUsage.targetPosition)
      Option(dungeon)
    } else {
      println(s"unit $unitId is not allowed to use $skillUsage on $dungeon")
      None
    }
  }

}

object DungeonService {
  def identifyTargetable(dungeon: Dungeon, skill: Skill, casterPosition: Int): Seq[Int] = {
    val casterInPatternIndex = skill.targetPattern.length / 2

    skill.targetPattern.zipWithIndex.flatMap { case (c: Char, i) =>
      if (c == '1') {
        Some(i - casterInPatternIndex + casterPosition)
      } else {
        None
      }
    }.filter(_ >= 0).filter(_ < dungeon.units.length)
  }

  def identifyHits(dungeon: Dungeon, skill: Skill, target: Int): Seq[Int] = {
    val targetInPatternIndex = skill.effectPattern.length / 2

    skill.effectPattern.zipWithIndex.flatMap { case (c: Char, i) =>
      if (c == '1') {
        Some(target + i - targetInPatternIndex)
      } else {
        None
      }
    }.filter(_ >= 0).filter(_ < dungeon.units.length)
  }

}
