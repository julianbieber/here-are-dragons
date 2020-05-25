package testUtil

import java.util.UUID

import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import scalikejdbc.{AutoSession, ConnectionPool, using, _}

object SQLSpec {

  private val user = "postgres"
  private val password = "example"
  private def url(dbName: String) = s"jdbc:postgresql://127.0.0.1:5433/$dbName"
  val preDataSource: DataSource = {
    val ds = new HikariDataSource()
    ds.setDataSourceClassName("org.postgresql.ds.PGPoolingDataSource")
    ds.addDataSourceProperty("url", s"jdbc:postgresql://127.0.0.1:5433/")
    ds.addDataSourceProperty("user", user)
    ds.addDataSourceProperty("password", password)
    ds
  }
  val prePool = new DataSourceConnectionPool(preDataSource)


  private def setup(dbName: String): (HikariDataSource, ConnectionPool) = {
    using(DB(prePool.borrow())){ connection =>
      using(connection.autoCommitSession()) { session =>
        SQL(s"CREATE DATABASE $dbName").execute.apply()(session)
      }
    }

    val dataSource: HikariDataSource = {
      val ds = new HikariDataSource()
      ds.setDataSourceClassName("org.postgresql.ds.PGPoolingDataSource")
      ds.addDataSourceProperty("url", url(dbName))
      ds.addDataSourceProperty("user", user)
      ds.addDataSourceProperty("password", password)
      ds.setMinimumIdle(0)
      ds.setMaximumPoolSize(1)
      ds.setIdleTimeout(10000)
      ds
    }
    dataSource -> new DataSourceConnectionPool(dataSource, closer = () => dataSource.close())
  }

  def withPool[A](f: ConnectionPool => A): A = {
    val dbName = s"test_${UUID.randomUUID()}".replaceAll("-", "_")

    val (ds, pool) = setup(dbName)

    val fw = Flyway.configure().dataSource(url(dbName), user, password).locations("migration").load()
    fw.migrate()

    try{
      f(pool)
    } finally {
      ds.close()
      pool.close()
    }
  }
}
