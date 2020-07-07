package model

import model.Dungeon.ExtendedSkillBar
import service.{PlayerUnit, Status}

object Character {
  case class Character(
    rangerExperience: Long,
    sorcererExperience: Long,
    warriorExperience: Long,
    skillBar: ExtendedSkillBar,
    unlockedAttributes: Attributes,
    selectedAttributes: Attributes
  )

  case class Attributes(
    strength: Int, // Warrior offensive
    constitution: Int, // Warrior defensive
    spellPower: Int, // Sorcerer offensive
    willpower: Int, // Sorcerer defensive
    dexterity: Int, // Ranger offensive
    evasion: Int // Ranger defensive
  ) {
    def check(max: Int): Boolean = strength + constitution + spellPower + willpower + dexterity + evasion <= max
    def check(cap: Attributes): Boolean = {
      strength <= cap.strength &&
      constitution <= cap.constitution &&
      spellPower <= cap.spellPower &&
      willpower <= cap.willpower &&
      dexterity <= cap.dexterity &&
      evasion <= cap.evasion
    }

    def toUnit(idInDungeon: Int, userId: Int): PlayerUnit = {
      PlayerUnit(
        id = idInDungeon,
        userId = userId,
        health = constitution * 10,
        ap = 4,
        maxAP = 6,
        apGain = 4,
        status = Status.empty,
        attributes = this
      )
    }
  }

  object Attributes {
    def empty: Attributes = Attributes(0, 0, 0, 0, 0, 0)
  }
}
