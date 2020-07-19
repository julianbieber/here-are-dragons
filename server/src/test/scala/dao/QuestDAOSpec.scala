package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import testUtil.SQLSpec._

import scala.util.Random.{nextFloat, nextLong};


class QuestDAOSpec extends AnyFlatSpec with Matchers {

  "fillDatabaseFromPoIs" must "fill the Database public.quest with quests" in withPool { pool =>
    val dao = new QuestDAO(pool)
    val DAOP= new PoIDAO(pool)

    val poiId = nextLong()
    val long = nextFloat()
    val lat = nextFloat()

    val priority = nextFloat()
    val tags = None

    DAOP.createPoI(poiId, long,lat, priority, tags)

    val poiId1 = nextLong()
    val priority1 = nextFloat()
    val tags1 = None

    DAOP.createPoI(poiId1, long,lat, priority1, tags1)

    val p = DAOP.getPoIs(long,lat)
    dao.fillDatabaseFromPoIs(p,0)

    val computedResult = dao.getFromDatabase().length


    computedResult should be (2)
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

    val p = DAOP.getPoIs(long, lat)
    dao.fillDatabaseFromPoIs(p,0)

    val computedResult = dao.checkIfActive(poiId,0).get
    computedResult should be (false)

    dao.makeActive(poiId,0)
    val computedResult1 = dao.checkIfActive(poiId,0).get
    computedResult1 should be (true)

    dao.makeUnActive(poiId,0)
    val computedResult2 = dao.checkIfActive(poiId,0).get
    computedResult2 should be (false)

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

    val p = DAOP.getPoIs(long,lat)
    dao.fillDatabaseFromPoIs(p,0)

    dao.makeUnActive(poiId,0)

    val computedResult = dao.getListOfActivataibleQuestsNerby(long, lat, 999f,0)
    val expectedResult = new DAOQuest(poiId,long,lat,priority,tags)

    computedResult should be (List(expectedResult))
  }

  "getOldest" must "get the QuestID of the oldest Quest" in withPool { pool =>
    val dao = new QuestDAO(pool)
    val DAOP= new PoIDAO(pool)

    val poiId = nextLong()
    val long = nextFloat()
    val lat = nextFloat()
    val priority = nextFloat()
    val tags = None

    DAOP.createPoI(poiId, long,lat, priority, tags)

    val p = DAOP.getPoIs(long,lat)
    dao.fillDatabaseFromPoIs(p,0)

    val poiId1 = nextLong()
    val priority1 = nextFloat()

    DAOP.createPoI(poiId1, long,lat, priority1, tags)

    val p1 = DAOP.getPoIs(long,lat)
    dao.fillDatabaseFromPoIs(p1,0)

    dao.getOldest(0)

    val computedResult = dao.getOldest(0).get
    val expectedResult = poiId

    computedResult should be (expectedResult)
  }
}

