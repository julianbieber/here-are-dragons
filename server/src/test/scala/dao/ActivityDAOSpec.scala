package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.SQLSpec._
import testUtil.GeneratorUtil._

class ActivityDAOSpec extends AnyFlatSpec with Matchers {

  "ActivityDAO" must "record start and sp of activities" in withPool { pool =>
    val userDAO = new UserDAO(pool)
    val activityDAO = new ActivityDAO(pool)

    val userId = userDAO.createUser(oneRandom(genString), oneRandom(genString)).get

    activityDAO.getCurrentActivity(userId) must be(None)

    activityDAO.startActivity(userId, "RUNNING")

    activityDAO.getCurrentActivity(userId) must be(Some("RUNNING"))

    activityDAO.stopActivity(userId, "RUNNING")

    activityDAO.getCurrentActivity(userId) must be(None)

  }

  it must "process activities" in withPool { pool =>
    val userDAO = new UserDAO(pool)
    val activityDAO = new ActivityDAO(pool)

    val userId = userDAO.createUser(oneRandom(genString), oneRandom(genString)).get

    activityDAO.startActivity(userId, "RUNNING")
    activityDAO.stopActivity(userId, "RUNNING")

    activityDAO.startActivity(userId, "CYCLING")
    activityDAO.stopActivity(userId, "CYCLING")

    val activities = activityDAO.getNotProcessedActivities()

    activities must have size 4

    activityDAO.setProcessed(activities)

    activityDAO.getNotProcessedActivities() must be(empty)

  }

  it must "combine activities when the start and end are close together" in withPool { pool =>
    val userDAO = new UserDAO(pool)
    val activityDAO = new ActivityDAO(pool)

    val userId = userDAO.createUser(oneRandom(genString), oneRandom(genString)).get

    activityDAO.startActivity(userId, "RUNNING")
    activityDAO.stopActivity(userId, "RUNNING")

    activityDAO.startActivity(userId, "RUNNING")
    activityDAO.stopActivity(userId, "RUNNING")

    activityDAO.startActivity(userId, "CYCLING")
    activityDAO.stopActivity(userId, "CYCLING")
    val activitiesBefore = activityDAO.getNotProcessedActivities()
    activitiesBefore must have size 6

    activityDAO.combineActivities()

    val activitiesAfter = activityDAO.getNotProcessedActivities()
    activitiesAfter must have size 4

  }

}
