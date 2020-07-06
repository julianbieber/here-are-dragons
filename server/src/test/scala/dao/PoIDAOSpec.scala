package dao

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import testUtil.SQLSpec._
import scala.util.Random.{nextFloat, nextLong};

class PoIDAOSpec extends AnyFlatSpec with Matchers {
  "createPoIfromAPI" must "create a POI in Database and return the PoIId" in withPool{ pool =>
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

  "createPoIFromAPI" must "get a List pof PoIs" in withPool{ pool =>
    val dao = new PoIDAO(pool)

    val poiId = nextLong()
    val long = nextFloat()
    val lat = nextFloat()
    val priority = nextFloat()
    val tags = None

    dao.createPoI(poiId, long,lat, priority, tags)

    val computedResult : List[DAOPoI] = dao.getPoIs()
    val poi = DAOPoI(poiId,long,lat,priority,tags)
    val expectedResult = List(poi)

    expectedResult should be (computedResult)
  }
}
