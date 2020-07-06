CREATE EXTENSION intarray;
create table public.users (
    id serial unique primary key,
    name varchar unique not null,
    hash varchar not null
)