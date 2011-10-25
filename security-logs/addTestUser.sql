INSERT INTO csm_user (USER_ID, LOGIN_NAME, MIGRATED_FLAG, FIRST_NAME, LAST_NAME, PASSWORD, UPDATE_DATE)
select (select coalesce(MAX(id), 0)+Max(user_id)+1 from principal, csm_user), 'testuser', 0 , 'testuser', 'testuser', 'orDBlaojDQE=', sysdate();

insert into principal (id, version)
select user_id, 0 from csm_user where login_name = 'testuser';

insert into user (principal_id, login, csm_user_id, bulk_emails, full_name, email, need_change_pwd)
select user_id, login_name, user_id, 1, first_name + ' ' + last_name, email_id, 0 from csm_user where login_name = 'testuser';

insert into csm_user_group_role_pg (user_id, role_id, protection_group_id, update_date)
select user_id, 8, 1, sysdate() from csm_user where login_name = 'testuser';

-- add testuser to group Super Administrators
insert into group_user(user_id, group_id)
select u.principal_id, g.principal_id from user u, bb_group g where u.login='testuser' and g.name='Super Administrators';
