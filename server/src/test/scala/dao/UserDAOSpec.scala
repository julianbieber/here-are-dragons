package dao

import model.Account.LoginResponse
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
    val LoginResponse(id, token) = dao.login(name, password).get
    dao.isLoggedIn(id, token) should be(true)
    dao.logout(id, token)
    dao.deleteUser(name)
  }

  it must "get a list of every user" in withPool{ pool =>
    val dao = new UserDAO(pool)

    //create first user
    val name = oneRandom(genString)
    val password = oneRandom(genString)
    val i = dao.createUser(name,password).get

    //create second user
    val name1 = oneRandom(genString)
    val password1 = oneRandom(genString)
    val j = dao.createUser(name1,password1).get

    //generate expected result and the result of the tested method
    val generatedList = dao.getListOfEveryUserId()
    val result = Seq[Int](0,i,j)

    //compares expected result and the result of the tested method
    generatedList should be(result)
    dao.deleteUser(name)
    dao.deleteUser(name1)
  }
}
