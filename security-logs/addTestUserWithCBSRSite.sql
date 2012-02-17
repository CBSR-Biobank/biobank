INSERT INTO csm_user (USER_ID, LOGIN_NAME, MIGRATED_FLAG, FIRST_NAME, LAST_NAME, PASSWORD, UPDATE_DATE)
       select coalesce(MAX(id), 0)+Max(user_id)+1, 'testuser', 0 , 'testuser', 'testuser', 'orDBlaojDQE=', sysdate()
       from principal, csm_user;

insert into principal (id, version, discriminator, login, csm_user_id, recv_bulk_emails, full_name, email, need_pwd_change,activity_status_id)
       select user_id, 0, 'User', login_name, user_id, 1, concat(first_name, ' ', last_name), email_id, 0, 1
       from csm_user
       where login_name = 'testuser';

insert into csm_user_group_role_pg (user_id, role_id, protection_group_id, update_date)
       select user_id, 8, 1, sysdate()
       from csm_user
       where login_name = 'testuser';

-- add testuser to group Super Administrators
insert into group_user(user_id, group_id)
       select u.id, g.id
       from principal u, principal g
       where u.login='testuser'
       and g.name='Super Administrators';

-- add a membership for site CBSR (id = 34)
insert into membership (id, version, center_id, principal_id)
       select coalesce(MAX(ms.id), 0)+1, 0, 34, u.user_id
       from csm_user as u, membership as ms
       where login_name = 'testuser';


