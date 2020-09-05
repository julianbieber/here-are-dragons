create table public.difficulty (
    id serial primary key,
    difficulty int not null,
    user_id int not null,
    unlocked_in_group boolean not null,
    dungeon boolean not null default false,
    "timestamp" timestamp not null default (now() at time zone 'utc'),
    group_members int[] default '{}'
);

