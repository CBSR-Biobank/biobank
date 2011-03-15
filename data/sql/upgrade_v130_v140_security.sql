-- all webadministrators are now Super Administrator (renaming of web admin) and CBST Tech Level 1
INSERT INTO csm_user_group (USER_ID, GROUP_ID) 
select cuser.USER_ID , cgroup.GROUP_ID 
from csm_group as cgroup, csm_user as cuser
join csm_user_group as adminGroupLink on adminGroupLink.user_id=cuser.user_id
where adminGroupLink.group_id=5 and cuser.login_name != 'testuser' and cgroup.GROUP_NAME='CBSR Technician Level 1';



