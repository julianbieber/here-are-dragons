package testUtil

import org.scalacheck.Gen

object GeneratorUtil {
  def oneRandom[A](gen: Gen[A]): A = gen.sample.get

  def genString: Gen[String] = Gen.asciiPrintableStr.map(_.take(2))

  def genFloat: Gen[Float] = Gen.chooseNum[Float](-1000.0f, 1000.0f)

  def genPosInt: Gen[Int] = Gen.chooseNum[Int](0, 1000)

  def genOption[A](g: Gen[A]): Gen[Option[A]] = Gen.option(g)

  def genSmallPosInt: Gen[Int] = Gen.chooseNum[Int](0, 10)

  def genSeq[A](gen: Gen[A]): Gen[Seq[A]] = Gen.listOf(gen)

}
