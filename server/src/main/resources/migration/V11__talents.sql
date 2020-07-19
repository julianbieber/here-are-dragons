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
-- time_in_day: amount of minutes spend on the activity in sum from 00:00:00 UTC to 23:59:59.999UTC

insert into talents (id, name, skill_unlock, next_talents, activity_id, distance) values (1, 'Novice Spellcaster', 0, '{2, 3, 4}', 2, 5000);
insert into talents (id, name, skill_unlock, next_talents, activity_id, time) values (2, 'Novice Pyromancer', 1, '{}', 2, 45);
insert into talents (id, name, skill_unlock, next_talents, activity_id, speed) values (3, 'Novice Airomancer', 2, '{}', 2, 12);
insert into talents (id, name, skill_unlock, next_talents, activity_id, distance) values (4, 'Novice Cryomancer', 3, '{}', 2, 10000);

insert into talents (id, name, skill_unlock, next_talents, activity_id, distance) values (5, 'Tracker', 4, '{}', 1, 1000);




