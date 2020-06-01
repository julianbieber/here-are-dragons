package model
import dao.DAOQuest

object Quest {
  case class QuestsResponse(quests:Seq[DAOQuest])


}

