
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
INSERT INTO `csm_group` VALUES (5,'Super Administrator','Super administrator of the application','2011-03-11',2);
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
) ENGINE=InnoDB AUTO_INCREMENT=1422 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_pg_pe`
--

LOCK TABLES `csm_pg_pe` WRITE;
/*!40000 ALTER TABLE `csm_pg_pe` DISABLE KEYS */;
INSERT INTO `csm_pg_pe` VALUES (1289,1,186,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1290,1,18,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1291,1,25,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1292,1,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1293,1,180,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1294,1,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1295,1,21,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1296,1,178,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1297,1,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1298,1,6,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1299,1,188,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1300,1,170,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1301,1,51,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1302,1,8,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1303,1,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1304,1,151,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1305,1,32,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1306,1,65,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1307,1,27,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1308,1,183,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1309,1,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1310,1,20,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1311,1,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1312,1,179,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1313,1,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1314,1,187,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1315,1,5,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1316,1,192,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1318,1,30,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1320,1,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1321,1,193,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1322,1,177,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1323,1,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1324,1,181,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1325,1,175,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1326,1,184,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1327,1,171,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1328,1,35,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1329,1,4,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1330,1,176,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1331,1,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1332,1,185,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1333,1,12,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1334,1,182,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1335,46,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1336,46,193,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1353,65,175,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1354,65,179,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1355,65,184,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1356,65,180,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1357,65,176,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1358,65,178,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1359,65,183,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1360,65,177,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1361,65,182,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1362,65,181,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1372,66,186,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1373,66,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1374,67,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1375,48,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1376,48,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1377,48,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1378,48,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1380,48,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1381,50,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1382,50,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1383,50,171,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1384,50,151,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1385,50,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1387,50,192,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1388,50,65,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1389,50,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1395,49,187,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1396,68,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1397,69,32,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1398,70,12,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1409,47,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1410,47,8,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1411,47,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1412,47,185,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1413,47,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1414,45,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1415,45,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1417,45,20,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1418,1,195,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1419,74,195,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1420,1,196,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (1421,74,196,'0000-00-00');
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
) ENGINE=InnoDB AUTO_INCREMENT=197 DEFAULT CHARSET=latin1;
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
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_protection_group`
--

LOCK TABLES `csm_protection_group` WRITE;
/*!40000 ALTER TABLE `csm_protection_group` DISABLE KEYS */;
INSERT INTO `csm_protection_group` VALUES (1,'Internal: All Objects','Contains Protection Element of each model object',2,0,'2011-03-11',NULL);
INSERT INTO `csm_protection_group` VALUES (45,'Internal: Center Administrator','** DO NOT RENAME **\r\nAct like a flag to tell if will be administrator of the working centers - Also contains all center specific features',2,0,'2011-03-14',NULL);
INSERT INTO `csm_protection_group` VALUES (46,'Center Feature: Clinic Shipments','Represents the ability to create/delete/update shipments (needed when clinics doesn\'t use the software)',2,0,'2011-03-11',45);
INSERT INTO `csm_protection_group` VALUES (47,'Center Feature: Collection Event','Represents the ability to create/update/delete patients and collection events',2,0,'2011-03-11',45);
INSERT INTO `csm_protection_group` VALUES (48,'Center Feature: Assign positions','Represents the ability to assign a position to a specimen',2,0,'2011-03-11',45);
INSERT INTO `csm_protection_group` VALUES (49,'Global Feature: Specimen Type','Represents the ability to create/edit/delete specimen types',2,0,'2011-03-11',73);
INSERT INTO `csm_protection_group` VALUES (50,'Center Feature: Dispatch/Request','Represent the dispatch and request features + contains protection elements needed to manage dispatches',2,0,'2011-03-11',45);
INSERT INTO `csm_protection_group` VALUES (65,'Center Feature: Reports','Represents the reports feature',2,0,'2011-03-11',45);
INSERT INTO `csm_protection_group` VALUES (66,'Center Feature: Processing Event','Represents the ability to create/delete/update processing events',2,0,'2011-03-11',45);
INSERT INTO `csm_protection_group` VALUES (67,'Center Feature: Link specimens','Represents the ability to link specimens to their source specimens',2,0,'2011-03-11',45);
INSERT INTO `csm_protection_group` VALUES (68,'Global Feature: Activity Status','Represents the ability to create/edit/delete activity statuses',2,0,'2011-03-11',73);
INSERT INTO `csm_protection_group` VALUES (69,'Global Feature: Shipping Method','Represents the ability to create/edit/delete shipping methodes',2,0,'2011-03-11',73);
INSERT INTO `csm_protection_group` VALUES (70,'Global Feature: Collection Event Attributes Types','Represents the ability to create/edit/delete collection Event Attributes Types',2,0,'2011-03-11',73);
INSERT INTO `csm_protection_group` VALUES (73,'Internal: All Global Features','contains all non center specific features',2,0,'2011-03-14',NULL);
INSERT INTO `csm_protection_group` VALUES (74,'Center Feature: Printer Labels','Used to print labels',2,0,'2011-06-06',45);
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
INSERT INTO `csm_role` VALUES (9,'Center Full Access','has read and update privilege on center object',2,1,'2011-03-11');
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
INSERT INTO `csm_role_privilege` VALUES (16,7,3);
INSERT INTO `csm_role_privilege` VALUES (18,8,3);
INSERT INTO `csm_role_privilege` VALUES (23,9,3);
INSERT INTO `csm_role_privilege` VALUES (20,8,5);
INSERT INTO `csm_role_privilege` VALUES (24,9,5);
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
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
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=latin1;
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
INSERT INTO `csm_user_group` VALUES (50,24,5);
INSERT INTO `csm_user_group` VALUES (51,24,6);
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
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_group_role_pg`
--

LOCK TABLES `csm_user_group_role_pg` WRITE;
/*!40000 ALTER TABLE `csm_user_group_role_pg` DISABLE KEYS */;
INSERT INTO `csm_user_group_role_pg` VALUES (163,NULL,9,8,45,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (165,NULL,8,7,1,'2010-10-20');
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
INSERT INTO `csm_user_group_role_pg` VALUES (184,NULL,9,7,1,'2010-10-20');
INSERT INTO `csm_user_group_role_pg` VALUES (193,NULL,7,8,67,'2011-03-11');
INSERT INTO `csm_user_group_role_pg` VALUES (194,NULL,7,8,66,'2011-03-11');
INSERT INTO `csm_user_group_role_pg` VALUES (195,NULL,7,8,65,'2011-03-11');
INSERT INTO `csm_user_group_role_pg` VALUES (196,NULL,8,8,67,'2011-03-11');
INSERT INTO `csm_user_group_role_pg` VALUES (197,NULL,8,8,66,'2011-03-11');
INSERT INTO `csm_user_group_role_pg` VALUES (198,NULL,5,8,45,'2011-03-11');
INSERT INTO `csm_user_group_role_pg` VALUES (199,NULL,5,8,73,'2011-03-14');
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

-- Dump completed on 2011-06-06 12:02:19
