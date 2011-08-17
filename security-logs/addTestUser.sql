INSERT INTO csm_user (LOGIN_NAME, MIGRATED_FLAG, FIRST_NAME, LAST_NAME, PASSWORD, UPDATE_DATE)
VALUES ('testuser',0,'testuser','testuser','orDBlaojDQE=',sysdate());

insert into principal (id, version)  
select user_id, 0 from csm_user where login_name = 'testuser';

insert into user (principal_id, login, csm_user_id, is_super_admin, bulk_emails, first_name, last_name, email, need_change_pwd) 
select user_id, login_name, user_id, 1, 1, first_name, last_name, email_id, 0 from csm_user where login_name = 'testuser';

insert into csm_user_group_role_pg (user_id, role_id, protection_group_id, update_date)
select user_id, 8, 1, sysdate() from csm_user where login_name = 'testuser';


