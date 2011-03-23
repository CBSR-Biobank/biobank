INSERT INTO csm_user (LOGIN_NAME, MIGRATED_FLAG, FIRST_NAME, LAST_NAME, PASSWORD, UPDATE_DATE)
VALUES ('testuser',0,'testuser','testuser','orDBlaojDQE=',sysdate());

INSERT INTO csm_user_group (USER_ID, GROUP_ID) 
select USER_ID , GROUP_ID from csm_user, csm_group where LOGIN_NAME="testuser" and GROUP_NAME="Super Administrator";
INSERT INTO csm_user_group (USER_ID, GROUP_ID) 
select USER_ID , GROUP_ID from csm_user, csm_group where LOGIN_NAME="testuser" and GROUP_NAME="CBSR Technician Level 1";



