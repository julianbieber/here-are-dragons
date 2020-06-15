package controllers
import dao.{DAOPosition, GroupDAO, PositionDAO, UserDAO}
import javax.inject.Inject
import model.Group.{Group, JoinRequest}
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.Request

import scala.concurrent.{ExecutionContext, Future}

class GroupController @Inject() (override val userDAO: UserDAO, val groupDAO: GroupDAO, executionContext: ExecutionContext, val positionDAO: PositionDAO) extends UserUtil {

  private implicit val ec: ExecutionContext = executionContext

  post("/joinGroup") { request: Request =>
    withUserAsync(request) { userId =>
      Future {
        val joinRequest = readFromString[JoinRequest](request.contentString)
        if (groupDAO.addToGroup(userId, joinRequest.userName)) {
          response.ok()
        } else {
          response.badRequest("User does not exist")
        }
      }
    }
  }

  post("/leaveGroup") { request: Request =>
    withUser(request) { userId =>
      groupDAO.leaveGroup(userId)
      response.ok()
    }
  }

  get("/group") { request: Request =>
    withUser(request) { userId =>
      groupDAO.getGroup(userId).map{ internalGroup =>
        val positions = internalGroup.members.map { memberId =>
          positionDAO.getPosition(memberId).getOrElse(DAOPosition(memberId, 0.0f, 0.0f))
        }
        response.ok(writeToString(Group(positions)))
      }.getOrElse(response.notFound)
    }
  }
}
