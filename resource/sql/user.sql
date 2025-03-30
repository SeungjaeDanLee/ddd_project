create table user
(
    id          binary(16)  not null comment 'UUID for user'
        primary key,
    name        varchar(32) null,
    age         int         null,
    create_date datetime    null,
    update_date datetime    null
);