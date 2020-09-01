create table public.talents (
    id int primary key,
    name varchar,
    skill_unlock int,
    next_talents int[],
    activity_id int,
    distance int default null,
    speed int default null,
    time int default null,
    time_in_day int default null
);

-- distance alone: required distance for a single run/ride...
-- speed alone: the average speed over any distance must be above the speed
-- time alone: required time spend for a single run/ride
-- distance + speed: the maximum average speed over the distance must be above the speed
-- time_in_day: amount of minutes (seconds for pushups and pullups) spend on the activity in sum within the last 24h

insert into talents (id, name, skill_unlock, next_talents, activity_id, distance) values (1, 'Novice Spellcaster', 1, '{2, 4, 6}', 2, 5000); -- Smolder

insert into talents (id, name, skill_unlock, next_talents, activity_id, time) values (2, 'Novice Pyromancer', 2, '{3}', 2, 45); -- spark
insert into talents (id, name, skill_unlock, next_talents, activity_id, time) values (3, 'Pyromancer', 6, '{}', 2, 120); -- fireball


insert into talents (id, name, skill_unlock, next_talents, activity_id, speed) values (4, 'Novice Airomancer', 3, '{5}', 2, 15); -- Shock
insert into talents (id, name, skill_unlock, next_talents, activity_id, speed, distance) values (5, 'Airomancer', 7, '{}', 2, 25, 2000); -- Lightning

insert into talents (id, name, skill_unlock, next_talents, activity_id, distance) values (6, 'Novice Cryomancer', 4, '{}', 2, 15000); -- Rain
insert into talents (id, name, skill_unlock, next_talents, activity_id, distance) values (7, 'Cryomancer', 5, '{}', 2, 50000); -- Ice Shards

insert into talents (id, name, skill_unlock, next_talents, activity_id, time_in_day) values (8, 'Novice Fighter', 8, '{9, 11}', 3, 120); -- Slice
insert into talents (id, name, skill_unlock, next_talents, activity_id, time_in_day) values (9, 'Fighter', 9, '{10}', 3, 300); -- Flurry
insert into talents (id, name, skill_unlock, next_talents, activity_id, time_in_day) values (10, 'Precise Fighter', 10, '{}', 3, 600); -- Thrust
insert into talents (id, name, skill_unlock, next_talents, activity_id, time_in_day) values (11, 'Hard Hitter', 11, '{12}', 4, 300); -- Stun
insert into talents (id, name, skill_unlock, next_talents, activity_id, time_in_day) values (12, 'Aim for the Knees', 12, '{13}', 4, 600); -- Swipe Legs
insert into talents (id, name, skill_unlock, next_talents, activity_id, time_in_day) values (13, 'Recklessness', 13, '{}', 4, 1000); -- Throw Sword


insert into talents (id, name, skill_unlock, next_talents, activity_id, distance) values (14, 'Tracker', 14, '{15, 17}', 1, 2500); -- Shoot
insert into talents (id, name, skill_unlock, next_talents, activity_id, speed, distance) values (15, 'Fast Aim', 15, '{16}', 1, 12, 2500); -- Quickshot
insert into talents (id, name, skill_unlock, next_talents, activity_id, distance) values (16, 'Precise Aim', 17, '{18}', 1, 10000); -- Snipe
insert into talents (id, name, skill_unlock, next_talents, activity_id, time) values (17, 'Utility Arrows', 16, '{19}', 1, 60); -- Shock Shot
insert into talents (id, name, skill_unlock, next_talents, activity_id, speed, distance) values (18, 'More Arrows More Success', 18, '{}', 1, 15, 1000); -- Rain of Arrows
insert into talents (id, name, skill_unlock, next_talents, activity_id, distance) values (19, 'Do Arrows Burn?', 19, '{20}', 1, 20000); -- Burning Arrow
insert into talents (id, name, skill_unlock, next_talents, activity_id, time) values (20, 'Explosive Ammunition', 20, '{}', 1, 180); -- Explosive Shot





