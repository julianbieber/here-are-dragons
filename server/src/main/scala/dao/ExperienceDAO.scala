package dao

import background.ExperienceValue
import javax.inject.Inject
import scalikejdbc.{ConnectionPool, _}

class ExperienceDAO @Inject() (val pool: ConnectionPool) extends SQLUtil {
  def addExperiences(experiences: Seq[ExperienceValue]): Unit = {
    withSession(pool) { implicit session: DBSession =>
      val experienceTuples = experiences.map{ exp =>
        Seq(
          "userid" -> exp.user,
          "activityId" -> exp.experienceType,
          "amount" -> exp.amount
        )
      }

      sql"INSERT INTO public.experiences (userid, activity_id, amount) VALUES ({userid}, {activityId}, {amount}) ON CONFLICT (userid, activity_id) DO UPDATE SET amount = experiences.amount + excluded.amount"
        .batchByName(experienceTuples:_*).apply()
    }
  }

  def getExperiences(userId: Int): UserExperience = {
    withReadOnlySession(pool) { implicit session =>
      val experiences = sql"SELECT activity_id, amount from public.experiences where userid = $userId".map{ row =>
        ExperienceValue(userId, row.int("activity_id"), row.long("amount"))
      }.list().apply()
      val sorcererExperience = experiences.find(_.experienceType == 1).map(_.amount).getOrElse(0L)
      val rangerExperience = experiences.find(_.experienceType == 2).map(_.amount).getOrElse(0L)
      val warriorExperience = experiences.find(_.experienceType == 3).map(_.amount).getOrElse(0L)
      UserExperience(warriorExperience, sorcererExperience, rangerExperience)
    }
  }
}

case class UserExperience(warrior: Long, sorcerer: Long, ranger: Long)
