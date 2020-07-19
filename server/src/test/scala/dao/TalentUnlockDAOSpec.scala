package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.GeneratorUtil._
import testUtil.SQLSpec._

class TalentUnlockDAOSpec extends AnyFlatSpec with Matchers {

  "TalntUnlockDAO" must "retrieve all talents" in withPool { pool =>
    val dao = new TalentUnlockDAO(pool)

    dao.getUnlocks(1) must be(empty)

    dao.startUnlocking(1, 1)

    dao.getUnlocks(1).get must be(UnlockRow(1, Option(1), Seq()))

    dao.unlock(1)

    dao.getUnlocks(1).get must be(UnlockRow(1, None, Seq(1)))
  }

}
