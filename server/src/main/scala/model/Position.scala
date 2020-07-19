package model



object Position{
  case class PositionRequest(longitude: Float,latitude:Float)
  case class positionResponse()
  case class UserPosition(userID:Integer,longitude:Float,latitude:Float)
}

