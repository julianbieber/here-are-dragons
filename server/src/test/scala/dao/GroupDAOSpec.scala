package dao

import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class GroupDAOSpec extends AnyFlatSpec with Matchers with MockFactory {
  "GroupDAO" must "add users to groups and leave again" in {
    val userDAO = mock[UserDAO]
    val user1 = 1
    val user2 = 2
    val user2Name = "name"

    (userDAO.getUser _).expects(user2Name).returns(Some(DAOUser(user2, user2Name, "")))

    val groupDAO = new GroupDAO(userDAO)
    groupDAO.addToGroup(user1, user2Name) must be(true)

    groupDAO.leaveGroup(user1)

    GroupDAO.groups must be(empty)

  }
}
