/************** CBSR pe and pg ***********************/
/* add protection element for CBSR */
INSERT INTO csm_protection_element (protection_element_name, protection_element_description, object_id, attribute, attribute_value, application_id, update_date)
select "edu.ualberta.med.biobank.model.Site/CBSR", 'CBSR', "edu.ualberta.med.biobank.model.Site", "id", id, 2, sysdate() 
from center where name_short='CBSR';

/* add protection group for CBSR with parent "all sites" (id = 11) */
INSERT INTO csm_protection_group (PROTECTION_GROUP_NAME, PROTECTION_GROUP_DESCRIPTION, APPLICATION_ID, PARENT_PROTECTION_GROUP_ID, UPDATE_DATE)
VALUES ('CBSR site', "protection group for site CBSR", 2, 11, sysdate());

/* add the CBSR protection element to the CBSR protection group */
INSERT INTO csm_pg_pe (protection_group_id, protection_element_id, update_date) 
select protection_group_id, protection_element_id,  sysdate() from csm_protection_group, csm_protection_element where protection_group_name='CBSR site' and protection_element_description='CBSR';
/************** CBSR pe and pg ***********************/

/************** Calgary pe and pg ***********************/
/* add protection element for calgary */
INSERT INTO csm_protection_element (protection_element_name, protection_element_description, object_id, attribute, attribute_value, application_id, update_date)
select "edu.ualberta.med.biobank.model.Site/Calgary-F", 'Calgary-F', "edu.ualberta.med.biobank.model.Site", "id", id, 2, sysdate() 
from center where name_short='Calgary-F';

/* add protection group for Calgary with parent "all sites" (id = 11) */
INSERT INTO csm_protection_group (protection_group_name, protection_group_description, application_id, parent_protection_group_id, update_date)
VALUES ('Calgary-F site', "protection group for site Calgary-F", 2, 11, sysdate());

/* add the Calgary protection element to the Calgary protection group */
INSERT INTO csm_pg_pe (protection_group_id, protection_element_id, update_date) 
select protection_group_id, protection_element_id,  sysdate() from csm_protection_group, csm_protection_element where protection_group_name='Calgary-F site' and protection_element_description='Calgary-F';

/************** Calgary pe and pg ***********************/


/************** CBSR groups ***********************/
/* add CBSR Tech 1 and 2 read on Calgary-F site*/
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() from csm_group, csm_role, csm_protection_group where group_name='CBSR Technician Level 1' and role_name='Read Only' and protection_group_name='Calgary-F site';
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() from csm_group, csm_role, csm_protection_group where group_name='CBSR Technician Level 2' and role_name='Read Only' and protection_group_name='Calgary-F site';


/* add CBSR Tech 1 and 2 read/update on CBSR site*/
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() from csm_group, csm_role, csm_protection_group where group_name='CBSR Technician Level 1' and role_name='Site Full Access' and protection_group_name='CBSR site';
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() from csm_group, csm_role, csm_protection_group where group_name='CBSR Technician Level 2' and role_name='Site Full Access' and protection_group_name='CBSR site';
/************** CBSR groups ***********************/


/************** Calgary groups ***********************/
/* add Calgary Admin and Calgary Tech read on CBSR site*/
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() from csm_group, csm_role, csm_protection_group where group_name='Calgary Administrator' and role_name='Read Only' and protection_group_name='CBSR site';
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() from csm_group, csm_role, csm_protection_group where group_name='Calgary Technicians' and role_name='Read Only' and protection_group_name='CBSR site';


/* add Calgary Admin ant Calgary Tech read/update on Calgary-F site*/
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() from csm_group, csm_role, csm_protection_group where group_name='Calgary Administrator' and role_name='Site Full Access' and protection_group_name='Calgary-F site';
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() from csm_group, csm_role, csm_protection_group where group_name='Calgary Technicians' and role_name='Site Full Access' and protection_group_name='Calgary-F site';
/************** Calgary groups ***********************/

