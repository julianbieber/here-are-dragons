package dao

import java.util.UUID

import javax.inject.Inject

import scala.collection.mutable

class GroupDAO @Inject() (userDAO: UserDAO) {
  def addToGroup(user: Int, otherUserName: String): Boolean = {
    userDAO.getUser(otherUserName).map{ case DAOUser(otherUserId , _, _)=>
      GroupDAO.groups.synchronized{
        if (!GroupDAO.groups.exists(_.members.contains(user))) {
          GroupDAO.groups.find(_.members.contains(otherUserId)) match {
            case Some(existingGroup) =>
              if (!existingGroup.members.contains(user))
                existingGroup.members += user
            case None =>
              val group = new mutable.ListBuffer[Int]
              group += user
              group += otherUserId
              GroupDAO.groups += InternalGroup(UUID.randomUUID().toString, group)
          }
        }
      }
    }.isDefined
  }

  def leaveGroup(user: Int): Unit = {
    GroupDAO.synchronized {
      GroupDAO.groups.foreach { group =>
        if (group.members.contains(user)) {
          group.members -= user
        }
      }
      GroupDAO.groups = GroupDAO.groups.filterNot(_.members.size <= 1)
    }
  }

  def getGroup(user: Int): Option[InternalGroup] = {
    GroupDAO.groups.synchronized {
      GroupDAO.groups.find(_.members.contains(user))
    }
  }
}

private[dao] object GroupDAO {
  var groups: mutable.ListBuffer[InternalGroup] = mutable.ListBuffer[InternalGroup]()
}

case class InternalGroup(id: String, members: mutable.ListBuffer[Int])