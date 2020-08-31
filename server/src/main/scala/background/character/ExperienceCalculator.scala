package background.character

import org.joda.time.Period

object ExperienceCalculator {
  def forActivity(activityId: Int, duration: Period, kmPerH: Double): Long = {
    (duration.getMinutes.toDouble * activityToMultiplier(activityId).find{ case ((min, max), _) => min >= kmPerH && max < kmPerH}.map(_._2).getOrElse(0.0)).toLong
  }

  def forCalisthenics(count: Int, calisthenicsType: Int): Long = {
    if (calisthenicsType == 3) {
      count * 15
    } else if (calisthenicsType == 4) {
      count * 30
    } else {
      0
    }
  }

  private val activityToMultiplier = Map(
    1 -> Map(
      (1, 8) -> 1.0,
      (8, 12) -> 1.25,
      (12, Int.MaxValue) -> 1.5
    ), // Running
    2 -> Map(
      (1, 15) -> 0.5,
      (15, 25) -> 0.75,
      (25, Int.MaxValue) -> 1.0
    )
  )


}
