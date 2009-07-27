-- 
--The following entries creates a super admin application incase you decide 
--to use this database to run UPT also. In that case you need to provide
--the project login id and name for the super admin.
--However in incase you are using this database just to host the application's
--authorization schema, these enteries are not used and hence they can be left as 
--it is.
--


INSERT INTO csm_application(APPLICATION_NAME,APPLICATION_DESCRIPTION,DECLARATIVE_FLAG,ACTIVE_FLAG,UPDATE_DATE)
VALUES ("CLM","CLM",0,0,sysdate());

insert into csm_protection_element(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE)
values("CLM","CLM","CLM",1,sysdate());


COMMIT;
