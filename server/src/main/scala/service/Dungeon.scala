package service

import model.Dungeon.{Skill, SkillUsage}

import scala.collection.mutable

case class Dungeon(
  userId: Option[Int],
  groupId: Option[String],
  units: mutable.Buffer[DungeonUnit],
  var currentTurn: Int,
  turnOrder: Seq[Int]
) {
  def isCurrentTurn(unitId: Int): Boolean = {
    currentTurnUnit.id == unitId
  }

  def provideAP(unitId: Int): Unit = {
    units.transform { unit =>
      if (unit.id == unitId) {
        unit.gainAP()
      } else {
        unit
      }
    }
  }

  def applyStatuses(): Unit = {
    units.transform{ unit =>
      if (isCurrentTurn(unit.id)) {
        unit.applyStatus()
      } else {
        unit
      }
    }
  }

  def moveTurnPointer(): Unit = {
    currentTurn = (currentTurn + 1) % turnOrder.length
  }

  def countDownCDs(): Unit = {
    units.transform{ unit =>
      if (isCurrentTurn(unit.id)) {
        unit.countDownCDs()
      } else {
        unit
      }
    }
  }

  def completed: (Boolean, Boolean) = {
    !units.exists(_.isInstanceOf[NPC]) -> !units.exists(_.isInstanceOf[PlayerUnit])
  }

  def isAllowedToUse(unitId: Int, skillUsage: SkillUsage): Boolean = {
    currentTurnUnit.id == unitId &&
      skillUsage.skill.apCost <= currentTurnUnit.ap &&
      DungeonService.identifyTargetable(this, skillUsage.skill, findUnitById(unitId)._2).contains(skillUsage.targetPosition) &&
      skillUsage.skill.remainingCoolDown == 0
  }

  def currentTurnUnit: DungeonUnit = findUnitById(turnOrder(currentTurn))._1

  def findUser(userId: Int): (PlayerUnit, Int) = {
    units.zipWithIndex.collectFirst { case (unit: PlayerUnit, i) if unit.userId == userId => unit -> i }.get
  }

  def findUnitById(unitId: Int): (DungeonUnit, Int) = units.zipWithIndex.collectFirst { case (unit, i) if unit.id == unitId => unit -> i }.get

  private def swap(unitPosition: Int, position: Int): Unit = {
    val tmp = units(position)

    units(position) = units(unitPosition)
    units(position).status.add(tmp.status)
    tmp.status = units(unitPosition).status.locationBased
    units(unitPosition) = tmp
  }

  def applySkill(casterId: Int, skill: Skill, targetPosition: Int): Unit = {
    val (_, casterPosition) = findUnitById(casterId)
    val hits = DungeonService.identifyHits(this, skill, targetPosition)
    val hitUnitIds = hits.map(units(_).id)
    units.transform { unit =>
      if (hitUnitIds.contains(unit.id)) {
        unit.applySkill(skill.status, DamageCalc(findUnitById(casterId)._1, unit, skill))
      } else {
        unit
      }
    }
    if (skill.moves) {
      executeMovement(casterPosition, targetPosition, skill.movementOffset)
    }

    units.transform{ unit =>
      if (unit.id == casterId) {
        unit match {
          case p: PlayerUnit => p.copy(ap = p.ap - skill.apCost, skills = p.skills.map{ s =>
            if (s.id == skill.id) {
              s.copy(remainingCoolDown = s.coolDown)
            } else {
              s
            }
          })
          case p: NPC => p.copy(ap = p.ap - skill.apCost, skills = p.skills.map{ s =>
              if (s.id == skill.id) {
                s.copy(remainingCoolDown = s.coolDown)
              } else {
                s
              }
            })
          case empty: Empty => empty
        }
      } else {
        unit
      }
    }
  }

  private def executeMovement(originalPosition: Int, target: Int, offset: Int): Unit = {
    val newPosition = if (originalPosition < target) {
      target - offset
    } else {
      target + offset
    }
    if (units(newPosition).isInstanceOf[Empty]) {
      swap(originalPosition, newPosition)
    }
  }

}
