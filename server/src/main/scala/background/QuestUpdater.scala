package background
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import model._
import javax.inject.Inject
import sttp.client._
import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.dmarcous.s2utils._
import com.github.dmarcous.s2utils.converters.CoordinateConverters
import com.vividsolutions.jts.math.Vector2D

class QuestUpdater @Inject() (val questdao : dao.QuestDAO, val positiondao:dao.PositionDAO,val userdao: dao.UserDAO) extends Background {

  def a(node:Node): Unit= questdao.createQuestFromAPI(node.id,node.lon,node.lat)

  override def run(): Unit = {

    implicit val nodesCodec = JsonCodecMaker.make[Nodes]

    val sort: Option[String] = None
    val query = "http language:scala"
    implicit val pointOfInterest = JsonCodecMaker.make[PoI]
    implicit val nameNode = JsonCodecMaker.make[Seq[Node]]


    //TODO:an dieser Stelle muss noch die Position übergeben werden, als (longitude,latitude)


    val listOfUsers : Seq[Int] = userdao.getListOfEveryUserId()

    for(i <- listOfUsers) {


      positiondao.getPosition(i).map {
        pos =>
          val positionOfPlayer = new Vector2D(pos.longitude, pos.latitude)


          val cell = CoordinateConverters.lonLatToS2CellID(positionOfPlayer.getX, positionOfPlayer.getY, 30)
          val cid = "0100011110111101011100000110001100000000000000000000000000000000"
        //cell.id().toString



          val r = new PoI("GetPoI", cid, "5", "[10,12]")
          val request = basicRequest.post(uri"http://130.83.245.99:8080").body(writeToString(r))


          implicit val backend = HttpURLConnectionBackend()
          val response = request.send()
          // response.header(...): Option[String]
          println(response.header("Content-Length"))

          // response.body: by default read into an Either[String, String] to indicate failure or success
          println(response.body)
          response.body match {
            case Left(varibla) => println(varibla)

            case Right(value) =>
              val nods = readFromString[Nodes](value)
              val n =readFromString[Seq[Node]](nods.nodes)
              n.foreach(a)
          }
      }
    }
  }
}

case class PoI(update_type:String,cell_id:String, amount:String,classifiers:String)
case class Nodes(nodes:String)
case class Node(id:String,lat:Float,lon:Float,priority:Float,tags:Map[String,String])
