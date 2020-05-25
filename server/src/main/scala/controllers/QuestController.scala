package controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.Inject
import dao.{UserDAO,PositionDAO,QuestDAO}
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

import scala.concurrent.ExecutionContext

class QuestController @Inject()(val questDAO: QuestDAO,positionDAO: PositionDAO,executionContext: ExecutionContext) extends UserUtil {

  private implicit val ec: ExecutionContext = executionContext
  private implicit val daoPositon :JsonValueCodec[dao.DAOPosition]=JsonCodecMaker.make[dao.DAOPosition];
  private implicit val daoQuestcodec :JsonValueCodec[dao.DAOQuest]=JsonCodecMaker.make[dao.DAOQuest];
  private implicit val lOQ :JsonValueCodec[List[dao.DAOQuest]]=JsonCodecMaker.make[List[dao.DAOQuest]];

  get("/getListOfRequests") { request: Request =>
    val y=request.getParam("distance").toFloat
      withUser(request) { userId =>

        var i =positionDAO.getPosition(userId).get
        response.ok(writeToString(questDAO.getListOfQuestsNerby(i.longitude, i.latitude, y)))
       }
  }
}
