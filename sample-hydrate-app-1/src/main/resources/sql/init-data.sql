with ben as (
    insert into tbl_member (full_name, email_addr, city, state_prov, date_created)
    values ('big ben', 'bigben@email.com', 'London', 'Greater London, UK', now())
    on conflict do nothing
    returning id
)
insert into tbl_account (username, access_code, member_id, date_created)
select 'bigben', 'bigben', ben.id, now() from ben
on conflict (username) do nothing;

with jane as (
    insert into tbl_member (full_name, email_addr, city, state_prov, date_created)
    values ('jane doe', 'janedoe@email.com', 'New York', 'NY', now())
    on conflict do nothing
    returning id
)
insert into tbl_account (username, access_code, member_id, date_created)
select 'janedoe', 'janedoe', jane.id, now() from jane
on conflict (username) do nothing;

with james as (
    insert into tbl_member (full_name, email_addr, city, state_prov, date_created)
    values ('james brown', 'jamesbrown@email.com', 'Memphis', 'TN', now())
    on conflict do nothing
    returning id
)
insert into tbl_account (username, access_code, member_id, date_created)
select 'jamesbrown', 'jamesbrown', james.id, now() from james
on conflict (username) do nothing;

with steve as (
    insert into tbl_member (full_name, email_addr, city, state_prov, date_created)
    values ('steve mikes', 'stevemikes@email.com', 'Chicago', 'IL', now())
    on conflict do nothing
    returning id
)
insert into tbl_account (username, access_code, member_id, date_created)
select 'stevemikes', 'stevemikes', steve.id, now() from steve
on conflict (username) do nothing;

insert into tbl_club (title, activity, date_created)
values ('hunting club', 'game hunting', now())
on conflict (title) do nothing;

insert into tbl_club (title, activity, date_created)
values ('book club', 'reading', now())
on conflict (title) do nothing;

insert into tbl_club (title, activity, date_created)
values ('dog club', 'training dogs', now())
on conflict (title) do nothing;

insert into tbl_club (title, activity, date_created)
values ('fitness club', 'fitness and motivation', now())
on conflict (title) do nothing;

with hunting as (
    select id from tbl_club where title = 'hunting club'
)
insert into tbl_membership (club_id, member_id, member_alias, date_created)
select hunting.id, (select id from tbl_member where email_addr = 'stevemikes@email.com'), 'hunting-stevemikes', now() from hunting
union
select hunting.id, (select id from tbl_member where email_addr = 'jamesbrown@email.com'), 'hunting-jamesbrown', now() from hunting
on conflict(member_alias) do nothing;

with bookie as (
    select id from tbl_club where title = 'book club'
)
insert into tbl_membership (club_id, member_id, member_alias, date_created)
select bookie.id, (select id from tbl_member where email_addr = 'stevemikes@email.com'), 'bookie-stevemikes', now() from bookie
union
select bookie.id, (select id from tbl_member where email_addr = 'janedoe@email.com'), 'bookie-janedoe', now() from bookie
on conflict(member_alias) do nothing;

with dogie as (
    select id from tbl_club where title = 'dog club'
)
insert into tbl_membership (club_id, member_id, member_alias, date_created)
select dogie.id, (select id from tbl_member where email_addr = 'bigben@email.com'), 'dogie-bigben', now() from dogie
union
select dogie.id, (select id from tbl_member where email_addr = 'janedoe@email.com'), 'dogie-janedoe', now() from dogie
on conflict(member_alias) do nothing;

with fitness as (
    select id from tbl_club where title = 'fitness club'
)
insert into tbl_membership (club_id, member_id, member_alias, date_created)
select fitness.id, (select id from tbl_member where email_addr = 'bigben@email.com'), 'fitness-bigben', now() from fitness
union
select fitness.id, (select id from tbl_member where email_addr = 'jamesbrown@email.com'), 'fitness-jamesbrown', now() from fitness
on conflict(member_alias) do nothing;