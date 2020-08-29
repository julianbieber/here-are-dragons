package model

object Activity {
  case class ActivityStart(activityType: String)
  case class CalisthenicsPutBody(vector: Seq[Float])
}
