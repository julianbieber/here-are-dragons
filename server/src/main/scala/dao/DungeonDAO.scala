package dao

import java.util.concurrent.atomic.AtomicInteger

import model.Dungeon.{Skill, SkillUsage}
import service.{DungeonGenerator, DungeonService, PlayerCharacter}

import scala.collection.mutable

object DungeonDAO {

  def newDungeon(userId: Int, playerCharacter: PlayerCharacter, generator: DungeonGenerator): (Int, Dungeon) = {
    val id = maxId.incrementAndGet()
    val dungeon = generator.generate(userId, Seq(playerCharacter.toUnit.copy(id = 0)))

    dungeons.synchronized {
      dungeons.put(id, dungeon)
    }
    id -> dungeon
  }

  def getDungeon(id: Int): Option[Dungeon] = dungeons.synchronized {
    dungeons.get(id)
  }

  def updateDungeon(id: Int, d: Dungeon): Dungeon = dungeons.synchronized {
    dungeons.put(id, d)
    d
  }

  def getDungeonForUser(userId: Int): Option[(Int, Dungeon)] = {
    dungeons.synchronized {
      dungeons.find { case (_, dungeon) =>
        dungeon.userId == Option(userId)
      }
    }
  }

  def getDungeonForGroup(groupId: String): Option[(Int, Dungeon)] = {
    dungeons.synchronized {
      dungeons.find { case (_, dungeon) =>
        dungeon.groupId == Option(groupId)
      }
    }
  }

  private val dungeons: mutable.Map[Int, Dungeon] = mutable.Map[Int, Dungeon]()
  private val maxId = new AtomicInteger(0)

}

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
    currentTurnUnit.applyStatuses()
  }

  def moveTurnPointer(): Unit = {
    currentTurn = (currentTurn + 1) % turnOrder.length
  }

  def completed: (Boolean, Boolean) = {
    !units.exists(_.isInstanceOf[NPC]) -> !units.exists(_.isInstanceOf[PlayerUnit])
  }

  def isAllowedToUse(unitId: Int, skillUsage: SkillUsage): Boolean = {
    println(turnOrder, currentTurn)
    println(currentTurnUnit.id, unitId)
    println(skillUsage.skill.apCost, currentTurnUnit.ap)
    println(DungeonService.identifyTargetable(this, skillUsage.skill, findUnitById(unitId)._2), (skillUsage.targetPosition))
    currentTurnUnit.id == unitId &&
      skillUsage.skill.apCost <= currentTurnUnit.ap &&
      DungeonService.identifyTargetable(this, skillUsage.skill, findUnitById(unitId)._2).contains(skillUsage.targetPosition)
  }

  def currentTurnUnit: DungeonUnit = findUnitById(turnOrder(currentTurn))._1

  def findUser(userId: Int): (PlayerUnit, Int) = {
    units.zipWithIndex.collectFirst { case (unit: PlayerUnit, i) if unit.userId == userId => unit -> i }.get
  }

  def findUnitById(unitId: Int): (DungeonUnit, Int) = units.zipWithIndex.collectFirst { case (unit, i) if unit.id == unitId => unit -> i }.get

  private def swap(unitPosition: Int, position: Int): Unit = {
    val tmp = units(position)

    units(position) = units(unitPosition)
    units(unitPosition) = tmp
  }

  def applySkill(casterId: Int, skill: Skill, targetPosition: Int): Unit = {
    val (_, casterPosition) = findUnitById(casterId)
    val hits = DungeonService.identifyHits(this, skill, targetPosition)
    val hitUnitIds = hits.map(units(_).id)
    units.transform { unit =>
      if (hitUnitIds.contains(unit.id)) {
        unit.applySkill(skill)
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
          case p: PlayerUnit => p.copy(ap = p.ap - skill.apCost)
          case p: NPC => p.copy(ap = p.ap - skill.apCost)
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

sealed trait DungeonUnit {
  def id: Int

  def ap: Int = 0

  def status: Status

  def applySkill(skill: Skill): DungeonUnit

  def applyStatuses(): DungeonUnit

  def gainAP(): DungeonUnit
}

case class PlayerUnit(
  id: Int,
  userId: Int,
  health: Int,
  override val ap: Int,
  maxAP: Int,
  apGain: Int,
  override val status: Status
) extends DungeonUnit {
  override def applySkill(skill: Skill): DungeonUnit = {
    val newHealth = health - skill.damage
    if (newHealth <= 0) {
      Empty(id, 0, status)
    } else {
      copy(health = newHealth)
    }
  }

  override def gainAP(): DungeonUnit = {
    copy(ap = math.min(maxAP, ap + apGain))
  }

  override def applyStatuses(): DungeonUnit = this
}

case class NPC(id: Int, prefabId: Int, health: Int, skills: Seq[Skill], override val ap: Int, maxAP: Int, apGain: Int, override val status: Status) extends DungeonUnit {
  override def applySkill(skill: Skill): DungeonUnit = {
    val newHealth = health - skill.damage
    if (newHealth <= 0) {
      Empty(id, 0, status)
    } else {
      copy(health = newHealth)
    }
  }

  override def gainAP(): DungeonUnit = {
    copy(ap = math.min(maxAP, ap + apGain))
  }

  override def applyStatuses(): DungeonUnit = this
}

case class Empty(id: Int, prefabId: Int, override val status: Status) extends DungeonUnit {
  override def applySkill(skill: Skill): DungeonUnit = {
    this
  }

  override def applyStatuses(): DungeonUnit = this

  override def gainAP(): DungeonUnit = this
}

case class Status(
  burning: Int,
  wet: Int,
  stunned:Int,
  shocked: Int,
  knockedDown: Int
) {
  def countDown(): Status = {
    copy(
      burning = math.max(burning-1, 0),
      wet = math.max(wet-1, 0),
      stunned = math.max(stunned-1, 0),
      shocked = math.max(shocked-1, 0),
      knockedDown = math.max(knockedDown-1, 0),
    )
  }

  def setOnFire(duration: Int): Status = copy(
    burning = math.max(burning, duration),
    wet = 0
  )

  def drowse(duration: Int): Status = copy(
    wet = math.max(wet, duration),
    burning = 0
  )

  def stun(duration: Int): Status = copy(
    stunned = math.max(stunned, duration),
    shocked = 0
  )

  def shock(duration: Int): Status = {
    if (shocked > 0) {
      copy(stunned = math.max(math.max(stunned, duration), shocked))
    } else {
      copy(shocked = duration)
    }
  }

  def knockDown(duration: Int): Status = {
    copy(knockedDown = math.max(knockedDown, duration))
  }

}

object Status {
  def empty: Status = Status(0, 0, 0, 0, 0)
}