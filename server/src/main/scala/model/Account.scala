package model


object Account {
  case class LoginRequest(name: String, password: String)

  case class LoginResponse(id: Int, token: String)

  case class CreateResponse(id: Int)
}

