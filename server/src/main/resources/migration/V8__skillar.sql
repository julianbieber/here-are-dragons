create table skillbar (
    user_id bigint not null references users(id) unique,
    selected int[],
    unlocked int[]
)