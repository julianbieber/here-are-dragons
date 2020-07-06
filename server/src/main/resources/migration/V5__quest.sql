create table public.quest (
    id bigint PRIMARY KEY,
    active_user_ids int[] default array[]::int[],
    activatable_user_ids int[] default array[]::int[]
);

