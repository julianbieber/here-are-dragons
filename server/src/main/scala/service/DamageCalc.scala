package service

import model.Dungeon.Skill

object DamageCalc {
  def apply(caster: DungeonUnit, target: DungeonUnit, skill: Skill): Int = {
    val damage = caster.attributes.dexterity * skill.dexterityScaling + caster.attributes.spellPower * skill.spellPowerScaling + caster.attributes.strength * skill.strengthScaling + skill.damage
    (damage / target.attributes.evasion * 100).toInt
  }
}
