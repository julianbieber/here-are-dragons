create table public.player_characters(
    user_id bigint not null references users(id) unique,
    max_health int not null
)