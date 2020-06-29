create table public.poi (
    id bigint unique not null,
    longitude float8 not null,
    latitude float8 not null,
    priority float8 not null,
    tags varchar
);
