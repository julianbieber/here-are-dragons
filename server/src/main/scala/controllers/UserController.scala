package controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.Inject
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import dao.{AttributesDAO, SkillDAO, SkillbarDAO, UserDAO}
import model.Account.{CreateResponse, LoginRequest, LoginResponse}
import model.Character.Attributes

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(userDao: UserDAO, executionContext: ExecutionContext, attributesDAO: AttributesDAO, skillbarDAO: SkillbarDAO) extends Controller {

  private implicit val ec: ExecutionContext = executionContext

  post("/login") { request: Request =>
    Future {
      val loginData = readFromString[LoginRequest](request.contentString)
      userDao.login(loginData.name, loginData.password)
        .map { loginResponse =>
          response.ok(writeToString(loginResponse))
        }
        .getOrElse(response.unauthorized)
    }
  }

  post("/createUser") { request: Request =>
    Future {
      val user = readFromString[LoginRequest](request.contentString)
      userDao.createUser(user.name, user.password)
        .map { userId =>
          attributesDAO.storeAttributes(userId, Attributes.all(1), Attributes.all(1), 0)
          skillbarDAO.unlock(userId, SkillDAO.skills.head.id)
          writeToString(CreateResponse(userId))
        }
        .map(response.ok)
        .getOrElse(response.badRequest("Could not create account"))
    }
  }

}
