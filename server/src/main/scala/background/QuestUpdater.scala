package background
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import model._
import javax.inject.Inject
import sttp.client._
import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.dmarcous.s2utils._
import com.github.dmarcous.s2utils.converters.CoordinateConverters
import com.google.common.geometry.{S2CellId, S2LatLng}
import com.vividsolutions.jts.math.Vector2D
import javax.xml.bind.DatatypeConverter



class QuestUpdater @Inject() (val questdao : dao.QuestDAO, val positiondao:dao.PositionDAO,val userdao: dao.UserDAO) extends Background {

  private var iter :Int = 0;

  def a(node:Node): Unit= questdao.createQuestFromAPI(node.id,node.lon,node.lat)

  def nullenAuffuellen(s: String):String = {
    var result = s
    while(result.length <= 63){
       result = "0"+result
    }
    result
  }

  override def run(): Unit = {
    if (iter <5){
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

          val varrr = nullenAuffuellen(CoordinateConverters.lonLatToS2CellID(positionOfPlayer.getY,positionOfPlayer.getX,14).id().toBinaryString)
          val cid = "0100011110111101011100000110001100000000000000000000000000000000"



          println(positionOfPlayer.getX)
          println(positionOfPlayer.getY)
          println(varrr)
          println("__________________________")




          val r = new PoI("GetPoI", varrr, "5", "[10,12]")
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
              iter = iter + 1
              val nods = readFromString[Nodes](value)
              val n =readFromString[Seq[Node]](nods.nodes)
              n.foreach(a)
          }
      }
    }

    }
  }

}

case class PoI(update_type:String,cell_id:String, amount:String,classifiers:String)
case class Nodes(nodes:String)
case class Node(id:String,lat:Float,lon:Float,priority:Float,tags:Map[String,String])
