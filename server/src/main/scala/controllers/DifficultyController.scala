package controllers

import com.twitter.finagle.http.Request
import dao.{DifficultyDAO, GroupDAO, InternalGroup, UserDAO}
import javax.inject.Inject
import model.Dungeon.{DifficultyResponse, ExtendedDifficultyRow}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

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

  get("/difficulty") { request: Request =>
    withUserAsyncAuto(request) { userId =>
      Future {
        val rows = difficultyDAO.getAvailableDifficulties(userId)
        val allUserIds = rows.flatMap{ r =>
          r.userId :: r.groupMembers.toList
        }.distinct

        val users = userDAO.resolveNames(allUserIds).map(u => u.id -> u.name).toMap

        val extendedRows = rows.map{ row =>
          ExtendedDifficultyRow(
            row.id,
            row.difficulty,
            (row.userId :: row.groupMembers.toList).map(users)
          )
        }
        DifficultyResponse(extendedRows)
      }
    }
  }

}
