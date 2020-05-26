package background

import dao.{ActivityDAO, ExperienceDAO}
import javax.inject.Inject

class Experience @Inject() (activityDAO: ActivityDAO, experienceDAO: ExperienceDAO) extends Background {
  private def calculateMissingExperiences(): Seq[ExperienceValue] = {
    val activities = activityDAO.getNotProcessedActivities()
    val experiencesAndProcessedActivities = activities.groupBy(_.user).flatMap{ case (user, userActivities) =>
      userActivities.groupBy(_.activity).flatMap { case (activityType, typeGroup) =>
        val starts = typeGroup.filter(_.start).sortBy(_.timestamp.getMillis)
        val stops = typeGroup.filter(_.stop).sortBy(_.timestamp.getMillis)

        starts.zip(stops).map{ case (start, stop) =>
          (ExperienceValue(user, activityType, stop.timestamp.getMillis - start.timestamp.getMillis), start, stop)
        }
      }
    }
    val processed = experiencesAndProcessedActivities.flatMap(a => Seq(a._2, a._3))
    val experiences = experiencesAndProcessedActivities.map(_._1)
    activityDAO.setProcessed(processed.toSeq)
    experiences.toSeq
  }

  override def run(): Unit = {
    val experiences = calculateMissingExperiences()
    experienceDAO.addExperiences(experiences)
  }

}

case class ExperienceValue(user: Int, experienceType: Int, amount: Long)
