package service

import model.Dungeon.{Skill, SkillUsage}

import scala.collection.mutable

case class Dungeon(
  userIds: Seq[Int],
  var currentLevel: Int,
  units: Seq[mutable.Buffer[DungeonUnit]],
  var currentTurn: Int,
  var turnOrder: Seq[Int]
) {
  def isCurrentTurn(unitId: Int): Boolean = {
    currentTurnUnit.id == unitId
  }

  private def provideAP(): Unit = {
    units(currentLevel).transform { unit =>
      if (isCurrentTurn(unit.id)) {
        unit.gainAP()
      } else {
        unit
      }
    }
  }

  private def applyStatuses(): Unit = {
    units(currentLevel).transform{ unit =>
      if (isCurrentTurn(unit.id)) {
        unit.applyStatus()
      } else {
        unit
      }
    }
  }

  private def countDownOffsets(): Unit = {
    units(currentLevel).transform{ unit =>
      if (isCurrentTurn(unit.id)) {
        unit.countDownOffsets()
      }
      unit
    }
  }

  def endTurnActions(): Unit = {
    provideAP()
    countDownCDs()
    applyStatuses()
    countDownOffsets()
    moveTurnPointer()
  }

  def moveTurnPointer(): Unit = {
    turnOrder = turnOrder.filterNot(id => findUnitById(id)._1.isInstanceOf[Empty])
    currentTurn = (currentTurn + 1) % turnOrder.length
  }

  def conditionallyMoveToNextFloor(): Unit = {
    if (!units(currentLevel).exists(_.isInstanceOf[NPC]) && currentLevel < units.length - 1) {
      val players = units(currentLevel).filter(_.isInstanceOf[PlayerUnit])
      currentLevel += 1
      units(currentLevel).prependAll(players)
      turnOrder = units(currentLevel).map(_.id)
      currentTurn = 0
    }
  }

  def countDownCDs(): Unit = {
    units(currentLevel).transform{ unit =>
      if (isCurrentTurn(unit.id)) {
        unit.countDownCDs()
      } else {
        unit
      }
    }
  }

  def completed: (Boolean, Boolean) = {
    val remainingNPCs = units.map(_.count(_.isInstanceOf[NPC])).sum
    val remainingPlayers = units.map(_.count(_.isInstanceOf[PlayerUnit])).sum
    (remainingNPCs == 0) -> (remainingPlayers == 0)
  }

  def isAllowedToUse(unitId: Int, skillUsage: SkillUsage): Boolean = {
    currentTurnUnit.id == unitId &&
      skillUsage.skill.apCost <= currentTurnUnit.ap &&
      DungeonService.identifyTargetable(this, skillUsage.skill, findUnitById(unitId)._2).contains(skillUsage.targetPosition) &&
      skillUsage.skill.remainingCoolDown == 0
  }

  def currentTurnUnit: DungeonUnit = findUnitById(turnOrder(currentTurn))._1

  def findUser(userId: Int): (PlayerUnit, Int) = {
    units(currentLevel).zipWithIndex.collectFirst { case (unit: PlayerUnit, i) if unit.userId == userId => unit -> i }.get
  }

  def findUnitById(unitId: Int): (DungeonUnit, Int) = units(currentLevel).zipWithIndex.collectFirst { case (unit, i) if unit.id == unitId => unit -> i }.get

  private def swap(source: Int, target: Int): Unit = {
    val oldTargetUnit = units(currentLevel)(target)
    val targetStatus = oldTargetUnit.status.locationBased
    val oldSourceUnit = units(currentLevel)(source)
    oldTargetUnit.status = oldSourceUnit.status.locationBased
    oldSourceUnit.status.add(targetStatus)

    units(currentLevel)(source) = oldTargetUnit
    units(currentLevel)(target) = oldSourceUnit
  }

  def applySkill(casterId: Int, skill: Skill, targetPosition: Int): Unit = {
    val (_, casterPosition) = findUnitById(casterId)
    val hitPositions = DungeonService.identifyHits(this, skill, targetPosition)
    val hitUnitIds = hitPositions.map(units(currentLevel)(_).id)
    units(currentLevel).transform { unit =>
      if (hitUnitIds.contains(unit.id)) {
        unit.applySkill(skill.status, DamageCalc(findUnitById(casterId)._1, unit, skill), skill.attributesOffset.toOption, skill.attributesOffsetDuration)
      } else {
        unit
      }
    }
    if (skill.moves) {
      executeMovement(casterPosition, targetPosition, skill.movementOffset)
    }

    units(currentLevel).transform{ unit =>
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
    if (offset == 0) {
      if (units(currentLevel)(target).isInstanceOf[Empty]) {
        swap(originalPosition, target)
      }
    } else {
      val newPosition = if (originalPosition < target) {
        target - offset
      } else {
        target + offset
      }
      val copy = units(currentLevel)(originalPosition)
      units(currentLevel).append(copy)
      ((newPosition) until (units(currentLevel).length -1) ).reverse.map(i => i -> (i + 1)).foreach{ case (a, b) =>
        simpleSwap(a, b)
      }

      if (originalPosition < newPosition) {
        units(currentLevel).remove(originalPosition)
      } else {
        units(currentLevel).remove(originalPosition + 1)
      }

    }
  }

  private def simpleSwap(a: Int, b: Int): Unit = {
    val tmp = units(currentLevel)(a)
    units(currentLevel)(a) = units(currentLevel)(b)
    units(currentLevel)(b) = tmp
  }

}
