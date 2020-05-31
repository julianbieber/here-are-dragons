package controllers

import com.github.plokhotnyuk.jsoniter_scala.macros._

import com.google.inject.Inject
import com.twitter.finagle.http.Request
import dao.{ExperienceDAO, UserDAO}
import model.Character.Character

import scala.concurrent.{ExecutionContext, Future}

class CharacterController @Inject() (override val userDAO: UserDAO, executionContext: ExecutionContext, experienceDAO: ExperienceDAO) extends UserUtil {
  implicit val ec: ExecutionContext = executionContext

  implicit val characterCodec = JsonCodecMaker.make[Character]

  get("/character") { request: Request =>
    withUserAsyncAuto(request) { userId =>
      Future {
        val experience = experienceDAO.getExperiences(userId)
        val rangerExperience = experience.find(_.experienceType == 1).map(_.amount).getOrElse(0L)
        val sorcererExperience = experience.find(_.experienceType == 2).map(_.amount).getOrElse(0L)
        val warriorExperience = experience.find(_.experienceType == 3).map(_.amount).getOrElse(0L)

        Character(rangerExperience, sorcererExperience, warriorExperience)
      }
    }
  }
}
