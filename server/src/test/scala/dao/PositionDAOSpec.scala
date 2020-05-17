package dao

import model.Account.LoginResponse
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import testUtil.SQLSpec._
import testUtil.GeneratorUtil._
import scala.util.Random.{nextFloat,nextInt};


class PositionDAOSpec extends AnyFlatSpec with Matchers {
  "PositionDAO" must "get a Position" in withPool{ pool =>
    val dao = new PositionDAO(pool)
    val userID =  nextInt(100)
    val long = nextFloat()
    val lat =nextFloat();
    dao.setPosition(userID,lat,long)

    val pos = dao.getPosition(userID).get

    pos.longitude should be (long)
    pos.latitude should be (lat)

  }

}
