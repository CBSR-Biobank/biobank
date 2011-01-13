
-- MySQL dump 10.13  Distrib 5.1.41, for Win32 (ia32)
--
-- Host: localhost    Database: biobank2
-- ------------------------------------------------------
-- Server version	5.1.41

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

--
-- Table structure for table `csm_application`
--

DROP TABLE IF EXISTS `csm_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_application` (
  `APPLICATION_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `APPLICATION_NAME` varchar(255) NOT NULL,
  `APPLICATION_DESCRIPTION` varchar(200) NOT NULL,
  `DECLARATIVE_FLAG` tinyint(1) NOT NULL DEFAULT '0',
  `ACTIVE_FLAG` tinyint(1) NOT NULL DEFAULT '0',
  `UPDATE_DATE` date DEFAULT '0000-00-00',
  `DATABASE_URL` varchar(100) DEFAULT NULL,
  `DATABASE_USER_NAME` varchar(100) DEFAULT NULL,
  `DATABASE_PASSWORD` varchar(100) DEFAULT NULL,
  `DATABASE_DIALECT` varchar(100) DEFAULT NULL,
  `DATABASE_DRIVER` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`APPLICATION_ID`),
  UNIQUE KEY `UQ_APPLICATION_NAME` (`APPLICATION_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_application`
--

LOCK TABLES `csm_application` WRITE;
/*!40000 ALTER TABLE `csm_application` DISABLE KEYS */;
INSERT INTO `csm_application` VALUES (1,'csmupt','CSM UPT Super Admin Application',0,0,'2009-07-22',NULL,NULL,NULL,NULL,NULL);
INSERT INTO `csm_application` VALUES (2,'biobank2','biobank2',0,0,'2009-07-22','jdbc:mysql://localhost:3306/biobank2','dummy','4UlzrQJztJY=','org.hibernate.dialect.MySQLDialect','com.mysql.jdbc.Driver');
/*!40000 ALTER TABLE `csm_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_filter_clause`
--

DROP TABLE IF EXISTS `csm_filter_clause`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_filter_clause` (
  `FILTER_CLAUSE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CLASS_NAME` varchar(100) NOT NULL,
  `FILTER_CHAIN` varchar(2000) NOT NULL,
  `TARGET_CLASS_NAME` varchar(100) NOT NULL,
  `TARGET_CLASS_ATTRIBUTE_NAME` varchar(100) NOT NULL,
  `TARGET_CLASS_ATTRIBUTE_TYPE` varchar(100) NOT NULL,
  `TARGET_CLASS_ALIAS` varchar(100) DEFAULT NULL,
  `TARGET_CLASS_ATTRIBUTE_ALIAS` varchar(100) DEFAULT NULL,
  `GENERATED_SQL_USER` varchar(4000) NOT NULL,
  `GENERATED_SQL_GROUP` varchar(4000) NOT NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`FILTER_CLAUSE_ID`),
  KEY `FK_APPLICATION_FILTER_CLAUSE` (`APPLICATION_ID`),
  CONSTRAINT `FK_APPLICATION_FILTER_CLAUSE` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_filter_clause`
--

LOCK TABLES `csm_filter_clause` WRITE;
/*!40000 ALTER TABLE `csm_filter_clause` DISABLE KEYS */;
INSERT INTO `csm_filter_clause` VALUES (1,'edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site - self','id','java.lang.Integer','','','id in (select s.ID from site s where s.id in ( select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.user_id = u.user_id and ugrpg.protection_group_id = any (select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id = (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= \'edu.ualberta.med.biobank.model.Site\' and pe.attribute=\'id\' and p.privilege_name=\'READ\' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID) or s.id in (select distinct pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_group g, csm_role_privilege rp, csm_role r, csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.group_id = g.group_id and ugrpg.protection_group_id = any ( select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id =  (select pg2.parent_protection_group_id from csm_protection_group pg2  where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= \'edu.ualberta.med.biobank.model.Site\' and p.privilege_name=\'READ\' and g.group_name in (select grp.group_name from csm_group grp, csm_user_group userGrp, csm_user u where u.user_id = userGrp.user_id and userGrp.group_id = grp.group_id and u.login_name=:USER_NAME) and pe.application_id=:APPLICATION_ID))','id in (select table_name_csm_.id   from site table_name_csm_ where table_name_csm_.id in \r\n( select distinct pe.attribute_value from csm_protection_group pg, 	csm_protection_element pe, 	csm_pg_pe pgpe,	csm_user_group_role_pg ugrpg, 	csm_group g, 	csm_role_privilege rp, 	csm_role r, 	csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.group_id = g.group_id and ugrpg.protection_group_id = any ( select pg1.protection_group_id from csm_protection_group pg1  where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id =  (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id) ) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= \'edu.ualberta.med.biobank.model.Site\' and p.privilege_name=\'READ\' and g.group_name in (:GROUP_NAMES ) and pe.application_id=:APPLICATION_ID))',2,'2010-01-27');
/*!40000 ALTER TABLE `csm_filter_clause` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_group`
--

DROP TABLE IF EXISTS `csm_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_group` (
  `GROUP_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `GROUP_NAME` varchar(255) NOT NULL,
  `GROUP_DESC` varchar(200) DEFAULT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  `APPLICATION_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`GROUP_ID`),
  UNIQUE KEY `UQ_GROUP_GROUP_NAME` (`APPLICATION_ID`,`GROUP_NAME`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  CONSTRAINT `FK_APPLICATION_GROUP` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_group`
--

LOCK TABLES `csm_group` WRITE;
/*!40000 ALTER TABLE `csm_group` DISABLE KEYS */;
INSERT INTO `csm_group` VALUES (5,'Website Administrator','** DO NOT RENAME **','2010-10-20',2);
INSERT INTO `csm_group` VALUES (6,'CBSR Technician Level 1','','2010-01-28',2);
INSERT INTO `csm_group` VALUES (7,'CBSR Technician Level 2','','2010-01-28',2);
INSERT INTO `csm_group` VALUES (8,'Calgary Technicians','','2010-04-20',2);
INSERT INTO `csm_group` VALUES (9,'Calgary Administrator','','2010-04-20',2);
/*!40000 ALTER TABLE `csm_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_pg_pe`
--

DROP TABLE IF EXISTS `csm_pg_pe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_pg_pe` (
  `PG_PE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PROTECTION_GROUP_ID` bigint(20) NOT NULL,
  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date DEFAULT '0000-00-00',
  PRIMARY KEY (`PG_PE_ID`),
  UNIQUE KEY `UQ_PROTECTION_GROUP_PROTECTION_ELEMENT_PROTECTION_GROUP_ID` (`PROTECTION_ELEMENT_ID`,`PROTECTION_GROUP_ID`),
  KEY `idx_PROTECTION_ELEMENT_ID` (`PROTECTION_ELEMENT_ID`),
  KEY `idx_PROTECTION_GROUP_ID` (`PROTECTION_GROUP_ID`),
  CONSTRAINT `FK_PROTECTION_ELEMENT_PROTECTION_GROUP` FOREIGN KEY (`PROTECTION_ELEMENT_ID`) REFERENCES `csm_protection_element` (`PROTECTION_ELEMENT_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PROTECTION_GROUP_PROTECTION_ELEMENT` FOREIGN KEY (`PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1128 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_pg_pe`
--

LOCK TABLES `csm_pg_pe` WRITE;
/*!40000 ALTER TABLE `csm_pg_pe` DISABLE KEYS */;
INSERT INTO `csm_pg_pe` VALUES (825,47,33,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (826,47,8,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (827,47,9,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (828,47,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (829,47,150,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (836,48,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (837,48,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (838,48,14,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (839,48,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (840,48,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (841,48,34,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (842,46,31,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (843,46,150,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (844,46,62,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (877,50,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (878,50,65,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (879,50,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (880,50,64,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (881,50,151,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (882,50,34,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (883,50,62,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (935,49,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (936,49,12,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (937,49,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (938,49,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (939,49,32,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1011,45,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1012,45,35,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1013,45,4,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1014,45,5,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1015,45,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1016,45,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1017,45,25,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1018,45,30,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1019,45,20,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1043,11,18,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1091,1,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1092,1,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1093,1,33,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1094,1,150,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1095,1,21,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1096,1,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1097,1,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1098,1,6,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1099,1,12,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1100,1,170,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1101,1,51,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1102,1,8,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1103,1,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1104,1,32,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1105,1,65,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1106,1,27,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1107,1,20,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1108,1,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1109,1,31,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1110,1,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1111,1,35,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1112,1,5,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1113,1,169,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1114,1,172,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1115,1,64,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1116,1,14,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1117,1,30,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1118,1,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1119,1,34,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1120,1,151,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1121,1,9,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1122,1,171,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1123,1,4,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1124,1,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1125,1,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1126,1,25,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1127,1,62,'0000-00-00');
/*!40000 ALTER TABLE `csm_pg_pe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_privilege`
--

DROP TABLE IF EXISTS `csm_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_privilege` (
  `PRIVILEGE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PRIVILEGE_NAME` varchar(100) NOT NULL,
  `PRIVILEGE_DESCRIPTION` varchar(200) DEFAULT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`PRIVILEGE_ID`),
  UNIQUE KEY `UQ_PRIVILEGE_NAME` (`PRIVILEGE_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_privilege`
--

LOCK TABLES `csm_privilege` WRITE;
/*!40000 ALTER TABLE `csm_privilege` DISABLE KEYS */;
INSERT INTO `csm_privilege` VALUES (1,'CREATE','This privilege grants permission to a user to create an entity. This entity can be an object, a database entry, or a resource such as a network connection','2009-07-22');
INSERT INTO `csm_privilege` VALUES (2,'ACCESS','This privilege allows a user to access a particular resource.  Examples of resources include a network or database connection, socket, module of the application, or even the application itself','2009-07-22');
INSERT INTO `csm_privilege` VALUES (3,'READ','This privilege permits the user to read data from a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to read data about a particular entry','2009-07-22');
INSERT INTO `csm_privilege` VALUES (4,'WRITE','This privilege allows a user to write data to a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to write data about a particular entity','2009-07-22');
INSERT INTO `csm_privilege` VALUES (5,'UPDATE','This privilege grants permission at an entity level and signifies that the user is allowed to update data for a particular entity. Entities may include an object, object attribute, database row etc','2009-07-22');
INSERT INTO `csm_privilege` VALUES (6,'DELETE','This privilege permits a user to delete a logical entity. This entity can be an object, a database entry, a resource such as a network connection, etc','2009-07-22');
INSERT INTO `csm_privilege` VALUES (7,'EXECUTE','This privilege allows a user to execute a particular resource. The resource can be a method, function, behavior of the application, URL, button etc','2009-07-22');
/*!40000 ALTER TABLE `csm_privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_protection_element`
--

DROP TABLE IF EXISTS `csm_protection_element`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_protection_element` (
  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PROTECTION_ELEMENT_NAME` varchar(100) NOT NULL,
  `PROTECTION_ELEMENT_DESCRIPTION` varchar(200) DEFAULT NULL,
  `OBJECT_ID` varchar(100) NOT NULL,
  `ATTRIBUTE` varchar(100) DEFAULT NULL,
  `ATTRIBUTE_VALUE` varchar(100) DEFAULT NULL,
  `PROTECTION_ELEMENT_TYPE` varchar(100) DEFAULT NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`PROTECTION_ELEMENT_ID`),
  UNIQUE KEY `UQ_PE_PE_NAME_ATTRIBUTE_VALUE_APP_ID` (`OBJECT_ID`,`ATTRIBUTE`,`ATTRIBUTE_VALUE`,`APPLICATION_ID`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  CONSTRAINT `FK_PE_APPLICATION` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_protection_element`
--

LOCK TABLES `csm_protection_element` WRITE;
/*!40000 ALTER TABLE `csm_protection_element` DISABLE KEYS */;
INSERT INTO `csm_protection_element` VALUES (1,'csmupt','CSM UPT Super Admin Application Protection Element','csmupt',NULL,NULL,NULL,1,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (2,'biobank2','biobank2','biobank2',NULL,NULL,NULL,1,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (3,'edu.ualberta.med.biobank.model.ActivityStatus','edu.ualberta.med.biobank.model.ActivityStatus','edu.ualberta.med.biobank.model.ActivityStatus','','','',2,'2010-03-04');
INSERT INTO `csm_protection_element` VALUES (4,'edu.ualberta.med.biobank.model.Address','edu.ualberta.med.biobank.model.Address','edu.ualberta.med.biobank.model.Address',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (5,'edu.ualberta.med.biobank.model.Capacity','edu.ualberta.med.biobank.model.Capacity','edu.ualberta.med.biobank.model.Capacity',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (6,'edu.ualberta.med.biobank.model.Clinic','edu.ualberta.med.biobank.model.Clinic','edu.ualberta.med.biobank.model.Clinic',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (7,'edu.ualberta.med.biobank.model.ContainerPosition','edu.ualberta.med.biobank.model.ContainerPosition','edu.ualberta.med.biobank.model.ContainerPosition',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (8,'edu.ualberta.med.biobank.model.Patient','edu.ualberta.med.biobank.model.Patient','edu.ualberta.med.biobank.model.Patient',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (9,'edu.ualberta.med.biobank.model.PatientVisit','edu.ualberta.med.biobank.model.PatientVisit','edu.ualberta.med.biobank.model.PatientVisit',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (10,'edu.ualberta.med.biobank.model.StudyPvAttr','edu.ualberta.med.biobank.model.StudyPvAttr','edu.ualberta.med.biobank.model.StudyPvAttr','','','',2,'2009-12-03');
INSERT INTO `csm_protection_element` VALUES (11,'edu.ualberta.med.biobank.model.PvAttr','edu.ualberta.med.biobank.model.PvAttr','edu.ualberta.med.biobank.model.PvAttr','','','',2,'2009-12-03');
INSERT INTO `csm_protection_element` VALUES (12,'edu.ualberta.med.biobank.model.GlobalPvAttr','edu.ualberta.med.biobank.model.GlobalPvAttr','edu.ualberta.med.biobank.model.GlobalPvAttr','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (13,'edu.ualberta.med.biobank.model.PvAttrType','edu.ualberta.med.biobank.model.PvAttrType','edu.ualberta.med.biobank.model.PvAttrType','','','',2,'2009-12-03');
INSERT INTO `csm_protection_element` VALUES (14,'edu.ualberta.med.biobank.model.Aliquot','edu.ualberta.med.biobank.model.Aliquot','edu.ualberta.med.biobank.model.Aliquot',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (15,'edu.ualberta.med.biobank.model.AliquotPosition','edu.ualberta.med.biobank.model.AliquotPosition','edu.ualberta.med.biobank.model.AliquotPosition',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (16,'edu.ualberta.med.biobank.model.SampleType','edu.ualberta.med.biobank.model.SampleType','edu.ualberta.med.biobank.model.SampleType',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (18,'edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (19,'edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','','','',2,'2010-08-19');
INSERT INTO `csm_protection_element` VALUES (20,'edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (21,'edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (24,'edu.ualberta.med.biobank.model.SourceVessel','edu.ualberta.med.biobank.model.SourceVessel','edu.ualberta.med.biobank.model.SourceVessel','','','',2,'2009-07-23');
INSERT INTO `csm_protection_element` VALUES (25,'edu.ualberta.med.biobank.model.SampleStorage','edu.ualberta.med.biobank.model.SampleStorage','edu.ualberta.med.biobank.model.SampleStorage','','','',2,'2009-07-23');
INSERT INTO `csm_protection_element` VALUES (27,'edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','','','',2,'2009-07-26');
INSERT INTO `csm_protection_element` VALUES (30,'edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','','','',2,'2009-08-24');
INSERT INTO `csm_protection_element` VALUES (31,'edu.ualberta.med.biobank.model.Shipment','edu.ualberta.med.biobank.model.Shipment','edu.ualberta.med.biobank.model.Shipment','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (32,'edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','','','',2,'2009-11-30');
INSERT INTO `csm_protection_element` VALUES (33,'edu.ualberta.med.biobank.model.PvSourceVessel','edu.ualberta.med.biobank.model.PvSourceVessel','edu.ualberta.med.biobank.model.PvSourceVessel','','','',2,'2009-11-30');
INSERT INTO `csm_protection_element` VALUES (34,'edu.ualberta.med.biobank.model.ContainerPath','edu.ualberta.med.biobank.model.ContainerPath','edu.ualberta.med.biobank.model.ContainerPath','','','',2,'2010-01-11');
INSERT INTO `csm_protection_element` VALUES (35,'edu.ualberta.med.biobank.model.StudySourceVessel','edu.ualberta.med.biobank.model.StudySourceVessel','edu.ualberta.med.biobank.model.StudySourceVessel','','','',2,'2010-04-13');
INSERT INTO `csm_protection_element` VALUES (36,'edu.ualberta.med.biobank.model.AbstractPosition','','edu.ualberta.med.biobank.model.AbstractPosition','','','',2,'2010-03-15');
INSERT INTO `csm_protection_element` VALUES (51,'edu.ualberta.med.biobank.model.Log','','edu.ualberta.med.biobank.model.Log','','','',2,'2010-05-25');
INSERT INTO `csm_protection_element` VALUES (62,'edu.ualberta.med.biobank.model.AbstractShipment','edu.ualberta.med.biobank.model.AbstractShipment','edu.ualberta.med.biobank.model.AbstractShipment','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (64,'edu.ualberta.med.biobank.model.DispatchInfo','edu.ualberta.med.biobank.model.DispatchInfo','edu.ualberta.med.biobank.model.DispatchInfo','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (65,'edu.ualberta.med.biobank.model.Dispatch','edu.ualberta.med.biobank.model.Dispatch','edu.ualberta.med.biobank.model.Dispatch','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (150,'edu.ualberta.med.biobank.model.ShipmentPatient','edu.ualberta.med.biobank.model.ShipmentPatient','edu.ualberta.med.biobank.model.ShipmentPatient','','','',2,'2010-09-29');
INSERT INTO `csm_protection_element` VALUES (151,'edu.ualberta.med.biobank.model.DispatchAliquot','edu.ualberta.med.biobank.model.DispatchAliquot','edu.ualberta.med.biobank.model.DispatchAliquot','','','',2,'2010-09-30');
INSERT INTO `csm_protection_element` VALUES (169,'edu.ualberta.med.biobank.model.Researcher','','edu.ualberta.med.biobank.model.Researcher','','','',2,'2010-12-07');
INSERT INTO `csm_protection_element` VALUES (170,'edu.ualberta.med.biobank.model.ResearchGroup','','edu.ualberta.med.biobank.model.ResearchGroup','','','',2,'2010-12-07');
INSERT INTO `csm_protection_element` VALUES (171,'edu.ualberta.med.biobank.model.Request','','edu.ualberta.med.biobank.model.Request','','','',2,'2010-12-08');
INSERT INTO `csm_protection_element` VALUES (172,'edu.ualberta.med.biobank.model.RequestAliquot','','edu.ualberta.med.biobank.model.RequestAliquot','','','',2,'2010-12-08');
/*!40000 ALTER TABLE `csm_protection_element` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_protection_group`
--

DROP TABLE IF EXISTS `csm_protection_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_protection_group` (
  `PROTECTION_GROUP_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PROTECTION_GROUP_NAME` varchar(100) NOT NULL,
  `PROTECTION_GROUP_DESCRIPTION` varchar(200) DEFAULT NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `LARGE_ELEMENT_COUNT_FLAG` tinyint(1) NOT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  `PARENT_PROTECTION_GROUP_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`PROTECTION_GROUP_ID`),
  UNIQUE KEY `UQ_PROTECTION_GROUP_PROTECTION_GROUP_NAME` (`APPLICATION_ID`,`PROTECTION_GROUP_NAME`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  KEY `idx_PARENT_PROTECTION_GROUP_ID` (`PARENT_PROTECTION_GROUP_ID`),
  CONSTRAINT `FK_PG_APPLICATION` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PROTECTION_GROUP` FOREIGN KEY (`PARENT_PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_protection_group`
--

LOCK TABLES `csm_protection_group` WRITE;
/*!40000 ALTER TABLE `csm_protection_group` DISABLE KEYS */;
INSERT INTO `csm_protection_group` VALUES (1,'All Objects','Contains Protection Element of each model object, except Site',2,0,'2010-12-08',NULL);
INSERT INTO `csm_protection_group` VALUES (11,'All Existing Sites','** DO NOT REMOVE ** Is parent of all sites protection elements + the Site class itself',2,0,'2010-10-20',NULL);
INSERT INTO `csm_protection_group` VALUES (45,'Site Administration Features','** DO NOT RENAME **\r\nContains protection elements (or protection groups children) that need privileges to manage the internal features of a site - will be available only to sites the user can update',2,0,'2010-10-20',NULL);
INSERT INTO `csm_protection_group` VALUES (46,'Clinic Shipments Feature','Represents the clinic shipments feature + contains the protection elements that need specific privileges to create/update/delete clinic shipments - will be available only to sites the user can update',2,0,'2010-10-20',45);
INSERT INTO `csm_protection_group` VALUES (47,'Patient/Patient Visit Feature','Represents the patient/visit feature + contains the protection elements that need specific privileges to create/update/delete patients and visits - will be available only to sites the user can update',2,0,'2010-10-20',45);
INSERT INTO `csm_protection_group` VALUES (48,'Link/Assign Feature','represents the aliquot link/assign feature  + contains the protection elements that need create/update/delete privileges to manage these aliquots - will be available only to sites the user can update',2,0,'2010-10-20',45);
INSERT INTO `csm_protection_group` VALUES (49,'Global Objects Administration Features','Objects that are global to all sites',2,0,'2010-10-20',NULL);
INSERT INTO `csm_protection_group` VALUES (50,'Dispatch Feature','Represent the dispatch feature + contains protection elements needed to manage dispatches - will be available only to sites the user can update',2,0,'2010-10-20',45);
/*!40000 ALTER TABLE `csm_protection_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_role`
--

DROP TABLE IF EXISTS `csm_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_role` (
  `ROLE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ROLE_NAME` varchar(100) NOT NULL,
  `ROLE_DESCRIPTION` varchar(200) DEFAULT NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `ACTIVE_FLAG` tinyint(1) NOT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`ROLE_ID`),
  UNIQUE KEY `UQ_ROLE_ROLE_NAME` (`APPLICATION_ID`,`ROLE_NAME`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  CONSTRAINT `FK_APPLICATION_ROLE` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_role`
--

LOCK TABLES `csm_role` WRITE;
/*!40000 ALTER TABLE `csm_role` DISABLE KEYS */;
INSERT INTO `csm_role` VALUES (7,'Read Only','has read privilege on objects',2,1,'2010-10-20');
INSERT INTO `csm_role` VALUES (8,'Object Full Access','has create/read/update/delete privileges on objects',2,1,'2010-10-20');
INSERT INTO `csm_role` VALUES (9,'Site Full Access','has read and update privilege on site object',2,1,'2010-10-20');
/*!40000 ALTER TABLE `csm_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_role_privilege`
--

DROP TABLE IF EXISTS `csm_role_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_role_privilege` (
  `ROLE_PRIVILEGE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ROLE_ID` bigint(20) NOT NULL,
  `PRIVILEGE_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ROLE_PRIVILEGE_ID`),
  UNIQUE KEY `UQ_ROLE_PRIVILEGE_ROLE_ID` (`PRIVILEGE_ID`,`ROLE_ID`),
  KEY `idx_PRIVILEGE_ID` (`PRIVILEGE_ID`),
  KEY `idx_ROLE_ID` (`ROLE_ID`),
  CONSTRAINT `FK_PRIVILEGE_ROLE` FOREIGN KEY (`PRIVILEGE_ID`) REFERENCES `csm_privilege` (`PRIVILEGE_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_ROLE` FOREIGN KEY (`ROLE_ID`) REFERENCES `csm_role` (`ROLE_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_role_privilege`
--

LOCK TABLES `csm_role_privilege` WRITE;
/*!40000 ALTER TABLE `csm_role_privilege` DISABLE KEYS */;
INSERT INTO `csm_role_privilege` VALUES (19,8,1);
INSERT INTO `csm_role_privilege` VALUES (16,7,3);
INSERT INTO `csm_role_privilege` VALUES (18,8,3);
INSERT INTO `csm_role_privilege` VALUES (20,8,5);
INSERT INTO `csm_role_privilege` VALUES (22,9,5);
INSERT INTO `csm_role_privilege` VALUES (17,8,6);
/*!40000 ALTER TABLE `csm_role_privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user`
--

DROP TABLE IF EXISTS `csm_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_user` (
  `USER_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `LOGIN_NAME` varchar(500) NOT NULL,
  `MIGRATED_FLAG` tinyint(1) NOT NULL DEFAULT '0',
  `FIRST_NAME` varchar(100) NOT NULL,
  `LAST_NAME` varchar(100) NOT NULL,
  `ORGANIZATION` varchar(100) DEFAULT NULL,
  `DEPARTMENT` varchar(100) DEFAULT NULL,
  `TITLE` varchar(100) DEFAULT NULL,
  `PHONE_NUMBER` varchar(15) DEFAULT NULL,
  `PASSWORD` varchar(100) DEFAULT NULL,
  `EMAIL_ID` varchar(100) DEFAULT NULL,
  `START_DATE` date DEFAULT NULL,
  `END_DATE` date DEFAULT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  `PREMGRT_LOGIN_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `UQ_LOGIN_NAME` (`LOGIN_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user`
--

LOCK TABLES `csm_user` WRITE;
/*!40000 ALTER TABLE `csm_user` DISABLE KEYS */;
INSERT INTO `csm_user` VALUES (1,'administrator',0,'Administrator','NoName',NULL,NULL,NULL,NULL,'zJPWCwDeSgG8j2uyHEABIQ==',NULL,NULL,NULL,'2009-07-22',NULL);
INSERT INTO `csm_user` VALUES (2,'bbadmin',0,'Biobank Administrator','NoName',NULL,NULL,NULL,NULL,'7Bg9siN5e7M=',NULL,NULL,NULL,'2009-07-22',NULL);
INSERT INTO `csm_user` VALUES (8,'miniaci',0,'Jessica','Miniaci','Canadian Biosample Repository','','Laboratory Technician','780-919-6735','ACrDFGBVCHOq4RawigB4Ig==','jessica.miniaci@ualberta.ca',NULL,NULL,'2010-07-08','');
INSERT INTO `csm_user` VALUES (9,'elizabeth',0,'Elizabeth','Taylor','','','','','Vzk3xic4SKi8j2uyHEABIQ==','',NULL,NULL,'2010-04-16','');
INSERT INTO `csm_user` VALUES (10,'peck',0,'Aaron','Peck','Canadian Biosample Repository','','Lab Technician','','zs4yUro9LDo=','aaron.peck@ualberta.ca',NULL,NULL,'2010-07-08','');
INSERT INTO `csm_user` VALUES (11,'holland',0,'Charity','Holland','Canadian Biosample Repository','','','','jgw6x+HUai0=','charity.holland@ualberta.ca',NULL,NULL,'2010-07-08','');
INSERT INTO `csm_user` VALUES (12,'Meagen',0,'Meagen','LaFave','','','','','Z2+3QVFL27DxoZuHa6AoWA==','cbsr.financial@me.com',NULL,NULL,'2010-06-08','');
INSERT INTO `csm_user` VALUES (13,'degrisda',0,'Delphine','Degris-Dard','','','','','CFu6ZPVAO+S8j2uyHEABIQ==','',NULL,NULL,'2010-06-30','');
INSERT INTO `csm_user` VALUES (15,'loyola',0,'Nelson','Loyola','','','','','Um6QXDsC3vs=','loyola@ualberta.ca',NULL,NULL,'2010-07-14','');
INSERT INTO `csm_user` VALUES (17,'tpolasek',0,'thomas','polasek','','','','','8y8jUYdY0sg=','',NULL,NULL,'2010-06-30','');
INSERT INTO `csm_user` VALUES (18,'aaron_aicml',0,'Aaron','Young','','','','','qmP9VkaU0jO32lSKMjM/lw==','aaron.young@ualberta.ca',NULL,NULL,'2010-10-18','');
INSERT INTO `csm_user` VALUES (19,'Andrijana',0,'Andrijana','Lawton','','','','','V4PzQj6by/Q=','',NULL,NULL,'2010-08-11','');
INSERT INTO `csm_user` VALUES (20,'Virginia',0,'Virginia','Doe','','','','','tsjSShkZ7qC8j2uyHEABIQ==','',NULL,NULL,'2010-08-11','');
/*!40000 ALTER TABLE `csm_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user_group`
--

DROP TABLE IF EXISTS `csm_user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_user_group` (
  `USER_GROUP_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` bigint(20) NOT NULL,
  `GROUP_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`USER_GROUP_ID`),
  KEY `idx_USER_ID` (`USER_ID`),
  KEY `idx_GROUP_ID` (`GROUP_ID`),
  CONSTRAINT `FK_UG_GROUP` FOREIGN KEY (`GROUP_ID`) REFERENCES `csm_group` (`GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_group`
--

LOCK TABLES `csm_user_group` WRITE;
/*!40000 ALTER TABLE `csm_user_group` DISABLE KEYS */;
INSERT INTO `csm_user_group` VALUES (23,9,5);
INSERT INTO `csm_user_group` VALUES (26,8,5);
INSERT INTO `csm_user_group` VALUES (27,13,5);
INSERT INTO `csm_user_group` VALUES (28,17,7);
INSERT INTO `csm_user_group` VALUES (32,15,5);
INSERT INTO `csm_user_group` VALUES (33,18,5);
INSERT INTO `csm_user_group` VALUES (35,10,5);
INSERT INTO `csm_user_group` VALUES (36,12,6);
INSERT INTO `csm_user_group` VALUES (44,11,5);
INSERT INTO `csm_user_group` VALUES (45,20,8);
INSERT INTO `csm_user_group` VALUES (46,19,9);
INSERT INTO `csm_user_group` VALUES (47,21,5);
/*!40000 ALTER TABLE `csm_user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user_group_role_pg`
--

DROP TABLE IF EXISTS `csm_user_group_role_pg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_user_group_role_pg` (
  `USER_GROUP_ROLE_PG_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` bigint(20) DEFAULT NULL,
  `GROUP_ID` bigint(20) DEFAULT NULL,
  `ROLE_ID` bigint(20) NOT NULL,
  `PROTECTION_GROUP_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`USER_GROUP_ROLE_PG_ID`),
  KEY `idx_GROUP_ID` (`GROUP_ID`),
  KEY `idx_ROLE_ID` (`ROLE_ID`),
  KEY `idx_PROTECTION_GROUP_ID` (`PROTECTION_GROUP_ID`),
  KEY `idx_USER_ID` (`USER_ID`),
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_GROUPS` FOREIGN KEY (`GROUP_ID`) REFERENCES `csm_group` (`GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_PROTECTION_GROUP` FOREIGN KEY (`PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_ROLE` FOREIGN KEY (`ROLE_ID`) REFERENCES `csm_role` (`ROLE_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_USER` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=193 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_group_role_pg`
--

LOCK TABLES `csm_user_group_role_pg` WRITE;
/*!40000 ALTER TABLE `csm_user_group_role_pg` DISABLE KEYS */;
INSERT INTO `csm_user_group_role_pg` VALUES (163,NULL,9,8,45,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (164,NULL,8,8,48,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (165,NULL,8,7,1,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (168,NULL,8,8,46,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (169,NULL,8,8,50,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (170,NULL,8,8,47,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (171,NULL,6,7,1,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (174,NULL,6,8,45,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (175,NULL,7,8,48,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (176,NULL,7,7,1,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (179,NULL,7,8,46,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (180,NULL,7,8,50,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (181,NULL,7,8,47,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (182,NULL,5,8,1,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (183,NULL,5,8,11,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (184,NULL,9,7,1,'2010-10-20');
/*!40000 ALTER TABLE `csm_user_group_role_pg` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user_pe`
--

DROP TABLE IF EXISTS `csm_user_pe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_user_pe` (
  `USER_PROTECTION_ELEMENT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`USER_PROTECTION_ELEMENT_ID`),
  UNIQUE KEY `UQ_USER_PROTECTION_ELEMENT_PROTECTION_ELEMENT_ID` (`USER_ID`,`PROTECTION_ELEMENT_ID`),
  KEY `idx_USER_ID` (`USER_ID`),
  KEY `idx_PROTECTION_ELEMENT_ID` (`PROTECTION_ELEMENT_ID`),
  CONSTRAINT `FK_PE_USER` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PROTECTION_ELEMENT_USER` FOREIGN KEY (`PROTECTION_ELEMENT_ID`) REFERENCES `csm_protection_element` (`PROTECTION_ELEMENT_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_pe`
--

LOCK TABLES `csm_user_pe` WRITE;
/*!40000 ALTER TABLE `csm_user_pe` DISABLE KEYS */;
INSERT INTO `csm_user_pe` VALUES (1,1,1);
INSERT INTO `csm_user_pe` VALUES (2,2,2);
/*!40000 ALTER TABLE `csm_user_pe` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-12-08 16:48:18
