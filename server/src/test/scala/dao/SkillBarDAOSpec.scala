package dao

import model.Dungeon.SkillBar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import testUtil.GeneratorUtil._
import testUtil.SQLSpec._

class SkillBarDAOSpec extends AnyFlatSpec with Matchers {
  "SkillBarDAO" must "unlock skills" in withPool{ pool =>
    val userDao = new UserDAO(pool)
    val name = oneRandom(genString)
    val password = oneRandom(genString)

    userDao.createUser(name, password)

    val user = userDao.getUser(name).get

    val dao = new SkillbarDAO(pool)

    dao.getSkillBar(user.id) should be(None)

    dao.unlock(user.id, 1)
    dao.unlock(user.id, 4)

    dao.getSkillBar(user.id) should be(Some(SkillBar(
      user.id,
      Seq(),
      Seq(1, 4)
    )))

    dao.selectSkill(user.id, 1, 0)

    dao.getSkillBar(user.id) should be(Some(SkillBar(
      user.id,
      Seq(1),
      Seq(1, 4)
    )))

    dao.selectSkill(user.id, 4, 0)

    dao.getSkillBar(user.id) should be(Some(SkillBar(
      user.id,
      Seq(4),
      Seq(1, 4)
    )))

    dao.selectSkill(user.id, 2, 1)

    dao.getSkillBar(user.id) should be(Some(SkillBar(
      user.id,
      Seq(4),
      Seq(1, 4)
    )))

    dao.unselectSkill(user.id, 1)

    dao.getSkillBar(user.id) should be(Some(SkillBar(
      user.id,
      Seq(4),
      Seq(1, 4)
    )))

  }


}
