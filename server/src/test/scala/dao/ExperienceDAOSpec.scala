package dao

import background.character.ExperienceValue
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import testUtil.SQLSpec._

class ExperienceDAOSpec extends AnyFlatSpec with Matchers {
  "ExperienceDAO" must "store and get experiences" in withPool { pool =>
    val userDAO = new UserDAO(pool)
    val experienceDAO = new ExperienceDAO(pool)

    val userId = userDAO.createUser("user", "pass").get

    experienceDAO.addExperiences(Seq(ExperienceValue(userId, 1, 100)))
    println(experienceDAO.getExperiences(userId))
    println("-----------------------")
    experienceDAO.getExperiences(userId).ranger must be(100)

    experienceDAO.addExperiences(Seq(ExperienceValue(userId, 1, 150)))
    experienceDAO.getExperiences(userId).ranger must be(250)
  }

}
