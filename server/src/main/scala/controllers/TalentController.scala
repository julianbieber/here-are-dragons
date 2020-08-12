package controllers

import com.twitter.finagle.http.Request
import dao._
import javax.inject.Inject
import model.Character.{GroupTalent, Talent, TalentResponse}

import scala.concurrent.{ExecutionContext, Future}

class TalentController @Inject()(
  override val userDAO: UserDAO,
  executionContext: ExecutionContext,
  talentDAO: TalentDAO,
  talentUnlockDAO: TalentUnlockDAO,
  groupTalentDAO: GroupTalentDAO,
  groupDAO: GroupDAO,
  groupTalentUnlockDAO: GroupTalentUnlockDAO
) extends UserUtil {
  private implicit val ec: ExecutionContext = executionContext

  get("/talents") { request: Request =>
    withUserAsyncAuto(request) { userId =>
      Future {
        val (unlocking, unlockable) = getTalents(userId)

        val (groupUnlocking, groupUnlockable) = getGroupTalents(userId)

        TalentResponse(
          unlocking = unlocking,
          unlockOptions = unlockable,
          groupUnlocking = groupUnlocking,
          groupUnlockOptions = groupUnlockable
        )
      }
    }
  }

  private def getTalents(userId: Int): (Option[Talent], Seq[Talent]) = {
    talentUnlockDAO.getUnlocks(userId).map { unlockState =>
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

      (unlocking.headOption.map(TalentTree.createTalent),
        (available ++ triviallyUnlockable).map(TalentTree.createTalent))

    }.getOrElse((
      None,
      TalentTree
        .findRoots(talentDAO.getTalents()
        ).map(TalentTree.createTalent)
    ))
  }

  private def getGroupTalents(userId: Int): (Option[GroupTalent], Seq[GroupTalent]) = {
    groupDAO.getGroup(userId).flatMap { group =>
      groupTalentUnlockDAO.getUnlocks(group.members).map { unlockState =>
        val unlocked = groupTalentDAO.getTalents(unlockState.unlocked)

        def isUnlocked(id: Int): Boolean = unlocked.exists(_.id == id)

        val unlocking = groupTalentDAO.getTalents(unlockState.currentlyUnlocking.toSeq)

        def isUnlocking(id: Int): Boolean = unlocking.exists(_.id == id)

        val available = groupTalentDAO.getTalents(
          unlocked.flatMap(_.nextTalents).filterNot(id => isUnlocked(id) || isUnlocking(id))
        )

        def isAvailable(id: Int): Boolean = available.exists(_.id == id)


        val triviallyUnlockable = GroupTalentTree
          .findRoots(groupTalentDAO.getTalents()
          ).filterNot(u => isUnlocked(u.id) || isUnlocking(u.id) || isAvailable(u.id))

        (unlocking.headOption.map(GroupTalentTree.createTalent),
          (available ++ triviallyUnlockable).map(GroupTalentTree.createTalent))

      }
    }
      .getOrElse((
        None,
        GroupTalentTree
          .findRoots(groupTalentDAO.getTalents()
          ).map(GroupTalentTree.createTalent)
      ))
  }

  post("/talents/startUnlock") { request: Request =>
    withUserAsyncAutoOption(request) { userId =>
      Future {
        val talentToUnlock = request.getIntParam("id")
        val group = request.getBooleanParam("group")

        val (unlocking, unlockable) = getTalents(userId)

        val (groupUnlocking, groupUnlockable) = getGroupTalents(userId)

        if (group) {
          groupDAO.getGroup(userId).flatMap { currentGroup =>
            if (groupUnlockable.map(_.id).contains(talentToUnlock)) {
              groupTalentUnlockDAO.startUnlocking(currentGroup.members, talentToUnlock)
              Option(TalentResponse(
                unlocking = unlocking,
                unlockOptions = unlockable,
                groupUnlocking = groupUnlocking,
                groupUnlockOptions = groupUnlockable
              ))
            } else {
              None
            }
          }
        } else {
          if (unlockable.map(_.id).contains(talentToUnlock)) {
            talentUnlockDAO.startUnlocking(userId, talentToUnlock)
            Option(TalentResponse(
              unlocking = unlocking,
              unlockOptions = unlockable,
              groupUnlocking = groupUnlocking,
              groupUnlockOptions = groupUnlockable
            ))
          } else {
            None
          }
        }


      }
    }
  }


}
