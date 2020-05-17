package dao

import javax.inject.Inject

import scala.collection.mutable

class GroupDAO @Inject() (userDAO: UserDAO) {
  def addToGroup(user: Int, otherUserName: String): Boolean = {
    userDAO.getUser(otherUserName).map{ case DAOUser(otherUserId , _, _)=>
      GroupDAO.groups.synchronized{
        GroupDAO.groups.find(_.contains(otherUserId)) match {
          case Some(existingGroup) => existingGroup += user
          case None =>
            val group = new mutable.ListBuffer[Int]
            group += user
            group += otherUserId
            GroupDAO.groups += group
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
}

private[dao] object GroupDAO {
  var groups: mutable.ListBuffer[mutable.ListBuffer[Int]] = mutable.ListBuffer[mutable.ListBuffer[Int]]()
}