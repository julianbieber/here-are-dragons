package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import testUtil.SQLSpec._

import scala.util.Random.{nextFloat, nextInt};


class QuestDAOSpec extends AnyFlatSpec with Matchers {
  "QuestDAO" must "create and delete a Quest" in withPool { pool =>
    val dao = new QuestDAO(pool)
    val long = nextFloat()
    val lat = nextFloat()
    val userID : Long= dao.createQuestFromAPI("1",lat, long).get
    userID should be >=(0)

    dao.deleteQuest(userID)
    dao.getQuests(userID) should be (None)
  }
  it must "return a List of Quests nearby" in withPool { pool =>
    val dao = new QuestDAO(pool)
    val long = nextFloat()
    val longNearBy = long + 0.511111f
    val longNotNearBy = long - 10.511111f
    val lat = nextFloat()
    val id = "1"
    val latNearBy = lat + 0.5111111f
    val userID = dao.createQuestFromAPI(id,latNearBy, longNearBy).get
    val result = List(dao.getQuests(userID).get)
    dao.getListOfQuestsNerby(long, lat, 111000f) should equal(result)
  }
  it must "set the Value of SQL to true" in withPool { pool =>
    val dao = new QuestDAO(pool)
    val long = nextFloat()
    val lat = nextFloat()
    val id = "1"
    val userID = dao.createQuestFromAPI(id, lat, long).get
    dao.setQuestToErledigt(userID)
    dao.getStatusErledigt(userID).get should equal(true)
  }
}

