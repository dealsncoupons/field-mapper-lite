CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

delete table tbl_task if exists;

create table tbl_task (
    id UUID default uuid_generate_v1(),
    name varchar(64) not null,
    completed boolean default false,
    date_created timestamp default now(),
    parent_task UUID,
    constraint pk_task_id primary key(id),
    constraint uniq_task_name unique(name),
    constraint fk_parent_task_id foreign key(parent_task) references tbl_task(id),
);