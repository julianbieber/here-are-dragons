package service

import model.Dungeon.Skill

object DamageCalc {
  def apply(caster: DungeonUnit, target: DungeonUnit, skill: Skill): Int = {
    val damage =
      (caster.attributes.dexterity + caster.attributesOffsets.map(_._1.dexterity).sum) * skill.dexterityScaling +
      (caster.attributes.spellPower + caster.attributesOffsets.map(_._1.spellPower).sum) * skill.spellPowerScaling +
      (caster.attributes.strength + caster.attributesOffsets.map(_._1.strength).sum) * skill.strengthScaling +
      skill.damage.toFloat
    (damage * (50 / ((target.attributes.evasion + target.attributesOffsets.map(_._1.evasion).sum) + 5).toFloat) ).toInt
  }
}
