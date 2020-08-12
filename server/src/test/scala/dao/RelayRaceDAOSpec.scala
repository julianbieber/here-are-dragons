package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.GeneratorUtil._
import testUtil.SQLSpec._

class RelayRaceDAOSpec extends AnyFlatSpec with Matchers {

  "ActivityDAO" must "record start and sp of activities" in withPool { pool =>
    val dao = new RelayRaceDAO(pool)
    val group = oneRandom(genString)
    val users = oneRandom(genSeq(genPosInt)) ++ Seq(oneRandom(genPosInt))
    dao.getNotProcessedRelayRaces() must be(Seq())

    val startDate = dao.start(group, users, "RUNNING")

    dao.stop(users.head)

    val races = dao.getNotProcessedRelayRaces()
    races must have size 1
    races.head.users must contain theSameElementsAs(users)

    dao.setProcessed(Seq(group -> startDate))

    dao.getNotProcessedRelayRaces() must have size 0

  }

}
