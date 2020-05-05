import com.zaxxer.hikari.HikariDataSource
import dao.UserDAO
import dungeons.dungeons.{Auth, DungeonsGrpc, EmptyResponse, LoginResponse, User}
import io.grpc.{Server, ServerBuilder}
import javax.sql.DataSource
import scalikejdbc.{AutoSession, ConnectionPool, DataSourceConnectionPool}

import scala.concurrent.{ExecutionContext, Future}


object Api extends App {

  private val port = 50051

  val dataSource: DataSource = {
    val ds = new HikariDataSource()
    ds.setDataSourceClassName("org.postgresql.Driver")
    ds.addDataSourceProperty("url", "jdbc:postgresql://db:5432/postgres")
    ds.addDataSourceProperty("user", "postgres")
    ds.addDataSourceProperty("password", "example")
    ds
  }
  val pool = new DataSourceConnectionPool(dataSource)

  val api = new Api(ExecutionContext.global, new UserDAO(pool))
  api.start()
  api.blockUntilShutdown()
}


class Api(executionContext: ExecutionContext, userDao: UserDAO) {
  self =>
  private[this] var server: Server = null

  private def start(): Unit = {
    server = ServerBuilder
      .forPort(Api.port)
      .addService(DungeonsGrpc.bindService(new DungeonsImpl, executionContext))
      .build
    server.start()

    sys.addShutdownHook {
      self.stop()
    }
  }

  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }

  private class DungeonsImpl extends DungeonsGrpc.Dungeons {
    override def login(request: User): Future[LoginResponse] = Future {
      userDao.login(request.name, request.password).map { token =>
        LoginResponse(token)
      }.getOrElse(throw new RuntimeException("Failed to log in"))
    }(executionContext)

    override def createUser(request: User): Future[LoginResponse] = Future {
      userDao.createUser(request.name, request.password).map{ _ =>
        val token = userDao.login(request.name, request.password).get
        LoginResponse(token)
      }.getOrElse(throw new RuntimeException(s"Failed to create user: ${request.name}"))
    }(executionContext)

  }

}