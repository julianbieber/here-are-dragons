create table public.quest (
    id bigint not null,
    ids bigint[] default '{}',
    --active_user_ids int[] default array[]::int[],
    userID int not null,
    --activatable_user_ids int[] default array[]::int[]
    progres int not null default 0,
    difficulty int not null default 0,

    -- If this Boolean is true, the quest is active, if it is false it is activatable
    activ boolean not null,
    "timestamp" timestamp not null default (now() at time zone 'utc'),
    PRIMARY KEY (id, userID)
);

