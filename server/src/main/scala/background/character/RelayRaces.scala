package background.character

import background.Background
import com.github.dmarcous.s2utils.geo.GeographyUtilities
import dao._
import javax.inject.Inject

class RelayRaces @Inject()(activityDAO: ActivityDAO, positionDAO: PositionDAO, relayRaceDAO: RelayRaceDAO) extends Background {
  override def run(): Unit = {
    val races = relayRaceDAO.getNotProcessedRelayRaces()

    races.map(findRoute)
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
