--create users and associated accounts
with sam as (
insert into tbl_user (first_name, last_name, email_address, addr_city, addr_state_prov, addr_zip_code)
values ('sam', 'bazar', 'sam.bazar@email.com', 'Chicago', 'IL', '60600')
on conflict (email_address) do nothing returning id)
insert into tbl_account (user_name, access_code, user_id)
select 'sambazar', 'sam_code', sam.id from sam;

with pete as (
insert into tbl_user (first_name, last_name, email_address, addr_city, addr_state_prov, addr_zip_code)
values ('pete', 'maji', 'pete.maji@email.com', 'Milwaukee', 'WI', '56788')
on conflict (email_address) do nothing returning id)
insert into tbl_account (user_name, access_code, user_id)
select 'petemaji', 'pete_code', pete.id from pete;

with pam as (
insert into tbl_user (first_name, last_name, email_address, addr_city, addr_state_prov, addr_zip_code)
values ('pam', 'shika', 'pam.shika@email.com', 'Dalla', 'TX', '78845')
on conflict (email_address) do nothing returning id)
insert into tbl_account (user_name, access_code, user_id)
select 'pamshika', 'pam_code', pam.id from pam;

-- create tasks
insert into tbl_task (name) values ('put in park') on conflict (name) do nothing;
insert into tbl_task (name) values ('get jerk') on conflict (name) do nothing;
insert into tbl_task (name) values ('get spanner') on conflict (name) do nothing;
insert into tbl_task (name) values ('position jerk') on conflict (name) do nothing;
insert into tbl_task (name) values ('loosen nuts') on conflict (name) do nothing;
insert into tbl_task (name) values ('lift vehicle') on conflict (name) do nothing;
insert into tbl_task (name) values ('remove nuts') on conflict (name) do nothing;
insert into tbl_task (name) values ('remove tire') on conflict (name) do nothing;
insert into tbl_task (name) values ('put spare tire') on conflict (name) do nothing;
insert into tbl_task (name) values ('screw on nuts') on conflict (name) do nothing;
insert into tbl_task (name) values ('lower vehicle') on conflict (name) do nothing;
insert into tbl_task (name) values ('tighten nuts') on conflict (name) do nothing;
insert into tbl_task (name) values ('put back spanner') on conflict (name) do nothing;
insert into tbl_task (name) values ('put back jerk') on conflict (name) do nothing;

-- update next_task relationships
update tbl_task set next_task = (select id from tbl_task where name = 'get jerk') where name = 'put in park';
update tbl_task set next_task = (select id from tbl_task where name = 'get spanner') where name = 'get jerk';
update tbl_task set next_task = (select id from tbl_task where name = 'position jerk') where name = 'get spanner';
update tbl_task set next_task = (select id from tbl_task where name = 'loosen nuts') where name = 'position jerk';
update tbl_task set next_task = (select id from tbl_task where name = 'lift vehicle') where name = 'loosen nuts';
update tbl_task set next_task = (select id from tbl_task where name = 'remove nuts') where name = 'lift vehicle';
update tbl_task set next_task = (select id from tbl_task where name = 'remove tire') where name = 'remove nuts';
update tbl_task set next_task = (select id from tbl_task where name = 'put spare tire') where name = 'remove tire';
update tbl_task set next_task = (select id from tbl_task where name = 'screw on nuts') where name = 'put spare tire';
update tbl_task set next_task = (select id from tbl_task where name = 'lower vehicle') where name = 'screw on nuts';
update tbl_task set next_task = (select id from tbl_task where name = 'tighten nuts') where name = 'lower vehicle';
update tbl_task set next_task = (select id from tbl_task where name = 'put back spanner') where name = 'tighten nuts';
update tbl_task set next_task = (select id from tbl_task where name = 'put back jerk') where name = 'put back spanner';

-- update parent_task relationships
update tbl_task set parent_task = (select id from tbl_task where name = 'put in park') where name in ('get jerk', 'get spanner', 'position jerk');
update tbl_task set parent_task = (select id from tbl_task where name = 'position jerk') where name = 'loosen nuts';
update tbl_task set parent_task = (select id from tbl_task where name = 'loosen nuts') where name = 'lift vehicle';
update tbl_task set parent_task = (select id from tbl_task where name = 'lift vehicle') where name = 'remove nuts';
update tbl_task set parent_task = (select id from tbl_task where name = 'remove nuts') where name = 'remove tire';
update tbl_task set parent_task = (select id from tbl_task where name = 'remove tire') where name = 'put spare tire';
update tbl_task set parent_task = (select id from tbl_task where name = 'put spare tire') where name = 'screw on nuts';
update tbl_task set parent_task = (select id from tbl_task where name = 'screw on nuts') where name = 'lower vehicle';
update tbl_task set parent_task = (select id from tbl_task where name = 'lower vehicle') where name = 'tighten nuts';
update tbl_task set parent_task = (select id from tbl_task where name = 'tighten nuts') where name in ('put back jerk', 'put back spanner');

-- create task assignments
insert into tbl_assignment (task_id, assignee_id)
select id as task_id, (select id as assignee_id from tbl_user where email_address = 'sam.bazar@email.com')
from tbl_task where name in ('put in park', 'get jerk', 'get spanner', 'position jerk', 'loosen nuts') on conflict (task_id, assignee_id) do nothing;

insert into tbl_assignment (task_id, assignee_id)
select id as task_id, (select id as assignee_id from tbl_user where email_address = 'pete.maji@email.com')
from tbl_task where name in ('lift vehicle', 'remove nuts', 'remove tire', 'put spare tire', 'screw on nuts') on conflict (task_id, assignee_id) do nothing;

insert into tbl_assignment (task_id, assignee_id)
select id as task_id, (select id as assignee_id from tbl_user where email_address = 'pam.shika@email.com')
from tbl_task where name in ('lower vehicle', 'tighten nuts', 'put back jerk', 'put back spanner') on conflict (task_id, assignee_id) do nothing;
