create table public.talent_unlocks (
    user_id int primary key,
    currently_unlocking int default null,
    unlocked int[] default '{}'
);
