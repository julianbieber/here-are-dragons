import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import dao.{DAOPosition, Status}
import model.Account.{CreateResponse, LoginRequest, LoginResponse}
import model.Activity.ActivityStart
import model.Character.Character
import model.Dungeon._
import model.Group.{Group, JoinRequest}
import model.Position.PositionRequest
import model.Quest.QuestsResponse

package object controllers {
  implicit val loginCodec = JsonCodecMaker.make[LoginRequest]
  implicit val loginResponseCodec = JsonCodecMaker.make[LoginResponse]
  implicit val createResponseCodec = JsonCodecMaker.make[CreateResponse]
  implicit val activityStartCodec = JsonCodecMaker.make[ActivityStart]
  implicit val characterCodec = JsonCodecMaker.make[Character]
  implicit val skillBarCodec = JsonCodecMaker.make[ExtendedSkillBar]
  implicit val availableDungeonsCodec = JsonCodecMaker.make[AvailableDungeons]
  implicit val openRequestCodec = JsonCodecMaker.make[OpenRequest]
  implicit val unitResponseCodec = JsonCodecMaker.make[UnitResponse]
  implicit val dungeonResponseCodec = JsonCodecMaker.make[DungeonResponse]
  implicit val skillCodec = JsonCodecMaker.make[Skill]
  implicit val skillUsageCodec = JsonCodecMaker.make[SkillUsage]
  implicit val turnCodec = JsonCodecMaker.make[Turn]
  implicit val joinRequestCodec = JsonCodecMaker.make[JoinRequest]
  implicit val positionCodec = JsonCodecMaker.make[DAOPosition]
  implicit val GroupCodec = JsonCodecMaker.make[Group]
  implicit val daoQuestcodec = JsonCodecMaker.make[dao.DAOQuest]
  implicit val lOQ = JsonCodecMaker.make[List[dao.DAOQuest]]
  implicit val QuestsResponseCodec = JsonCodecMaker.make[QuestsResponse]
  implicit val positionRequestCodec = JsonCodecMaker.make[PositionRequest]
  implicit val statusCodec = JsonCodecMaker.make[Status]
}
