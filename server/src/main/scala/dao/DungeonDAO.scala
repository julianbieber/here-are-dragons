package dao

import java.util.concurrent.atomic.AtomicInteger

import model.Character.Attributes
import model.Dungeon.Skill
import service.{Dungeon, DungeonGenerator, PlayerUnit, Status}

import scala.collection.mutable

object DungeonDAO {

  def newDungeon(userIds: Seq[Int], playerAttributes: Seq[Attributes], playerSkills: Seq[Seq[Skill]], generator: DungeonGenerator): (Int, Dungeon) = {
    val id = maxId.incrementAndGet()
    val players = userIds.zip(playerAttributes.zip(playerSkills)).zipWithIndex.map{ case ((userId, (attributes, skills)), id) =>
      combineToPlayerUnit(id, userId, attributes, skills)
    }
    val dungeon = generator.generate(userIds, players)

    dungeons.synchronized {
      dungeons.put(id, dungeon)
    }
    id -> dungeon
  }

  private def combineToPlayerUnit(id: Int, userId: Int, attributes: Attributes, skills: Seq[Skill]): PlayerUnit = {
    PlayerUnit(
      id = id,
      userId = userId,
      healthOffset = 0,
      ap = 4,
      maxAP = 6,
      apGain = 4,
      status = Status.empty,
      attributes = attributes,
      skills
    )
  }

  def getDungeon(id: Int): Option[Dungeon] = dungeons.synchronized {
    dungeons.get(id)
  }

  def updateDungeon(id: Int, d: Dungeon): Dungeon = dungeons.synchronized {
    dungeons.put(id, d)
    d
  }

  def getDungeonForUser(userId: Int): Option[(Int, Dungeon)] = {
    dungeons.synchronized {
      dungeons.find { case (_, dungeon) =>
        dungeon.userIds.contains(userId)
      }
    }
  }

  private val dungeons: mutable.Map[Int, Dungeon] = mutable.Map[Int, Dungeon]()
  private val maxId = new AtomicInteger(0)

}
