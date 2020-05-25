package background

import dao.{ActivityDAO, PositionDAO}
import javax.inject.Inject

class Experience @Inject() (activityDAO: ActivityDAO, positionDAO: PositionDAO) {
  def calculateMissingExperiences(): Seq[ExperienceValue] = {
    Seq()
  }

}

case class ExperienceValue(user: Int, experienceType: String, amount: Long)
