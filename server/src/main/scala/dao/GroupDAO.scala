package dao

import javax.inject.Inject
import model.Group.Group

import scala.collection.mutable

class GroupDAO @Inject() (userDAO: UserDAO) {
  def addToGroup(user: Int, otherUserName: String): Boolean = {
    userDAO.getUser(otherUserName).map{ case DAOUser(otherUserId , _, _)=>
      GroupDAO.groups.synchronized{
        if (!GroupDAO.groups.exists(_.contains(user))) {
          GroupDAO.groups.find(_.contains(otherUserId)) match {
            case Some(existingGroup) =>
              if (!existingGroup.contains(user))
                existingGroup += user
            case None =>
              val group = new mutable.ListBuffer[Int]
              group += user
              group += otherUserId
              GroupDAO.groups += group
          }
        }
      }
    }.isDefined
  }

  def leaveGroup(user: Int): Unit = {
    GroupDAO.synchronized {
      GroupDAO.groups.foreach { group =>
        if (group.contains(user)) {
          group -= user
        }
      }
      GroupDAO.groups = GroupDAO.groups.filterNot(_.size <= 1)
    }
  }

  def getGroup(user: Int): Option[Seq[Int]] = {
    GroupDAO.groups.synchronized {
      GroupDAO.groups.find(_.contains(user))
    }
  }
}

private[dao] object GroupDAO {
  var groups: mutable.ListBuffer[mutable.ListBuffer[Int]] = mutable.ListBuffer[mutable.ListBuffer[Int]]()
}