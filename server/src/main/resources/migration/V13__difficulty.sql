create table public.difficulty (
    difficulty int not null,
    userID int not null,
    unlockedInGroup boolean not null,
    dungeon boolean not null default false,
    "timestamp" timestamp not null default (now() at time zone 'utc'),
    groupMembers int[] default '{}'
);

