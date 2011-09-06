-- start bug#1405 fix
-- add properties for columns and filters for collectionEvent-s and topSpecimen-s

INSERT INTO entity_property VALUES (32, 'processingEvent.worksheet', 1, 1, 0);

INSERT INTO entity_column VALUES (31, 'Processing Event Worksheet', 32, 0);

INSERT INTO entity_filter VALUES (31, 1, 'Processing Event Worksheet', 32, 0);

-- end bug#1405 fix

-- -----------------------------------------------------------------------
--
-- New security / user management
--
-- -----------------------------------------------------------------------

-- add new objects into the csm database:
insert into csm_protection_element (protection_element_name, object_id, application_id, update_date) values 
('edu.ualberta.med.biobank.model.User','edu.ualberta.med.biobank.model.User',2,sysdate()),
('edu.ualberta.med.biobank.model.BbGroup','edu.ualberta.med.biobank.model.BbGroup',2,sysdate()),
('edu.ualberta.med.biobank.model.Principal','edu.ualberta.med.biobank.model.Principal',2,sysdate()),
('edu.ualberta.med.biobank.model.Membership','edu.ualberta.med.biobank.model.Membership',2,sysdate()),
('edu.ualberta.med.biobank.model.Permission','edu.ualberta.med.biobank.model.Permission',2,sysdate()),
('edu.ualberta.med.biobank.model.BbRight','edu.ualberta.med.biobank.model.BbRight',2,sysdate()),
('edu.ualberta.med.biobank.model.Privilege','edu.ualberta.med.biobank.model.Privilege',2,sysdate()),
('edu.ualberta.med.biobank.model.Role','edu.ualberta.med.biobank.model.Role',2,sysdate());

-- add the new object into the protection group with id 1 (the one containing all objects protection elements)
insert into csm_pg_pe (protection_group_id, protection_element_id, update_date)  
select 1, protection_element_id, sysdate() from csm_protection_element 
where protection_element_name = 'edu.ualberta.med.biobank.model.User'
or protection_element_name = 'edu.ualberta.med.biobank.model.BbGroup'
or protection_element_name = 'edu.ualberta.med.biobank.model.Principal'
or protection_element_name = 'edu.ualberta.med.biobank.model.Membership'
or protection_element_name = 'edu.ualberta.med.biobank.model.Permission'
or protection_element_name = 'edu.ualberta.med.biobank.model.BbRight'
or protection_element_name = 'edu.ualberta.med.biobank.model.Privilege'
or protection_element_name = 'edu.ualberta.med.biobank.model.Role';


