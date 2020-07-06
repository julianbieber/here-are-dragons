package dao

import java.util.concurrent.atomic.AtomicInteger

import service.{Dungeon, DungeonGenerator, PlayerCharacter}

import scala.collection.mutable

object DungeonDAO {

  def newDungeon(userId: Int, playerCharacter: PlayerCharacter, generator: DungeonGenerator): (Int, Dungeon) = {
    val id = maxId.incrementAndGet()
    val dungeon = generator.generate(userId, Seq(playerCharacter.toUnit.copy(id = 0)))

    dungeons.synchronized {
      dungeons.put(id, dungeon)
    }
    id -> dungeon
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
        dungeon.userId == Option(userId)
      }
    }
  }

  def getDungeonForGroup(groupId: String): Option[(Int, Dungeon)] = {
    dungeons.synchronized {
      dungeons.find { case (_, dungeon) =>
        dungeon.groupId == Option(groupId)
      }
    }
  }

  private val dungeons: mutable.Map[Int, Dungeon] = mutable.Map[Int, Dungeon]()
  private val maxId = new AtomicInteger(0)

}
