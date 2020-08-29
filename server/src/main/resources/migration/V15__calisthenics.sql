create table public.calisthenics (
    user_id int not null,
    calisthenics_type int,
    vector float4[],
    timestamp timestamp,
    processed boolean default false,
    PRIMARY KEY (user_id, timestamp)
);


