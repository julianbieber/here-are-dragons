package background.character

import dao.{ActivityDAO, GroupTalentDAO, GroupTalentUnlockDAO, PositionDAO, RelayRaceDAO, UserDAO}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.SQLSpec._

class RelayRacesSpec extends AnyFlatSpec with Matchers with MockFactory  {
  "RelayRaces" must "accumulate the route for a relay race" in withPool{ pool =>
    val userDAO = new UserDAO(pool)
    userDAO.createUser("1", "1")
    userDAO.createUser("2", "1")
    userDAO.createUser("3", "1")

    val activityDAO = new ActivityDAO(pool)
    val positionDAO = new PositionDAO(pool)
    val relayRaceDAO = new RelayRaceDAO(pool)
    val groupTalentDAO = new GroupTalentDAO(pool)
    val groupTalentUnlockDAO = new GroupTalentUnlockDAO(pool)

    val group = "group"
    val users = Seq(1, 2, 3)
    val raceStart = relayRaceDAO.start(group, users, "RUNNING")
    Thread.sleep(1)
    val activity1Start = activityDAO.startActivity(1, "RUNNING")
    (0 to 10).foreach{ lat =>
      Thread.sleep(1)
      positionDAO.setPosition(1, 0, lat.toFloat / 10)
    }
    val activity2Start = activityDAO.startActivity(2, "RUNNING")

    (5 to 10).foreach{ lat =>
      Thread.sleep(1)
      positionDAO.setPosition(2, 0, lat.toFloat / 10)
    }
    activityDAO.stopActivity(1, activity1Start)
    (11 to 15).foreach{ lat =>
      Thread.sleep(1)
      positionDAO.setPosition(2, 0, lat.toFloat / 10)
    }
    activityDAO.stopActivity(2, activity2Start)
    Thread.sleep(1)
    relayRaceDAO.stop(2)

    val race = relayRaceDAO.getNotProcessedRelayRaces().head

    val uut = new RelayRaces(activityDAO, positionDAO, relayRaceDAO, groupTalentDAO, groupTalentUnlockDAO)
    val route = uut.findRoute(race)
    route.map(_.latitude) must contain theSameElementsAs((0 to 15).map(_.toFloat / 10))
  }

}
