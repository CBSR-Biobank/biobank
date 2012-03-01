-- MySQL dump 10.11
--
-- Host: localhost    Database: biobank
-- ------------------------------------------------------
-- Server version	5.0.51a-24+lenny5

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
-- Table structure for table `abstract_position`
--

DROP TABLE IF EXISTS `abstract_position`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `abstract_position` (
  `ID` int(11) NOT NULL,
  `DISCRIMINATOR` varchar(255) collate latin1_general_cs NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ROW` int(11) NOT NULL,
  `COL` int(11) NOT NULL,
  `PARENT_CONTAINER_ID` int(11) default NULL,
  `POSITION_STRING` varchar(50) collate latin1_general_cs default NULL,
  `SPECIMEN_ID` int(11) default NULL,
  `CONTAINER_ID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `SPECIMEN_ID` (`SPECIMEN_ID`),
  KEY `FKBC4AE0A67366CE44` (`PARENT_CONTAINER_ID`),
  KEY `FKBC4AE0A6EF199765` (`SPECIMEN_ID`),
  KEY `FKBC4AE0A69BFD88CF` (`CONTAINER_ID`),
  CONSTRAINT `FKBC4AE0A67366CE44` FOREIGN KEY (`PARENT_CONTAINER_ID`) REFERENCES `container` (`ID`),
  CONSTRAINT `FKBC4AE0A69BFD88CF` FOREIGN KEY (`CONTAINER_ID`) REFERENCES `container` (`ID`),
  CONSTRAINT `FKBC4AE0A6EF199765` FOREIGN KEY (`SPECIMEN_ID`) REFERENCES `specimen` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `abstract_position`
--

LOCK TABLES `abstract_position` WRITE;
/*!40000 ALTER TABLE `abstract_position` DISABLE KEYS */;
/*!40000 ALTER TABLE `abstract_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_status`
--

DROP TABLE IF EXISTS `activity_status`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `activity_status` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(50) collate latin1_general_cs NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `activity_status`
--

LOCK TABLES `activity_status` WRITE;
/*!40000 ALTER TABLE `activity_status` DISABLE KEYS */;
INSERT INTO `activity_status` VALUES (1,0,'Active'),(2,0,'Closed'),(4,0,'Flagged');
/*!40000 ALTER TABLE `activity_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `address` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `STREET1` varchar(255) collate latin1_general_cs default NULL,
  `STREET2` varchar(255) collate latin1_general_cs default NULL,
  `CITY` varchar(50) collate latin1_general_cs default NULL,
  `PROVINCE` varchar(50) collate latin1_general_cs default NULL,
  `POSTAL_CODE` varchar(50) collate latin1_general_cs default NULL,
  `EMAIL_ADDRESS` varchar(100) collate latin1_general_cs default NULL,
  `PHONE_NUMBER` varchar(50) collate latin1_general_cs default NULL,
  `FAX_NUMBER` varchar(50) collate latin1_general_cs default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,0,NULL,NULL,'Toronto',NULL,NULL,NULL,NULL,NULL),(2,0,NULL,NULL,'Toronto',NULL,NULL,NULL,NULL,NULL),(5,0,'100 TO St.',NULL,'Toronto','Ontario','m1m1m1',NULL,NULL,NULL),(6,1,'500 Rep Rd.',NULL,'Toronto',NULL,'n2n2n2',NULL,NULL,NULL),(7,0,NULL,NULL,'argmonton',NULL,NULL,NULL,NULL,NULL),(9,0,NULL,NULL,'argmonton',NULL,NULL,NULL,NULL,NULL),(10,0,NULL,NULL,'argmonton',NULL,NULL,NULL,NULL,NULL),(11,0,NULL,NULL,'towmonton',NULL,NULL,NULL,NULL,NULL),(12,0,NULL,NULL,'towmonton',NULL,NULL,NULL,NULL,NULL),(13,0,NULL,NULL,'towmonton',NULL,NULL,NULL,NULL,NULL),(14,0,NULL,NULL,'towmonton',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aliquoted_specimen`
--

DROP TABLE IF EXISTS `aliquoted_specimen`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `aliquoted_specimen` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `QUANTITY` int(11) default NULL,
  `VOLUME` double default NULL,
  `STUDY_ID` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `SPECIMEN_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK75EACAC1F2A2464F` (`STUDY_ID`),
  KEY `FK75EACAC1C449A4` (`ACTIVITY_STATUS_ID`),
  KEY `FK75EACAC138445996` (`SPECIMEN_TYPE_ID`),
  CONSTRAINT `FK75EACAC138445996` FOREIGN KEY (`SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`),
  CONSTRAINT `FK75EACAC1C449A4` FOREIGN KEY (`ACTIVITY_STATUS_ID`) REFERENCES `activity_status` (`ID`),
  CONSTRAINT `FK75EACAC1F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `aliquoted_specimen`
--

LOCK TABLES `aliquoted_specimen` WRITE;
/*!40000 ALTER TABLE `aliquoted_specimen` DISABLE KEYS */;
INSERT INTO `aliquoted_specimen` VALUES (1,1,2,0.5,2,1,51),(2,1,2,1,2,1,23),(3,1,2,0.4,2,1,7),(4,1,1,1,2,1,2),(5,0,1,5,2,1,53),(6,0,1,5,2,1,45),(7,0,2,2,2,1,15),(8,0,2,2,2,1,4);
/*!40000 ALTER TABLE `aliquoted_specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `capacity`
--

DROP TABLE IF EXISTS `capacity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `capacity` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ROW_CAPACITY` int(11) NOT NULL,
  `COL_CAPACITY` int(11) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `capacity`
--

LOCK TABLES `capacity` WRITE;
/*!40000 ALTER TABLE `capacity` DISABLE KEYS */;
INSERT INTO `capacity` VALUES (1,2,1,2),(2,0,2,2);
/*!40000 ALTER TABLE `capacity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `center`
--

DROP TABLE IF EXISTS `center`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `center` (
  `ID` int(11) NOT NULL,
  `DISCRIMINATOR` varchar(255) collate latin1_general_cs NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) collate latin1_general_cs NOT NULL,
  `NAME_SHORT` varchar(50) collate latin1_general_cs NOT NULL,
  `COMMENT` text collate latin1_general_cs,
  `ADDRESS_ID` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `STUDY_ID` int(11) default NULL,
  `SENDS_SHIPMENTS` bit(1) default NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `NAME_SHORT` (`NAME_SHORT`),
  UNIQUE KEY `ADDRESS_ID` (`ADDRESS_ID`),
  UNIQUE KEY `STUDY_ID` (`STUDY_ID`),
  KEY `FK7645C055F2A2464F` (`STUDY_ID`),
  KEY `FK7645C055C449A4` (`ACTIVITY_STATUS_ID`),
  KEY `FK7645C0556AF2992F` (`ADDRESS_ID`),
  CONSTRAINT `FK7645C0556AF2992F` FOREIGN KEY (`ADDRESS_ID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK7645C055C449A4` FOREIGN KEY (`ACTIVITY_STATUS_ID`) REFERENCES `activity_status` (`ID`),
  CONSTRAINT `FK7645C055F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `center`
--

LOCK TABLES `center` WRITE;
/*!40000 ALTER TABLE `center` DISABLE KEYS */;
INSERT INTO `center` VALUES (1,'Clinic',0,'IK Clinic','IK Clinic',NULL,1,1,NULL,'\0'),(2,'Site',0,'IK Site','IK Site',NULL,2,1,NULL,NULL),(5,'Clinic',0,'Clinic1','Clinic1','Test Clinic',5,1,NULL,''),(6,'Site',1,'Repository1','Repository1',NULL,6,1,NULL,NULL),(7,'Site',0,'example_19pmd1ko20nuup862c7g71ud3f','example_19pmd1ko20nuup862c7g71ud3f_short',NULL,7,1,NULL,NULL),(9,'Site',0,'example_mps930jnsnj057jkshvprupc0j','example_mps930jnsnj057jkshvprupc0j_short',NULL,9,1,NULL,NULL),(10,'Site',0,'example_p5sje0bt1mgr7i0j8vprmkfkhv','example_p5sje0bt1mgr7i0j8vprmkfkhv_short',NULL,10,1,NULL,NULL),(11,'Site',0,'example_smakbll1mlgklpmjrdppp6namm','example_smakbll1mlgklpmjrdppp6namm_short',NULL,11,1,NULL,NULL),(12,'Site',0,'example_uiqp2a719sijarr3f98p8ojk8r','example_uiqp2a719sijarr3f98p8ojk8r_short',NULL,12,1,NULL,NULL),(13,'Site',0,'example_3t4kebmlpm5om5ovtfahali0io','example_3t4kebmlpm5om5ovtfahali0io_short',NULL,13,1,NULL,NULL),(14,'Site',0,'example_gmj7tm1n62v8c2sf9cl7u3g1dr','example_gmj7tm1n62v8c2sf9cl7u3g1dr_short',NULL,14,1,NULL,NULL);
/*!40000 ALTER TABLE `center` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collection_event`
--

DROP TABLE IF EXISTS `collection_event`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `collection_event` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `VISIT_NUMBER` int(11) NOT NULL,
  `COMMENT` text collate latin1_general_cs,
  `PATIENT_ID` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `uc_visit_number` (`VISIT_NUMBER`,`PATIENT_ID`),
  KEY `FKEDAD8999C449A4` (`ACTIVITY_STATUS_ID`),
  KEY `FKEDAD8999B563F38F` (`PATIENT_ID`),
  CONSTRAINT `FKEDAD8999B563F38F` FOREIGN KEY (`PATIENT_ID`) REFERENCES `patient` (`ID`),
  CONSTRAINT `FKEDAD8999C449A4` FOREIGN KEY (`ACTIVITY_STATUS_ID`) REFERENCES `activity_status` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `collection_event`
--

LOCK TABLES `collection_event` WRITE;
/*!40000 ALTER TABLE `collection_event` DISABLE KEYS */;
INSERT INTO `collection_event` VALUES (3,1,1,NULL,4,1),(4,3,1,NULL,5,1),(5,0,1,NULL,6,1),(13,0,1,NULL,22,1),(18,1,1,NULL,27,1),(22,0,1,NULL,32,1),(23,0,1,NULL,33,1),(24,0,1,NULL,34,1),(27,4,1,'wwww',37,1),(28,1,1,NULL,38,1);
/*!40000 ALTER TABLE `collection_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `contact` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(100) collate latin1_general_cs default NULL,
  `TITLE` varchar(100) collate latin1_general_cs default NULL,
  `MOBILE_NUMBER` varchar(50) collate latin1_general_cs default NULL,
  `FAX_NUMBER` varchar(50) collate latin1_general_cs default NULL,
  `EMAIL_ADDRESS` varchar(50) collate latin1_general_cs default NULL,
  `PAGER_NUMBER` varchar(50) collate latin1_general_cs default NULL,
  `OFFICE_NUMBER` varchar(50) collate latin1_general_cs default NULL,
  `CLINIC_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK6382B00057F87A25` (`CLINIC_ID`),
  CONSTRAINT `FK6382B00057F87A25` FOREIGN KEY (`CLINIC_ID`) REFERENCES `center` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `contact`
--

LOCK TABLES `contact` WRITE;
/*!40000 ALTER TABLE `contact` DISABLE KEYS */;
INSERT INTO `contact` VALUES (1,0,'Will Smith','Doctor',NULL,NULL,NULL,NULL,NULL,1),(2,0,'Con1','Top Dude',NULL,NULL,NULL,NULL,NULL,5);
/*!40000 ALTER TABLE `contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container`
--

DROP TABLE IF EXISTS `container`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `container` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `PRODUCT_BARCODE` varchar(255) collate latin1_general_cs default NULL,
  `COMMENT` text collate latin1_general_cs,
  `LABEL` varchar(255) collate latin1_general_cs NOT NULL,
  `TEMPERATURE` double default NULL,
  `PATH` varchar(255) collate latin1_general_cs default NULL,
  `POSITION_ID` int(11) default NULL,
  `SITE_ID` int(11) NOT NULL,
  `TOP_CONTAINER_ID` int(11) default NULL,
  `CONTAINER_TYPE_ID` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `uc_label` (`LABEL`,`CONTAINER_TYPE_ID`),
  UNIQUE KEY `POSITION_ID` (`POSITION_ID`),
  UNIQUE KEY `uc_productbarcode` (`PRODUCT_BARCODE`,`SITE_ID`),
  KEY `FK8D995C61C449A4` (`ACTIVITY_STATUS_ID`),
  KEY `FK8D995C611BE0C379` (`TOP_CONTAINER_ID`),
  KEY `FK8D995C61AC528270` (`POSITION_ID`),
  KEY `FK8D995C61B3E77A12` (`CONTAINER_TYPE_ID`),
  KEY `FK8D995C613F52C885` (`SITE_ID`),
  KEY `PATH_IDX` (`PATH`),
  CONSTRAINT `FK8D995C611BE0C379` FOREIGN KEY (`TOP_CONTAINER_ID`) REFERENCES `container` (`ID`),
  CONSTRAINT `FK8D995C613F52C885` FOREIGN KEY (`SITE_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK8D995C61AC528270` FOREIGN KEY (`POSITION_ID`) REFERENCES `abstract_position` (`ID`),
  CONSTRAINT `FK8D995C61B3E77A12` FOREIGN KEY (`CONTAINER_TYPE_ID`) REFERENCES `container_type` (`ID`),
  CONSTRAINT `FK8D995C61C449A4` FOREIGN KEY (`ACTIVITY_STATUS_ID`) REFERENCES `activity_status` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `container`
--

LOCK TABLES `container` WRITE;
/*!40000 ALTER TABLE `container` DISABLE KEYS */;
INSERT INTO `container` VALUES (1,0,NULL,NULL,'IK Container Name',NULL,'',NULL,2,1,1,1);
/*!40000 ALTER TABLE `container` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_labeling_scheme`
--

DROP TABLE IF EXISTS `container_labeling_scheme`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `container_labeling_scheme` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(50) collate latin1_general_cs default NULL,
  `MIN_CHARS` int(11) default NULL,
  `MAX_CHARS` int(11) default NULL,
  `MAX_ROWS` int(11) default NULL,
  `MAX_COLS` int(11) default NULL,
  `MAX_CAPACITY` int(11) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `container_labeling_scheme`
--

LOCK TABLES `container_labeling_scheme` WRITE;
/*!40000 ALTER TABLE `container_labeling_scheme` DISABLE KEYS */;
INSERT INTO `container_labeling_scheme` VALUES (1,0,'SBS Standard',2,3,16,24,384),(2,0,'CBSR 2 char alphabetic',2,2,NULL,NULL,576),(3,0,'2 char numeric',2,2,NULL,NULL,99),(4,0,'Dewar',2,2,2,2,4),(5,0,'CBSR SBS',2,2,9,9,81),(6,0,'2 char alphabetic',2,2,NULL,NULL,676);
/*!40000 ALTER TABLE `container_labeling_scheme` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_type`
--

DROP TABLE IF EXISTS `container_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `container_type` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) collate latin1_general_cs NOT NULL,
  `NAME_SHORT` varchar(50) collate latin1_general_cs NOT NULL,
  `TOP_LEVEL` bit(1) default NULL,
  `DEFAULT_TEMPERATURE` double default NULL,
  `COMMENT` text collate latin1_general_cs,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `CAPACITY_ID` int(11) NOT NULL,
  `SITE_ID` int(11) NOT NULL,
  `CHILD_LABELING_SCHEME_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `CAPACITY_ID` (`CAPACITY_ID`),
  UNIQUE KEY `uc_name` (`NAME`,`SITE_ID`),
  UNIQUE KEY `uc_nameshort` (`NAME_SHORT`,`SITE_ID`),
  KEY `FKB2C87858C449A4` (`ACTIVITY_STATUS_ID`),
  KEY `FKB2C878581764E225` (`CAPACITY_ID`),
  KEY `FKB2C878585D63DFF0` (`CHILD_LABELING_SCHEME_ID`),
  KEY `FKB2C878583F52C885` (`SITE_ID`),
  CONSTRAINT `FKB2C878581764E225` FOREIGN KEY (`CAPACITY_ID`) REFERENCES `capacity` (`ID`),
  CONSTRAINT `FKB2C878583F52C885` FOREIGN KEY (`SITE_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKB2C878585D63DFF0` FOREIGN KEY (`CHILD_LABELING_SCHEME_ID`) REFERENCES `container_labeling_scheme` (`ID`),
  CONSTRAINT `FKB2C87858C449A4` FOREIGN KEY (`ACTIVITY_STATUS_ID`) REFERENCES `activity_status` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `container_type`
--

LOCK TABLES `container_type` WRITE;
/*!40000 ALTER TABLE `container_type` DISABLE KEYS */;
INSERT INTO `container_type` VALUES (1,2,'IK Container Type','IK Container Type','',NULL,NULL,1,1,2,6),(2,0,'Container1','Container1','',-29,NULL,1,2,6,4);
/*!40000 ALTER TABLE `container_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_type_container_type`
--

DROP TABLE IF EXISTS `container_type_container_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `container_type_container_type` (
  `PARENT_CONTAINER_TYPE_ID` int(11) NOT NULL,
  `CHILD_CONTAINER_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY  (`PARENT_CONTAINER_TYPE_ID`,`CHILD_CONTAINER_TYPE_ID`),
  KEY `FK5991B31F9C2855BD` (`PARENT_CONTAINER_TYPE_ID`),
  KEY `FK5991B31F371DC9AF` (`CHILD_CONTAINER_TYPE_ID`),
  CONSTRAINT `FK5991B31F371DC9AF` FOREIGN KEY (`CHILD_CONTAINER_TYPE_ID`) REFERENCES `container_type` (`ID`),
  CONSTRAINT `FK5991B31F9C2855BD` FOREIGN KEY (`PARENT_CONTAINER_TYPE_ID`) REFERENCES `container_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `container_type_container_type`
--

LOCK TABLES `container_type_container_type` WRITE;
/*!40000 ALTER TABLE `container_type_container_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `container_type_container_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_type_specimen_type`
--

DROP TABLE IF EXISTS `container_type_specimen_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `container_type_specimen_type` (
  `CONTAINER_TYPE_ID` int(11) NOT NULL,
  `SPECIMEN_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY  (`CONTAINER_TYPE_ID`,`SPECIMEN_TYPE_ID`),
  KEY `FKE2F4C26AB3E77A12` (`CONTAINER_TYPE_ID`),
  KEY `FKE2F4C26A38445996` (`SPECIMEN_TYPE_ID`),
  CONSTRAINT `FKE2F4C26A38445996` FOREIGN KEY (`SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`),
  CONSTRAINT `FKE2F4C26AB3E77A12` FOREIGN KEY (`CONTAINER_TYPE_ID`) REFERENCES `container_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `container_type_specimen_type`
--

LOCK TABLES `container_type_specimen_type` WRITE;
/*!40000 ALTER TABLE `container_type_specimen_type` DISABLE KEYS */;
INSERT INTO `container_type_specimen_type` VALUES (1,7),(1,15),(1,62),(2,86),(2,94),(2,116);
/*!40000 ALTER TABLE `container_type_specimen_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_application`
--

DROP TABLE IF EXISTS `csm_application`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_application` (
  `APPLICATION_ID` bigint(20) NOT NULL auto_increment,
  `APPLICATION_NAME` varchar(255) NOT NULL,
  `APPLICATION_DESCRIPTION` varchar(200) NOT NULL,
  `DECLARATIVE_FLAG` tinyint(1) NOT NULL default '0',
  `ACTIVE_FLAG` tinyint(1) NOT NULL default '0',
  `UPDATE_DATE` date default '0000-00-00',
  `DATABASE_URL` varchar(100) default NULL,
  `DATABASE_USER_NAME` varchar(100) default NULL,
  `DATABASE_PASSWORD` varchar(100) default NULL,
  `DATABASE_DIALECT` varchar(100) default NULL,
  `DATABASE_DRIVER` varchar(100) default NULL,
  PRIMARY KEY  (`APPLICATION_ID`),
  UNIQUE KEY `UQ_APPLICATION_NAME` (`APPLICATION_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_application`
--

LOCK TABLES `csm_application` WRITE;
/*!40000 ALTER TABLE `csm_application` DISABLE KEYS */;
INSERT INTO `csm_application` VALUES (1,'csmupt','CSM UPT Super Admin Application',0,0,'2009-07-22',NULL,NULL,NULL,NULL,NULL),(2,'biobank','biobank',0,0,'2009-07-22','jdbc:mysql://localhost:3306/biobank','dummy','4UlzrQJztJY=','org.hibernate.dialect.MySQLDialect','com.mysql.jdbc.Driver');
/*!40000 ALTER TABLE `csm_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_filter_clause`
--

DROP TABLE IF EXISTS `csm_filter_clause`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_filter_clause` (
  `FILTER_CLAUSE_ID` bigint(20) NOT NULL auto_increment,
  `CLASS_NAME` varchar(100) NOT NULL,
  `FILTER_CHAIN` varchar(2000) NOT NULL,
  `TARGET_CLASS_NAME` varchar(100) NOT NULL,
  `TARGET_CLASS_ATTRIBUTE_NAME` varchar(100) NOT NULL,
  `TARGET_CLASS_ATTRIBUTE_TYPE` varchar(100) NOT NULL,
  `TARGET_CLASS_ALIAS` varchar(100) default NULL,
  `TARGET_CLASS_ATTRIBUTE_ALIAS` varchar(100) default NULL,
  `GENERATED_SQL_USER` varchar(4000) NOT NULL,
  `GENERATED_SQL_GROUP` varchar(4000) NOT NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date NOT NULL default '0000-00-00',
  PRIMARY KEY  (`FILTER_CLAUSE_ID`),
  KEY `FK_APPLICATION_FILTER_CLAUSE` (`APPLICATION_ID`),
  CONSTRAINT `FK_APPLICATION_FILTER_CLAUSE` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

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
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_group` (
  `GROUP_ID` bigint(20) NOT NULL auto_increment,
  `GROUP_NAME` varchar(255) NOT NULL,
  `GROUP_DESC` varchar(200) default NULL,
  `UPDATE_DATE` date NOT NULL default '0000-00-00',
  `APPLICATION_ID` bigint(20) NOT NULL,
  PRIMARY KEY  (`GROUP_ID`),
  UNIQUE KEY `UQ_GROUP_GROUP_NAME` (`APPLICATION_ID`,`GROUP_NAME`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  CONSTRAINT `FK_APPLICATION_GROUP` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_group`
--

LOCK TABLES `csm_group` WRITE;
/*!40000 ALTER TABLE `csm_group` DISABLE KEYS */;
INSERT INTO `csm_group` VALUES (5,'Super Administrator','Super administrator of the application','2011-03-11',2),(6,'CBSR Technician Level 1','','2010-01-28',2),(7,'CBSR Technician Level 2','','2010-01-28',2),(8,'Calgary Technicians','','2010-04-20',2),(9,'Calgary Administrator','','2010-04-20',2);
/*!40000 ALTER TABLE `csm_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_pg_pe`
--

DROP TABLE IF EXISTS `csm_pg_pe`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_pg_pe` (
  `PG_PE_ID` bigint(20) NOT NULL auto_increment,
  `PROTECTION_GROUP_ID` bigint(20) NOT NULL,
  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date default '0000-00-00',
  PRIMARY KEY  (`PG_PE_ID`),
  UNIQUE KEY `UQ_PROTECTION_GROUP_PROTECTION_ELEMENT_PROTECTION_GROUP_ID` (`PROTECTION_ELEMENT_ID`,`PROTECTION_GROUP_ID`),
  KEY `idx_PROTECTION_ELEMENT_ID` (`PROTECTION_ELEMENT_ID`),
  KEY `idx_PROTECTION_GROUP_ID` (`PROTECTION_GROUP_ID`),
  CONSTRAINT `FK_PROTECTION_ELEMENT_PROTECTION_GROUP` FOREIGN KEY (`PROTECTION_ELEMENT_ID`) REFERENCES `csm_protection_element` (`PROTECTION_ELEMENT_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PROTECTION_GROUP_PROTECTION_ELEMENT` FOREIGN KEY (`PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1441 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_pg_pe`
--

LOCK TABLES `csm_pg_pe` WRITE;
/*!40000 ALTER TABLE `csm_pg_pe` DISABLE KEYS */;
INSERT INTO `csm_pg_pe` VALUES (1289,1,186,'0000-00-00'),(1290,1,18,'0000-00-00'),(1291,1,25,'0000-00-00'),(1292,1,7,'0000-00-00'),(1293,1,180,'0000-00-00'),(1294,1,10,'0000-00-00'),(1295,1,21,'0000-00-00'),(1296,1,178,'0000-00-00'),(1297,1,36,'0000-00-00'),(1298,1,6,'0000-00-00'),(1299,1,188,'0000-00-00'),(1300,1,170,'0000-00-00'),(1301,1,51,'0000-00-00'),(1302,1,8,'0000-00-00'),(1303,1,24,'0000-00-00'),(1304,1,151,'0000-00-00'),(1305,1,32,'0000-00-00'),(1306,1,65,'0000-00-00'),(1307,1,27,'0000-00-00'),(1308,1,183,'0000-00-00'),(1309,1,13,'0000-00-00'),(1310,1,20,'0000-00-00'),(1311,1,19,'0000-00-00'),(1312,1,179,'0000-00-00'),(1313,1,16,'0000-00-00'),(1314,1,187,'0000-00-00'),(1315,1,5,'0000-00-00'),(1316,1,192,'0000-00-00'),(1318,1,30,'0000-00-00'),(1320,1,11,'0000-00-00'),(1321,1,193,'0000-00-00'),(1322,1,177,'0000-00-00'),(1323,1,15,'0000-00-00'),(1324,1,181,'0000-00-00'),(1325,1,175,'0000-00-00'),(1326,1,184,'0000-00-00'),(1327,1,171,'0000-00-00'),(1328,1,35,'0000-00-00'),(1329,1,4,'0000-00-00'),(1330,1,176,'0000-00-00'),(1331,1,3,'0000-00-00'),(1332,1,185,'0000-00-00'),(1333,1,12,'0000-00-00'),(1334,1,182,'0000-00-00'),(1335,46,16,'0000-00-00'),(1336,46,193,'0000-00-00'),(1353,65,175,'0000-00-00'),(1354,65,179,'0000-00-00'),(1355,65,184,'0000-00-00'),(1356,65,180,'0000-00-00'),(1357,65,176,'0000-00-00'),(1358,65,178,'0000-00-00'),(1359,65,183,'0000-00-00'),(1360,65,177,'0000-00-00'),(1361,65,182,'0000-00-00'),(1362,65,181,'0000-00-00'),(1372,66,186,'0000-00-00'),(1373,66,24,'0000-00-00'),(1374,67,24,'0000-00-00'),(1375,48,19,'0000-00-00'),(1376,48,7,'0000-00-00'),(1377,48,24,'0000-00-00'),(1378,48,36,'0000-00-00'),(1380,48,15,'0000-00-00'),(1381,50,19,'0000-00-00'),(1382,50,7,'0000-00-00'),(1383,50,171,'0000-00-00'),(1384,50,151,'0000-00-00'),(1385,50,24,'0000-00-00'),(1387,50,192,'0000-00-00'),(1388,50,65,'0000-00-00'),(1389,50,36,'0000-00-00'),(1395,49,187,'0000-00-00'),(1396,68,3,'0000-00-00'),(1397,69,32,'0000-00-00'),(1398,70,12,'0000-00-00'),(1409,47,16,'0000-00-00'),(1410,47,8,'0000-00-00'),(1411,47,24,'0000-00-00'),(1412,47,185,'0000-00-00'),(1413,47,11,'0000-00-00'),(1414,45,19,'0000-00-00'),(1415,45,7,'0000-00-00'),(1417,45,20,'0000-00-00'),(1418,1,195,'0000-00-00'),(1419,74,195,'0000-00-00'),(1420,1,196,'0000-00-00'),(1421,74,196,'0000-00-00'),(1422,1,197,'0000-00-00'),(1423,74,197,'0000-00-00'),(1424,75,198,'0000-00-00'),(1425,76,199,'0000-00-00'),(1428,79,202,'0000-00-00'),(1429,80,203,'0000-00-00'),(1430,81,204,'0000-00-00'),(1431,82,205,'0000-00-00'),(1432,83,206,'0000-00-00'),(1433,84,207,'0000-00-00'),(1434,85,208,'0000-00-00'),(1435,86,209,'0000-00-00'),(1436,87,210,'0000-00-00'),(1437,1,211,'2012-02-02'),(1438,1,212,'2012-02-02'),(1439,1,213,'2012-02-02'),(1440,1,214,'2012-02-02');
/*!40000 ALTER TABLE `csm_pg_pe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_privilege`
--

DROP TABLE IF EXISTS `csm_privilege`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_privilege` (
  `PRIVILEGE_ID` bigint(20) NOT NULL auto_increment,
  `PRIVILEGE_NAME` varchar(100) NOT NULL,
  `PRIVILEGE_DESCRIPTION` varchar(200) default NULL,
  `UPDATE_DATE` date NOT NULL default '0000-00-00',
  PRIMARY KEY  (`PRIVILEGE_ID`),
  UNIQUE KEY `UQ_PRIVILEGE_NAME` (`PRIVILEGE_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_privilege`
--

LOCK TABLES `csm_privilege` WRITE;
/*!40000 ALTER TABLE `csm_privilege` DISABLE KEYS */;
INSERT INTO `csm_privilege` VALUES (1,'CREATE','This privilege grants permission to a user to create an entity. This entity can be an object, a database entry, or a resource such as a network connection','2009-07-22'),(2,'ACCESS','This privilege allows a user to access a particular resource.  Examples of resources include a network or database connection, socket, module of the application, or even the application itself','2009-07-22'),(3,'READ','This privilege permits the user to read data from a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to read data about a particular entry','2009-07-22'),(4,'WRITE','This privilege allows a user to write data to a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to write data about a particular entity','2009-07-22'),(5,'UPDATE','This privilege grants permission at an entity level and signifies that the user is allowed to update data for a particular entity. Entities may include an object, object attribute, database row etc','2009-07-22'),(6,'DELETE','This privilege permits a user to delete a logical entity. This entity can be an object, a database entry, a resource such as a network connection, etc','2009-07-22'),(7,'EXECUTE','This privilege allows a user to execute a particular resource. The resource can be a method, function, behavior of the application, URL, button etc','2009-07-22');
/*!40000 ALTER TABLE `csm_privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_protection_element`
--

DROP TABLE IF EXISTS `csm_protection_element`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_protection_element` (
  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL auto_increment,
  `PROTECTION_ELEMENT_NAME` varchar(100) NOT NULL,
  `PROTECTION_ELEMENT_DESCRIPTION` varchar(200) default NULL,
  `OBJECT_ID` varchar(100) NOT NULL,
  `ATTRIBUTE` varchar(100) default NULL,
  `ATTRIBUTE_VALUE` varchar(100) default NULL,
  `PROTECTION_ELEMENT_TYPE` varchar(100) default NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date NOT NULL default '0000-00-00',
  PRIMARY KEY  (`PROTECTION_ELEMENT_ID`),
  UNIQUE KEY `UQ_PE_PE_NAME_ATTRIBUTE_VALUE_APP_ID` (`OBJECT_ID`,`ATTRIBUTE`,`ATTRIBUTE_VALUE`,`APPLICATION_ID`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  CONSTRAINT `FK_PE_APPLICATION` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=215 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_protection_element`
--

LOCK TABLES `csm_protection_element` WRITE;
/*!40000 ALTER TABLE `csm_protection_element` DISABLE KEYS */;
INSERT INTO `csm_protection_element` VALUES (1,'csmupt','CSM UPT Super Admin Application Protection Element','csmupt',NULL,NULL,NULL,1,'2009-07-22'),(2,'biobank','biobank','biobank',NULL,NULL,NULL,1,'2009-07-22'),(3,'edu.ualberta.med.biobank.model.ActivityStatus','edu.ualberta.med.biobank.model.ActivityStatus','edu.ualberta.med.biobank.model.ActivityStatus','','','',2,'2010-03-04'),(4,'edu.ualberta.med.biobank.model.Address','edu.ualberta.med.biobank.model.Address','edu.ualberta.med.biobank.model.Address',NULL,NULL,NULL,2,'2009-07-22'),(5,'edu.ualberta.med.biobank.model.Capacity','edu.ualberta.med.biobank.model.Capacity','edu.ualberta.med.biobank.model.Capacity',NULL,NULL,NULL,2,'2009-07-22'),(6,'edu.ualberta.med.biobank.model.Clinic','edu.ualberta.med.biobank.model.Clinic','edu.ualberta.med.biobank.model.Clinic',NULL,NULL,NULL,2,'2009-07-22'),(7,'edu.ualberta.med.biobank.model.ContainerPosition','edu.ualberta.med.biobank.model.ContainerPosition','edu.ualberta.med.biobank.model.ContainerPosition',NULL,NULL,NULL,2,'2009-07-22'),(8,'edu.ualberta.med.biobank.model.Patient','edu.ualberta.med.biobank.model.Patient','edu.ualberta.med.biobank.model.Patient',NULL,NULL,NULL,2,'2009-07-22'),(10,'edu.ualberta.med.biobank.model.StudyEventAttr','edu.ualberta.med.biobank.model.StudyEventAttr','edu.ualberta.med.biobank.model.StudyEventAttr','','','',2,'2011-02-28'),(11,'edu.ualberta.med.biobank.model.EventAttr','edu.ualberta.med.biobank.model.EventAttr','edu.ualberta.med.biobank.model.EventAttr','','','',2,'2011-02-28'),(12,'edu.ualberta.med.biobank.model.GlobalEventAttr','edu.ualberta.med.biobank.model.GlobalEventAttr','edu.ualberta.med.biobank.model.GlobalEventAttr','','','',2,'2011-02-28'),(13,'edu.ualberta.med.biobank.model.EventAttrType','edu.ualberta.med.biobank.model.EventAttrType','edu.ualberta.med.biobank.model.EventAttrType','','','',2,'2011-02-28'),(15,'edu.ualberta.med.biobank.model.SpecimenPosition','edu.ualberta.med.biobank.model.SpecimenPosition','edu.ualberta.med.biobank.model.SpecimenPosition','','','',2,'2011-02-28'),(16,'edu.ualberta.med.biobank.model.OriginInfo','edu.ualberta.med.biobank.model.OriginInfo','edu.ualberta.med.biobank.model.OriginInfo','','','',2,'2011-02-28'),(18,'edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site',NULL,NULL,NULL,2,'2009-07-22'),(19,'edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','','','',2,'2010-08-19'),(20,'edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType',NULL,NULL,NULL,2,'2009-07-22'),(21,'edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study',NULL,NULL,NULL,2,'2009-07-22'),(24,'edu.ualberta.med.biobank.model.Specimen','edu.ualberta.med.biobank.model.Specimen','edu.ualberta.med.biobank.model.Specimen','','','',2,'2011-02-28'),(25,'edu.ualberta.med.biobank.model.AliquotedSpecimen','edu.ualberta.med.biobank.model.AliquotedSpecimen','edu.ualberta.med.biobank.model.AliquotedSpecimen','','','',2,'2011-02-28'),(27,'edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','','','',2,'2009-07-26'),(30,'edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','','','',2,'2009-08-24'),(32,'edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','','','',2,'2009-11-30'),(35,'edu.ualberta.med.biobank.model.SourceSpecimen','edu.ualberta.med.biobank.model.SourceSpecimen','edu.ualberta.med.biobank.model.SourceSpecimen','','','',2,'2011-02-28'),(36,'edu.ualberta.med.biobank.model.AbstractPosition','','edu.ualberta.med.biobank.model.AbstractPosition','','','',2,'2010-03-15'),(51,'edu.ualberta.med.biobank.model.Log','','edu.ualberta.med.biobank.model.Log','','','',2,'2010-05-25'),(65,'edu.ualberta.med.biobank.model.Dispatch','edu.ualberta.med.biobank.model.Dispatch','edu.ualberta.med.biobank.model.Dispatch','','','',2,'2010-08-18'),(151,'edu.ualberta.med.biobank.model.DispatchSpecimen','edu.ualberta.med.biobank.model.DispatchSpecimen','edu.ualberta.med.biobank.model.DispatchSpecimen','','','',2,'2011-02-28'),(170,'edu.ualberta.med.biobank.model.ResearchGroup','','edu.ualberta.med.biobank.model.ResearchGroup','','','',2,'2010-12-07'),(171,'edu.ualberta.med.biobank.model.Request','','edu.ualberta.med.biobank.model.Request','','','',2,'2010-12-08'),(175,'edu.ualberta.med.biobank.model.Report','','edu.ualberta.med.biobank.model.Report','','','',2,'2011-01-13'),(176,'edu.ualberta.med.biobank.model.ReportFilter','','edu.ualberta.med.biobank.model.ReportFilter','','','',2,'2011-01-13'),(177,'edu.ualberta.med.biobank.model.ReportFilterValue','','edu.ualberta.med.biobank.model.ReportFilterValue','','','',2,'2011-01-13'),(178,'edu.ualberta.med.biobank.model.ReportColumn','','edu.ualberta.med.biobank.model.ReportColumn','','','',2,'2011-01-13'),(179,'edu.ualberta.med.biobank.model.Entity','','edu.ualberta.med.biobank.model.Entity','','','',2,'2011-01-13'),(180,'edu.ualberta.med.biobank.model.EntityFilter','','edu.ualberta.med.biobank.model.EntityFilter','','','',2,'2011-01-13'),(181,'edu.ualberta.med.biobank.model.EntityColumn','','edu.ualberta.med.biobank.model.EntityColumn','','','',2,'2011-01-13'),(182,'edu.ualberta.med.biobank.model.EntityProperty','','edu.ualberta.med.biobank.model.EntityProperty','','','',2,'2011-01-13'),(183,'edu.ualberta.med.biobank.model.PropertyModifier','','edu.ualberta.med.biobank.model.PropertyModifier','','','',2,'2011-01-13'),(184,'edu.ualberta.med.biobank.model.PropertyType','','edu.ualberta.med.biobank.model.PropertyType','','','',2,'2011-01-13'),(185,'edu.ualberta.med.biobank.model.CollectionEvent','','edu.ualberta.med.biobank.model.CollectionEvent','','','',2,'2011-02-15'),(186,'edu.ualberta.med.biobank.model.ProcessingEvent','','edu.ualberta.med.biobank.model.ProcessingEvent','','','',2,'2011-02-15'),(187,'edu.ualberta.med.biobank.model.SpecimenType','edu.ualberta.med.biobank.model.SpecimenType','edu.ualberta.med.biobank.model.SpecimenType','','','',2,'2011-02-28'),(188,'edu.ualberta.med.biobank.model.Center','','edu.ualberta.med.biobank.model.Center','','','',2,'2011-02-15'),(192,'edu.ualberta.med.biobank.model.RequestSpecimen','','edu.ualberta.med.biobank.model.RequestSpecimen','','','',2,'2011-02-28'),(193,'edu.ualberta.med.biobank.model.ShipmentInfo','edu.ualberta.med.biobank.model.ShipmentInfo','edu.ualberta.med.biobank.model.ShipmentInfo','','','',2,'2011-02-28'),(195,'edu.ualberta.med.biobank.model.PrintedSsInvItem','','edu.ualberta.med.biobank.model.PrintedSsInvItem','','','',2,'2011-06-06'),(196,'edu.ualberta.med.biobank.model.PrinterLabelTemplate','','edu.ualberta.med.biobank.model.PrinterLabelTemplate','','','',2,'2011-06-06'),(197,'edu.ualberta.med.biobank.model.JasperTemplate','','edu.ualberta.med.biobank.model.JasperTemplate','','','',2,'2011-06-07'),(198,'Clinic/IK Clinic','IK Clinic','edu.ualberta.med.biobank.model.Clinic','id','1','',2,'2011-10-12'),(199,'Site/IK Site','IK Site','edu.ualberta.med.biobank.model.Site','id','2','',2,'2011-10-12'),(202,'Clinic/Clinic1','Clinic1','edu.ualberta.med.biobank.model.Clinic','id','5','',2,'2011-10-25'),(203,'Site/Repository1','Repository1','edu.ualberta.med.biobank.model.Site','id','6','',2,'2011-10-25'),(204,'Site/example_19pmd1ko20nuup862c7g71ud3f_short','example_19pmd1ko20nuup862c7g71ud3f_short','edu.ualberta.med.biobank.model.Site','id','7','',2,'2011-12-15'),(205,'Site/example_mps930jnsnj057jkshvprupc0j_short','example_mps930jnsnj057jkshvprupc0j_short','edu.ualberta.med.biobank.model.Site','id','9','',2,'2011-12-15'),(206,'Site/example_p5sje0bt1mgr7i0j8vprmkfkhv_short','example_p5sje0bt1mgr7i0j8vprmkfkhv_short','edu.ualberta.med.biobank.model.Site','id','10','',2,'2011-12-15'),(207,'Site/example_smakbll1mlgklpmjrdppp6namm_short','example_smakbll1mlgklpmjrdppp6namm_short','edu.ualberta.med.biobank.model.Site','id','11','',2,'2012-01-04'),(208,'Site/example_uiqp2a719sijarr3f98p8ojk8r_short','example_uiqp2a719sijarr3f98p8ojk8r_short','edu.ualberta.med.biobank.model.Site','id','12','',2,'2012-01-04'),(209,'Site/example_3t4kebmlpm5om5ovtfahali0io_short','example_3t4kebmlpm5om5ovtfahali0io_short','edu.ualberta.med.biobank.model.Site','id','13','',2,'2012-01-10'),(210,'Site/example_gmj7tm1n62v8c2sf9cl7u3g1dr_short','example_gmj7tm1n62v8c2sf9cl7u3g1dr_short','edu.ualberta.med.biobank.model.Site','id','14','',2,'2012-01-13'),(211,'edu.ualberta.med.biobank.model.StudySpecimenAttr',NULL,'edu.ualberta.med.biobank.model.StudySpecimenAttr',NULL,NULL,NULL,2,'2012-02-02'),(212,'edu.ualberta.med.biobank.model.SpecimenAttr',NULL,'edu.ualberta.med.biobank.model.SpecimenAttr',NULL,NULL,NULL,2,'2012-02-02'),(213,'edu.ualberta.med.biobank.model.SpecimenAttrType',NULL,'edu.ualberta.med.biobank.model.SpecimenAttrType',NULL,NULL,NULL,2,'2012-02-02'),(214,'edu.ualberta.med.biobank.model.GlobalSpecimenAttr',NULL,'edu.ualberta.med.biobank.model.GlobalSpecimenAttr',NULL,NULL,NULL,2,'2012-02-02');
/*!40000 ALTER TABLE `csm_protection_element` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_protection_group`
--

DROP TABLE IF EXISTS `csm_protection_group`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_protection_group` (
  `PROTECTION_GROUP_ID` bigint(20) NOT NULL auto_increment,
  `PROTECTION_GROUP_NAME` varchar(100) NOT NULL,
  `PROTECTION_GROUP_DESCRIPTION` varchar(200) default NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `LARGE_ELEMENT_COUNT_FLAG` tinyint(1) NOT NULL,
  `UPDATE_DATE` date NOT NULL default '0000-00-00',
  `PARENT_PROTECTION_GROUP_ID` bigint(20) default NULL,
  PRIMARY KEY  (`PROTECTION_GROUP_ID`),
  UNIQUE KEY `UQ_PROTECTION_GROUP_PROTECTION_GROUP_NAME` (`APPLICATION_ID`,`PROTECTION_GROUP_NAME`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  KEY `idx_PARENT_PROTECTION_GROUP_ID` (`PARENT_PROTECTION_GROUP_ID`),
  CONSTRAINT `FK_PG_APPLICATION` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PROTECTION_GROUP` FOREIGN KEY (`PARENT_PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_protection_group`
--

LOCK TABLES `csm_protection_group` WRITE;
/*!40000 ALTER TABLE `csm_protection_group` DISABLE KEYS */;
INSERT INTO `csm_protection_group` VALUES (1,'Internal: All Objects','Contains Protection Element of each model object',2,0,'2011-03-11',NULL),(45,'Internal: Center Administrator','** DO NOT RENAME **\r\nAct like a flag to tell if will be administrator of the working centers - Also contains all center specific features',2,0,'2011-03-14',NULL),(46,'Center Feature: Clinic Shipments','Represents the ability to create/delete/update shipments (needed when clinics doesn\'t use the software)',2,0,'2011-03-11',45),(47,'Center Feature: Collection Event','Represents the ability to create/update/delete patients and collection events',2,0,'2011-03-11',45),(48,'Center Feature: Assign positions','Represents the ability to assign a position to a specimen',2,0,'2011-03-11',45),(49,'Global Feature: Specimen Type','Represents the ability to create/edit/delete specimen types',2,0,'2011-03-11',73),(50,'Center Feature: Dispatch/Request','Represent the dispatch and request features + contains protection elements needed to manage dispatches',2,0,'2011-03-11',45),(65,'Center Feature: Reports','Represents the reports feature',2,0,'2011-03-11',45),(66,'Center Feature: Processing Event','Represents the ability to create/delete/update processing events',2,0,'2011-03-11',45),(67,'Center Feature: Link specimens','Represents the ability to link specimens to their source specimens',2,0,'2011-03-11',45),(68,'Global Feature: Activity Status','Represents the ability to create/edit/delete activity statuses',2,0,'2011-03-11',73),(69,'Global Feature: Shipping Method','Represents the ability to create/edit/delete shipping methodes',2,0,'2011-03-11',73),(70,'Global Feature: Collection Event Attributes Types','Represents the ability to create/edit/delete collection Event Attributes Types',2,0,'2011-03-11',73),(73,'Internal: All Global Features','contains all non center specific features',2,0,'2011-03-14',NULL),(74,'Center Feature: Printer Labels','Used to print labels',2,0,'2011-06-06',45),(75,'Clinic IK Clinic','Protection group for center IK Clinic (id=1)',2,0,'2011-10-12',NULL),(76,'Site IK Site','Protection group for center IK Site (id=2)',2,0,'2011-10-12',NULL),(79,'Clinic Clinic1','Protection group for center Clinic1 (id=5)',2,0,'2011-10-25',NULL),(80,'Site Repository1','Protection group for center Repository1 (id=6)',2,0,'2011-10-25',NULL),(81,'Site example_19pmd1ko20nuup862c7g71ud3f_short','Protection group for center example_19pmd1ko20nuup862c7g71ud3f_short (id=7)',2,0,'2011-12-15',NULL),(82,'Site example_mps930jnsnj057jkshvprupc0j_short','Protection group for center example_mps930jnsnj057jkshvprupc0j_short (id=9)',2,0,'2011-12-15',NULL),(83,'Site example_p5sje0bt1mgr7i0j8vprmkfkhv_short','Protection group for center example_p5sje0bt1mgr7i0j8vprmkfkhv_short (id=10)',2,0,'2011-12-15',NULL),(84,'Site example_smakbll1mlgklpmjrdppp6namm_short','Protection group for center example_smakbll1mlgklpmjrdppp6namm_short (id=11)',2,0,'2012-01-04',NULL),(85,'Site example_uiqp2a719sijarr3f98p8ojk8r_short','Protection group for center example_uiqp2a719sijarr3f98p8ojk8r_short (id=12)',2,0,'2012-01-04',NULL),(86,'Site example_3t4kebmlpm5om5ovtfahali0io_short','Protection group for center example_3t4kebmlpm5om5ovtfahali0io_short (id=13)',2,0,'2012-01-10',NULL),(87,'Site example_gmj7tm1n62v8c2sf9cl7u3g1dr_short','Protection group for center example_gmj7tm1n62v8c2sf9cl7u3g1dr_short (id=14)',2,0,'2012-01-13',NULL);
/*!40000 ALTER TABLE `csm_protection_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_role`
--

DROP TABLE IF EXISTS `csm_role`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_role` (
  `ROLE_ID` bigint(20) NOT NULL auto_increment,
  `ROLE_NAME` varchar(100) NOT NULL,
  `ROLE_DESCRIPTION` varchar(200) default NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `ACTIVE_FLAG` tinyint(1) NOT NULL,
  `UPDATE_DATE` date NOT NULL default '0000-00-00',
  PRIMARY KEY  (`ROLE_ID`),
  UNIQUE KEY `UQ_ROLE_ROLE_NAME` (`APPLICATION_ID`,`ROLE_NAME`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  CONSTRAINT `FK_APPLICATION_ROLE` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_role`
--

LOCK TABLES `csm_role` WRITE;
/*!40000 ALTER TABLE `csm_role` DISABLE KEYS */;
INSERT INTO `csm_role` VALUES (7,'Read Only','has read privilege on objects',2,1,'2010-10-20'),(8,'Object Full Access','has create/read/update/delete privileges on objects',2,1,'2010-10-20'),(9,'Center Full Access','has read and update privilege on center object',2,1,'2011-03-11');
/*!40000 ALTER TABLE `csm_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_role_privilege`
--

DROP TABLE IF EXISTS `csm_role_privilege`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_role_privilege` (
  `ROLE_PRIVILEGE_ID` bigint(20) NOT NULL auto_increment,
  `ROLE_ID` bigint(20) NOT NULL,
  `PRIVILEGE_ID` bigint(20) NOT NULL,
  PRIMARY KEY  (`ROLE_PRIVILEGE_ID`),
  UNIQUE KEY `UQ_ROLE_PRIVILEGE_ROLE_ID` (`PRIVILEGE_ID`,`ROLE_ID`),
  KEY `idx_PRIVILEGE_ID` (`PRIVILEGE_ID`),
  KEY `idx_ROLE_ID` (`ROLE_ID`),
  CONSTRAINT `FK_PRIVILEGE_ROLE` FOREIGN KEY (`PRIVILEGE_ID`) REFERENCES `csm_privilege` (`PRIVILEGE_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_ROLE` FOREIGN KEY (`ROLE_ID`) REFERENCES `csm_role` (`ROLE_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_role_privilege`
--

LOCK TABLES `csm_role_privilege` WRITE;
/*!40000 ALTER TABLE `csm_role_privilege` DISABLE KEYS */;
INSERT INTO `csm_role_privilege` VALUES (19,8,1),(16,7,3),(18,8,3),(23,9,3),(20,8,5),(24,9,5),(17,8,6);
/*!40000 ALTER TABLE `csm_role_privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user`
--

DROP TABLE IF EXISTS `csm_user`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_user` (
  `USER_ID` bigint(20) NOT NULL auto_increment,
  `LOGIN_NAME` varchar(500) NOT NULL,
  `MIGRATED_FLAG` tinyint(1) NOT NULL default '0',
  `FIRST_NAME` varchar(100) NOT NULL,
  `LAST_NAME` varchar(100) NOT NULL,
  `ORGANIZATION` varchar(100) default NULL,
  `DEPARTMENT` varchar(100) default NULL,
  `TITLE` varchar(100) default NULL,
  `PHONE_NUMBER` varchar(15) default NULL,
  `PASSWORD` varchar(100) default NULL,
  `EMAIL_ID` varchar(100) default NULL,
  `START_DATE` date default NULL,
  `END_DATE` date default NULL,
  `UPDATE_DATE` date NOT NULL default '0000-00-00',
  `PREMGRT_LOGIN_NAME` varchar(100) default NULL,
  PRIMARY KEY  (`USER_ID`),
  UNIQUE KEY `UQ_LOGIN_NAME` (`LOGIN_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_user`
--

LOCK TABLES `csm_user` WRITE;
/*!40000 ALTER TABLE `csm_user` DISABLE KEYS */;
INSERT INTO `csm_user` VALUES (1,'administrator',0,'Administrator','NoName',NULL,NULL,NULL,NULL,'zJPWCwDeSgG8j2uyHEABIQ==',NULL,NULL,NULL,'2009-07-22',NULL),(2,'bbadmin',0,'Biobank Administrator','NoName',NULL,NULL,NULL,NULL,'7Bg9siN5e7M=',NULL,NULL,NULL,'2009-07-22',NULL),(8,'miniaci',0,'Jessica','Miniaci','Canadian Biosample Repository','','Laboratory Technician','780-919-6735','ACrDFGBVCHOq4RawigB4Ig==','jessica.miniaci@ualberta.ca',NULL,NULL,'2010-07-08',''),(9,'elizabeth',0,'Elizabeth','Taylor','','','','','Vzk3xic4SKi8j2uyHEABIQ==','',NULL,NULL,'2010-04-16',''),(10,'peck',0,'Aaron','Peck','Canadian Biosample Repository','','Lab Technician','','zs4yUro9LDo=','aaron.peck@ualberta.ca',NULL,NULL,'2010-07-08',''),(11,'holland',0,'Charity','Holland','Canadian Biosample Repository','','','','jgw6x+HUai0=','charity.holland@ualberta.ca',NULL,NULL,'2010-07-08',''),(12,'Meagen',0,'Meagen','LaFave','','','','','Z2+3QVFL27DxoZuHa6AoWA==','cbsr.financial@me.com',NULL,NULL,'2010-06-08',''),(13,'degrisda',0,'Delphine','Degris-Dard','','','','','CFu6ZPVAO+S8j2uyHEABIQ==','',NULL,NULL,'2010-06-30',''),(15,'loyola',0,'Nelson','Loyola','','','','','Um6QXDsC3vs=','loyola@ualberta.ca',NULL,NULL,'2010-07-14',''),(17,'tpolasek',0,'thomas','polasek','','','','','8y8jUYdY0sg=','',NULL,NULL,'2010-06-30',''),(18,'aaron_aicml',0,'Aaron','Young','','','','','qmP9VkaU0jO32lSKMjM/lw==','aaron.young@ualberta.ca',NULL,NULL,'2010-10-18',''),(19,'Andrijana',0,'Andrijana','Lawton','','','','','V4PzQj6by/Q=','',NULL,NULL,'2010-08-11',''),(20,'Virginia',0,'Virginia','Doe','','','','','tsjSShkZ7qC8j2uyHEABIQ==','',NULL,NULL,'2010-08-11',''),(26,'igor',0,'igor','koganov','','','','','56IJY5WB+DA=','',NULL,NULL,'2011-10-12',''),(27,'testuser',0,'testuser','testuser',NULL,NULL,NULL,NULL,'orDBlaojDQE=',NULL,NULL,NULL,'2011-11-02',NULL);
/*!40000 ALTER TABLE `csm_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user_group`
--

DROP TABLE IF EXISTS `csm_user_group`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_user_group` (
  `USER_GROUP_ID` bigint(20) NOT NULL auto_increment,
  `USER_ID` bigint(20) NOT NULL,
  `GROUP_ID` bigint(20) NOT NULL,
  PRIMARY KEY  (`USER_GROUP_ID`),
  KEY `idx_USER_ID` (`USER_ID`),
  KEY `idx_GROUP_ID` (`GROUP_ID`),
  CONSTRAINT `FK_UG_GROUP` FOREIGN KEY (`GROUP_ID`) REFERENCES `csm_group` (`GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_user_group`
--

LOCK TABLES `csm_user_group` WRITE;
/*!40000 ALTER TABLE `csm_user_group` DISABLE KEYS */;
INSERT INTO `csm_user_group` VALUES (23,9,5),(26,8,5),(27,13,5),(28,17,7),(32,15,5),(33,18,5),(35,10,5),(36,12,6),(44,11,5),(45,20,8),(46,19,9),(54,26,5),(55,27,5),(56,27,6);
/*!40000 ALTER TABLE `csm_user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user_group_role_pg`
--

DROP TABLE IF EXISTS `csm_user_group_role_pg`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_user_group_role_pg` (
  `USER_GROUP_ROLE_PG_ID` bigint(20) NOT NULL auto_increment,
  `USER_ID` bigint(20) default NULL,
  `GROUP_ID` bigint(20) default NULL,
  `ROLE_ID` bigint(20) NOT NULL,
  `PROTECTION_GROUP_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date NOT NULL default '0000-00-00',
  PRIMARY KEY  (`USER_GROUP_ROLE_PG_ID`),
  KEY `idx_GROUP_ID` (`GROUP_ID`),
  KEY `idx_ROLE_ID` (`ROLE_ID`),
  KEY `idx_PROTECTION_GROUP_ID` (`PROTECTION_GROUP_ID`),
  KEY `idx_USER_ID` (`USER_ID`),
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_GROUPS` FOREIGN KEY (`GROUP_ID`) REFERENCES `csm_group` (`GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_PROTECTION_GROUP` FOREIGN KEY (`PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_ROLE` FOREIGN KEY (`ROLE_ID`) REFERENCES `csm_role` (`ROLE_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_USER` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_user_group_role_pg`
--

LOCK TABLES `csm_user_group_role_pg` WRITE;
/*!40000 ALTER TABLE `csm_user_group_role_pg` DISABLE KEYS */;
INSERT INTO `csm_user_group_role_pg` VALUES (163,NULL,9,8,45,'2010-10-20'),(165,NULL,8,7,1,'2010-10-20'),(169,NULL,8,8,50,'2010-10-20'),(170,NULL,8,8,47,'2010-10-20'),(171,NULL,6,7,1,'2010-10-20'),(174,NULL,6,8,45,'2010-10-20'),(175,NULL,7,8,48,'2010-10-20'),(176,NULL,7,7,1,'2010-10-20'),(179,NULL,7,8,46,'2010-10-20'),(180,NULL,7,8,50,'2010-10-20'),(181,NULL,7,8,47,'2010-10-20'),(182,NULL,5,8,1,'2010-10-20'),(184,NULL,9,7,1,'2010-10-20'),(193,NULL,7,8,67,'2011-03-11'),(194,NULL,7,8,66,'2011-03-11'),(195,NULL,7,8,65,'2011-03-11'),(196,NULL,8,8,67,'2011-03-11'),(197,NULL,8,8,66,'2011-03-11'),(198,NULL,5,8,45,'2011-03-11'),(199,NULL,5,8,73,'2011-03-14');
/*!40000 ALTER TABLE `csm_user_group_role_pg` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user_pe`
--

DROP TABLE IF EXISTS `csm_user_pe`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `csm_user_pe` (
  `USER_PROTECTION_ELEMENT_ID` bigint(20) NOT NULL auto_increment,
  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  PRIMARY KEY  (`USER_PROTECTION_ELEMENT_ID`),
  UNIQUE KEY `UQ_USER_PROTECTION_ELEMENT_PROTECTION_ELEMENT_ID` (`USER_ID`,`PROTECTION_ELEMENT_ID`),
  KEY `idx_USER_ID` (`USER_ID`),
  KEY `idx_PROTECTION_ELEMENT_ID` (`PROTECTION_ELEMENT_ID`),
  CONSTRAINT `FK_PE_USER` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PROTECTION_ELEMENT_USER` FOREIGN KEY (`PROTECTION_ELEMENT_ID`) REFERENCES `csm_protection_element` (`PROTECTION_ELEMENT_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `csm_user_pe`
--

LOCK TABLES `csm_user_pe` WRITE;
/*!40000 ALTER TABLE `csm_user_pe` DISABLE KEYS */;
INSERT INTO `csm_user_pe` VALUES (1,1,1),(2,2,2);
/*!40000 ALTER TABLE `csm_user_pe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispatch`
--

DROP TABLE IF EXISTS `dispatch`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `dispatch` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `STATE` int(11) default NULL,
  `COMMENT` text collate latin1_general_cs,
  `SHIPMENT_INFO_ID` int(11) default NULL,
  `RECEIVER_CENTER_ID` int(11) NOT NULL,
  `SENDER_CENTER_ID` int(11) NOT NULL,
  `REQUEST_ID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `SHIPMENT_INFO_ID` (`SHIPMENT_INFO_ID`),
  KEY `FK3F9F347AA2F14F4F` (`REQUEST_ID`),
  KEY `FK3F9F347A307B2CB5` (`RECEIVER_CENTER_ID`),
  KEY `FK3F9F347AF59D873A` (`SHIPMENT_INFO_ID`),
  KEY `FK3F9F347A91BC3D7B` (`SENDER_CENTER_ID`),
  CONSTRAINT `FK3F9F347A307B2CB5` FOREIGN KEY (`RECEIVER_CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK3F9F347A91BC3D7B` FOREIGN KEY (`SENDER_CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK3F9F347AA2F14F4F` FOREIGN KEY (`REQUEST_ID`) REFERENCES `request` (`ID`),
  CONSTRAINT `FK3F9F347AF59D873A` FOREIGN KEY (`SHIPMENT_INFO_ID`) REFERENCES `shipment_info` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `dispatch`
--

LOCK TABLES `dispatch` WRITE;
/*!40000 ALTER TABLE `dispatch` DISABLE KEYS */;
INSERT INTO `dispatch` VALUES (1,4,3,NULL,33,2,1,NULL),(2,4,3,NULL,1,2,1,NULL),(3,4,3,NULL,2,2,1,NULL),(4,0,0,NULL,NULL,1,2,NULL),(5,4,3,NULL,3,2,1,NULL),(6,4,3,NULL,4,2,1,NULL),(7,4,3,NULL,7,2,1,NULL),(8,4,3,NULL,8,2,1,NULL),(9,4,3,NULL,9,2,1,NULL),(10,4,3,NULL,12,2,1,NULL),(11,4,3,NULL,13,2,1,NULL),(12,4,3,NULL,16,1,2,NULL),(13,4,3,NULL,17,2,1,NULL),(15,4,3,NULL,19,2,1,NULL),(16,4,3,NULL,21,2,1,NULL),(17,4,3,NULL,23,2,1,NULL),(18,4,3,NULL,25,2,1,NULL),(19,4,3,NULL,26,2,1,NULL),(20,5,3,NULL,27,2,1,NULL),(21,4,3,NULL,29,2,1,NULL),(22,7,3,NULL,30,2,1,NULL),(23,4,3,NULL,34,2,1,NULL),(24,4,3,NULL,35,2,1,NULL),(26,4,3,NULL,38,2,1,NULL),(28,4,3,NULL,41,2,1,NULL),(29,4,3,NULL,43,2,1,NULL),(31,4,3,NULL,45,2,1,NULL),(32,4,3,NULL,47,2,1,NULL),(33,4,3,NULL,49,2,1,NULL),(36,4,3,NULL,52,2,1,NULL),(37,4,3,NULL,54,2,1,NULL),(38,4,3,NULL,56,2,1,NULL),(39,4,3,NULL,58,2,1,NULL),(40,2,2,NULL,59,5,6,NULL),(41,0,0,NULL,NULL,5,6,NULL);
/*!40000 ALTER TABLE `dispatch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispatch_specimen`
--

DROP TABLE IF EXISTS `dispatch_specimen`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `dispatch_specimen` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `STATE` int(11) default NULL,
  `COMMENT` text collate latin1_general_cs,
  `DISPATCH_ID` int(11) NOT NULL,
  `SPECIMEN_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKEE25592DEF199765` (`SPECIMEN_ID`),
  KEY `FKEE25592DDE99CA25` (`DISPATCH_ID`),
  CONSTRAINT `FKEE25592DDE99CA25` FOREIGN KEY (`DISPATCH_ID`) REFERENCES `dispatch` (`ID`),
  CONSTRAINT `FKEE25592DEF199765` FOREIGN KEY (`SPECIMEN_ID`) REFERENCES `specimen` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `dispatch_specimen`
--

LOCK TABLES `dispatch_specimen` WRITE;
/*!40000 ALTER TABLE `dispatch_specimen` DISABLE KEYS */;
INSERT INTO `dispatch_specimen` VALUES (1,4,1,NULL,1,21),(2,4,1,NULL,2,31),(3,4,1,NULL,3,30),(4,4,1,NULL,5,48),(5,4,1,NULL,6,47),(6,4,1,NULL,7,45),(7,1,3,NULL,6,44),(8,1,3,NULL,6,46),(9,4,1,NULL,8,59),(10,4,1,NULL,9,58),(11,4,1,NULL,10,49),(12,4,1,NULL,11,43),(13,4,1,NULL,12,114),(14,4,1,NULL,13,176),(16,4,1,NULL,15,173),(17,4,1,NULL,16,172),(18,4,1,NULL,17,171),(19,4,1,NULL,18,170),(20,4,1,NULL,19,169),(21,1,3,NULL,19,170),(22,5,1,NULL,20,168),(23,4,1,NULL,21,167),(24,7,1,NULL,22,199),(25,4,1,NULL,23,198),(26,4,1,NULL,24,206),(28,4,1,NULL,26,209),(30,4,1,NULL,28,208),(31,4,1,NULL,29,207),(33,4,1,NULL,31,210),(34,4,1,NULL,32,211),(35,4,1,NULL,33,212),(38,4,1,NULL,36,213),(39,4,1,NULL,37,227),(40,4,1,NULL,38,237),(41,4,1,NULL,39,247),(42,2,0,NULL,40,261),(43,0,0,NULL,41,260);
/*!40000 ALTER TABLE `dispatch_specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity`
--

DROP TABLE IF EXISTS `entity`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `entity` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CLASS_NAME` varchar(255) collate latin1_general_cs default NULL,
  `NAME` varchar(255) collate latin1_general_cs default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `entity`
--

LOCK TABLES `entity` WRITE;
/*!40000 ALTER TABLE `entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_column`
--

DROP TABLE IF EXISTS `entity_column`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `entity_column` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) collate latin1_general_cs default NULL,
  `ENTITY_PROPERTY_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK16BD7321698D6AC` (`ENTITY_PROPERTY_ID`),
  CONSTRAINT `FK16BD7321698D6AC` FOREIGN KEY (`ENTITY_PROPERTY_ID`) REFERENCES `entity_property` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `entity_column`
--

LOCK TABLES `entity_column` WRITE;
/*!40000 ALTER TABLE `entity_column` DISABLE KEYS */;
/*!40000 ALTER TABLE `entity_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_filter`
--

DROP TABLE IF EXISTS `entity_filter`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `entity_filter` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `FILTER_TYPE` int(11) default NULL,
  `NAME` varchar(255) collate latin1_general_cs default NULL,
  `ENTITY_PROPERTY_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK635CF541698D6AC` (`ENTITY_PROPERTY_ID`),
  CONSTRAINT `FK635CF541698D6AC` FOREIGN KEY (`ENTITY_PROPERTY_ID`) REFERENCES `entity_property` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `entity_filter`
--

LOCK TABLES `entity_filter` WRITE;
/*!40000 ALTER TABLE `entity_filter` DISABLE KEYS */;
/*!40000 ALTER TABLE `entity_filter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_property`
--

DROP TABLE IF EXISTS `entity_property`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `entity_property` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `PROPERTY` varchar(255) collate latin1_general_cs default NULL,
  `PROPERTY_TYPE_ID` int(11) NOT NULL,
  `ENTITY_ID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK3FC956B191CFD445` (`ENTITY_ID`),
  KEY `FK3FC956B157C0C3B0` (`PROPERTY_TYPE_ID`),
  CONSTRAINT `FK3FC956B157C0C3B0` FOREIGN KEY (`PROPERTY_TYPE_ID`) REFERENCES `property_type` (`ID`),
  CONSTRAINT `FK3FC956B191CFD445` FOREIGN KEY (`ENTITY_ID`) REFERENCES `entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `entity_property`
--

LOCK TABLES `entity_property` WRITE;
/*!40000 ALTER TABLE `entity_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `entity_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_attr`
--

DROP TABLE IF EXISTS `event_attr`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `event_attr` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `VALUE` varchar(255) collate latin1_general_cs default NULL,
  `COLLECTION_EVENT_ID` int(11) NOT NULL,
  `STUDY_EVENT_ATTR_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK59508C96280272F2` (`COLLECTION_EVENT_ID`),
  KEY `FK59508C96A9CFCFDB` (`STUDY_EVENT_ATTR_ID`),
  CONSTRAINT `FK59508C96280272F2` FOREIGN KEY (`COLLECTION_EVENT_ID`) REFERENCES `collection_event` (`ID`),
  CONSTRAINT `FK59508C96A9CFCFDB` FOREIGN KEY (`STUDY_EVENT_ATTR_ID`) REFERENCES `study_event_attr` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `event_attr`
--

LOCK TABLES `event_attr` WRITE;
/*!40000 ALTER TABLE `event_attr` DISABLE KEYS */;
INSERT INTO `event_attr` VALUES (1,3,'pleb',27,1),(2,3,'type1',27,2),(3,3,'two;one',27,3),(4,3,'3',27,5),(5,1,'',28,3);
/*!40000 ALTER TABLE `event_attr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_attr_type`
--

DROP TABLE IF EXISTS `event_attr_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `event_attr_type` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(50) collate latin1_general_cs default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `event_attr_type`
--

LOCK TABLES `event_attr_type` WRITE;
/*!40000 ALTER TABLE `event_attr_type` DISABLE KEYS */;
INSERT INTO `event_attr_type` VALUES (1,0,'number'),(2,0,'text'),(3,0,'date_time'),(4,0,'select_single'),(5,0,'select_multiple');
/*!40000 ALTER TABLE `event_attr_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `global_event_attr`
--

DROP TABLE IF EXISTS `global_event_attr`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `global_event_attr` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `LABEL` varchar(50) collate latin1_general_cs default NULL,
  `EVENT_ATTR_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKBE7ED6B25B770B31` (`EVENT_ATTR_TYPE_ID`),
  CONSTRAINT `FKBE7ED6B25B770B31` FOREIGN KEY (`EVENT_ATTR_TYPE_ID`) REFERENCES `event_attr_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `global_event_attr`
--

LOCK TABLES `global_event_attr` WRITE;
/*!40000 ALTER TABLE `global_event_attr` DISABLE KEYS */;
INSERT INTO `global_event_attr` VALUES (1,0,'PBMC Count (x10^6)',1),(3,0,'Consent',5),(4,0,'Phlebotomist',2),(6,0,'Biopsy Length',1),(7,0,'Patient Type',4);
/*!40000 ALTER TABLE `global_event_attr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `global_specimen_attr`
--

DROP TABLE IF EXISTS `global_specimen_attr`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `global_specimen_attr` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `LABEL` varchar(50) default NULL,
  `SPECIMEN_ATTR_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKA55D6D2C494E5767` (`SPECIMEN_ATTR_TYPE_ID`),
  CONSTRAINT `FKA55D6D2C494E5767` FOREIGN KEY (`SPECIMEN_ATTR_TYPE_ID`) REFERENCES `specimen_attr_type` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `global_specimen_attr`
--

LOCK TABLES `global_specimen_attr` WRITE;
/*!40000 ALTER TABLE `global_specimen_attr` DISABLE KEYS */;
INSERT INTO `global_specimen_attr` VALUES (1,0,'Volume',1),(2,0,'Concentration',1),(3,0,'startProcess',3),(4,0,'endProcess',3),(5,0,'SampleErrors',2);
/*!40000 ALTER TABLE `global_specimen_attr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jasper_template`
--

DROP TABLE IF EXISTS `jasper_template`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `jasper_template` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(50) collate latin1_general_cs default NULL,
  `XML` text collate latin1_general_cs,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `jasper_template`
--

LOCK TABLES `jasper_template` WRITE;
/*!40000 ALTER TABLE `jasper_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `jasper_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `log` (
  `ID` int(11) NOT NULL auto_increment,
  `USERNAME` varchar(100) collate latin1_general_cs default NULL,
  `CREATED_AT` datetime default NULL,
  `CENTER` varchar(50) collate latin1_general_cs default NULL,
  `ACTION` varchar(100) collate latin1_general_cs default NULL,
  `PATIENT_NUMBER` varchar(100) collate latin1_general_cs default NULL,
  `INVENTORY_ID` varchar(100) collate latin1_general_cs default NULL,
  `LOCATION_LABEL` varchar(255) collate latin1_general_cs default NULL,
  `DETAILS` text collate latin1_general_cs,
  `TYPE` varchar(100) collate latin1_general_cs default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1932 DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `log`
--

LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
INSERT INTO `log` VALUES (1,'igor','2011-10-12 12:23:13','','login','','','','',''),(2,'igor','2011-10-12 12:27:44','','logout','','','','',''),(3,'igor','2011-10-12 12:27:51','','login','','','','',''),(4,'igor','2011-10-12 12:36:40','','logout','','','','',''),(5,'igor','2011-10-12 12:36:50','','login','','','','',''),(6,'igor','2011-10-12 12:37:18','','insert','P10','','','','Patient'),(7,'igor','2011-10-12 12:37:19','IK Clinic','select','P10','','','Patient LOOKUP','Patient'),(8,'igor','2011-10-12 12:38:43','','logout','','','','',''),(9,'igor','2011-10-12 12:38:49','','login','','','','',''),(10,'igor','2011-10-12 12:38:56','IK Clinic','select','P10','','','Patient LOOKUP','Patient'),(11,'igor','2011-10-12 12:39:01','','delete','P10','','','','Patient'),(12,'igor','2011-10-12 12:39:49','','insert','P10','','','','Patient'),(13,'igor','2011-10-12 12:39:50','IK Clinic','select','P10','','','Patient LOOKUP','Patient'),(14,'igor','2011-10-12 14:16:32','','logout','','','','',''),(15,'igor','2011-10-13 10:23:33','','login','','','','',''),(16,'igor','2011-10-13 10:23:56','','logout','','','','',''),(17,'igor','2011-10-13 10:24:05','','login','','','','',''),(18,'igor','2011-10-13 10:24:19','','logout','','','','',''),(19,'igor','2011-10-13 10:30:45','','login','','','','',''),(20,'igor','2011-10-13 10:31:27','','insert','P100','','','','Patient'),(21,'igor','2011-10-13 10:31:27','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(22,'igor','2011-10-13 10:54:04','','login','','','','',''),(23,'igor','2011-10-13 10:54:09','','logout','','','','',''),(24,'igor','2011-10-13 11:39:10','','insert','P100','','','visit:1, specimens:10','CollectionEvent'),(25,'igor','2011-10-13 11:39:10','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(26,'igor','2011-10-13 11:39:55','IK Clinic','edit','P100','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(27,'igor','2011-10-13 11:40:38','IK Clinic','delete','P100','C010','','','Specimen'),(28,'igor','2011-10-13 11:40:39','IK Clinic','delete','P100','C009','','','Specimen'),(29,'igor','2011-10-13 11:40:39','IK Clinic','delete','P100','C008','','','Specimen'),(30,'igor','2011-10-13 11:40:39','IK Clinic','delete','P100','C007','','','Specimen'),(31,'igor','2011-10-13 11:40:39','IK Clinic','delete','P100','C006','','','Specimen'),(32,'igor','2011-10-13 11:40:39','IK Clinic','delete','P100','C005','','','Specimen'),(33,'igor','2011-10-13 11:40:39','IK Clinic','delete','P100','C004','','','Specimen'),(34,'igor','2011-10-13 11:40:40','IK Clinic','delete','P100','C003','','','Specimen'),(35,'igor','2011-10-13 11:40:40','IK Clinic','delete','P100','C002','','','Specimen'),(36,'igor','2011-10-13 11:40:40','IK Clinic','delete','P100','C001','','','Specimen'),(37,'igor','2011-10-13 11:40:40','','update','P100','','','visit:1, specimens:0','CollectionEvent'),(38,'igor','2011-10-13 11:40:40','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(39,'igor','2011-10-13 11:40:44','','delete','P100','','','visit:1, specimens:0','CollectionEvent'),(40,'igor','2011-10-13 11:40:49','','delete','P100','','','','Patient'),(41,'igor','2011-10-13 11:41:12','','insert','P100','','','','Patient'),(42,'igor','2011-10-13 11:41:12','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(43,'igor','2011-10-13 11:41:19','','insert','P100','','','visit:1, specimens:10','CollectionEvent'),(44,'igor','2011-10-13 11:41:19','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(45,'igor','2011-10-13 11:45:17','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(46,'igor','2011-10-13 11:46:45','IK Clinic','edit','P100','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(47,'igor','2011-10-13 12:06:02','IK Clinic','delete','P100','C004','','','Specimen'),(48,'igor','2011-10-13 12:06:02','IK Clinic','delete','P100','C005','','','Specimen'),(49,'igor','2011-10-13 12:06:02','IK Clinic','delete','P100','C002','','','Specimen'),(50,'igor','2011-10-13 12:06:02','IK Clinic','delete','P100','C003','','','Specimen'),(51,'igor','2011-10-13 12:06:03','IK Clinic','delete','P100','C001','','','Specimen'),(52,'igor','2011-10-13 12:06:03','IK Clinic','delete','P100','C010','','','Specimen'),(53,'igor','2011-10-13 12:06:03','IK Clinic','delete','P100','C009','','','Specimen'),(54,'igor','2011-10-13 12:06:03','IK Clinic','delete','P100','C008','','','Specimen'),(55,'igor','2011-10-13 12:06:03','IK Clinic','delete','P100','C007','','','Specimen'),(56,'igor','2011-10-13 12:06:03','IK Clinic','delete','P100','C006','','','Specimen'),(57,'igor','2011-10-13 12:06:03','','update','P100','','','visit:1, specimens:0','CollectionEvent'),(58,'igor','2011-10-13 12:06:03','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(59,'igor','2011-10-13 12:06:15','','delete','P100','','','visit:1, specimens:0','CollectionEvent'),(60,'igor','2011-10-13 12:06:18','','delete','P100','','','','Patient'),(61,'igor','2011-10-13 13:49:07','','insert','P01','','','','Patient'),(62,'igor','2011-10-13 13:49:07','IK Clinic','select','P01','','','Patient LOOKUP','Patient'),(63,'igor','2011-10-13 13:49:32','','insert','P01','','','visit:1, specimens:1','CollectionEvent'),(64,'igor','2011-10-13 13:49:33','IK Clinic','select','P01','','','CollectionEvent LOOKUP, visit:1, specimens:1','CollectionEvent'),(65,'igor','2011-10-13 13:49:50','IK Clinic','insert','','','','state: Creation','Dispatch'),(66,'igor','2011-10-13 13:49:50','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(67,'igor','2011-10-13 13:51:39','','insert','P100','','','','Patient'),(68,'igor','2011-10-13 13:51:39','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(69,'igor','2011-10-13 13:51:46','','insert','P100','','','visit:1, specimens:10','CollectionEvent'),(70,'igor','2011-10-13 13:51:46','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(71,'igor','2011-10-13 13:52:23','IK Clinic','insert','','','','state: Creation','Dispatch'),(72,'igor','2011-10-13 13:52:23','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(73,'igor','2011-10-13 13:52:45','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-13 13:52','Dispatch'),(74,'igor','2011-10-13 13:52:45','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-13 13:52','Dispatch'),(75,'igor','2011-10-13 13:53:00','IK Clinic','insert','','','','state: Creation','Dispatch'),(76,'igor','2011-10-13 13:53:00','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(77,'igor','2011-10-13 13:53:09','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-13 13:53','Dispatch'),(78,'igor','2011-10-13 13:53:09','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-13 13:53','Dispatch'),(79,'igor','2011-10-13 13:53:13','','logout','','','','',''),(80,'igor','2011-10-13 13:53:20','','login','','','','',''),(81,'igor','2011-10-13 13:53:32','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-13 13:52','Dispatch'),(82,'igor','2011-10-13 13:53:38','IK Site','update','','','','state: Received, received at: 2011-10-13 13:53','Dispatch'),(83,'igor','2011-10-13 13:53:38','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-13 13:53','Dispatch'),(84,'igor','2011-10-13 13:53:57','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-13 13:53','Dispatch'),(85,'igor','2011-10-13 13:53:58','IK Site','update','','','','state: Received, received at: 2011-10-13 13:53','Dispatch'),(86,'igor','2011-10-13 13:53:59','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-13 13:53','Dispatch'),(87,'igor','2011-10-13 13:55:06','IK Site','update','P100','C002','','','Specimen'),(88,'igor','2011-10-13 13:55:06','IK Site','update','','','','state: Received, received at: 2011-10-13 13:53','Dispatch'),(89,'igor','2011-10-13 13:55:06','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-13 13:53','Dispatch'),(90,'igor','2011-10-13 13:55:26','IK Site','update','','','','state: Closed, received at: 2011-10-13 13:53','Dispatch'),(91,'igor','2011-10-13 13:55:26','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-13 13:53','Dispatch'),(92,'igor','2011-10-13 13:55:31','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-13 13:53','Dispatch'),(93,'igor','2011-10-13 13:55:34','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-13 13:53','Dispatch'),(94,'igor','2011-10-13 13:55:43','IK Site','update','P100','C001','','','Specimen'),(95,'igor','2011-10-13 13:55:43','IK Site','update','','','','state: Received, received at: 2011-10-13 13:53','Dispatch'),(96,'igor','2011-10-13 13:55:43','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-13 13:53','Dispatch'),(97,'igor','2011-10-13 13:55:46','IK Site','update','','','','state: Closed, received at: 2011-10-13 13:53','Dispatch'),(98,'igor','2011-10-13 13:55:46','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-13 13:53','Dispatch'),(99,'igor','2011-10-13 13:55:51','','logout','','','','',''),(100,'igor','2011-10-13 13:55:59','','login','','','','',''),(101,'igor','2011-10-13 13:56:04','','logout','','','','',''),(102,'igor','2011-10-13 13:56:11','','login','','','','',''),(103,'igor','2011-10-13 14:07:46','','login','','','','',''),(104,'igor','2011-10-13 14:10:17','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(105,'igor','2011-10-13 14:10:24','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(106,'igor','2011-10-13 14:12:31','','logout','','','','',''),(107,'igor','2011-10-13 14:13:40','','login','','','','',''),(108,'igor','2011-10-13 14:14:42','','insert','P101','','','','Patient'),(109,'igor','2011-10-13 14:14:42','IK Clinic','select','P101','','','Patient LOOKUP','Patient'),(110,'igor','2011-10-13 14:14:50','','insert','P101','','','visit:1, specimens:10','CollectionEvent'),(111,'igor','2011-10-13 14:14:50','IK Clinic','select','P101','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(112,'igor','2011-10-13 14:15:21','IK Clinic','edit','P101','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(113,'igor','2011-10-13 14:15:54','IK Clinic','delete','P101','C018','','','Specimen'),(114,'igor','2011-10-13 14:15:54','IK Clinic','delete','P101','C017','','','Specimen'),(115,'igor','2011-10-13 14:15:54','IK Clinic','delete','P101','C020','','','Specimen'),(116,'igor','2011-10-13 14:15:54','IK Clinic','delete','P101','C019','','','Specimen'),(117,'igor','2011-10-13 14:15:54','IK Clinic','delete','P101','C014','','','Specimen'),(118,'igor','2011-10-13 14:15:54','IK Clinic','delete','P101','C013','','','Specimen'),(119,'igor','2011-10-13 14:15:54','IK Clinic','delete','P101','C016','','','Specimen'),(120,'igor','2011-10-13 14:15:54','IK Clinic','delete','P101','C015','','','Specimen'),(121,'igor','2011-10-13 14:15:55','IK Clinic','delete','P101','C012','','','Specimen'),(122,'igor','2011-10-13 14:15:55','IK Clinic','delete','P101','C011','','','Specimen'),(123,'igor','2011-10-13 14:15:55','','update','P101','','','visit:1, specimens:0','CollectionEvent'),(124,'igor','2011-10-13 14:15:55','IK Clinic','select','P101','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(125,'igor','2011-10-13 14:15:58','','delete','P101','','','visit:1, specimens:0','CollectionEvent'),(126,'igor','2011-10-13 14:16:05','','delete','P101','','','','Patient'),(127,'igor','2011-10-13 14:16:11','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(128,'igor','2011-10-13 14:16:13','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(129,'igor','2011-10-13 14:16:14','IK Clinic','edit','P100','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(130,'igor','2011-10-13 14:17:09','IK Clinic','delete','P100','C009','','','Specimen'),(131,'igor','2011-10-13 14:17:10','IK Clinic','delete','P100','C010','','','Specimen'),(132,'igor','2011-10-13 14:17:10','IK Clinic','delete','P100','C007','','','Specimen'),(133,'igor','2011-10-13 14:17:10','IK Clinic','delete','P100','C008','','','Specimen'),(134,'igor','2011-10-13 14:17:10','IK Clinic','delete','P100','C005','','','Specimen'),(135,'igor','2011-10-13 14:17:10','IK Clinic','delete','P100','C006','','','Specimen'),(136,'igor','2011-10-13 14:17:10','IK Clinic','delete','P100','C003','','','Specimen'),(137,'igor','2011-10-13 14:17:10','IK Clinic','delete','P100','C004','','','Specimen'),(138,'igor','2011-10-13 14:17:28','','delete','','','','','Specimen'),(139,'igor','2011-10-13 14:17:28','','delete','','','','','Specimen'),(140,'igor','2011-10-13 14:17:28','','delete','','','','','Specimen'),(141,'igor','2011-10-13 14:17:28','','delete','','','','','Specimen'),(142,'igor','2011-10-13 14:17:28','','delete','','','','','Specimen'),(143,'igor','2011-10-13 14:17:28','','delete','','','','','Specimen'),(144,'igor','2011-10-13 14:17:28','','delete','','','','','Specimen'),(145,'igor','2011-10-13 14:17:28','','delete','','','','','Specimen'),(146,'igor','2011-10-13 14:17:52','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:2','CollectionEvent'),(147,'igor','2011-10-13 14:17:58','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:2','CollectionEvent'),(148,'igor','2011-10-13 14:18:22','IK Clinic','edit','P100','','','CollectionEvent EDIT, visit:1, specimens:2','CollectionEvent'),(149,'igor','2011-10-13 14:19:56','','update','P100','','','visit:1, specimens:10','CollectionEvent'),(150,'igor','2011-10-13 14:19:56','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(151,'igor','2011-10-13 14:22:59','','logout','','','','',''),(152,'igor','2011-10-13 14:25:46','','login','','','','',''),(153,'igor','2011-10-13 14:27:01','','logout','','','','',''),(154,'igor','2011-10-13 14:51:58','','login','','','','',''),(155,'igor','2011-10-13 14:52:36','','logout','','','','',''),(156,'igor','2011-10-13 15:03:50','','login','','','','',''),(157,'igor','2011-10-13 15:04:44','','logout','','','','',''),(158,'igor','2011-10-13 15:05:17','','login','','','','',''),(159,'igor','2011-10-13 15:05:57','','logout','','','','',''),(160,'igor','2011-10-13 15:07:22','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(161,'igor','2011-10-13 15:08:16','IK Clinic','update','','','','waybill:null, specimens:1','Shipment'),(162,'igor','2011-10-13 15:09:27','IK Clinic','update','P100','C003','','','Specimen'),(163,'igor','2011-10-13 15:09:27','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(164,'igor','2011-10-13 15:09:34','','logout','','','','',''),(165,'igor','2011-10-13 15:11:23','','login','','','','',''),(166,'igor','2011-10-13 15:40:14','','login','','','','',''),(167,'igor','2011-10-13 15:41:19','IK Site','insert','','','','state: Creation','Dispatch'),(168,'igor','2011-10-13 15:41:19','IK Site','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(169,'igor','2011-10-13 15:41:23','IK Site','edit','','','','Dispatch EDIT, state: Creation','Dispatch'),(170,'igor','2011-10-13 15:42:44','','logout','','','','',''),(171,'igor','2011-10-13 15:42:53','','login','','','','',''),(172,'igor','2011-10-13 16:51:42','','logout','','','','',''),(173,'igor','2011-10-14 09:01:18','','login','','','','',''),(174,'igor','2011-10-14 09:02:24','','logout','','','','',''),(175,'igor','2011-10-14 09:11:51','','login','','','','',''),(176,'igor','2011-10-14 09:12:25','','logout','','','','',''),(177,'igor','2011-10-14 09:18:54','','login','','','','',''),(178,'igor','2011-10-14 09:24:18','','logout','','','','',''),(179,'igor','2011-10-14 09:28:49','','login','','','','',''),(180,'igor','2011-10-14 09:29:26','IK Clinic','insert','','','','state: Creation','Dispatch'),(181,'igor','2011-10-14 09:29:26','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(182,'igor','2011-10-14 09:29:42','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-14 09:29','Dispatch'),(183,'igor','2011-10-14 09:29:42','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 09:29','Dispatch'),(184,'igor','2011-10-14 09:29:57','IK Clinic','insert','','','','state: Creation','Dispatch'),(185,'igor','2011-10-14 09:29:58','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(186,'igor','2011-10-14 09:30:11','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-14 09:30','Dispatch'),(187,'igor','2011-10-14 09:30:11','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 09:30','Dispatch'),(188,'igor','2011-10-14 09:30:48','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(189,'igor','2011-10-14 09:32:37','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(190,'igor','2011-10-14 09:33:28','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 09:29','Dispatch'),(191,'igor','2011-10-14 09:33:31','IK Clinic','edit','','','','Dispatch EDIT, state: In Transit, packed at: 2011-10-14 09:29','Dispatch'),(192,'igor','2011-10-14 09:33:38','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 09:29','Dispatch'),(193,'igor','2011-10-14 09:33:48','','logout','','','','',''),(194,'igor','2011-10-14 09:33:58','','login','','','','',''),(195,'igor','2011-10-14 09:34:23','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 09:29','Dispatch'),(196,'igor','2011-10-14 09:34:31','IK Site','update','','','','state: Received, received at: 2011-10-14 09:34','Dispatch'),(197,'igor','2011-10-14 09:34:31','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 09:34','Dispatch'),(198,'igor','2011-10-14 09:34:51','IK Site','update','P100','C004','','','Specimen'),(199,'igor','2011-10-14 09:34:51','IK Site','update','','','','state: Received, received at: 2011-10-14 09:34','Dispatch'),(200,'igor','2011-10-14 09:34:51','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 09:34','Dispatch'),(201,'igor','2011-10-14 09:34:54','IK Site','update','','','','state: Closed, received at: 2011-10-14 09:34','Dispatch'),(202,'igor','2011-10-14 09:34:54','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-14 09:34','Dispatch'),(203,'igor','2011-10-14 09:34:58','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 09:30','Dispatch'),(204,'igor','2011-10-14 09:35:01','IK Site','update','','','','state: Received, received at: 2011-10-14 09:35','Dispatch'),(205,'igor','2011-10-14 09:35:02','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 09:35','Dispatch'),(206,'igor','2011-10-14 09:35:30','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 09:35','Dispatch'),(207,'igor','2011-10-14 09:35:48','','logout','','','','',''),(208,'igor','2011-10-14 09:35:55','','login','','','','',''),(209,'igor','2011-10-14 09:36:14','IK Clinic','insert','','','','state: Creation','Dispatch'),(210,'igor','2011-10-14 09:36:14','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(211,'igor','2011-10-14 09:36:21','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-14 09:36','Dispatch'),(212,'igor','2011-10-14 09:36:21','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 09:36','Dispatch'),(213,'igor','2011-10-14 09:36:32','','logout','','','','',''),(214,'igor','2011-10-14 09:36:36','','login','','','','',''),(215,'igor','2011-10-14 09:36:43','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 09:36','Dispatch'),(216,'igor','2011-10-14 09:36:45','IK Site','update','','','','state: Received, received at: 2011-10-14 09:36','Dispatch'),(217,'igor','2011-10-14 09:36:45','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 09:36','Dispatch'),(218,'igor','2011-10-14 09:37:12','IK Site','update','P100','C007','','','Specimen'),(219,'igor','2011-10-14 09:37:12','IK Site','update','','','','state: Received, received at: 2011-10-14 09:36','Dispatch'),(220,'igor','2011-10-14 09:37:12','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 09:36','Dispatch'),(221,'igor','2011-10-14 09:37:22','IK Site','update','','','','state: Closed, received at: 2011-10-14 09:36','Dispatch'),(222,'igor','2011-10-14 09:37:22','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-14 09:36','Dispatch'),(223,'igor','2011-10-14 09:37:40','IK Clinic','update','P100','C006','','','Specimen'),(224,'igor','2011-10-14 09:37:40','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(225,'igor','2011-10-14 09:37:48','IK Site','update','P100','C007','','','Specimen'),(226,'igor','2011-10-14 09:37:48','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(227,'igor','2011-10-14 09:37:52','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 09:35','Dispatch'),(228,'igor','2011-10-14 09:38:01','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 09:35','Dispatch'),(229,'igor','2011-10-14 09:39:03','IK Site','update','P100','C006','','','Specimen'),(230,'igor','2011-10-14 09:39:03','IK Site','update','P100','C008','','','Specimen'),(231,'igor','2011-10-14 09:39:03','IK Site','update','P100','C005','','','Specimen'),(232,'igor','2011-10-14 09:39:03','IK Site','update','','','','state: Received, received at: 2011-10-14 09:35','Dispatch'),(233,'igor','2011-10-14 09:39:03','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 09:35','Dispatch'),(234,'igor','2011-10-14 09:39:06','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 09:35','Dispatch'),(235,'igor','2011-10-14 09:41:28','','logout','','','','',''),(236,'igor','2011-10-14 09:41:34','','login','','','','',''),(237,'igor','2011-10-14 09:45:26','','logout','','','','',''),(238,'igor','2011-10-14 09:47:13','','login','','','','',''),(239,'igor','2011-10-14 09:48:38','','insert','P101','','','','Patient'),(240,'igor','2011-10-14 09:48:38','IK Clinic','select','P101','','','Patient LOOKUP','Patient'),(241,'igor','2011-10-14 09:48:46','','insert','P101','','','visit:1, specimens:10','CollectionEvent'),(242,'igor','2011-10-14 09:48:46','IK Clinic','select','P101','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(243,'igor','2011-10-14 09:49:52','','logout','','','','',''),(244,'igor','2011-10-14 09:50:34','','login','','','','',''),(245,'igor','2011-10-14 09:51:25','','logout','','','','',''),(246,'igor','2011-10-14 10:03:01','','login','','','','',''),(247,'igor','2011-10-14 10:16:08','','logout','','','','',''),(248,'igor','2011-10-14 10:19:51','','login','','','','',''),(249,'igor','2011-10-14 10:21:33','IK Clinic','select','P101','','','Patient LOOKUP','Patient'),(250,'igor','2011-10-14 10:21:49','IK Clinic','insert','','','','state: Creation','Dispatch'),(251,'igor','2011-10-14 10:21:49','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(252,'igor','2011-10-14 10:22:03','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-14 10:21','Dispatch'),(253,'igor','2011-10-14 10:22:04','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 10:21','Dispatch'),(254,'igor','2011-10-14 10:22:15','IK Clinic','insert','','','','state: Creation','Dispatch'),(255,'igor','2011-10-14 10:22:15','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(256,'igor','2011-10-14 10:22:23','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-14 10:22','Dispatch'),(257,'igor','2011-10-14 10:22:23','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 10:22','Dispatch'),(258,'igor','2011-10-14 10:22:49','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(259,'igor','2011-10-14 10:23:40','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(260,'igor','2011-10-14 10:24:04','','logout','','','','',''),(261,'igor','2011-10-14 10:24:14','','login','','','','',''),(262,'igor','2011-10-14 10:24:45','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 10:21','Dispatch'),(263,'igor','2011-10-14 10:24:50','IK Site','update','','','','state: Received, received at: 2011-10-14 10:24','Dispatch'),(264,'igor','2011-10-14 10:24:50','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 10:24','Dispatch'),(265,'igor','2011-10-14 10:24:58','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-14 10:22','Dispatch'),(266,'igor','2011-10-14 10:25:00','IK Site','update','','','','state: Received, received at: 2011-10-14 10:25','Dispatch'),(267,'igor','2011-10-14 10:25:01','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 10:25','Dispatch'),(268,'igor','2011-10-14 10:25:35','IK Site','update','P101','C012','','','Specimen'),(269,'igor','2011-10-14 10:25:35','IK Site','update','','','','state: Received, received at: 2011-10-14 10:25','Dispatch'),(270,'igor','2011-10-14 10:25:35','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:25','Dispatch'),(271,'igor','2011-10-14 10:25:40','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 10:25','Dispatch'),(272,'igor','2011-10-14 10:25:47','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:25','Dispatch'),(273,'igor','2011-10-14 10:26:34','','logout','','','','',''),(274,'igor','2011-10-14 10:27:08','','login','','','','',''),(275,'igor','2011-10-14 10:46:14','','logout','','','','',''),(276,'igor','2011-10-14 10:59:00','','login','','','','',''),(277,'igor','2011-10-14 11:00:54','','logout','','','','',''),(278,'igor','2011-10-14 11:20:52','','login','','','','',''),(279,'igor','2011-10-14 11:21:56','','logout','','','','',''),(280,'igor','2011-10-14 11:34:25','','login','','','','',''),(281,'igor','2011-10-14 11:35:20','','logout','','','','',''),(282,'igor','2011-10-14 11:36:16','','login','','','','',''),(283,'igor','2011-10-14 11:36:22','','insert','P102','','','','Patient'),(284,'igor','2011-10-14 11:36:22','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(285,'igor','2011-10-14 11:36:30','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(286,'igor','2011-10-14 11:36:31','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(287,'igor','2011-10-14 11:36:47','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(288,'igor','2011-10-14 11:37:19','IK Clinic','delete','P102','C022','','','Specimen'),(289,'igor','2011-10-14 11:37:20','IK Clinic','delete','P102','C021','','','Specimen'),(290,'igor','2011-10-14 11:37:20','IK Clinic','delete','P102','C026','','','Specimen'),(291,'igor','2011-10-14 11:37:20','IK Clinic','delete','P102','C025','','','Specimen'),(292,'igor','2011-10-14 11:37:20','IK Clinic','delete','P102','C024','','','Specimen'),(293,'igor','2011-10-14 11:37:20','IK Clinic','delete','P102','C023','','','Specimen'),(294,'igor','2011-10-14 11:37:20','IK Clinic','delete','P102','C027','','','Specimen'),(295,'igor','2011-10-14 11:37:20','IK Clinic','delete','P102','C028','','','Specimen'),(296,'igor','2011-10-14 11:37:20','IK Clinic','delete','P102','C029','','','Specimen'),(297,'igor','2011-10-14 11:37:21','IK Clinic','delete','P102','C030','','','Specimen'),(298,'igor','2011-10-14 11:37:21','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(299,'igor','2011-10-14 11:37:21','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(300,'igor','2011-10-14 11:37:24','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(301,'igor','2011-10-14 11:37:28','','delete','P102','','','','Patient'),(302,'igor','2011-10-14 11:37:31','','logout','','','','',''),(303,'igor','2011-10-14 11:38:02','','login','','','','',''),(304,'igor','2011-10-14 11:38:11','','logout','','','','',''),(305,'igor','2011-10-14 11:39:47','','login','','','','',''),(306,'igor','2011-10-14 11:39:52','','insert','P102','','','','Patient'),(307,'igor','2011-10-14 11:39:53','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(308,'igor','2011-10-14 11:40:01','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(309,'igor','2011-10-14 11:40:01','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(310,'igor','2011-10-14 11:40:32','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(311,'igor','2011-10-14 11:41:00','IK Clinic','delete','P102','C030','','','Specimen'),(312,'igor','2011-10-14 11:41:00','IK Clinic','delete','P102','C029','','','Specimen'),(313,'igor','2011-10-14 11:41:00','IK Clinic','delete','P102','C024','','','Specimen'),(314,'igor','2011-10-14 11:41:00','IK Clinic','delete','P102','C023','','','Specimen'),(315,'igor','2011-10-14 11:41:00','IK Clinic','delete','P102','C022','','','Specimen'),(316,'igor','2011-10-14 11:41:00','IK Clinic','delete','P102','C021','','','Specimen'),(317,'igor','2011-10-14 11:41:00','IK Clinic','delete','P102','C028','','','Specimen'),(318,'igor','2011-10-14 11:41:01','IK Clinic','delete','P102','C027','','','Specimen'),(319,'igor','2011-10-14 11:41:01','IK Clinic','delete','P102','C026','','','Specimen'),(320,'igor','2011-10-14 11:41:01','IK Clinic','delete','P102','C025','','','Specimen'),(321,'igor','2011-10-14 11:41:01','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(322,'igor','2011-10-14 11:41:01','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(323,'igor','2011-10-14 11:41:08','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(324,'igor','2011-10-14 11:41:11','','delete','P102','','','','Patient'),(325,'igor','2011-10-14 11:41:15','','logout','','','','',''),(326,'igor','2011-10-14 11:42:20','','login','','','','',''),(327,'igor','2011-10-14 11:42:25','','insert','P102','','','','Patient'),(328,'igor','2011-10-14 11:42:25','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(329,'igor','2011-10-14 11:42:32','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(330,'igor','2011-10-14 11:42:33','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(331,'igor','2011-10-14 11:44:14','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(332,'igor','2011-10-14 11:44:43','IK Clinic','delete','P102','C025','','','Specimen'),(333,'igor','2011-10-14 11:44:43','IK Clinic','delete','P102','C026','','','Specimen'),(334,'igor','2011-10-14 11:44:43','IK Clinic','delete','P102','C023','','','Specimen'),(335,'igor','2011-10-14 11:44:44','IK Clinic','delete','P102','C024','','','Specimen'),(336,'igor','2011-10-14 11:44:44','IK Clinic','delete','P102','C029','','','Specimen'),(337,'igor','2011-10-14 11:44:44','IK Clinic','delete','P102','C030','','','Specimen'),(338,'igor','2011-10-14 11:44:44','IK Clinic','delete','P102','C027','','','Specimen'),(339,'igor','2011-10-14 11:44:44','IK Clinic','delete','P102','C028','','','Specimen'),(340,'igor','2011-10-14 11:44:44','IK Clinic','delete','P102','C021','','','Specimen'),(341,'igor','2011-10-14 11:44:44','IK Clinic','delete','P102','C022','','','Specimen'),(342,'igor','2011-10-14 11:44:44','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(343,'igor','2011-10-14 11:44:45','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(344,'igor','2011-10-14 11:44:48','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(345,'igor','2011-10-14 11:44:55','','delete','P102','','','','Patient'),(346,'igor','2011-10-14 15:06:09','','logout','','','','',''),(347,'igor','2011-10-14 15:06:27','','login','','','','',''),(348,'igor','2011-10-14 15:06:33','','insert','P102','','','','Patient'),(349,'igor','2011-10-14 15:06:33','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(350,'igor','2011-10-14 15:06:42','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(351,'igor','2011-10-14 15:06:43','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(352,'igor','2011-10-14 15:36:38','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(353,'igor','2011-10-14 15:37:09','IK Clinic','delete','P102','C022','','','Specimen'),(354,'igor','2011-10-14 15:37:09','IK Clinic','delete','P102','C021','','','Specimen'),(355,'igor','2011-10-14 15:37:09','IK Clinic','delete','P102','C024','','','Specimen'),(356,'igor','2011-10-14 15:37:09','IK Clinic','delete','P102','C023','','','Specimen'),(357,'igor','2011-10-14 15:37:09','IK Clinic','delete','P102','C027','','','Specimen'),(358,'igor','2011-10-14 15:37:09','IK Clinic','delete','P102','C028','','','Specimen'),(359,'igor','2011-10-14 15:37:09','IK Clinic','delete','P102','C025','','','Specimen'),(360,'igor','2011-10-14 15:37:09','IK Clinic','delete','P102','C026','','','Specimen'),(361,'igor','2011-10-14 15:37:10','IK Clinic','delete','P102','C029','','','Specimen'),(362,'igor','2011-10-14 15:37:10','IK Clinic','delete','P102','C030','','','Specimen'),(363,'igor','2011-10-14 15:37:10','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(364,'igor','2011-10-14 15:37:10','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(365,'igor','2011-10-14 15:37:13','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(366,'igor','2011-10-14 15:37:17','','delete','P102','','','','Patient'),(367,'igor','2011-10-14 15:37:31','','logout','','','','',''),(368,'igor','2011-10-14 15:38:36','','login','','','','',''),(369,'igor','2011-10-14 15:38:41','','insert','P102','','','','Patient'),(370,'igor','2011-10-14 15:38:42','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(371,'igor','2011-10-14 15:38:49','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(372,'igor','2011-10-14 15:38:50','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(373,'igor','2011-10-14 15:49:11','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(374,'igor','2011-10-14 15:49:17','IK Clinic','edit','P102','','','Patient EDIT','Patient'),(375,'igor','2011-10-14 15:49:20','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(376,'igor','2011-10-14 15:49:24','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(377,'igor','2011-10-14 15:49:55','IK Clinic','delete','P102','C028','','','Specimen'),(378,'igor','2011-10-14 15:49:55','IK Clinic','delete','P102','C027','','','Specimen'),(379,'igor','2011-10-14 15:49:55','IK Clinic','delete','P102','C030','','','Specimen'),(380,'igor','2011-10-14 15:49:55','IK Clinic','delete','P102','C029','','','Specimen'),(381,'igor','2011-10-14 15:49:55','IK Clinic','delete','P102','C022','','','Specimen'),(382,'igor','2011-10-14 15:49:55','IK Clinic','delete','P102','C021','','','Specimen'),(383,'igor','2011-10-14 15:49:55','IK Clinic','delete','P102','C024','','','Specimen'),(384,'igor','2011-10-14 15:49:55','IK Clinic','delete','P102','C023','','','Specimen'),(385,'igor','2011-10-14 15:49:56','IK Clinic','delete','P102','C026','','','Specimen'),(386,'igor','2011-10-14 15:49:56','IK Clinic','delete','P102','C025','','','Specimen'),(387,'igor','2011-10-14 15:49:56','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(388,'igor','2011-10-14 15:49:56','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(389,'igor','2011-10-14 15:50:00','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(390,'igor','2011-10-14 15:50:09','','delete','P102','','','','Patient'),(391,'igor','2011-10-14 15:51:23','','insert','P1000','','','','Patient'),(392,'igor','2011-10-14 15:51:23','IK Clinic','select','P1000','','','Patient LOOKUP','Patient'),(393,'igor','2011-10-14 15:51:49','','insert','P1000','','','visit:1, specimens:1','CollectionEvent'),(394,'igor','2011-10-14 15:51:49','IK Clinic','select','P1000','','','CollectionEvent LOOKUP, visit:1, specimens:1','CollectionEvent'),(395,'igor','2011-10-14 16:01:44','IK Clinic','select','P1000','','','CollectionEvent LOOKUP, visit:1, specimens:1','CollectionEvent'),(396,'igor','2011-10-14 16:01:45','IK Clinic','edit','P1000','','','CollectionEvent EDIT, visit:1, specimens:1','CollectionEvent'),(397,'igor','2011-10-14 16:01:54','IK Clinic','delete','P1000','fq','','','Specimen'),(398,'igor','2011-10-14 16:01:55','','update','P1000','','','visit:1, specimens:0','CollectionEvent'),(399,'igor','2011-10-14 16:01:55','IK Clinic','select','P1000','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(400,'igor','2011-10-14 16:01:57','','delete','P1000','','','visit:1, specimens:0','CollectionEvent'),(401,'igor','2011-10-14 16:02:01','','delete','P1000','','','','Patient'),(402,'igor','2011-10-14 16:02:09','','logout','','','','',''),(403,'igor','2011-10-14 16:05:13','','login','','','','',''),(404,'igor','2011-10-14 16:05:18','','insert','P102','','','','Patient'),(405,'igor','2011-10-14 16:05:18','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(406,'igor','2011-10-14 16:07:15','','delete','P102','','','','Patient'),(407,'igor','2011-10-14 16:07:18','','logout','','','','',''),(408,'igor','2011-10-14 16:07:38','','login','','','','',''),(409,'igor','2011-10-14 16:08:04','','insert','P102','','','','Patient'),(410,'igor','2011-10-14 16:08:04','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(411,'igor','2011-10-14 16:11:43','','delete','P102','','','','Patient'),(412,'igor','2011-10-14 16:11:44','','logout','','','','',''),(413,'igor','2011-10-14 16:12:02','','login','','','','',''),(414,'igor','2011-10-14 16:12:07','','insert','P102','','','','Patient'),(415,'igor','2011-10-14 16:12:07','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(416,'igor','2011-10-14 16:13:13','','delete','P102','','','','Patient'),(417,'igor','2011-10-14 16:13:16','','logout','','','','',''),(418,'igor','2011-10-14 16:13:31','','login','','','','',''),(419,'igor','2011-10-14 16:13:35','','insert','P102','','','','Patient'),(420,'igor','2011-10-14 16:13:36','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(421,'igor','2011-10-14 16:13:53','','delete','P102','','','','Patient'),(422,'igor','2011-10-14 16:13:53','','logout','','','','',''),(423,'igor','2011-10-14 16:14:08','','login','','','','',''),(424,'igor','2011-10-14 16:14:12','','insert','P102','','','','Patient'),(425,'igor','2011-10-14 16:14:12','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(426,'igor','2011-10-14 16:15:59','','delete','P102','','','','Patient'),(427,'igor','2011-10-14 16:16:01','','logout','','','','',''),(428,'igor','2011-10-14 16:16:13','','login','','','','',''),(429,'igor','2011-10-14 16:16:17','','insert','P102','','','','Patient'),(430,'igor','2011-10-14 16:16:18','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(431,'igor','2011-10-14 16:16:51','','delete','P102','','','','Patient'),(432,'igor','2011-10-14 16:27:51','','logout','','','','',''),(433,'igor','2011-10-17 09:11:14','','login','','','','',''),(434,'igor','2011-10-17 09:11:54','IK Clinic','select','P01','','','Patient LOOKUP','Patient'),(435,'igor','2011-10-17 09:12:01','IK Clinic','select','P01','','','CollectionEvent LOOKUP, visit:1, specimens:1','CollectionEvent'),(436,'igor','2011-10-17 09:12:24','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(437,'igor','2011-10-17 09:12:30','IK Clinic','edit','P100','','','Patient EDIT','Patient'),(438,'igor','2011-10-17 09:12:36','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(439,'igor','2011-10-17 09:12:37','IK Clinic','select','P01','','','CollectionEvent LOOKUP, visit:1, specimens:1','CollectionEvent'),(440,'igor','2011-10-17 09:12:40','IK Clinic','edit','P01','','','CollectionEvent EDIT, visit:1, specimens:1','CollectionEvent'),(441,'igor','2011-10-17 09:12:57','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(442,'igor','2011-10-17 09:14:33','IK Clinic','insert','','','','state: Creation','Dispatch'),(443,'igor','2011-10-17 09:14:34','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(444,'igor','2011-10-17 09:14:44','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-17 09:14','Dispatch'),(445,'igor','2011-10-17 09:14:44','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-17 09:14','Dispatch'),(446,'igor','2011-10-17 09:15:28','IK Clinic','insert','','','','state: Creation','Dispatch'),(447,'igor','2011-10-17 09:15:29','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(448,'igor','2011-10-17 09:15:36','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-17 09:15','Dispatch'),(449,'igor','2011-10-17 09:15:36','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-17 09:15','Dispatch'),(450,'igor','2011-10-17 09:16:07','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(451,'igor','2011-10-17 09:17:32','IK Clinic','insert','','','','waybill:null, specimens:1','Shipment'),(452,'igor','2011-10-17 09:19:12','','logout','','','','',''),(453,'igor','2011-10-17 09:19:18','','login','','','','',''),(454,'igor','2011-10-17 09:19:59','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-17 09:14','Dispatch'),(455,'igor','2011-10-17 09:20:06','IK Site','update','','','','state: Received, received at: 2011-10-17 09:20','Dispatch'),(456,'igor','2011-10-17 09:20:06','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:20','Dispatch'),(457,'igor','2011-10-17 09:20:09','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-17 09:20','Dispatch'),(458,'igor','2011-10-17 09:20:20','IK Site','update','P100','C003','','','Specimen'),(459,'igor','2011-10-17 09:20:20','IK Site','update','','','','state: Received, received at: 2011-10-17 09:20','Dispatch'),(460,'igor','2011-10-17 09:20:21','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:20','Dispatch'),(461,'igor','2011-10-17 09:20:25','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-17 09:15','Dispatch'),(462,'igor','2011-10-17 09:20:27','IK Site','update','','','','state: Received, received at: 2011-10-17 09:20','Dispatch'),(463,'igor','2011-10-17 09:20:27','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-17 09:20','Dispatch'),(464,'igor','2011-10-17 09:20:56','IK Site','update','P100','C009','','','Specimen'),(465,'igor','2011-10-17 09:20:56','IK Site','update','','','','state: Received, received at: 2011-10-17 09:20','Dispatch'),(466,'igor','2011-10-17 09:20:57','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:20','Dispatch'),(467,'igor','2011-10-17 09:21:01','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-17 09:20','Dispatch'),(468,'igor','2011-10-17 09:21:04','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:20','Dispatch'),(469,'igor','2011-10-17 09:21:45','IK Clinic','update','','','','waybill:null, specimens:1','Shipment'),(470,'igor','2011-10-17 09:21:47','','logout','','','','',''),(471,'igor','2011-10-17 09:21:55','','login','','','','',''),(472,'igor','2011-10-17 09:23:11','IK Site','select','P01','','','Patient LOOKUP','Patient'),(473,'igor','2011-10-17 09:23:12','IK Site','select','P01','','','CollectionEvent LOOKUP, visit:1, specimens:1','CollectionEvent'),(474,'igor','2011-10-17 09:23:14','IK Site','edit','P01','','','CollectionEvent EDIT, visit:1, specimens:1','CollectionEvent'),(475,'igor','2011-10-17 09:23:48','','update','P01','','','visit:1, specimens:5','CollectionEvent'),(476,'igor','2011-10-17 09:23:49','IK Site','select','P01','','','CollectionEvent LOOKUP, visit:1, specimens:5','CollectionEvent'),(477,'igor','2011-10-17 09:40:12','IK Site','insert','','','','state: Creation','Dispatch'),(478,'igor','2011-10-17 09:40:13','IK Site','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(479,'igor','2011-10-17 09:40:22','IK Site','update','','','','state: In Transit, packed at: 2011-10-17 09:40','Dispatch'),(480,'igor','2011-10-17 09:40:23','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-17 09:40','Dispatch'),(481,'igor','2011-10-17 09:40:25','','logout','','','','',''),(482,'igor','2011-10-17 09:40:32','','login','','','','',''),(483,'igor','2011-10-17 09:41:06','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:24','Dispatch'),(484,'igor','2011-10-17 09:41:16','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-17 09:40','Dispatch'),(485,'igor','2011-10-17 09:41:34','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-17 09:40','Dispatch'),(486,'igor','2011-10-17 09:41:45','IK Site','edit','','','','Dispatch EDIT, state: In Transit, packed at: 2011-10-17 09:40','Dispatch'),(487,'igor','2011-10-17 09:41:57','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-17 09:40','Dispatch'),(488,'igor','2011-10-17 09:42:07','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-17 09:40','Dispatch'),(489,'igor','2011-10-17 09:42:10','','logout','','','','',''),(490,'igor','2011-10-17 09:42:17','','login','','','','',''),(491,'igor','2011-10-17 09:42:25','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-17 09:40','Dispatch'),(492,'igor','2011-10-17 09:42:38','IK Clinic','update','','','','state: Received, received at: 2011-10-17 09:42','Dispatch'),(493,'igor','2011-10-17 09:42:38','IK Clinic','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:42','Dispatch'),(494,'igor','2011-10-17 09:53:15','','logout','','','','',''),(495,'igor','2011-10-17 09:53:26','','login','','','','',''),(496,'igor','2011-10-17 09:53:41','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:24','Dispatch'),(497,'igor','2011-10-17 09:53:51','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 10:24','Dispatch'),(498,'igor','2011-10-17 09:53:57','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:25','Dispatch'),(499,'igor','2011-10-17 09:54:01','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 10:25','Dispatch'),(500,'igor','2011-10-17 09:54:24','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:20','Dispatch'),(501,'igor','2011-10-17 09:54:29','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:20','Dispatch'),(502,'igor','2011-10-17 14:24:23','','logout','','','','',''),(503,'igor','2011-10-17 14:24:48','','login','','','','',''),(504,'igor','2011-10-17 14:24:53','','insert','P102','','','','Patient'),(505,'igor','2011-10-17 14:24:53','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(506,'igor','2011-10-17 14:25:01','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(507,'igor','2011-10-17 14:25:02','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(508,'igor','2011-10-17 14:25:09','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(509,'igor','2011-10-17 14:25:38','IK Clinic','delete','P102','C026','','','Specimen'),(510,'igor','2011-10-17 14:25:38','IK Clinic','delete','P102','C027','','','Specimen'),(511,'igor','2011-10-17 14:25:38','IK Clinic','delete','P102','C028','','','Specimen'),(512,'igor','2011-10-17 14:25:38','IK Clinic','delete','P102','C029','','','Specimen'),(513,'igor','2011-10-17 14:25:38','IK Clinic','delete','P102','C030','','','Specimen'),(514,'igor','2011-10-17 14:25:38','IK Clinic','delete','P102','C021','','','Specimen'),(515,'igor','2011-10-17 14:25:38','IK Clinic','delete','P102','C022','','','Specimen'),(516,'igor','2011-10-17 14:25:38','IK Clinic','delete','P102','C023','','','Specimen'),(517,'igor','2011-10-17 14:25:38','IK Clinic','delete','P102','C024','','','Specimen'),(518,'igor','2011-10-17 14:25:39','IK Clinic','delete','P102','C025','','','Specimen'),(519,'igor','2011-10-17 14:25:39','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(520,'igor','2011-10-17 14:25:39','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(521,'igor','2011-10-17 14:25:43','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(522,'igor','2011-10-17 14:25:51','','delete','P102','','','','Patient'),(523,'igor','2011-10-17 14:25:53','','logout','','','','',''),(524,'igor','2011-10-17 14:28:12','','login','','','','',''),(525,'igor','2011-10-17 14:28:27','','insert','Pat11','','','','Patient'),(526,'igor','2011-10-17 14:28:28','IK Clinic','select','Pat11','','','Patient LOOKUP','Patient'),(527,'igor','2011-10-17 14:36:36','','delete','Pat11','','','','Patient'),(528,'igor','2011-10-17 14:41:19','','insert','Pat11','','','','Patient'),(529,'igor','2011-10-17 14:41:19','IK Clinic','select','Pat11','','','Patient LOOKUP','Patient'),(530,'igor','2011-10-17 14:45:01','','logout','','','','',''),(531,'igor','2011-10-17 14:45:09','','login','','','','',''),(532,'igor','2011-10-17 14:45:19','IK Clinic','select','Pat11','','','Patient LOOKUP','Patient'),(533,'igor','2011-10-17 14:45:24','','delete','Pat11','','','','Patient'),(534,'igor','2011-10-17 14:53:38','','insert','PPP','','','','Patient'),(535,'igor','2011-10-17 14:53:38','IK Clinic','select','PPP','','','Patient LOOKUP','Patient'),(536,'igor','2011-10-17 14:54:34','','insert','PPP','','','visit:1, specimens:2','CollectionEvent'),(537,'igor','2011-10-17 14:54:34','IK Clinic','select','PPP','','','CollectionEvent LOOKUP, visit:1, specimens:2','CollectionEvent'),(538,'igor','2011-10-17 14:54:35','IK Clinic','edit','PPP','','','CollectionEvent EDIT, visit:1, specimens:2','CollectionEvent'),(539,'igor','2011-10-17 15:21:43','','logout','','','','',''),(540,'igor','2011-10-17 15:21:52','','login','','','','',''),(541,'igor','2011-10-17 15:25:26','IK Clinic','select','PPP','','','Patient LOOKUP','Patient'),(542,'igor','2011-10-17 15:29:02','','logout','','','','',''),(543,'igor','2011-10-17 15:29:43','','login','','','','',''),(544,'igor','2011-10-17 15:29:53','IK Clinic','select','PPP','','','Patient LOOKUP','Patient'),(545,'igor','2011-10-17 15:42:13','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(546,'igor','2011-10-17 15:42:21','IK Clinic','select','PPP','','','Patient LOOKUP','Patient'),(547,'igor','2011-10-17 15:42:28','','logout','','','','',''),(548,'igor','2011-10-17 15:42:54','','login','','','','',''),(549,'igor','2011-10-17 15:46:55','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(550,'igor','2011-10-17 15:47:46','','logout','','','','',''),(551,'igor','2011-10-17 15:48:08','','login','','','','',''),(552,'igor','2011-10-18 09:16:34','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(553,'igor','2011-10-18 09:32:22','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(554,'igor','2011-10-18 09:32:24','IK Clinic','edit','P100','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(555,'igor','2011-10-18 09:33:26','','logout','','','','',''),(556,'igor','2011-10-18 09:33:49','','login','','','','',''),(557,'igor','2011-10-18 10:14:26','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(558,'igor','2011-10-18 10:14:42','','logout','','','','',''),(559,'igor','2011-10-18 10:14:51','','login','','','','',''),(560,'igor','2011-10-18 11:18:21','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(561,'igor','2011-10-18 11:18:36','','logout','','','','',''),(562,'igor','2011-10-18 11:18:53','','login','','','','',''),(563,'igor','2011-10-18 11:19:39','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(564,'igor','2011-10-18 11:28:59','','logout','','','','',''),(565,'igor','2011-10-18 11:29:20','','login','','','','',''),(566,'igor','2011-10-18 11:29:25','','insert','P102','','','','Patient'),(567,'igor','2011-10-18 11:29:25','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(568,'igor','2011-10-18 11:29:33','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(569,'igor','2011-10-18 11:29:34','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(570,'igor','2011-10-18 11:32:06','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(571,'igor','2011-10-18 11:32:32','IK Clinic','delete','P102','C021','','','Specimen'),(572,'igor','2011-10-18 11:32:32','IK Clinic','delete','P102','C030','','','Specimen'),(573,'igor','2011-10-18 11:32:32','IK Clinic','delete','P102','C028','','','Specimen'),(574,'igor','2011-10-18 11:32:32','IK Clinic','delete','P102','C029','','','Specimen'),(575,'igor','2011-10-18 11:32:32','IK Clinic','delete','P102','C026','','','Specimen'),(576,'igor','2011-10-18 11:32:33','IK Clinic','delete','P102','C027','','','Specimen'),(577,'igor','2011-10-18 11:32:33','IK Clinic','delete','P102','C024','','','Specimen'),(578,'igor','2011-10-18 11:32:33','IK Clinic','delete','P102','C025','','','Specimen'),(579,'igor','2011-10-18 11:32:33','IK Clinic','delete','P102','C022','','','Specimen'),(580,'igor','2011-10-18 11:32:33','IK Clinic','delete','P102','C023','','','Specimen'),(581,'igor','2011-10-18 11:32:33','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(582,'igor','2011-10-18 11:32:33','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(583,'igor','2011-10-18 11:32:38','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(584,'igor','2011-10-18 11:32:41','','delete','P102','','','','Patient'),(585,'igor','2011-10-18 11:32:43','','logout','','','','',''),(586,'igor','2011-10-18 11:33:27','','login','','','','',''),(587,'igor','2011-10-18 11:33:31','','insert','P102','','','','Patient'),(588,'igor','2011-10-18 11:33:31','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(589,'igor','2011-10-18 11:33:39','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(590,'igor','2011-10-18 11:33:40','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(591,'igor','2011-10-18 11:36:59','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(592,'igor','2011-10-18 11:37:26','IK Clinic','delete','P102','C030','','','Specimen'),(593,'igor','2011-10-18 11:37:26','IK Clinic','delete','P102','C028','','','Specimen'),(594,'igor','2011-10-18 11:37:26','IK Clinic','delete','P102','C029','','','Specimen'),(595,'igor','2011-10-18 11:37:26','IK Clinic','delete','P102','C026','','','Specimen'),(596,'igor','2011-10-18 11:37:26','IK Clinic','delete','P102','C027','','','Specimen'),(597,'igor','2011-10-18 11:37:26','IK Clinic','delete','P102','C024','','','Specimen'),(598,'igor','2011-10-18 11:37:26','IK Clinic','delete','P102','C025','','','Specimen'),(599,'igor','2011-10-18 11:37:27','IK Clinic','delete','P102','C023','','','Specimen'),(600,'igor','2011-10-18 11:37:27','IK Clinic','delete','P102','C022','','','Specimen'),(601,'igor','2011-10-18 11:37:27','IK Clinic','delete','P102','C021','','','Specimen'),(602,'igor','2011-10-18 11:37:27','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(603,'igor','2011-10-18 11:37:27','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(604,'igor','2011-10-18 11:37:30','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(605,'igor','2011-10-18 11:37:37','','delete','P102','','','','Patient'),(606,'igor','2011-10-18 11:37:39','','logout','','','','',''),(607,'igor','2011-10-18 11:37:58','','login','','','','',''),(608,'igor','2011-10-18 11:38:03','','insert','P102','','','','Patient'),(609,'igor','2011-10-18 11:38:03','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(610,'igor','2011-10-18 11:38:11','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(611,'igor','2011-10-18 11:38:12','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(612,'igor','2011-10-18 11:40:50','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(613,'igor','2011-10-18 11:41:15','IK Clinic','delete','P102','C025','','','Specimen'),(614,'igor','2011-10-18 11:41:15','IK Clinic','delete','P102','C024','','','Specimen'),(615,'igor','2011-10-18 11:41:15','IK Clinic','delete','P102','C023','','','Specimen'),(616,'igor','2011-10-18 11:41:15','IK Clinic','delete','P102','C022','','','Specimen'),(617,'igor','2011-10-18 11:41:15','IK Clinic','delete','P102','C021','','','Specimen'),(618,'igor','2011-10-18 11:41:15','IK Clinic','delete','P102','C030','','','Specimen'),(619,'igor','2011-10-18 11:41:15','IK Clinic','delete','P102','C029','','','Specimen'),(620,'igor','2011-10-18 11:41:15','IK Clinic','delete','P102','C028','','','Specimen'),(621,'igor','2011-10-18 11:41:16','IK Clinic','delete','P102','C027','','','Specimen'),(622,'igor','2011-10-18 11:41:16','IK Clinic','delete','P102','C026','','','Specimen'),(623,'igor','2011-10-18 11:41:16','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(624,'igor','2011-10-18 11:41:16','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(625,'igor','2011-10-18 11:41:19','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(626,'igor','2011-10-18 11:41:22','','delete','P102','','','','Patient'),(627,'igor','2011-10-18 11:41:23','','logout','','','','',''),(628,'igor','2011-10-18 11:41:38','','login','','','','',''),(629,'igor','2011-10-18 11:41:42','','insert','P102','','','','Patient'),(630,'igor','2011-10-18 11:41:43','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(631,'igor','2011-10-18 11:41:52','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(632,'igor','2011-10-18 11:41:52','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(633,'igor','2011-10-18 11:42:10','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(634,'igor','2011-10-18 11:42:37','IK Clinic','delete','P102','C030','','','Specimen'),(635,'igor','2011-10-18 11:42:37','IK Clinic','delete','P102','C029','','','Specimen'),(636,'igor','2011-10-18 11:42:37','IK Clinic','delete','P102','C028','','','Specimen'),(637,'igor','2011-10-18 11:42:37','IK Clinic','delete','P102','C024','','','Specimen'),(638,'igor','2011-10-18 11:42:38','IK Clinic','delete','P102','C025','','','Specimen'),(639,'igor','2011-10-18 11:42:38','IK Clinic','delete','P102','C026','','','Specimen'),(640,'igor','2011-10-18 11:42:38','IK Clinic','delete','P102','C027','','','Specimen'),(641,'igor','2011-10-18 11:42:38','IK Clinic','delete','P102','C021','','','Specimen'),(642,'igor','2011-10-18 11:42:38','IK Clinic','delete','P102','C022','','','Specimen'),(643,'igor','2011-10-18 11:42:38','IK Clinic','delete','P102','C023','','','Specimen'),(644,'igor','2011-10-18 11:42:38','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(645,'igor','2011-10-18 11:42:38','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(646,'igor','2011-10-18 11:42:43','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(647,'igor','2011-10-18 11:42:46','','delete','P102','','','','Patient'),(648,'igor','2011-10-18 11:45:10','','logout','','','','',''),(649,'igor','2011-10-18 11:45:32','','login','','','','',''),(650,'igor','2011-10-18 13:45:25','','insert','Pat100','','','','Patient'),(651,'igor','2011-10-18 13:45:25','IK Clinic','select','Pat100','','','Patient LOOKUP','Patient'),(652,'igor','2011-10-18 13:49:07','','insert','Pat100','','','visit:1, specimens:10','CollectionEvent'),(653,'igor','2011-10-18 13:49:07','IK Clinic','select','Pat100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(654,'igor','2011-10-18 15:00:26','IK Clinic','insert','','','','state: Creation','Dispatch'),(655,'igor','2011-10-18 15:00:27','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(656,'igor','2011-10-18 15:27:15','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-18 15:06','Dispatch'),(657,'igor','2011-10-18 15:27:15','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-18 15:06','Dispatch'),(658,'igor','2011-10-18 16:12:34','IK Clinic','insert','','','','state: Creation','Dispatch'),(659,'igor','2011-10-18 16:12:34','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(660,'igor','2011-10-18 16:12:58','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(661,'igor','2011-10-18 16:13:01','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(662,'igor','2011-10-18 16:13:04','IK Clinic','delete','','','','state: Creation','Dispatch'),(663,'igor','2011-10-18 16:41:25','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(664,'igor','2011-10-18 16:57:10','','logout','','','','',''),(665,'igor','2011-10-19 09:19:08','','login','','','','',''),(666,'igor','2011-10-19 09:56:33','','logout','','','','',''),(667,'igor','2011-10-19 10:02:34','','login','','','','',''),(668,'igor','2011-10-19 10:02:40','','logout','','','','',''),(669,'igor','2011-10-19 10:06:26','','login','','','','',''),(670,'igor','2011-10-19 10:07:44','','logout','','','','',''),(671,'igor','2011-10-19 10:07:56','','login','','','','',''),(672,'igor','2011-10-19 10:24:12','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-18 15:06','Dispatch'),(673,'igor','2011-10-19 10:27:09','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-18 15:06','Dispatch'),(674,'igor','2011-10-19 10:27:32','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-18 15:06','Dispatch'),(675,'igor','2011-10-19 10:29:20','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-18 15:06','Dispatch'),(676,'igor','2011-10-19 10:29:45','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-18 15:06','Dispatch'),(677,'igor','2011-10-19 11:03:49','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-18 15:06','Dispatch'),(678,'igor','2011-10-19 11:05:08','IK Site','select','Pat100','Event03','','Specimen LOOKUP','Specimen'),(679,'igor','2011-10-19 11:34:16','','logout','','','','',''),(680,'igor','2011-10-19 12:05:53','','login','','','','',''),(681,'igor','2011-10-19 12:05:59','','insert','P102','','','','Patient'),(682,'igor','2011-10-19 12:06:00','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(683,'igor','2011-10-19 12:06:09','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(684,'igor','2011-10-19 12:06:10','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(685,'igor','2011-10-19 12:06:27','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(686,'igor','2011-10-19 12:07:22','IK Clinic','delete','P102','C021','','','Specimen'),(687,'igor','2011-10-19 12:07:22','IK Clinic','delete','P102','C023','','','Specimen'),(688,'igor','2011-10-19 12:07:22','IK Clinic','delete','P102','C022','','','Specimen'),(689,'igor','2011-10-19 12:07:22','IK Clinic','delete','P102','C029','','','Specimen'),(690,'igor','2011-10-19 12:07:22','IK Clinic','delete','P102','C028','','','Specimen'),(691,'igor','2011-10-19 12:07:22','IK Clinic','delete','P102','C030','','','Specimen'),(692,'igor','2011-10-19 12:07:22','IK Clinic','delete','P102','C025','','','Specimen'),(693,'igor','2011-10-19 12:07:22','IK Clinic','delete','P102','C024','','','Specimen'),(694,'igor','2011-10-19 12:07:22','IK Clinic','delete','P102','C027','','','Specimen'),(695,'igor','2011-10-19 12:07:22','IK Clinic','delete','P102','C026','','','Specimen'),(696,'igor','2011-10-19 12:07:23','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(697,'igor','2011-10-19 12:07:23','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(698,'igor','2011-10-19 12:07:26','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(699,'igor','2011-10-19 12:07:29','','delete','P102','','','','Patient'),(700,'igor','2011-10-19 12:07:31','','logout','','','','',''),(701,'igor','2011-10-19 12:07:49','','login','','','','',''),(702,'igor','2011-10-19 12:07:54','','insert','P102','','','','Patient'),(703,'igor','2011-10-19 12:07:54','IK Clinic','select','P102','','','Patient LOOKUP','Patient'),(704,'igor','2011-10-19 12:08:03','','insert','P102','','','visit:1, specimens:10','CollectionEvent'),(705,'igor','2011-10-19 12:08:03','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(706,'igor','2011-10-19 12:08:29','IK Clinic','edit','P102','C030','','Specimen EDIT','Specimen'),(707,'igor','2011-10-19 12:08:33','IK Clinic','select','P102','C030','','Specimen LOOKUP','Specimen'),(708,'igor','2011-10-19 12:08:39','IK Clinic','edit','P102','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(709,'igor','2011-10-19 12:09:01','IK Clinic','delete','P102','C030','','','Specimen'),(710,'igor','2011-10-19 12:09:01','IK Clinic','delete','P102','C027','','','Specimen'),(711,'igor','2011-10-19 12:09:01','IK Clinic','delete','P102','C026','','','Specimen'),(712,'igor','2011-10-19 12:09:01','IK Clinic','delete','P102','C029','','','Specimen'),(713,'igor','2011-10-19 12:09:01','IK Clinic','delete','P102','C028','','','Specimen'),(714,'igor','2011-10-19 12:09:01','IK Clinic','delete','P102','C021','','','Specimen'),(715,'igor','2011-10-19 12:09:01','IK Clinic','delete','P102','C024','','','Specimen'),(716,'igor','2011-10-19 12:09:01','IK Clinic','delete','P102','C025','','','Specimen'),(717,'igor','2011-10-19 12:09:01','IK Clinic','delete','P102','C022','','','Specimen'),(718,'igor','2011-10-19 12:09:02','IK Clinic','delete','P102','C023','','','Specimen'),(719,'igor','2011-10-19 12:09:02','','update','P102','','','visit:1, specimens:0','CollectionEvent'),(720,'igor','2011-10-19 12:09:02','IK Clinic','select','P102','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(721,'igor','2011-10-19 12:09:05','','delete','P102','','','visit:1, specimens:0','CollectionEvent'),(722,'igor','2011-10-19 12:09:09','','delete','P102','','','','Patient'),(723,'igor','2011-10-19 12:09:20','IK Clinic','select','Pat100','','','Patient LOOKUP','Patient'),(724,'igor','2011-10-19 12:09:25','IK Clinic','select','Pat100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(725,'igor','2011-10-19 12:48:40','IK Clinic','insert','','','','state: Creation','Dispatch'),(726,'igor','2011-10-19 12:48:40','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(727,'igor','2011-10-19 12:48:57','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-19 12:48','Dispatch'),(728,'igor','2011-10-19 12:48:57','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-19 12:48','Dispatch'),(729,'igor','2011-10-19 12:49:36','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(730,'igor','2011-10-19 12:49:59','','logout','','','','',''),(731,'igor','2011-10-19 12:50:06','','login','','','','',''),(732,'igor','2011-10-19 13:02:02','','logout','','','','',''),(733,'igor','2011-10-19 13:02:22','','login','','','','',''),(734,'igor','2011-10-19 13:04:11','','logout','','','','',''),(735,'igor','2011-10-19 13:04:18','','login','','','','',''),(736,'igor','2011-10-19 13:05:14','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-18 15:06','Dispatch'),(737,'igor','2011-10-19 13:05:16','IK Site','update','','','','state: Received, received at: 2011-10-19 13:05','Dispatch'),(738,'igor','2011-10-19 13:05:16','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-19 13:05','Dispatch'),(739,'igor','2011-10-19 13:05:31','IK Site','update','Pat100','Event01','','','Specimen'),(740,'igor','2011-10-19 13:05:31','IK Site','update','','','','state: Received, received at: 2011-10-19 13:05','Dispatch'),(741,'igor','2011-10-19 13:05:32','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-19 13:05','Dispatch'),(742,'igor','2011-10-19 13:05:40','IK Site','update','','','','state: Closed, received at: 2011-10-19 13:05','Dispatch'),(743,'igor','2011-10-19 13:05:40','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-19 13:05','Dispatch'),(744,'igor','2011-10-19 13:10:41','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-19 12:48','Dispatch'),(745,'igor','2011-10-19 13:10:45','IK Site','update','','','','state: Received, received at: 2011-10-19 13:10','Dispatch'),(746,'igor','2011-10-19 13:10:46','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-19 13:10','Dispatch'),(747,'igor','2011-10-19 13:11:04','IK Site','update','Pat100','Event04','','','Specimen'),(748,'igor','2011-10-19 13:11:04','IK Site','update','','','','state: Received, received at: 2011-10-19 13:10','Dispatch'),(749,'igor','2011-10-19 13:11:05','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-19 13:10','Dispatch'),(750,'igor','2011-10-19 13:11:08','IK Site','update','','','','state: Closed, received at: 2011-10-19 13:10','Dispatch'),(751,'igor','2011-10-19 13:11:08','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-19 13:10','Dispatch'),(752,'igor','2011-10-19 13:12:07','IK Clinic','update','Pat100','Event05','','','Specimen'),(753,'igor','2011-10-19 13:12:07','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(754,'igor','2011-10-19 13:12:12','','logout','','','','',''),(755,'igor','2011-10-19 13:13:23','','login','','','','',''),(756,'igor','2011-10-19 13:13:56','IK Clinic','insert','','','','state: Creation','Dispatch'),(757,'igor','2011-10-19 13:13:57','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(758,'igor','2011-10-19 13:13:59','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-19 13:13','Dispatch'),(759,'igor','2011-10-19 13:13:59','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-19 13:13','Dispatch'),(760,'igor','2011-10-19 13:14:02','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(761,'igor','2011-10-19 13:14:05','','logout','','','','',''),(762,'igor','2011-10-19 13:14:05','','login','','','','',''),(763,'igor','2011-10-19 13:20:20','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-19 13:13','Dispatch'),(764,'igor','2011-10-19 13:20:26','IK Site','update','','','','state: Received, received at: 2011-10-19 13:20','Dispatch'),(765,'igor','2011-10-19 13:20:26','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-19 13:20','Dispatch'),(766,'igor','2011-10-19 13:21:50','IK Site','update','Pat100','Event05','','','Specimen'),(767,'igor','2011-10-19 13:21:50','IK Site','update','','','','state: Received, received at: 2011-10-19 13:20','Dispatch'),(768,'igor','2011-10-19 13:21:51','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-19 13:20','Dispatch'),(769,'igor','2011-10-19 13:21:57','IK Site','update','','','','state: Closed, received at: 2011-10-19 13:20','Dispatch'),(770,'igor','2011-10-19 13:21:57','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-19 13:20','Dispatch'),(771,'igor','2011-10-19 13:22:08','IK Clinic','update','Pat100','Event06','','','Specimen'),(772,'igor','2011-10-19 13:22:08','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(773,'igor','2011-10-19 15:24:36','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:24','Dispatch'),(774,'igor','2011-10-19 15:24:40','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 10:24','Dispatch'),(775,'igor','2011-10-19 15:24:51','','logout','','','','',''),(776,'igor','2011-10-19 15:25:00','','login','','','','',''),(777,'igor','2011-10-19 15:26:26','','logout','','','','',''),(778,'igor','2011-10-19 15:26:54','','login','','','','',''),(779,'igor','2011-10-19 15:27:10','IK Clinic','insert','','','','state: Creation','Dispatch'),(780,'igor','2011-10-19 15:27:11','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(781,'igor','2011-10-19 15:27:13','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-19 15:27','Dispatch'),(782,'igor','2011-10-19 15:27:13','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-19 15:27','Dispatch'),(783,'igor','2011-10-19 15:27:16','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(784,'igor','2011-10-19 15:27:19','','logout','','','','',''),(785,'igor','2011-10-19 15:27:19','','login','','','','',''),(786,'igor','2011-10-19 16:29:46','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-19 15:27','Dispatch'),(787,'igor','2011-10-19 16:30:12','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-19 15:27','Dispatch'),(788,'igor','2011-10-20 10:30:51','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-19 15:27','Dispatch'),(789,'igor','2011-10-20 10:30:59','','logout','','','','',''),(790,'igor','2011-10-20 10:31:05','','login','','','','',''),(791,'igor','2011-10-20 10:31:37','IK Clinic','insert','','','','state: Creation','Dispatch'),(792,'igor','2011-10-20 10:31:37','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(793,'igor','2011-10-20 10:32:03','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(794,'igor','2011-10-20 10:32:03','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(795,'igor','2011-10-20 10:32:15','','logout','','','','',''),(796,'igor','2011-10-20 10:32:39','','login','','','','',''),(797,'igor','2011-10-20 10:34:01','','logout','','','','',''),(798,'igor','2011-10-20 10:34:19','','login','','','','',''),(799,'igor','2011-10-20 10:34:33','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(800,'igor','2011-10-20 10:43:04','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(801,'igor','2011-10-20 10:46:38','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(802,'igor','2011-10-20 10:50:17','','logout','','','','',''),(803,'igor','2011-10-20 10:50:23','','login','','','','',''),(804,'igor','2011-10-20 10:50:44','IK Clinic','insert','','','','state: Creation','Dispatch'),(805,'igor','2011-10-20 10:50:44','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(806,'igor','2011-10-20 10:53:31','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(807,'igor','2011-10-20 10:53:31','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(808,'igor','2011-10-20 10:53:35','','logout','','','','',''),(809,'igor','2011-10-20 10:53:43','','login','','','','',''),(810,'igor','2011-10-20 10:54:15','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(811,'igor','2011-10-20 10:58:38','IK Site','update','','','','state: Received, received at: 2011-10-20 10:58','Dispatch'),(812,'igor','2011-10-20 10:58:38','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 10:58','Dispatch'),(813,'igor','2011-10-20 10:59:43','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 10:58','Dispatch'),(814,'igor','2011-10-20 11:02:08','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-19 15:27','Dispatch'),(815,'igor','2011-10-20 11:03:33','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(816,'igor','2011-10-20 11:06:06','IK Site','update','','','','state: Received, received at: 2011-10-20 11:06','Dispatch'),(817,'igor','2011-10-20 11:06:06','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 11:06','Dispatch'),(818,'igor','2011-10-20 11:15:47','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 11:06','Dispatch'),(819,'igor','2011-10-20 11:15:56','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:24','Dispatch'),(820,'igor','2011-10-20 11:16:11','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 11:06','Dispatch'),(821,'igor','2011-10-20 11:26:38','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 11:06','Dispatch'),(822,'igor','2011-10-20 11:26:41','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 11:06','Dispatch'),(823,'igor','2011-10-20 11:33:03','','logout','','','','',''),(824,'igor','2011-10-20 11:33:10','','login','','','','',''),(825,'igor','2011-10-20 11:34:07','','logout','','','','',''),(826,'igor','2011-10-20 11:34:27','','login','','','','',''),(827,'igor','2011-10-20 11:34:42','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 11:06','Dispatch'),(828,'igor','2011-10-20 11:34:44','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 11:06','Dispatch'),(829,'igor','2011-10-20 11:35:05','','logout','','','','',''),(830,'igor','2011-10-20 11:35:14','','login','','','','',''),(831,'igor','2011-10-20 11:37:01','','logout','','','','',''),(832,'igor','2011-10-20 11:37:09','','login','','','','',''),(833,'igor','2011-10-20 11:37:26','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(834,'igor','2011-10-20 11:37:31','IK Site','update','','','','state: Received, received at: 2011-10-20 11:37','Dispatch'),(835,'igor','2011-10-20 11:37:32','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 11:37','Dispatch'),(836,'igor','2011-10-20 11:37:49','IK Site','update','Pat100','Event07','','','Specimen'),(837,'igor','2011-10-20 11:37:49','IK Site','update','Pat100','Event08','','','Specimen'),(838,'igor','2011-10-20 11:37:49','IK Site','update','','','','state: Received, received at: 2011-10-20 11:37','Dispatch'),(839,'igor','2011-10-20 11:37:49','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 11:37','Dispatch'),(840,'igor','2011-10-20 11:37:51','IK Site','update','','','','state: Closed, received at: 2011-10-20 11:37','Dispatch'),(841,'igor','2011-10-20 11:37:51','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 11:37','Dispatch'),(842,'igor','2011-10-20 11:56:53','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:24','Dispatch'),(843,'igor','2011-10-20 11:56:56','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 10:24','Dispatch'),(844,'igor','2011-10-20 11:57:16','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:24','Dispatch'),(845,'igor','2011-10-20 11:57:24','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-14 10:24','Dispatch'),(846,'igor','2011-10-20 11:58:30','IK Site','update','P101','C011','','','Specimen'),(847,'igor','2011-10-20 11:58:30','IK Site','update','','','','state: Received, received at: 2011-10-14 10:24','Dispatch'),(848,'igor','2011-10-20 11:58:30','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:24','Dispatch'),(849,'igor','2011-10-20 11:58:32','IK Site','update','','','','state: Closed, received at: 2011-10-14 10:24','Dispatch'),(850,'igor','2011-10-20 11:58:33','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-14 10:24','Dispatch'),(851,'igor','2011-10-20 11:58:55','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 10:25','Dispatch'),(852,'igor','2011-10-20 11:59:06','IK Site','update','','','','state: Closed, received at: 2011-10-14 10:25','Dispatch'),(853,'igor','2011-10-20 11:59:06','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-14 10:25','Dispatch'),(854,'igor','2011-10-20 12:01:55','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:20','Dispatch'),(855,'igor','2011-10-20 12:01:59','IK Site','update','','','','state: Closed, received at: 2011-10-17 09:20','Dispatch'),(856,'igor','2011-10-20 12:02:00','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-17 09:20','Dispatch'),(857,'igor','2011-10-20 12:05:14','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:20','Dispatch'),(858,'igor','2011-10-20 12:05:17','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-17 09:20','Dispatch'),(859,'igor','2011-10-20 12:05:19','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:20','Dispatch'),(860,'igor','2011-10-20 12:05:22','IK Site','update','','','','state: Closed, received at: 2011-10-17 09:20','Dispatch'),(861,'igor','2011-10-20 12:05:23','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-17 09:20','Dispatch'),(862,'igor','2011-10-20 12:07:04','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 11:06','Dispatch'),(863,'igor','2011-10-20 12:07:09','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 11:06','Dispatch'),(864,'igor','2011-10-20 12:07:52','IK Site','update','Pat100','Event06','','','Specimen'),(865,'igor','2011-10-20 12:07:52','IK Site','update','','','','state: Received, received at: 2011-10-20 11:06','Dispatch'),(866,'igor','2011-10-20 12:07:52','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 11:06','Dispatch'),(867,'igor','2011-10-20 12:11:22','IK Site','update','','','','state: Closed, received at: 2011-10-20 11:06','Dispatch'),(868,'igor','2011-10-20 12:11:22','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 11:06','Dispatch'),(869,'igor','2011-10-20 12:12:06','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 10:58','Dispatch'),(870,'igor','2011-10-20 12:12:13','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 10:58','Dispatch'),(871,'igor','2011-10-20 12:12:47','IK Site','update','Pat100','Event07','','','Specimen'),(872,'igor','2011-10-20 12:12:47','IK Site','update','','','','state: Received, received at: 2011-10-20 10:58','Dispatch'),(873,'igor','2011-10-20 12:12:47','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 10:58','Dispatch'),(874,'igor','2011-10-20 14:05:57','IK Site','update','','','','state: Closed, received at: 2011-10-20 10:58','Dispatch'),(875,'igor','2011-10-20 14:05:57','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 10:58','Dispatch'),(876,'igor','2011-10-20 14:06:34','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-14 09:35','Dispatch'),(877,'igor','2011-10-20 14:07:46','IK Site','update','','','','state: Closed, received at: 2011-10-14 09:35','Dispatch'),(878,'igor','2011-10-20 14:07:46','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-14 09:35','Dispatch'),(879,'igor','2011-10-20 14:08:18','','logout','','','','',''),(880,'igor','2011-10-20 14:08:26','','login','','','','',''),(881,'igor','2011-10-20 14:08:34','','logout','','','','',''),(882,'igor','2011-10-20 14:08:52','','login','','','','',''),(883,'igor','2011-10-20 14:10:00','IK Clinic','select','Pat100','','','Patient LOOKUP','Patient'),(884,'igor','2011-10-20 14:10:03','IK Clinic','select','Pat100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(885,'igor','2011-10-20 14:10:13','IK Clinic','edit','Pat100','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(886,'igor','2011-10-20 14:12:30','','update','Pat100','','','visit:1, specimens:20','CollectionEvent'),(887,'igor','2011-10-20 14:12:31','IK Clinic','select','Pat100','','','CollectionEvent LOOKUP, visit:1, specimens:20','CollectionEvent'),(888,'igor','2011-10-20 14:13:47','IK Clinic','insert','','','','state: Creation','Dispatch'),(889,'igor','2011-10-20 14:13:47','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(890,'igor','2011-10-20 14:14:03','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(891,'igor','2011-10-20 14:14:03','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(892,'igor','2011-10-20 14:14:53','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(893,'igor','2011-10-20 14:15:03','','logout','','','','',''),(894,'igor','2011-10-20 14:15:10','','login','','','','',''),(895,'igor','2011-10-20 14:15:55','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(896,'igor','2011-10-20 14:16:52','IK Site','update','','','','state: Received, received at: 2011-10-20 14:16','Dispatch'),(897,'igor','2011-10-20 14:16:52','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 14:16','Dispatch'),(898,'igor','2011-10-20 14:19:14','IK Site','update','Pat100','Event09','','','Specimen'),(899,'igor','2011-10-20 14:19:14','IK Site','update','','','','state: Received, received at: 2011-10-20 14:16','Dispatch'),(900,'igor','2011-10-20 14:19:14','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 14:16','Dispatch'),(901,'igor','2011-10-20 14:19:59','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 14:16','Dispatch'),(902,'igor','2011-10-20 14:23:56','IK Site','update','','','','state: Received, received at: 2011-10-20 14:16','Dispatch'),(903,'igor','2011-10-20 14:23:57','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 14:16','Dispatch'),(904,'igor','2011-10-20 14:24:18','IK Site','update','','','','state: Closed, received at: 2011-10-20 14:16','Dispatch'),(905,'igor','2011-10-20 14:24:18','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 14:16','Dispatch'),(906,'igor','2011-10-20 14:24:59','','logout','','','','',''),(907,'igor','2011-10-20 14:25:18','','login','','','','',''),(908,'igor','2011-10-20 14:25:53','IK Clinic','insert','','','','state: Creation','Dispatch'),(909,'igor','2011-10-20 14:25:54','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(910,'igor','2011-10-20 14:26:11','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(911,'igor','2011-10-20 14:26:11','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(912,'igor','2011-10-20 14:26:19','','logout','','','','',''),(913,'igor','2011-10-20 14:26:25','','login','','','','',''),(914,'igor','2011-10-20 14:26:54','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(915,'igor','2011-10-20 14:27:03','IK Site','update','','','','state: Received, received at: 2011-10-20 14:27','Dispatch'),(916,'igor','2011-10-20 14:27:04','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 14:27','Dispatch'),(917,'igor','2011-10-20 14:28:01','IK Site','update','Pat100','Event10','','','Specimen'),(918,'igor','2011-10-20 14:28:01','IK Site','update','','','','state: Received, received at: 2011-10-20 14:27','Dispatch'),(919,'igor','2011-10-20 14:28:01','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 14:27','Dispatch'),(920,'igor','2011-10-20 14:28:49','IK Site','update','','','','state: Closed, received at: 2011-10-20 14:27','Dispatch'),(921,'igor','2011-10-20 14:28:49','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 14:27','Dispatch'),(922,'igor','2011-10-20 14:29:27','','logout','','','','',''),(923,'igor','2011-10-20 14:29:41','','login','','','','',''),(924,'igor','2011-10-20 14:30:03','IK Clinic','insert','','','','state: Creation','Dispatch'),(925,'igor','2011-10-20 14:30:03','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(926,'igor','2011-10-20 14:30:15','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(927,'igor','2011-10-20 14:30:15','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(928,'igor','2011-10-20 14:30:25','IK Clinic','edit','','','','Dispatch EDIT, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(929,'igor','2011-10-20 14:30:34','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(930,'igor','2011-10-20 14:30:35','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(931,'igor','2011-10-20 14:30:41','','logout','','','','',''),(932,'igor','2011-10-20 14:30:46','','login','','','','',''),(933,'igor','2011-10-20 14:31:18','','logout','','','','',''),(934,'igor','2011-10-20 14:31:33','','login','','','','',''),(935,'igor','2011-10-20 14:31:42','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(936,'igor','2011-10-20 14:31:42','IK Site','update','','','','state: Received, received at: 2011-10-20 14:31','Dispatch'),(937,'igor','2011-10-20 14:31:42','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 14:31','Dispatch'),(938,'igor','2011-10-20 14:32:15','IK Site','update','Pat100','Event11','','','Specimen'),(939,'igor','2011-10-20 14:32:15','IK Site','update','','','','state: Received, received at: 2011-10-20 14:31','Dispatch'),(940,'igor','2011-10-20 14:32:15','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 14:31','Dispatch'),(941,'igor','2011-10-20 14:35:58','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 14:31','Dispatch'),(942,'igor','2011-10-20 14:36:16','IK Site','update','','','','state: Received, received at: 2011-10-20 14:31','Dispatch'),(943,'igor','2011-10-20 14:36:16','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 14:31','Dispatch'),(944,'igor','2011-10-20 14:37:04','IK Site','update','','','','state: Closed, received at: 2011-10-20 14:31','Dispatch'),(945,'igor','2011-10-20 14:37:04','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 14:31','Dispatch'),(946,'igor','2011-10-20 14:37:15','IK Site','edit','','','','Dispatch EDIT, state: Closed, received at: 2011-10-20 14:31','Dispatch'),(947,'igor','2011-10-20 14:38:30','IK Site','update','','','','state: Closed, received at: 2011-10-20 14:31','Dispatch'),(948,'igor','2011-10-20 14:38:30','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 14:31','Dispatch'),(949,'igor','2011-10-20 14:43:37','IK Site','update','Pat100','Event10','','','Specimen'),(950,'igor','2011-10-20 14:43:37','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(951,'igor','2011-10-20 14:43:40','','logout','','','','',''),(952,'igor','2011-10-20 14:43:48','','login','','','','',''),(953,'igor','2011-10-20 14:44:58','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(954,'igor','2011-10-20 14:45:03','','logout','','','','',''),(955,'igor','2011-10-20 14:45:09','','login','','','','',''),(956,'igor','2011-10-20 14:51:05','','logout','','','','',''),(957,'igor','2011-10-20 14:51:18','','login','','','','',''),(958,'igor','2011-10-20 14:54:21','IK Clinic','update','','','','waybill:null, specimens:1','Shipment'),(959,'igor','2011-10-20 15:04:30','','logout','','','','',''),(960,'igor','2011-10-20 15:04:43','','login','','','','',''),(961,'igor','2011-10-20 15:05:21','IK Site','update','Pat100','Event09','','','Specimen'),(962,'igor','2011-10-20 15:05:21','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(963,'igor','2011-10-20 15:05:44','IK Clinic','insert','','','','state: Creation','Dispatch'),(964,'igor','2011-10-20 15:05:45','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(965,'igor','2011-10-20 15:13:13','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(966,'igor','2011-10-20 15:15:15','IK Clinic','update','Pat100','Event13','','','Specimen'),(967,'igor','2011-10-20 15:15:15','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(968,'igor','2011-10-20 15:18:29','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(969,'igor','2011-10-20 15:19:04','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 15:18','Dispatch'),(970,'igor','2011-10-20 15:19:05','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 15:18','Dispatch'),(971,'igor','2011-10-20 15:19:08','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(972,'igor','2011-10-20 15:19:14','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 15:19','Dispatch'),(973,'igor','2011-10-20 15:19:14','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 15:19','Dispatch'),(974,'igor','2011-10-20 15:19:22','IK Clinic','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:42','Dispatch'),(975,'igor','2011-10-20 15:19:30','IK Clinic','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-17 09:42','Dispatch'),(976,'igor','2011-10-20 15:19:52','IK Clinic','update','P01','002','','','Specimen'),(977,'igor','2011-10-20 15:19:52','IK Clinic','update','','','','state: Received, received at: 2011-10-17 09:42','Dispatch'),(978,'igor','2011-10-20 15:19:53','IK Clinic','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-17 09:42','Dispatch'),(979,'igor','2011-10-20 15:19:54','IK Clinic','update','','','','state: Closed, received at: 2011-10-17 09:42','Dispatch'),(980,'igor','2011-10-20 15:19:54','IK Clinic','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-17 09:42','Dispatch'),(981,'igor','2011-10-20 15:20:03','','logout','','','','',''),(982,'igor','2011-10-20 15:20:12','','login','','','','',''),(983,'igor','2011-10-20 15:20:23','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 15:18','Dispatch'),(984,'igor','2011-10-20 15:20:25','IK Site','update','','','','state: Received, received at: 2011-10-20 15:20','Dispatch'),(985,'igor','2011-10-20 15:20:25','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 15:20','Dispatch'),(986,'igor','2011-10-20 15:20:36','IK Site','update','P01','001','','','Specimen'),(987,'igor','2011-10-20 15:20:36','IK Site','update','','','','state: Received, received at: 2011-10-20 15:20','Dispatch'),(988,'igor','2011-10-20 15:20:36','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 15:20','Dispatch'),(989,'igor','2011-10-20 15:20:37','IK Site','update','','','','state: Closed, received at: 2011-10-20 15:20','Dispatch'),(990,'igor','2011-10-20 15:20:37','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 15:20','Dispatch'),(991,'igor','2011-10-20 15:20:42','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 15:19','Dispatch'),(992,'igor','2011-10-20 15:20:52','IK Site','update','','','','state: Received, received at: 2011-10-20 15:20','Dispatch'),(993,'igor','2011-10-20 15:20:52','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 15:20','Dispatch'),(994,'igor','2011-10-20 15:21:00','IK Site','update','Pat100','Event12','','','Specimen'),(995,'igor','2011-10-20 15:21:00','IK Site','update','','','','state: Received, received at: 2011-10-20 15:20','Dispatch'),(996,'igor','2011-10-20 15:21:01','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 15:20','Dispatch'),(997,'igor','2011-10-20 15:21:02','IK Site','update','','','','state: Closed, received at: 2011-10-20 15:20','Dispatch'),(998,'igor','2011-10-20 15:21:02','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 15:20','Dispatch'),(999,'igor','2011-10-20 15:21:13','','logout','','','','',''),(1000,'igor','2011-10-20 15:50:37','','login','','','','',''),(1001,'igor','2011-10-20 15:51:46','IK Clinic','insert','','','','state: Creation','Dispatch'),(1002,'igor','2011-10-20 15:51:46','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1003,'igor','2011-10-20 15:51:56','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1004,'igor','2011-10-20 15:51:56','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1005,'igor','2011-10-20 15:52:04','','logout','','','','',''),(1006,'igor','2011-10-20 15:52:12','','login','','','','',''),(1007,'igor','2011-10-20 15:57:52','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1008,'igor','2011-10-20 16:11:53','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1009,'igor','2011-10-20 16:12:35','IK Clinic','update','Pat100','Event19','','','Specimen'),(1010,'igor','2011-10-20 16:12:35','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1011,'igor','2011-10-20 16:16:19','IK Site','update','','','','state: Received, received at: 2011-10-20 16:16','Dispatch'),(1012,'igor','2011-10-20 16:16:19','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 16:16','Dispatch'),(1013,'igor','2011-10-20 16:16:40','IK Site','update','Pat100','Event20','','','Specimen'),(1014,'igor','2011-10-20 16:16:40','IK Site','update','','','','state: Received, received at: 2011-10-20 16:16','Dispatch'),(1015,'igor','2011-10-20 16:16:40','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 16:16','Dispatch'),(1016,'igor','2011-10-20 16:22:45','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 16:16','Dispatch'),(1017,'igor','2011-10-20 16:24:59','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 16:16','Dispatch'),(1018,'igor','2011-10-20 16:29:01','IK Site','update','','','','state: Closed, received at: 2011-10-20 16:16','Dispatch'),(1019,'igor','2011-10-20 16:29:01','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 16:16','Dispatch'),(1020,'igor','2011-10-20 16:31:17','','logout','','','','',''),(1021,'igor','2011-10-20 16:31:59','','login','','','','',''),(1022,'igor','2011-10-20 16:32:49','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1023,'igor','2011-10-20 16:33:30','','logout','','','','',''),(1024,'igor','2011-10-20 16:33:43','','login','','','','',''),(1025,'igor','2011-10-20 16:35:49','IK Clinic','update','','','','waybill:null, specimens:1','Shipment'),(1026,'igor','2011-10-20 16:37:25','','logout','','','','',''),(1027,'igor','2011-10-20 16:37:38','','login','','','','',''),(1028,'igor','2011-10-20 16:37:48','IK Clinic','update','Pat100','Event19','','','Specimen'),(1029,'igor','2011-10-20 16:37:48','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1030,'igor','2011-10-20 16:44:28','','logout','','','','',''),(1031,'igor','2011-10-20 16:44:41','','login','','','','',''),(1032,'igor','2011-10-20 16:44:54','IK Clinic','insert','','','','state: Creation','Dispatch'),(1033,'igor','2011-10-20 16:44:55','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1034,'igor','2011-10-20 16:46:44','IK Clinic','delete','','','','state: Creation','Dispatch'),(1035,'igor','2011-10-20 16:47:47','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(1036,'igor','2011-10-20 16:47:53','IK Clinic','edit','P100','','','Patient EDIT','Patient'),(1037,'igor','2011-10-20 16:47:56','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(1038,'igor','2011-10-20 16:47:57','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(1039,'igor','2011-10-20 16:48:08','IK Clinic','edit','P100','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(1040,'igor','2011-10-20 16:50:01','','update','P100','','','visit:1, specimens:15','CollectionEvent'),(1041,'igor','2011-10-20 16:50:01','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:15','CollectionEvent'),(1042,'igor','2011-10-20 16:50:23','IK Clinic','insert','','','','state: Creation','Dispatch'),(1043,'igor','2011-10-20 16:50:23','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1044,'igor','2011-10-20 16:50:41','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1045,'igor','2011-10-20 16:50:41','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1046,'igor','2011-10-20 16:50:52','','logout','','','','',''),(1047,'igor','2011-10-20 16:51:06','','login','','','','',''),(1048,'igor','2011-10-20 16:51:18','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1049,'igor','2011-10-20 16:51:21','IK Site','update','','','','state: Received, received at: 2011-10-20 16:51','Dispatch'),(1050,'igor','2011-10-20 16:51:21','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 16:51','Dispatch'),(1051,'igor','2011-10-20 16:51:34','IK Site','update','P100','E01','','','Specimen'),(1052,'igor','2011-10-20 16:51:35','IK Site','update','','','','state: Received, received at: 2011-10-20 16:51','Dispatch'),(1053,'igor','2011-10-20 16:51:35','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 16:51','Dispatch'),(1054,'igor','2011-10-20 16:57:57','IK Site','update','','','','state: Closed, received at: 2011-10-20 16:51','Dispatch'),(1055,'igor','2011-10-20 16:57:57','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 16:51','Dispatch'),(1056,'igor','2011-10-20 16:58:00','','logout','','','','',''),(1057,'igor','2011-10-20 16:58:16','','login','','','','',''),(1058,'igor','2011-10-20 16:58:56','IK Clinic','insert','','','','state: Creation','Dispatch'),(1059,'igor','2011-10-20 16:58:56','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1060,'igor','2011-10-20 17:04:24','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 17:03','Dispatch'),(1061,'igor','2011-10-20 17:04:24','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 17:03','Dispatch'),(1062,'igor','2011-10-20 17:05:08','IK Clinic','update','','','','state: Creation','Dispatch'),(1063,'igor','2011-10-20 17:05:13','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1064,'igor','2011-10-20 17:06:07','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1065,'igor','2011-10-20 17:06:07','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1066,'igor','2011-10-20 17:06:18','IK Clinic','update','','','','state: Creation','Dispatch'),(1067,'igor','2011-10-20 17:06:21','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1068,'igor','2011-10-20 17:11:09','IK Clinic','delete','','','','state: Creation','Dispatch'),(1069,'igor','2011-10-20 17:11:48','IK Clinic','insert','','','','state: Creation','Dispatch'),(1070,'igor','2011-10-20 17:11:48','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1071,'igor','2011-10-20 17:11:51','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1072,'igor','2011-10-20 17:11:51','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1073,'igor','2011-10-20 17:11:54','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1074,'igor','2011-10-20 17:11:57','','logout','','','','',''),(1075,'igor','2011-10-20 17:11:57','','login','','','','',''),(1076,'igor','2011-10-20 17:13:11','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1077,'igor','2011-10-20 17:14:52','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1078,'igor','2011-10-20 17:16:03','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1079,'igor','2011-10-20 17:16:22','IK Site','update','','','','state: Received, received at: 2011-10-20 17:16','Dispatch'),(1080,'igor','2011-10-20 17:16:22','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 17:16','Dispatch'),(1081,'igor','2011-10-20 17:16:39','IK Site','update','P100','E02','','','Specimen'),(1082,'igor','2011-10-20 17:16:39','IK Site','update','','','','state: Received, received at: 2011-10-20 17:16','Dispatch'),(1083,'igor','2011-10-20 17:16:39','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 17:16','Dispatch'),(1084,'igor','2011-10-20 17:16:40','IK Site','update','','','','state: Closed, received at: 2011-10-20 17:16','Dispatch'),(1085,'igor','2011-10-20 17:16:41','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 17:16','Dispatch'),(1086,'igor','2011-10-20 17:16:51','IK Clinic','update','P100','E03','','','Specimen'),(1087,'igor','2011-10-20 17:16:51','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1088,'igor','2011-10-20 17:16:53','','logout','','','','',''),(1089,'igor','2011-10-20 17:17:45','','login','','','','',''),(1090,'igor','2011-10-20 17:18:02','IK Clinic','insert','','','','state: Creation','Dispatch'),(1091,'igor','2011-10-20 17:18:02','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1092,'igor','2011-10-20 17:18:05','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1093,'igor','2011-10-20 17:18:05','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1094,'igor','2011-10-20 17:18:08','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1095,'igor','2011-10-20 17:18:10','','logout','','','','',''),(1096,'igor','2011-10-20 17:18:11','','login','','','','',''),(1097,'igor','2011-10-20 17:18:15','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1098,'igor','2011-10-20 17:18:16','IK Site','update','','','','state: Received, received at: 2011-10-20 17:18','Dispatch'),(1099,'igor','2011-10-20 17:18:16','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 17:18','Dispatch'),(1100,'igor','2011-10-20 17:18:18','IK Site','update','P100','E03','','','Specimen'),(1101,'igor','2011-10-20 17:18:18','IK Site','update','','','','state: Received, received at: 2011-10-20 17:18','Dispatch'),(1102,'igor','2011-10-20 17:18:18','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 17:18','Dispatch'),(1103,'igor','2011-10-20 17:18:18','IK Site','update','','','','state: Closed, received at: 2011-10-20 17:18','Dispatch'),(1104,'igor','2011-10-20 17:18:19','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 17:18','Dispatch'),(1105,'igor','2011-10-20 17:19:03','','logout','','','','',''),(1106,'igor','2011-10-20 17:19:09','','login','','','','',''),(1107,'igor','2011-10-20 17:19:34','IK Clinic','update','P100','E04','','','Specimen'),(1108,'igor','2011-10-20 17:19:34','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1109,'igor','2011-10-20 17:19:47','IK Clinic','insert','','','','state: Creation','Dispatch'),(1110,'igor','2011-10-20 17:19:47','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1111,'igor','2011-10-20 17:22:24','IK Clinic','delete','','','','state: Creation','Dispatch'),(1112,'igor','2011-10-20 17:23:55','IK Clinic','insert','','','','state: Creation','Dispatch'),(1113,'igor','2011-10-20 17:23:55','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1114,'igor','2011-10-20 17:23:58','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1115,'igor','2011-10-20 17:23:58','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1116,'igor','2011-10-20 17:24:02','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1117,'igor','2011-10-20 17:24:05','','logout','','','','',''),(1118,'igor','2011-10-20 17:24:05','','login','','','','',''),(1119,'igor','2011-10-20 17:25:10','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1120,'igor','2011-10-20 17:25:18','IK Site','update','','','','state: Received, received at: 2011-10-20 17:25','Dispatch'),(1121,'igor','2011-10-20 17:25:18','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 17:25','Dispatch'),(1122,'igor','2011-10-20 17:25:27','IK Site','update','P100','E04','','','Specimen'),(1123,'igor','2011-10-20 17:25:27','IK Site','update','','','','state: Received, received at: 2011-10-20 17:25','Dispatch'),(1124,'igor','2011-10-20 17:25:28','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 17:25','Dispatch'),(1125,'igor','2011-10-20 17:25:29','IK Site','update','','','','state: Closed, received at: 2011-10-20 17:25','Dispatch'),(1126,'igor','2011-10-20 17:25:29','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 17:25','Dispatch'),(1127,'igor','2011-10-20 17:25:44','IK Clinic','update','P100','E05','','','Specimen'),(1128,'igor','2011-10-20 17:25:44','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1129,'igor','2011-10-20 17:25:46','','logout','','','','',''),(1130,'igor','2011-10-20 17:25:51','','login','','','','',''),(1131,'igor','2011-10-20 17:26:00','IK Clinic','select','P100','','','Patient LOOKUP','Patient'),(1132,'igor','2011-10-20 17:26:04','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:15','CollectionEvent'),(1133,'igor','2011-10-20 17:26:09','IK Clinic','edit','P100','','','CollectionEvent EDIT, visit:1, specimens:15','CollectionEvent'),(1134,'igor','2011-10-20 17:26:54','','update','P100','','','visit:1, specimens:20','CollectionEvent'),(1135,'igor','2011-10-20 17:26:54','IK Clinic','select','P100','','','CollectionEvent LOOKUP, visit:1, specimens:20','CollectionEvent'),(1136,'igor','2011-10-20 17:27:04','','logout','','','','',''),(1137,'igor','2011-10-20 17:27:30','','login','','','','',''),(1138,'igor','2011-10-20 17:28:05','IK Clinic','insert','','','','state: Creation','Dispatch'),(1139,'igor','2011-10-20 17:28:05','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1140,'igor','2011-10-20 17:28:08','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1141,'igor','2011-10-20 17:28:08','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1142,'igor','2011-10-20 17:28:12','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1143,'igor','2011-10-20 17:28:15','','logout','','','','',''),(1144,'igor','2011-10-20 17:28:16','','login','','','','',''),(1145,'igor','2011-10-20 17:28:20','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-20 00:00','Dispatch'),(1146,'igor','2011-10-20 17:28:21','IK Site','update','','','','state: Received, received at: 2011-10-20 17:28','Dispatch'),(1147,'igor','2011-10-20 17:28:21','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-20 17:28','Dispatch'),(1148,'igor','2011-10-20 17:28:22','IK Site','update','P100','E05','','','Specimen'),(1149,'igor','2011-10-20 17:28:22','IK Site','update','','','','state: Received, received at: 2011-10-20 17:28','Dispatch'),(1150,'igor','2011-10-20 17:28:23','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-20 17:28','Dispatch'),(1151,'igor','2011-10-20 17:28:23','IK Site','update','','','','state: Closed, received at: 2011-10-20 17:28','Dispatch'),(1152,'igor','2011-10-20 17:28:24','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-20 17:28','Dispatch'),(1153,'igor','2011-10-21 09:15:56','IK Clinic','update','P100','E06','','','Specimen'),(1154,'igor','2011-10-21 09:15:57','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1155,'igor','2011-10-21 09:18:40','','logout','','','','',''),(1156,'igor','2011-10-21 09:23:28','','login','','','','',''),(1157,'igor','2011-10-21 09:24:50','IK Clinic','insert','','','','state: Creation','Dispatch'),(1158,'igor','2011-10-21 09:24:50','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1159,'igor','2011-10-21 09:24:53','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1160,'igor','2011-10-21 09:24:53','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1161,'igor','2011-10-21 09:24:59','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1162,'igor','2011-10-21 09:25:02','','logout','','','','',''),(1163,'igor','2011-10-21 09:25:10','','login','','','','',''),(1164,'igor','2011-10-21 09:25:14','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1165,'igor','2011-10-21 09:25:14','IK Site','update','','','','state: Received, received at: 2011-10-21 09:25','Dispatch'),(1166,'igor','2011-10-21 09:25:15','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-21 09:25','Dispatch'),(1167,'igor','2011-10-21 09:25:16','IK Site','update','P100','E06','','','Specimen'),(1168,'igor','2011-10-21 09:25:16','IK Site','update','','','','state: Received, received at: 2011-10-21 09:25','Dispatch'),(1169,'igor','2011-10-21 09:25:17','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-21 09:25','Dispatch'),(1170,'igor','2011-10-21 09:25:17','IK Site','update','','','','state: Closed, received at: 2011-10-21 09:25','Dispatch'),(1171,'igor','2011-10-21 09:25:17','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-21 09:25','Dispatch'),(1172,'igor','2011-10-21 09:26:28','IK Clinic','update','P100','E07','','','Specimen'),(1173,'igor','2011-10-21 09:26:28','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1174,'igor','2011-10-21 09:27:05','','logout','','','','',''),(1175,'igor','2011-10-21 09:27:18','','login','','','','',''),(1176,'igor','2011-10-21 09:27:30','IK Clinic','insert','','','','state: Creation','Dispatch'),(1177,'igor','2011-10-21 09:27:31','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1178,'igor','2011-10-21 09:27:33','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1179,'igor','2011-10-21 09:27:33','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1180,'igor','2011-10-21 09:29:51','IK Clinic','update','','','','state: Creation','Dispatch'),(1181,'igor','2011-10-21 09:29:58','IK Clinic','delete','','','','state: Creation','Dispatch'),(1182,'igor','2011-10-21 09:30:06','','logout','','','','',''),(1183,'igor','2011-10-21 09:30:26','','login','','','','',''),(1184,'igor','2011-10-21 09:31:39','IK Clinic','insert','','','','state: Creation','Dispatch'),(1185,'igor','2011-10-21 09:31:39','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1186,'igor','2011-10-21 09:33:54','IK Clinic','delete','','','','state: Creation','Dispatch'),(1187,'igor','2011-10-21 09:34:16','IK Clinic','insert','','','','state: Creation','Dispatch'),(1188,'igor','2011-10-21 09:34:16','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1189,'igor','2011-10-21 09:34:19','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1190,'igor','2011-10-21 09:34:19','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1191,'igor','2011-10-21 09:34:22','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1192,'igor','2011-10-21 09:34:25','','logout','','','','',''),(1193,'igor','2011-10-21 09:34:32','','login','','','','',''),(1194,'igor','2011-10-21 09:34:35','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1195,'igor','2011-10-21 09:34:36','IK Site','update','','','','state: Received, received at: 2011-10-21 09:34','Dispatch'),(1196,'igor','2011-10-21 09:34:36','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-21 09:34','Dispatch'),(1197,'igor','2011-10-21 09:34:38','IK Site','update','P100','E07','','','Specimen'),(1198,'igor','2011-10-21 09:34:38','IK Site','update','','','','state: Received, received at: 2011-10-21 09:34','Dispatch'),(1199,'igor','2011-10-21 09:34:39','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-21 09:34','Dispatch'),(1200,'igor','2011-10-21 09:34:39','IK Site','update','','','','state: Closed, received at: 2011-10-21 09:34','Dispatch'),(1201,'igor','2011-10-21 09:34:39','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-21 09:34','Dispatch'),(1202,'igor','2011-10-21 09:39:41','IK Clinic','update','P100','E08','','','Specimen'),(1203,'igor','2011-10-21 09:39:41','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1204,'igor','2011-10-21 09:44:16','','insert','rrr','','','','Patient'),(1205,'igor','2011-10-21 09:44:16','IK Site','select','rrr','','','Patient LOOKUP','Patient'),(1206,'igor','2011-10-21 09:47:09','','delete','rrr','','','','Patient'),(1207,'igor','2011-10-21 09:47:11','','logout','','','','',''),(1208,'igor','2011-10-21 09:47:27','','login','','','','',''),(1209,'igor','2011-10-21 09:47:42','','insert','rrr','','','','Patient'),(1210,'igor','2011-10-21 09:47:43','IK Clinic','select','rrr','','','Patient LOOKUP','Patient'),(1211,'igor','2011-10-21 09:48:35','','insert','rrr','','','visit:1, specimens:1','CollectionEvent'),(1212,'igor','2011-10-21 09:48:36','IK Clinic','select','rrr','','','CollectionEvent LOOKUP, visit:1, specimens:1','CollectionEvent'),(1213,'igor','2011-10-21 09:51:54','IK Clinic','edit','rrr','','','CollectionEvent EDIT, visit:1, specimens:1','CollectionEvent'),(1214,'igor','2011-10-21 09:51:59','IK Clinic','delete','rrr','ttt','','','Specimen'),(1215,'igor','2011-10-21 09:51:59','','update','rrr','','','visit:1, specimens:0','CollectionEvent'),(1216,'igor','2011-10-21 09:51:59','IK Clinic','select','rrr','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(1217,'igor','2011-10-21 09:52:02','','delete','rrr','','','visit:1, specimens:0','CollectionEvent'),(1218,'igor','2011-10-21 09:52:07','','delete','rrr','','','','Patient'),(1219,'igor','2011-10-21 09:53:19','','logout','','','','',''),(1220,'igor','2011-10-21 09:53:48','','login','','','','',''),(1221,'igor','2011-10-21 09:53:52','','insert','Patient001','','','','Patient'),(1222,'igor','2011-10-21 09:53:53','IK Clinic','select','Patient001','','','Patient LOOKUP','Patient'),(1223,'igor','2011-10-21 09:54:02','','insert','Patient001','','','visit:1, specimens:10','CollectionEvent'),(1224,'igor','2011-10-21 09:54:02','IK Clinic','select','Patient001','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(1225,'igor','2011-10-21 09:54:06','IK Clinic','insert','','','','state: Creation','Dispatch'),(1226,'igor','2011-10-21 09:54:06','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1227,'igor','2011-10-21 09:54:09','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1228,'igor','2011-10-21 09:54:09','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1229,'igor','2011-10-21 09:54:12','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1230,'igor','2011-10-21 09:54:15','','logout','','','','',''),(1231,'igor','2011-10-21 09:54:22','','login','','','','',''),(1232,'igor','2011-10-21 09:54:25','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1233,'igor','2011-10-21 09:54:26','IK Site','update','','','','state: Received, received at: 2011-10-21 09:54','Dispatch'),(1234,'igor','2011-10-21 09:54:26','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-21 09:54','Dispatch'),(1235,'igor','2011-10-21 09:54:28','IK Site','update','Patient001','S001','','','Specimen'),(1236,'igor','2011-10-21 09:54:28','IK Site','update','','','','state: Received, received at: 2011-10-21 09:54','Dispatch'),(1237,'igor','2011-10-21 09:54:29','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-21 09:54','Dispatch'),(1238,'igor','2011-10-21 09:54:29','IK Site','update','','','','state: Closed, received at: 2011-10-21 09:54','Dispatch'),(1239,'igor','2011-10-21 09:54:30','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-21 09:54','Dispatch'),(1240,'igor','2011-10-21 09:55:41','IK Clinic','update','Patient001','S002','','','Specimen'),(1241,'igor','2011-10-21 09:55:41','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1242,'igor','2011-10-21 09:55:42','','logout','','','','',''),(1243,'igor','2011-10-21 09:58:26','','login','','','','',''),(1244,'igor','2011-10-21 09:58:31','','insert','Patient002','','','','Patient'),(1245,'igor','2011-10-21 09:58:31','IK Clinic','select','Patient002','','','Patient LOOKUP','Patient'),(1246,'igor','2011-10-21 09:58:41','','insert','Patient002','','','visit:1, specimens:10','CollectionEvent'),(1247,'igor','2011-10-21 09:58:41','IK Clinic','select','Patient002','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(1248,'igor','2011-10-21 09:58:44','IK Clinic','insert','','','','state: Creation','Dispatch'),(1249,'igor','2011-10-21 09:58:45','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1250,'igor','2011-10-21 09:58:48','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1251,'igor','2011-10-21 09:58:48','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1252,'igor','2011-10-21 09:58:51','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1253,'igor','2011-10-21 09:58:54','','logout','','','','',''),(1254,'igor','2011-10-21 09:59:01','','login','','','','',''),(1255,'igor','2011-10-21 09:59:04','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1256,'igor','2011-10-21 09:59:05','IK Site','update','','','','state: Received, received at: 2011-10-21 09:59','Dispatch'),(1257,'igor','2011-10-21 09:59:05','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-21 09:59','Dispatch'),(1258,'igor','2011-10-21 09:59:07','IK Site','update','Patient002','S011','','','Specimen'),(1259,'igor','2011-10-21 09:59:07','IK Site','update','','','','state: Received, received at: 2011-10-21 09:59','Dispatch'),(1260,'igor','2011-10-21 09:59:08','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-21 09:59','Dispatch'),(1261,'igor','2011-10-21 09:59:08','IK Site','update','','','','state: Closed, received at: 2011-10-21 09:59','Dispatch'),(1262,'igor','2011-10-21 09:59:08','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-21 09:59','Dispatch'),(1263,'igor','2011-10-21 10:00:47','IK Clinic','update','Patient002','S012','','','Specimen'),(1264,'igor','2011-10-21 10:00:47','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1265,'igor','2011-10-21 10:00:48','','logout','','','','',''),(1266,'igor','2011-10-21 10:01:03','','login','','','','',''),(1267,'igor','2011-10-21 10:01:08','','insert','Patient003','','','','Patient'),(1268,'igor','2011-10-21 10:01:08','IK Clinic','select','Patient003','','','Patient LOOKUP','Patient'),(1269,'igor','2011-10-21 10:01:18','','insert','Patient003','','','visit:1, specimens:10','CollectionEvent'),(1270,'igor','2011-10-21 10:01:18','IK Clinic','select','Patient003','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(1271,'igor','2011-10-21 10:01:21','IK Clinic','insert','','','','state: Creation','Dispatch'),(1272,'igor','2011-10-21 10:01:21','IK Clinic','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1273,'igor','2011-10-21 10:01:24','IK Clinic','update','','','','state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1274,'igor','2011-10-21 10:01:24','IK Clinic','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1275,'igor','2011-10-21 10:01:27','IK Clinic','insert','','','','waybill:null, specimens:2','Shipment'),(1276,'igor','2011-10-21 10:01:30','','logout','','','','',''),(1277,'igor','2011-10-21 10:01:37','','login','','','','',''),(1278,'igor','2011-10-21 10:01:41','IK Site','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-10-21 00:00','Dispatch'),(1279,'igor','2011-10-21 10:01:42','IK Site','update','','','','state: Received, received at: 2011-10-21 10:01','Dispatch'),(1280,'igor','2011-10-21 10:01:42','IK Site','edit','','','','Dispatch EDIT, state: Received, received at: 2011-10-21 10:01','Dispatch'),(1281,'igor','2011-10-21 10:01:44','IK Site','update','Patient003','S021','','','Specimen'),(1282,'igor','2011-10-21 10:01:44','IK Site','update','','','','state: Received, received at: 2011-10-21 10:01','Dispatch'),(1283,'igor','2011-10-21 10:01:44','IK Site','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-10-21 10:01','Dispatch'),(1284,'igor','2011-10-21 10:01:45','IK Site','update','','','','state: Closed, received at: 2011-10-21 10:01','Dispatch'),(1285,'igor','2011-10-21 10:01:45','IK Site','select','','','','Dispatch LOOKUP, state: Closed, received at: 2011-10-21 10:01','Dispatch'),(1286,'igor','2011-10-21 10:02:47','IK Clinic','update','Patient003','S022','','','Specimen'),(1287,'igor','2011-10-21 10:02:47','IK Clinic','delete','','','','waybill:null, specimens:1','Shipment'),(1288,'igor','2011-10-21 10:03:23','','insert','utghjhfgh','','','','Patient'),(1289,'igor','2011-10-21 10:03:23','IK Site','select','utghjhfgh','','','Patient LOOKUP','Patient'),(1290,'igor','2011-10-21 10:03:34','','insert','utghjhfgh','','','visit:1, specimens:1','CollectionEvent'),(1291,'igor','2011-10-21 10:03:34','IK Site','select','utghjhfgh','','','CollectionEvent LOOKUP, visit:1, specimens:1','CollectionEvent'),(1292,'igor','2011-10-21 10:08:04','IK Site','edit','utghjhfgh','','','CollectionEvent EDIT, visit:1, specimens:1','CollectionEvent'),(1293,'igor','2011-10-21 10:08:09','IK Site','delete','utghjhfgh','htyuty','','','Specimen'),(1294,'igor','2011-10-21 10:08:09','','update','utghjhfgh','','','visit:1, specimens:0','CollectionEvent'),(1295,'igor','2011-10-21 10:08:09','IK Site','select','utghjhfgh','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(1296,'igor','2011-10-21 10:08:12','','delete','utghjhfgh','','','visit:1, specimens:0','CollectionEvent'),(1297,'igor','2011-10-21 10:08:15','','delete','utghjhfgh','','','','Patient'),(1298,'igor','2011-10-21 10:08:17','','logout','','','','',''),(1299,'igor','2011-10-21 10:10:56','','login','','','','',''),(1300,'igor','2011-10-21 10:11:00','','insert','Patient004','','','','Patient'),(1301,'igor','2011-10-21 10:11:01','IK Clinic','select','Patient004','','','Patient LOOKUP','Patient'),(1302,'igor','2011-10-21 10:11:10','','insert','Patient004','','','visit:1, specimens:10','CollectionEvent'),(1303,'igor','2011-10-21 10:11:10','IK Clinic','select','Patient004','','','CollectionEvent LOOKUP, visit:1, specimens:10','CollectionEvent'),(1304,'igor','2011-10-21 10:14:14','IK Clinic','edit','Patient004','','','CollectionEvent EDIT, visit:1, specimens:10','CollectionEvent'),(1305,'igor','2011-10-21 10:14:38','IK Clinic','delete','Patient004','S035','','','Specimen'),(1306,'igor','2011-10-21 10:14:38','IK Clinic','delete','Patient004','S031','','','Specimen'),(1307,'igor','2011-10-21 10:14:38','IK Clinic','delete','Patient004','S034','','','Specimen'),(1308,'igor','2011-10-21 10:14:38','IK Clinic','delete','Patient004','S037','','','Specimen'),(1309,'igor','2011-10-21 10:14:38','IK Clinic','delete','Patient004','S033','','','Specimen'),(1310,'igor','2011-10-21 10:14:38','IK Clinic','delete','Patient004','S036','','','Specimen'),(1311,'igor','2011-10-21 10:14:38','IK Clinic','delete','Patient004','S032','','','Specimen'),(1312,'igor','2011-10-21 10:14:38','IK Clinic','delete','Patient004','S039','','','Specimen'),(1313,'igor','2011-10-21 10:14:39','IK Clinic','delete','Patient004','S038','','','Specimen'),(1314,'igor','2011-10-21 10:14:39','IK Clinic','delete','Patient004','S040','','','Specimen'),(1315,'igor','2011-10-21 10:14:39','','update','Patient004','','','visit:1, specimens:0','CollectionEvent'),(1316,'igor','2011-10-21 10:14:39','IK Clinic','select','Patient004','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(1317,'igor','2011-10-21 10:14:43','','delete','Patient004','','','visit:1, specimens:0','CollectionEvent'),(1318,'igor','2011-10-21 10:14:46','','delete','Patient004','','','','Patient'),(1319,'igor','2011-10-21 10:15:19','','logout','','','','',''),(1320,'igor','2011-10-21 10:15:45','','login','','','','',''),(1321,'igor','2011-10-21 10:16:19','','logout','','','','',''),(1322,'igor','2011-10-21 10:16:33','','login','','','','',''),(1323,'igor','2011-10-21 10:21:53','','logout','','','','',''),(1324,'igor','2011-10-21 10:22:06','','login','','','','',''),(1325,'igor','2011-10-21 10:25:28','','logout','','','','',''),(1326,'igor','2011-10-21 10:25:42','','login','','','','',''),(1327,'igor','2011-10-21 10:31:47','','logout','','','','',''),(1328,'igor','2011-10-21 10:32:01','','login','','','','',''),(1329,'igor','2011-10-21 11:31:54','','logout','','','','',''),(1330,'igor','2011-10-21 11:32:35','','login','','','','',''),(1331,'igor','2011-10-21 11:37:51','IK Clinic','select','Pat100','','','Patient LOOKUP','Patient'),(1332,'igor','2011-10-21 11:38:01','IK Clinic','select','Pat100','','','CollectionEvent LOOKUP, visit:1, specimens:20','CollectionEvent'),(1333,'igor','2011-10-21 11:38:04','','logout','','','','',''),(1334,'igor','2011-10-21 11:38:18','','login','','','','',''),(1335,'igor','2011-10-21 11:38:22','','logout','','','','',''),(1336,'igor','2011-10-21 11:38:31','','login','','','','',''),(1337,'igor','2011-10-21 11:39:50','','logout','','','','',''),(1338,'igor','2011-10-21 15:50:31','','login','','','','',''),(1339,'igor','2011-10-21 15:50:37','','logout','','','','',''),(1340,'igor','2011-10-21 15:50:45','','login','','','','',''),(1341,'igor','2011-10-21 16:07:47','','logout','','','','',''),(1342,'igor','2011-10-21 16:08:05','','login','','','','',''),(1343,'igor','2011-10-21 16:09:25','','logout','','','','',''),(1344,'igor','2011-10-21 16:09:33','','login','','','','',''),(1345,'igor','2011-10-21 16:09:56','IK Clinic','select','Pat100','','','Patient LOOKUP','Patient'),(1346,'igor','2011-10-21 16:10:00','IK Clinic','select','Pat100','','','CollectionEvent LOOKUP, visit:1, specimens:20','CollectionEvent'),(1347,'igor','2011-10-21 16:20:23','IK Clinic','insert','','','','Source Specimens: 2, Worksheet: 1','ProcessingEvent'),(1348,'igor','2011-10-21 16:23:32','IK Clinic','select','Pat100','','','CollectionEvent LOOKUP, visit:1, specimens:20','CollectionEvent'),(1349,'igor','2011-10-21 16:24:16','IK Clinic','select','Pat100','','','CollectionEvent LOOKUP, visit:1, specimens:20','CollectionEvent'),(1350,'igor','2011-10-21 16:25:40','IK Clinic','select','Pat100','Event15','','Specimen LOOKUP','Specimen'),(1351,'igor','2011-10-21 16:31:47','IK Clinic','select','Pat100','','','CollectionEvent LOOKUP, visit:1, specimens:20','CollectionEvent'),(1352,'igor','2011-10-21 16:45:39','','logout','','','','',''),(1353,'igor','2011-10-21 16:45:47','','login','','','','',''),(1354,'igor','2011-10-21 16:49:34','IK Clinic','update','','','','Source Specimens: 1, Worksheet: 1111','ProcessingEvent'),(1355,'igor','2011-10-21 17:00:50','','logout','','','','',''),(1356,'igor','2011-10-25 09:26:36','','login','','','','',''),(1357,'igor','2011-10-25 18:03:24','','logout','','','','',''),(1358,'igor','2011-10-26 10:14:01','','login','','','','',''),(1359,'igor','2011-10-27 09:59:07','','logout','','','','',''),(1360,'igor','2011-10-27 12:07:56','','login','','','','',''),(1361,'igor','2011-10-27 15:32:45','','logout','','','','',''),(1362,'igor','2011-10-28 09:35:09','','login','','','','',''),(1363,'igor','2011-10-28 11:29:19','','insert','C01','','','','Patient'),(1364,'igor','2011-10-28 11:29:19','Clinic1','select','C01','','','Patient LOOKUP','Patient'),(1365,'igor','2011-10-28 11:59:39','','insert','C01','','','visit:1, specimens:0','CollectionEvent'),(1366,'igor','2011-10-28 12:01:18','Clinic1','select','C01','','','Patient LOOKUP','Patient'),(1367,'igor','2011-10-28 12:01:21','Clinic1','edit','C01','','','Patient EDIT','Patient'),(1368,'igor','2011-10-28 12:01:29','Clinic1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(1369,'igor','2011-10-28 12:01:37','Clinic1','edit','C01','','','CollectionEvent EDIT, visit:1, specimens:0','CollectionEvent'),(1370,'igor','2011-10-28 12:06:17','','update','C01','','','visit:1, specimens:0','CollectionEvent'),(1371,'igor','2011-10-28 12:06:17','Clinic1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(1372,'igor','2011-10-28 12:52:10','Clinic1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(1373,'igor','2011-10-28 12:56:21','Clinic1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(1374,'igor','2011-10-28 16:03:19','Clinic1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(1375,'igor','2011-10-28 16:06:35','','logout','','','','',''),(1376,'igor','2011-11-01 10:35:25','','login','','','','',''),(1377,'igor','2011-11-01 10:57:32','','logout','','','','',''),(1378,'igor','2011-11-01 10:57:41','','login','','','','',''),(1379,'igor','2011-11-01 17:25:06','','logout','','','','',''),(1380,'igor','2011-11-02 09:01:09','','login','','','','',''),(1381,'igor','2011-11-02 09:45:30','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1382,'igor','2011-11-02 09:45:42','Repository1','edit','C01','','','Patient EDIT','Patient'),(1383,'igor','2011-11-02 09:45:55','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1384,'igor','2011-11-02 09:45:58','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:0','CollectionEvent'),(1385,'igor','2011-11-02 09:46:07','Repository1','edit','C01','','','CollectionEvent EDIT, visit:1, specimens:0','CollectionEvent'),(1386,'igor','2011-11-02 09:49:51','','update','C01','','','visit:1, specimens:4','CollectionEvent'),(1387,'igor','2011-11-02 09:49:51','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1388,'igor','2011-11-02 09:50:03','Repository1','edit','C01','','','CollectionEvent EDIT, visit:1, specimens:4','CollectionEvent'),(1389,'igor','2011-11-02 09:50:59','Repository1','delete','C01','C010004','','','Specimen'),(1390,'igor','2011-11-02 09:50:59','','update','C01','','','visit:1, specimens:3','CollectionEvent'),(1391,'igor','2011-11-02 09:51:00','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:3','CollectionEvent'),(1392,'igor','2011-11-02 09:51:03','Repository1','edit','C01','','','CollectionEvent EDIT, visit:1, specimens:3','CollectionEvent'),(1393,'igor','2011-11-02 09:52:00','','update','C01','','','visit:1, specimens:4','CollectionEvent'),(1394,'igor','2011-11-02 09:52:01','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1395,'igor','2011-11-02 09:55:08','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1396,'igor','2011-11-02 09:55:29','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1397,'testuser','2011-11-02 17:43:42','','login','','','','',''),(1398,'testuser','2011-11-02 17:44:20','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1399,'testuser','2011-11-03 10:23:19','','login','','','','',''),(1400,'testuser','2011-11-03 10:24:45','','logout','','','','',''),(1401,'testuser','2011-11-03 10:28:39','','login','','','','',''),(1402,'testuser','2011-11-03 11:20:52','','logout','','','','',''),(1403,'testuser','2011-11-03 11:21:47','','login','','','','',''),(1404,'testuser','2011-11-03 12:37:42','','logout','','','','',''),(1405,'testuser','2011-11-03 12:38:28','','login','','','','',''),(1406,'testuser','2011-11-03 12:51:56','','login','','','','',''),(1407,'testuser','2011-11-03 14:13:00','','logout','','','','',''),(1408,'testuser','2011-11-03 14:16:16','','login','','','','',''),(1409,'testuser','2011-11-03 14:19:52','Repository1','insert','','','','Source Specimens: 2, Worksheet: 1','ProcessingEvent'),(1410,'testuser','2011-11-03 14:20:51','','logout','','','','',''),(1411,'testuser','2011-11-03 14:24:06','','login','','','','',''),(1412,'testuser','2011-11-03 14:26:15','','logout','','','','',''),(1413,'testuser','2011-11-03 14:38:16','','login','','','','',''),(1414,'testuser','2011-11-03 14:47:32','','logout','','','','',''),(1415,'testuser','2011-11-03 15:06:09','','login','','','','',''),(1416,'testuser','2011-11-03 15:10:05','','logout','','','','',''),(1417,'testuser','2011-11-03 15:37:59','','login','','','','',''),(1418,'testuser','2011-11-03 15:39:58','','logout','','','','',''),(1419,'testuser','2011-11-04 10:12:34','','login','','','','',''),(1420,'testuser','2011-11-04 10:29:33','','logout','','','','',''),(1421,'testuser','2011-11-04 10:38:20','','login','','','','',''),(1422,'testuser','2011-11-04 10:53:26','','login','','','','',''),(1423,'testuser','2011-11-04 10:59:44','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1424,'testuser','2011-11-04 11:00:01','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1425,'testuser','2011-11-04 11:18:27','','logout','','','','',''),(1426,'testuser','2011-11-04 11:23:22','','login','','','','',''),(1427,'testuser','2011-11-04 11:24:36','','logout','','','','',''),(1428,'testuser','2011-11-04 11:33:53','','login','','','','',''),(1429,'testuser','2011-11-04 11:35:03','','logout','','','','',''),(1430,'testuser','2011-11-04 11:43:03','','login','','','','',''),(1431,'testuser','2011-11-04 12:09:03','','logout','','','','',''),(1432,'testuser','2011-11-04 12:09:26','','login','','','','',''),(1433,'testuser','2011-11-04 12:10:29','','logout','','','','',''),(1434,'testuser','2011-11-04 12:22:49','','login','','','','',''),(1435,'testuser','2011-11-04 14:11:21','','login','','','','',''),(1436,'testuser','2011-11-04 14:18:20','','login','','','','',''),(1437,'testuser','2011-11-04 14:31:51','','login','','','','',''),(1438,'testuser','2011-11-04 15:22:14','','login','','','','',''),(1439,'testuser','2011-11-04 15:22:59','','login','','','','',''),(1440,'testuser','2011-11-04 15:25:26','','login','','','','',''),(1441,'testuser','2011-11-04 16:02:31','','login','','','','',''),(1442,'testuser','2011-11-04 16:24:56','','login','','','','',''),(1443,'testuser','2011-11-07 09:17:36','','login','','','','',''),(1444,'testuser','2011-11-07 09:23:43','','login','','','','',''),(1445,'testuser','2011-11-07 13:46:54','','login','','','','',''),(1446,'testuser','2011-11-07 13:52:07','','login','','','','',''),(1447,'testuser','2011-11-07 13:52:30','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1448,'testuser','2011-11-07 13:52:49','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1449,'testuser','2011-11-07 14:04:19','Repository1','update','','','','Source Specimens: 1, Worksheet: 1','ProcessingEvent'),(1450,'testuser','2011-11-07 14:05:21','Repository1','update','','','','Source Specimens: 1, Worksheet: 1','ProcessingEvent'),(1451,'testuser','2011-11-07 16:56:48','','logout','','','','',''),(1452,'testuser','2011-11-08 09:51:15','','login','','','','',''),(1453,'testuser','2011-11-08 09:55:08','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1454,'testuser','2011-11-08 09:55:21','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1455,'testuser','2011-11-08 10:18:40','','login','','','','',''),(1456,'testuser','2011-11-08 11:28:00','','logout','','','','',''),(1457,'testuser','2011-11-08 11:50:48','','login','','','','',''),(1458,'testuser','2011-11-08 13:54:10','','login','','','','',''),(1459,'testuser','2011-11-08 13:57:48','','login','','','','',''),(1460,'testuser','2011-11-08 15:02:28','','login','','','','',''),(1461,'testuser','2011-11-08 15:04:05','','login','','','','',''),(1462,'testuser','2011-11-08 15:09:06','','login','','','','',''),(1463,'testuser','2011-11-08 15:11:19','','login','','','','',''),(1464,'testuser','2011-11-08 15:13:31','','logout','','','','',''),(1465,'testuser','2011-11-08 15:13:40','','login','','','','',''),(1466,'testuser','2011-11-08 15:14:39','','login','','','','',''),(1467,'testuser','2011-11-08 15:17:26','','login','','','','',''),(1468,'testuser','2011-11-08 15:21:39','','login','','','','',''),(1469,'testuser','2011-11-08 17:23:22','Repository1','insert','','','','state: Creation','Dispatch'),(1470,'testuser','2011-11-08 17:23:23','Repository1','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1471,'testuser','2011-11-08 18:19:49','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1472,'testuser','2011-11-08 18:19:58','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1473,'testuser','2011-11-08 18:20:15','Repository1','select','C01','C010001','','Specimen LOOKUP','Specimen'),(1474,'testuser','2011-11-08 18:22:54','Repository1','insert','C01','C01001','','','Specimen'),(1475,'testuser','2011-11-08 18:24:12','','print','','','','','SpecimenLink'),(1476,'testuser','2011-11-08 18:24:51','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1477,'testuser','2011-11-08 18:52:59','','print','','','','','TecanScanLink'),(1478,'testuser','2011-11-08 18:58:54','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1479,'testuser','2011-11-08 19:45:29','Repository1','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1480,'testuser','2011-11-08 19:45:58','Repository1','update','','','','state: In Transit, packed at: 2011-11-08 19:45, , waybill: 2','Dispatch'),(1481,'testuser','2011-11-08 19:46:00','Repository1','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-11-08 19:45, , waybill: 2','Dispatch'),(1482,'testuser','2011-11-08 19:46:20','','logout','','','','',''),(1483,'testuser','2011-11-08 19:46:28','','login','','','','',''),(1484,'testuser','2011-11-08 19:46:46','Clinic1','select','','','','Dispatch LOOKUP, state: In Transit, packed at: 2011-11-08 19:45, , waybill: 2','Dispatch'),(1485,'testuser','2011-11-08 19:46:56','Clinic1','update','','','','state: Received, received at: 2011-11-08 19:46, , waybill: 2','Dispatch'),(1486,'testuser','2011-11-08 19:46:57','Clinic1','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-11-08 19:46, , waybill: 2','Dispatch'),(1487,'testuser','2011-11-08 19:47:19','Clinic1','edit','','','','Dispatch EDIT, state: Received, received at: 2011-11-08 19:46, , waybill: 2','Dispatch'),(1488,'testuser','2011-11-08 19:47:30','Clinic1','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-11-08 19:46, , waybill: 2','Dispatch'),(1489,'testuser','2011-11-08 19:47:42','Clinic1','edit','','','','Dispatch EDIT, state: Received, received at: 2011-11-08 19:46, , waybill: 2','Dispatch'),(1490,'testuser','2011-11-08 19:51:14','Clinic1','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-11-08 19:46, , waybill: 2','Dispatch'),(1491,'testuser','2011-11-08 20:08:11','Clinic1','select','C01','','','Patient LOOKUP','Patient'),(1492,'testuser','2011-11-08 20:08:18','Clinic1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1493,'testuser','2011-11-09 08:59:38','','login','','','','',''),(1494,'testuser','2011-11-14 08:50:26','','login','','','','',''),(1495,'testuser','2011-11-14 08:52:37','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1496,'testuser','2011-11-14 09:16:59','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1497,'testuser','2011-11-14 09:19:39','Repository1','select','C01','C01001','','Specimen LOOKUP','Specimen'),(1498,'testuser','2011-11-14 09:21:10','Repository1','edit','C01','C01001','','Specimen EDIT','Specimen'),(1499,'testuser','2011-11-14 09:23:04','Repository1','select','C01','C01001','','Specimen LOOKUP','Specimen'),(1500,'testuser','2011-11-15 11:29:09','','login','','','','',''),(1501,'testuser','2011-11-15 13:43:26','','logout','','','','',''),(1502,'testuser','2011-11-15 14:21:09','','login','','','','',''),(1503,'testuser','2011-11-16 08:47:39','','login','','','','',''),(1504,'testuser','2011-11-16 10:40:45','','login','','','','',''),(1505,'testuser','2011-11-16 11:53:58','','print','','','','','TecanScanLink'),(1506,'testuser','2011-11-16 17:52:17','','logout','','','','',''),(1507,'testuser','2011-11-16 17:52:40','','login','','','','',''),(1508,'testuser','2011-11-16 17:53:31','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1509,'testuser','2011-11-16 18:26:09','','logout','','','','',''),(1510,'testuser','2011-11-17 13:52:59','','login','','','','',''),(1511,'testuser','2011-11-17 13:56:15','','print','','','','','TecanScanLink'),(1512,'testuser','2011-11-17 13:58:09','','print','','','','','TecanScanLink'),(1513,'testuser','2011-11-17 16:44:02','Repository1','insert','C01','12345678','','','Specimen'),(1514,'testuser','2011-11-17 16:46:37','Repository1','insert','C01','87654321','','','Specimen'),(1515,'testuser','2011-11-17 16:53:03','Repository1','insert','C01','343434','','','Specimen'),(1516,'testuser','2011-11-17 17:12:27','','print','','','','','SpecimenLink'),(1517,'testuser','2011-11-17 17:12:32','','logout','','','','',''),(1518,'testuser','2011-11-18 14:28:39','','login','','','','',''),(1519,'testuser','2011-11-18 14:31:35','Repository1','insert','','','','state: Creation','Dispatch'),(1520,'testuser','2011-11-18 14:31:50','Repository1','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1521,'testuser','2011-11-18 14:35:07','Repository1','edit','','','','Dispatch EDIT, state: Creation','Dispatch'),(1522,'testuser','2011-11-21 09:58:43','','login','','','','',''),(1523,'testuser','2011-11-21 11:54:48','','logout','','','','',''),(1524,'testuser','2011-11-21 11:56:59','','login','','','','',''),(1525,'testuser','2011-11-21 11:58:46','Clinic1','select','','','','Dispatch LOOKUP, state: Received, received at: 2011-11-08 19:46, , waybill: 2','Dispatch'),(1526,'testuser','2011-11-21 11:59:40','','logout','','','','',''),(1527,'testuser','2011-11-21 12:00:12','','login','','','','',''),(1528,'testuser','2011-11-21 13:33:33','Repository1','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1529,'testuser','2011-11-21 13:33:55','Repository1','delete','','','','state: Creation','Dispatch'),(1530,'testuser','2011-11-21 13:43:18','Repository1','insert','','','','state: Creation','Dispatch'),(1531,'testuser','2011-11-21 13:43:33','Repository1','select','','','','Dispatch LOOKUP, state: Creation','Dispatch'),(1532,'testuser','2011-11-21 13:44:40','','logout','','','','',''),(1533,'testuser','2011-11-21 13:56:17','','login','','','','',''),(1534,'testuser','2011-11-21 14:25:30','','logout','','','','',''),(1535,'testuser','2011-11-21 16:49:04','','login','','','','',''),(1536,'testuser','2011-11-21 17:00:21','','login','','','','',''),(1537,'testuser','2011-11-21 17:02:27','','logout','','','','',''),(1538,'testuser','2011-11-22 09:55:43','','login','','','','',''),(1539,'testuser','2011-11-22 10:27:54','','logout','','','','',''),(1540,'testuser','2011-11-22 12:06:09','','login','','','','',''),(1541,'testuser','2011-11-22 16:59:19','','login','','','','',''),(1542,'testuser','2011-11-23 09:32:08','','login','','','','',''),(1543,'testuser','2011-11-23 10:54:29','','logout','','','','',''),(1544,'testuser','2011-11-23 11:01:02','','login','','','','',''),(1545,'testuser','2011-11-23 14:21:48','','login','','','','',''),(1546,'testuser','2011-11-23 14:23:49','','logout','','','','',''),(1547,'testuser','2011-11-23 18:53:22','','login','','','','',''),(1548,'testuser','2011-11-23 18:55:17','','logout','','','','',''),(1549,'testuser','2011-11-23 19:01:38','','login','','','','',''),(1550,'testuser','2011-11-23 19:03:27','','logout','','','','',''),(1551,'testuser','2011-11-23 19:04:19','','login','','','','',''),(1552,'testuser','2011-11-24 16:38:51','','login','','','','',''),(1553,'testuser','2011-11-28 17:10:49','','login','','','','',''),(1554,'testuser','2011-11-28 18:34:24','','login','','','','',''),(1555,'testuser','2011-11-28 18:50:45','','login','','','','',''),(1556,'testuser','2011-11-28 19:01:03','','logout','','','','',''),(1557,'testuser','2011-11-29 11:15:51','','login','','','','',''),(1558,'testuser','2011-11-29 11:21:17','','logout','','','','',''),(1559,'testuser','2011-11-29 11:35:09','','login','','','','',''),(1560,'testuser','2011-11-29 11:48:41','','login','','','','',''),(1561,'testuser','2011-11-29 11:57:16','','login','','','','',''),(1562,'testuser','2011-11-29 12:20:18','','login','','','','',''),(1563,'testuser','2011-11-29 12:25:18','','login','','','','',''),(1564,'testuser','2011-11-29 12:30:17','','login','','','','',''),(1565,'testuser','2011-11-29 12:33:14','','logout','','','','',''),(1566,'testuser','2011-11-29 12:38:53','','login','','','','',''),(1567,'testuser','2011-11-29 13:55:29','','login','','','','',''),(1568,'testuser','2011-11-29 15:28:18','','logout','','','','',''),(1569,'testuser','2011-11-29 15:30:27','','login','','','','',''),(1570,'testuser','2011-11-29 15:40:55','','login','','','','',''),(1571,'testuser','2011-11-29 16:02:55','','login','','','','',''),(1572,'testuser','2011-11-29 16:08:15','','logout','','','','',''),(1573,'testuser','2011-11-29 16:30:34','','login','','','','',''),(1574,'testuser','2011-11-29 16:35:32','','logout','','','','',''),(1575,'testuser','2011-11-29 16:51:45','','login','','','','',''),(1576,'testuser','2011-11-29 16:54:10','','print','','','','','TecanScanLink'),(1577,'testuser','2011-11-29 16:55:56','','logout','','','','',''),(1578,'testuser','2011-11-29 17:23:57','','login','','','','',''),(1579,'testuser','2011-11-29 17:58:10','','login','','','','',''),(1580,'testuser','2011-11-29 18:11:16','','logout','','','','',''),(1581,'testuser','2011-11-29 18:11:49','','login','','','','',''),(1582,'testuser','2011-11-29 18:43:08','','login','','','','',''),(1583,'testuser','2011-11-29 18:56:32','','login','','','','',''),(1584,'testuser','2011-11-29 19:03:21','','login','','','','',''),(1585,'testuser','2011-11-29 19:07:23','','login','','','','',''),(1586,'testuser','2011-11-29 19:12:59','','login','','','','',''),(1587,'testuser','2011-11-29 19:23:03','','login','','','','',''),(1588,'testuser','2011-11-29 19:38:18','','login','','','','',''),(1589,'testuser','2011-11-29 19:41:55','','login','','','','',''),(1590,'testuser','2011-11-29 19:50:20','','login','','','','',''),(1591,'testuser','2011-11-29 19:54:34','','login','','','','',''),(1592,'testuser','2011-11-29 20:10:18','','login','','','','',''),(1593,'testuser','2011-11-29 20:21:08','','login','','','','',''),(1594,'testuser','2011-11-29 20:26:11','','login','','','','',''),(1595,'testuser','2011-11-29 20:33:37','','login','','','','',''),(1596,'testuser','2011-11-29 20:41:19','','login','','','','',''),(1597,'testuser','2011-11-30 10:47:40','','login','','','','',''),(1598,'testuser','2011-12-04 15:02:15','','login','','','','',''),(1599,'testuser','2011-12-04 15:13:09','','login','','','','',''),(1600,'testuser','2011-12-04 15:25:18','','login','','','','',''),(1601,'testuser','2011-12-04 16:15:21','','logout','','','','',''),(1602,'testuser','2011-12-04 16:17:16','','login','','','','',''),(1603,'testuser','2011-12-04 16:37:49','','login','','','','',''),(1604,'testuser','2011-12-04 16:43:28','','logout','','','','',''),(1605,'testuser','2011-12-04 16:53:01','','login','','','','',''),(1606,'testuser','2011-12-04 16:55:31','','logout','','','','',''),(1607,'testuser','2011-12-04 17:23:55','','login','','','','',''),(1608,'testuser','2011-12-04 17:52:44','','logout','','','','',''),(1609,'testuser','2011-12-11 13:28:23','','login','','','','',''),(1610,'testuser','2011-12-11 13:36:50','','login','','','','',''),(1611,'testuser','2011-12-11 13:39:30','','login','','','','',''),(1612,'testuser','2011-12-11 13:44:38','','login','','','','',''),(1613,'testuser','2011-12-11 13:48:41','','login','','','','',''),(1614,'testuser','2011-12-11 13:54:53','','login','','','','',''),(1615,'testuser','2011-12-11 14:01:02','','login','','','','',''),(1616,'testuser','2011-12-11 14:05:15','','login','','','','',''),(1617,'testuser','2011-12-11 15:38:21','','login','','','','',''),(1618,'testuser','2011-12-12 10:13:10','','login','','','','',''),(1619,'testuser','2011-12-12 10:38:56','','login','','','','',''),(1620,'testuser','2011-12-12 11:33:12','','logout','','','','',''),(1621,'testuser','2011-12-12 11:38:53','','login','','','','',''),(1622,'testuser','2011-12-12 11:44:01','','logout','','','','',''),(1623,'testuser','2011-12-12 13:25:33','','login','','','','',''),(1624,'testuser','2011-12-12 13:59:40','','logout','','','','',''),(1625,'testuser','2011-12-12 14:31:17','','login','','','','',''),(1626,'testuser','2011-12-15 11:44:23','','login','','','','',''),(1627,'testuser','2011-12-15 11:49:37','','logout','','','','',''),(1628,'testuser','2011-12-15 11:50:22','','login','','','','',''),(1629,'testuser','2011-12-15 11:52:55','','login','','','','',''),(1630,'testuser','2011-12-15 12:27:57','','login','','','','',''),(1631,'testuser','2011-12-15 15:39:13','','logout','','','','',''),(1632,'testuser','2011-12-15 15:44:05','','login','','','','',''),(1633,'testuser','2011-12-15 15:47:11','','logout','','','','',''),(1634,'testuser','2012-01-03 10:15:45','','login','','','','',''),(1635,'testuser','2012-01-03 15:20:49','','logout','','','','',''),(1636,'testuser','2012-01-03 15:22:05','','login','','','','',''),(1637,'testuser','2012-01-03 15:22:16','','logout','','','','',''),(1638,'testuser','2012-01-03 15:22:26','','login','','','','',''),(1639,'testuser','2012-01-03 15:25:51','','logout','','','','',''),(1640,'testuser','2012-01-03 15:32:30','','login','','','','',''),(1641,'testuser','2012-01-03 15:32:45','','logout','','','','',''),(1642,'testuser','2012-01-03 15:32:53','','login','','','','',''),(1643,'testuser','2012-01-03 17:07:15','','login','','','','',''),(1644,'testuser','2012-01-03 17:29:26','','print','','','','','TecanScanLink'),(1645,'testuser','2012-01-03 17:29:32','','logout','','','','',''),(1646,'testuser','2012-01-03 17:34:25','','login','','','','',''),(1647,'testuser','2012-01-04 10:16:11','','login','','','','',''),(1648,'testuser','2012-01-04 11:01:24','','logout','','','','',''),(1649,'testuser','2012-01-04 11:03:47','','login','','','','',''),(1650,'testuser','2012-01-04 11:05:56','','logout','','','','',''),(1651,'testuser','2012-01-04 11:06:14','','login','','','','',''),(1652,'testuser','2012-01-04 11:08:02','','logout','','','','',''),(1653,'testuser','2012-01-04 11:59:49','','login','','','','',''),(1654,'testuser','2012-01-04 12:02:59','','logout','','','','',''),(1655,'testuser','2012-01-04 15:06:46','','login','','','','',''),(1656,'testuser','2012-01-04 16:09:34','','logout','','','','',''),(1657,'testuser','2012-01-04 16:11:57','','login','','','','',''),(1658,'testuser','2012-01-04 17:17:39','','login','','','','',''),(1659,'testuser','2012-01-04 17:25:45','','logout','','','','',''),(1660,'testuser','2012-01-04 17:34:38','','login','','','','',''),(1661,'testuser','2012-01-04 17:50:14','','login','','','','',''),(1662,'testuser','2012-01-04 18:00:49','','login','','','','',''),(1663,'testuser','2012-01-04 18:23:08','','login','','','','',''),(1664,'testuser','2012-01-04 18:41:17','','login','','','','',''),(1665,'testuser','2012-01-04 18:48:23','','login','','','','',''),(1666,'testuser','2012-01-04 20:02:54','','login','','','','',''),(1667,'testuser','2012-01-05 10:57:54','','login','','','','',''),(1668,'testuser','2012-01-05 11:16:11','','login','','','','',''),(1669,'testuser','2012-01-05 11:33:02','','login','','','','',''),(1670,'testuser','2012-01-05 11:37:09','','logout','','','','',''),(1671,'testuser','2012-01-05 11:40:09','','login','','','','',''),(1672,'testuser','2012-01-05 12:09:47','','login','','','','',''),(1673,'testuser','2012-01-05 12:42:36','','login','','','','',''),(1674,'testuser','2012-01-05 14:51:15','','login','','','','',''),(1675,'testuser','2012-01-05 14:58:28','','login','','','','',''),(1676,'testuser','2012-01-05 15:47:24','','login','','','','',''),(1677,'testuser','2012-01-05 15:49:15','','logout','','','','',''),(1678,'testuser','2012-01-05 16:25:17','','login','','','','',''),(1679,'testuser','2012-01-05 16:46:47','','login','','','','',''),(1680,'testuser','2012-01-05 16:51:59','','logout','','','','',''),(1681,'testuser','2012-01-05 17:19:42','','login','','','','',''),(1682,'testuser','2012-01-05 17:42:26','','logout','','','','',''),(1683,'testuser','2012-01-05 17:46:27','','login','','','','',''),(1684,'testuser','2012-01-05 18:09:45','','login','','','','',''),(1685,'testuser','2012-01-05 18:12:48','','logout','','','','',''),(1686,'testuser','2012-01-05 20:49:39','','login','','','','',''),(1687,'testuser','2012-01-05 21:25:32','','print','','','','','TecanScanLink'),(1688,'testuser','2012-01-05 21:55:10','','logout','','','','',''),(1689,'testuser','2012-01-06 10:52:42','','login','','','','',''),(1690,'testuser','2012-01-06 11:46:36','','logout','','','','',''),(1691,'testuser','2012-01-06 12:59:14','','login','','','','',''),(1692,'testuser','2012-01-06 13:33:43','','print','','','','','TecanScanLink'),(1693,'testuser','2012-01-06 13:39:47','','logout','','','','',''),(1694,'testuser','2012-01-06 16:32:34','','login','','','','',''),(1695,'testuser','2012-01-06 16:40:47','','logout','','','','',''),(1696,'testuser','2012-01-06 16:45:07','','login','','','','',''),(1697,'testuser','2012-01-09 09:35:51','','login','','','','',''),(1698,'testuser','2012-01-09 12:18:10','','login','','','','',''),(1699,'testuser','2012-01-09 12:20:39','','logout','','','','',''),(1700,'testuser','2012-01-09 12:34:11','','login','','','','',''),(1701,'testuser','2012-01-09 12:43:06','','login','','','','',''),(1702,'testuser','2012-01-09 13:47:07','','logout','','','','',''),(1703,'testuser','2012-01-09 13:55:35','','login','','','','',''),(1704,'testuser','2012-01-09 14:25:42','','login','','','','',''),(1705,'testuser','2012-01-09 14:27:15','Repository1','insert','','','','','ProcessingEvent'),(1706,'testuser','2012-01-09 16:06:46','','logout','','','','',''),(1707,'testuser','2012-01-09 17:01:32','','login','','','','',''),(1708,'testuser','2012-01-09 18:06:05','','login','','','','',''),(1709,'testuser','2012-01-09 18:15:42','','login','','','','',''),(1710,'testuser','2012-01-09 18:31:32','','login','','','','',''),(1711,'testuser','2012-01-09 18:38:54','','logout','','','','',''),(1712,'testuser','2012-01-09 18:46:12','','login','','','','',''),(1713,'testuser','2012-01-09 18:48:51','','logout','','','','',''),(1714,'testuser','2012-01-09 18:57:56','','login','','','','',''),(1715,'testuser','2012-01-09 19:01:25','Repository1','insert','','','','Source Specimens: 0, Worksheet: 222_ONE','ProcessingEvent'),(1716,'testuser','2012-01-09 19:02:23','','logout','','','','',''),(1717,'testuser','2012-01-10 09:47:14','','login','','','','',''),(1718,'testuser','2012-01-10 09:55:26','','login','','','','',''),(1719,'testuser','2012-01-10 09:56:30','Repository1','insert','','','','Source Specimens: 0, Worksheet: 1q1q_ONE','ProcessingEvent'),(1720,'testuser','2012-01-10 10:19:46','','logout','','','','',''),(1721,'testuser','2012-01-10 10:23:53','','login','','','','',''),(1722,'testuser','2012-01-10 10:26:43','Repository1','insert','','','','Source Specimens: 0, Worksheet: efef_ONE','ProcessingEvent'),(1723,'testuser','2012-01-10 11:35:03','','login','','','','',''),(1724,'testuser','2012-01-10 12:31:21','','logout','','','','',''),(1725,'testuser','2012-01-10 12:31:49','','login','','','','',''),(1726,'testuser','2012-01-10 12:32:07','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1727,'testuser','2012-01-10 12:32:28','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1728,'testuser','2012-01-10 14:58:09','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1729,'testuser','2012-01-10 14:58:17','Repository1','edit','C01','','','CollectionEvent EDIT, visit:1, specimens:4','CollectionEvent'),(1730,'testuser','2012-01-10 19:35:14','','logout','','','','',''),(1731,'testuser','2012-01-10 20:51:13','','login','','','','',''),(1732,'testuser','2012-01-10 20:53:13','Repository1','insert','','','','Source Specimens: 0, Worksheet: 3_ONE','ProcessingEvent'),(1733,'testuser','2012-01-10 21:31:07','','login','','','','',''),(1734,'testuser','2012-01-10 21:33:23','Repository1','insert','','','','Source Specimens: 0, Worksheet: 12_ONE','ProcessingEvent'),(1735,'testuser','2012-01-10 21:38:20','','login','','','','',''),(1736,'testuser','2012-01-10 21:40:01','Repository1','insert','','','','Source Specimens: 0, Worksheet: 5432_ONE','ProcessingEvent'),(1737,'testuser','2012-01-10 21:53:14','','logout','','','','',''),(1738,'testuser','2012-01-10 21:59:13','','login','','','','',''),(1739,'testuser','2012-01-10 22:00:42','Repository1','insert','','','','Source Specimens: 0, Worksheet: 1232_ONE','ProcessingEvent'),(1740,'testuser','2012-01-10 22:14:16','','login','','','','',''),(1741,'testuser','2012-01-10 22:15:41','Repository1','insert','','','','Source Specimens: 0, Worksheet: 1232_ONE','ProcessingEvent'),(1742,'testuser','2012-01-10 22:16:37','','insert','C01','C014AAAA','','','Specimen'),(1743,'testuser','2012-01-10 23:04:05','','login','','','','',''),(1744,'testuser','2012-01-10 23:08:01','','logout','','','','',''),(1745,'testuser','2012-01-12 16:11:07','','login','','','','',''),(1746,'testuser','2012-01-12 16:28:02','','login','','','','',''),(1747,'testuser','2012-01-13 10:55:24','','login','','','','',''),(1748,'testuser','2012-01-16 13:43:54','','login','','','','',''),(1749,'testuser','2012-01-16 13:57:09','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1750,'testuser','2012-01-16 13:57:17','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1751,'testuser','2012-01-16 13:58:52','Repository1','select','C01','C01001','','Specimen LOOKUP','Specimen'),(1752,'testuser','2012-01-16 13:59:29','Repository1','select','C01','C010003','','Specimen LOOKUP','Specimen'),(1753,'testuser','2012-01-16 14:01:02','Repository1','select','C01','C010003','','Specimen LOOKUP','Specimen'),(1754,'testuser','2012-01-16 14:03:26','Repository1','select','C01','C010001','','Specimen LOOKUP','Specimen'),(1755,'testuser','2012-01-16 14:04:15','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1756,'testuser','2012-01-16 14:05:14','Repository1','edit','C01','C01001','','Specimen EDIT','Specimen'),(1757,'testuser','2012-01-16 14:05:45','Repository1','update','C01','C01001','','','Specimen'),(1758,'testuser','2012-01-16 14:05:48','Repository1','select','C01','C01001','','Specimen LOOKUP','Specimen'),(1759,'testuser','2012-01-16 14:08:25','Repository1','select','C01','C010001','','Specimen LOOKUP','Specimen'),(1760,'testuser','2012-01-16 14:09:33','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1761,'testuser','2012-01-16 14:12:58','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1762,'testuser','2012-01-16 14:21:21','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1763,'testuser','2012-01-16 14:23:47','Repository1','select','C01','C014AAAA','','Specimen LOOKUP','Specimen'),(1764,'testuser','2012-01-16 14:24:33','Repository1','select','C01','C010001','','Specimen LOOKUP','Specimen'),(1765,'testuser','2012-01-16 14:29:09','Repository1','select','C01','C010003','','Specimen LOOKUP','Specimen'),(1766,'testuser','2012-01-16 14:29:19','Repository1','edit','C01','C010003','','Specimen EDIT','Specimen'),(1767,'testuser','2012-01-16 14:29:30','Repository1','select','C01','C010003','','Specimen LOOKUP','Specimen'),(1768,'testuser','2012-01-16 14:31:02','Repository1','select','C01','C010002','','Specimen LOOKUP','Specimen'),(1769,'testuser','2012-01-16 14:31:57','Repository1','select','C01','C014AAAA','','Specimen LOOKUP','Specimen'),(1770,'testuser','2012-01-16 14:33:00','Repository1','select','C01','343434','','Specimen LOOKUP','Specimen'),(1771,'testuser','2012-01-16 14:44:15','','logout','','','','',''),(1772,'testuser','2012-01-18 18:37:16','','login','','','','',''),(1773,'testuser','2012-01-18 18:37:42','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1774,'testuser','2012-01-18 18:37:55','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1775,'testuser','2012-01-18 18:39:06','Repository1','select','C01','C014AAAA','','Specimen LOOKUP','Specimen'),(1776,'testuser','2012-01-18 18:39:43','','logout','','','','',''),(1777,'testuser','2012-01-23 10:03:06','','login','','','','',''),(1778,'testuser','2012-01-23 10:03:24','','login','','','','',''),(1779,'testuser','2012-01-23 11:04:46','','login','','','','',''),(1780,'testuser','2012-01-23 11:11:58','','logout','','','','',''),(1781,'testuser','2012-01-23 11:23:24','','login','','','','',''),(1782,'testuser','2012-01-23 11:26:02','Repository1','insert','','','','Source Specimens: 0, Worksheet: 3244_PROCC010001','ProcessingEvent'),(1783,'testuser','2012-01-23 11:26:20','Repository1','insert','C01','C014BBBA','','','Specimen'),(1784,'testuser','2012-01-23 11:30:17','','logout','','','','',''),(1785,'testuser','2012-01-24 11:41:28','','login','','','','',''),(1786,'testuser','2012-01-24 19:06:19','','login','','','','',''),(1787,'testuser','2012-01-24 19:10:57','','login','','','','',''),(1788,'testuser','2012-01-24 19:12:06','Repository1','insert','','','','Source Specimens: 0, Worksheet: ftght_PROCC010001','ProcessingEvent'),(1789,'testuser','2012-01-24 19:12:22','Repository1','insert','C01','C014CCCA','','','Specimen'),(1790,'testuser','2012-01-24 19:26:02','Repository1','insert','','','','Source Specimens: 0, Worksheet: dfgwetgre_PROCC010001','ProcessingEvent'),(1791,'testuser','2012-01-24 19:32:06','Repository1','insert','C01','C014DDDA','','','Specimen'),(1792,'testuser','2012-01-24 19:36:08','','login','','','','',''),(1793,'testuser','2012-01-24 19:37:15','Repository1','insert','','','','Source Specimens: 0, Worksheet: ttttt_PROCC010001','ProcessingEvent'),(1794,'testuser','2012-01-24 19:44:52','','login','','','','',''),(1795,'testuser','2012-01-24 19:45:56','Repository1','insert','','','','Source Specimens: 0, Worksheet: eee_PROCC010001','ProcessingEvent'),(1796,'testuser','2012-01-24 20:17:05','','logout','','','','',''),(1797,'testuser','2012-01-26 12:09:38','','login','','','','',''),(1798,'testuser','2012-01-26 12:15:00','Repository1','delete','','','','Source Specimens: 0, Worksheet: eee_PROCC010001','ProcessingEvent'),(1799,'testuser','2012-01-26 12:17:10','Repository1','update','C01','C014DDDA','','','Specimen'),(1800,'testuser','2012-01-26 12:17:10','Repository1','delete','','','','Source Specimens: 1, Worksheet: dfgwetgre_PROCC010001','ProcessingEvent'),(1801,'testuser','2012-01-26 12:21:39','Repository1','select','C01','C014CCCA','','Specimen LOOKUP','Specimen'),(1802,'testuser','2012-01-26 12:21:53','Repository1','edit','C01','C014CCCA','','Specimen EDIT','Specimen'),(1803,'testuser','2012-01-26 12:22:09','Repository1','select','C01','C014CCCA','','Specimen LOOKUP','Specimen'),(1804,'testuser','2012-01-26 12:24:40','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1805,'testuser','2012-01-26 12:24:41','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1806,'testuser','2012-01-26 12:24:56','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1807,'testuser','2012-01-26 12:25:19','Repository1','select','C01','C010004','','Specimen LOOKUP','Specimen'),(1808,'testuser','2012-01-26 12:33:35','Repository1','select','C01','C010001','','Specimen LOOKUP','Specimen'),(1809,'testuser','2012-01-26 12:35:30','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1810,'testuser','2012-01-26 12:35:37','Repository1','edit','C01','C010003','','Specimen EDIT','Specimen'),(1811,'testuser','2012-01-26 12:38:10','Repository1','select','C01','C010003','','Specimen LOOKUP','Specimen'),(1812,'testuser','2012-01-26 12:39:08','','logout','','','','',''),(1813,'testuser','2012-01-26 16:32:31','','login','','','','',''),(1814,'testuser','2012-01-26 16:37:55','Repository1','insert','','','','Source Specimens: 0, Worksheet: 123_PROCC010001','ProcessingEvent'),(1815,'testuser','2012-01-26 17:05:20','','login','','','','',''),(1816,'testuser','2012-01-26 17:06:24','Repository1','insert','','','','Source Specimens: 0, Worksheet: eter_PROCC010001','ProcessingEvent'),(1817,'testuser','2012-01-26 17:48:14','','login','','','','',''),(1818,'testuser','2012-01-26 17:49:22','Repository1','insert','','','','Source Specimens: 0, Worksheet: eeee_PROCC010001','ProcessingEvent'),(1819,'testuser','2012-01-26 17:49:27','Repository1','insert','C01','C014FFFA','','','Specimen'),(1820,'testuser','2012-01-26 17:55:24','','logout','','','','',''),(1821,'testuser','2012-01-27 16:39:11','','login','','','','',''),(1822,'testuser','2012-01-27 16:40:27','Repository1','insert','','','','Source Specimens: 0, Worksheet: ert_PROCC010001','ProcessingEvent'),(1823,'testuser','2012-01-27 17:02:46','','login','','','','',''),(1824,'testuser','2012-01-27 17:03:40','Repository1','insert','','','','Source Specimens: 0, Worksheet: 34_PROCC010001','ProcessingEvent'),(1825,'testuser','2012-01-27 17:35:46','','login','','','','',''),(1826,'testuser','2012-01-27 17:36:49','Repository1','insert','','','','Source Specimens: 0, Worksheet: fgh_PROCC010001','ProcessingEvent'),(1827,'testuser','2012-01-27 17:50:19','','login','','','','',''),(1828,'testuser','2012-01-27 17:51:20','Repository1','insert','','','','Source Specimens: 0, Worksheet: eee_PROCC010001','ProcessingEvent'),(1829,'testuser','2012-01-27 17:59:14','','login','','','','',''),(1830,'testuser','2012-01-27 18:00:09','Repository1','insert','','','','Source Specimens: 0, Worksheet: qqq_PROCC010001','ProcessingEvent'),(1831,'testuser','2012-01-27 18:01:26','','logout','','','','',''),(1832,'testuser','2012-01-30 12:05:19','','login','','','','',''),(1833,'testuser','2012-01-30 12:07:27','Repository1','insert','','','','Source Specimens: 0, Worksheet: fgfg_PROCC010001','ProcessingEvent'),(1834,'testuser','2012-01-30 12:10:17','Repository1','insert','','','','Source Specimens: 0, Worksheet: llll_PROCC010001','ProcessingEvent'),(1835,'testuser','2012-01-30 12:13:14','Repository1','insert','','','','Source Specimens: 0, Worksheet: gjdfjf_PROCC010001','ProcessingEvent'),(1836,'testuser','2012-01-30 12:15:35','Repository1','insert','','','','Source Specimens: 0, Worksheet: more_PROCC010001','ProcessingEvent'),(1837,'testuser','2012-01-30 14:53:36','','logout','','','','',''),(1838,'testuser','2012-01-30 14:58:27','','login','','','','',''),(1839,'testuser','2012-01-30 15:00:53','Repository1','insert','','','','Source Specimens: 0, Worksheet: t_PROCC010001','ProcessingEvent'),(1840,'testuser','2012-01-30 15:07:53','','logout','','','','',''),(1841,'testuser','2012-01-30 15:17:11','','login','','','','',''),(1842,'testuser','2012-01-30 15:32:25','','login','','','','',''),(1843,'testuser','2012-01-30 15:36:50','','logout','','','','',''),(1844,'testuser','2012-01-30 15:46:08','','login','','','','',''),(1845,'testuser','2012-01-30 17:17:22','','login','','','','',''),(1846,'testuser','2012-01-30 17:32:04','','login','','','','',''),(1847,'testuser','2012-01-31 13:43:16','','login','','','','',''),(1848,'testuser','2012-01-31 14:04:28','','logout','','','','',''),(1849,'testuser','2012-01-31 14:07:04','','login','','','','',''),(1850,'testuser','2012-01-31 15:25:28','','login','','','','',''),(1851,'testuser','2012-01-31 15:33:28','','login','','','','',''),(1852,'testuser','2012-01-31 16:20:48','','login','','','','',''),(1853,'testuser','2012-01-31 16:41:29','','login','','','','',''),(1854,'testuser','2012-01-31 16:59:03','','login','','','','',''),(1855,'testuser','2012-01-31 17:01:06','','logout','','','','',''),(1856,'testuser','2012-01-31 18:20:54','','login','','','','',''),(1857,'testuser','2012-01-31 18:26:22','','login','','','','',''),(1858,'testuser','2012-01-31 18:28:01','Repository1','select','C01','C010001','','Specimen LOOKUP','Specimen'),(1859,'testuser','2012-01-31 18:28:32','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1860,'testuser','2012-01-31 18:28:39','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1861,'testuser','2012-01-31 18:29:31','Repository1','select','C01','C01001','','Specimen LOOKUP','Specimen'),(1862,'testuser','2012-01-31 18:29:45','Repository1','select','C01','C01001','','Specimen LOOKUP','Specimen'),(1863,'testuser','2012-01-31 18:30:15','Repository1','edit','C01','C01001','','Specimen EDIT','Specimen'),(1864,'testuser','2012-01-31 18:30:49','Repository1','select','C01','C010001','','Specimen LOOKUP','Specimen'),(1865,'testuser','2012-01-31 18:41:15','','logout','','','','',''),(1866,'testuser','2012-01-31 18:55:28','','login','','','','',''),(1867,'testuser','2012-01-31 18:56:17','Repository1','select','C01','C01001','','Specimen LOOKUP','Specimen'),(1868,'testuser','2012-01-31 19:08:38','','logout','','','','',''),(1869,'testuser','2012-01-31 19:46:23','','login','','','','',''),(1870,'testuser','2012-01-31 19:47:10','Repository1','select','C01','C01001','','Specimen LOOKUP','Specimen'),(1871,'testuser','2012-02-01 11:26:22','','login','','','','',''),(1872,'testuser','2012-02-01 11:32:38','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1873,'testuser','2012-02-01 11:36:25','','login','','','','',''),(1874,'testuser','2012-02-01 11:37:14','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1875,'testuser','2012-02-01 11:45:09','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1876,'testuser','2012-02-01 11:48:46','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1877,'testuser','2012-02-01 11:49:32','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1878,'testuser','2012-02-01 11:51:49','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1879,'testuser','2012-02-01 16:49:37','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1880,'testuser','2012-02-01 16:51:52','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1881,'testuser','2012-02-01 16:54:09','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1882,'testuser','2012-02-01 17:06:42','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1883,'testuser','2012-02-01 17:09:58','Repository1','edit','C01','C01001','','Specimen EDIT','Specimen'),(1884,'testuser','2012-02-01 17:11:08','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1885,'testuser','2012-02-01 17:14:54','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1886,'testuser','2012-02-01 17:15:55','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1887,'testuser','2012-02-01 17:22:02','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1888,'testuser','2012-02-01 17:23:07','Repository1','select','C01','C010003','','Specimen LOOKUP','Specimen'),(1889,'testuser','2012-02-01 17:24:30','Repository1','edit','C01','C010003','','Specimen EDIT','Specimen'),(1890,'testuser','2012-02-01 17:25:03','Repository1','select','C01','C010003','','Specimen LOOKUP','Specimen'),(1891,'testuser','2012-02-01 17:45:42','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1892,'testuser','2012-02-01 18:05:31','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1893,'testuser','2012-02-01 18:09:39','Repository1','select','C01','C010004','','Specimen LOOKUP','Specimen'),(1894,'testuser','2012-02-01 18:10:57','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1895,'testuser','2012-02-01 18:21:46','Repository1','select','C01','C010004','','Specimen LOOKUP','Specimen'),(1896,'testuser','2012-02-01 19:16:07','','logout','','','','',''),(1897,'testuser','2012-02-02 10:19:08','','login','','','','',''),(1898,'testuser','2012-02-02 10:19:26','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1899,'testuser','2012-02-02 10:19:37','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1900,'testuser','2012-02-02 10:19:52','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1901,'testuser','2012-02-02 10:20:59','','logout','','','','',''),(1902,'testuser','2012-02-02 10:23:30','','login','','','','',''),(1903,'testuser','2012-02-02 10:23:51','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1904,'testuser','2012-02-02 10:23:57','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1905,'testuser','2012-02-02 10:24:06','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1906,'testuser','2012-02-02 10:38:50','','logout','','','','',''),(1907,'testuser','2012-02-02 10:40:26','','login','','','','',''),(1908,'testuser','2012-02-02 10:40:50','Repository1','select','C01','','','Patient LOOKUP','Patient'),(1909,'testuser','2012-02-02 10:40:58','Repository1','select','C01','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1910,'testuser','2012-02-02 10:41:06','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1911,'testuser','2012-02-02 11:01:45','Repository1','select','C01','C014FFFA','','Specimen LOOKUP','Specimen'),(1912,'testuser','2012-02-02 11:02:15','Repository1','select','C01','C01001','','Specimen LOOKUP','Specimen'),(1913,'testuser','2012-02-02 11:03:23','Repository1','select','C01','C010001','','Specimen LOOKUP','Specimen'),(1914,'testuser','2012-02-02 11:04:59','','logout','','','','',''),(1915,'testuser','2012-02-03 09:14:57','','login','','','','',''),(1916,'testuser','2012-02-05 13:07:59','','login','','','','',''),(1917,'testuser','2012-02-05 13:14:23','','insert','Patient02','','','','Patient'),(1918,'testuser','2012-02-05 13:14:25','Repository1','select','Patient02','','','Patient LOOKUP','Patient'),(1919,'testuser','2012-02-05 13:19:58','','insert','Patient02','','','visit:1, specimens:8','CollectionEvent'),(1920,'testuser','2012-02-05 13:20:01','Repository1','select','Patient02','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1921,'testuser','2012-02-05 13:37:15','Repository1','edit','C01','C010001','','Specimen EDIT','Specimen'),(1922,'testuser','2012-02-05 13:37:30','Repository1','select','C01','C010001','','Specimen LOOKUP','Specimen'),(1923,'testuser','2012-02-05 14:20:05','','logout','','','','',''),(1924,'testuser','2012-02-05 15:20:37','','login','','','','',''),(1925,'testuser','2012-02-07 11:16:44','','login','','','','',''),(1926,'testuser','2012-02-07 11:17:58','Repository1','select','Patient02','','','Patient LOOKUP','Patient'),(1927,'testuser','2012-02-07 11:18:14','Repository1','select','Patient02','','','CollectionEvent LOOKUP, visit:1, specimens:4','CollectionEvent'),(1928,'testuser','2012-02-07 11:18:20','Repository1','edit','Patient02','','','CollectionEvent EDIT, visit:1, specimens:4','CollectionEvent'),(1929,'testuser','2012-02-07 11:20:01','','update','Patient02','','','visit:1, specimens:5','CollectionEvent'),(1930,'testuser','2012-02-07 11:20:03','Repository1','select','Patient02','','','CollectionEvent LOOKUP, visit:1, specimens:5','CollectionEvent'),(1931,'testuser','2012-02-07 11:20:48','','logout','','','','','');
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `origin_info`
--

DROP TABLE IF EXISTS `origin_info`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `origin_info` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `RECEIVER_SITE_ID` int(11) default NULL,
  `SHIPMENT_INFO_ID` int(11) default NULL,
  `CENTER_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `SHIPMENT_INFO_ID` (`SHIPMENT_INFO_ID`),
  KEY `FKE92E7A275598FA35` (`RECEIVER_SITE_ID`),
  KEY `FKE92E7A2792FAA705` (`CENTER_ID`),
  KEY `FKE92E7A27F59D873A` (`SHIPMENT_INFO_ID`),
  CONSTRAINT `FKE92E7A275598FA35` FOREIGN KEY (`RECEIVER_SITE_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKE92E7A2792FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKE92E7A27F59D873A` FOREIGN KEY (`SHIPMENT_INFO_ID`) REFERENCES `shipment_info` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `origin_info`
--

LOCK TABLES `origin_info` WRITE;
/*!40000 ALTER TABLE `origin_info` DISABLE KEYS */;
INSERT INTO `origin_info` VALUES (1,0,NULL,NULL,1),(2,0,NULL,NULL,1),(3,0,NULL,NULL,1),(4,0,NULL,NULL,1),(5,0,NULL,NULL,1),(6,0,NULL,NULL,1),(8,0,NULL,NULL,1),(11,0,NULL,NULL,1),(12,0,NULL,NULL,2),(13,0,NULL,NULL,1),(14,0,2,10,1),(15,0,2,11,1),(16,0,NULL,NULL,1),(17,0,NULL,NULL,1),(18,0,NULL,NULL,1),(19,0,NULL,NULL,1),(20,0,NULL,NULL,1),(21,0,NULL,NULL,1),(22,0,2,14,1),(23,1,2,15,1),(24,0,NULL,NULL,2),(25,0,NULL,NULL,1),(26,0,NULL,NULL,1),(27,0,NULL,NULL,1),(28,0,NULL,NULL,1),(29,0,NULL,NULL,1),(30,0,NULL,NULL,1),(31,0,NULL,NULL,1),(32,0,2,18,1),(33,0,NULL,NULL,1),(34,0,NULL,NULL,1),(36,0,NULL,NULL,1),(38,0,NULL,NULL,1),(39,0,2,24,1),(40,0,NULL,NULL,1),(42,0,NULL,NULL,2),(44,0,NULL,NULL,2),(46,0,NULL,NULL,1),(48,0,NULL,NULL,1),(50,0,NULL,NULL,1),(51,0,NULL,NULL,1),(53,0,NULL,NULL,1),(55,0,NULL,NULL,1),(57,0,NULL,NULL,1),(58,0,NULL,NULL,1),(60,0,NULL,NULL,1),(62,0,NULL,NULL,1),(64,0,NULL,NULL,1),(65,0,NULL,NULL,1),(66,0,NULL,NULL,1),(68,0,NULL,NULL,1),(69,0,NULL,NULL,1),(71,0,NULL,NULL,1),(72,0,NULL,NULL,1),(74,0,NULL,NULL,1),(75,0,NULL,NULL,2),(76,0,NULL,NULL,1),(77,0,NULL,NULL,6),(78,0,NULL,NULL,6),(79,0,NULL,NULL,6),(80,0,NULL,NULL,6),(81,0,NULL,NULL,6),(82,0,NULL,NULL,6),(83,0,NULL,NULL,6),(84,0,NULL,NULL,6),(85,0,NULL,NULL,6),(86,0,NULL,NULL,6),(87,0,NULL,NULL,6),(88,0,NULL,NULL,6),(89,0,NULL,NULL,6),(90,0,NULL,NULL,6),(91,0,NULL,NULL,6),(92,0,NULL,NULL,6),(93,0,NULL,NULL,6),(94,0,NULL,NULL,6),(95,0,NULL,NULL,6),(96,0,NULL,NULL,6),(97,0,NULL,NULL,6),(98,0,NULL,NULL,6),(99,0,NULL,NULL,6);
/*!40000 ALTER TABLE `origin_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `patient` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `PNUMBER` varchar(100) collate latin1_general_cs NOT NULL,
  `CREATED_AT` datetime default NULL,
  `STUDY_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `PNUMBER` (`PNUMBER`),
  KEY `FKFB9F76E5F2A2464F` (`STUDY_ID`),
  KEY `NUMBER_IDX` (`PNUMBER`),
  CONSTRAINT `FKFB9F76E5F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
INSERT INTO `patient` VALUES (2,0,'P10','2011-10-12 12:39:00',1),(4,0,'P01','2011-10-13 13:49:00',1),(5,0,'P100','2011-10-13 13:51:00',1),(6,0,'P101','2011-10-14 09:48:00',1),(22,0,'PPP','2011-10-17 14:53:00',1),(27,0,'Pat100','2011-10-18 13:45:00',1),(32,0,'Patient001','2011-10-21 09:53:00',1),(33,0,'Patient002','2011-10-21 09:58:00',1),(34,0,'Patient003','2011-10-21 10:01:00',1),(37,0,'C01','2011-10-28 11:29:00',2),(38,0,'Patient02','2012-02-05 13:13:00',2);
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `printed_ss_inv_item`
--

DROP TABLE IF EXISTS `printed_ss_inv_item`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `printed_ss_inv_item` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `TXT` varchar(15) collate latin1_general_cs default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `printed_ss_inv_item`
--

LOCK TABLES `printed_ss_inv_item` WRITE;
/*!40000 ALTER TABLE `printed_ss_inv_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `printed_ss_inv_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `printer_label_template`
--

DROP TABLE IF EXISTS `printer_label_template`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `printer_label_template` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(50) collate latin1_general_cs default NULL,
  `PRINTER_NAME` varchar(50) collate latin1_general_cs default NULL,
  `CONFIG_DATA` text collate latin1_general_cs,
  `JASPER_TEMPLATE_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  KEY `FKC6463C6AA4B878C8` (`JASPER_TEMPLATE_ID`),
  CONSTRAINT `FKC6463C6AA4B878C8` FOREIGN KEY (`JASPER_TEMPLATE_ID`) REFERENCES `jasper_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `printer_label_template`
--

LOCK TABLES `printer_label_template` WRITE;
/*!40000 ALTER TABLE `printer_label_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `printer_label_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processing_event`
--

DROP TABLE IF EXISTS `processing_event`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `processing_event` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `WORKSHEET` varchar(100) collate latin1_general_cs default NULL,
  `CREATED_AT` datetime NOT NULL,
  `COMMENT` text collate latin1_general_cs,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `CENTER_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK327B1E4EC449A4` (`ACTIVITY_STATUS_ID`),
  KEY `FK327B1E4E92FAA705` (`CENTER_ID`),
  KEY `CREATED_AT_IDX` (`CREATED_AT`),
  CONSTRAINT `FK327B1E4E92FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK327B1E4EC449A4` FOREIGN KEY (`ACTIVITY_STATUS_ID`) REFERENCES `activity_status` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `processing_event`
--

LOCK TABLES `processing_event` WRITE;
/*!40000 ALTER TABLE `processing_event` DISABLE KEYS */;
INSERT INTO `processing_event` VALUES (1,1,'1111','2011-10-21 16:11:04',NULL,1,1),(2,2,'1','2011-11-03 14:18:08','com1',1,6),(3,0,'123_ONE','2012-01-09 14:26:44',NULL,1,6),(4,0,'222_ONE','2012-01-09 18:58:47',NULL,1,6),(5,0,'1q1q_ONE','2012-01-10 09:56:16',NULL,1,6),(6,0,'efef_ONE','2012-01-10 10:26:26',NULL,1,6),(7,0,'1232_ONE','2012-01-10 22:15:41',NULL,1,6),(8,0,'3244_PROCC010001','2012-01-23 11:25:54','comment Y',1,6),(9,0,'ftght_PROCC010001','2012-01-24 19:12:06','yer',1,6),(10,0,'eter_PROCC010001','2012-01-26 17:06:24','retewrt',1,6),(11,0,'eeee_PROCC010001','2012-01-26 17:49:21','eeee',1,6),(12,0,'fgh_PROCC010001','2012-01-27 17:36:49','fgh',1,6),(13,0,'eee_PROCC010001','2012-01-27 17:51:20','eee',1,6),(14,0,'qqq_PROCC010001','2012-01-27 18:00:09','qqq',1,6),(15,0,'fgfg_PROCC010001','2012-01-30 12:07:27','xfgsd',1,6),(16,0,'llll_PROCC010001','2012-01-30 12:10:17','dd',1,6),(17,0,'gjdfjf_PROCC010001','2012-01-30 12:13:14','dd',1,6),(18,0,'more_PROCC010001','2012-01-30 12:15:35','dd',1,6),(19,0,'t_PROCC010001','2012-01-30 15:00:53','trt',1,6);
/*!40000 ALTER TABLE `processing_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_modifier`
--

DROP TABLE IF EXISTS `property_modifier`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `property_modifier` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` text collate latin1_general_cs,
  `PROPERTY_MODIFIER` text collate latin1_general_cs,
  `PROPERTY_TYPE_ID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK5DF9160157C0C3B0` (`PROPERTY_TYPE_ID`),
  CONSTRAINT `FK5DF9160157C0C3B0` FOREIGN KEY (`PROPERTY_TYPE_ID`) REFERENCES `property_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `property_modifier`
--

LOCK TABLES `property_modifier` WRITE;
/*!40000 ALTER TABLE `property_modifier` DISABLE KEYS */;
/*!40000 ALTER TABLE `property_modifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_type`
--

DROP TABLE IF EXISTS `property_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `property_type` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) collate latin1_general_cs default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `property_type`
--

LOCK TABLES `property_type` WRITE;
/*!40000 ALTER TABLE `property_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `property_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `report` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) collate latin1_general_cs default NULL,
  `DESCRIPTION` text collate latin1_general_cs,
  `USER_ID` int(11) default NULL,
  `IS_PUBLIC` bit(1) default NULL,
  `IS_COUNT` bit(1) default NULL,
  `ENTITY_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK8FDF493491CFD445` (`ENTITY_ID`),
  CONSTRAINT `FK8FDF493491CFD445` FOREIGN KEY (`ENTITY_ID`) REFERENCES `entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_column`
--

DROP TABLE IF EXISTS `report_column`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `report_column` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `POSITION` int(11) default NULL,
  `COLUMN_ID` int(11) NOT NULL,
  `PROPERTY_MODIFIER_ID` int(11) default NULL,
  `REPORT_ID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKF0B78C1BE9306A5` (`REPORT_ID`),
  KEY `FKF0B78C1C2DE3790` (`PROPERTY_MODIFIER_ID`),
  KEY `FKF0B78C1A946D8E8` (`COLUMN_ID`),
  CONSTRAINT `FKF0B78C1A946D8E8` FOREIGN KEY (`COLUMN_ID`) REFERENCES `entity_column` (`ID`),
  CONSTRAINT `FKF0B78C1BE9306A5` FOREIGN KEY (`REPORT_ID`) REFERENCES `report` (`ID`),
  CONSTRAINT `FKF0B78C1C2DE3790` FOREIGN KEY (`PROPERTY_MODIFIER_ID`) REFERENCES `property_modifier` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `report_column`
--

LOCK TABLES `report_column` WRITE;
/*!40000 ALTER TABLE `report_column` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_filter`
--

DROP TABLE IF EXISTS `report_filter`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `report_filter` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `POSITION` int(11) default NULL,
  `OPERATOR` int(11) default NULL,
  `ENTITY_FILTER_ID` int(11) NOT NULL,
  `REPORT_ID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK13D570E3445CEC4C` (`ENTITY_FILTER_ID`),
  KEY `FK13D570E3BE9306A5` (`REPORT_ID`),
  CONSTRAINT `FK13D570E3445CEC4C` FOREIGN KEY (`ENTITY_FILTER_ID`) REFERENCES `entity_filter` (`ID`),
  CONSTRAINT `FK13D570E3BE9306A5` FOREIGN KEY (`REPORT_ID`) REFERENCES `report` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `report_filter`
--

LOCK TABLES `report_filter` WRITE;
/*!40000 ALTER TABLE `report_filter` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_filter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_filter_value`
--

DROP TABLE IF EXISTS `report_filter_value`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `report_filter_value` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `POSITION` int(11) default NULL,
  `VALUE` text collate latin1_general_cs,
  `SECOND_VALUE` text collate latin1_general_cs,
  `REPORT_FILTER_ID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK691EF6F59FFD1CEE` (`REPORT_FILTER_ID`),
  CONSTRAINT `FK691EF6F59FFD1CEE` FOREIGN KEY (`REPORT_FILTER_ID`) REFERENCES `report_filter` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `report_filter_value`
--

LOCK TABLES `report_filter_value` WRITE;
/*!40000 ALTER TABLE `report_filter_value` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_filter_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request`
--

DROP TABLE IF EXISTS `request`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `request` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `SUBMITTED` datetime default NULL,
  `CREATED` datetime default NULL,
  `STATE` int(11) default NULL,
  `STUDY_ID` int(11) NOT NULL,
  `ADDRESS_ID` int(11) NOT NULL,
  `REQUESTER_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `ADDRESS_ID` (`ADDRESS_ID`),
  KEY `FK6C1A7E6FF2A2464F` (`STUDY_ID`),
  KEY `FK6C1A7E6F6AF2992F` (`ADDRESS_ID`),
  KEY `FK6C1A7E6F80AB67E` (`REQUESTER_ID`),
  CONSTRAINT `FK6C1A7E6F6AF2992F` FOREIGN KEY (`ADDRESS_ID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK6C1A7E6F80AB67E` FOREIGN KEY (`REQUESTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK6C1A7E6FF2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `request`
--

LOCK TABLES `request` WRITE;
/*!40000 ALTER TABLE `request` DISABLE KEYS */;
/*!40000 ALTER TABLE `request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request_specimen`
--

DROP TABLE IF EXISTS `request_specimen`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `request_specimen` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `STATE` int(11) default NULL,
  `CLAIMED_BY` varchar(50) collate latin1_general_cs default NULL,
  `AREQUEST_ID` int(11) NOT NULL,
  `SPECIMEN_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK579572D8D990A70` (`AREQUEST_ID`),
  KEY `FK579572D8EF199765` (`SPECIMEN_ID`),
  CONSTRAINT `FK579572D8D990A70` FOREIGN KEY (`AREQUEST_ID`) REFERENCES `request` (`ID`),
  CONSTRAINT `FK579572D8EF199765` FOREIGN KEY (`SPECIMEN_ID`) REFERENCES `specimen` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `request_specimen`
--

LOCK TABLES `request_specimen` WRITE;
/*!40000 ALTER TABLE `request_specimen` DISABLE KEYS */;
/*!40000 ALTER TABLE `request_specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment_info`
--

DROP TABLE IF EXISTS `shipment_info`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `shipment_info` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `RECEIVED_AT` datetime default NULL,
  `PACKED_AT` datetime default NULL,
  `WAYBILL` varchar(255) collate latin1_general_cs default NULL,
  `BOX_NUMBER` varchar(255) collate latin1_general_cs default NULL,
  `SHIPPING_METHOD_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK95BCA433DCA49682` (`SHIPPING_METHOD_ID`),
  KEY `WAYBILL_IDX` (`WAYBILL`),
  KEY `RECEIVED_AT_IDX` (`RECEIVED_AT`),
  CONSTRAINT `FK95BCA433DCA49682` FOREIGN KEY (`SHIPPING_METHOD_ID`) REFERENCES `shipping_method` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `shipment_info`
--

LOCK TABLES `shipment_info` WRITE;
/*!40000 ALTER TABLE `shipment_info` DISABLE KEYS */;
INSERT INTO `shipment_info` VALUES (1,3,'2011-10-13 13:53:38','2011-10-13 13:52:25',NULL,NULL,7),(2,3,'2011-10-13 13:53:58','2011-10-13 13:53:01',NULL,NULL,5),(3,3,'2011-10-14 09:34:31','2011-10-14 09:29:31',NULL,NULL,5),(4,3,'2011-10-14 09:35:01','2011-10-14 09:30:00',NULL,NULL,7),(7,3,'2011-10-14 09:36:45','2011-10-14 09:36:15',NULL,NULL,7),(8,3,'2011-10-14 10:24:50','2011-10-14 10:21:51',NULL,NULL,7),(9,3,'2011-10-14 10:25:00','2011-10-14 10:22:16',NULL,NULL,7),(10,0,'2011-10-14 10:22:29','2011-10-14 10:22:29',NULL,NULL,6),(11,0,'2011-10-14 10:22:51','2011-10-14 10:22:51',NULL,NULL,7),(12,3,'2011-10-17 09:20:06','2011-10-17 09:14:36',NULL,NULL,7),(13,3,'2011-10-17 09:20:27','2011-10-17 09:15:30',NULL,NULL,6),(14,0,'2011-10-17 09:15:40','2011-10-17 09:15:41',NULL,NULL,6),(15,1,'2011-10-17 09:16:13','2011-10-17 09:16:13',NULL,NULL,5),(16,3,'2011-10-17 09:42:38','2011-10-17 09:40:14',NULL,NULL,7),(17,3,'2011-10-19 13:05:15','2011-10-18 15:06:15',NULL,NULL,5),(18,0,'2011-10-18 16:15:04','2011-10-18 16:35:00',NULL,'',5),(19,3,'2011-10-19 13:10:45','2011-10-19 12:48:48',NULL,NULL,5),(21,3,'2011-10-19 13:20:26','2011-10-19 13:13:59',NULL,NULL,5),(23,3,'2011-10-20 11:06:06','2011-10-19 15:27:13',NULL,NULL,5),(24,0,'2011-10-19 15:27:14','2011-10-19 15:27:00',NULL,NULL,6),(25,3,'2011-10-20 10:58:38','2011-10-20 00:00:40',NULL,NULL,6),(26,3,'2011-10-20 11:37:31','2011-10-20 00:00:45',NULL,NULL,5),(27,4,'2011-10-20 14:16:52','2011-10-20 00:00:48',NULL,NULL,5),(29,3,'2011-10-20 14:27:03','2011-10-20 00:00:56',NULL,NULL,5),(30,6,'2011-10-20 14:31:42','2011-10-20 00:00:04',NULL,NULL,5),(33,3,'2011-10-20 15:20:25','2011-10-20 15:18:46',NULL,NULL,7),(34,3,'2011-10-20 15:20:52','2011-10-20 15:19:10',NULL,NULL,6),(35,3,'2011-10-20 16:16:19','2011-10-20 00:00:47',NULL,NULL,5),(38,3,'2011-10-20 16:51:20','2011-10-20 00:00:26',NULL,NULL,5),(39,1,NULL,NULL,NULL,NULL,6),(41,3,'2011-10-20 17:16:22','2011-10-20 00:00:00',NULL,NULL,5),(43,3,'2011-10-20 17:18:16','2011-10-20 00:00:00',NULL,NULL,5),(45,3,'2011-10-20 17:25:18','2011-10-20 00:00:00',NULL,NULL,5),(47,3,'2011-10-20 17:28:21','2011-10-20 00:00:00',NULL,NULL,5),(49,3,'2011-10-21 09:25:14','2011-10-21 00:00:00',NULL,NULL,5),(52,3,'2011-10-21 09:34:36','2011-10-21 00:00:00',NULL,NULL,5),(54,3,'2011-10-21 09:54:26','2011-10-21 00:00:00',NULL,NULL,5),(56,3,'2011-10-21 09:59:05','2011-10-21 00:00:00',NULL,NULL,5),(58,3,'2011-10-21 10:01:42','2011-10-21 00:00:00',NULL,NULL,5),(59,1,'2011-11-08 19:46:51','2011-11-08 19:45:38','2',NULL,7);
/*!40000 ALTER TABLE `shipment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment_temp_logger`
--

DROP TABLE IF EXISTS `shipment_temp_logger`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `shipment_temp_logger` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `HIGH_TEMPERATURE` int(11) default NULL,
  `LOW_TEMPERATURE` int(11) default NULL,
  `TEMPERATURE_RESULT` bit(1) default NULL,
  `MINUTES_ABOVE_MAX` int(11) default NULL,
  `MINUTES_BELOW_MAX` int(11) default NULL,
  `REPORT` text collate latin1_general_cs,
  `DEVICE_ID` text collate latin1_general_cs,
  `SHIPMENT_INFO_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `SHIPMENT_INFO_ID` (`SHIPMENT_INFO_ID`),
  KEY `FK1DF89D36F59D873A` (`SHIPMENT_INFO_ID`),
  CONSTRAINT `FK1DF89D36F59D873A` FOREIGN KEY (`SHIPMENT_INFO_ID`) REFERENCES `shipment_info` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `shipment_temp_logger`
--

LOCK TABLES `shipment_temp_logger` WRITE;
/*!40000 ALTER TABLE `shipment_temp_logger` DISABLE KEYS */;
INSERT INTO `shipment_temp_logger` VALUES (1,3,45,43,'',NULL,NULL,NULL,'44',2),(2,3,6,222,'\0',NULL,NULL,NULL,'33',4),(4,3,77,55,'',NULL,NULL,NULL,'66',7),(5,3,66,99,'\0',NULL,NULL,NULL,'55',9),(6,0,56,53,'',NULL,NULL,NULL,'55',11),(7,3,23,22,'',32,1,NULL,'33',13),(8,1,3,12,'\0',NULL,NULL,NULL,'33',15),(9,3,675454,5,'\0',NULL,NULL,NULL,'44',16),(10,3,55,55,'\0',NULL,NULL,NULL,'33',17),(11,0,45,43,'\0',NULL,NULL,NULL,'44',18),(12,3,33,3,'\0',NULL,NULL,NULL,'33',19),(14,3,43,34,'\0',NULL,NULL,NULL,'33',21),(16,3,55,44,'\0',NULL,NULL,NULL,'33',23),(17,0,45,43,'\0',NULL,NULL,NULL,'44',24),(18,4,44,25,'\0',NULL,NULL,NULL,'55',27),(20,3,44,25,'\0',NULL,NULL,NULL,'55',29),(21,5,44,25,'\0',NULL,NULL,NULL,'55',30),(24,3,567,675,'\0',NULL,NULL,NULL,'66',35),(26,3,66,66,'\0',NULL,NULL,NULL,'55',38),(27,1,NULL,NULL,'\0',NULL,NULL,NULL,'33',39),(29,3,7678,67,'\0',NULL,NULL,NULL,'33',41),(31,3,45,25,'\0',NULL,NULL,NULL,'33',43),(33,3,8,87,'\0',NULL,NULL,NULL,'33',45),(35,3,45,25,'\0',NULL,NULL,NULL,'33',47),(37,3,45,25,'\0',NULL,NULL,NULL,'33',49),(40,3,45,25,'\0',NULL,NULL,NULL,'33',52),(42,3,45,25,'\0',NULL,NULL,NULL,'33',54),(44,3,45,25,'\0',NULL,NULL,NULL,'33',56),(46,3,45,25,'\0',NULL,NULL,NULL,'33',58),(47,1,NULL,NULL,'\0',NULL,NULL,NULL,'1234',59);
/*!40000 ALTER TABLE `shipment_temp_logger` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_method`
--

DROP TABLE IF EXISTS `shipping_method`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `shipping_method` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) collate latin1_general_cs NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `shipping_method`
--

LOCK TABLES `shipping_method` WRITE;
/*!40000 ALTER TABLE `shipping_method` DISABLE KEYS */;
INSERT INTO `shipping_method` VALUES (1,0,'unknown'),(2,0,'Drop-off'),(3,0,'Pick-up'),(4,0,'Inter-Hospital'),(5,0,'Canada Post'),(6,0,'DHL'),(7,0,' FedEx'),(8,0,'Hospital Courier'),(9,0,'Purolator');
/*!40000 ALTER TABLE `shipping_method` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `site_study`
--

DROP TABLE IF EXISTS `site_study`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `site_study` (
  `STUDY_ID` int(11) NOT NULL,
  `SITE_ID` int(11) NOT NULL,
  PRIMARY KEY  (`SITE_ID`,`STUDY_ID`),
  KEY `FK7A197EB1F2A2464F` (`STUDY_ID`),
  KEY `FK7A197EB13F52C885` (`SITE_ID`),
  CONSTRAINT `FK7A197EB13F52C885` FOREIGN KEY (`SITE_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK7A197EB1F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `site_study`
--

LOCK TABLES `site_study` WRITE;
/*!40000 ALTER TABLE `site_study` DISABLE KEYS */;
INSERT INTO `site_study` VALUES (1,2),(2,6);
/*!40000 ALTER TABLE `site_study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `source_specimen`
--

DROP TABLE IF EXISTS `source_specimen`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `source_specimen` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NEED_ORIGINAL_VOLUME` bit(1) default NULL,
  `SPECIMEN_TYPE_ID` int(11) NOT NULL,
  `STUDY_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK28D36ACF2A2464F` (`STUDY_ID`),
  KEY `FK28D36AC38445996` (`SPECIMEN_TYPE_ID`),
  CONSTRAINT `FK28D36AC38445996` FOREIGN KEY (`SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`),
  CONSTRAINT `FK28D36ACF2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `source_specimen`
--

LOCK TABLES `source_specimen` WRITE;
/*!40000 ALTER TABLE `source_specimen` DISABLE KEYS */;
INSERT INTO `source_specimen` VALUES (1,1,'\0',46,1),(2,1,'\0',34,1),(3,1,'\0',23,1),(4,1,'\0',11,1),(5,1,'\0',12,1),(6,1,'\0',7,1),(7,1,'\0',117,1),(8,1,'\0',108,1),(9,1,'\0',5,1),(13,3,'',86,2),(14,0,'\0',94,2),(15,0,'',116,2),(16,0,'',99,2);
/*!40000 ALTER TABLE `source_specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specimen`
--

DROP TABLE IF EXISTS `specimen`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `specimen` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `INVENTORY_ID` varchar(100) collate latin1_general_cs NOT NULL,
  `COMMENT` text collate latin1_general_cs,
  `QUANTITY` double default NULL,
  `CREATED_AT` datetime NOT NULL,
  `TOP_SPECIMEN_ID` int(11) default NULL,
  `COLLECTION_EVENT_ID` int(11) NOT NULL,
  `CURRENT_CENTER_ID` int(11) default NULL,
  `ORIGINAL_COLLECTION_EVENT_ID` int(11) default NULL,
  `SPECIMEN_TYPE_ID` int(11) NOT NULL,
  `ORIGIN_INFO_ID` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `PROCESSING_EVENT_ID` int(11) default NULL,
  `PARENT_SPECIMEN_ID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `INVENTORY_ID` (`INVENTORY_ID`),
  KEY `FKAF84F308FBB79BBF` (`CURRENT_CENTER_ID`),
  KEY `FKAF84F30886857784` (`ORIGINAL_COLLECTION_EVENT_ID`),
  KEY `FKAF84F308C449A4` (`ACTIVITY_STATUS_ID`),
  KEY `FKAF84F308280272F2` (`COLLECTION_EVENT_ID`),
  KEY `FKAF84F30812E55F12` (`ORIGIN_INFO_ID`),
  KEY `FKAF84F30861674F50` (`PARENT_SPECIMEN_ID`),
  KEY `FKAF84F30833126C8` (`PROCESSING_EVENT_ID`),
  KEY `FKAF84F30838445996` (`SPECIMEN_TYPE_ID`),
  KEY `FKAF84F308C9EF5F7B` (`TOP_SPECIMEN_ID`),
  KEY `INV_ID_IDX` (`INVENTORY_ID`),
  CONSTRAINT `FKAF84F30812E55F12` FOREIGN KEY (`ORIGIN_INFO_ID`) REFERENCES `origin_info` (`ID`),
  CONSTRAINT `FKAF84F308280272F2` FOREIGN KEY (`COLLECTION_EVENT_ID`) REFERENCES `collection_event` (`ID`),
  CONSTRAINT `FKAF84F30833126C8` FOREIGN KEY (`PROCESSING_EVENT_ID`) REFERENCES `processing_event` (`ID`),
  CONSTRAINT `FKAF84F30838445996` FOREIGN KEY (`SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`),
  CONSTRAINT `FKAF84F30861674F50` FOREIGN KEY (`PARENT_SPECIMEN_ID`) REFERENCES `specimen` (`ID`),
  CONSTRAINT `FKAF84F30886857784` FOREIGN KEY (`ORIGINAL_COLLECTION_EVENT_ID`) REFERENCES `collection_event` (`ID`),
  CONSTRAINT `FKAF84F308C449A4` FOREIGN KEY (`ACTIVITY_STATUS_ID`) REFERENCES `activity_status` (`ID`),
  CONSTRAINT `FKAF84F308C9EF5F7B` FOREIGN KEY (`TOP_SPECIMEN_ID`) REFERENCES `specimen` (`ID`),
  CONSTRAINT `FKAF84F308FBB79BBF` FOREIGN KEY (`CURRENT_CENTER_ID`) REFERENCES `center` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `specimen`
--

LOCK TABLES `specimen` WRITE;
/*!40000 ALTER TABLE `specimen` DISABLE KEYS */;
INSERT INTO `specimen` VALUES (21,2,'001',NULL,NULL,'2011-10-13 13:49:11',21,3,2,3,5,3,1,NULL,NULL),(30,4,'C002',NULL,NULL,'2011-10-13 13:51:40',30,4,2,4,18,4,1,NULL,NULL),(31,4,'C001',NULL,NULL,'2011-10-13 13:51:40',31,4,2,4,13,4,1,NULL,NULL),(42,3,'C010',NULL,NULL,'2011-10-13 14:18:22',42,4,1,4,107,22,1,NULL,NULL),(43,3,'C009',NULL,NULL,'2011-10-13 14:18:22',43,4,2,4,119,6,1,NULL,NULL),(44,3,'C008',NULL,NULL,'2011-10-13 14:18:22',44,4,2,4,7,6,1,NULL,NULL),(45,5,'C007',NULL,NULL,'2011-10-13 14:18:22',45,4,2,4,23,12,1,NULL,NULL),(46,5,'C006',NULL,NULL,'2011-10-13 14:18:22',46,4,2,4,46,11,1,NULL,NULL),(47,3,'C005',NULL,NULL,'2011-10-13 14:18:22',47,4,2,4,11,6,1,NULL,NULL),(48,3,'C004',NULL,NULL,'2011-10-13 14:18:22',48,4,2,4,117,6,1,NULL,NULL),(49,6,'C003',NULL,NULL,'2011-10-13 14:18:22',49,4,2,4,12,8,1,NULL,NULL),(50,0,'C020',NULL,NULL,'2011-10-14 09:48:40',50,5,1,5,45,13,1,NULL,NULL),(51,0,'C019',NULL,NULL,'2011-10-14 09:48:40',51,5,1,5,85,13,1,NULL,NULL),(52,0,'C018',NULL,NULL,'2011-10-14 09:48:40',52,5,1,5,43,13,1,NULL,NULL),(53,0,'C017',NULL,NULL,'2011-10-14 09:48:40',53,5,1,5,110,13,1,NULL,NULL),(54,0,'C016',NULL,NULL,'2011-10-14 09:48:40',54,5,1,5,39,13,1,NULL,NULL),(55,2,'C015',NULL,NULL,'2011-10-14 09:48:40',55,5,1,5,24,23,1,NULL,NULL),(56,1,'C014',NULL,NULL,'2011-10-14 09:48:40',56,5,1,5,23,15,1,NULL,NULL),(57,1,'C013',NULL,NULL,'2011-10-14 09:48:40',57,5,1,5,107,14,1,NULL,NULL),(58,1,'C012',NULL,NULL,'2011-10-14 09:48:40',58,5,2,5,18,13,1,NULL,NULL),(59,1,'C011',NULL,NULL,'2011-10-14 09:48:40',59,5,2,5,13,13,1,NULL,NULL),(111,0,'005',NULL,NULL,'2011-10-17 09:23:14',111,3,2,3,46,24,1,NULL,NULL),(112,0,'004',NULL,NULL,'2011-10-17 09:23:14',112,3,2,3,108,24,1,NULL,NULL),(113,0,'003',NULL,NULL,'2011-10-17 09:23:14',113,3,2,3,117,24,1,NULL,NULL),(114,1,'002',NULL,NULL,'2011-10-17 09:23:14',114,3,1,3,12,24,1,NULL,NULL),(125,0,'2',NULL,NULL,'2011-10-17 14:54:07',125,13,1,13,46,26,1,NULL,NULL),(126,0,'1',NULL,NULL,'2011-10-17 14:54:07',126,13,1,13,5,26,1,NULL,NULL),(167,4,'Event10',NULL,NULL,'2011-10-18 13:45:29',167,18,2,18,107,42,1,NULL,NULL),(168,5,'Event09',NULL,NULL,'2011-10-18 13:45:29',168,18,2,18,16,44,1,NULL,NULL),(169,2,'Event08',NULL,NULL,'2011-10-18 13:45:29',169,18,2,18,118,31,1,NULL,NULL),(170,4,'Event07',NULL,NULL,'2011-10-18 13:45:29',170,18,2,18,46,39,1,NULL,NULL),(171,4,'Event06',NULL,NULL,'2011-10-18 13:45:29',171,18,2,18,34,38,1,NULL,NULL),(172,4,'Event05',NULL,NULL,'2011-10-18 13:45:29',172,18,2,18,23,36,1,NULL,NULL),(173,2,'Event04',NULL,NULL,'2011-10-18 13:45:29',173,18,2,18,11,31,1,NULL,NULL),(174,1,'Event03',NULL,NULL,'2011-10-18 13:45:29',174,18,1,18,12,31,1,NULL,NULL),(175,2,'Event02',NULL,NULL,'2011-10-18 13:45:29',175,18,1,18,5,32,1,NULL,NULL),(176,2,'Event01',NULL,NULL,'2011-10-18 13:45:29',176,18,2,18,108,31,1,NULL,NULL),(197,2,'Event13',NULL,NULL,'2011-10-20 14:10:13',197,18,1,18,119,46,1,NULL,NULL),(198,1,'Event12',NULL,NULL,'2011-10-20 14:10:13',198,18,2,18,2,40,1,NULL,NULL),(199,1,'Event11',NULL,NULL,'2011-10-20 14:10:13',199,18,2,18,84,40,1,NULL,NULL),(200,0,'Event14',NULL,NULL,'2011-10-20 14:10:13',200,18,1,18,21,40,1,NULL,NULL),(201,2,'Event15',NULL,NULL,'2011-10-20 14:10:13',201,18,1,18,20,40,1,1,NULL),(202,0,'Event16',NULL,NULL,'2011-10-20 14:10:13',202,18,1,18,29,40,1,NULL,NULL),(203,0,'Event17',NULL,NULL,'2011-10-20 14:10:13',203,18,1,18,34,40,1,NULL,NULL),(204,0,'Event18',NULL,NULL,'2011-10-20 14:10:13',204,18,1,18,74,40,1,NULL,NULL),(205,5,'Event19',NULL,NULL,'2011-10-20 14:10:13',205,18,1,18,66,50,1,NULL,NULL),(206,1,'Event20',NULL,NULL,'2011-10-20 14:10:13',206,18,2,18,49,40,1,NULL,NULL),(207,4,'E03',NULL,NULL,'2011-10-20 16:48:08',207,4,2,4,25,53,1,NULL,NULL),(208,2,'E02',NULL,NULL,'2011-10-20 16:48:08',208,4,2,4,124,51,1,NULL,NULL),(209,2,'E01',NULL,NULL,'2011-10-20 16:48:08',209,4,2,4,88,51,1,NULL,NULL),(210,4,'E04',NULL,NULL,'2011-10-20 16:48:08',210,4,2,4,119,55,1,NULL,NULL),(211,4,'E05',NULL,NULL,'2011-10-20 16:48:08',211,4,2,4,86,57,1,NULL,NULL),(212,3,'E06',NULL,NULL,'2011-10-20 17:26:10',212,4,2,4,125,60,1,NULL,NULL),(213,3,'E07',NULL,NULL,'2011-10-20 17:26:10',213,4,2,4,122,62,1,NULL,NULL),(214,2,'E08',NULL,NULL,'2011-10-20 17:26:10',214,4,1,4,122,64,1,NULL,NULL),(215,0,'E09',NULL,NULL,'2011-10-20 17:26:10',215,4,1,4,105,58,1,NULL,NULL),(216,0,'E10',NULL,NULL,'2011-10-20 17:26:10',216,4,1,4,124,58,1,NULL,NULL),(218,0,'S010',NULL,NULL,'2011-10-21 09:53:55',218,22,1,22,45,66,1,NULL,NULL),(219,0,'S009',NULL,NULL,'2011-10-21 09:53:55',219,22,1,22,85,66,1,NULL,NULL),(220,0,'S008',NULL,NULL,'2011-10-21 09:53:55',220,22,1,22,43,66,1,NULL,NULL),(221,0,'S007',NULL,NULL,'2011-10-21 09:53:55',221,22,1,22,110,66,1,NULL,NULL),(222,0,'S006',NULL,NULL,'2011-10-21 09:53:55',222,22,1,22,39,66,1,NULL,NULL),(223,0,'S005',NULL,NULL,'2011-10-21 09:53:55',223,22,1,22,24,66,1,NULL,NULL),(224,0,'S004',NULL,NULL,'2011-10-21 09:53:55',224,22,1,22,23,66,1,NULL,NULL),(225,0,'S003',NULL,NULL,'2011-10-21 09:53:55',225,22,1,22,107,66,1,NULL,NULL),(226,2,'S002',NULL,NULL,'2011-10-21 09:53:55',226,22,1,22,18,68,1,NULL,NULL),(227,1,'S001',NULL,NULL,'2011-10-21 09:53:55',227,22,2,22,13,66,1,NULL,NULL),(228,0,'S020',NULL,NULL,'2011-10-21 09:58:34',228,23,1,23,45,69,1,NULL,NULL),(229,0,'S019',NULL,NULL,'2011-10-21 09:58:34',229,23,1,23,85,69,1,NULL,NULL),(230,0,'S018',NULL,NULL,'2011-10-21 09:58:34',230,23,1,23,43,69,1,NULL,NULL),(231,0,'S017',NULL,NULL,'2011-10-21 09:58:34',231,23,1,23,110,69,1,NULL,NULL),(232,0,'S016',NULL,NULL,'2011-10-21 09:58:34',232,23,1,23,39,69,1,NULL,NULL),(233,0,'S015',NULL,NULL,'2011-10-21 09:58:34',233,23,1,23,24,69,1,NULL,NULL),(234,0,'S014',NULL,NULL,'2011-10-21 09:58:34',234,23,1,23,23,69,1,NULL,NULL),(235,0,'S013',NULL,NULL,'2011-10-21 09:58:34',235,23,1,23,107,69,1,NULL,NULL),(236,2,'S012',NULL,NULL,'2011-10-21 09:58:34',236,23,1,23,18,71,1,NULL,NULL),(237,1,'S011',NULL,NULL,'2011-10-21 09:58:34',237,23,2,23,13,69,1,NULL,NULL),(238,0,'S030',NULL,NULL,'2011-10-21 10:01:10',238,24,1,24,45,72,1,NULL,NULL),(239,0,'S029',NULL,NULL,'2011-10-21 10:01:10',239,24,1,24,85,72,1,NULL,NULL),(240,0,'S028',NULL,NULL,'2011-10-21 10:01:10',240,24,1,24,43,72,1,NULL,NULL),(241,0,'S027',NULL,NULL,'2011-10-21 10:01:10',241,24,1,24,110,72,1,NULL,NULL),(242,0,'S026',NULL,NULL,'2011-10-21 10:01:10',242,24,1,24,39,72,1,NULL,NULL),(243,0,'S025',NULL,NULL,'2011-10-21 10:01:10',243,24,1,24,24,72,1,NULL,NULL),(244,0,'S024',NULL,NULL,'2011-10-21 10:01:10',244,24,1,24,23,72,1,NULL,NULL),(245,0,'S023',NULL,NULL,'2011-10-21 10:01:10',245,24,1,24,107,72,1,NULL,NULL),(246,2,'S022',NULL,NULL,'2011-10-21 10:01:10',246,24,1,24,18,74,1,NULL,NULL),(247,1,'S021',NULL,NULL,'2011-10-21 10:01:10',247,24,2,24,13,72,1,NULL,NULL),(260,2,'C010003','C010003',10,'2011-11-02 09:46:07',260,27,6,27,116,77,1,NULL,NULL),(261,2,'C010002','C010002',10,'2011-11-02 09:46:07',261,27,6,27,86,77,1,NULL,NULL),(262,5,'C010001','C010001',20,'2011-11-02 09:46:07',262,27,6,27,99,77,1,2,NULL),(263,0,'C010004',NULL,NULL,'2011-11-02 09:51:03',263,27,6,27,94,78,1,NULL,NULL),(264,1,'C01001',NULL,5,'2011-11-08 18:22:53',262,27,6,NULL,45,79,1,NULL,262),(265,0,'12345678',NULL,5,'2011-11-17 16:43:48',262,27,6,NULL,45,80,1,NULL,262),(266,0,'87654321',NULL,5,'2011-11-17 16:46:21',262,27,6,NULL,45,81,1,NULL,262),(267,0,'343434',NULL,5,'2011-11-17 16:52:50',262,27,6,NULL,45,82,1,NULL,262),(268,0,'C014AAAA',NULL,NULL,'2012-01-10 22:16:32',262,27,6,NULL,45,83,1,7,262),(269,0,'C014BBBA',NULL,NULL,'2012-01-23 11:26:20',262,27,6,NULL,45,84,1,8,262),(270,0,'C014CCCA',NULL,NULL,'2012-01-24 19:12:06',262,27,6,NULL,45,85,1,9,262),(271,1,'C014DDDA',NULL,NULL,'2012-01-24 19:26:02',262,27,6,NULL,45,86,1,NULL,262),(272,0,'C014FFFA',NULL,NULL,'2012-01-26 17:49:22',262,27,6,NULL,45,89,1,11,262),(273,1,'patient02_SH01','10 mls',NULL,'2012-02-05 13:14:39',273,28,6,28,94,98,1,NULL,NULL),(274,1,'patient02_Urine01',NULL,50,'2012-02-05 13:14:39',274,28,6,28,99,98,1,NULL,NULL),(275,1,'patient02_LH01',NULL,10,'2012-02-05 13:14:39',275,28,6,28,116,98,1,NULL,NULL),(276,1,'patient02_EDTA01',NULL,10,'2012-02-05 13:14:39',276,28,6,28,86,98,1,NULL,NULL),(277,0,'patient02_EDTA02',NULL,10,'2012-02-07 11:18:20',277,28,6,28,86,99,1,NULL,NULL);
/*!40000 ALTER TABLE `specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specimen_attr`
--

DROP TABLE IF EXISTS `specimen_attr`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `specimen_attr` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `VALUE` varchar(255) default NULL,
  `STUDY_SPECIMEN_ATTR_ID` int(11) NOT NULL,
  `SPECIMEN_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK1F9B3CC8EF199765` (`SPECIMEN_ID`),
  KEY `FK1F9B3CC882F01EDF` (`STUDY_SPECIMEN_ATTR_ID`),
  CONSTRAINT `FK1F9B3CC882F01EDF` FOREIGN KEY (`STUDY_SPECIMEN_ATTR_ID`) REFERENCES `study_specimen_attr` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK1F9B3CC8EF199765` FOREIGN KEY (`SPECIMEN_ID`) REFERENCES `specimen` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `specimen_attr`
--

LOCK TABLES `specimen_attr` WRITE;
/*!40000 ALTER TABLE `specimen_attr` DISABLE KEYS */;
INSERT INTO `specimen_attr` VALUES (1,0,'12345',1,272);
/*!40000 ALTER TABLE `specimen_attr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specimen_attr_type`
--

DROP TABLE IF EXISTS `specimen_attr_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `specimen_attr_type` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(50) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `specimen_attr_type`
--

LOCK TABLES `specimen_attr_type` WRITE;
/*!40000 ALTER TABLE `specimen_attr_type` DISABLE KEYS */;
INSERT INTO `specimen_attr_type` VALUES (1,0,'number'),(2,0,'text'),(3,0,'date_time'),(4,0,'select_single'),(5,0,'select_multiple');
/*!40000 ALTER TABLE `specimen_attr_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specimen_type`
--

DROP TABLE IF EXISTS `specimen_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `specimen_type` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(100) collate latin1_general_cs NOT NULL,
  `NAME_SHORT` varchar(50) collate latin1_general_cs NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `NAME_SHORT` (`NAME_SHORT`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `specimen_type`
--

LOCK TABLES `specimen_type` WRITE;
/*!40000 ALTER TABLE `specimen_type` DISABLE KEYS */;
INSERT INTO `specimen_type` VALUES (1,0,'Ascending Colon','Colon, A'),(2,0,'Buffy coat','BC'),(3,0,'CDPA Plasma','CDPA Plasma'),(4,0,'Cells500','Cells500'),(5,0,'Centrifuged Urine','C Urine'),(6,0,'Cord Blood Mononuclear Cells','CBMC'),(7,0,'DNA (Blood)','DNA(Blood)'),(8,0,'DNA (White blood cells)','DNA (WBC)'),(9,0,'Descending Colon','Colon, D'),(10,0,'Duodenum','Duodenum'),(11,0,'Filtered Urine','F Urine'),(12,0,'Finger Nails','F Nails'),(13,0,'Hair','Hair'),(14,0,'Hemodialysate','Dialysate'),(15,0,'Heparin Blood','HB'),(16,0,'Ileum','Ileum'),(17,0,'Jejunum','Jejunum'),(18,0,'Lithium Heparin Plasma','Lith Hep Plasma'),(19,0,'Meconium - BABY','Meconium'),(20,0,'Paxgene800','Paxgene800'),(21,0,'Peritoneal Dialysate','Effluent'),(22,0,'Plasma (Na Heparin) - DAD','Plasma SH'),(23,0,'Plasma','Plasma'),(24,0,'Platelet free plasma','PF Plasma'),(25,0,'RNA CBMC','CBMC RNA'),(26,0,'RNA-Ascending Colon','R-ColonA'),(27,0,'RNA-Descending Colon','R-ColonD'),(28,0,'RNA-Duodenum','R-Duodenum'),(29,0,'RNA-Ileum','R-Ilieum'),(30,0,'RNA-Jejunum','R-Jejunum'),(31,0,'RNA-Stomach, Antrum','R-StomachA'),(32,0,'RNA-Stomach, Body','R-StomachB'),(33,0,'RNA-Transverse Colon','R-ColonT'),(34,0,'RNAlater Biopsies','RNA Biopsy'),(35,0,'Serum (Beige top)','Serum B'),(36,0,'SerumG400','SerumG400'),(37,0,'SerumPellet - BABY','Serum Pel'),(38,0,'SodiumAzideUrine','ZUrine'),(39,0,'Source Water','S Water'),(40,0,'Stomach, Antrum','Stomach, A'),(41,0,'Stomach, Body','Stomach, B'),(42,0,'Tap Water','T Water'),(43,0,'Toe Nails','T Nails'),(44,0,'Transverse Colon','Colon, T'),(45,0,'Urine','Urine'),(46,0,'WB - BABY','WBlood'),(47,0,'WB DMSO','WB DMSO'),(48,0,'WB Plasma - BABY','WB Plasma'),(49,0,'WB RNA - BABY','WB RNA'),(50,0,'WB Serum - BABY','WB Serum'),(51,0,'Whole Blood EDTA','WBE'),(52,0,'LH PFP 200','LH PFP 200'),(53,0,'UrineC900','UrineC900'),(54,0,'PlasmaE800','PlasmaE800'),(55,0,'P100 500','P100 500'),(56,0,'PlasmaL500','PlasmaL500'),(57,0,'LH PFP 500','LH PFP 500'),(58,0,'PlasmaE200','PlasmaE200'),(59,0,'DNA L 1000','DNA L 1000'),(60,0,'SerumG500','SerumG500'),(61,0,'PlasmaL200','PlasmaL200'),(62,0,'DNA E 1000','DNA E 1000'),(63,0,'PlasmaE500','PlasmaE500'),(64,0,'UrineSA900','UrineSA900'),(65,0,'PlasmaE250','PlasmaE250'),(66,0,'UrineSA700','UrineSA700'),(67,0,'RNA-normal rectum biopsy','RNA-normal rectum b'),(68,0,'RNA-normal left biopsy','RNA-normal L b'),(69,0,'RNA-normal right biopsy','RNA-normal R b'),(70,0,'RNA-adjacent diseased biopsy','RNA-adjacent diseased b'),(71,0,'RNA-diseased biopsy','RNA-diseased b'),(72,0,'RNA-normal biopsy','RNA-normal b'),(73,0,'PlasmaE400','PlasmaE400'),(74,0,'SerumB900','SerumB900'),(75,0,'SerumB400','SerumB400'),(76,0,'SerumG200','SerumG200'),(77,0,'PlasmaE BHT200','PlasmaE BHT200'),(78,0,'PlasmaE300','PlasmaE300'),(79,0,'PlasmaE125','PlasmaE125'),(80,0,'PlasmaE75','PlasmaE75'),(81,0,'DNA E 500','DNA E 500'),(82,0,'N/A','N/A'),(83,0,'Unknown / import','Unknown / import'),(84,0,'Damaged','Damaged'),(85,0,'Unusable','Unusable'),(86,0,'10mL lavender top EDTA tube','10mL lavender top EDTA tube'),(87,0,'6mL lavender top EDTA tube','6mL lavender top EDTA tube'),(88,0,'4ml lavender top EDTA tube','4ml lavender top EDTA tube'),(89,0,'3mL lavender top EDTA tube','3mL lavender top EDTA tube'),(90,0,'5mL gold top serum tube','5mL gold top serum tube'),(91,0,'6mL beige top tube','6mL beige top tube'),(92,0,'3mL red top tube (hemodialysate)','3mL red top tube (hemodialysate)'),(93,0,'3ml red top tube (source water)','3ml red top tube (source water)'),(94,0,'10ml green top sodium heparin tube','10ml green top sodium heparin tube'),(95,0,'6ml light green top lithium heparin tube','6ml light green top lithium heparin tube'),(96,0,'10ml orange top PAXgene tube','10ml orange top PAXgene tube'),(97,0,'15ml centrifuge tube (sodium azide urine)','15ml centrifuge tube (sodium azide urine)'),(98,0,'6ml beige top tube (tap water)','6ml beige top tube (tap water)'),(99,0,'urine cup','urine cup'),(100,0,'fingernail tube','fingernail tube'),(101,0,'toenail tube','toenail tube'),(102,0,'hair bagette','hair bagette'),(103,0,'4.5mL blue top Sodium citrate tube','4.5mL blue top Sodium citrate tube'),(104,0,'2.7mL blue top Sodium citrate tube','2.7mL blue top Sodium citrate tube'),(105,0,'15ml centrifuge tube (ascites fluid)','15ml centrifuge tube (ascites fluid)'),(106,0,'EDTA cryovial','EDTA cryovial'),(107,0,'Nasal Swab','Nasal Swab'),(108,0,'Breast milk','Breast milk'),(109,0,'CHILD Meconium','CHILD Meconium'),(110,0,'Stool','Stool'),(111,0,'ERCIN Serum processing pallet','ERCIN Serum processing pallet'),(112,0,'ERCIN Urine processing pallet','ERCIN Urine processing pallet'),(113,0,'AHFEM processing pallet ','AHFEM processing pallet '),(114,0,'8.5ml P100 orange top tube','8.5ml P100 orange top tube'),(115,0,'9ml CPDA yellow top tube','9ml CPDA yellow top tube'),(116,0,'10ml green top Lithium Heparin tube','10ml green top Lithium Heparin tube'),(117,0,'Biopsy, RNA later','Biopsy, RNA later'),(118,0,'Colonoscopy Kit','Colonoscopy Kit'),(119,0,'Gastroscopy Kit','Gastroscopy Kit'),(120,0,'Enteroscopy Kit','Enteroscopy Kit'),(121,0,'4ml green top sodium heparin BD 367871','4ml green top sodium heparin BD 367871'),(122,0,'4ml gold top serum tube','4ml gold top serum tube'),(123,0,'RVS Nitric Oxide processing pallet','RVS Nitric Oxide processing pallet'),(124,0,'7ml EDTA conventional top','7ml EDTA conventional top'),(125,0,'3ml lavender top EDTA tube w BHT and Desferal','3ml lavender top EDTA tube w BHT and Desferal');
/*!40000 ALTER TABLE `specimen_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specimen_type_specimen_type`
--

DROP TABLE IF EXISTS `specimen_type_specimen_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `specimen_type_specimen_type` (
  `CHILD_SPECIMEN_TYPE_ID` int(11) NOT NULL,
  `PARENT_SPECIMEN_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY  (`PARENT_SPECIMEN_TYPE_ID`,`CHILD_SPECIMEN_TYPE_ID`),
  KEY `FKD95844635F3DC8B` (`PARENT_SPECIMEN_TYPE_ID`),
  KEY `FKD9584463D9672259` (`CHILD_SPECIMEN_TYPE_ID`),
  CONSTRAINT `FKD95844635F3DC8B` FOREIGN KEY (`PARENT_SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`),
  CONSTRAINT `FKD9584463D9672259` FOREIGN KEY (`CHILD_SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `specimen_type_specimen_type`
--

LOCK TABLES `specimen_type_specimen_type` WRITE;
/*!40000 ALTER TABLE `specimen_type_specimen_type` DISABLE KEYS */;
INSERT INTO `specimen_type_specimen_type` VALUES (67,83),(68,83),(69,83),(70,83),(71,83),(72,83),(2,86),(4,86),(7,86),(8,86),(23,86),(24,86),(47,86),(51,86),(54,86),(58,86),(62,86),(63,86),(65,86),(73,86),(81,86),(2,87),(4,87),(7,87),(8,87),(23,87),(24,87),(47,87),(51,87),(54,87),(58,87),(62,87),(63,87),(65,87),(73,87),(81,87),(2,88),(4,88),(7,88),(8,88),(23,88),(24,88),(47,88),(51,88),(54,88),(58,88),(62,88),(63,88),(65,88),(73,88),(81,88),(2,89),(4,89),(7,89),(8,89),(23,89),(24,89),(47,89),(51,89),(54,89),(58,89),(62,89),(63,89),(65,89),(73,89),(81,89),(36,90),(60,90),(76,90),(35,91),(74,91),(75,91),(14,92),(39,93),(4,94),(6,94),(8,94),(15,94),(22,94),(24,94),(25,94),(46,94),(48,94),(49,94),(50,94),(15,95),(18,95),(52,95),(56,95),(57,95),(59,95),(61,95),(20,96),(38,97),(64,97),(66,97),(42,98),(5,99),(11,99),(38,99),(45,99),(53,99),(64,99),(66,99),(12,100),(43,101),(13,102),(2,106),(4,106),(7,106),(8,106),(23,106),(24,106),(47,106),(51,106),(54,106),(58,106),(62,106),(63,106),(65,106),(73,106),(81,106),(19,109),(55,114),(3,115),(15,116),(18,116),(52,116),(56,116),(57,116),(59,116),(61,116),(34,117),(1,118),(9,118),(16,118),(26,118),(27,118),(29,118),(33,118),(44,118),(10,119),(28,119),(31,119),(32,119),(40,119),(41,119),(16,120),(17,120),(29,120),(30,120),(4,121),(6,121),(8,121),(15,121),(22,121),(24,121),(25,121),(46,121),(48,121),(49,121),(50,121),(36,122),(60,122),(76,122),(2,124),(4,124),(7,124),(8,124),(23,124),(24,124),(47,124),(51,124),(54,124),(58,124),(62,124),(63,124),(65,124),(73,124),(81,124),(77,125);
/*!40000 ALTER TABLE `specimen_type_specimen_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study`
--

DROP TABLE IF EXISTS `study`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `study` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) collate latin1_general_cs NOT NULL,
  `NAME_SHORT` varchar(50) collate latin1_general_cs NOT NULL,
  `COMMENT` text collate latin1_general_cs,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `NAME_SHORT` (`NAME_SHORT`),
  KEY `FK4B915A9C449A4` (`ACTIVITY_STATUS_ID`),
  CONSTRAINT `FK4B915A9C449A4` FOREIGN KEY (`ACTIVITY_STATUS_ID`) REFERENCES `activity_status` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `study`
--

LOCK TABLES `study` WRITE;
/*!40000 ALTER TABLE `study` DISABLE KEYS */;
INSERT INTO `study` VALUES (1,2,'IK Study','IK Study',NULL,1),(2,4,'OHS1','OHS1','Test Study',1);
/*!40000 ALTER TABLE `study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_contact`
--

DROP TABLE IF EXISTS `study_contact`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `study_contact` (
  `STUDY_ID` int(11) NOT NULL,
  `CONTACT_ID` int(11) NOT NULL,
  PRIMARY KEY  (`STUDY_ID`,`CONTACT_ID`),
  KEY `FKAA13B36AF2A2464F` (`STUDY_ID`),
  KEY `FKAA13B36AA07999AF` (`CONTACT_ID`),
  CONSTRAINT `FKAA13B36AA07999AF` FOREIGN KEY (`CONTACT_ID`) REFERENCES `contact` (`ID`),
  CONSTRAINT `FKAA13B36AF2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `study_contact`
--

LOCK TABLES `study_contact` WRITE;
/*!40000 ALTER TABLE `study_contact` DISABLE KEYS */;
INSERT INTO `study_contact` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `study_contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_event_attr`
--

DROP TABLE IF EXISTS `study_event_attr`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `study_event_attr` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `LABEL` varchar(50) collate latin1_general_cs NOT NULL,
  `PERMISSIBLE` text collate latin1_general_cs,
  `REQUIRED` bit(1) default NULL,
  `STUDY_ID` int(11) NOT NULL,
  `EVENT_ATTR_TYPE_ID` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `uc_label` (`LABEL`,`STUDY_ID`),
  KEY `FK3EACD8ECF2A2464F` (`STUDY_ID`),
  KEY `FK3EACD8ECC449A4` (`ACTIVITY_STATUS_ID`),
  KEY `FK3EACD8EC5B770B31` (`EVENT_ATTR_TYPE_ID`),
  CONSTRAINT `FK3EACD8EC5B770B31` FOREIGN KEY (`EVENT_ATTR_TYPE_ID`) REFERENCES `event_attr_type` (`ID`),
  CONSTRAINT `FK3EACD8ECC449A4` FOREIGN KEY (`ACTIVITY_STATUS_ID`) REFERENCES `activity_status` (`ID`),
  CONSTRAINT `FK3EACD8ECF2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `study_event_attr`
--

LOCK TABLES `study_event_attr` WRITE;
/*!40000 ALTER TABLE `study_event_attr` DISABLE KEYS */;
INSERT INTO `study_event_attr` VALUES (1,2,'Phlebotomist',NULL,'\0',2,2,1),(2,2,'Patient Type','type1;type2','\0',2,4,1),(3,2,'Consent','one;two','\0',2,5,1),(4,2,'PBMC Count (x10^6)',NULL,'\0',2,1,1),(5,2,'Biopsy Length',NULL,'\0',2,1,1);
/*!40000 ALTER TABLE `study_event_attr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_specimen_attr`
--

DROP TABLE IF EXISTS `study_specimen_attr`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `study_specimen_attr` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `LABEL` varchar(50) default NULL,
  `PERMISSIBLE` text,
  `REQUIRED` tinyint(1) default NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `SPECIMEN_ATTR_TYPE_ID` int(11) NOT NULL,
  `STUDY_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKF7288B2F2A2464F` (`STUDY_ID`),
  KEY `FKF7288B2C449A4` (`ACTIVITY_STATUS_ID`),
  KEY `FKF7288B2494E5767` (`SPECIMEN_ATTR_TYPE_ID`),
  CONSTRAINT `FKF7288B2494E5767` FOREIGN KEY (`SPECIMEN_ATTR_TYPE_ID`) REFERENCES `specimen_attr_type` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FKF7288B2C449A4` FOREIGN KEY (`ACTIVITY_STATUS_ID`) REFERENCES `activity_status` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FKF7288B2F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `study_specimen_attr`
--

LOCK TABLES `study_specimen_attr` WRITE;
/*!40000 ALTER TABLE `study_specimen_attr` DISABLE KEYS */;
INSERT INTO `study_specimen_attr` VALUES (1,0,'Volume',NULL,0,1,1,2),(2,0,'Concentration',NULL,0,1,1,2),(3,0,'startProcess',NULL,0,1,3,2),(4,0,'endProcess',NULL,0,1,3,2),(5,0,'SampleErrors',NULL,0,1,2,2);
/*!40000 ALTER TABLE `study_specimen_attr` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-02-09  1:05:43
