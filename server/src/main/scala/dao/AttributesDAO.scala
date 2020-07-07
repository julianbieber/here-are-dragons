package dao

import javax.inject.Inject
import model.Character.Attributes
import scalikejdbc._

class AttributesDAO @Inject() (val pool: ConnectionPool) extends SQLUtil {

  def storeAttributes(userId: Int, selected: Attributes, unlocked: Attributes, max: Int): Option[AttributesTable] = {
    val cappedMax = math.min(AttributesDAO.globalMax, max)
    if (selected.check(cappedMax) && selected.check(unlocked)) {

      withSession(pool) { implicit session =>
        sql"insert into public.attributes (user_id, selected_strength, selected_constitution, selected_spell_power, selected_will_power, selected_dexterity, selected_evasion, unlocked_strength, unlocked_constitution, unlocked_spell_power, unlocked_will_power, unlocked_dexterity, unlocked_evasion, max_attributes) VALUES (${userId}, ${selected.strength}, ${selected.constitution}, ${selected.spellPower}, ${selected.willpower}, ${selected.dexterity}, ${selected.evasion}, ${unlocked.strength}, ${unlocked.constitution}, ${unlocked.spellPower}, ${unlocked.willpower}, ${unlocked.dexterity}, ${unlocked.evasion}, ${cappedMax}) ON CONFLICT (user_id) DO UPDATE SET selected_strength = excluded.selected_strength, selected_constitution= excluded.selected_constitution, selected_spell_power= excluded.selected_spell_power, selected_will_power= excluded.selected_will_power, selected_dexterity= excluded.selected_dexterity, selected_evasion= excluded.selected_evasion, unlocked_strength= excluded.unlocked_strength, unlocked_constitution= excluded.unlocked_constitution, unlocked_spell_power= excluded.unlocked_spell_power, unlocked_will_power= excluded.unlocked_will_power, unlocked_dexterity= excluded.unlocked_dexterity, unlocked_evasion= excluded.unlocked_evasion, max_attributes= excluded.max_attributes".executeUpdate().apply()
      }

      Option(AttributesTable(userId, selected, unlocked, cappedMax))
    } else {
      None
    }
  }

  def readAttributes(userId: Int): Option[AttributesTable] = {
    withReadOnlySession(pool) { implicit session =>
      sql"select selected_strength, selected_constitution, selected_spell_power, selected_will_power, selected_dexterity, selected_evasion, unlocked_strength, unlocked_constitution, unlocked_spell_power, unlocked_will_power, unlocked_dexterity, unlocked_evasion, max_attributes, max_attributes from public.attributes where user_id = $userId".map{ attributesRow =>
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
          attributesRow.int("max_attributes")
        )
      }.first().apply()
    }
  }


}

object AttributesDAO {
  private[dao] val globalMax = 100
}

case class AttributesTable(userId: Int, selected: Attributes, unlocked: Attributes, max: Int)
