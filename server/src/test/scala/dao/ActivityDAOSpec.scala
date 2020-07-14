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

    val Some((activity, runningStart)) = activityDAO.getCurrentActivity(userId)
    activity must be("RUNNING")
    activityDAO.stopActivity(userId, runningStart)

    activityDAO.getCurrentActivity(userId) must be(None)

  }

  it must "process activities" in withPool { pool =>
    val userDAO = new UserDAO(pool)
    val activityDAO = new ActivityDAO(pool)

    val userId = userDAO.createUser(oneRandom(genString), oneRandom(genString)).get

    activityDAO.startActivity(userId, "RUNNING")
    val Some((_, runningStart)) = activityDAO.getCurrentActivity(userId)
    activityDAO.stopActivity(userId, runningStart)

    activityDAO.startActivity(userId, "CYCLING")
    val Some((_, cyclingStart)) = activityDAO.getCurrentActivity(userId)
    activityDAO.stopActivity(userId, cyclingStart)

    val activities = activityDAO.getAllActivities()

    activityDAO.getAllActivities().filterNot(_.processed) must have size 2

    activityDAO.setProcessed(activities)

    activityDAO.getAllActivities().filterNot(_.processed) must be(empty)

  }

  it must "combine activities when the start and end are close together" in withPool { pool =>
    val userDAO = new UserDAO(pool)
    val activityDAO = new ActivityDAO(pool)

    val userId = userDAO.createUser(oneRandom(genString), oneRandom(genString)).get

    activityDAO.startActivity(userId, "RUNNING")
    val Some((_, runningStart1)) = activityDAO.getCurrentActivity(userId)
    activityDAO.stopActivity(userId, runningStart1)

    activityDAO.startActivity(userId, "RUNNING")
    val Some((_, runningStart2)) = activityDAO.getCurrentActivity(userId)
    activityDAO.stopActivity(userId, runningStart2)

    activityDAO.startActivity(userId, "CYCLING")
    val Some((_, cyclingStart)) = activityDAO.getCurrentActivity(userId)
    activityDAO.stopActivity(userId, cyclingStart)

    val activitiesBefore = activityDAO.getAllActivities()
    activitiesBefore must have size 2
  }

}
