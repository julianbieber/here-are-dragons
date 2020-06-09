package service

import dao.{Dungeon, DungeonDAO, Empty, NPC, PlayerUnit}
import javax.inject.Inject
import model.Dungeon.{Skill, SkillUsage, Turn}

class DungeonService @Inject() () {

  def newSPDungeon(userId: Int, difficulty: Int, player: PlayerCharacter): (Int, Dungeon) = {
    val generator = Difficulty.generator(difficulty)
    DungeonDAO.newDungeon(userId, player, generator)
  }

  def findUser(userId: Int, dungeon: Dungeon): Option[Int] =
    dungeon.units.zipWithIndex.find{ case (unit, _) => unit match {
      case PlayerUnit(u, _) => u == userId
      case NPC(_, _) =>false
      case Empty(_) =>false
    }}.map(_._2)

  def applyTurn(userId: Int, dungeon: Dungeon, turn: Turn): Dungeon = {
    findUser(userId, dungeon).map{ caster =>
      if (dungeon.currentTurn == caster) {
        val after = turn.skillsUsed.foldLeft(dungeon){ case (d, skillUsage) =>
          applySkillUsage(d, skillUsage, caster)
        }
        after.copy(currentTurn = nextTurn(after))
      } else {
        dungeon
      }
    }.getOrElse(dungeon)
  }

  def nextTurn(dungeon: Dungeon): Int = {
    if (dungeon.currentTurn + 1 >= dungeon.units.length) {
      0
    } else {
      dungeon.currentTurn + 1
    }
  }

  private def applySkillUsage(dungeon: Dungeon, skillUsage: SkillUsage, caster: Int): Dungeon = {
    val target = skillUsage.targetId
    val skill = skillUsage.skill
    if (identifyTargetable(dungeon, skill, caster).contains(target)) {
      val hits = identifyHits(dungeon, skill, target)
      dungeon.copy(units = dungeon.units.zipWithIndex.map{ case (unit, i) =>
        if (hits.contains(i)) {
          unit.applySkill(skill)
        } else {
          unit
        }
      })
    } else {
      dungeon
    }
  }

  def identifyTargetable(dungeon: Dungeon, skill: Skill, casterInDungeon: Int): Seq[Int] = {
    val casterInPatternIndex = skill.targetPattern.length / 2


    skill.targetPattern.zipWithIndex.flatMap{ case (c: Char, i) =>
      if (c == '1') {
        Some(i - casterInPatternIndex + casterInDungeon)
      } else {
        None
      }
    }.filter(_ >= 0).filter(_ < dungeon.units.length)
  }

  def identifyHits(dungeon: Dungeon, skill: Skill, target: Int): Seq[Int] = {
    val targetInPatternIndex = skill.effectPattern.length / 2

    skill.effectPattern.zipWithIndex.flatMap{ case (c: Char, i) =>
      if (c == '1') {
        Some(target + i - targetInPatternIndex)
      } else {
        None
      }
    }.filter(_ >= 0).filter(_ < dungeon.units.length)
  }

}
