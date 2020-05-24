create table public.quest (
    id serial unique not null,
    longitude float8 not null,
    latitude float8 not null
)