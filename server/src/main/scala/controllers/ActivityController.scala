package controllers

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.Request
import dao.{ActivityDAO, GroupDAO, UserDAO}
import javax.inject.Inject
import model.Activity._

import scala.concurrent.{ExecutionContext, Future}

class ActivityController @Inject() (override val userDAO: UserDAO, executionContext: ExecutionContext, activityDAO: ActivityDAO, groupDAO: GroupDAO) extends UserUtil {
  private implicit val ec: ExecutionContext = executionContext
  private val supportedActivities = Seq("RUNNING", "CYCLING")

  put("/activity") { request: Request =>
    withUserAsync(request){ userId =>
      Future {
        val activityType = request.getParam("type")
        if (!supportedActivities.contains(activityType)) {
          response.badRequest(s"Activity: ${activityType} is not supported, the ony supported activities are: ${supportedActivities.mkString(", ")}")
        } else {
          activityDAO.startActivity(userId, activityType)
          response.ok("")
        }
      }
    }
  }

  delete("/activity") { request: Request =>
    withUserAsync(request) { userId =>
      Future {
        activityDAO.getCurrentActivity(userId).map{ activity =>
          activityDAO.stopActivity(userId, activity._2)
        }.map(_ => response.ok("")).getOrElse(response.badRequest("User does not have a current activity to be stopped"))

      }
    }
  }
}
