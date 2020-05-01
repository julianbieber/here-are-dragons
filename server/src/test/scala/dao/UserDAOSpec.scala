package dao

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import testUtil.SQLSpec._
import testUtil.GeneratorUtil._

class UserDAOSpec extends AnyFlatSpec with Matchers {
  "UserDAO" must "create and delete a user a user" in withPool{ pool =>
    val dao = new UserDAO(pool)
    val name = oneRandom(genString)
    val password = oneRandom(genString)

    dao.createUser(name, password)

    val user = dao.getUser(name).get
    user.passwordHash should not be password
    user.name should be(name)

    dao.deleteUser(name)

    dao.getUser(name) should be(None)
  }

  it must "login, be logged in and logout" in withPool{ pool =>
    val dao = new UserDAO(pool)
    val name = oneRandom(genString)
    val password = oneRandom(genString)

    dao.createUser(name, password)
    val token = dao.login(name, password).get
    dao.isLoggedIn(name, token) should be(true)
    dao.logout(name, token)
    dao.deleteUser(name)
  }
}
