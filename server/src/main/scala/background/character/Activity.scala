package background.character

import background.Background
import com.github.dmarcous.s2utils.geo.GeographyUtilities
import dao._
import javax.inject.Inject
import org.joda.time.{Minutes, Period}
import util.TimeUtil

class Activity @Inject()(activityDAO: ActivityDAO, experienceDAO: ExperienceDAO, positionDAO: PositionDAO, talentUnlockDAO: TalentUnlockDAO, talentDAO: TalentDAO) extends Background {
  override def run(): Unit = {
    val experiences = calculateMissingExperiences()
    experienceDAO.addExperiences(experiences)
  }

  private def calculateMissingExperiences(): Seq[ExperienceValue] = {
    val activities = activityDAO.getNotProcessedActivities()
    activityDAO.setProcessed(activities)
    checkTimeInDayUnlocks()
    activities.flatMap { activity =>
      val positions = positionDAO.getHistory(activity.user, activity.startTimestamp, activity.endTimestamp.get).sortBy(_.timestamp.getMillis)
      if (positions.nonEmpty) {
        talentUnlockDAO
          .getUnlocks(activity.user)
          .flatMap(_.currentlyUnlocking)
          .toSeq
          .flatMap(i => talentDAO.getTalents(Seq(i)))
          .headOption
          .foreach { currentUnlock =>
            if (isFullfilledBySingleActivity(currentUnlock, positions)) {
              talentUnlockDAO.unlock(activity.user)
            }
          }


        Option(ExperienceValue(activity.user, activity.activity, ExperienceCalculator.forActivity(activity.activity, new Period(positions.head.timestamp, positions.last.timestamp), averageSpeed(positions))))
      } else {
        None
      }
    }
  }

  private def isFullfilledBySingleActivity(talentRow: TalentRow, positions: Seq[DAOPosition]): Boolean = {
    talentRow match {
      case TalentRow(_, _, _, _, _, Some(distance), None, None, _) => forDistance(distance, positions).nonEmpty
      case TalentRow(_, _, _, _, _, None, Some(speed), None, _) => averageSpeed(positions) >= speed
      case TalentRow(_, _, _, _, _, None, None, Some(time), _) => new Period(positions.head.timestamp, positions.last.timestamp).getMinutes >= time
      case TalentRow(_, _, _, _, _, Some(distance), Some(speed), None, _) =>getMaxSpeedForDistance(distance, positions) >= speed
    }
  }

  private def getMaxSpeedForDistance(distanceInMeters: Double, positions: Seq[DAOPosition]): Double = {
    if (positions.isEmpty) {
      0.0
    } else {
      positions.indices.map { startIndex =>
        averageSpeed(forDistance(distanceInMeters, positions.view(startIndex, positions.length)))
      }.max
    }
  }

  private def averageSpeed(positions: Iterable[DAOPosition]): Double = {
    if (positions.isEmpty) {
      0.0
    } else {
      val duration = new Period(positions.head.timestamp, positions.last.timestamp)
      val fullDistance = positions.tail.foldLeft((0.0, positions.head)) { case ((distance, point), nextPoint) =>
        distance + GeographyUtilities.haversineDistance(point.longitude, point.latitude, nextPoint.longitude, nextPoint.latitude) -> nextPoint
      }
      (fullDistance._1 / 1000.0) / (duration.getSeconds / 3600)
    }
  }

  private def forDistance(distanceInMeters: Double, positions: Iterable[DAOPosition]): Iterable[DAOPosition] = {
    var distance = 0.0
    var point = positions.head
    val positionsForDistance = positions.tail.takeWhile { nextPoint =>
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

  private def checkTimeInDayUnlocks(): Unit = {
    val user2Unlock = talentUnlockDAO.getAllUnlocks().collect{ case UnlockRow(userId, Some(currentlyUnlocking), _) =>
      userId -> currentlyUnlocking
    }
    val talents = talentDAO.getTalents(user2Unlock.map(_._2))
    val user2Talent = user2Unlock.map{ case (user, talentId) =>
      user -> talents.find(_.id == talentId).get
    }

    val now = TimeUtil.now
    user2Talent.collect{ case (user, TalentRow(_, _, _, _, activityId, _, _, _, Some(timeInDay))) =>
      val activeTime = activityDAO.getActivitiesBetween(user, activityId, now.minusDays(1), now).map{ activity =>
        Minutes.minutesBetween(activity.startTimestamp, activity.endTimestamp.get).getMinutes
      }.sum

      if (activeTime > timeInDay) {
        talentUnlockDAO.unlock(user)
      }
    }
  }

}

case class ExperienceValue(user: Int, experienceType: Int, amount: Long)
