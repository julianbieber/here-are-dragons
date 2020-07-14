package controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.Inject
import dao.{PositionDAO, UserDAO}
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

import scala.concurrent.ExecutionContext
import model.Position.{PositionRequest, UserPosition, positionResponse}

import scala.concurrent.{ExecutionContext, Future}

class PositionController @Inject()(override val userDAO: UserDAO,val positionDAO: PositionDAO, executionContext: ExecutionContext) extends UserUtil {

  get("/Positon") { request: Request =>
    withUser(request) { userId =>
      positionDAO.getPosition(userId).map { positionResponse =>
        response.ok(writeToString(UserPosition(positionResponse.userID, positionResponse.longitude, positionResponse.latitude)))
      }.getOrElse(response.badRequest)
    }
  }
  post("/Position") { request: Request =>
    val position = readFromString[PositionRequest](request.contentString)
    withUser(request) { userId =>
      positionDAO.setPosition(userId,position.longitude, position.latitude)
      response.ok
    }
  }

}

