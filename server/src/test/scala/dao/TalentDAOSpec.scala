package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.GeneratorUtil._
import testUtil.SQLSpec._

class TalentDAOSpec extends AnyFlatSpec with Matchers {
  "TalentDAO" must "retrieve all talents" in withPool { pool =>
    val dao = new TalentDAO(pool)
    dao.getTalents() must have size 20
  }

}
