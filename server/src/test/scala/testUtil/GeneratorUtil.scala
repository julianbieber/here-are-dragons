package testUtil

import org.scalacheck.Gen

object GeneratorUtil {
  def oneRandom[A](gen: Gen[A]): A = gen.sample.get

  def genString: Gen[String] = Gen.asciiPrintableStr

  def genFloat: Gen[Float] = Gen.chooseNum[Float](-1000.0f, 1000.0f)

  def genPosInt: Gen[Int] = Gen.posNum[Int]

  def genSeq[A](gen: Gen[A]): Gen[Seq[A]] = Gen.listOf(gen)

}
