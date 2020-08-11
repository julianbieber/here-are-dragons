package dao

import javax.inject.Inject
import model.Character.GroupTalent
import scalikejdbc._

class GroupTalentDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def getTalents(): Seq[GroupTalentRow] = {
    withReadOnlySession(pool) { implicit session =>
      sql"select id, name, skill_unlock, next_talents, activity_id, distance, speed, time from public.group_talents"
        .map(readRow)
        .list()
        .apply()
    }
  }

  def getTalents(talents: Seq[Int]): Seq[GroupTalentRow] = {
    if (talents.nonEmpty) {
      withReadOnlySession(pool) { implicit session =>
        sql"select id, name, skill_unlock, next_talents, activity_id, distance, speed, time from public.group_talents where id = any (${talents})"
          .map(readRow)
          .list()
          .apply()
      }
    } else {
      Seq()
    }
  }

  private def readRow(row: WrappedResultSet): GroupTalentRow = {
    GroupTalentRow(
      id = row.int("id"),
      users = row.array("users").getArray.asInstanceOf[Array[Integer]].toSeq.map(_.intValue()),
      name = row.string("name"),
      skillUnlock = row.int("skill_unlock"),
      nextTalents = row.array("next_talents").getArray().asInstanceOf[Array[Integer]].map(_.intValue()).toSeq,
      activityId = row.int("activity_id"),
      distance = row.intOpt("distance"),
      speed = row.intOpt("speed"),
      time = row.intOpt("time")
    )
  }

}

case class GroupTalentRow(id: Int, users: Seq[Int], name: String, skillUnlock: Int, nextTalents: Seq[Int], activityId: Int, distance: Option[Int], speed: Option[Int], time: Option[Int])

object GroupTalentTree {
  def findRoots(rows: Seq[GroupTalentRow]): Seq[GroupTalentRow] = rows.filterNot(r => rows.flatMap(_.nextTalents).contains(r.id))

  def createTalent(row: GroupTalentRow): GroupTalent = GroupTalent(id = row.id,
    name = row.name,
    skillUnlock = row.skillUnlock,
    activityId = row.activityId,
    distance = row.distance,
    speed = row.speed,
    time = row.time
  )
}