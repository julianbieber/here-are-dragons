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

}
