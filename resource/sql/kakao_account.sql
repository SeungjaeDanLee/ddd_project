create table kakao_account
(
    id      varchar(128) not null
        primary key,
    user_id binary(16)   null,
    constraint kakao_account_user_uuid_fk
        foreign key (user_id) references user (id)
);