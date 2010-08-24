INSERT INTO csm_user (LOGIN_NAME, MIGRATED_FLAG, FIRST_NAME, LAST_NAME, PASSWORD, UPDATE_DATE)
VALUES ('testuser',0,'testuser','testuser','orDBlaojDQE=',sysdate());


INSERT INTO csm_user_group (USER_ID, GROUP_ID) 
select USER_ID ,5 from csm_user where LOGIN_NAME="testuser"

