package dao

import java.util.concurrent.atomic.AtomicInteger

import service.{DungeonGenerator, PlayerCharacter}

import scala.collection.mutable

object DungeonDAO {

  def newDungeon(userId: Int, playerCharacter: PlayerCharacter, generator: DungeonGenerator): (Int, Dungeon) = {
    val id = maxId.incrementAndGet()
    val dungeon = generator.generate(userId, Seq(playerCharacter.toUnit))

    dungeons.synchronized {
      dungeons.put(id, dungeon)
    }
    id -> dungeon
  }


  def getDungeonForUser(userId: Int): Option[(Int, Dungeon)] = {
    dungeons.find{ case (_, dungeon) =>
      dungeon.userId == Option(userId)
    }
  }

  def getDungeonForGroup(groupId: String): Option[(Int, Dungeon)] = {
    dungeons.find{ case (_, dungeon) =>
      dungeon.groupId == Option(groupId)
    }
  }

  private val dungeons:  mutable.Map[Int, Dungeon] = mutable.Map[Int, Dungeon]()
  private val maxId = new AtomicInteger(0)

}

case class Dungeon (
  userId: Option[Int],
  groupId: Option[String],
  units: Seq[DungeonUnit],
  currentTurn: Int,
  ap: Int
) {

}

sealed trait DungeonUnit {

}

case class PlayerUnit(userId: Int, health: Int) extends DungeonUnit

case class NPC(prefabId: Int, health: Int) extends DungeonUnit

case class Empty(prefabId: Int) extends DungeonUnit