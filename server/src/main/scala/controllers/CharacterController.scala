package controllers

import java.util.concurrent.Semaphore

import com.google.inject.Inject
import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.Request
import dao.{AttributesDAO, AttributesTable, ExperienceDAO, SkillDAO, SkillbarDAO, UserDAO, UserExperience}
import model.Character.{Attributes, Character, Levels}
import model.Dungeon.{ExtendedSkillBar, SkillBar}

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.mutable

class CharacterController @Inject() (override val userDAO: UserDAO, executionContext: ExecutionContext, experienceDAO: ExperienceDAO, skillbarDAO: SkillbarDAO, attributesDAO: AttributesDAO) extends UserUtil {
  implicit val ec: ExecutionContext = executionContext

  get("/character") { request: Request =>
    withUserAsyncAuto(request) { userId =>
      Future {
        getCharacter(userId)
      }
    }
  }

  private def getCharacter(userId: Int): Character = {
    val experience = experienceDAO.getExperiences(userId)

    val skillBar = skillbarDAO.getSkillBar(userId).map(extend).getOrElse(ExtendedSkillBar(userId, Seq(), Seq()))
    val attributes = attributesDAO.readAttributes(userId)

    val unlockAttempt = attributes.unlocked.add(Attributes(1, 0, 1, 0, 1, 0))
    Character(
      experience.ranger,
      experience.sorcerer,
      experience.warrior,
      skillBar,
      attributes.unlocked,
      attributes.selected,
      isAllowedToLevelUp(attributes.level, experience),
      unlockAttempt.warriorCosts <= experience.warrior,
      unlockAttempt.sorcererCosts <= experience.sorcerer,
      unlockAttempt.rangerCosts <= experience.ranger,
      Levels.maxAttributes(attributes.level))
  }

  post("/character/attributes/unlock") { request: Request =>
    withUserAsyncAuto(request) { userId =>
      Future {
        val lock = CharacterController.getUserLock(userId)
        lock.acquire()
        try {
          val currentAttributes = attributesDAO.readAttributes(userId)
          val attributesDiff = readFromString[Attributes](request.contentString)
          val combined = currentAttributes.unlocked.add(attributesDiff)

          val experience = experienceDAO.getExperiences(userId)
          if (
            combined.warriorCosts <= experience.warrior &&
            combined.sorcererCosts <= experience.sorcerer &&
            combined.rangerCosts <= experience.ranger
          ) {
            attributesDAO.storeAttributes(userId, currentAttributes.selected, combined, currentAttributes.level)
          } else {
            throw new RuntimeException("")
          }
        } finally {
          lock.release()
        }
      }
    }
  }

  post("/character/attributes/select") { request: Request =>
    withUserAsyncAuto(request) { userId =>
      Future {
        val lock = CharacterController.getUserLock(userId)
        lock.acquire()
        try {
          val currentAttributes = attributesDAO.readAttributes(userId)
          val attributesDiff = readFromString[Attributes](request.contentString)
          val combined = currentAttributes.selected.add(attributesDiff)
          if (combined.check(currentAttributes.level) && combined.check(currentAttributes.unlocked)) {
            attributesDAO.storeAttributes(userId, combined, currentAttributes.unlocked, currentAttributes.level)
          } else {
            throw new RuntimeException("")
          }
        } finally {
          lock.release()
        }
      }
    }
  }

  post("/character/levelUp") { request: Request =>
    withUserAsyncAuto(request) { userId =>
      Future {
        val lock = CharacterController.getUserLock(userId)
        lock.acquire()
        try {
          val currentAttributes = attributesDAO.readAttributes(userId)
          val experience = experienceDAO.getExperiences(userId)

          if (isAllowedToLevelUp(currentAttributes.level, experience)) {
            attributesDAO.storeAttributes(userId, currentAttributes.selected, currentAttributes.unlocked, currentAttributes.level + 1)
          } else {
            throw new RuntimeException("")
          }
        } finally {
          lock.release()
        }
      }
    }
  }

  private def isAllowedToLevelUp(currentLevel: Int, userExperience: UserExperience): Boolean = {
    if (currentLevel + 1 < Levels.requirements.length) {
      val requirement = Levels.requirements(currentLevel + 1)
      requirement <= userExperience.ranger ||
      requirement <= userExperience.sorcerer ||
      requirement <= userExperience.warrior
    } else {
      false
    }
  }

  post("/character/skills/select") { request: Request =>
    withUserAuto(request) { userId =>
      val skillId = request.params("skill").toInt
      skillbarDAO.selectSkill(userId, skillId)
      skillbarDAO.getSkillBar(userId).map(extend).getOrElse(ExtendedSkillBar(userId, Seq(), Seq()))
    }
  }

  delete("/character/skills/select") { request: Request =>
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

object CharacterController {
  def getUserLock(userId: Int): Semaphore = {
    synchronized{
      locks.getOrElseUpdate(userId, { new Semaphore(1)})
    }
  }

  private val locks = mutable.Map[Int, Semaphore]()
}
