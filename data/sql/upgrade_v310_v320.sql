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
('edu.ualberta.med.biobank.model.Role','edu.ualberta.med.biobank.model.Role',2,sysdate());

-- add the new object into the protection group with id 1 (the one containing all objects protection elements)
insert into csm_pg_pe (protection_group_id, protection_element_id, update_date)  
select 1, protection_element_id, sysdate() from csm_protection_element 
where protection_element_name = 'edu.ualberta.med.biobank.model.User'
or protection_element_name = 'edu.ualberta.med.biobank.model.BbGroup'
or protection_element_name = 'edu.ualberta.med.biobank.model.Principal'
or protection_element_name = 'edu.ualberta.med.biobank.model.Membership'
or protection_element_name = 'edu.ualberta.med.biobank.model.Permission'
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
  `STUDY_ID` int(11) DEFAULT NULL,
  `PRINCIPAL_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `uc_membership` (`PRINCIPAL_ID`,`CENTER_ID`,`STUDY_ID`),
  KEY `FKCD0773D6FF154DAF` (`PRINCIPAL_ID`),
  KEY `FKCD0773D6F2A2464F` (`STUDY_ID`),
  KEY `FKCD0773D692FAA705` (`CENTER_ID`),
  CONSTRAINT `FKCD0773D692FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKCD0773D6F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`),
  CONSTRAINT `FKCD0773D6FF154DAF` FOREIGN KEY (`PRINCIPAL_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `membership_permission` (
  `MEMBERSHIP_ID` int(11) NOT NULL,
  `PERMISSION_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEMBERSHIP_ID`,`PERMISSION_ID`),
  KEY `FK1350F1D8F196CF45` (`PERMISSION_ID`),
  KEY `FK1350F1D8D26ABDE5` (`MEMBERSHIP_ID`),
  CONSTRAINT `FK1350F1D8D26ABDE5` FOREIGN KEY (`MEMBERSHIP_ID`) REFERENCES `membership` (`ID`),
  CONSTRAINT `FK1350F1D8F196CF45` FOREIGN KEY (`PERMISSION_ID`) REFERENCES `permission` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `permission` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CLASS_NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `CLASS_NAME` (`CLASS_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `principal` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
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

CREATE TABLE `role_permission` (
  `ROLE_ID` int(11) NOT NULL,
  `PERMISSION_ID` int(11) NOT NULL,
  PRIMARY KEY (`ROLE_ID`,`PERMISSION_ID`),
  KEY `FK9C6EC93814388625` (`ROLE_ID`),
  KEY `FK9C6EC938F196CF45` (`PERMISSION_ID`),
  CONSTRAINT `FK9C6EC938F196CF45` FOREIGN KEY (`PERMISSION_ID`) REFERENCES `permission` (`ID`),
  CONSTRAINT `FK9C6EC93814388625` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ID`)
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

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;





