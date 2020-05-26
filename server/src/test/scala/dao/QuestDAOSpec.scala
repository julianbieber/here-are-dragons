package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import testUtil.SQLSpec._

import scala.util.Random.{nextFloat, nextInt};


class QuestDAOSpec extends AnyFlatSpec with Matchers {
  "QuestDAO" must "create and delete a Quest" in withPool{ pool =>
    val dao = new QuestDAO(pool)
    val long = nextFloat()
    val lat =nextFloat();
    val userID = dao.createQuest(lat,long).get

    userID should be >= 0

    dao.deleteQuest(userID)
    dao.getQuests(userID) should be (None)
  }
 it must "return a List of Quests nearby" in withPool{ pool =>
    val dao = new QuestDAO(pool)
    val long = nextFloat()
    val longNearBy= long+0.511111f;
   val longNotNearBy= long-1.511111f;
    val lat =nextFloat();
    val latNearBy = lat +0.5111111f;
    val userID = dao.createQuest(latNearBy,longNearBy).get
    dao.createQuest(latNearBy,longNotNearBy).get
    val result= List(dao.getQuests(userID).get)
    dao.getListOfQuestsNerby(long,lat,111000f) should equal (result)
  }
}
