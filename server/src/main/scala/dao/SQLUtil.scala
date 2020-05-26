package dao

import java.time.ZonedDateTime
import java.util.TimeZone

import org.joda.time.{DateTime, DateTimeZone}
import scalikejdbc._

trait SQLUtil {
  def withSession[A](pool: ConnectionPool)(f: DBSession => A): A = {
    using(DB(pool.borrow())) { connection =>
      using(connection.autoCommitSession()) { session =>
        f(session)
      }
    }
  }

  def withReadOnlySession[A](pool: ConnectionPool)(f: DBSession => A): A = {
    using(DB(pool.borrow())) { connection =>
      using(connection.readOnlySession()) { session =>
        f(session)
      }
    }
  }

  def toDateTime(t: ZonedDateTime): DateTime = {
    val d = new DateTime(
      t.toInstant.toEpochMilli,
      DateTimeZone.forTimeZone(TimeZone.getTimeZone(t.getZone)))
    d.toDateTime(DateTimeZone.UTC)
  }
}
