package controllers

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.twitter.finagle.http.Request
import dao.{AttributesDAO, DifficultyDAO, DifficultyRow, DungeonDAO, GroupDAO, QuestDAO, SkillDAO, SkillbarDAO, UserDAO}
import javax.inject.Inject
import model.Dungeon.{AvailableDungeons, DungeonResponse, OpenRequest, SkillUsage, UnitResponse}
import model.Quest.Difficulty
import service.{Dungeon, DungeonService, DungeonUnit, Empty, NPC, PlayerUnit}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class DungeonController @Inject() (
  override val userDAO: UserDAO,
  executionContext: ExecutionContext,
  service: DungeonService,
  difficultyDAO: DifficultyDAO,
  attributesDAO: AttributesDAO,
  skillbarDAO: SkillbarDAO
) extends UserUtil {
  private implicit val ec: ExecutionContext = executionContext

  get("/dungeons") { request: Request =>
    withUserAuto(request) { userId =>
      val dungeons = DungeonDAO.getDungeonForUser(userId)
      val ids = dungeons.filterNot{ case (_, dungeon) =>
        val (won, lost) = dungeon.completed
        won || lost
      }.map(_._1)
      AvailableDungeons(ids.toSeq)
    }
  }

  put("/dungeon") { request: Request =>
    withUserAsyncAutoOption(request) { userId =>
      Future{
        val openRequest = readFromString[OpenRequest](request.getContentString())
        difficultyDAO.getDifficultyById(openRequest.difficultyId).flatMap{ difficulty =>
        //val difficulty = DifficultyRow(0, userId, Seq(), openRequest.difficultyId) // tmp
          val userIds = difficulty.groupMembers.+:(difficulty.userId)
          if (userIds.contains(userId) && userIds.forall{ id =>
            DungeonDAO.getDungeonForUser(id).forall { case (_, d) =>
              val (won, lost) = d.completed
              won || lost
            }
          }) {
            difficultyDAO.setDungeon(difficulty.id)
            val selectedAttributes = userIds.map(attributesDAO.readAttributes(_).selected)
            val skills = userIds.map(skillbarDAO.getSkillBar(_).map(_.selected.map(SkillDAO.skills(_))).getOrElse(Seq()))
            val (id, dungeon) = service.newDungeon(userIds, difficulty.difficulty, selectedAttributes, skills)
            Option(dungeonToResponse(id, userId, dungeon))
          } else {
            None
          }
        }
      }
    }
  }

  private def dungeonToResponse(id: Int, userId: Int, dungeon: Dungeon): DungeonResponse = {
    val (won, lost) = dungeon.completed
    DungeonResponse(
      dungeonId = id,
      currentLevel = dungeon.currentLevel,
      levels = dungeon.units.length,
      units = dungeon.units(dungeon.currentLevel).map(unitToResponse),
      myTurn = Try(dungeon.findUser(userId)._1.id == dungeon.currentTurn).getOrElse(false),
      ap = Try(dungeon.findUser(userId)._1.ap).getOrElse(0),
      won,
      lost
    )
  }

  private def unitToResponse(unit: DungeonUnit): UnitResponse = {
    unit match {
      case PlayerUnit(_, userId, healthOffset, _, _, _, status, attributes, skills) => UnitResponse(tyype = "player", userId = Option(userId), health = Option((attributes.constitution + unit.attributesOffsets.map(_._1.constitution).sum) * 10 - healthOffset), prefabId = None, status = status, skills)
      case NPC(_, prefabId, healthOffset, skills, _, _, _, status, attributes) => UnitResponse(tyype = "npc", userId = None, health = Option((attributes.constitution + unit.attributesOffsets.map(_._1.constitution).sum) * 10 - healthOffset), prefabId = Option(prefabId), status = status, skills)
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
        val (playerUnit, _) = dungeon.findUser(userId)
        playerUnit.skills.find(_.id == attemptedSkillUsage.skill.id).flatMap{ actualSkillUsage =>

          val skillUsage = attemptedSkillUsage.copy(skill = actualSkillUsage)

          service.applyAction(playerUnit.id, skillUsage, dungeon).map{ updated =>
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
