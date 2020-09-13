package dao

import model.Character.Attributes
import model.Dungeon.Skill
import service.Status

object SkillDAO {
  val skills = IndexedSeq(
    // Always unlocked
    SkillBuilder
      .create("Walk")
      .withTargetPattern("101")
      .withCost(1)
      .movesTo(0)
      .build(),

    // Sorcerer
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
    SkillBuilder.create("Ice Shards")
      .withTargetPattern("111")
      .withSpellPower(2.0f)
      .withCost(2)
      .withCD(2)
      .withWet(2)
      .build(),
    SkillBuilder.create("Fireball")
      .withTargetPattern("1110111")
      .withBurn(2)
      .withEffectPattern("11111")
      .withSpellPower(2.5f)
      .withCost(4)
      .withCD(2)
      .build(),
    SkillBuilder.create("Lightning")
      .withTargetPattern("11011")
      .withCost(2)
      .withStun(1)
      .withCD(3)
      .withSpellPower(2.0f)
      .build(),


    // Warrior
    SkillBuilder.create("Slice")
      .withTargetPattern("101")
      .withCost(2)
      .withStrength(1f)
      .withCD(1)
      .build(),
    SkillBuilder.create("Flurry")
      .withTargetPattern("101")
      .withCost(3)
      .withCD(2)
      .withStrength(2.0f)
      .build(),
    SkillBuilder.create("Thrust")
      .withTargetPattern("11011")
      .withCost(2)
      .withCD(3)
      .withStrength(2.0f)
      .build(),

    SkillBuilder.create("Stun")
      .withTargetPattern("101")
      .withCost(4)
      .withStun(1)
      .withCD(4)
      .withStrength(0.6f)
      .build(),
    SkillBuilder.create("Swipe Legs")
      .withTargetPattern("101")
      .withKD(1)
      .withCD(4)
      .withCost(4)
      .withStrength(0.5f)
      .build(),
    SkillBuilder.create("Charge")
      .withTargetPattern("1110111")
      .movesTo(1)
      .withKD(1)
      .withCost(4)
      .withStrength(1.0f)
      .build(),
    SkillBuilder.create("Throw Sword")
      .withStun(2)
      .withCD(4)
      .withCost(6)
      .withStrength(2.0f)
      .build(),

    // Ranger
    SkillBuilder.create("Shoot")
      .withDexterity(1.5f)
      .withCost(2)
      .withCD(1)
      .withTargetPattern("1110111")
      .build(),
    SkillBuilder.create("Quickshot")
      .withDexterity(1.0f)
      .withCost(1)
      .withTargetPattern("11011")
      .build(),
    SkillBuilder.create("Shock Shot")
      .withDexterity(0.7f)
      .withCost(2)
      .withShock(1)
      .withTargetPattern("11011")
      .withCD(2)
      .build(),
    SkillBuilder.create("Snipe")
      .withDexterity(2.0f)
      .withCost(3)
      .withTargetPattern("111101111")
      .withCD(1)
      .build(),
    SkillBuilder.create("Rain of Arrows")
      .withDexterity(0.8f)
      .withCost(4)
      .withTargetPattern("1110111")
      .withEffectPattern("111")
      .withCD(2)
      .build(),
    SkillBuilder.create("Burning Arrow")
      .withDexterity(0.4f)
      .withCost(2)
      .withBurn(2)
      .withTargetPattern("11011")
      .withEffectPattern("111")
      .withCD(2)
      .build(),
    SkillBuilder.create("Explosive Shot")
      .withDamage(20)
      .withDexterity(1.0f)
      .withTargetPattern("11011")
      .withEffectPattern("111")
      .withKD(1)
      .withCost(4)
      .withCD(2)
      .build(),

    // Group
    dao.SkillBuilder.create("Strength in Numbers")
      .withStrengthOffset(4)
      .withSpellPowerOffset(4)
      .withDexterityOffset(4)
      .withAttributesDuration(2)
      .withEffectPattern("111")
      .withTargetPattern("111")
      .withCD(2)
      .build(),
    dao.SkillBuilder.create("Take Cover!")
      .withEvasionOffset(10)
      .withAttributesDuration(1)
      .withEffectPattern("1")
      .withTargetPattern("11111")
      .withCD(5)
      .build(),
    dao.SkillBuilder.create("Focus him!")
      .withEvasionOffset(-3)
      .withAttributesDuration(1)
      .withEffectPattern("1")
      .withTargetPattern("1111111")
      .withCD(5)
      .build(),
    dao.SkillBuilder.create("Recover!")
      .withConstitutionOffset(3)
      .withAttributesDuration(3)
      .withCD(2)
      .withTargetPattern("111")
      .withEffectPattern("1")
      .build(),
    dao.SkillBuilder.create("Taunt")
      .withConstitutionOffset(-3)
      .withAttributesDuration(2)
      .withCD(1)
      .withTargetPattern("11111")
      .withEffectPattern("1")
      .build(),
    dao.SkillBuilder.create("Weakling!")
      .withStrengthOffset(-3)
      .withSpellPowerOffset(-3)
      .withDexterityOffset(-3)
      .withEffectPattern("1")
      .withTargetPattern("11111")
      .withAttributesDuration(2)
      .withCD(2)
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
  coolDown: Int = 1,
  attributeOffset: Attributes = Attributes.empty,
  attributesOffsetDuration: Int = 0
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
  def withStrengthOffset(o: Int): SkillBuilder = copy(attributeOffset = attributeOffset.add(Attributes.empty.copy(strength = o)))
  def withConstitutionOffset(o: Int): SkillBuilder = copy(attributeOffset = attributeOffset.add(Attributes.empty.copy(constitution = o)))
  def withSpellPowerOffset(o: Int): SkillBuilder = copy(attributeOffset = attributeOffset.add(Attributes.empty.copy(spellPower = o)))
  def withWillPowerOffset(o: Int): SkillBuilder = copy(attributeOffset = attributeOffset.add(Attributes.empty.copy(willPower = o)))
  def withDexterityOffset(o: Int): SkillBuilder = copy(attributeOffset = attributeOffset.add(Attributes.empty.copy(dexterity = o)))
  def withEvasionOffset(o: Int): SkillBuilder = copy(attributeOffset = attributeOffset.add(Attributes.empty.copy(evasion = o)))
  def withAttributesDuration(d: Int): SkillBuilder = copy(attributesOffsetDuration = d)

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
      0,
      attributeOffset,
      attributesOffsetDuration
    )
  }

}

private object Skills {
  var nextId = 0
}
