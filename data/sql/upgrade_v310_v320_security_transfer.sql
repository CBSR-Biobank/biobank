-- add data in security tables:

-- convert users from csm to users from biobank:
-- add + 10 because groups (that are also principals) should be added. So far only one is added, but just in case we add more, add 10
set autocommit = 0;
start transaction;

insert into principal (id, version, discriminator, login, csm_user_id, recv_bulk_emails, full_name, email, need_pwd_change, activity_status_id)
       select user_id + 10, 0, 'User', login_name, user_id, 1, concat(first_name, ' ', last_name), null, 0, 1
       from csm_user
       where login_name != 'administrator'
       and login_name != 'bbadmin'
       and length(email_id) = 0;

insert into principal (id, version, discriminator, login, csm_user_id, recv_bulk_emails, full_name, email, need_pwd_change, activity_status_id)
       select user_id + 10, 0, 'User', login_name, user_id, 1, concat(first_name, ' ', last_name), email_id, 0, 1
       from csm_user
       where login_name != 'administrator'
       and login_name != 'bbadmin'
       and length(email_id) != 0;

set @group_super_admin = null;

select id from principal where name='Global Administrators' into @group_super_admin;

-- Old super admin group_id was 5 in csm database. Add previous super admin users in new super admin group:
insert into group_user(user_id, group_id)
select u.id, @group_super_admin from principal u, csm_user_group ug
where ug.group_id = 5 and ug.user_id = u.csm_user_id and u.discriminator='User';

-- give access to all object to all users (in csm):
insert into csm_user_group_role_pg (user_id, role_id, protection_group_id, update_date)
select user_id, 8, 1, sysdate() from csm_user;

-- delete olds csm_groups
delete from csm_group;

-- delete old csm_protection_group except for the one with id=1
delete from csm_protection_group where protection_group_id != 1 and parent_protection_group_id is not null;
delete from csm_protection_group where protection_group_id != 1 ;

-- delete old csm_protection_element for centers:
delete from csm_protection_element where object_id = 'edu.ualberta.med.biobank.model.Clinic' and attribute = 'id';
delete from csm_protection_element where object_id = 'edu.ualberta.med.biobank.model.Site' and attribute = 'id';
delete from csm_protection_element where object_id = 'edu.ualberta.med.biobank.model.ResearchGroup' and attribute = 'id';

-- delete old csm_role
delete from csm_role where role_id = 7;
delete from csm_role where role_id = 9;

commit;
