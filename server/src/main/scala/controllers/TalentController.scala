package controllers

import com.twitter.finagle.http.Request
import dao.{ActivityDAO, TalentDAO, TalentRow, TalentTree, TalentUnlockDAO, UserDAO}
import javax.inject.Inject
import model.Character.TalentResponse

import scala.concurrent.{ExecutionContext, Future}

class TalentController @Inject() (override val userDAO: UserDAO, executionContext: ExecutionContext, talentDAO: TalentDAO, talentUnlockDAO: TalentUnlockDAO) extends UserUtil {
  private implicit val ec: ExecutionContext = executionContext


  get("/talents") { request: Request =>
    withUserAsyncAuto(request) { userId =>
      Future {
        talentUnlockDAO.getUnlocks(userId).map{ unlockState =>
          val unlocked = talentDAO.getTalents(unlockState.unlocked)
          def isUnlocked(id: Int): Boolean = unlocked.exists(_.id == id)
          val unlocking = talentDAO.getTalents(unlockState.currentlyUnlocking.toSeq)
          def isUnlocking(id: Int): Boolean = unlocking.exists(_.id == id)

          val available = talentDAO.getTalents(
            unlocked.flatMap(_.nextTalents).filterNot(id => isUnlocked(id) || isUnlocking(id))
          )
          def isAvailable(id: Int): Boolean = available.exists(_.id == id)


          val triviallyUnlockable = TalentTree
            .findRoots(talentDAO.getTalents()
            ).filterNot(u => isUnlocked(u.id) || isUnlocking(u.id) || isAvailable(u.id))

          TalentResponse(
            unlocked = TalentTree.fromRows(unlocked),
            unlocking = unlocking.headOption.map(TalentTree.createTalent),
            unlockOptions = (available ++ triviallyUnlockable).map(TalentTree.createTalent)
          )

        }.getOrElse(TalentResponse(
          Seq(),
          None.flatten,
          TalentTree
            .findRoots(talentDAO.getTalents()
            ).map(TalentTree.createTalent)
        ))
      }
    }
  }

}
