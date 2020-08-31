package dao


import javax.inject.Inject
import org.joda.time.DateTime
import scalikejdbc.{ConnectionPool, _}

class CalisthenicsDAO @Inject()(val pool: ConnectionPool) extends SQLUtil {

  def store(row: CalisthenicsRow): Unit = {
    withSession(pool) { implicit session: DBSession =>
      sql"insert into public.calisthenics (user_id, calisthenics_type, vector, timestamp, processed) values (${row.userId}, ${row.calisthenicsType}, ${row.vector.toArray}, ${row.timestamp}, ${row.processed})"
        .execute()
        .apply()
    }
  }

  def getNotProcessed(): Seq[CalisthenicsRow] = {
    withReadOnlySession(pool) { implicit session: DBSession =>
      sql"select user_id, calisthenics_type, vector, timestamp, processed from public.calisthenics where processed = false".map{ row =>
        CalisthenicsRow(
          row.int("user_id"),
          row.int("calisthenics_type"),
          row.array("vector").getArray.asInstanceOf[Array[java.lang.Float]].toSeq.map(_.floatValue()),
          toDateTime(row.timestamp("timestamp").toZonedDateTime),
          row.boolean("processed")
        )
      }.list().apply()
    }
  }

  def getAll(): Seq[CalisthenicsRow] = {
    withReadOnlySession(pool) { implicit session: DBSession =>
      sql"select user_id, calisthenics_type, vector, timestamp, processed from public.calisthenics".map{ row =>
        CalisthenicsRow(
          row.int("user_id"),
          row.int("calisthenics_type"),
          row.array("vector").getArray.asInstanceOf[Array[java.lang.Float]].toSeq.map(_.floatValue()),
          toDateTime(row.timestamp("timestamp").toZonedDateTime),
          row.boolean("processed")
        )
      }.list().apply()
    }
  }

  def setProcessed(user2Time: Seq[(Int, DateTime)]): Unit = {
    withSession(pool) { implicit session: DBSession =>
      sql"UPDATE public.calisthenics SET processed = true where user_id = {u} and timestamp = {t}".batchByName(user2Time.map { case(userId, timestamp) =>
        Seq("u" -> userId, "t" -> timestamp)
      }: _*).apply()
    }
  }

  def getBetween(user: Int, start: DateTime, end: DateTime): Seq[CalisthenicsRow] = {
    withReadOnlySession(pool) { implicit session: DBSession =>
      sql"select user_id, calisthenics_type, vector, timestamp, processed from public.calisthenics where user_id = $user and timestamp between $start and $end".map{ row =>
        CalisthenicsRow(
          row.int("user_id"),
          row.int("calisthenics_type"),
          row.array("vector").getArray.asInstanceOf[Array[java.lang.Float]].toSeq.map(_.floatValue()),
          toDateTime(row.timestamp("timestamp").toZonedDateTime),
          row.boolean("processed")
        )
      }.list().apply()
    }
  }

}

case class CalisthenicsRow(userId: Int, calisthenicsType: Int, vector: Seq[Float], timestamp: DateTime, processed: Boolean)