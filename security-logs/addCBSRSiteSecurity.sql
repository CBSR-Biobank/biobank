/**** sites *****/

/* add protection element for all sites */
INSERT INTO csm_protection_element (protection_element_name, protection_element_description, object_id, attribute, attribute_value, application_id, update_date)
select concat('Site/',name_short), name_short, 'edu.ualberta.med.biobank.model.Site', 'id', id, 2, sysdate() 
from center where discriminator='Site';

/* add protection group for all sites */
INSERT INTO csm_protection_group (PROTECTION_GROUP_NAME, PROTECTION_GROUP_DESCRIPTION, APPLICATION_ID, UPDATE_DATE)
select concat('Site ',name_short), concat('Protection group for site ', name_short), 2, sysdate()
from center where discriminator='Site';

/**** clinics *****/

/* add protection element for all clinics */
INSERT INTO csm_protection_element (protection_element_name, protection_element_description, object_id, attribute, attribute_value, application_id, update_date)
select concat('Clinic/',name_short), name_short, 'edu.ualberta.med.biobank.model.Clinic', 'id', id, 2, sysdate() 
from center where discriminator='Clinic';

/* add protection group for all clinics */
INSERT INTO csm_protection_group (PROTECTION_GROUP_NAME, PROTECTION_GROUP_DESCRIPTION, APPLICATION_ID, UPDATE_DATE)
select concat('Clinic ',name_short), concat('Protection group for clinic ', name_short), 2, sysdate()
from center where discriminator='Clinic';


/**** Common ****/
/* add the center protection element corresponding protection group */
/* FYI: the 'BINARY' keyword is there to avoid a collate exception on like and = between the 2 tables results*/
INSERT INTO csm_pg_pe (protection_group_id, protection_element_id, update_date) 
select protection_group_id, protection_element_id,  sysdate() 
from csm_protection_group, csm_protection_element, center 
where BINARY protection_group_name like BINARY concat('% ',center.name_short) 
and BINARY protection_element_description=BINARY center.name_short;



/************** CBSR groups ***********************/
/* add CBSR Tech 1 and 2 read/update on CBSR site*/
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() 
from csm_group, csm_role, csm_protection_group 
where group_name='CBSR Technician Level 1' and role_name='Center Full Access' and protection_group_name='Site CBSR';

INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() 
from csm_group, csm_role, csm_protection_group 
where group_name='CBSR Technician Level 2' and role_name='Center Full Access' and protection_group_name='Site CBSR';

/************** CBSR groups ***********************/


/************** Calgary groups ***********************/
/* add Calgary Admin ant Calgary Tech read/update on Calgary-F site*/
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() 
from csm_group, csm_role, csm_protection_group 
where group_name='Calgary Administrator' and role_name='Center Full Access' and protection_group_name='Site Calgary-F';

INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) 
select group_id , role_id, protection_group_id, sysdate() 
from csm_group, csm_role, csm_protection_group 
where group_name='Calgary Technicians' and role_name='Center Full Access' and protection_group_name='Site Calgary-F';
/************** Calgary groups ***********************/



