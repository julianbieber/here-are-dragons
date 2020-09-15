package background.character

import org.joda.time.Period

object ExperienceCalculator {
  def forActivity(activityId: Int, duration: Period, kmPerH: Double): Long = {
    (duration.toStandardMinutes.getMinutes.toDouble * activityToMultiplier(activityId).find{ case ((min, max), _) => min <= kmPerH && max > kmPerH}.map(_._2).getOrElse(0)).toLong
  }

  def forCalisthenics(count: Int, calisthenicsType: Int): Long = {
    if (calisthenicsType == 3) {
      count * 3 * 4
    } else if (calisthenicsType == 4) {
      count * 3 * 6
    } else {
      0
    }
  }

  private val activityToMultiplier = Map(
    1 -> Map(
      (1, 9) -> 9,
      (9, 11) -> 11,
      (11, 13) -> 13,
      (13, 15) -> 15,
      (15, Int.MaxValue) -> 15
    ), // Running
    2 -> Map(
      (1, 10) -> 5,
      (10, 15) -> 6,
      (15, 20) -> 7,
      (20, 25) -> 9,
      (25, Int.MaxValue) -> 10
    )
  )


}
