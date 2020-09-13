package service

import java.io.PrintWriter

import dao.SkillDAO
import model.Character.Attributes
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class DamageCalcSpec extends AnyFlatSpec with Matchers {
  private val player = PlayerUnit(0, 0, 0, 0, 0, 0, Status.empty, Attributes.empty, Seq())
  private val enemy = NPC(0, 0, 0, Seq(), 0, 0, 0, Status.empty, Attributes.empty)

  "DamageCalc" must "" in {
    SkillDAO.skills.foreach{ skill =>
      val writer = new PrintWriter(s"skills/${skill.name.replace(" ", "_")}.csv")
      println(skill.name)
      writer.write(s"off,def,dmg\n")
      (1 to 15).foreach{ offensive =>
        (1 to 15).foreach{ defensive =>
          val caster = Attributes.all(offensive)
          val target = Attributes.all(defensive)
          val dmg = DamageCalc(player.copy(attributes = caster), enemy.copy(attributes = target), skill)
          writer.write(s"$offensive,$defensive,$dmg\n")
        }
      }
      writer.close()
    }
  }
}
