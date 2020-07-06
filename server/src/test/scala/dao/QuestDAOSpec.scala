package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import testUtil.SQLSpec._

import scala.util.Random.{nextFloat, nextLong};


class QuestDAOSpec extends AnyFlatSpec with Matchers {

  "fillDatabaseWithQuestsFromPoIs" must "fill the Database public.quest with quests" in withPool { pool =>
    val dao = new QuestDAO(pool)
    val DAOP= new PoIDAO(pool)

    val poiId = nextLong()
    val long = nextFloat()
    val lat = nextFloat()

    val priority = nextFloat()
    val tags = None

    DAOP.createPoI(poiId, long,lat, priority, tags)

    val poiId1 = nextLong()
    val long1 = nextFloat()
    val lat1 = nextFloat()
    val priority1 = nextFloat()
    val tags1 = None

    DAOP.createPoI(poiId1, long1,lat1, priority1, tags1)

    val p = DAOP.getPoIs()
    dao.fillDatabaseWithQuestsFromPoIs(p)

    val computedResult = dao.getQuestsFromDatabase()
    val expectedResult = List(DAOQuest(poiId,long,lat), DAOQuest(poiId1,long1,lat1))


    computedResult should be (expectedResult)
  }
  "Make Quest active/activalable" must "set a Quest from active to activalable and vice versa" in withPool { pool =>
    val dao = new QuestDAO(pool)
    val DAOP= new PoIDAO(pool)

    val poiId = nextLong()
    val long = nextFloat()
    val lat = nextFloat()
    val priority = nextFloat()
    val tags = None

    DAOP.createPoI(poiId, long,lat, priority, tags)

    val poiId1 = nextLong()
    val long1 = nextFloat()
    val lat1 = nextFloat()
    val priority1 = nextFloat()
    val tags1 = None

    DAOP.createPoI(poiId1, long1,lat1, priority1, tags1)

    val p = DAOP.getPoIs()
    dao.fillDatabaseWithQuestsFromPoIs(p)

    dao.makeQuestActive(poiId,0)

    val computedResult = dao.getActiveUsersForQuest(poiId).get
    val expectedResult = Seq(0).toArray

    computedResult should be (expectedResult)

    dao.makeQuestUnActive(poiId,0)
    val computedResult1 = dao.getActivatableUserForQuest(poiId).get

    val computedResult2 = dao.getActiveUsersForQuest(poiId).get
    val expectedResult1 : Array[Int] = Array()

    computedResult1 should be (expectedResult)
    computedResult2 should be (expectedResult1)
  }

  "getListOfActivataibleQuestsNerby" must "get List of activatablee quests" in withPool { pool =>
    val dao = new QuestDAO(pool)
    val DAOP= new PoIDAO(pool)

    val poiId = nextLong()
    val long = nextFloat()
    val lat = nextFloat()
    val priority = nextFloat()
    val tags = None

    DAOP.createPoI(poiId, long,lat, priority, tags)

    val p = DAOP.getPoIs()
    dao.fillDatabaseWithQuestsFromPoIs(p)
    println(p)

    dao.makeQuestUnActive(poiId,0)

    val computedResult = dao.getListOfActivataibleQuestsNerby(long, lat, 999f,0)
    val expectedResult = new DAOQuest(poiId,long,lat)

    computedResult should be (List(expectedResult))
  }
}

