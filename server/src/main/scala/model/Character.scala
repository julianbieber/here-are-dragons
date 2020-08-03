package model

import model.Dungeon.{ExtendedSkillBar, Skill}
import service.{PlayerUnit, Status}

object Character {
  case class Character(
    rangerExperience: Long,
    sorcererExperience: Long,
    warriorExperience: Long,
    skillBar: ExtendedSkillBar,
    unlockedAttributes: Attributes,
    selectedAttributes: Attributes,
    canLevelUp: Boolean,
    canUnlockWarrior: Boolean,
    canUnlockSorcerer: Boolean,
    canUnlockRanger: Boolean,
    maxSelectableAttributes: Int
  )

  case class Attributes(
    strength: Int, // Warrior offensive
    constitution: Int, // Warrior defensive
    spellPower: Int, // Sorcerer offensive
    willPower: Int, // Sorcerer defensive
    dexterity: Int, // Ranger offensive
    evasion: Int // Ranger defensive
  ) {
    def check(level: Int): Boolean = strength + constitution + spellPower + willPower + dexterity + evasion <= Levels.maxAttributes(level)
    def check(cap: Attributes): Boolean = {
      strength <= cap.strength &&
      constitution <= cap.constitution &&
      spellPower <= cap.spellPower &&
      willPower <= cap.willPower &&
      dexterity <= cap.dexterity &&
      evasion <= cap.evasion
    }

    def add(other: Attributes): Attributes = {
      copy(
        strength = strength + other.strength,
        constitution = constitution + other.constitution,
        spellPower = spellPower + other.spellPower,
        willPower = willPower + other.willPower,
        dexterity = dexterity + other.dexterity,
        evasion = strength + other.evasion
      )
    }

    def warriorCosts: Long = (math.pow(2, strength) + math.pow(2, constitution)).toLong - 2
    def sorcererCosts: Long = (math.pow(2, spellPower) + math.pow(2, willPower)).toLong - 2
    def rangerCosts: Long = (math.pow(2, dexterity) + math.pow(2, evasion)).toLong - 2
  }

  object Attributes {
    def empty: Attributes = Attributes(0, 0, 0, 0, 0, 0)
  }

  object Levels {
    val requirements = IndexedSeq(0L, 1000L)
    val maxAttributes = IndexedSeq(0, 2)
  }

  case class Talent(
    id: Int,
    name: String,
    skillUnlock: Int,
    activityId: Int,
    distance: Option[Int],
    speed: Option[Int],
    time: Option[Int],
    timeInDay: Option[Int]
  )

  case class TalentTreeNode(
    talent: Talent,
    next: Seq[TalentTreeNode],
  )

  case class TalentResponse(
    unlocking: Option[Talent],
    unlockOptions: Seq[Talent]
  )

}
