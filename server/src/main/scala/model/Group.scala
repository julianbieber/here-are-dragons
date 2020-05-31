package model

import dao.DAOPosition

object Group {
  case class JoinRequest(userName: String)
  case class Group(users: Seq[DAOPosition])
}
