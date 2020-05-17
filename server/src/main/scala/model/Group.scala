package model

object Group {
  case class JoinRequest(userName: String)
  case class Group(users: Seq[Int])
}
