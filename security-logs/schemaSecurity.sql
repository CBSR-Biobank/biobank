-- MySQL dump 10.13  Distrib 5.1.54, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: biobank
-- ------------------------------------------------------
-- Server version	5.1.54-1ubuntu4

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
INSERT INTO `csm_application` VALUES (2,'biobank','biobank',0,0,'2009-07-22','jdbc:mysql://localhost:3306/biobank','dummy','4UlzrQJztJY=','org.hibernate.dialect.MySQLDialect','com.mysql.jdbc.Driver');
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
) ENGINE=InnoDB AUTO_INCREMENT=1484 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_pg_pe`
--

LOCK TABLES `csm_pg_pe` WRITE;
/*!40000 ALTER TABLE `csm_pg_pe` DISABLE KEYS */;
INSERT INTO `csm_pg_pe` VALUES (1428,1,186,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1429,1,18,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1430,1,25,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1431,1,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1432,1,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1433,1,180,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1434,1,21,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1435,1,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1436,1,204,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1437,1,170,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1438,1,8,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1439,1,201,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1440,1,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1441,1,32,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1442,1,27,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1443,1,65,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1444,1,183,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1445,1,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1446,1,195,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1447,1,187,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1448,1,192,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1449,1,200,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1450,1,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1451,1,177,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1452,1,181,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1453,1,196,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1454,1,184,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1455,1,35,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1456,1,176,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1457,1,185,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1458,1,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1459,1,12,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1460,1,182,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1461,1,206,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1462,1,205,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1463,1,199,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1464,1,178,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1465,1,197,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1466,1,203,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1467,1,6,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1468,1,188,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1469,1,51,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1470,1,151,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1471,1,20,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1472,1,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1473,1,179,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1474,1,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1475,1,5,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1476,1,30,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1477,1,193,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1478,1,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1479,1,175,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1480,1,198,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1481,1,171,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1482,1,4,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1483,1,202,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1484,1,207,'0000-00-00');
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
) ENGINE=InnoDB AUTO_INCREMENT=207 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_protection_element`
--

