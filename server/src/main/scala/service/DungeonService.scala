package service

import dao.{Dungeon, DungeonDAO, Empty, NPC, PlayerUnit}
import javax.inject.Inject
import model.Dungeon.{Skill, SkillUsage, Turn}

class DungeonService @Inject() (ai: AI) {

  def newSPDungeon(userId: Int, difficulty: Int, player: PlayerCharacter): (Int, Dungeon) = {
    val generator = Difficulty.generator(difficulty)
    DungeonDAO.newDungeon(userId, player, generator)
  }

  private def findUser(userId: Int, dungeon: Dungeon): Option[Int] =
    dungeon.units.zipWithIndex.find{ case (unit, _) => unit match {
      case PlayerUnit(u, _, _, _, _) => u == userId
      case NPC(_, _, _, _, _, _) => false
      case Empty(_) => false
    }}.map(_._2)

  def apply(userId: Int, dungeon: Dungeon, turn: Turn): Option[Dungeon] = {
    findUser(userId, dungeon).map(applyTurn(_, dungeon, turn)).map{ afterPlayer =>
      var d = afterPlayer
      while (!d.units(d.currentTurn).isInstanceOf[PlayerUnit]) {
        val turn = ai.decideTurn(d)
        d = applyTurn(d.currentTurn, d, turn)
      }
      d
    }
  }

  private[service] def applyTurn(caster: Int, dungeon: Dungeon, turn: Turn): Dungeon = {
    if (dungeon.currentTurn == caster) {
      val after = turn.skillsUsed.foldLeft(dungeon){ case (d, skillUsage) =>
        applySkillUsage(d, skillUsage, caster)
      }
      println(s"nextTurn: ${nextTurn(after)}")
      provideAP(after).copy(currentTurn = nextTurn(after))
    } else {
      dungeon
    }
  }

  private def provideAP(dungeon: Dungeon): Dungeon = {
    val updatedUnits = dungeon.units.zipWithIndex.map{ case (u, i) =>
      if (i == dungeon.currentTurn) {
        u.gainAP()
      } else {
        u
      }
    }
    dungeon.copy(units = updatedUnits)
  }

  private def nextTurn(dungeon: Dungeon): Int = {
    if (dungeon.currentTurn + 1 >= dungeon.units.length) {
      0
    } else {
      dungeon.currentTurn + 1
    }
  }

  private def applySkillUsage(dungeon: Dungeon, skillUsage: SkillUsage, caster: Int): Dungeon = {
    val target = skillUsage.targetId
    val skill = skillUsage.skill
    if (DungeonService.identifyTargetable(dungeon, skill, caster).contains(target)) {
      val hits = DungeonService.identifyHits(dungeon, skill, target)
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


}

object DungeonService {
  private[service] def identifyTargetable(dungeon: Dungeon, skill: Skill, casterInDungeon: Int): Seq[Int] = {
    val casterInPatternIndex = skill.targetPattern.length / 2


    skill.targetPattern.zipWithIndex.flatMap{ case (c: Char, i) =>
      if (c == '1') {
        Some(i - casterInPatternIndex + casterInDungeon)
      } else {
        None
      }
    }.filter(_ >= 0).filter(_ < dungeon.units.length)
  }

  private[service] def identifyHits(dungeon: Dungeon, skill: Skill, target: Int): Seq[Int] = {
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
