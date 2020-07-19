package dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.GeneratorUtil._
import testUtil.SQLSpec._

class TalentDAOSpec extends AnyFlatSpec with Matchers {
  "TalentDAO" must "build a tree" in {
    val rows = Seq(
      exampleRow(1, Seq(2, 3)),
      exampleRow(2, Seq(4)),
      exampleRow(3, Seq(5, 6)),
      exampleRow(4, Seq()),
      exampleRow(5, Seq()),
      exampleRow(6, Seq()),
      exampleRow(7, Seq())
    )

    val expectedTrees = Seq(
      TalentTree.createNode(rows(0)).copy(next = Seq(
        TalentTree.createNode(rows(1)).copy(next = Seq(
          TalentTree.createNode(rows(3)) // 4
        )), // 2
        TalentTree.createNode(rows(2)).copy(next = Seq(
          TalentTree.createNode(rows(4)), // 5
          TalentTree.createNode(rows(5)) // 6
        )) // 3
      )), // 1

      TalentTree.createNode(rows(6)) // 7
    )

    TalentTree.fromRows(rows) must be(expectedTrees)
  }

  it must "retrieve all talents" in withPool { pool =>
    val dao = new TalentDAO(pool)
    dao.getTalents() must have size 5
  }

  private def exampleRow(id: Int, next: Seq[Int]): TalentRow = TalentRow(
    id,
    oneRandom(genString),
    oneRandom(genPosInt),
    next,
    oneRandom(genPosInt),
    oneRandom(genOption(genPosInt)),
    oneRandom(genOption(genPosInt)),
    oneRandom(genOption(genPosInt)),
    oneRandom(genOption(genPosInt))
  )
}
