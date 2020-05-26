create table public.quest (
    id serial unique not null,
    longitude float8 not null,
    latitude float8 not null
)
INSERT INTO public.SampleQuest  ( longitude, latitude) VALUES ( 24.968580,60.191880)
INSERT INTO public.SampleQuest  (longitude, latitude) VALUES ( 24.968589,60.191887)
INSERT INTO public.SampleQuest  ( longitude, latitude) VALUES ( 25.968580,60.191880)