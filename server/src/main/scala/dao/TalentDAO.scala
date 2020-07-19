package dao

import javax.inject.Inject
import scalikejdbc._

class TalentDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def getTalents(): Seq[TalentRow] = {
    withReadOnlySession(pool) { implicit session =>
      sql"select id, name, skill_unlock, next_talents, activity_id, distance, speed, time, time_in_day from public.talents"
        .map(readRow)
        .list()
        .apply()
    }
  }

  def getTalents(talents: Seq[Int]): Seq[TalentRow] = {
    withReadOnlySession(pool) { implicit session =>
      sql"select id, name, skill_unlock, next_talents, activity_id, distance, speed, time, time_in_day from public.talents where id = any (${talents})"
        .map(readRow)
        .list()
        .apply()
    }
  }

  private def readRow(row: WrappedResultSet): TalentRow = {
    TalentRow(
      id = row.int("id"),
      name = row.string("name"),
      skillUnlock = row.int("skill_unlock"),
      nextTalents = row.array("next_talents").getArray().asInstanceOf[Array[Integer]].map(_.intValue()).toSeq,
      activityId = row.int("activity_id"),
      distance = row.intOpt("distance"),
      speed = row.intOpt("speed"),
      time = row.intOpt("time"),
      timeInDay = row.intOpt("time_in_day")
    )
  }

}

case class TalentRow(id: Int, name: String, skillUnlock: Int, nextTalents: Seq[Int], activityId: Int, distance: Option[Int], speed: Option[Int], time: Option[Int], timeInDay: Option[Int])

case class TalentTreeNode(
  id: Int,
  name: String,
  skillUnlock: Int,
  next: Seq[TalentTreeNode],
  activityId: Int,
  distance: Option[Int],
  speed: Option[Int],
  time: Option[Int],
  timeInDay: Option[Int]
)



object TalentTree {
  def fromRows(rows: Seq[TalentRow]): Seq[TalentTreeNode] = {
    val roots = rows.filterNot(r => rows.flatMap(_.nextTalents).contains(r.id))
    roots.map(fill(_, rows))
  }

  def fill(row: TalentRow, rows: Seq[TalentRow]): TalentTreeNode = {
    if (row.nextTalents.isEmpty) {
      createNode(row)
    } else {
      val children = row.nextTalents.map{ id =>
        rows.find(_.id == id).get
      }.map(fill(_, rows))
      createNode(row).copy(next = children)
    }
  }

  def createNode(row: TalentRow): TalentTreeNode = TalentTreeNode(
    id = row.id,
    name = row.name,
    skillUnlock = row.skillUnlock,
    next = Seq(),
    activityId = row.activityId,
    distance = row.distance,
    speed = row.speed,
    time = row.time,
    timeInDay = row.timeInDay
  )
}