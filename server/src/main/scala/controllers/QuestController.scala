package controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.Inject
import dao.{PositionDAO, QuestDAO, UserDAO}
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class QuestController @Inject()(val questDAO: QuestDAO, positionDAO: PositionDAO, override val userDAO: UserDAO, executionContext: ExecutionContext) extends UserUtil {

  private implicit val ec: ExecutionContext = executionContext


  post("/activateQuest") {request: Request =>
    val y = request.getParam("questID").toLong
    withUser(request) { userId =>
      questDAO.makeActive(y, userId);
      response.ok
    }
  }

  post("/unactivateQuest") {request: Request =>
    val y = request.getParam("questID").toLong
    withUser(request) { userId =>
      questDAO.makeUnActive(y,userId);
      response.ok
    }
  }

  get("/getListOfQuests") { request: Request =>
    val y = request.getParam("distance").toFloat
    withUser(request) { userId =>
      var i = positionDAO.getPosition(userId).get
      try {
        response.ok(writeToString(model.Quest.QuestsResponse(questDAO.getListOfActivataibleQuestsNerby(i.longitude, i.latitude, y,userId))))
      } catch {
        case NonFatal(e) => e.printStackTrace()
          response.internalServerError()
      }
    }
  }

}
