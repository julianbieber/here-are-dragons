package dao

import model.Dungeon.Skill

object SkillDAO {
  val skills = IndexedSeq(
    SkillBuilder
      .create("Fireball")
      .withTargetPattern("11111")
      .withEffectPattern("111")
      .withDamage(20)
      .withBurn(2)
      .withCost(2)
      .build(),
    SkillBuilder
      .create("Walk")
      .withTargetPattern("101")
      .withCost(1)
      .movesTo(0)
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
  burnDuration: Int = 0,
  moves: Boolean = false,
  movementOffset: Int = 0
) {
  def withTargetPattern(p: String): SkillBuilder = copy(targetPattern = p)
  def withEffectPattern(p: String): SkillBuilder = copy(effectPattern = p)
  def withCost(c: Int): SkillBuilder = copy(apCost = c)
  def withDamage(d: Int): SkillBuilder = copy(damage = d)
  def withBurn(d: Int): SkillBuilder = copy(burnDuration = d)
  def movesTo(offset: Int): SkillBuilder = copy(moves = true, movementOffset = offset)

  def build(): Skill = {
    Skill(
      id,
      name,
      targetPattern,
      effectPattern,
      apCost,
      damage,
      burnDuration,
      moves,
      movementOffset
    )
  }

}

private object Skills {
  var nextId = 0
}
