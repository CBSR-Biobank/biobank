--
-- The following entries creates a super admin application incase you decide 
-- to use this database to run UPT also. In that case you need to provide
-- the project login id and name for the super admin.
-- However in incase you are using this database just to host the application's
-- authorization schema, these enteries are not used and hence they can be left as 
-- it is.
--

set foreign_key_checks=1;

insert into csm_application(APPLICATION_NAME,APPLICATION_DESCRIPTION,DECLARATIVE_FLAG,ACTIVE_FLAG,UPDATE_DATE)
values ("csmupt","CSM UPT Super Admin Application",0,0,sysdate());

insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE)
values ("administrator","Administrator","NoName","zJPWCwDeSgG8j2uyHEABIQ==",sysdate());
 
insert into csm_protection_element(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE)
values("csmupt","CSM UPT Super Admin Application Protection Element","csmupt",1,sysdate());

insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID)
values(1,1);

-- 
-- The following entry is for your application. 
-- Replace <<application_context_name>> with your application name.
--

INSERT INTO csm_application(APPLICATION_NAME,APPLICATION_DESCRIPTION,DECLARATIVE_FLAG,ACTIVE_FLAG,UPDATE_DATE)
VALUES ("biobank2","biobank2",0,0,sysdate());

insert into csm_protection_element(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE)
values("biobank2","biobank2","biobank2",1,sysdate());

-- admin user for biobank2 and testuser
insert into csm_user (LOGIN_NAME,FIRST_NAME,LAST_NAME,PASSWORD,UPDATE_DATE) values 
("bbadmin","Biobank Administrator","NoName","7Bg9siN5e7M=",sysdate()),
("testuser","TestUser","NoName","",sysdate());

-- set bbadmin (user_id = 2) as biobank user
insert into csm_user_pe(PROTECTION_ELEMENT_ID,USER_ID)
values(2,2);

-- add tester role
insert into csm_role(ROLE_NAME, ROLE_DESCRIPTION, APPLICATION_ID, ACTIVE_FLAG, UPDATE_DATE)
values("tester", "Tester role", 2, 1, sysdate());

-- protection group for all classes
insert into csm_protection_group(PROTECTION_GROUP_NAME, PROTECTION_GROUP_DESCRIPTION, APPLICATION_ID, LARGE_ELEMENT_COUNT_FLAG, UPDATE_DATE)
values("pg-biobank-all", "Protection for all the classes", 2, 0, sysdate());

-- assign role + protection to user testuser
insert into csm_user_group_role_pg(USER_ID, ROLE_ID, PROTECTION_GROUP_ID, UPDATE_DATE)
values(3, 1, 1, sysdate());

