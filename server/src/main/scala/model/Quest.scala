package model
import dao.DAOQuest

object Quest {
  case class QuestsResponse(quests:Seq[DAOQuest])
  case class nextPosition(latlong:Array[Float])
  case class Difficulty(difficulty:Int)
}

