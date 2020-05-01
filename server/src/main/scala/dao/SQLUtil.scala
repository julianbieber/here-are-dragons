package dao

import scalikejdbc._

trait SQLUtil {
  def withSession[A](pool: ConnectionPool)(f: DBSession => A): A = {
    using(DB(pool.borrow())){ connection =>
      using(connection.autoCommitSession()) { session =>
        f(session)
      }
    }
  }

  def withReadOnlySession[A](pool: ConnectionPool)(f: DBSession => A): A = {
    using(DB(pool.borrow())){ connection =>
      using(connection.readOnlySession()) { session =>
        f(session)
      }
    }
  }
}
