package model

object Dungeon {
  case class AvailableDungeons(ids: Seq[Int])
  case class OpenRequest(questId: Int)
  case class DungeonResponse(
    dungeonId: Int,
    units: Seq[UnitResponse],
    myTurn: Boolean,
    ap: Int
  )
  case class UnitResponse (
    tyype: String,
    userId: Option[Int],
    health: Option[Int],
    prefabId: Option[Int]
  )
}
