package background.character

import background.Background
import background.character.RouteUtil.{averageSpeed, forDistance, getMaxSpeedForDistance}
import dao._
import javax.inject.Inject
import org.joda.time.{Minutes, Period}
import util.TimeUtil

class Activity @Inject()(activityDAO: ActivityDAO, experienceDAO: ExperienceDAO, positionDAO: PositionDAO, talentUnlockDAO: TalentUnlockDAO, talentDAO: TalentDAO, calisthenicsDAO: CalisthenicsDAO) extends Background {
  override def run(): Unit = {
    checkTimeInDayUnlocks()
    val experiences = calculateMissingExperiencesSorcererRanger() ++ calculateMissingExperienceWarrior()
    experienceDAO.addExperiences(experiences)
  }

  private def calculateMissingExperiencesSorcererRanger(): Seq[ExperienceValue] = {
    val activities = activityDAO.getNotProcessedActivities()
    activityDAO.setProcessed(activities)
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
            if (activity.activity == currentUnlock.activityId && isFullfilledBySingleActivity(currentUnlock, positions)) {
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
      case TalentRow(_, _, _, _, _, Some(distance), Some(speed), None, _) => getMaxSpeedForDistance(distance, positions) >= speed
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
    user2Talent.collect{
      case (user, TalentRow(_, _, _, _, activityId, _, _, _, Some(timeInDay))) if Seq(1,2).contains(activityId) =>
        val activeTime = activityDAO.getActivitiesBetween(user, activityId, now.minusDays(1), now).map{ activity =>
          Minutes.minutesBetween(activity.startTimestamp, activity.endTimestamp.get).getMinutes
        }.sum

        if (activeTime > timeInDay) {
          talentUnlockDAO.unlock(user)
        }
      case (user, TalentRow(_, _, _, _, activityId, _, _, _, Some(timeInDay))) if Seq(3,4).contains(activityId) =>
        val relevant = calisthenicsDAO.getBetween(user, now.minusDays(1), now).filter(_.calisthenicsType == activityId)
        if (relevant.length * 3 == timeInDay) {
          talentUnlockDAO.unlock(user)
        }

    }
  }


  private def calculateMissingExperienceWarrior(): Seq[ExperienceValue] = {
    val calisthenics = calisthenicsDAO.getNotProcessed()
    calisthenicsDAO.setProcessed(calisthenics.map(c => c.userId -> c.timestamp))
    calisthenics.groupBy(_.userId).map{ case (user, calisthenics) =>

      val (push, pull) = calisthenics.partition(_.calisthenicsType == 3)
      val experience = ExperienceCalculator.forCalisthenics(push.length, 3) + ExperienceCalculator.forCalisthenics(pull.length, 4)
      ExperienceValue(user, 3, experience)
    }.toSeq
  }
}

case class ExperienceValue(user: Int, experienceType: Int, amount: Long)
