ALTER TABLE shipment_info
      ADD COLUMN COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL COMMENT '';

ALTER TABLE request DROP FOREIGN KEY FK6C1A7E6F80AB67E;
ALTER TABLE request DROP INDEX FK6C1A7E6F80AB67E, DROP COLUMN STATE, DROP COLUMN REQUESTER_ID;

ALTER TABLE request_specimen DROP FOREIGN KEY FK579572D8D990A70;
ALTER TABLE request_specimen DROP INDEX FK579572D8D990A70, DROP COLUMN AREQUEST_ID;
ALTER TABLE request_specimen ADD COLUMN REQUEST_ID INT(11) NOT NULL COMMENT '', ADD INDEX FK579572D8A2F14F4F (REQUEST_ID);
ALTER TABLE request_specimen ADD CONSTRAINT FK579572D8A2F14F4F FOREIGN KEY FK579572D8A2F14F4F (REQUEST_ID) REFERENCES request (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- add country field and set all existing ones to Canada (issue #1237)
alter table ADDRESS add COLUMN COUNTRY varchar(50);
update ADDRESS set COUNTRY = 'Canada';


-- update discriminator of SpecimenPosition (before was AliquotPosition)
update abstract_position
set discriminator = 'SpecimenPosition'
where discriminator = 'AliquotPosition';


-- start updates in security tables - see issue #1324

-- all center features are no longer children of the 'center feature'
update csm_protection_group set parent_protection_group_id = null where protection_group_name like 'Center Feature:%';

-- remove center_admin protection group (id=45) from super admin group (id=5) (not needed)
delete from csm_user_group_role_pg where group_id = 5 and protection_group_id = 45;

-- add all center features to CBSR Tech1 (id=6) (which is a center admin group)
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date)
select 6, 8, protection_group_id, sysdate() 
from csm_protection_group where protection_group_name like 'Center Feature:%';

-- add some of center features to Calgary Admin (id=9) (which is a center admin group)
INSERT INTO csm_user_group_role_pg (group_id, role_id, protection_group_id, update_date) values
(9, 8, 47, sysdate()),  -- collection event
(9, 8, 48, sysdate()),  -- assign
(9, 8, 50, sysdate()),  -- dispatch
(9, 8, 67, sysdate()),  -- link
(9, 8, 66, sysdate());  -- processing

-- insert 3 new protection groups specifics to each center type, children of the general Center Admin (id =45):
insert into csm_protection_group (protection_group_name, protection_group_description, application_id, large_element_count_flag, update_date, parent_protection_group_id) values
('Internal: Clinic Administrator', '** DO NOT RENAME **\ncontains specific classes that a clinic admin can work with\nIs a child of Center Admin', 2, false, sysdate(), 45),
('Internal: Site Administrator', '** DO NOT RENAME **\ncontains specific classes that a site admin can work with\nIs a child of Center Admin', 2, false,	sysdate(), 45),
('Internal: Research Group Administrator', '** DO NOT RENAME **\ncontains specific classes that a RG admin can work with\nIs a child of Center Admin', 2, false,	sysdate(), 45);

-- remove exiting protection elements linked to center admin (id=45). They will now be linked to the site admin.
delete from csm_pg_pe where protection_group_id = 45;

-- link ContainerType to SiteAdmin protection group
insert into csm_pg_pe (protection_group_id, protection_element_id, update_date) 
select protection_group_id, 20, sysdate()
from csm_protection_group where protection_group_name = 'Internal: Site Administrator'; 

-- link Container to SiteAdmin protection group
insert into csm_pg_pe (protection_group_id, protection_element_id, update_date) 
select protection_group_id, 19, sysdate()
from csm_protection_group where protection_group_name = 'Internal: Site Administrator'; 

-- link ContainerPosition to SiteAdmin protection group
insert into csm_pg_pe (protection_group_id, protection_element_id, update_date) 
select protection_group_id, 7, sysdate()
from csm_protection_group where protection_group_name = 'Internal: Site Administrator'; 

-- link Contact to ClinicAdmin protection group
insert into csm_pg_pe (protection_group_id, protection_element_id, update_date) 
select protection_group_id, 30, sysdate()
from csm_protection_group where protection_group_name = 'Internal: Clinic Administrator'; 

-- end updates in security tables
