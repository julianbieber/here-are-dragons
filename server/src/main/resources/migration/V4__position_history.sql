alter table public.position add column "timestamp" timestamp not null default (now() at time zone 'utc');
alter table public.position drop constraint "position_id_key";

create table experiences (
    userid int not null references users(id),
    activity_id int not null references activity_type(id),
    amount bigint,
    PRIMARY KEY (userid, activity_id)
)