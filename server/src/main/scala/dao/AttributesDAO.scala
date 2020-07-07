package dao

import javax.inject.Inject
import model.Character.Attributes
import scalikejdbc._

class AttributesDAO @Inject() (val pool: ConnectionPool) extends SQLUtil {

  def storeAttributes(userId: Int, selected: Attributes, unlocked: Attributes, level: Int): AttributesTable = {
    withSession(pool) { implicit session =>
      sql"insert into public.attributes (user_id, selected_strength, selected_constitution, selected_spell_power, selected_will_power, selected_dexterity, selected_evasion, unlocked_strength, unlocked_constitution, unlocked_spell_power, unlocked_will_power, unlocked_dexterity, unlocked_evasion, level) VALUES (${userId}, ${selected.strength}, ${selected.constitution}, ${selected.spellPower}, ${selected.willPower}, ${selected.dexterity}, ${selected.evasion}, ${unlocked.strength}, ${unlocked.constitution}, ${unlocked.spellPower}, ${unlocked.willPower}, ${unlocked.dexterity}, ${unlocked.evasion}, ${level}) ON CONFLICT (user_id) DO UPDATE SET selected_strength = excluded.selected_strength, selected_constitution= excluded.selected_constitution, selected_spell_power= excluded.selected_spell_power, selected_will_power= excluded.selected_will_power, selected_dexterity= excluded.selected_dexterity, selected_evasion= excluded.selected_evasion, unlocked_strength= excluded.unlocked_strength, unlocked_constitution= excluded.unlocked_constitution, unlocked_spell_power= excluded.unlocked_spell_power, unlocked_will_power= excluded.unlocked_will_power, unlocked_dexterity= excluded.unlocked_dexterity, unlocked_evasion= excluded.unlocked_evasion, level = excluded.level".executeUpdate().apply()
    }

    AttributesTable(userId, selected, unlocked, level)
  }

  def readAttributes(userId: Int): AttributesTable = {
    withReadOnlySession(pool) { implicit session =>
      sql"select selected_strength, selected_constitution, selected_spell_power, selected_will_power, selected_dexterity, selected_evasion, unlocked_strength, unlocked_constitution, unlocked_spell_power, unlocked_will_power, unlocked_dexterity, unlocked_evasion, level from public.attributes where user_id = $userId".map{ attributesRow =>
        val selected = Attributes(
          attributesRow.int("selected_strength"),
          attributesRow.int("selected_constitution"),
          attributesRow.int("selected_spell_power"),
          attributesRow.int("selected_will_power"),
          attributesRow.int("selected_dexterity"),
          attributesRow.int("selected_evasion")
        )
        val unlocked = Attributes(
          attributesRow.int("unlocked_strength"),
          attributesRow.int("unlocked_constitution"),
          attributesRow.int("unlocked_spell_power"),
          attributesRow.int("unlocked_will_power"),
          attributesRow.int("unlocked_dexterity"),
          attributesRow.int("unlocked_evasion")
        )

        AttributesTable(
          userId,
          selected,
          unlocked,
          attributesRow.int("level")
        )
      }.first().apply().getOrElse(AttributesTable(userId, Attributes.empty, Attributes.empty, 0))
    }
  }


}

object AttributesDAO {
  private[dao] val globalMax = 100
}

case class AttributesTable(userId: Int, selected: Attributes, unlocked: Attributes, level: Int)
