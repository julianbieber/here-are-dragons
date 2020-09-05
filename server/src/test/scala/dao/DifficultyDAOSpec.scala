package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import testUtil.SQLSpec._

import scala.collection.mutable.ListBuffer


class DifficultyDAOSpec extends AnyFlatSpec with Matchers {
  "DifficultyDAO" must "fillDatabase with difficulty" in withPool{ pool =>
    val dao = new DifficultyDAO(pool)

    dao.setDifficulty(0,100,false, ListBuffer(1, 2))

    val computedResult = dao.getAvailableDifficulties(0)

    computedResult should have size 1
    computedResult.head.difficulty should be(100)
    computedResult.head.groupMembers should contain theSameElementsAs Seq(1,2)

    dao.getAvailableDifficulties(1) should contain theSameElementsAs(computedResult)
    dao.getAvailableDifficulties(2) should contain theSameElementsAs(computedResult)
  }

  it must "not retrieve difficulties when the dungeon has already been opened" in withPool { pool =>
    val dao = new DifficultyDAO(pool)

    dao.setDifficulty(0,100,false, ListBuffer(1, 2))

    dao.setDungeon(dao.getAvailableDifficulties(0).head.id)

    val computedResult =  dao.getAvailableDifficulties(0)
    computedResult should be(empty)

  }


}
