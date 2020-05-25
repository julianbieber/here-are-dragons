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

  def getExperiences(userId: Int): Seq[ExperienceValue] = {
    withReadOnlySession(pool) { implicit session =>
      sql"SELECT activity_id, amount from public.experiences where userid = $userId".map{ row =>
        ExperienceValue(userId, row.int("activity_id"), row.long("amount"))
      }.list().apply()
    }
  }
}
