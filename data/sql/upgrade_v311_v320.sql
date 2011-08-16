-- add new objects into the csm database:
INSERT INTO csm_protection_element VALUES (198,'edu.ualberta.med.biobank.model.User','','edu.ualberta.med.biobank.model.User','','','',2,'2011-08-15');
INSERT INTO csm_protection_element VALUES (199,'edu.ualberta.med.biobank.model.Group','','edu.ualberta.med.biobank.model.Group','','','',2,'2011-08-15');
INSERT INTO csm_protection_element VALUES (200,'edu.ualberta.med.biobank.model.Principal','','edu.ualberta.med.biobank.model.Principal','','','',2,'2011-08-15');
INSERT INTO csm_protection_element VALUES (201,'edu.ualberta.med.biobank.model.Membership','','edu.ualberta.med.biobank.model.Membership','','','',2,'2011-08-15');
INSERT INTO csm_protection_element VALUES (202,'edu.ualberta.med.biobank.model.MembershipObject','','edu.ualberta.med.biobank.model.MembershipObject','','','',2,'2011-08-15');
INSERT INTO csm_protection_element VALUES (203,'edu.ualberta.med.biobank.model.RightPrivilege','','edu.ualberta.med.biobank.model.RightPrivilege','','','',2,'2011-08-15');
INSERT INTO csm_protection_element VALUES (204,'edu.ualberta.med.biobank.model.Right','','edu.ualberta.med.biobank.model.Right','','','',2,'2011-08-15');
INSERT INTO csm_protection_element VALUES (205,'edu.ualberta.med.biobank.model.Privilege','','edu.ualberta.med.biobank.model.Privilege','','','',2,'2011-08-15');
INSERT INTO csm_protection_element VALUES (206,'edu.ualberta.med.biobank.model.Role','','edu.ualberta.med.biobank.model.Role','','','',2,'2011-08-15');

-- add and remove associations from pg-pe relation:
delete from csm_pg_pe where pg_pe_id = 1289;
delete from csm_pg_pe where pg_pe_id = 1290;
delete from csm_pg_pe where pg_pe_id = 1291;
delete from csm_pg_pe where pg_pe_id = 1292;
delete from csm_pg_pe where pg_pe_id = 1293;
delete from csm_pg_pe where pg_pe_id = 1294;
delete from csm_pg_pe where pg_pe_id = 1295;
delete from csm_pg_pe where pg_pe_id = 1296;
delete from csm_pg_pe where pg_pe_id = 1297;
delete from csm_pg_pe where pg_pe_id = 1298;
delete from csm_pg_pe where pg_pe_id = 1299;
delete from csm_pg_pe where pg_pe_id = 1300;
delete from csm_pg_pe where pg_pe_id = 1301;
delete from csm_pg_pe where pg_pe_id = 1302;
delete from csm_pg_pe where pg_pe_id = 1303;
delete from csm_pg_pe where pg_pe_id = 1304;
delete from csm_pg_pe where pg_pe_id = 1305;
delete from csm_pg_pe where pg_pe_id = 1306;
delete from csm_pg_pe where pg_pe_id = 1307;
delete from csm_pg_pe where pg_pe_id = 1308;
delete from csm_pg_pe where pg_pe_id = 1309;
delete from csm_pg_pe where pg_pe_id = 1310;
delete from csm_pg_pe where pg_pe_id = 1311;
delete from csm_pg_pe where pg_pe_id = 1312;
delete from csm_pg_pe where pg_pe_id = 1313;
delete from csm_pg_pe where pg_pe_id = 1314;
delete from csm_pg_pe where pg_pe_id = 1315;
delete from csm_pg_pe where pg_pe_id = 1316;
delete from csm_pg_pe where pg_pe_id = 1318;
delete from csm_pg_pe where pg_pe_id = 1320;
delete from csm_pg_pe where pg_pe_id = 1321;
delete from csm_pg_pe where pg_pe_id = 1322;
delete from csm_pg_pe where pg_pe_id = 1323;
delete from csm_pg_pe where pg_pe_id = 1324;
delete from csm_pg_pe where pg_pe_id = 1325;
delete from csm_pg_pe where pg_pe_id = 1326;
delete from csm_pg_pe where pg_pe_id = 1327;
delete from csm_pg_pe where pg_pe_id = 1328;
delete from csm_pg_pe where pg_pe_id = 1329;
delete from csm_pg_pe where pg_pe_id = 1330;
delete from csm_pg_pe where pg_pe_id = 1331;
delete from csm_pg_pe where pg_pe_id = 1332;
delete from csm_pg_pe where pg_pe_id = 1333;
delete from csm_pg_pe where pg_pe_id = 1334;
delete from csm_pg_pe where pg_pe_id = 1418;
delete from csm_pg_pe where pg_pe_id = 1420;
delete from csm_pg_pe where pg_pe_id = 1422;
insert into csm_pg_pe () values 
(1428,1,186,'0000-00-00'),
(1429,1,18,'0000-00-00'),
(1430,1,25,'0000-00-00'),
(1431,1,7,'0000-00-00'),
(1432,1,10,'0000-00-00'),
(1433,1,180,'0000-00-00'),
(1434,1,21,'0000-00-00'),
(1435,1,36,'0000-00-00'),
(1436,1,204,'0000-00-00'),
(1437,1,170,'0000-00-00'),
(1438,1,8,'0000-00-00'),
(1439,1,201,'0000-00-00'),
(1440,1,24,'0000-00-00'),
(1441,1,32,'0000-00-00'),
(1442,1,27,'0000-00-00'),
(1443,1,65,'0000-00-00'),
(1444,1,183,'0000-00-00'),
(1445,1,13,'0000-00-00'),
(1446,1,195,'0000-00-00'),
(1447,1,187,'0000-00-00'),
(1448,1,192,'0000-00-00'),
(1449,1,200,'0000-00-00'),
(1450,1,11,'0000-00-00'),
(1451,1,177,'0000-00-00'),
(1452,1,181,'0000-00-00'),
(1453,1,196,'0000-00-00'),
(1454,1,184,'0000-00-00'),
(1455,1,35,'0000-00-00'),
(1456,1,176,'0000-00-00'),
(1457,1,185,'0000-00-00'),
(1458,1,3,'0000-00-00'),
(1459,1,12,'0000-00-00'),
(1460,1,182,'0000-00-00'),
(1461,1,206,'0000-00-00'),
(1462,1,205,'0000-00-00'),
(1463,1,199,'0000-00-00'),
(1464,1,178,'0000-00-00'),
(1465,1,197,'0000-00-00'),
(1466,1,203,'0000-00-00'),
(1467,1,6,'0000-00-00');


