package dao

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import testUtil.SQLSpec._
import scala.util.Random.{nextFloat, nextLong};

class PoIDAOSpec extends AnyFlatSpec with Matchers {
  "createPoI" must "create a POI in Database and return the PoIId" in withPool{ pool =>
    val dao = new PoIDAO(pool)

    val poiId = nextLong()
    val long = nextFloat()
    val lat = nextFloat()
    val priority = nextFloat()
    val tags = None

    val computedResult : Option[Long] = dao.createPoI(poiId, long,lat, priority, tags)
    val expectedResult = Option(poiId)

    expectedResult should be (computedResult)
  }

  "createPoI" must "get a List pof PoIs" in withPool{ pool =>
    val dao = new PoIDAO(pool)

    val poiId = nextLong()
    val long = nextFloat()
    val lat = nextFloat()
    val priority = nextFloat()
    val tags = None

    dao.createPoI(poiId, long,lat, priority, tags)

    val computedResult : List[DAOPoI] = dao.getPoIs(long,lat)
    val poi = DAOPoI(poiId,long,lat,priority,tags)
    val expectedResult = List(poi)

    expectedResult should be (computedResult)
  }
  "createPoIsForOnePosition" must "get a List pof PoIs close to given Position" in withPool{ pool =>
    val dao = new PoIDAO(pool)

    val poiId = nextLong()
    val long = nextFloat()
    val lat = nextFloat()
    val priority = nextFloat()
    val tags = None

    dao.createPoI(poiId, long,lat, priority, tags)

    val poiId1 = nextLong()
    val long1 = long+0.02f
    val lat1 = lat +0.002f
    val priority1 = nextFloat()
    val tags1 = None

    dao.createPoI(poiId1, long1,lat1, priority1, tags1)

    val poiId2 = nextLong()
    val long2 = long+3f
    val lat2 = lat +0.002f
    val priority2 = nextFloat()
    val tags2 = None

    dao.createPoI(poiId2, long2,lat2, priority2, tags2)

    val computedResult : Integer = dao.getPoIs(long,lat).length

    2 should be (computedResult)
  }
}
