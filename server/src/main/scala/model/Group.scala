package model

import model.Position.UserPosition

object Group {
  case class JoinRequest(userName: String)
  case class Group(users: Seq[UserPosition])
}
