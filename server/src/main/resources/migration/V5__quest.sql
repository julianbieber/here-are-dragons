create table public.quest (
    id bigint unique not null,
    longitude float8 not null,
    latitude float8 not null,
    erledigt bool not null
);
/*INSERT INTO public.quest  (longitude, latitude, erledigt) VALUES ( 24.968580,60.191880,false);*/
