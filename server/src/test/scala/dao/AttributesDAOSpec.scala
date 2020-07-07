package dao

import model.Character.Attributes
import org.scalacheck.Gen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.SQLSpec._
import testUtil.GeneratorUtil._

class AttributesDAOSpec extends  AnyFlatSpec with Matchers {

  def genAttributes: Gen[Attributes] = for {
    strength <- genSmallPosInt
    constitution <- genSmallPosInt
    spellPower <- genSmallPosInt
    willPower <- genSmallPosInt
    dexterity <- genSmallPosInt
    evasion <- genSmallPosInt
  } yield {
    Attributes(
      strength,
      constitution,
      spellPower,
      willPower,
      dexterity,
      evasion
    )
  }

  "AttributesDAO" should "store and retrieve player attributes" in withPool{ pool =>
    val dao = new AttributesDAO(pool)
    val attributes = oneRandom(genAttributes)
    val max = attributes.strength + attributes.constitution + attributes.spellPower + attributes.willpower + attributes.dexterity + attributes.evasion

    dao.storeAttributes(1, attributes, attributes, max) must be('defined)
    dao.readAttributes(1).get must be(AttributesTable(1, attributes, attributes, max))
  }

}
