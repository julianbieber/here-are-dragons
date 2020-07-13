create table public.quest (
    id bigint not null,
    --active_user_ids int[] default array[]::int[],
    userID int not null,
    --activatable_user_ids int[] default array[]::int[]

    -- If this Boolean is true, the quest is active, if it is false it is activatable
    activ boolean not null

);

