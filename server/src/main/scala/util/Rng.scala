package util

import scala.util.Random

object Rng {
  def between(start: Int, end: Int): Int = {
    start + Random.nextInt( (end - start) + 1 )
  }
}
