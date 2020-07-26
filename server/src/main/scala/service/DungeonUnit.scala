package service

import model.Character.Attributes
import model.Dungeon.Skill

sealed trait DungeonUnit {
  def countDownCDs(): DungeonUnit

  def id: Int

  val ap: Int

  var status: Status
  val attributes: Attributes

  def applySkill(status: Status, damage: Int): DungeonUnit

  def gainAP(): DungeonUnit

  def applyStatus(): DungeonUnit

}



case class PlayerUnit(
  id: Int,
  userId: Int,
  health: Int,
  override val ap: Int,
  maxAP: Int,
  apGain: Int,
  var status: Status,
  attributes: Attributes,
  skills: Seq[Skill]
) extends DungeonUnit {
  override def applySkill(status: Status, damage: Int): DungeonUnit = {
    status.add(status)
    val newHealth = health - damage
    if (newHealth <= 0) {
      Empty(id, 0, status)
    } else {
      copy(health = newHealth)
    }
  }

  override def gainAP(): DungeonUnit = {
    copy(ap = math.max(0, math.min(maxAP, ap + status.calculateAPGain(apGain))))
  }

  override def applyStatus(): DungeonUnit = {
    val newHealth = if (status.burning > 0) {
      health - 10
    } else {
      health
    }
    status.countDown()
    if (newHealth > 0) {
      copy(health = newHealth)
    } else {
      Empty(id, 0, status)
    }
  }

  override def countDownCDs(): DungeonUnit = {
    val newSkills = skills.map{ skill =>
      skill.copy(remainingCoolDown = math.max(skill.remainingCoolDown - 1, 0))
    }
    copy(skills = newSkills)
  }
}

case class NPC(id: Int, prefabId: Int, health: Int, skills: Seq[Skill], ap: Int, maxAP: Int, apGain: Int, var status: Status, attributes: Attributes) extends DungeonUnit {
  override def applySkill(status: Status, damage: Int): DungeonUnit = {
    status.add(status)
    val newAP = if (status.knockedDown > 0 || status.stunned > 0){
      0
    } else {
      ap
    }
    val newHealth = health - damage
    if (newHealth <= 0) {
      Empty(id, 0, status)
    } else {
      copy(health = newHealth, ap = newAP)
    }
  }

  override def gainAP(): DungeonUnit = {
    copy(ap = math.min(maxAP, ap + apGain))
  }

  override def applyStatus(): DungeonUnit = {
    val newHealth = if (status.burning > 0) {
      health - 10
    } else {
      health
    }
    status.countDown()
    if (newHealth > 0) {
      copy(health = newHealth)
    } else {
      Empty(id, 0, status)
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
  override def applySkill(status: Status, damage: Int): DungeonUnit = {
    status.add(status.locationBased)
    this
  }

  override def gainAP(): DungeonUnit = this

  override def applyStatus(): DungeonUnit = {
    status.countDown()
    this
  }

  override def countDownCDs(): DungeonUnit = this
}
