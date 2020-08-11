package background.character

import com.github.dmarcous.s2utils.geo.GeographyUtilities
import dao.DAOPosition
import org.joda.time.Period

object RouteUtil {
  def getMaxSpeedForDistance(distanceInMeters: Double, positions: Seq[DAOPosition]): Double = {
    if (positions.isEmpty) {
      0.0
    } else {
      positions.indices.map { startIndex =>
        averageSpeed(forDistance(distanceInMeters, positions.view(startIndex, positions.length)))
      }.max
    }
  }

  def averageSpeed(positions: Iterable[DAOPosition]): Double = {
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

  def forDistance(distanceInMeters: Double, positions: Iterable[DAOPosition]): Iterable[DAOPosition] = {
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
}
