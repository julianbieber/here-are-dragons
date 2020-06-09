package service

import dao.{Dungeon, Empty, NPC, PlayerUnit}
import model.Dungeon.{Skill, SkillUsage, Turn}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class DungeonServiceSpec extends AnyFlatSpec with Matchers {
  "DungeonService" must "identify the targetable units" in {
    val service = new DungeonService
    val dungeon = Dungeon(
      None,
      None,
      Seq(Empty(1), Empty(1), Empty(1), PlayerUnit(0, 0), Empty(1), Empty(1), Empty(1)),
      0,
      0
    )

    val skill = Skill(
      "",
      "01010",
      "",
      0,
      0,
      0
    )

    val targetable = service.identifyTargetable(dungeon, skill, 3)
    targetable must contain theSameElementsInOrderAs Seq(2, 4)
  }

  it must "identiy the units that have been hit" in {
    val service = new DungeonService
    val dungeon = Dungeon(
      None,
      None,
      Seq(Empty(1), Empty(1), Empty(1), PlayerUnit(0, 0), Empty(1), Empty(1), Empty(1)),
      0,
      0
    )

    val skill = Skill(
      "",
      "",
      "01010",
      0,
      0,
      0
    )

    val hits = service.identifyHits(dungeon, skill, 3)
    hits must contain theSameElementsInOrderAs Seq(2, 4)
  }

  it must "apply a turn" in {
    val dungeon = Dungeon(
      Option(0),
      None,
      Seq(PlayerUnit(0, 0), NPC(0, 10), NPC(0, 10), NPC(0, 10), NPC(0, 10)),
      0,
      5
    )
    val skill = Skill(
      "",
      "111101111",
      "111",
      2,
      5,
      0
    )

    val turn = Turn(
      0,
      Seq(
        SkillUsage(
          2, skill
        )
      )
    )

    val service = new DungeonService

    val afterTurn = service.applyTurn(0, dungeon, turn)

    afterTurn.units must contain theSameElementsInOrderAs Seq(
      PlayerUnit(0, 0),
      NPC(0, 5),
      NPC(0, 5),
      NPC(0, 5),
      NPC(0, 10)
    )
    afterTurn.currentTurn must be(1)
    val afterSecondTurnAttempt = service.applyTurn(0, afterTurn, turn)
    afterSecondTurnAttempt must be(afterTurn)

    val afterSecondTurnSuccess = service.applyTurn(0, afterTurn.copy(currentTurn = 0), turn)

    afterSecondTurnSuccess.units must contain theSameElementsInOrderAs Seq(
      PlayerUnit(0, 0),
      Empty(0),
      Empty(0),
      Empty(0),
      NPC(0, 10)
    )

  }
}
