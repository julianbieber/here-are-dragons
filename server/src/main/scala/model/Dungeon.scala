package model

import service.Status

object Dungeon {

  case class AvailableDungeons(ids: Seq[Int])

  case class OpenRequest(questId: Int)

  case class DungeonResponse(
    dungeonId: Int,
    currentLevel: Int,
    units: Seq[Seq[UnitResponse]],
    myTurn: Boolean,
    ap: Int,
    won: Boolean,
    lost: Boolean
  )

  case class UnitResponse(
    tyype: String,
    userId: Option[Int],
    health: Option[Int],
    prefabId: Option[Int],
    status: Status,
    skills: Seq[Skill]
  )

  case class Turn(
    turnId: Int,
    skillsUsed: Seq[SkillUsage]
  )

  case class SkillUsage(
    targetPosition: Int,
    skill: Skill
  )

  case class Skill(
    id: Int,
    name: String,
    targetPattern: String,
    effectPattern: String,
    apCost: Int,
    damage: Int,
    strengthScaling: Float,
    spellPowerScaling: Float,
    dexterityScaling: Float,
    status: Status,
    moves: Boolean,
    movementOffset: Int,
    coolDown: Int,
    remainingCoolDown: Int
  )

  case class SkillBar(
    userId: Int,
    selected: Seq[Int],
    unlocked: Seq[Int]
  )

  case class ExtendedSkillBar(
    userId: Int,
    selected: Seq[Skill],
    unlocked: Seq[Skill]
  )

}
