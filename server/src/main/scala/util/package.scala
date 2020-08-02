import scala.util.Random

package object util {
  implicit class ExtendedSeq[A](seq: Seq[A]) {
    def without[B, C](bs: Seq[B], a2c: A => C, b2c: B => C): Seq[A] = {
      val cs = bs.map(b2c).toSet
      seq.filterNot(a => cs.contains(a2c(a)))
    }

    def shuffle: Seq[A] = {
      Random.shuffle(seq)
    }

    def randomOne: A = seq(Rng.between(0, seq.length - 1))
  }

}
