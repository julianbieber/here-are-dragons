package background.character

import background.Background
import background.character.RouteUtil.{averageSpeed, forDistance, getMaxSpeedForDistance}
import com.github.dmarcous.s2utils.geo.GeographyUtilities
import dao._
import javax.inject.Inject
import org.joda.time.Period

class RelayRaces @Inject()(activityDAO: ActivityDAO, positionDAO: PositionDAO, relayRaceDAO: RelayRaceDAO, groupTalentDAO: GroupTalentDAO, groupTalentUnlockDAO: GroupTalentUnlockDAO) extends Background {
  override def run(): Unit = {
    val races = relayRaceDAO.getNotProcessedRelayRaces()

    races.map(r => r -> findRoute(r)).map{case (race, route) =>
      groupTalentUnlockDAO.getUnlocks(race.users).flatMap(_.currentlyUnlocking).map { unlocking =>
        val unlockingTalent = groupTalentDAO.getTalents(Seq(unlocking)).head
        val unlocked = unlockingTalent match {
          case GroupTalentRow(_, _, _, _, _, _, Some(distance), None, None) => forDistance(distance, route).nonEmpty
          case GroupTalentRow(_, _, _, _, _, _, None, Some(speed), None) => averageSpeed(route) >= speed
          case GroupTalentRow(_, _, _, _, _, _, None, None, Some(time)) => new Period(route.head.timestamp, route.last.timestamp).getMinutes >= time
          case GroupTalentRow(_, _, _, _, _, _, Some(distance), Some(speed), None) =>getMaxSpeedForDistance(distance, route) >= speed
        }
        if (unlocked) {
          groupTalentUnlockDAO.unlock(race.users)
        }
      }
    }
  }


  def findRoute(race: RelayRaceRow): Seq[DAOPosition] = {
    val groupActivities = race.users.flatMap(activityDAO.getActivitiesBetween(_, race.activity, race.startTimestamp, race.endTimestamp.get))
    println(groupActivities)
    val user2Movement = groupActivities.map { activity =>
      activity.user -> positionDAO.getHistory(activity.user, activity.startTimestamp, activity.endTimestamp.get)
    }
      .groupBy(_._1)
      .mapValues(_.map(_._2)).mapValues(_.maxBy(_.length))
      .toSeq
      .sortBy(_._2.head.timestamp.getMillis)

    val (last, exceptLastRun) = user2Movement
      .sliding(2, 1)
      .foldLeft((0, Seq[DAOPosition]())) { 
        case ((start, acc), Seq((_, movement1), (_, movement2))) =>
          val (stop1, start2) = findOverlap(movement1, movement2).get
          (start2, acc ++ movement1.slice(start, stop1))
        case ((lastStart, acc), Seq((_, movement))) =>
          (movement.length - 1, acc ++ movement.slice(lastStart, movement.length))
      }
    if (user2Movement.length > 1) {
      exceptLastRun ++ user2Movement.last._2.slice(last, user2Movement.last._2.length)
    } else {
      exceptLastRun
    }
  }

  def findOverlap(seq1: Seq[DAOPosition], seq2: Seq[DAOPosition]): Option[(Int, Int)] = {
    seq1.zipWithIndex.flatMap { case (p1, i1) =>
      seq2.zipWithIndex.find { case (p2, _) =>
        GeographyUtilities.haversineDistance(p1.longitude, p1.latitude, p2.longitude, p2.latitude) <= 10
      }.map(i1 -> _._2)
    }.headOption
  }


}
