-- add new objects into the csm database:
insert into csm_protection_element (protection_element_name, object_id, application_id, update_date) values 
('edu.ualberta.med.biobank.model.User','edu.ualberta.med.biobank.model.User',2,sysdate()),
('edu.ualberta.med.biobank.model.BbGroup','edu.ualberta.med.biobank.model.BbGroup',2,sysdate()),
('edu.ualberta.med.biobank.model.Principal','edu.ualberta.med.biobank.model.Principal',2,sysdate()),
('edu.ualberta.med.biobank.model.Membership','edu.ualberta.med.biobank.model.Membership',2,sysdate()),
('edu.ualberta.med.biobank.model.MembershipRole','edu.ualberta.med.biobank.model.MembershipRole',2,sysdate()),
('edu.ualberta.med.biobank.model.MembershipRight','edu.ualberta.med.biobank.model.MembershipRight',2,sysdate()),
('edu.ualberta.med.biobank.model.RightPrivilege','edu.ualberta.med.biobank.model.RightPrivilege',2,sysdate()),
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
or protection_element_name = 'edu.ualberta.med.biobank.model.MembershipRole'
or protection_element_name = 'edu.ualberta.med.biobank.model.MembershipRight'
or protection_element_name = 'edu.ualberta.med.biobank.model.RightPrivilege'
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

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bb_group` (
  `PRINCIPAL_ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`PRINCIPAL_ID`),
  KEY `FK119439A0FF154DAF` (`PRINCIPAL_ID`),
  CONSTRAINT `FK119439A0FF154DAF` FOREIGN KEY (`PRINCIPAL_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `principal` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `PRINCIPAL_ID` int(11) NOT NULL,
  `LOGIN` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `CSM_USER_ID` bigint(20) DEFAULT NULL,
  `IS_SUPER_ADMIN` bit(1) DEFAULT NULL,
  `BULK_EMAILS` bit(1) DEFAULT NULL,
  `FIRST_NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `LAST_NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `EMAIL` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `NEED_CHANGE_PWD` bit(1) DEFAULT NULL,
  PRIMARY KEY (`PRINCIPAL_ID`),
  KEY `FK27E3CBFF154DAF` (`PRINCIPAL_ID`),
  CONSTRAINT `FK27E3CBFF154DAF` FOREIGN KEY (`PRINCIPAL_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_user` (
  `USER_ID` int(11) NOT NULL,
  `GROUP_ID` int(11) NOT NULL,
  PRIMARY KEY (`GROUP_ID`,`USER_ID`),
  KEY `FK6B1EC1ABB9634A05` (`USER_ID`),
  KEY `FK6B1EC1AB691634EF` (`GROUP_ID`),
  CONSTRAINT `FK6B1EC1AB691634EF` FOREIGN KEY (`GROUP_ID`) REFERENCES `bb_group` (`PRINCIPAL_ID`),
  CONSTRAINT `FK6B1EC1ABB9634A05` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `membership` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `PRINCIPAL_ID` int(11) NOT NULL,
  `CENTER_ID` int(11) DEFAULT NULL,
  `STUDY_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKCD0773D6F2A2464F` (`STUDY_ID`),
  KEY `FKCD0773D6FF154DAF` (`PRINCIPAL_ID`),
  KEY `FKCD0773D692FAA705` (`CENTER_ID`),
  CONSTRAINT `FKCD0773D692FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKCD0773D6F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`),
  CONSTRAINT `FKCD0773D6FF154DAF` FOREIGN KEY (`PRINCIPAL_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `membership_role` (
  `MEMBERSHIP_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEMBERSHIP_ID`),
  KEY `FKEF36B33FD26ABDE5` (`MEMBERSHIP_ID`),
  CONSTRAINT `FKEF36B33FD26ABDE5` FOREIGN KEY (`MEMBERSHIP_ID`) REFERENCES `membership` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `membership_right` (
  `MEMBERSHIP_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEMBERSHIP_ID`),
  KEY `FKF79CE853D26ABDE5` (`MEMBERSHIP_ID`),
  CONSTRAINT `FKF79CE853D26ABDE5` FOREIGN KEY (`MEMBERSHIP_ID`) REFERENCES `membership` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `membership_role_role` (
  `MEMBERSHIP_ROLE_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEMBERSHIP_ROLE_ID`,`ROLE_ID`),
  KEY `FK6E3AD76D6BCA5F2` (`MEMBERSHIP_ROLE_ID`),
  KEY `FK6E3AD7614388625` (`ROLE_ID`),
  CONSTRAINT `FK6E3AD7614388625` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ID`),
  CONSTRAINT `FK6E3AD76D6BCA5F2` FOREIGN KEY (`MEMBERSHIP_ROLE_ID`) REFERENCES `membership_role` (`MEMBERSHIP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `right_privilege` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `MEMBERSHIP_RIGHT_ID` int(11) DEFAULT NULL,
  `RIGHT_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK4B32800E14388625` (`ROLE_ID`),
  KEY `FK4B32800EF5E5B3CF` (`RIGHT_ID`),
  KEY `FK4B32800EBB1B5B42` (`MEMBERSHIP_RIGHT_ID`),
  CONSTRAINT `FK4B32800EBB1B5B42` FOREIGN KEY (`MEMBERSHIP_RIGHT_ID`) REFERENCES `membership_right` (`MEMBERSHIP_ID`),
  CONSTRAINT `FK4B32800E14388625` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ID`),
  CONSTRAINT `FK4B32800EF5E5B3CF` FOREIGN KEY (`RIGHT_ID`) REFERENCES `bb_right` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `right_privilege_privilege` (
  `RIGHT_PRIVILEGE_ID` int(11) NOT NULL,
  `PRIVILEGE_ID` int(11) NOT NULL,
  PRIMARY KEY (`RIGHT_PRIVILEGE_ID`,`PRIVILEGE_ID`),
  KEY `FKE1E847A0AABB1ACF` (`PRIVILEGE_ID`),
  KEY `FKE1E847A03267910C` (`RIGHT_PRIVILEGE_ID`),
  CONSTRAINT `FKE1E847A03267910C` FOREIGN KEY (`RIGHT_PRIVILEGE_ID`) REFERENCES `right_privilege` (`ID`),
  CONSTRAINT `FKE1E847A0AABB1ACF` FOREIGN KEY (`PRIVILEGE_ID`) REFERENCES `privilege` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bb_right` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `FOR_SITE` bit(1) DEFAULT NULL,
  `FOR_CLINIC` bit(1) DEFAULT NULL,
  `FOR_RESEARCH_GROUP` bit(1) DEFAULT NULL,
  `FOR_STUDY` bit(1) DEFAULT NULL,
  `KEY_DESC` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `KEY_DESC` (`KEY_DESC`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

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
( 8, 0, 'Center administration', 1, 1, 1, 1, 'center-admin'),
( 9, 0, 'Container', 1, 0, 0, 0, 'Container'),
( 10, 0, 'Container Type', 1, 0, 0, 0, 'ContainerType'),
( 11, 0, 'Patient', 1, 1, 1, 1, 'Patient'),
( 12, 0, 'Collection Event', 1, 1, 0, 0, 'CollectionEvent'),
( 13, 0, 'Processing Event', 1, 1, 0, 0, 'ProcessingEvent'),
( 14, 0, 'Send Dispatch', 1, 1, 0, 0, 'send-Dispatch'),
( 15, 0, 'Receive Dispatch', 1, 1, 1, 0, 'receive-Dispatch'),
( 16, 0, 'Ask Request', 0, 0, 1, 1, 'ask-request'),
( 17, 0, 'Receive Request', 1, 0, 0, 0, 'receive-request'),
( 18, 0, 'Clinic Shipment (CBSR special)', 1, 0, 0, 0, 'cs-OriginInfo'),
( 19, 0, 'Print labels', 1, 1, 0, 0, 'print-labels'),
( 20, 0, 'Specimen Types', 1, 1, 1, 1, 'SpecimenType'),
( 21, 0, 'Shipping Methods', 1, 1, 1, 1, 'ShippingMethod'),
( 22, 0, 'Activity Status', 1, 1,1, 1, 'ActivityStatus'),
( 23, 0, 'Specimen', 1, 1, 1, 1, 'Specimen'),
( 24, 0, 'Contact', 1, 1, 1, 1, 'Contact');
UNLOCK TABLES;



/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `privilege` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `PRIVILEGE` WRITE;
INSERT INTO `PRIVILEGE` (ID, VERSION, NAME) VALUES
( 1, 0, 'Read'),
( 2, 0, 'Update'),
( 3, 0, 'Delete'),
( 4, 0, 'Create');
UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- convert users from csm to users from biobank:
insert into principal (id, version)  
select user_id, 0 from csm_user;

insert into user (principal_id, login, csm_user_id, bulk_emails, first_name, last_name, email, need_change_pwd, is_super_admin) 
select user_id, login_name, user_id, 1, first_name, last_name, email_id, 0, 0 from csm_user;
-- group_id = 5 is 'Super Admin Group'
update user as u, csm_user_group as ug
set u.is_super_admin = 1
where ug.group_id = 5 and ug.user_id = u.csm_user_id;

-- give access to all object to all users:
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



