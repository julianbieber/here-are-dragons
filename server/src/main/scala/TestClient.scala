import dungeons.dungeons._
import io.grpc.ManagedChannelBuilder

object TestClient extends App{
  val channel = ManagedChannelBuilder.forAddress("0.0.0.0", 50051).usePlaintext().build
  val request = User(name = "", password = "")
  val stub = DungeonsGrpc.blockingStub(channel)
  stub.login(request)
}
