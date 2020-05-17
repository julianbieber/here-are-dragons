package controllers
import dao.{GroupDAO, UserDAO}
import javax.inject.Inject
import model.Group.{Group, JoinRequest}
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.Request

import scala.concurrent.{ExecutionContext, Future}

class GroupController @Inject() (override val userDAO: UserDAO, val groupDAO: GroupDAO, executionContext: ExecutionContext) extends UserUtil {
  private implicit val ec: ExecutionContext = executionContext
  private implicit val joinRequestCodec = JsonCodecMaker.make[JoinRequest]
  private implicit val GroupCodec = JsonCodecMaker.make[Group]

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
      val group = groupDAO.getGroup(userId).getOrElse(Group(Seq(userId)))
      response.ok(writeToString(group))
    }
  }
}
