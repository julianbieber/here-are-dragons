alter table public.position add column "timestamp" timestamp not null default (now() at time zone 'utc');
alter table public.position drop constraint "position_id_key";