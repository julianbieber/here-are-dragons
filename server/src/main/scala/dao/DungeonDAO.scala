package dao

import java.util.concurrent.atomic.AtomicInteger

import model.Dungeon.Skill
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

  def getDungeon(id: Int): Option[Dungeon] = dungeons.synchronized{
    dungeons.get(id)
  }

  def updateDungeon(id: Int, d: Dungeon): Dungeon = dungeons.synchronized{
    dungeons.put(id, d)
    d
  }

  def getDungeonForUser(userId: Int): Option[(Int, Dungeon)] = {
    dungeons.synchronized{
      dungeons.find{ case (_, dungeon) =>
        dungeon.userId == Option(userId)
      }
    }
  }

  def getDungeonForGroup(groupId: String): Option[(Int, Dungeon)] = {
    dungeons.synchronized{
      dungeons.find{ case (_, dungeon) =>
        dungeon.groupId == Option(groupId)
      }
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
  def applySkill(skill: Skill): DungeonUnit
}

case class PlayerUnit(userId: Int, health: Int) extends DungeonUnit {
  override def applySkill(skill: Skill): DungeonUnit = {
    val newHealth = health - skill.damage
    if (newHealth <= 0) {
      Empty(0)
    } else {
      copy(health = newHealth)
    }
  }
}

case class NPC(prefabId: Int, health: Int) extends DungeonUnit {
  override def applySkill(skill: Skill): DungeonUnit = {
    val newHealth = health - skill.damage
    if (newHealth <= 0) {
      Empty(0)
    } else {
      copy(health = newHealth)
    }
  }
}

case class Empty(prefabId: Int) extends DungeonUnit {
  override def applySkill(skill: Skill): DungeonUnit = {
    this
  }
}