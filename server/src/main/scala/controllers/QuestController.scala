package controllers

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.twitter.finagle.http.Request
import dao.{QuestDAO, UserDAO}
import javax.inject.Inject
import model.Position.{PositionRequest, positionResponse}

import scala.concurrent.ExecutionContext

class QuestController @Inject()() extends UserUtil {
  override def userDAO: UserDAO = ???
}
