package service

import dao.SkillDAO
import model.Character.Attributes
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import pprint.pprintln
import service.Difficulty.difficultyRangeToMobRange

class DifficultySpec extends AnyFlatSpec with Matchers {
  val attributeRang = 1 until 15

  val allAttributes = for {
    strength <- attributeRang
    con <- attributeRang
    spellPower <- attributeRang
    willPower <- attributeRang
    dexterity <- attributeRang
    evasion <- attributeRang
  } yield {
    Attributes(
      strength,
      con,
      spellPower,
      willPower,
      dexterity,
      evasion
    )
  }


  "Difficulty" must "produce reasonable spell scores" in {
    SkillDAO.skills.foreach{ skill =>
      val powers = allAttributes.map{ attributes =>
        attributes -> Difficulty.skillToPower(skill, attributes)
      }
      val max = powers.maxBy(_._2)
      val min = powers.minBy(_._2)
      min._2 must be >= 0
      println(skill.name, min, max)

    }
  }

  it must "allow a total of 40 points per lvl 100 mob" in {
    (0 to 100).map { difficulty =>
       Difficulty.difficultyRangeToMobRange.reverse.find(_._1 <= difficulty).getOrElse(difficultyRangeToMobRange.head)._2.map{ case (minMobs, maxMobs) =>
         val min = math.max(1, minMobs)
         (min to maxMobs).foreach { mobs =>
           Difficulty.totalAttributes(difficulty, mobs) must be <= 40
         }
       }
    }
  }

  it must "generate reasonable attributes" in {
    Difficulty.minDifficultyPerPattern.foreach{ case (_, pattern) =>
      val difficulty = 50
      val playerCount = 1
      pprintln(pattern.generate(1, (difficulty / 100.0f * 30).toInt * math.pow(playerCount, 1.4).toInt).attributes)
    }
  }

}
