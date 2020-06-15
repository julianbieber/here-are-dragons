package controllers

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.Request
import dao.{ActivityDAO, UserDAO}
import javax.inject.Inject
import model.Activity._

import scala.concurrent.{ExecutionContext, Future}

class ActivityController @Inject() (override val userDAO: UserDAO, executionContext: ExecutionContext, activityDAO: ActivityDAO) extends UserUtil {
  private implicit val ec: ExecutionContext = executionContext
  private val supportedActivities = Seq("RUNNING", "CYCLING")

  put("/activity") { request: Request =>
    withUserAsync(request){ userId =>
      Future {
        val activityStart = readFromString[ActivityStart](request.contentString)
        if (!supportedActivities.contains(activityStart.activityType)) {
          response.badRequest(s"Activity: ${activityStart.activityType} is not supported, the ony supported activities are: ${supportedActivities.mkString(", ")}")
        } else {
          activityDAO.startActivity(userId, activityStart.activityType)
          response.ok("")
        }
      }
    }
  }

  delete("/activity") { request: Request =>
    withUserAsync(request) { userId =>
      Future {
        activityDAO.getCurrentActivity(userId).map{ activity =>
          activityDAO.stopActivity(userId, activity)
        }.map(_ => response.ok("")).getOrElse(response.badRequest("User does not have a current activity to be stopped"))

      }
    }
  }
}
