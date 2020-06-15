create table public.quest (
    id serial unique not null,
    longitude float8 not null,
    latitude float8 not null,
    erledigt bool not null
);
INSERT INTO public.quest  ( longitude, latitude, erledigt) VALUES ( 24.968580,60.191880,false);
INSERT INTO public.quest  (longitude, latitude,erledigt) VALUES ( 25.968589,60.191887,false);
INSERT INTO public.quest  (longitude, latitude,erledigt) VALUES ( 24.968589,59.191887,false);
