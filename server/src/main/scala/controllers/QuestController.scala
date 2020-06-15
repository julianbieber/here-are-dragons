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

class QuestController @Inject()(val questDAO: QuestDAO,positionDAO: PositionDAO,override val userDAO: UserDAO,executionContext: ExecutionContext) extends UserUtil {

  private implicit val ec: ExecutionContext = executionContext


  get("/getListOfQuests") { request: Request =>
    println(request.getParam("distance"))
    val y=request.getParam("distance").toFloat
      withUser(request) { userId =>

        var i =positionDAO.getPosition(userId).get
        try{
          response.ok(writeToString(model.Quest.QuestsResponse(questDAO.getListOfQuestsNerby(i.longitude, i.latitude, y))))
        } catch {
          case NonFatal(e) => e.printStackTrace()
            response.internalServerError()
        }
       }
  }
}
