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

create table public.group_talent_unlocks (
   users varchar primary key,
   currently_unlocking int default null,
   unlocked int[] default '{}'
);

