INSERT INTO csm_user (USER_ID, LOGIN_NAME, MIGRATED_FLAG, FIRST_NAME, LAST_NAME, PASSWORD, UPDATE_DATE)
       select (select coalesce(MAX(id), 0)+Max(user_id)+1
       from principal, csm_user), 'testuser', 0 , 'testuser', 'testuser', 'orDBlaojDQE=', sysdate();

insert into principal (discriminator, id, version, full_name, login, recv_bulk_emails, need_pwd_change, activity_status_id)
       select 'User', user_id, 0, concat(first_name, ' ', last_name), login_name, 0, 0, 1 from csm_user where login_name = 'testuser';

insert into csm_user_group_role_pg (user_id, role_id, protection_group_id, update_date)
       select user_id, 8, 1, sysdate()
       from csm_user
       where login_name = 'testuser';

-- add testuser to group Super Administrators
insert into group_user(user_id, group_id)
       select u.id, g.id
       from principal u, principal g
       where u.login='testuser'
       and g.name='Global Administrators'
       and u.discriminator = 'User'
       and g.discriminator = 'BbGroup';
