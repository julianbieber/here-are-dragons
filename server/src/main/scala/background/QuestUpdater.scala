package background
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import model._
import javax.inject.Inject
import sttp.client._
import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.dmarcous.s2utils.converters.CoordinateConverters
import com.vividsolutions.jts.math.Vector2D



class QuestUpdater @Inject() (val poIDAO: dao.PoIDAO, val questdao : dao.QuestDAO, val positiondao:dao.PositionDAO,val userdao: dao.UserDAO) extends Background {

  private var iter :Int = 0;

  def storeNode(node:Node): Unit={
    //questdao.createQuestFromAPI(node.id,node.lon,node.lat)
    poIDAO.createPoI(node.id.toLong,node.lon,node.lat,node.priority,node.tags.flatMap(_.get("name")))
  }

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

          val r = new PoI("GetPoI", varrr, "50", "[1,2,5,7,13,14,16,17,19,21]")
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
              n.foreach(storeNode)
          }
      }
    }

    }
  }

}

case class PoI(update_type:String,cell_id:String, amount:String,classifiers:String)
case class Nodes(nodes:String)
case class Node(id:String,lat:Float,lon:Float,priority:Float,tags:Option[Map[String,String]])
