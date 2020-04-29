import dungeons.dungeons.{DungeonsGrpc, Position}
import io.grpc.ManagedChannelBuilder

object TestClient extends App{
  val channel = ManagedChannelBuilder.forAddress("0.0.0.0", 50051).usePlaintext().build
  val request = Position(lat = 0.1f, long = 0.2f)
  val stub = DungeonsGrpc.blockingStub(channel)
  stub.reportPosition(request)
}
