create table public.activity_type (
    id serial primary key,
    name varchar not null
);

insert into public.activity_type (name)
(values ('RUNNING'), ('CYCLING'), ('OTHER'));

create table public.activity (
    userid int not null references users(id),
    start_timestamp timestamp not null,
    activity_id int not null references activity_type(id),
    end_timestamp timestamp default null,
    processed boolean default false,
    PRIMARY KEY (userid, start_timestamp)
)