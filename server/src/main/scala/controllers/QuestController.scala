package controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.Inject
import dao.{ActiveInGroup, GroupDAO, PositionDAO, QuestDAO, UserDAO}
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class QuestController @Inject()(val questDAO: QuestDAO, positionDAO: PositionDAO,groupDAO:GroupDAO, override val userDAO: UserDAO, executionContext: ExecutionContext) extends UserUtil {

  private implicit val ec: ExecutionContext = executionContext


  post("/activateQuest") {request: Request =>
    val y = request.getParam("questID").toLong
    val diff = request.getParam("difficulty").toInt
    withUser(request) { userId =>
      questDAO.makeActive(y, userId,diff);
      response.ok
    }
  }

  post("/unactivateQuest") {request: Request =>
    val y = request.getParam("questID").toLong
    withUser(request) { userId =>
      questDAO.deleteQuest(y,userId)
      response.ok
    }
  }

  post("/unactivateQuest1") {request: Request =>
    withUser(request) { userId =>
      questDAO.makeUnActive(userId)
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

  get("/calculateDifficulty"){request: Request =>
    val y = request.getParam("questID").toLong
    withUser(request) { userId =>
      val i = positionDAO.getPosition(userId).get
      try {
        response.ok(writeToString(model.Quest.Difficulty(questDAO.calculateDifficulty(i.longitude, i.latitude, y,userId))))
      } catch {
        case NonFatal(e) => e.printStackTrace()
          response.internalServerError()
      }
    }
  }

  get("/nextQuestPosition") { request :Request =>
    withUser(request){ userId =>
      val activeQuest =  questDAO.getActiveQuestID(userId)
      questDAO.setProgress(activeQuest,userId)
      val nextPosition = questDAO.getPositionOfNextQuest(activeQuest,userId)
      response.ok(writeToString(model.Quest.nextPosition(nextPosition)))
    }
  }

  get("/activeQuestInGroup"){ request: Request =>
    withUser(request) { userId =>
      val activeQuestInGroup:ActiveInGroup=
      groupDAO.getGroup(userId).map{ internalGroup =>
        questDAO.getActiveQuestInGroup(internalGroup.members,userId)
      }.getOrElse(ActiveInGroup(false))
      response.ok(writeToString(activeQuestInGroup))
    }
  }

}
