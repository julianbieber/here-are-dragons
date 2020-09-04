package controllers

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.Request
import dao.{DifficultyDAO, GroupDAO, InternalGroup, PositionDAO, QuestDAO, UserDAO}
import javax.inject.Inject

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

class DifficultyController @Inject()(val difficultyDAO: DifficultyDAO, groupDAO: GroupDAO, override val userDAO: UserDAO, executionContext: ExecutionContext) extends UserUtil {

  private implicit val ec: ExecutionContext = executionContext

  post("/difficulty") {request: Request =>
    val difficulty = request.getParam("difficulty").toInt
    val group = request.getParam("group").toBoolean
    withUser(request) { userId =>
      difficultyDAO.setDifficulty(userId, difficulty, group, groupDAO.getGroup(userId).getOrElse(InternalGroup("",new ListBuffer[Int])).members)
      response.ok
    }
  }

}
