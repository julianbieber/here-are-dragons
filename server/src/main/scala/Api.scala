import dao.TestDAO
import dungeons.dungeons.{DungeonsGrpc, Position}
import dungeons.dungeons.EmptyResponse
import io.grpc.{Server, ServerBuilder}
import scalikejdbc.{AutoSession, ConnectionPool}

import scala.concurrent.{ExecutionContext, Future}


object Api extends App {

  private val port = 50051

  Class.forName("org.postgresql.Driver")
  ConnectionPool.singleton("jdbc:postgresql://db:5432/postgres", "postgres", "example")
  val session = AutoSession

  val api = new Api(ExecutionContext.global, new TestDAO(session))
  api.start()
  api.blockUntilShutdown()
}


class Api(executionContext: ExecutionContext, dao: TestDAO) { self =>
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
    override def reportPosition(request: Position): Future[EmptyResponse] = {
      Future {
        dao.insert(request.lat, request.long)
        EmptyResponse()
      }(executionContext)
    }
  }

}