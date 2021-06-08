CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

drop table if exists tbl_assignment;
drop table if exists tbl_task;
drop table if exists tbl_account;
drop table if exists tbl_user;

create table tbl_user (
    id UUID default uuid_generate_v1(),
    first_name varchar(64),
    last_name varchar(64),
    email_address varchar(128),
    addr_city varchar(64),
    addr_state_prov varchar(64),
    addr_zip_code varchar(64),
    date_created timestamp default now(),
    constraint uniq_email unique (email_address),
    constraint pk_user_id primary key(id)
);

create table tbl_account (
    id UUID default uuid_generate_v1(),
    user_name varchar(64),
    access_code varchar(256),
    user_id UUID,
    date_created timestamp default now(),
    constraint pk_account_id primary key(id),
    constraint fk_user_id foreign key(user_id) references tbl_user(id)
);

create table tbl_task (
    id UUID default uuid_generate_v1(),
    name varchar(64) not null,
    done boolean default false,
    date_created timestamp default now(),
    next_task UUID,
    parent_task UUID,
    constraint pk_task_id primary key(id),
    constraint uniq_task_name unique(name),
    constraint fk_next_task_id foreign key(next_task) references tbl_task(id),
    constraint fk_parent_task_id foreign key(parent_task) references tbl_task(id)
);

create table tbl_assignment (
    id UUID default uuid_generate_v1(),
    task_id UUID not null,
    assignee_id UUID not null,
    date_assigned timestamp default now(),
    constraint pk_assignment_id primary key(id),
    constraint uniq_task_assignment unique(task_id, assignee_id),
    constraint fk_task_id foreign key(task_id) references tbl_task(id),
    constraint fk_assignee_id foreign key(assignee_id) references tbl_user(id)
);