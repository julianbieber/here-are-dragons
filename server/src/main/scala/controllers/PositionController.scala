package controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.Inject
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

import scala.concurrent.{ExecutionContext, Future}

class PositionController @Inject()() extends Controller {

  get("/getPositon") { request: Request =>

  }

  post("/setPosition") { request: Request =>
    Future {
      val position = readFromString[PositionRequest](request.contentString)
      positionDao.getPosition(postion)
        .map { position => writeToString(CreateResponse(position)) }
        .map(response.ok)
        .getOrElse(response.badRequest("Could not create account"))
    }
  }

}
