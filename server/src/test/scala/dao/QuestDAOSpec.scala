package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import testUtil.SQLSpec._

import scala.util.Random.{nextFloat, nextInt};


class QuestDAOSpec extends AnyFlatSpec with Matchers {
  "QuestAO" must "create a Quest" in withPool{ pool =>
    val dao = new QuestDAO(pool)
    val long = nextFloat()
    val lat =nextFloat();
    val userID = dao.createQuest(lat,long).get


    userID should be >= 0

  }

}
