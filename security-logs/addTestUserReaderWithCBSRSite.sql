set collation_connection ='latin1_general_cs';

set @USER_NAME = 'testreader';
set @PASSWORD = 'orDBlaojDQE=';
set @GROUP_NAME = 'CBSR Technician Level 1';
set @GROUP_DISCRIMINATOR ='BbGroup';
set @USER_DISCRIMINATOR ='User';
set @READPERM='READ_ALL';

-- create csm_user
insert into csm_user (USER_ID, LOGIN_NAME, MIGRATED_FLAG, FIRST_NAME, LAST_NAME, PASSWORD, UPDATE_DATE)
       select coalesce(MAX(id), 0)+Max(user_id)+1, @USER_NAME, 0 , @USER_NAME, @USER_NAME, @PASSWORD, sysdate()
       from principal, csm_user;

-- create principal user
insert into principal (id, version, discriminator, login, csm_user_id, recv_bulk_emails, full_name, email, need_pwd_change, activity_status_id)
       select coalesce(MAX(id), 0)+1, 0, @USER_DISCRIMINATOR, login_name, user_id, 1, concat(first_name, ' ', last_name), email_id, 0, 1
       from csm_user, principal
       where login_name collate latin1_general_cs = @USER_NAME;

-- CSM protection group (should be removed eventually)
insert into csm_user_group_role_pg (user_id, role_id, protection_group_id, update_date)
       select user_id, 8, 1, sysdate()
       from csm_user
       where login_name collate latin1_general_cs = @USER_NAME;

-- add limited access group
insert into principal (id, version, discriminator, name, activity_status_id)
       select coalesce(MAX(p.id), 0)+1, 0, @GROUP_DISCRIMINATOR, @GROUP_NAME, 1
       from principal p;

-- add user to group
insert into group_user(user_id, group_id)
       select u.id, g.id
       from principal u, principal g
       where u.login collate latin1_general_cs =@USER_NAME
       and g.name collate latin1_general_cs =@GROUP_NAME;

-- create a new domain	   
insert into domain (id, version, all_centers, all_studies)
		select coalesce(max(d.id),0)+1, 0, 0, 1 
		from domain d;
		
-- give this domain cbsr access
insert into domain_center (domain_id, center_id)
		select max(d.id), 34
		from domain d;
		
	   
-- add the groups membership to the domain
insert into membership (id, version, every_permission, user_manager, domain_id, principal_id)
       select coalesce(MAX(ms.id), 0)+1, 0, 0, 0, max(d.id), max(p.id)
       from membership as ms, principal as p, domain as d;

-- add a role
insert into role (id, version, name)
		select coalesce(MAX(r.id), 0)+1, 0, @READPERM
       from role r;

-- Add read permissions to the role
create temporary table TECHNICIAN_READ_ALL (perm int);
insert into TECHNICIAN_READ_ALL values (3),(9),(13),(18),(22),(26),(30),(35),(39),(43),(48),(53),(57),(61),(64),(65),(66),(67);
insert into role_permission (id, permission_id)
       select coalesce((select MAX(r.id) from role r), 0), t.perm
       from TECHNICIAN_READ_ALL t;

-- add the role to the membership
insert into membership_role (membership_id, role_id)
       select max(m.id), max(r.id) from membership m, role r;
