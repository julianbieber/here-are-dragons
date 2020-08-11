package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.SQLSpec._

class GroupTalentUnlockDAOSpec extends AnyFlatSpec with Matchers {

  "GroupTalentUnlockDAO" must "retrieve all talents" in withPool { pool =>
    val dao = new GroupTalentUnlockDAO(pool)

    dao.getUnlocks(Seq(1, 2)) must be(empty)

    dao.startUnlocking(Seq(1, 2), 1)

    dao.getUnlocks(Seq(1, 2)).get must be(GroupUnlockRow(Seq(1, 2), Option(1), Seq()))

    dao.unlock(Seq(1, 2))

    dao.getUnlocks(Seq(1, 2)).get must be(GroupUnlockRow(Seq(1, 2), None, Seq(1)))
  }

}
