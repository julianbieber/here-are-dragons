package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.SQLSpec._

class GroupTalentDAOSpec extends AnyFlatSpec with Matchers {

  "GroupTalentDAO" must "retrieve all talents" in withPool { pool =>
    val dao = new GroupTalentDAO(pool)
    dao.getTalents() must have size 0
  }
}
