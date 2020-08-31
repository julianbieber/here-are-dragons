package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.GeneratorUtil._
import testUtil.SQLSpec._
import util.TimeUtil

class CalisthenicsDAOSpec extends AnyFlatSpec with Matchers {

  "CalisthenicsDAO" must "set process" in withPool { pool =>
    val userDAO = new UserDAO(pool)
    val activityDAO = new CalisthenicsDAO(pool)

    val userId = userDAO.createUser(oneRandom(genString), oneRandom(genString)).get
    val timestamp = TimeUtil.now
    activityDAO.store(CalisthenicsRow(
      userId,
      1,
      Seq(0.01f),
      timestamp,
      false
    ))

    activityDAO.setProcessed(Seq(userId -> timestamp))

    activityDAO.getNotProcessed() must be(empty)

  }

  it must "retrieve rows between two timestamps" in withPool { pool =>
    val activityDAO = new CalisthenicsDAO(pool)

    val userId = 1
    val timestamp = TimeUtil.now
    activityDAO.store(CalisthenicsRow(
      userId,
      1,
      Seq(0.01f),
      timestamp,
      false
    ))

    activityDAO.getBetween(userId, timestamp.minusSeconds(1), timestamp.plusSeconds(1)) must have length 1
    activityDAO.getBetween(userId, timestamp.minusSeconds(2), timestamp.minusSeconds(1)) must have length 0


  }

}