LOCK TABLES `csm_protection_element` WRITE;
/*!40000 ALTER TABLE `csm_protection_element` DISABLE KEYS */;
INSERT INTO `csm_protection_element` VALUES (1,'csmupt','CSM UPT Super Admin Application Protection Element','csmupt',NULL,NULL,NULL,1,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (2,'biobank','biobank','biobank',NULL,NULL,NULL,1,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (3,'edu.ualberta.med.biobank.model.ActivityStatus','edu.ualberta.med.biobank.model.ActivityStatus','edu.ualberta.med.biobank.model.ActivityStatus','','','',2,'2010-03-04');
INSERT INTO `csm_protection_element` VALUES (4,'edu.ualberta.med.biobank.model.Address','edu.ualberta.med.biobank.model.Address','edu.ualberta.med.biobank.model.Address',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (5,'edu.ualberta.med.biobank.model.Capacity','edu.ualberta.med.biobank.model.Capacity','edu.ualberta.med.biobank.model.Capacity',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (6,'edu.ualberta.med.biobank.model.Clinic','edu.ualberta.med.biobank.model.Clinic','edu.ualberta.med.biobank.model.Clinic',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (7,'edu.ualberta.med.biobank.model.ContainerPosition','edu.ualberta.med.biobank.model.ContainerPosition','edu.ualberta.med.biobank.model.ContainerPosition',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (8,'edu.ualberta.med.biobank.model.Patient','edu.ualberta.med.biobank.model.Patient','edu.ualberta.med.biobank.model.Patient',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (10,'edu.ualberta.med.biobank.model.StudyEventAttr','edu.ualberta.med.biobank.model.StudyEventAttr','edu.ualberta.med.biobank.model.StudyEventAttr','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (11,'edu.ualberta.med.biobank.model.EventAttr','edu.ualberta.med.biobank.model.EventAttr','edu.ualberta.med.biobank.model.EventAttr','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (12,'edu.ualberta.med.biobank.model.GlobalEventAttr','edu.ualberta.med.biobank.model.GlobalEventAttr','edu.ualberta.med.biobank.model.GlobalEventAttr','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (13,'edu.ualberta.med.biobank.model.EventAttrType','edu.ualberta.med.biobank.model.EventAttrType','edu.ualberta.med.biobank.model.EventAttrType','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (15,'edu.ualberta.med.biobank.model.SpecimenPosition','edu.ualberta.med.biobank.model.SpecimenPosition','edu.ualberta.med.biobank.model.SpecimenPosition','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (16,'edu.ualberta.med.biobank.model.OriginInfo','edu.ualberta.med.biobank.model.OriginInfo','edu.ualberta.med.biobank.model.OriginInfo','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (18,'edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (19,'edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','','','',2,'2010-08-19');
INSERT INTO `csm_protection_element` VALUES (20,'edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (21,'edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (24,'edu.ualberta.med.biobank.model.Specimen','edu.ualberta.med.biobank.model.Specimen','edu.ualberta.med.biobank.model.Specimen','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (25,'edu.ualberta.med.biobank.model.AliquotedSpecimen','edu.ualberta.med.biobank.model.AliquotedSpecimen','edu.ualberta.med.biobank.model.AliquotedSpecimen','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (27,'edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','','','',2,'2009-07-26');
INSERT INTO `csm_protection_element` VALUES (30,'edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','','','',2,'2009-08-24');
INSERT INTO `csm_protection_element` VALUES (32,'edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','','','',2,'2009-11-30');
INSERT INTO `csm_protection_element` VALUES (35,'edu.ualberta.med.biobank.model.SourceSpecimen','edu.ualberta.med.biobank.model.SourceSpecimen','edu.ualberta.med.biobank.model.SourceSpecimen','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (36,'edu.ualberta.med.biobank.model.AbstractPosition','','edu.ualberta.med.biobank.model.AbstractPosition','','','',2,'2010-03-15');
INSERT INTO `csm_protection_element` VALUES (51,'edu.ualberta.med.biobank.model.Log','','edu.ualberta.med.biobank.model.Log','','','',2,'2010-05-25');
INSERT INTO `csm_protection_element` VALUES (65,'edu.ualberta.med.biobank.model.Dispatch','edu.ualberta.med.biobank.model.Dispatch','edu.ualberta.med.biobank.model.Dispatch','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (151,'edu.ualberta.med.biobank.model.DispatchSpecimen','edu.ualberta.med.biobank.model.DispatchSpecimen','edu.ualberta.med.biobank.model.DispatchSpecimen','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (170,'edu.ualberta.med.biobank.model.ResearchGroup','','edu.ualberta.med.biobank.model.ResearchGroup','','','',2,'2010-12-07');
INSERT INTO `csm_protection_element` VALUES (171,'edu.ualberta.med.biobank.model.Request','','edu.ualberta.med.biobank.model.Request','','','',2,'2010-12-08');
INSERT INTO `csm_protection_element` VALUES (175,'edu.ualberta.med.biobank.model.Report','','edu.ualberta.med.biobank.model.Report','','','',2,'2011-01-13');
INSERT INTO `csm_protection_element` VALUES (176,'edu.ualberta.med.biobank.model.ReportFilter','','edu.ualberta.med.biobank.model.ReportFilter','','','',2,'2011-01-13');
INSERT INTO `csm_protection_element` VALUES (177,'edu.ualberta.med.biobank.model.ReportFilterValue','','edu.ualberta.med.biobank.model.ReportFilterValue','','','',2,'2011-01-13');
INSERT INTO `csm_protection_element` VALUES (178,'edu.ualberta.med.biobank.model.ReportColumn','','edu.ualberta.med.biobank.model.ReportColumn','','','',2,'2011-01-13');
INSERT INTO `csm_protection_element` VALUES (179,'edu.ualberta.med.biobank.model.Entity','','edu.ualberta.med.biobank.model.Entity','','','',2,'2011-01-13');
INSERT INTO `csm_protection_element` VALUES (180,'edu.ualberta.med.biobank.model.EntityFilter','','edu.ualberta.med.biobank.model.EntityFilter','','','',2,'2011-01-13');
INSERT INTO `csm_protection_element` VALUES (181,'edu.ualberta.med.biobank.model.EntityColumn','','edu.ualberta.med.biobank.model.EntityColumn','','','',2,'2011-01-13');
INSERT INTO `csm_protection_element` VALUES (182,'edu.ualberta.med.biobank.model.EntityProperty','','edu.ualberta.med.biobank.model.EntityProperty','','','',2,'2011-01-13');
INSERT INTO `csm_protection_element` VALUES (183,'edu.ualberta.med.biobank.model.PropertyModifier','','edu.ualberta.med.biobank.model.PropertyModifier','','','',2,'2011-01-13');
INSERT INTO `csm_protection_element` VALUES (184,'edu.ualberta.med.biobank.model.PropertyType','','edu.ualberta.med.biobank.model.PropertyType','','','',2,'2011-01-13');
INSERT INTO `csm_protection_element` VALUES (185,'edu.ualberta.med.biobank.model.CollectionEvent','','edu.ualberta.med.biobank.model.CollectionEvent','','','',2,'2011-02-15');
INSERT INTO `csm_protection_element` VALUES (186,'edu.ualberta.med.biobank.model.ProcessingEvent','','edu.ualberta.med.biobank.model.ProcessingEvent','','','',2,'2011-02-15');
INSERT INTO `csm_protection_element` VALUES (187,'edu.ualberta.med.biobank.model.SpecimenType','edu.ualberta.med.biobank.model.SpecimenType','edu.ualberta.med.biobank.model.SpecimenType','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (188,'edu.ualberta.med.biobank.model.Center','','edu.ualberta.med.biobank.model.Center','','','',2,'2011-02-15');
INSERT INTO `csm_protection_element` VALUES (192,'edu.ualberta.med.biobank.model.RequestSpecimen','','edu.ualberta.med.biobank.model.RequestSpecimen','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (193,'edu.ualberta.med.biobank.model.ShipmentInfo','edu.ualberta.med.biobank.model.ShipmentInfo','edu.ualberta.med.biobank.model.ShipmentInfo','','','',2,'2011-02-28');
INSERT INTO `csm_protection_element` VALUES (195,'edu.ualberta.med.biobank.model.PrintedSsInvItem','','edu.ualberta.med.biobank.model.PrintedSsInvItem','','','',2,'2011-06-06');
INSERT INTO `csm_protection_element` VALUES (196,'edu.ualberta.med.biobank.model.PrinterLabelTemplate','','edu.ualberta.med.biobank.model.PrinterLabelTemplate','','','',2,'2011-06-06');
INSERT INTO `csm_protection_element` VALUES (197,'edu.ualberta.med.biobank.model.JasperTemplate','','edu.ualberta.med.biobank.model.JasperTemplate','','','',2,'2011-06-07');
INSERT INTO `csm_protection_element` VALUES (198,'edu.ualberta.med.biobank.model.User','','edu.ualberta.med.biobank.model.User','','','',2,'2011-08-15');
INSERT INTO `csm_protection_element` VALUES (199,'edu.ualberta.med.biobank.model.BbGroup','','edu.ualberta.med.biobank.model.BbGroup','','','',2,'2011-08-15');
INSERT INTO `csm_protection_element` VALUES (200,'edu.ualberta.med.biobank.model.Principal','','edu.ualberta.med.biobank.model.Principal','','','',2,'2011-08-15');
INSERT INTO `csm_protection_element` VALUES (201,'edu.ualberta.med.biobank.model.Membership','','edu.ualberta.med.biobank.model.Membership','','','',2,'2011-08-15');
INSERT INTO `csm_protection_element` VALUES (204,'edu.ualberta.med.biobank.model.Permission','','edu.ualberta.med.biobank.model.Permission','','','',2,'2011-08-15');
INSERT INTO `csm_protection_element` VALUES (205,'edu.ualberta.med.biobank.model.BbRight','','edu.ualberta.med.biobank.model.BbRight','','','',2,'2011-08-15');
INSERT INTO `csm_protection_element` VALUES (206,'edu.ualberta.med.biobank.model.Privilege','','edu.ualberta.med.biobank.model.Privilege','','','',2,'2011-08-15');
INSERT INTO `csm_protection_element` VALUES (207,'edu.ualberta.med.biobank.model.Role','','edu.ualberta.med.biobank.model.Role','','','',2,'2011-08-15');
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
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_protection_group`
--

LOCK TABLES `csm_protection_group` WRITE;
/*!40000 ALTER TABLE `csm_protection_group` DISABLE KEYS */;
INSERT INTO `csm_protection_group` VALUES (1,'Internal: All Objects','Contains Protection Element of each model object',2,0,'2011-03-11',NULL);
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
INSERT INTO `csm_role` VALUES (8,'Object Full Access','has create/read/update/delete privileges on objects',2,1,'2010-10-20');
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_role_privilege`
--

LOCK TABLES `csm_role_privilege` WRITE;
/*!40000 ALTER TABLE `csm_role_privilege` DISABLE KEYS */;
INSERT INTO `csm_role_privilege` VALUES (19,8,1);
INSERT INTO `csm_role_privilege` VALUES (18,8,3);
INSERT INTO `csm_role_privilege` VALUES (20,8,5);
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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user`
--

LOCK TABLES `csm_user` WRITE;
/*!40000 ALTER TABLE `csm_user` DISABLE KEYS */;
INSERT INTO `csm_user` VALUES (1,'administrator',0,'Administrator','NoName',NULL,NULL,NULL,NULL,'zJPWCwDeSgG8j2uyHEABIQ==',NULL,NULL,NULL,'2009-07-22',NULL);
INSERT INTO `csm_user` VALUES (2,'bbadmin',0,'Biobank Administrator','NoName',NULL,NULL,NULL,NULL,'7Bg9siN5e7M=',NULL,NULL,NULL,'2009-07-22',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_group`
--

LOCK TABLES `csm_user_group` WRITE;
/*!40000 ALTER TABLE `csm_user_group` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=220 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_group_role_pg`
--

LOCK TABLES `csm_user_group_role_pg` WRITE;
/*!40000 ALTER TABLE `csm_user_group_role_pg` DISABLE KEYS */;
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

-- Dump completed on 2011-08-16 16:35:24