-- add new security tables
CREATE TABLE `group` (
  `PRINCIPAL_ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `group_user` (
  `USER_ID` int(11) NOT NULL,
  `GROUP_ID` int(11) NOT NULL,
  PRIMARY KEY (`GROUP_ID`,`USER_ID`),
  KEY `FK6B1EC1ABB9634A05` (`USER_ID`),
  CONSTRAINT `FK6B1EC1ABB9634A05` FOREIGN KEY (`USER_ID`) REFERENCES `user` (`PRINCIPAL_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `membership` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `STUDY_ID` int(11) DEFAULT NULL,
  `CENTER_ID` int(11) DEFAULT NULL,
  `PRINCIPAL_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKCD0773D6FF154DAF` (`PRINCIPAL_ID`),
  KEY `FKCD0773D6F2A2464F` (`STUDY_ID`),
  KEY `FKCD0773D692FAA705` (`CENTER_ID`),
  CONSTRAINT `FKCD0773D692FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKCD0773D6F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`),
  CONSTRAINT `FKCD0773D6FF154DAF` FOREIGN KEY (`PRINCIPAL_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `membership_membership_object` (
  `MEMBERSHIP_ID` int(11) NOT NULL,
  `MEMBERSHIP_OBJECT_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEMBERSHIP_ID`,`MEMBERSHIP_OBJECT_ID`),
  KEY `FK9841979FD26ABDE5` (`MEMBERSHIP_ID`),
  KEY `FK9841979FA788A752` (`MEMBERSHIP_OBJECT_ID`),
  CONSTRAINT `FK9841979FA788A752` FOREIGN KEY (`MEMBERSHIP_OBJECT_ID`) REFERENCES `membership_object` (`ID`),
  CONSTRAINT `FK9841979FD26ABDE5` FOREIGN KEY (`MEMBERSHIP_ID`) REFERENCES `membership` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `membership_object` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `principal` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `privilege` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `right` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `right_privilege` (
  `MEMBERSHIP_OBJECT_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) DEFAULT NULL,
  `RIGHT_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEMBERSHIP_OBJECT_ID`),
  KEY `FK4B32800E14388625` (`ROLE_ID`),
  KEY `FK4B32800EA788A752` (`MEMBERSHIP_OBJECT_ID`),
  CONSTRAINT `FK4B32800EA788A752` FOREIGN KEY (`MEMBERSHIP_OBJECT_ID`) REFERENCES `membership_object` (`ID`),
  CONSTRAINT `FK4B32800E14388625` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`MEMBERSHIP_OBJECT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `right_privilege_privilege` (
  `RIGHT_PRIVILEGE_ID` int(11) NOT NULL,
  `PRIVILEGE_ID` int(11) NOT NULL,
  PRIMARY KEY (`RIGHT_PRIVILEGE_ID`,`PRIVILEGE_ID`),
  KEY `FKE1E847A0AABB1ACF` (`PRIVILEGE_ID`),
  KEY `FKE1E847A03267910C` (`RIGHT_PRIVILEGE_ID`),
  CONSTRAINT `FKE1E847A03267910C` FOREIGN KEY (`RIGHT_PRIVILEGE_ID`) REFERENCES `right_privilege` (`MEMBERSHIP_OBJECT_ID`),
  CONSTRAINT `FKE1E847A0AABB1ACF` FOREIGN KEY (`PRIVILEGE_ID`) REFERENCES `privilege` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `role` (
  `MEMBERSHIP_OBJECT_ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`MEMBERSHIP_OBJECT_ID`),
  KEY `FK267876A788A752` (`MEMBERSHIP_OBJECT_ID`),
  CONSTRAINT `FK267876A788A752` FOREIGN KEY (`MEMBERSHIP_OBJECT_ID`) REFERENCES `membership_object` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

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



-- will need to convert users from csm to users from biobank:
-- TODO




