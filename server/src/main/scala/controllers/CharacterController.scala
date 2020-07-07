package controllers

import com.google.inject.Inject
import com.twitter.finagle.http.Request
import dao.{AttributesDAO, AttributesTable, ExperienceDAO, SkillDAO, SkillbarDAO, UserDAO}
import model.Character.{Attributes, Character}
import model.Dungeon.{ExtendedSkillBar, SkillBar}

import scala.concurrent.{ExecutionContext, Future}

class CharacterController @Inject() (override val userDAO: UserDAO, executionContext: ExecutionContext, experienceDAO: ExperienceDAO, skillbarDAO: SkillbarDAO, attributesDAO: AttributesDAO) extends UserUtil {
  implicit val ec: ExecutionContext = executionContext

  get("/character") { request: Request =>
    withUserAsyncAuto(request) { userId =>
      Future {
        val experience = experienceDAO.getExperiences(userId)
        val rangerExperience = experience.find(_.experienceType == 1).map(_.amount).getOrElse(0L)
        val sorcererExperience = experience.find(_.experienceType == 2).map(_.amount).getOrElse(0L)
        val warriorExperience = experience.find(_.experienceType == 3).map(_.amount).getOrElse(0L)

        val skillBar = skillbarDAO.getSkillBar(userId).map(extend).getOrElse(ExtendedSkillBar(userId, Seq(), Seq()))
        val attributes = attributesDAO.readAttributes(userId).getOrElse(AttributesTable(userId, Attributes.empty, Attributes.empty, 0))

        Character(rangerExperience, sorcererExperience, warriorExperience, skillBar, attributes.unlocked, attributes.selected)
      }
    }
  }

  post("/character/select") { request: Request =>
    withUserAuto(request) { userId =>
      val skillId = request.params("skill").toInt
      skillbarDAO.selectSkill(userId, skillId)
      skillbarDAO.getSkillBar(userId).map(extend).getOrElse(ExtendedSkillBar(userId, Seq(), Seq()))
    }
  }

  delete("/character/select") { request: Request =>
    withUserAuto(request) { userId =>
      val skillId = request.params("skill").toInt
      skillbarDAO.unselectSkill(userId, skillId)
      skillbarDAO.getSkillBar(userId).map(extend).getOrElse(ExtendedSkillBar(userId, Seq(), Seq()))
    }
  }

  private def extend(skillBar: SkillBar): ExtendedSkillBar = {
    ExtendedSkillBar(
      skillBar.userId,
      SkillDAO.extend(skillBar.selected),
      SkillDAO.extend(skillBar.unlocked)
    )
  }
}