-- add new security tables

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE `bb_group` (
  `PRINCIPAL_ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`PRINCIPAL_ID`),
  KEY `FK119439A0FF154DAF` (`PRINCIPAL_ID`),
  CONSTRAINT `FK119439A0FF154DAF` FOREIGN KEY (`PRINCIPAL_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `bb_right` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `FOR_SITE` bit(1) DEFAULT NULL,
  `FOR_CLINIC` bit(1) DEFAULT NULL,
  `FOR_RESEARCH_GROUP` bit(1) DEFAULT NULL,
  `FOR_STUDY` bit(1) DEFAULT NULL,
  `KEY_DESC` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `principal` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `user` (
  `PRINCIPAL_ID` int(11) NOT NULL,
  `LOGIN` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `CSM_USER_ID` bigint(20) DEFAULT NULL,
  `BULK_EMAILS` bit(1) DEFAULT NULL,
  `FULL_NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `EMAIL` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `NEED_CHANGE_PWD` bit(1) DEFAULT NULL,
  PRIMARY KEY (`PRINCIPAL_ID`),
  KEY `FK27E3CBFF154DAF` (`PRINCIPAL_ID`),
  CONSTRAINT `FK27E3CBFF154DAF` FOREIGN KEY (`PRINCIPAL_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `group_user` (
  `USER_ID` int(11) NOT NULL,
  `GROUP_ID` int(11) NOT NULL,
  PRIMARY KEY (`GROUP_ID`,`USER_ID`),
  KEY `FK6B1EC1ABB9634A05` (`USER_ID`),
  KEY `FK6B1EC1AB691634EF` (`GROUP_ID`),
  CONSTRAINT `FK6B1EC1AB691634EF` FOREIGN KEY (`GROUP_ID`) REFERENCES `bb_group` (`PRINCIPAL_ID`),
  CONSTRAINT `FK6B1EC1ABB9634A05` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `membership` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CENTER_ID` int(11) DEFAULT NULL,
  `PRINCIPAL_ID` int(11) NOT NULL,
  `STUDY_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `uc_membership` (`PRINCIPAL_ID`,`CENTER_ID`,`STUDY_ID`),
  KEY `FKCD0773D6F2A2464F` (`STUDY_ID`),
  KEY `FKCD0773D6FF154DAF` (`PRINCIPAL_ID`),
  KEY `FKCD0773D692FAA705` (`CENTER_ID`),
  CONSTRAINT `FKCD0773D692FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKCD0773D6F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`),
  CONSTRAINT `FKCD0773D6FF154DAF` FOREIGN KEY (`PRINCIPAL_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `membership_role` (
  `MEMBERSHIP_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEMBERSHIP_ID`,`ROLE_ID`),
  KEY `FKEF36B33F14388625` (`ROLE_ID`),
  KEY `FKEF36B33FD26ABDE5` (`MEMBERSHIP_ID`),
  CONSTRAINT `FKEF36B33FD26ABDE5` FOREIGN KEY (`MEMBERSHIP_ID`) REFERENCES `membership` (`ID`),
  CONSTRAINT `FKEF36B33F14388625` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `role` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `permission` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `RIGHT_ID` int(11) NOT NULL,
  `MEMBERSHIP_ID` int(11) DEFAULT NULL,
  `ROLE_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKFE0FB1CF14388625` (`ROLE_ID`),
  KEY `FKFE0FB1CFD26ABDE5` (`MEMBERSHIP_ID`),
  KEY `FKFE0FB1CFF5E5B3CF` (`RIGHT_ID`),
  CONSTRAINT `FKFE0FB1CFF5E5B3CF` FOREIGN KEY (`RIGHT_ID`) REFERENCES `bb_right` (`ID`),
  CONSTRAINT `FKFE0FB1CF14388625` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ID`),
  CONSTRAINT `FKFE0FB1CFD26ABDE5` FOREIGN KEY (`MEMBERSHIP_ID`) REFERENCES `membership` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `permission_privilege` (
  `PERMISSION_ID` int(11) NOT NULL,
  `PRIVILEGE_ID` int(11) NOT NULL,
  PRIMARY KEY (`PERMISSION_ID`,`PRIVILEGE_ID`),
  KEY `FK6B26FC21AABB1ACF` (`PRIVILEGE_ID`),
  KEY `FK6B26FC21F196CF45` (`PERMISSION_ID`),
  CONSTRAINT `FK6B26FC21F196CF45` FOREIGN KEY (`PERMISSION_ID`) REFERENCES `permission` (`ID`),
  CONSTRAINT `FK6B26FC21AABB1ACF` FOREIGN KEY (`PRIVILEGE_ID`) REFERENCES `privilege` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `privilege` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `right_privilege` (
  `RIGHT_ID` int(11) NOT NULL,
  `PRIVILEGE_ID` int(11) NOT NULL,
  PRIMARY KEY (`RIGHT_ID`,`PRIVILEGE_ID`),
  KEY `FK4B32800EAABB1ACF` (`PRIVILEGE_ID`),
  KEY `FK4B32800EF5E5B3CF` (`RIGHT_ID`),
  CONSTRAINT `FK4B32800EF5E5B3CF` FOREIGN KEY (`RIGHT_ID`) REFERENCES `bb_right` (`ID`),
  CONSTRAINT `FK4B32800EAABB1ACF` FOREIGN KEY (`PRIVILEGE_ID`) REFERENCES `privilege` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- add data in security tables:

LOCK TABLES `BB_RIGHT` WRITE;
INSERT INTO `BB_RIGHT` (ID, VERSION, NAME, FOR_SITE, FOR_CLINIC, FOR_RESEARCH_GROUP, FOR_STUDY, KEY_DESC) VALUES
( 0, 0, 'Specimen Link', 1, 1, 0, 0, 'specimen-link'),
( 1, 0, 'Specimen Assign', 1, 0, 0, 0, 'specimen-assign'),
( 2, 0, 'Study', 1, 1, 1, 1, 'Study'),
( 3, 0, 'Clinic', 1, 1, 1, 1, 'Clinic'),
( 4, 0, 'Site', 1, 1, 1, 1, 'Site'),
( 5, 0, 'Research Group', 1, 1, 1, 1, 'ResearchGroup'),
( 6, 0, 'Logging', 1, 1, 1, 1, 'logging'),
( 7, 0, 'Reports', 1, 1, 1, 1, 'reports'),
( 8, 0, 'Container', 1, 0, 0, 0, 'Container'),
( 9, 0, 'Container Type', 1, 0, 0, 0, 'ContainerType'),
( 10, 0, 'Patient', 1, 1, 1, 1, 'Patient'),
( 11, 0, 'Collection Event', 1, 1, 0, 0, 'CollectionEvent'),
( 12, 0, 'Processing Event', 1, 1, 0, 0, 'ProcessingEvent'),
( 13, 0, 'Send Dispatch', 1, 1, 0, 0, 'send-Dispatch'),
( 14, 0, 'Receive Dispatch', 1, 1, 1, 0, 'receive-Dispatch'),
( 15, 0, 'Create Specimen Request', 0, 0, 1, 1, 'create-spec-request'),
( 16, 0, 'Receive Specimen Request', 1, 0, 0, 0, 'receive-spec-request'),
( 17, 0, 'Clinic Shipment (CBSR special)', 1, 0, 0, 0, 'cs-OriginInfo'),
( 18, 0, 'Print labels', 1, 1, 0, 0, 'print-labels'),
( 19, 0, 'Specimen Types', 1, 1, 1, 1, 'SpecimenType'),
( 20, 0, 'Shipping Methods', 1, 1, 1, 1, 'ShippingMethod'),
( 21, 0, 'Activity Status', 1, 1,1, 1, 'ActivityStatus'),
( 22, 0, 'Specimen', 1, 1, 1, 1, 'Specimen'),
( 23, 0, 'User Management', 1, 1, 1, 1, 'user-mgt'),
( 24, 0, 'Administrator', 1, 1, 1, 1, 'admin');
UNLOCK TABLES;


LOCK TABLES `PRIVILEGE` WRITE;
INSERT INTO `PRIVILEGE` (ID, VERSION, NAME) VALUES
( 1, 0, 'Read'),
( 2, 0, 'Update'),
( 3, 0, 'Delete'),
( 4, 0, 'Create'),
( 5, 0, 'Allowed');
UNLOCK TABLES;

LOCK TABLES `right_privilege` WRITE;
INSERT INTO `right_privilege` (right_id, privilege_id) VALUES
-- specimen link
( 0, 5),
-- specimen assign
( 1, 5),
-- study
( 2, 1),
( 2, 2),
( 2, 3),
( 2, 4),
-- clinic
( 3, 1),
( 3, 2),
( 3, 3),
( 3, 4),
-- Site
( 4, 1),
( 4, 2),
( 4, 3),
( 4, 4),
-- Research Group
( 5, 1),
( 5, 2),
( 5, 3),
( 5, 4),
-- Logging
( 6, 5),
-- reports
( 7, 5),
-- Container
( 8, 1),
( 8, 2),
( 8, 3),
( 8, 4),
-- Container Type
( 9, 1),
( 9, 2),
( 9, 3),
( 9, 4),
-- Patient
( 10, 1),
( 10, 2),
( 10, 3),
( 10, 4),
-- Collection Event
( 11, 1),
( 11, 2),
( 11, 3),
( 11, 4),
-- Processing Event
( 12, 1),
( 12, 2),
( 12, 3),
( 12, 4),
-- Send Dispatch
( 13, 5),
-- Receive Dispatch
( 14, 5),
-- Create specimen request
( 15, 5),
-- Receive specimen request
( 16, 5),
-- Clinic shipment
( 17, 1),
( 17, 2),
( 17, 3),
( 17, 4),
-- Print labels
( 18, 5),
-- Specimen types
( 19, 1),
( 19, 2),
( 19, 3),
( 19, 4),
-- Shipping methods
( 20, 1),
( 20, 2),
( 20, 3),
( 20, 4),
-- Activity status
( 21, 1),
( 21, 2),
( 21, 3),
( 21, 4),
-- Specimen
( 22, 1),
( 22, 2),
( 22, 3),
( 22, 4),
-- User management
( 23, 5),
-- Administrator
( 24, 5);
UNLOCK TABLES;


-- convert users from csm to users from biobank:
insert into principal (id, version)  
select user_id, 0 from csm_user;

insert into user (principal_id, login, csm_user_id, bulk_emails, full_name, email, need_change_pwd) 
select user_id, login_name, user_id, 1, concat(first_name, ' ', last_name), email_id, 0 from csm_user where login_name != 'administrator' and login_name != 'bbadmin';

-- Add a default super admin group. 
insert into principal (id, version) 
select coalesce(MAX(id), 0)+1, 0 from principal;

insert into bb_group (principal_id, name)
select max(id), 'Super Administrators' from principal;

-- add a membership to this super admin role
insert into membership(id, version, principal_id)
select 1, 0, max(id) from principal;

-- add a permission to this membership that contains right 'administrator' (id=24)
insert into permission(id, version, right_id, membership_id) values
(1, 0, 24, 1);

-- add privilege 'allowed' (id = 5) to this permission
insert into permission_privilege(permission_id, privilege_id) values
(1, 5);

-- Old super admin group_id was 5 in csm database. Add previous super admin users in this new super admin group:
insert into group_user(user_id, group_id) 
select u.principal_id, g.principal_id from user u, bb_group g, csm_user_group ug
where ug.group_id = 5 and ug.user_id = u.csm_user_id and g.name='Super Administrators';


-- give access to all object to all users (in csm):
insert into csm_user_group_role_pg (user_id, role_id, protection_group_id, update_date)
select user_id, 8, 1, sysdate() from csm_user;

-- delete olds csm_groups
delete from csm_group;

-- delete old csm_protection_group except for the one with id=1
delete from csm_protection_group where protection_group_id != 1 and parent_protection_group_id is not null;
delete from csm_protection_group where protection_group_id != 1 ;

-- delete old csm_protection_element for centers:
delete from csm_protection_element where object_id = 'edu.ualberta.med.biobank.model.Clinic' and attribute = 'id';
delete from csm_protection_element where object_id = 'edu.ualberta.med.biobank.model.Site' and attribute = 'id';
delete from csm_protection_element where object_id = 'edu.ualberta.med.biobank.model.ResearchGroup' and attribute = 'id';

-- delete old csm_role
delete from csm_role where role_id = 7;
delete from csm_role where role_id = 9;


