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




  get("/dungeons") { request: Request =>
    withUserAuto(request) { userId =>
      val groupDungeon = groupDAO.getGroup(userId).flatMap(g => DungeonDAO.getDungeonForGroup(g.id).map(_._1))
      val spDungeon = DungeonDAO.getDungeonForUser(userId).map(_._1)
      AvailableDungeons(Seq(groupDungeon, spDungeon).flatten)
    }
  }

  put("/dungeon") { request: Request =>
    withUserAuto(request) { userId =>
      val openRequest = readFromString[OpenRequest](request.getContentString())
      //questDAO.getQuests(openRequest.questId).map { quest =>
        // TODO if user has completed quest
      val (id, dungeon) = service.newSPDungeon(userId, openRequest.questId, characterDAO.getCharacter(userId))
      dungeonToResponse(id, userId, dungeon)
      //}
    }
  }

  private def dungeonToResponse(id: Int, userId: Int, dungeon: Dungeon): DungeonResponse = {
    val (won, lost) = dungeon.completed
    DungeonResponse(
      dungeonId = id,
      units = dungeon.units.map(unitToResponse),
      myTurn =  dungeon.units.zipWithIndex.find {
        case (PlayerUnit(unitId, _, _, _, _, _, _), _) => dungeon.turnOrder(dungeon.currentTurn) == unitId
        case _ => false
      }.map(_._2).contains(dungeon.currentTurn),
      ap = dungeon.units.find{
        case PlayerUnit(_, u, _, _, _, _, _) => u == userId
        case _ => false
      }.map(_.asInstanceOf[PlayerUnit].ap).getOrElse(0),
      won,
      lost
    )
  }

  private def unitToResponse(unit: DungeonUnit): UnitResponse = {
    unit match {
      case PlayerUnit(_, userId, health, _, _, _, status) => UnitResponse(tyype = "player", userId = Option(userId), health = Option(health), prefabId = None, status = status)
      case NPC(_, prefabId, health, _, _, _, _, status) => UnitResponse(tyype = "npc", userId = None, health = Option(health), prefabId = Option(prefabId), status = status)
      case Empty(_, prefabId, status) =>UnitResponse(tyype = "empty", userId = None, health = None, prefabId = Option(prefabId), status = status)
    }
  }

  // end turn
  post("/dungeon/:dungeonId") { request: Request =>
    withUserAutoOption(request) { userId =>
      val dungeonId = request.params("dungeonId").toInt
      DungeonDAO.getDungeon(dungeonId).flatMap{ dungeon =>
        val unitId = dungeon.findUser(userId)._1.id
        service.endTurn(unitId, dungeon).map{ updated =>
          DungeonDAO.updateDungeon(dungeonId, updated)
          updated
        }
      }.map(dungeonToResponse(dungeonId, userId, _))
    }
  }

  // Action
  post("/dungeon/:dungeonId/action") { request: Request =>
    withUserAutoOption(request) { userId =>
      val dungeonId = request.params("dungeonId").toInt
      val skillUsage = readFromString[SkillUsage](request.getContentString())
      DungeonDAO.getDungeon(dungeonId).flatMap{ dungeon =>
        val unitId = dungeon.findUser(userId)._1.id
        println(s"user $userId has unit: $unitId")
        service.applyAction(unitId, skillUsage, dungeon).map{ updated =>
          DungeonDAO.updateDungeon(dungeonId, updated)
        }
      }.map(dungeonToResponse(dungeonId, userId, _))
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
