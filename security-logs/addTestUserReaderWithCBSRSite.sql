-- create csm_user
INSERT INTO csm_user (USER_ID, LOGIN_NAME, MIGRATED_FLAG, FIRST_NAME, LAST_NAME, PASSWORD, UPDATE_DATE)
       select coalesce(MAX(id), 0)+Max(user_id)+1, 'testreader', 0 , 'testreader', 'testreader', 'orDBlaojDQE=', sysdate()
       from principal, csm_user;

-- create principal user
insert into principal (id, version, discriminator, login, csm_user_id, recv_bulk_emails, full_name, email, need_pwd_change,activity_status_id)
       select user_id, 0, 'User', login_name, user_id, 1, concat(first_name, ' ', last_name), email_id, 0, 1
       from csm_user
       where login_name = 'testreader';

-- CSM protection group (should be removed eventually)
insert into csm_user_group_role_pg (user_id, role_id, protection_group_id, update_date)
       select user_id, 8, 1, sysdate()
       from csm_user
       where login_name = 'testreader';

-- add limited access group 'Technician Level 1'
insert into principal values ('BbGroup', 157, 0, 1, NULL, 'Technician Level 1', NULL, NULL, NULL, NULL, NULL, NULL);	   
	   
-- add testuser to group 'Technician Level 1'
insert into group_user(user_id, group_id)
       select u.id, g.id
       from principal u, principal g
       where u.login='testreader'
       and g.name='Technician Level 1';

-- add a Technician Level 1 membership for site CBSR (id = 34)
insert into membership (id, version, center_id, NOT_NULL_CENTER_ID, principal_id,rank,level)
       select coalesce(MAX(ms.id), 0)+1, 0, 34, 34, 157,2,999
       from membership as ms;
	   
-- Add read permissions for all objects
create temporary table TECHNICIAN_READ_ALL (perm int);
insert into TECHNICIAN_READ_ALL values (3),(9),(13),(18),(22),(26),(30),(35),(39),(43),(48),(53),(57),(61),(64),(65),(66),(67);
insert into membership_permission (id, permission_id)
		select (select max(ms.id) from membership ms), t.perm 
		from TECHNICIAN_READ_ALL t
