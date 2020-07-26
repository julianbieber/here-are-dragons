package dao

import model.Dungeon.Skill
import service.Status

object SkillDAO {
  val skills = IndexedSeq(
    SkillBuilder
      .create("Smolder")
      .withTargetPattern("11111")
      .withEffectPattern("1")
      .withDamage(5)
      .withBurn(2)
      .withCost(1)
      .withSpellPower(0.2f)
      .build(),
    SkillBuilder
      .create("Spark")
      .withTargetPattern("11111")
      .withEffectPattern("1")
      .withDamage(5)
      .withBurn(2)
      .withCost(1)
      .withSpellPower(0.5f)
      .build(),
    SkillBuilder.create("Shock")
      .withTargetPattern("101")
      .withCost(2)
      .withShock(1)
      .withSpellPower(0.1f)
      .build(),
    SkillBuilder.create("Rain")
      .withTargetPattern("1")
      .withEffectPattern("111111111")
      .withWet(5)
      .withCost(2)
      .build(),
    SkillBuilder
      .create("Walk")
      .withTargetPattern("101")
      .withCost(1)
      .movesTo(0)
      .build(),

    SkillBuilder.create("Stun")
      .withTargetPattern("101")
      .withCost(1)
      .withStun(1)
      .build(),

    SkillBuilder.create("KD")
      .withTargetPattern("101")
      .withKD(1)
      .withCost(2)
      .withStrength(0.5f)
      .build()
  )

  def extend(ids: Seq[Int]): Seq[Skill] = {
    ids.map(skills(_))
  }
}

object SkillBuilder {
  def create(name: String): SkillBuilder = {
    val b = SkillBuilder(Skills.nextId, name)
    Skills.nextId += 1
    b
  }
}

case class SkillBuilder(
  id: Int,
  name: String,
  targetPattern: String = "1",
  effectPattern: String = "1",
  apCost: Int = 1,
  damage: Int = 0,
  strengthScaling: Float = 0,
  spellPowerScaling: Float = 0,
  dexterityScaling: Float = 0,
  burnDuration: Int = 0,
  shockDuration: Int = 0,
  wetDuration: Int = 0,
  stunDuration: Int = 0,
  knockDownDuration: Int = 0,
  moves: Boolean = false,
  movementOffset: Int = 0,
  coolDown: Int = 1
) {
  def withTargetPattern(p: String): SkillBuilder = copy(targetPattern = p)
  def withEffectPattern(p: String): SkillBuilder = copy(effectPattern = p)
  def withCost(c: Int): SkillBuilder = copy(apCost = c)
  def withDamage(d: Int): SkillBuilder = copy(damage = d)
  def withStrength(d: Float): SkillBuilder = copy(strengthScaling = d)
  def withSpellPower(d: Float): SkillBuilder = copy(spellPowerScaling = d)
  def withDexterity(d: Float): SkillBuilder = copy(dexterityScaling = d)
  def withBurn(d: Int): SkillBuilder = copy(burnDuration = d)
  def withShock(d: Int): SkillBuilder = copy(shockDuration = d)
  def withWet(d: Int): SkillBuilder = copy(wetDuration = d)
  def withStun(d: Int): SkillBuilder = copy(stunDuration = d)
  def withKD(d: Int): SkillBuilder = copy(knockDownDuration = d)
  def movesTo(offset: Int): SkillBuilder = copy(moves = true, movementOffset = offset)
  def withCD(cd: Int): SkillBuilder = copy(coolDown = cd)

  def build(): Skill = {
    Skill(
      id,
      name,
      targetPattern,
      effectPattern,
      apCost,
      damage,
      strengthScaling,
      spellPowerScaling,
      dexterityScaling,
      Status(
        burning = burnDuration,
        wet = wetDuration,
        stunned = stunDuration,
        shocked = shockDuration,
        knockedDown = knockDownDuration
      ),
      moves,
      movementOffset,
      coolDown,
      0
    )
  }

}

private object Skills {
  var nextId = 0
}
