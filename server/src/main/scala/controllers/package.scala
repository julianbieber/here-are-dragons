import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import dao.{ActiveInGroup, AttributesTable}
import model.Account.{CreateResponse, LoginRequest, LoginResponse}
import model.Activity.{ActivityStart, CalisthenicsPutBody}
import model.Character.{Attributes, Character, Talent, TalentResponse}
import model.Dungeon._
import model.Group.{Group, JoinRequest}
import model.Position.{PositionRequest, UserPosition}
import model.Quest.{Difficulty, QuestsResponse, nextPosition}
import service.Status

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
  implicit val positionCodec = JsonCodecMaker.make[UserPosition]
  implicit val GroupCodec = JsonCodecMaker.make[Group]
  implicit val daoQuestcodec = JsonCodecMaker.make[dao.DAOQuest]
  implicit val lOQ = JsonCodecMaker.make[List[dao.DAOQuest]]
  implicit val QuestsResponseCodec = JsonCodecMaker.make[QuestsResponse]
  implicit val positionRequestCodec = JsonCodecMaker.make[PositionRequest]
  implicit val statusCodec = JsonCodecMaker.make[Status]
  implicit val attributesCodec = JsonCodecMaker.make[Attributes]
  implicit val attributesTableCodec = JsonCodecMaker.make[AttributesTable]
  implicit val talentResponseCodec = JsonCodecMaker.make[TalentResponse]
  implicit val talentCodec = JsonCodecMaker.make[Talent]
  implicit val nextQuestPositionCodec = JsonCodecMaker.make[nextPosition]
  implicit val difficultyCodec = JsonCodecMaker.make[Difficulty]
  implicit val calisthenicsPutBodyCodec = JsonCodecMaker.make[CalisthenicsPutBody]
  implicit val difficultyResponseCoded = JsonCodecMaker.make[DifficultyResponse]
  implicit val activeInGroupCodec = JsonCodecMaker.make[ActiveInGroup]
  implicit val activeQuestCodec = JsonCodecMaker.make[Option[dao.DAOQuest]]
  implicit val activeDifficultyCodec = JsonCodecMaker.make[Option[Difficulty]]
}
