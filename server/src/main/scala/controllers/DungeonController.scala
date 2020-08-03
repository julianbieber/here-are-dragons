package controllers

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.Request
import dao.{AttributesDAO, DungeonDAO, GroupDAO, QuestDAO, SkillDAO, SkillbarDAO, UserDAO}
import javax.inject.Inject
import model.Dungeon.{AvailableDungeons, DungeonResponse, OpenRequest, SkillUsage, UnitResponse}
import service.{Dungeon, DungeonService, DungeonUnit, Empty, NPC, PlayerUnit}

import scala.concurrent.ExecutionContext

class DungeonController @Inject() (
  override val userDAO: UserDAO,
  executionContext: ExecutionContext,
  service: DungeonService,
  groupDAO: GroupDAO,
  questDAO: QuestDAO,
  attributesDAO: AttributesDAO,
  skillbarDAO: SkillbarDAO
) extends UserUtil {
  private implicit val ec: ExecutionContext = executionContext




  get("/dungeons") { request: Request =>
    withUserAuto(request) { userId =>
      val spDungeon = DungeonDAO.getDungeonForUser(userId).map(_._1)
      AvailableDungeons(spDungeon.toSeq)
    }
  }

  put("/dungeon") { request: Request =>
    withUserAuto(request) { userId =>
      val openRequest = readFromString[OpenRequest](request.getContentString())
      //questDAO.getQuests(openRequest.questId).map { quest =>
        // TODO if user has completed quest
      val userIds = groupDAO.getGroup(userId).map{ group =>
        group.members
      }.getOrElse(Seq(userId))

      val selectedAttributes = userIds.map(attributesDAO.readAttributes(_).selected)
      val skills = userIds.map(skillbarDAO.getSkillBar(_).map(_.selected.map(SkillDAO.skills(_))).getOrElse(Seq()))
      val (id, dungeon) = service.newDungeon(userIds, openRequest.questId, selectedAttributes, skills)
      dungeonToResponse(id, userId, dungeon)
      //}
    }
  }

  private def dungeonToResponse(id: Int, userId: Int, dungeon: Dungeon): DungeonResponse = {
    val (won, lost) = dungeon.completed
    DungeonResponse(
      dungeonId = id,
      currentLevel = dungeon.currentLevel,
      units = dungeon.units.map(_.map(unitToResponse)),
      myTurn =  dungeon.units(dungeon.currentLevel).zipWithIndex.find {
        case (PlayerUnit(unitId, _, _, _, _, _, _, _, _), _) => dungeon.turnOrder(dungeon.currentTurn) == unitId
        case _ => false
      }.map(_._2).contains(dungeon.currentTurn),
      ap = dungeon.units(dungeon.currentLevel).find{
        case PlayerUnit(_, u, _, _, _, _, _, _, _) => u == userId
        case _ => false
      }.map(_.asInstanceOf[PlayerUnit].ap).getOrElse(0),
      won,
      lost
    )
  }

  private def unitToResponse(unit: DungeonUnit): UnitResponse = {
    unit match {
      case PlayerUnit(_, userId, health, _, _, _, status, _, skills) => UnitResponse(tyype = "player", userId = Option(userId), health = Option(health), prefabId = None, status = status, skills)
      case NPC(_, prefabId, health, skills, _, _, _, status, _) => UnitResponse(tyype = "npc", userId = None, health = Option(health), prefabId = Option(prefabId), status = status, skills)
      case Empty(_, prefabId, status) =>UnitResponse(tyype = "empty", userId = None, health = None, prefabId = Option(prefabId), status = status, Seq())
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
      val attemptedSkillUsage = readFromString[SkillUsage](request.getContentString())
      DungeonDAO.getDungeon(dungeonId).flatMap{ dungeon =>
        val (playerUnit, unitId) = dungeon.findUser(userId)
        playerUnit.skills.find(_.id == attemptedSkillUsage.skill.id).flatMap{ actualSkillUsage =>

          val skillUsage = attemptedSkillUsage.copy(skill = actualSkillUsage)

          service.applyAction(unitId, skillUsage, dungeon).map{ updated =>
            DungeonDAO.updateDungeon(dungeonId, updated)
          }
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
