package service

import model.Character.Attributes
import model.Dungeon.Skill

import scala.collection.mutable
import scala.util.Random

sealed trait DungeonUnit {
  def countDownCDs(): DungeonUnit

  def id: Int

  val ap: Int

  var status: Status
  val attributes: Attributes
  var attributesOffsets: mutable.Buffer[(Attributes, Int)] = mutable.Buffer()

  def applySkill(status: Status, damage: Int, newOffset: Option[Attributes], offsetDuration: Int): DungeonUnit

  def gainAP(): DungeonUnit

  def applyStatus(): DungeonUnit

  def countDownOffsets(): Unit = attributesOffsets = attributesOffsets.map(o => o._1 -> (o._2 - 1)).filter(_._2 > 0)

}



case class PlayerUnit(
  id: Int,
  userId: Int,
  healthOffset: Int,
  override val ap: Int,
  maxAP: Int,
  apGain: Int,
  var status: Status,
  attributes: Attributes,
  skills: Seq[Skill]
) extends DungeonUnit {
  override def applySkill(status: Status, damage: Int, newOffset: Option[Attributes], offsetDuration: Int): DungeonUnit = {
    if ((1.0f - (attributes.willPower + attributesOffsets.map(_._1.willPower).sum).toFloat) / 100.0f < Random.nextFloat()) {
      this.status.add(status)
    }
    newOffset.foreach{ o =>
      attributesOffsets.append((o, offsetDuration))
    }
    val newHealth = attributes.constitution * 10 + healthOffset - damage
    if (newHealth <= 0) {
      Empty(id, 0, status.locationBased)
    } else {
      copy(healthOffset = healthOffset - damage)
    }
  }

  override def gainAP(): DungeonUnit = {
    copy(ap = math.max(0, math.min(maxAP, ap + status.calculateAPGain(apGain))))
  }

  override def applyStatus(): DungeonUnit = {
    val newHealthOffset = if (status.burning > 0) {
      healthOffset - 10
    } else {
      healthOffset
    }
    status.countDown()
    if ((attributes.constitution + attributesOffsets.map(_._1.constitution).sum) * 10 + newHealthOffset > 0) {
      copy(healthOffset = newHealthOffset)
    } else {
      Empty(id, 0, status.locationBased)
    }
  }

  override def countDownCDs(): DungeonUnit = {
    val newSkills = skills.map{ skill =>
      skill.copy(remainingCoolDown = math.max(skill.remainingCoolDown - 1, 0))
    }
    copy(skills = newSkills)
  }
}

case class NPC(id: Int, prefabId: Int, healthOffset: Int, skills: Seq[Skill], ap: Int, maxAP: Int, apGain: Int, var status: Status, attributes: Attributes) extends DungeonUnit {
  override def applySkill(status: Status, damage: Int, newOffset: Option[Attributes], offsetDuration: Int): DungeonUnit = {
    if (1.0f - ((attributes.willPower + attributesOffsets.map(_._1.willPower).sum).toFloat / 100.0f) < Random.nextFloat()) {
      this.status.add(status)
    }

    newOffset.foreach{ o =>
      attributesOffsets.append((o, offsetDuration))
    }
    val newHealth = attributes.constitution * 10 + healthOffset - damage
    if (newHealth <= 0) {
      Empty(id, 0, status.locationBased)
    } else {
      copy(healthOffset = healthOffset - damage)
    }
  }

  override def gainAP(): DungeonUnit = {
    copy(ap = math.max(0, math.min(maxAP, ap + status.calculateAPGain(apGain))))
  }

  override def applyStatus(): DungeonUnit = {
    val newHealthOffset = if (status.burning > 0) {
      healthOffset - 10
    } else {
      healthOffset
    }
    status.countDown()
    if ((attributes.constitution + attributesOffsets.map(_._1.constitution).sum) * 10 + newHealthOffset > 0) {
      copy(healthOffset = newHealthOffset)
    } else {
      Empty(id, 0, status.locationBased)
    }
  }

  override def countDownCDs(): DungeonUnit = {
    val newSkills = skills.map{ skill =>
      skill.copy(remainingCoolDown = math.max(skill.remainingCoolDown - 1, 0))
    }
    copy(skills = newSkills)
  }
}

case class Empty(id: Int, prefabId: Int, var status: Status) extends DungeonUnit {
  val ap = 0
  val attributes: Attributes = Attributes.empty
  override def applySkill(status: Status, damage: Int, newOffset: Option[Attributes], offsetDuration: Int): DungeonUnit = {
    this.status.add(status.locationBased)
    this
  }

  override def gainAP(): DungeonUnit = this

  override def applyStatus(): DungeonUnit = {
    status.countDown()
    this
  }

  override def countDownCDs(): DungeonUnit = this
}
