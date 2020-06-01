package controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.Inject
import dao.{PositionDAO, QuestDAO, UserDAO}
import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class QuestController @Inject()(val questDAO: QuestDAO,positionDAO: PositionDAO,override val userDAO: UserDAO,executionContext: ExecutionContext) extends UserUtil {

  private implicit val ec: ExecutionContext = executionContext
  private implicit val daoPositon :JsonValueCodec[dao.DAOPosition]=JsonCodecMaker.make[dao.DAOPosition];
  private implicit val daoQuestcodec :JsonValueCodec[dao.DAOQuest]=JsonCodecMaker.make[dao.DAOQuest];
  private implicit val lOQ :JsonValueCodec[List[dao.DAOQuest]]=JsonCodecMaker.make[List[dao.DAOQuest]];
  private implicit val QuestsResponseCodex :JsonValueCodec[model.Quest.QuestsResponse]=JsonCodecMaker.make[model.Quest.QuestsResponse];

  get("/getListOfQuests") { request: Request =>
    println(request.getParam("distance"))
    val y=request.getParam("distance").toFloat
      withUser(request) { userId =>

        var i =positionDAO.getPosition(userId).get
        try{
          response.ok(writeToString(model.Quest.QuestsResponse(questDAO.getListOfQuestsNerby(i.longitude, i.latitude, y))))
        } catch {
          case NonFatal(e) => e.printStackTrace()
            response.internalServerError()
        }
       }
  }
}
