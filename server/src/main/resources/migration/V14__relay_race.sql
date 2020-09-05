create table public.relay_race (
     group_id varchar not null,
     users int[],
     start_timestamp timestamp not null,
     activity_id int not null references activity_type(id),
     end_timestamp timestamp default null,
     processed boolean default false,
     PRIMARY KEY (group_id, start_timestamp)
);

create table public.group_talents (
    id serial primary key,
    name varchar,
    skill_unlock int,
    next_talents int[],
    activity_id int,
    distance int default null,
    speed int default null,
    time int default null
);

create table public.group_talent_unlocks (
   users varchar primary key,
   currently_unlocking int default null,
   unlocked int[] default '{}'
);

insert into public.group_talents (name, skill_unlock, next_talents, activity_id, distance, time) VALUES ('Strength in Numbers', 21, '{2}', 1, 3000, 12);
insert into public.group_talents (name, skill_unlock, next_talents, activity_id, distance, time) VALUES ('Take Cover!', 22, '{3}', 1, 4000, 16);
insert into public.group_talents (name, skill_unlock, next_talents, activity_id, distance, time) VALUES ('Focus him!', 23, '{4}', 1, 5000, 20);
insert into public.group_talents (name, skill_unlock, next_talents, activity_id, distance, time) VALUES ('Recover!', 24, '{5}', 1, 6000, 23);
insert into public.group_talents (name, skill_unlock, next_talents, activity_id, distance, time) VALUES ('Taunt', 25, '{6}', 1, 7000, 26);
insert into public.group_talents (name, skill_unlock, next_talents, activity_id, distance, time) VALUES ('Weakling!', 26, '{}', 1, 8000, 30);