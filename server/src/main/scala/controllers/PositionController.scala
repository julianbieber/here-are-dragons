package controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.Inject
import dao.{UserDAO,PositionDAO}
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import scala.concurrent.ExecutionContext
import model.Position.{positionResponse, PositionRequest}

import scala.concurrent.{ExecutionContext, Future}

class PositionController @Inject()(override val userDAO: UserDAO,val positionDAO: PositionDAO, executionContext: ExecutionContext) extends UserUtil {
  private implicit val loginResponseCodec: JsonValueCodec[positionResponse] = JsonCodecMaker.make[positionResponse]
  private implicit val loginCodec: JsonValueCodec[PositionRequest] = JsonCodecMaker.make[PositionRequest]
  private implicit val ec: ExecutionContext = executionContext
  private implicit val daoPositon :JsonValueCodec[dao.DAOPosition]=JsonCodecMaker.make[dao.DAOPosition];

  get("/Positon") { request: Request =>
    withUser(request) { userId =>
      positionDAO.getPosition(userId).map { positionResponse =>
        response.ok(writeToString(positionResponse))
      }.getOrElse(response.badRequest)
    }
  }
  post("/Position") { request: Request =>
    val position = readFromString[PositionRequest](request.contentString)
    withUser(request) { userId =>
      positionDAO.setPosition(userId,position.long, position.lat)
      response.ok
    }
  }

}
