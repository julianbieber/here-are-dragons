package service

import dao.SkillDAO
import model.Character.Attributes
import model.Dungeon.Skill
import util._

/**
 * Sets the boundaries for the dungeon generation
 */
object Difficulty {
  def generator(difficulty: Int, playerCount: Int): DungeonGenerator = {
    val mobsRanges = difficultyRangeToMobRange.reverse.find(_._1 <= difficulty).getOrElse(difficultyRangeToMobRange.head)._2
    val mobs = mobsRanges.map(mobsRange => Rng.between(mobsRange._1, mobsRange._2)).filterNot(_ == 0)

    val allowedPatterns = minDifficultyPerPattern.filter(_._1 <= difficulty).map(_._2)

    DungeonGenerator(mobs, allowedPatterns, (difficulty / 100.0f * 15).toInt * math.pow(playerCount, 1.4).toInt)
  }

  /**
   * the higher the difficulty the higher the total attribute points and the lower the number of enemies the higher the total attribute points per mob
   * @param difficulty
   * @param mobs
   * @return
   */
  def totalAttributes(difficulty: Int, mobs: Int): Int = math.max(1, {
    (difficulty / 100.0f * 20) / (mobs *  0.5)
  }.toInt)

  def skillToPower(skill: Skill, attributes: Attributes): Int = {
    val damage = (skill.damage + skill.strengthScaling * attributes.strength + skill.spellPowerScaling * attributes.spellPower + skill.dexterityScaling * attributes.dexterity).toInt /
      math.pow(1.2, attributes.constitution + attributes.evasion + attributes.willPower)
    val statusStrength = skill.status.stunned * 10 + skill.status.knockedDown * 10 + skill.status.shocked * 5 + skill.status.burning * 1 + skill.status.wet * 2
    val aoe = skill.targetPattern.length
    ((damage + statusStrength) * aoe).toInt
  }

  private[service] val difficultyRangeToMobRange = Seq(
    1 -> Seq(2 -> 3),
    10 -> Seq(2 -> 5, 0 -> 2),
    50 -> Seq(3 -> 7, 3 -> 6, 3 -> 7, 0 -> 3),
    90 -> Seq(4 -> 7, 5 -> 7, 6 -> 7, 2 -> 3, 1 -> 1)
  )

  val minDifficultyPerPattern = Seq(
    10 -> EnemyPattern(1f, Seq(SkillDAO.skills(3), SkillDAO.skills(7)), 100, 0), // Air elemental
    1 -> EnemyPattern(0.75f, Seq(SkillDAO.skills(12)), 150, 1), // Animated sword
    70 -> EnemyPattern(0.75f, Seq(SkillDAO.skills(2), SkillDAO.skills(6)), 200, 2), // fire dragon
    10 -> EnemyPattern(0.75f, Seq(SkillDAO.skills(1), SkillDAO.skills(19)), 130, 3), // fire drake
    1 -> EnemyPattern(0.75f, Seq(SkillDAO.skills(8), SkillDAO.skills(9)), 60, 4), // submerged orc
    10 -> EnemyPattern(0.75f, Seq(SkillDAO.skills(10), SkillDAO.skills.last), 100, 5), // untitled
    20 -> EnemyPattern(0.75f, Seq(SkillDAO.skills(9), SkillDAO.skills(21)), 20, 6), // goblin
    70 -> EnemyPattern(0.75f, Seq(SkillDAO.skills(5)), 200, 7), // ice dragon
    10 -> EnemyPattern(0.75f, Seq(SkillDAO.skills(4)), 130, 8), // ice drake
    1 -> EnemyPattern(0.75f, Seq(SkillDAO.skills(11)), 148, 9) // snake
  )
}

/**
 * Combines enemy patterns into a dungeon
 * @param numberOfEnemiesPerLevel
 * @param enemyPatterns
 * @param attributes
 */
case class DungeonGenerator(numberOfEnemiesPerLevel: Seq[Int], enemyPatterns: Seq[EnemyPattern], attributes: Int) {
  def generate(userIds: Seq[Int], players: Seq[PlayerUnit]): Dungeon = {
    val floors = numberOfEnemiesPerLevel.map { numberOfEnemies =>
      (0 to numberOfEnemies).map { i =>
        enemyPatterns.randomOne.generate(players.size + i, attributes).asInstanceOf[DungeonUnit]
      }
    }.zipWithIndex.map{ case (floor, i) =>
      if (i == 0) {
        players ++ floor
      } else {
        floor
      }
    }

    service.Dungeon(
      userIds = userIds,
      currentLevel = 0,
      units = floors.map(_.toBuffer),
      currentTurn = 0,
      turnOrder = floors.head.map(_.id),
    )
  }
}


/**
 * Generates a single enemy based on its boundaries
 * @param offensiveScale
 * @param fixedSkills
 * @param skillScore
 * @param prefabId
 */
case class EnemyPattern(
  offensiveScale: Float,
  fixedSkills: Seq[Skill],
  skillScore: Int,
  prefabId: Int,
) {
  def generate(id: Int, attributePoints: Int): NPC = {
    val totalScaling = fixedSkills.map(s => s.strengthScaling + s.spellPowerScaling + s.dexterityScaling).sum
    val warriorPercentage = fixedSkills.map(_.strengthScaling).sum / totalScaling
    val sorcererPercentage = fixedSkills.map(_.spellPowerScaling).sum / totalScaling
    val rangerPercentage = fixedSkills.map(_.dexterityScaling).sum / totalScaling

    val attributes = Attributes(
      strength = (attributePoints * warriorPercentage * offensiveScale).toInt + 1,
      constitution = (attributePoints * warriorPercentage * (1- offensiveScale)).toInt + 1,
      spellPower = (attributePoints * sorcererPercentage * offensiveScale).toInt + 1,
      willPower = (attributePoints * sorcererPercentage * (1 - offensiveScale)).toInt + 1,
      dexterity = (attributePoints * rangerPercentage * offensiveScale).toInt + 1,
      evasion = (attributePoints * rangerPercentage * (1 - offensiveScale)).toInt + 1
    )

    val remainingSkills = SkillDAO.skills.without(fixedSkills, _.id, (_:Skill).id)

    val skillsToSelect = Rng.between(1, 5 - fixedSkills.length)

    val maxPowerPerSkill = skillScore / skillsToSelect
    val skills = remainingSkills.filter(Difficulty.skillToPower(_, attributes) <= maxPowerPerSkill).shuffle.take(skillsToSelect)


    NPC(
      id,
      prefabId,
      attributes.constitution * 10,
      skills,
      4,
      6,
      2,
      Status.empty,
      attributes: Attributes
    )
  }
}