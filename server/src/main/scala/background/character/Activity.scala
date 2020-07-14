package background.character

import background.Background
import com.github.dmarcous.s2utils.geo.GeographyUtilities
import dao.{ActivityDAO, DAOPosition, ExperienceDAO, PositionDAO}
import javax.inject.Inject
import org.joda.time.Period

class Activity @Inject() (activityDAO: ActivityDAO, experienceDAO: ExperienceDAO, positionDAO: PositionDAO) extends Background {
  private def calculateMissingExperiences(): Seq[ExperienceValue] = {
    val activities = activityDAO.getNotProcessedActivities()
    activityDAO.setProcessed(activities)
    activities.flatMap{ activity =>
      val positions = positionDAO.getHistory(activity.user, activity.startTimestamp, activity.endTimestamp.get).sortBy(_.timestamp.getMillis)
      if (positions.nonEmpty) {
        Option( ExperienceValue(activity.user, activity.activity, ExperienceCalculator.forActivity(activity.activity, new Period(positions.head.timestamp, positions.last.timestamp), averageSpeed(positions))))
      } else {
        None
      }
    }
  }

  private def averageSpeed(positions: Iterable[DAOPosition]): Double = {
    if (positions.isEmpty) {
      0.0
    } else {
      val duration = new Period(positions.head.timestamp, positions.last.timestamp)
      val fullDistance = positions.tail.foldLeft((0.0, positions.head)){ case ((distance, point), nextPoint) =>
        distance + GeographyUtilities.haversineDistance(point.longitude, point.latitude, nextPoint.longitude, nextPoint.latitude) -> nextPoint
      }
      (fullDistance._1 / 1000.0) / (duration.getSeconds / 3600)
    }
  }

  private def getMaxSpeedForDistance(distanceInMeters: Double, positions: Seq[DAOPosition]): Double = {
    if (positions.isEmpty) {
      0.0
    } else {
      positions.indices.map{ startIndex =>
        averageSpeed(forDistance(distanceInMeters, positions.view(startIndex, positions.length)))
      }.max
    }
  }

  private def forDistance(distanceInMeters: Double, positions: Iterable[DAOPosition]): Iterable[DAOPosition] = {
    var distance = 0.0
    var point = positions.head
    val positionsForDistance = positions.tail.takeWhile{ nextPoint =>
      distance += GeographyUtilities.haversineDistance(point.longitude, point.latitude, nextPoint.longitude, nextPoint.latitude)
      point = nextPoint
      distance < distanceInMeters
    }
    if (distance < distanceInMeters) {
      Seq()
    } else {
      positionsForDistance
    }
  }

  override def run(): Unit = {
    val experiences = calculateMissingExperiences()
    experienceDAO.addExperiences(experiences)
  }

}

case class ExperienceValue(user: Int, experienceType: Int, amount: Long)
