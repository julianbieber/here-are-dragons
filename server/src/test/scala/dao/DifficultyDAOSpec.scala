package dao

import model.Dungeon.SkillBar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import testUtil.GeneratorUtil._
import testUtil.SQLSpec._

import scala.util.Random.{nextFloat, nextLong}

class DifficultyDAOSpec extends AnyFlatSpec with Matchers {
  "setDifficulty" must "fillDatabase with difficulty" in withPool{ pool =>
    val dao = new DifficultyDAO(pool)

    dao.setDifficulty(0,100,false)

    val computedResult : List[Int] = dao.getDifficulty(0)
    val expectedResult = List(100)

    expectedResult should be (computedResult)
  }


}
