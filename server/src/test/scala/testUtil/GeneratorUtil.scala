package testUtil

import org.scalacheck.Gen

object GeneratorUtil {
  def oneRandom[A](gen: Gen[A]): A = gen.sample.get

  def genString: Gen[String] = Gen.asciiPrintableStr

}
