package controllers

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.Request
import dao.{CharacterDAO, Dungeon, DungeonDAO, DungeonUnit, Empty, GroupDAO, NPC, PlayerUnit, QuestDAO, UserDAO}
import javax.inject.Inject
import model.Dungeon.{AvailableDungeons, DungeonResponse, OpenRequest, Skill, SkillUsage, Turn, UnitResponse}
import service.DungeonService

import scala.concurrent.ExecutionContext

class DungeonController @Inject() (override val userDAO: UserDAO, executionContext: ExecutionContext, service: DungeonService, groupDAO: GroupDAO, questDAO: QuestDAO, characterDAO: CharacterDAO) extends UserUtil {
  private implicit val ec: ExecutionContext = executionContext

  private implicit val availableDungeonsCodec = JsonCodecMaker.make[AvailableDungeons]
  private implicit val openRequestCodec = JsonCodecMaker.make[OpenRequest]
  private implicit val unitResponseCodec = JsonCodecMaker.make[UnitResponse]
  private implicit val dungeonResponseCodec = JsonCodecMaker.make[DungeonResponse]
  private implicit val skillCodec = JsonCodecMaker.make[Skill]
  private implicit val skillUsageCodec = JsonCodecMaker.make[SkillUsage]
  private implicit val turnCodec = JsonCodecMaker.make[Turn]


  get("/dungeons") { request: Request =>
    withUserAuto(request) { userId =>
      val groupDungeon = groupDAO.getGroup(userId).flatMap(g => DungeonDAO.getDungeonForGroup(g.id).map(_._1))
      val spDungeon = DungeonDAO.getDungeonForUser(userId).map(_._1)
      AvailableDungeons(Seq(groupDungeon, spDungeon).flatten)
    }
  }

  put("/dungeon") { request: Request =>
    withUserAutoOption(request) { userId =>

      val openRequest = readFromString[OpenRequest](request.getContentString())
      questDAO.getQuests(openRequest.questId).map { quest =>
        // TODO if user has completed quest
        val (id, dungeon) = service.newSPDungeon(userId, quest.questID, characterDAO.getCharacter(userId))
        dungeonToResponse(id, userId, dungeon)
      }
    }
  }

  private def dungeonToResponse(id: Int, userId: Int, dungeon: Dungeon): DungeonResponse = {
    DungeonResponse(
      dungeonId = id,
      units = dungeon.units.map(unitToResponse),
      myTurn = dungeon.currentTurn == userId,
      ap = dungeon.ap
    )
  }

  private def unitToResponse(unit: DungeonUnit): UnitResponse = {
    unit match {
      case PlayerUnit(userId, health) => UnitResponse(tyype = "player", userId = Option(userId), health = Option(health), prefabId = None)
      case NPC(prefabId, health) => UnitResponse(tyype = "npc", userId = None, health = Option(health), prefabId = Option(prefabId))
      case Empty(prefabId) =>UnitResponse(tyype = "empty", userId = None, health = None, prefabId = Option(prefabId))
    }
  }

  post("/dungeon/:dungeonId") { request: Request =>
    withUserAutoOption(request){ userId =>
      val dungeonId = request.params("dungeonId").toInt
      val turn = readFromString[Turn](request.getContentString())
      DungeonDAO.getDungeon(dungeonId).map{ preTurn =>
        val postTurn = service.applyTurn(userId, preTurn, turn)
        DungeonDAO.updateDungeon(dungeonId, postTurn)
        dungeonToResponse(dungeonId, userId, postTurn)
      }
    }
  }

  get("/dungeon/:dungeonId") { request: Request =>
    withUserAutoOption(request) { userId =>
      val id = request.params("dungeonId").toInt
      DungeonDAO.getDungeon(id).map{ dungeon =>
        dungeonToResponse(id, userId, dungeon)
      }
    }
  }
}
