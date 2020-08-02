package background

import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import javax.inject.Inject
import sttp.client._
import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.dmarcous.s2utils.converters.CoordinateConverters


class PoIUpdater @Inject()(val poIDAO: dao.PoIDAO, val questdao: dao.QuestDAO, val positiondao: dao.PositionDAO, val userdao: dao.UserDAO) extends Background {

  def storeNode(node: Node): Unit = {
    poIDAO.createPoI(node.id.toLong, node.lon, node.lat, node.priority, node.tags.flatMap(_.get("name")))
  }

  def nullenAuffuellen(s: String): String = {
    var result = s
    while (result.length <= 63) {
      result = "0" + result
    }
    result
  }

  override def run(): Unit = {
    implicit val nodesCodec = JsonCodecMaker.make[Nodes]

    implicit val pointOfInterest = JsonCodecMaker.make[PoI]
    implicit val nameNode = JsonCodecMaker.make[Seq[Node]]

    val listOfUsers: Seq[Int] = userdao.getListOfEveryUserId()

    for (i <- listOfUsers) {

      positiondao.getPosition(i).map {
        pos => {

          val cellid = nullenAuffuellen(CoordinateConverters.lonLatToS2CellID(pos.longitude, pos.latitude, 14).id().toBinaryString)

          val r = PoI("GetPoI", cellid, "50", "[1,2,5,7,13,14,16,17,19,21]")
          val request = basicRequest.post(uri"http://130.83.245.99:8080").body(writeToString(r))

          implicit val backend = HttpURLConnectionBackend()
          val response = request.send()

          response.body match {

            case Left(varibla) => println(varibla)

            case Right(value) => {
              val nods = readFromString[Nodes](value)
              val n = readFromString[Seq[Node]](nods.nodes)
              n.foreach(storeNode(_))
            }
          }
        }
      }
    }
  }
}

case class PoI(update_type: String, cell_id: String, amount: String, classifiers: String)

case class Nodes(nodes: String)

case class Node(id: String, lat: Float, lon: Float, priority: Float, tags: Option[Map[String, String]])
