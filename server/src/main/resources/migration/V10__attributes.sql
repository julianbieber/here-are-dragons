create table public.attributes (
    user_id int primary key,
    selected_strength int default 0,
    selected_constitution int default 0,
    selected_spell_power int default 0,
    selected_will_power int default 0,
    selected_dexterity int default 0,
    selected_evasion int default 0,
    unlocked_strength int default 0,
    unlocked_constitution int default 0,
    unlocked_spell_power int default 0,
    unlocked_will_power int default 0,
    unlocked_dexterity int default 0,
    unlocked_evasion int default 0,
    level int default 0
);