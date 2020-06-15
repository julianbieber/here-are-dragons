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
    println("dungeon request")
    withUserAuto(request) { userId =>
      println(s"dungeon request for $userId")
      val openRequest = readFromString[OpenRequest](request.getContentString())
      println(s"dungeon request for $openRequest")
      //questDAO.getQuests(openRequest.questId).map { quest =>
        // TODO if user has completed quest
      val (id, dungeon) = service.newSPDungeon(userId, openRequest.questId, characterDAO.getCharacter(userId))
      dungeonToResponse(id, userId, dungeon)
      //}
    }
  }

  private def dungeonToResponse(id: Int, userId: Int, dungeon: Dungeon): DungeonResponse = {
    DungeonResponse(
      dungeonId = id,
      units = dungeon.units.map(unitToResponse),
      myTurn =  dungeon.units.zipWithIndex.find {
        case (PlayerUnit(u, _, _, _, _), i) => u == userId
        case _ => false
      }.map(_._2).contains(dungeon.currentTurn),
      ap = dungeon.units.find{
        case PlayerUnit(u, _, _, _, _) => u == userId
        case _ => false
      }.map(_.asInstanceOf[PlayerUnit].ap).getOrElse(0)
    )
  }

  private def unitToResponse(unit: DungeonUnit): UnitResponse = {
    unit match {
      case PlayerUnit(userId, health, _, _, _) => UnitResponse(tyype = "player", userId = Option(userId), health = Option(health), prefabId = None)
      case NPC(prefabId, health, _, _, _, _) => UnitResponse(tyype = "npc", userId = None, health = Option(health), prefabId = Option(prefabId))
      case Empty(prefabId) =>UnitResponse(tyype = "empty", userId = None, health = None, prefabId = Option(prefabId))
    }
  }

  post("/dungeon/:dungeonId") { request: Request =>
    withUserAutoOption(request){ userId =>
      val dungeonId = request.params("dungeonId").toInt
      val turn = readFromString[Turn](request.getContentString())
      DungeonDAO.getDungeon(dungeonId).flatMap{ preTurn =>
        println(s"apply turn: $turn")
        service(userId, preTurn, turn).map{ postTurn =>
          DungeonDAO.updateDungeon(dungeonId, postTurn)
          dungeonToResponse(dungeonId, userId, postTurn)
        }
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
