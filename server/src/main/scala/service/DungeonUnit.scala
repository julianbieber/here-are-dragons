package service

import model.Dungeon.Skill

sealed trait DungeonUnit {
  def id: Int

  def ap: Int = 0

  var status: Status

  def applySkill(skill: Skill): DungeonUnit

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
  override var status: Status
) extends DungeonUnit {
  override def applySkill(skill: Skill): DungeonUnit = {
    status.add(skill.status)
    val newHealth = health - skill.damage
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
}

case class NPC(id: Int, prefabId: Int, health: Int, skills: Seq[Skill], override val ap: Int, maxAP: Int, apGain: Int, override var status: Status) extends DungeonUnit {
  override def applySkill(skill: Skill): DungeonUnit = {
    status.add(skill.status)
    val newAP = if (skill.status.knockedDown > 0 || skill.status.stunned > 0){
      0
    } else {
      ap
    }
    val newHealth = health - skill.damage
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
}

case class Empty(id: Int, prefabId: Int, override var status: Status) extends DungeonUnit {
  override def applySkill(skill: Skill): DungeonUnit = {
    status.add(skill.status)
    this
  }

  override def gainAP(): DungeonUnit = this

  override def applyStatus(): DungeonUnit = {
    status.countDown()
    this
  }
}
