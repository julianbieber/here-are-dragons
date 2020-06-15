package model

import model.Dungeon.ExtendedSkillBar

object Character {
  case class Character(
    rangerExperience: Long,
    sorcererExperience: Long,
    warriorExperience: Long,
    skillBar: ExtendedSkillBar
  )
}
