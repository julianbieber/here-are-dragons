package testUtil

import java.util.UUID

import org.flywaydb.core.Flyway
import scalikejdbc.{AutoSession, ConnectionPool}
import scalikejdbc._

object SQLSpec {
  private val user = "postgres"
  private val password = "example"
  Class.forName("org.postgresql.Driver")
  val preUrl = s"jdbc:postgresql://127.0.0.1:5432/"

  private def setup(dbName: String): Unit = {
    ConnectionPool.singleton(preUrl, user, password)
    val preSession = AutoSession
    SQL(s"CREATE DATABASE $dbName").execute.apply()(preSession)
    preSession.close()
    ConnectionPool.closeAll()
  }

  private def teardown(dbName: String): Unit = {
    ConnectionPool.singleton(preUrl, user, password)
    val preSession = AutoSession
    SQL(s"DROP DATABASE $dbName").execute.apply()(preSession)
    preSession.close()
    ConnectionPool.closeAll()
  }

  def withPool[A](f: ConnectionPool => A): A = {
    val dbName = s"test_${UUID.randomUUID()}".replaceAll("-", "_")

    setup(dbName)

    val url = s"$preUrl$dbName"
    val fw = Flyway.configure().dataSource(url, user, password).locations("migration").load()
    fw.migrate()

    ConnectionPool.singleton(url, user, password)

    try {
      f(ConnectionPool())
    } finally {
      fw.clean()
      teardown(dbName)
      ConnectionPool.closeAll()
    }
  }
}
