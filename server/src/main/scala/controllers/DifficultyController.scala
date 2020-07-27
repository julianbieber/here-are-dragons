package controllers

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.Request
import dao.{PositionDAO, QuestDAO, UserDAO,DifficultyDAO}
import javax.inject.Inject

import scala.concurrent.ExecutionContext

class DifficultyController @Inject()(val difficultyDAO: DifficultyDAO, override val userDAO: UserDAO, executionContext: ExecutionContext) extends UserUtil {

  private implicit val ec: ExecutionContext = executionContext

  post("/difficulty") {request: Request =>
    val difficulty = request.getParam("difficulty").toInt
    val group = request.getParam("group").toBoolean
    withUser(request) { userId =>
      difficultyDAO.setDifficulty(userId,difficulty,group)
      response.ok
    }
  }

}