-- protection_element for biobank2 app
insert into csm_protection_element(PROTECTION_ELEMENT_NAME,PROTECTION_ELEMENT_DESCRIPTION,OBJECT_ID,APPLICATION_ID,UPDATE_DATE) values
("edu.ualberta.med.biobank.model.AbstractPosition","edu.ualberta.med.biobank.model.AbstractPosition","edu.ualberta.med.biobank.model.AbstractPosition",2,sysdate()),
("edu.ualberta.med.biobank.model.Address","edu.ualberta.med.biobank.model.Address","edu.ualberta.med.biobank.model.Address",2,sysdate()),
("edu.ualberta.med.biobank.model.Capacity","edu.ualberta.med.biobank.model.Capacity","edu.ualberta.med.biobank.model.Capacity",2,sysdate()),
("edu.ualberta.med.biobank.model.Clinic","edu.ualberta.med.biobank.model.Clinic","edu.ualberta.med.biobank.model.Clinic",2,sysdate()),
("edu.ualberta.med.biobank.model.ContainerPosition","edu.ualberta.med.biobank.model.ContainerPosition","edu.ualberta.med.biobank.model.ContainerPosition",2,sysdate()),
("edu.ualberta.med.biobank.model.Patient","edu.ualberta.med.biobank.model.Patient","edu.ualberta.med.biobank.model.Patient",2,sysdate()),
("edu.ualberta.med.biobank.model.PatientVisit","edu.ualberta.med.biobank.model.PatientVisit","edu.ualberta.med.biobank.model.PatientVisit",2,sysdate()),
("edu.ualberta.med.biobank.model.PvInfo","edu.ualberta.med.biobank.model.PvInfo","edu.ualberta.med.biobank.model.PvInfo",2,sysdate()),
("edu.ualberta.med.biobank.model.PvInfoData","edu.ualberta.med.biobank.model.PvInfoData","edu.ualberta.med.biobank.model.PvInfoData",2,sysdate()),
("edu.ualberta.med.biobank.model.PvInfoPossible","edu.ualberta.med.biobank.model.PvInfoPossible","edu.ualberta.med.biobank.model.PvInfoPossible",2,sysdate()),
("edu.ualberta.med.biobank.model.PvInfoType","edu.ualberta.med.biobank.model.PvInfoType","edu.ualberta.med.biobank.model.PvInfoType",2,sysdate()),
("edu.ualberta.med.biobank.model.Sample","edu.ualberta.med.biobank.model.Sample","edu.ualberta.med.biobank.model.Sample",2,sysdate()),
("edu.ualberta.med.biobank.model.SamplePosition","edu.ualberta.med.biobank.model.SamplePosition","edu.ualberta.med.biobank.model.SamplePosition",2,sysdate()),
("edu.ualberta.med.biobank.model.SampleType","edu.ualberta.med.biobank.model.SampleType","edu.ualberta.med.biobank.model.SampleType",2,sysdate()),
("edu.ualberta.med.biobank.model.Shipment","edu.ualberta.med.biobank.model.Shipment","edu.ualberta.med.biobank.model.Shipment",2,sysdate()),
("edu.ualberta.med.biobank.model.Site","edu.ualberta.med.biobank.model.Site","edu.ualberta.med.biobank.model.Site",2,sysdate()),
("edu.ualberta.med.biobank.model.StorageContainer","edu.ualberta.med.biobank.model.StorageContainer","edu.ualberta.med.biobank.model.StorageContainer",2,sysdate()),
("edu.ualberta.med.biobank.model.StorageType","edu.ualberta.med.biobank.model.StorageType","edu.ualberta.med.biobank.model.StorageType",2,sysdate()),
("edu.ualberta.med.biobank.model.Study","edu.ualberta.med.biobank.model.Study","edu.ualberta.med.biobank.model.Study",2,sysdate()),
("edu.ualberta.med.biobank.model.User","edu.ualberta.med.biobank.model.User","edu.ualberta.med.biobank.model.User",2,sysdate()),
("edu.ualberta.med.biobank.model.Worksheet","edu.ualberta.med.biobank.model.Worksheet","edu.ualberta.med.biobank.model.Worksheet",2,sysdate());

-- association protection group / protection element
insert into csm_pg_pe(PROTECTION_GROUP_ID, PROTECTION_ELEMENT_ID, UPDATE_DATE) values
(1, 3, sysdate()),
(1, 4, sysdate()),
(1, 5, sysdate()),
(1, 6, sysdate()),
(1, 7, sysdate()),
(1, 8, sysdate()),
(1, 9, sysdate()),
(1, 10, sysdate()),
(1, 11, sysdate()),
(1, 12, sysdate()),
(1, 13, sysdate()),
(1, 14, sysdate()),
(1, 15, sysdate()),
(1, 16, sysdate()),
(1, 17, sysdate()),
(1, 18, sysdate()),
(1, 19, sysdate()),
(1, 20, sysdate()),
(1, 21, sysdate()),
(1, 22, sysdate()),
(1, 23, sysdate());

--
-- The following entries are Common Set of Privileges
--

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("CREATE","This privilege grants permission to a user to create an entity. This entity can be an object, a database entry, or a resource such as a network connection", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("ACCESS","This privilege allows a user to access a particular resource.  Examples of resources include a network or database connection, socket, module of the application, or even the application itself", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("READ","This privilege permits the user to read data from a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to read data about a particular entry", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("WRITE","This privilege allows a user to write data to a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to write data about a particular entity", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("UPDATE","This privilege grants permission at an entity level and signifies that the user is allowed to update data for a particular entity. Entities may include an object, object attribute, database row etc", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("DELETE","This privilege permits a user to delete a logical entity. This entity can be an object, a database entry, a resource such as a network connection, etc", sysdate());

INSERT INTO csm_privilege (privilege_name, privilege_description, update_date)
VALUES("EXECUTE","This privilege allows a user to execute a particular resource. The resource can be a method, function, behavior of the application, URL, button etc", sysdate());

set foreign_key_checks=1;

COMMIT;
