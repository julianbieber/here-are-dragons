package dao

import org.scalacheck.Gen
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import testUtil.SQLSpec._
import testUtil.GeneratorUtil._
import util.TimeUtil


class PositionDAOSpec extends AnyFlatSpec with Matchers {

  private val comparableTimestamp = TimeUtil.now

  def genPosition(userId: Int): Gen[DAOPosition] = for {
    lat <- genFloat
    long <- genFloat
  } yield {
    DAOPosition(
      userId,
      longitude = long,
      latitude = lat,
      comparableTimestamp
    )
  }

  "PositionDAO" must "get a Position" in withPool{ pool =>
    val dao = new PositionDAO(pool)
    val userID = oneRandom(genPosInt)
    val long = oneRandom(genFloat)
    val lat = oneRandom(genFloat)

    dao.setPosition(userID, long, lat)

    val pos = dao.getPosition(userID).get

    pos.longitude should be (long)
    pos.latitude should be (lat)

    val long2 = oneRandom(genFloat)
    val lat2 = oneRandom(genFloat)

    dao.setPosition(userID, long2, lat2)
    val pos2 = dao.getPosition(userID).get

    pos2.longitude should be (long2)
    pos2.latitude should be (lat2)
  }

  it must "remember the position history" in withPool{ pool =>
    val user = oneRandom(genPosInt)
    val history = oneRandom(genSeq(genPosition(user)))

    val dao = new PositionDAO(pool)
    history.foreach{ pos =>
      dao.setPosition(user, lat = pos.latitude, long = pos.longitude)
    }

    val dbHistory = dao.getHistory(user, TimeUtil.now.minusSeconds(1), TimeUtil.now.plusSeconds(1)).map(_.copy(timestamp = comparableTimestamp))

    dbHistory should be(history)

  }

}
