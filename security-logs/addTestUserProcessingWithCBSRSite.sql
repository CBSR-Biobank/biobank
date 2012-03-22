set collation_connection ='latin1_general_cs';

set @USER_NAME = 'testprocessor';
set @PASSWORD = 'orDBlaojDQE=';
set @GROUP_NAME = 'CBSR Technician Level 2';
set @GROUP_DISCRIMINATOR ='BbGroup';
set @USER_DISCRIMINATOR ='User';
set @PROCESS = 'Process';

-- create csm_user
insert into csm_user (USER_ID, LOGIN_NAME, MIGRATED_FLAG, FIRST_NAME, LAST_NAME, PASSWORD, UPDATE_DATE)
       select coalesce(MAX(id), 0)+Max(user_id)+1, @USER_NAME, 0 , @USER_NAME, @USER_NAME, @PASSWORD, sysdate()
       from principal, csm_user;

-- create principal user
insert into principal (id, version, discriminator, login, csm_user_id, recv_bulk_emails, full_name, email, need_pwd_change, activity_status_id)
       select coalesce(MAX(id), 0)+1, 0, @USER_DISCRIMINATOR, login_name, user_id, 1, concat(first_name, ' ', last_name), email_id, 0, 1
       from csm_user,principal
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

-- add a group membership for site CBSR (id = 34)
insert into membership (id, version, center_id, not_null_center_id, not_null_study_id, principal_id,rank,level)
       select coalesce(MAX(ms.id), 0)+1, 0, 34, 34, max(p.id) ,2,999
       from membership as ms, principal as p;

-- add a role
insert into role (id, version, name)
       select coalesce(MAX(r.id), 0)+1, 0, @PROCESS
       from role r;

-- Add read permissions to the role
create temporary table TECHNICIAN_PROCESS (perm int);
insert into TECHNICIAN_PROCESS values (2), (3), (5), (6), (7), (12), (13), (14), (15), (17), (18), (19), (20), (21), (22),
 (23), (24), (25), (26), (27), (28), (29), (30), (31), (32), (33), (43), (44), (46), (66), (67);
insert into role_permission (id, permission_id)
       select coalesce(MAX(r.id), 0), t.perm
       from role r, TECHNICIAN_PROCESS t;

-- add the role to the membership
insert into membership_role (membership_id, role_id)
	select max(m.id), max(r.id) from membership m, role r;

