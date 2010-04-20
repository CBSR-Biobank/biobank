-- MySQL dump 10.13  Distrib 5.1.37, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: biobank2
-- ------------------------------------------------------
-- Server version	5.1.37-1ubuntu5.1

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_application`
--

LOCK TABLES `csm_application` WRITE;
/*!40000 ALTER TABLE `csm_application` DISABLE KEYS */;
INSERT INTO `csm_application` VALUES (1,'csmupt','CSM UPT Super Admin Application',0,0,'2009-07-22',NULL,NULL,NULL,NULL,NULL);
INSERT INTO `csm_application` VALUES (2,'biobank2','biobank2',0,0,'2009-07-22','jdbc:mysql://localhost:3306/biobank2','dummy','4UlzrQJztJY=','org.hibernate.dialect.MySQLDialect','com.mysql.jdbc.Driver');
INSERT INTO `csm_application` VALUES (3,'CLM','CLM',1,1,'2009-07-27','jdbc:mysql://localhost:3306/biobank2','dummy','4UlzrQJztJY=','org.hibernate.dialect.MySQL5Dialect','com.mysql.jdbc.Driver');
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_filter_clause`
--

LOCK TABLES `csm_filter_clause` WRITE;
/*!40000 ALTER TABLE `csm_filter_clause` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_group`
--

LOCK TABLES `csm_group` WRITE;
/*!40000 ALTER TABLE `csm_group` DISABLE KEYS */;
INSERT INTO `csm_group` VALUES (2,'Viewers','','2009-07-22',2);
INSERT INTO `csm_group` VALUES (3,'Technicians','','2010-04-19',2);
INSERT INTO `csm_group` VALUES (4,'Administrators','Can access and modify everything','2009-07-23',2);
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
  CONSTRAINT `FK_PROTECTION_GROUP_PROTECTION_ELEMENT` FOREIGN KEY (`PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PROTECTION_ELEMENT_PROTECTION_GROUP` FOREIGN KEY (`PROTECTION_ELEMENT_ID`) REFERENCES `csm_protection_element` (`PROTECTION_ELEMENT_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_pg_pe`
--

LOCK TABLES `csm_pg_pe` WRITE;
/*!40000 ALTER TABLE `csm_pg_pe` DISABLE KEYS */;
INSERT INTO `csm_pg_pe` VALUES (25,2,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (26,2,20,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (51,9,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (53,9,14,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (54,9,25,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (60,3,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (61,3,27,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (62,3,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (63,3,5,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (65,10,29,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (66,7,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (67,7,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (68,7,12,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (69,7,26,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (71,7,21,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (72,5,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (73,5,12,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (74,5,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (75,5,26,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (76,5,8,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (78,5,9,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (79,5,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (80,6,4,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (81,6,6,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (82,6,30,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (84,6,31,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (90,7,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (91,9,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (92,5,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (93,7,33,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (94,9,33,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (95,5,33,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (96,3,34,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (98,8,31,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (99,8,32,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (100,8,18,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (101,7,35,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (102,8,4,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (103,8,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (104,8,22,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (105,11,31,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (106,11,32,'0000-00-00');
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
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=latin1;
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
INSERT INTO `csm_protection_element` VALUES (12,'edu.ualberta.med.biobank.model.SitePvAttr','edu.ualberta.med.biobank.model.SitePvAttr','edu.ualberta.med.biobank.model.SitePvAttr','','','',2,'2009-12-03');
INSERT INTO `csm_protection_element` VALUES (13,'edu.ualberta.med.biobank.model.PvAttrType','edu.ualberta.med.biobank.model.PvAttrType','edu.ualberta.med.biobank.model.PvAttrType','','','',2,'2009-12-03');
INSERT INTO `csm_protection_element` VALUES (14,'edu.ualberta.med.biobank.model.Aliquot','edu.ualberta.med.biobank.model.Aliquot','edu.ualberta.med.biobank.model.Aliquot',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (15,'edu.ualberta.med.biobank.model.AliquotPosition','edu.ualberta.med.biobank.model.AliquotPosition','edu.ualberta.med.biobank.model.AliquotPosition',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (16,'edu.ualberta.med.biobank.model.SampleType','edu.ualberta.med.biobank.model.SampleType','edu.ualberta.med.biobank.model.SampleType',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (18,'edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (19,'edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (20,'edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (21,'edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (22,'edu.ualberta.med.biobank.model.User','edu.ualberta.med.biobank.model.User','edu.ualberta.med.biobank.model.User',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (24,'edu.ualberta.med.biobank.model.SourceVessel','edu.ualberta.med.biobank.model.SourceVessel','edu.ualberta.med.biobank.model.SourceVessel','','','',2,'2009-07-23');
INSERT INTO `csm_protection_element` VALUES (25,'edu.ualberta.med.biobank.model.SampleStorage','edu.ualberta.med.biobank.model.SampleStorage','edu.ualberta.med.biobank.model.SampleStorage','','','',2,'2009-07-23');
INSERT INTO `csm_protection_element` VALUES (27,'edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','','','',2,'2009-07-26');
INSERT INTO `csm_protection_element` VALUES (28,'CLM','','CLM','','','',1,'2009-07-27');
INSERT INTO `csm_protection_element` VALUES (29,'APPLICATION_NAME:biobank2','','APPLICATION_NAME:biobank2','','','',3,'2009-07-27');
INSERT INTO `csm_protection_element` VALUES (30,'edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','','','',2,'2009-08-24');
INSERT INTO `csm_protection_element` VALUES (31,'edu.ualberta.med.biobank.model.Shipment','edu.ualberta.med.biobank.model.Shipment','edu.ualberta.med.biobank.model.Shipment','','','',2,'2009-11-24');
INSERT INTO `csm_protection_element` VALUES (32,'edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','','','',2,'2009-11-30');
INSERT INTO `csm_protection_element` VALUES (33,'edu.ualberta.med.biobank.model.PvSourceVessel','edu.ualberta.med.biobank.model.PvSourceVessel','edu.ualberta.med.biobank.model.PvSourceVessel','','','',2,'2009-11-30');
INSERT INTO `csm_protection_element` VALUES (34,'edu.ualberta.med.biobank.model.ContainerPath','edu.ualberta.med.biobank.model.ContainerPath','edu.ualberta.med.biobank.model.ContainerPath','','','',2,'2010-01-11');
INSERT INTO `csm_protection_element` VALUES (35,'edu.ualberta.med.biobank.model.StudySourceVessel','edu.ualberta.med.biobank.model.StudySourceVessel','edu.ualberta.med.biobank.model.StudySourceVessel','','','',2,'2010-04-13');
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
  CONSTRAINT `FK_PROTECTION_GROUP` FOREIGN KEY (`PARENT_PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`),
  CONSTRAINT `FK_PG_APPLICATION` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_protection_group`
--

LOCK TABLES `csm_protection_group` WRITE;
/*!40000 ALTER TABLE `csm_protection_group` DISABLE KEYS */;
INSERT INTO `csm_protection_group` VALUES (1,'pg-biobank-all','Protection for all the classes',2,0,'2009-07-22',NULL);
INSERT INTO `csm_protection_group` VALUES (2,'pg-types','Types defined',2,0,'2009-07-22',1);
INSERT INTO `csm_protection_group` VALUES (3,'pg-containers','',2,0,'2009-07-22',1);
INSERT INTO `csm_protection_group` VALUES (5,'pg-patients','',2,0,'2009-07-23',1);
INSERT INTO `csm_protection_group` VALUES (6,'pg-clinics','',2,0,'2009-07-23',1);
INSERT INTO `csm_protection_group` VALUES (7,'pg-study','',2,0,'2009-07-23',1);
INSERT INTO `csm_protection_group` VALUES (8,'pg-others','',2,0,'2009-07-23',1);
INSERT INTO `csm_protection_group` VALUES (9,'pg-samples','',2,0,'2009-07-23',1);
INSERT INTO `csm_protection_group` VALUES (10,'pg-biobank-clm','',3,0,'2009-07-27',NULL);
INSERT INTO `csm_protection_group` VALUES (11,'pg-shipments','shipments',2,0,'2010-04-19',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_role`
--

LOCK TABLES `csm_role` WRITE;
/*!40000 ALTER TABLE `csm_role` DISABLE KEYS */;
INSERT INTO `csm_role` VALUES (1,'read','',2,1,'2009-07-22');
INSERT INTO `csm_role` VALUES (2,'create-delete-update','',2,1,'2009-07-22');
INSERT INTO `csm_role` VALUES (3,'READ','',3,1,'2009-07-27');
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
  CONSTRAINT `FK_ROLE` FOREIGN KEY (`ROLE_ID`) REFERENCES `csm_role` (`ROLE_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PRIVILEGE_ROLE` FOREIGN KEY (`PRIVILEGE_ID`) REFERENCES `csm_privilege` (`PRIVILEGE_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_role_privilege`
--

LOCK TABLES `csm_role_privilege` WRITE;
/*!40000 ALTER TABLE `csm_role_privilege` DISABLE KEYS */;
INSERT INTO `csm_role_privilege` VALUES (10,2,1);
INSERT INTO `csm_role_privilege` VALUES (8,1,3);
INSERT INTO `csm_role_privilege` VALUES (12,3,3);
INSERT INTO `csm_role_privilege` VALUES (11,2,5);
INSERT INTO `csm_role_privilege` VALUES (9,2,6);
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user`
--

LOCK TABLES `csm_user` WRITE;
/*!40000 ALTER TABLE `csm_user` DISABLE KEYS */;
INSERT INTO `csm_user` VALUES (1,'administrator',0,'Administrator','NoName',NULL,NULL,NULL,NULL,'zJPWCwDeSgG8j2uyHEABIQ==',NULL,NULL,NULL,'2009-07-22',NULL);
INSERT INTO `csm_user` VALUES (2,'bbadmin',0,'Biobank Administrator','NoName',NULL,NULL,NULL,NULL,'7Bg9siN5e7M=',NULL,NULL,NULL,'2009-07-22',NULL);
INSERT INTO `csm_user` VALUES (3,'testuser',0,'TestUser','NoName',NULL,NULL,NULL,NULL,'orDBlaojDQE=',NULL,NULL,NULL,'2009-07-22',NULL);
INSERT INTO `csm_user` VALUES (4,'visitor',0,'visitor','visitor','','','','','xetbcEQIhCk=','',NULL,NULL,'2009-07-23','');
INSERT INTO `csm_user` VALUES (5,'tech1',0,'tech1','tech1','','','','','XrqZ9qCiTTo=','',NULL,NULL,'2009-07-23','');
INSERT INTO `csm_user` VALUES (6,'clmadmin',0,'clmadmin','clmadmin','','','','','tBGnppnJRZW8j2uyHEABIQ==','',NULL,NULL,'2009-07-27','');
INSERT INTO `csm_user` VALUES (7,'clm',0,'clm','clm','','','','','nGNTxuVEogo=','',NULL,NULL,'2009-07-27','');
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_group`
--

LOCK TABLES `csm_user_group` WRITE;
/*!40000 ALTER TABLE `csm_user_group` DISABLE KEYS */;
INSERT INTO `csm_user_group` VALUES (13,3,4);
INSERT INTO `csm_user_group` VALUES (14,2,4);
INSERT INTO `csm_user_group` VALUES (15,4,2);
INSERT INTO `csm_user_group` VALUES (16,5,3);
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
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_USER` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_GROUPS` FOREIGN KEY (`GROUP_ID`) REFERENCES `csm_group` (`GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_PROTECTION_GROUP` FOREIGN KEY (`PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_ROLE` FOREIGN KEY (`ROLE_ID`) REFERENCES `csm_role` (`ROLE_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_group_role_pg`
--

LOCK TABLES `csm_user_group_role_pg` WRITE;
/*!40000 ALTER TABLE `csm_user_group_role_pg` DISABLE KEYS */;
INSERT INTO `csm_user_group_role_pg` VALUES (7,NULL,3,2,3,'2009-07-22');
INSERT INTO `csm_user_group_role_pg` VALUES (8,NULL,3,1,3,'2009-07-22');
INSERT INTO `csm_user_group_role_pg` VALUES (14,NULL,4,2,1,'2009-07-23');
INSERT INTO `csm_user_group_role_pg` VALUES (15,NULL,4,1,1,'2009-07-23');
INSERT INTO `csm_user_group_role_pg` VALUES (16,NULL,3,2,9,'2009-07-23');
INSERT INTO `csm_user_group_role_pg` VALUES (17,NULL,3,1,9,'2009-07-23');
INSERT INTO `csm_user_group_role_pg` VALUES (18,NULL,3,2,5,'2009-07-23');
INSERT INTO `csm_user_group_role_pg` VALUES (19,NULL,3,1,5,'2009-07-23');
INSERT INTO `csm_user_group_role_pg` VALUES (20,NULL,2,1,1,'2009-07-23');
INSERT INTO `csm_user_group_role_pg` VALUES (21,NULL,3,1,1,'2009-07-23');
INSERT INTO `csm_user_group_role_pg` VALUES (22,7,NULL,3,10,'2009-07-27');
INSERT INTO `csm_user_group_role_pg` VALUES (23,NULL,3,2,11,'2010-04-19');
INSERT INTO `csm_user_group_role_pg` VALUES (24,NULL,3,1,11,'2010-04-19');
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
  CONSTRAINT `FK_PROTECTION_ELEMENT_USER` FOREIGN KEY (`PROTECTION_ELEMENT_ID`) REFERENCES `csm_protection_element` (`PROTECTION_ELEMENT_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PE_USER` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_pe`
--

LOCK TABLES `csm_user_pe` WRITE;
/*!40000 ALTER TABLE `csm_user_pe` DISABLE KEYS */;
INSERT INTO `csm_user_pe` VALUES (1,1,1);
INSERT INTO `csm_user_pe` VALUES (2,2,2);
INSERT INTO `csm_user_pe` VALUES (3,28,6);
/*!40000 ALTER TABLE `csm_user_pe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_message`
--

DROP TABLE IF EXISTS `log_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_message` (
  `LOG_ID` bigint(200) NOT NULL AUTO_INCREMENT,
  `APPLICATION` varchar(25) DEFAULT NULL,
  `SERVER` varchar(50) DEFAULT NULL,
  `CATEGORY` varchar(255) DEFAULT NULL,
  `THREAD` varchar(255) DEFAULT NULL,
  `USERNAME` varchar(255) DEFAULT NULL,
  `SESSION_ID` varchar(255) DEFAULT NULL,
  `MSG` text,
  `THROWABLE` text,
  `NDC` text,
  `CREATED_ON` bigint(20) NOT NULL,
  `OBJECT_ID` varchar(255) DEFAULT NULL,
  `OBJECT_NAME` varchar(255) DEFAULT NULL,
  `ORGANIZATION` varchar(255) DEFAULT NULL,
  `OPERATION` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`LOG_ID`),
  KEY `APPLICATION_LOGTAB_INDX` (`APPLICATION`),
  KEY `SERVER_LOGTAB_INDX` (`SERVER`),
  KEY `THREAD_LOGTAB_INDX` (`THREAD`),
  KEY `CREATED_ON_LOGTAB_INDX` (`CREATED_ON`),
  KEY `LOGID_LOGTAB_INDX` (`LOG_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=163921 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `object_attribute`
--

DROP TABLE IF EXISTS `object_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `object_attribute` (
  `OBJECT_ATTRIBUTE_ID` bigint(200) NOT NULL AUTO_INCREMENT,
  `CURRENT_VALUE` varchar(255) DEFAULT NULL,
  `PREVIOUS_VALUE` varchar(255) DEFAULT NULL,
  `ATTRIBUTE` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`OBJECT_ATTRIBUTE_ID`),
  KEY `OAID_INDX` (`OBJECT_ATTRIBUTE_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=212492 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `objectattributes`
--

DROP TABLE IF EXISTS `objectattributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `objectattributes` (
  `LOG_ID` bigint(200) NOT NULL DEFAULT '0',
  `OBJECT_ATTRIBUTE_ID` bigint(200) NOT NULL DEFAULT '0',
  KEY `Index_2` (`LOG_ID`),
  KEY `FK_objectattributes_2` (`OBJECT_ATTRIBUTE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-04-19 15:18:33
