drop table if exists tbl_membership;
drop table if exists tbl_club;
drop table if exists tbl_account;
drop table if exists tbl_member;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists tbl_member (
    id UUID default uuid_generate_v1(),
    full_name varchar(64) not null,
    email_addr varchar(256) not null,
    city varchar(64),
    state_prov varchar(64),
    zip_code varchar(10),
    date_created timestamp not null,
    last_updated timestamp default current_timestamp,
    constraint pk_member_id primary key(id),
    constraint uniq_member_email unique(email_addr)
);

create table if not exists tbl_account (
    id UUID default uuid_generate_v1(),
    username varchar(64) not null,
    access_code varchar(256) not null,
    member_id UUID not null,
    date_created timestamp not null,
    last_updated timestamp default current_timestamp,
    constraint pk_account_id primary key(id),
    constraint fk_member_id foreign key(member_id) references tbl_member(id),
    constraint uniq_username unique(username)
);

create table if not exists tbl_club (
    id UUID default uuid_generate_v1(),
    title varchar(64) not null,
    activity varchar(256) not null,
    date_created timestamp not null,
    last_updated timestamp default current_timestamp,
    constraint pk_club_id primary key(id),
    constraint uniq_club_title unique(title)
);

create table if not exists tbl_membership (
    id UUID default uuid_generate_v1(),
    member_id UUID not null,
    club_id UUID not null,
    member_alias varchar(32) not null,
    member_status varchar(32) default 'ok',
    date_created timestamp not null,
    last_updated timestamp default current_timestamp,
    constraint pk_membership_id primary key(id),
    constraint fk_member_id foreign key(member_id) references tbl_member(id),
    constraint fk_club_id foreign key(club_id) references tbl_club(id),
    constraint uniq_club_member unique(member_id, club_id),
    constraint uniq_member_alias unique(member_alias)
);