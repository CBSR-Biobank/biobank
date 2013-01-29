-- MySQL dump 10.13  Distrib 5.5.28, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: biobank_v340
-- ------------------------------------------------------
-- Server version	5.5.28-0ubuntu0.12.10.1

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
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CITY` varchar(50) COLLATE latin1_general_cs NOT NULL,
  `COUNTRY` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `EMAIL_ADDRESS` varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  `FAX_NUMBER` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `PHONE_NUMBER` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `POSTAL_CODE` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `PROVINCE` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `STREET1` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `STREET2` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aliquoted_specimen`
--

DROP TABLE IF EXISTS `aliquoted_specimen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aliquoted_specimen` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `QUANTITY` int(11) DEFAULT NULL,
  `VOLUME` decimal(20,10) DEFAULT NULL,
  `SPECIMEN_TYPE_ID` int(11) NOT NULL,
  `STUDY_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK75EACAC1F2A2464F` (`STUDY_ID`),
  KEY `FK75EACAC138445996` (`SPECIMEN_TYPE_ID`),
  CONSTRAINT `FK75EACAC138445996` FOREIGN KEY (`SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`),
  CONSTRAINT `FK75EACAC1F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aliquoted_specimen`
--

LOCK TABLES `aliquoted_specimen` WRITE;
/*!40000 ALTER TABLE `aliquoted_specimen` DISABLE KEYS */;
/*!40000 ALTER TABLE `aliquoted_specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_operation`
--

DROP TABLE IF EXISTS `batch_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `batch_operation` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `TIME_EXECUTED` datetime NOT NULL,
  `EXECUTED_BY_USER_ID` int(11) NOT NULL,
  `FILE_DATA_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK5B242662F55E67FE` (`FILE_DATA_ID`),
  KEY `FK5B242662FB2C14CD` (`EXECUTED_BY_USER_ID`),
  CONSTRAINT `FK5B242662FB2C14CD` FOREIGN KEY (`EXECUTED_BY_USER_ID`) REFERENCES `principal` (`ID`),
  CONSTRAINT `FK5B242662F55E67FE` FOREIGN KEY (`FILE_DATA_ID`) REFERENCES `file_data` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_operation`
--

LOCK TABLES `batch_operation` WRITE;
/*!40000 ALTER TABLE `batch_operation` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_operation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_operation_processing_event`
--

DROP TABLE IF EXISTS `batch_operation_processing_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `batch_operation_processing_event` (
  `PROCESSING_EVENT_ID` int(11) NOT NULL,
  `BATCH_OPERATION_ID` int(11) NOT NULL,
  PRIMARY KEY (`PROCESSING_EVENT_ID`,`BATCH_OPERATION_ID`),
  KEY `FKDA49AA8BD3BA0590` (`BATCH_OPERATION_ID`),
  CONSTRAINT `FKDA49AA8BD3BA0590` FOREIGN KEY (`BATCH_OPERATION_ID`) REFERENCES `batch_operation` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_operation_processing_event`
--

LOCK TABLES `batch_operation_processing_event` WRITE;
/*!40000 ALTER TABLE `batch_operation_processing_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_operation_processing_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `batch_operation_specimen`
--

DROP TABLE IF EXISTS `batch_operation_specimen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `batch_operation_specimen` (
  `SPECIMEN_ID` int(11) NOT NULL,
  `BATCH_OPERATION_ID` int(11) NOT NULL,
  PRIMARY KEY (`SPECIMEN_ID`,`BATCH_OPERATION_ID`),
  KEY `FKD2E0C45D3BA0590` (`BATCH_OPERATION_ID`),
  CONSTRAINT `FKD2E0C45D3BA0590` FOREIGN KEY (`BATCH_OPERATION_ID`) REFERENCES `batch_operation` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `batch_operation_specimen`
--

LOCK TABLES `batch_operation_specimen` WRITE;
/*!40000 ALTER TABLE `batch_operation_specimen` DISABLE KEYS */;
/*!40000 ALTER TABLE `batch_operation_specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `center`
--

DROP TABLE IF EXISTS `center`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `center` (
  `DISCRIMINATOR` varchar(31) COLLATE latin1_general_cs NOT NULL,
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `NAME_SHORT` varchar(50) COLLATE latin1_general_cs NOT NULL,
  `SENDS_SHIPMENTS` bit(1) DEFAULT NULL,
  `ADDRESS_ID` int(11) NOT NULL,
  `STUDY_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `NAME_SHORT` (`NAME_SHORT`),
  UNIQUE KEY `ADDRESS_ID` (`ADDRESS_ID`),
  UNIQUE KEY `STUDY_ID` (`STUDY_ID`),
  KEY `FK7645C055F2A2464F` (`STUDY_ID`),
  KEY `FK7645C0556AF2992F` (`ADDRESS_ID`),
  CONSTRAINT `FK7645C0556AF2992F` FOREIGN KEY (`ADDRESS_ID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK7645C055F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `center`
--

LOCK TABLES `center` WRITE;
/*!40000 ALTER TABLE `center` DISABLE KEYS */;
/*!40000 ALTER TABLE `center` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `center_comment`
--

DROP TABLE IF EXISTS `center_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `center_comment` (
  `CENTER_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`CENTER_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FKDF3FBC55CDA9FD4F` (`COMMENT_ID`),
  KEY `FKDF3FBC5592FAA705` (`CENTER_ID`),
  CONSTRAINT `FKDF3FBC5592FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKDF3FBC55CDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `center_comment`
--

LOCK TABLES `center_comment` WRITE;
/*!40000 ALTER TABLE `center_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `center_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collection_event`
--

DROP TABLE IF EXISTS `collection_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `collection_event` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `VISIT_NUMBER` int(11) NOT NULL,
  `PATIENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PATIENT_ID` (`PATIENT_ID`,`VISIT_NUMBER`),
  KEY `FKEDAD8999B563F38F` (`PATIENT_ID`),
  CONSTRAINT `FKEDAD8999B563F38F` FOREIGN KEY (`PATIENT_ID`) REFERENCES `patient` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collection_event`
--

LOCK TABLES `collection_event` WRITE;
/*!40000 ALTER TABLE `collection_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `collection_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collection_event_comment`
--

DROP TABLE IF EXISTS `collection_event_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `collection_event_comment` (
  `COLLECTION_EVENT_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`COLLECTION_EVENT_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FK1CFC0199280272F2` (`COLLECTION_EVENT_ID`),
  KEY `FK1CFC0199CDA9FD4F` (`COMMENT_ID`),
  CONSTRAINT `FK1CFC0199CDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`),
  CONSTRAINT `FK1CFC0199280272F2` FOREIGN KEY (`COLLECTION_EVENT_ID`) REFERENCES `collection_event` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collection_event_comment`
--

LOCK TABLES `collection_event_comment` WRITE;
/*!40000 ALTER TABLE `collection_event_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `collection_event_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comment` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CREATED_AT` datetime NOT NULL,
  `MESSAGE` text COLLATE latin1_general_cs NOT NULL,
  `USER_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK63717A3FB9634A05` (`USER_ID`),
  CONSTRAINT `FK63717A3FB9634A05` FOREIGN KEY (`USER_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `EMAIL_ADDRESS` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `FAX_NUMBER` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `MOBILE_NUMBER` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `NAME` varchar(100) COLLATE latin1_general_cs NOT NULL,
  `OFFICE_NUMBER` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `PAGER_NUMBER` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `TITLE` varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  `CLINIC_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK6382B00057F87A25` (`CLINIC_ID`),
  CONSTRAINT `FK6382B00057F87A25` FOREIGN KEY (`CLINIC_ID`) REFERENCES `center` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact`
--

LOCK TABLES `contact` WRITE;
/*!40000 ALTER TABLE `contact` DISABLE KEYS */;
/*!40000 ALTER TABLE `contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container`
--

DROP TABLE IF EXISTS `container`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `container` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `LABEL` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `PATH` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `PRODUCT_BARCODE` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `TEMPERATURE` double DEFAULT NULL,
  `CONTAINER_TYPE_ID` int(11) NOT NULL,
  `SITE_ID` int(11) NOT NULL,
  `TOP_CONTAINER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SITE_ID_2` (`SITE_ID`,`CONTAINER_TYPE_ID`,`LABEL`),
  UNIQUE KEY `SITE_ID` (`SITE_ID`,`PRODUCT_BARCODE`),
  KEY `FK8D995C611BE0C379` (`TOP_CONTAINER_ID`),
  KEY `FK8D995C613F52C885` (`SITE_ID`),
  KEY `FK_Container_containerType` (`CONTAINER_TYPE_ID`,`SITE_ID`),
  KEY `PATH_IDX` (`PATH`),
  KEY `ID` (`ID`,`CONTAINER_TYPE_ID`),
  CONSTRAINT `FK_Container_containerType` FOREIGN KEY (`CONTAINER_TYPE_ID`, `SITE_ID`) REFERENCES `container_type` (`ID`, `SITE_ID`),
  CONSTRAINT `FK8D995C611BE0C379` FOREIGN KEY (`TOP_CONTAINER_ID`) REFERENCES `container` (`ID`),
  CONSTRAINT `FK8D995C613F52C885` FOREIGN KEY (`SITE_ID`) REFERENCES `center` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container`
--

LOCK TABLES `container` WRITE;
/*!40000 ALTER TABLE `container` DISABLE KEYS */;
/*!40000 ALTER TABLE `container` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_comment`
--

DROP TABLE IF EXISTS `container_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `container_comment` (
  `CONTAINER_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`CONTAINER_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FK9A6C8C619BFD88CF` (`CONTAINER_ID`),
  KEY `FK9A6C8C61CDA9FD4F` (`COMMENT_ID`),
  CONSTRAINT `FK9A6C8C61CDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`),
  CONSTRAINT `FK9A6C8C619BFD88CF` FOREIGN KEY (`CONTAINER_ID`) REFERENCES `container` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container_comment`
--

LOCK TABLES `container_comment` WRITE;
/*!40000 ALTER TABLE `container_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `container_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_labeling_scheme`
--

DROP TABLE IF EXISTS `container_labeling_scheme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `container_labeling_scheme` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `MAX_CAPACITY` int(11) DEFAULT NULL,
  `MAX_CHARS` int(11) DEFAULT NULL,
  `MAX_COLS` int(11) DEFAULT NULL,
  `MAX_ROWS` int(11) DEFAULT NULL,
  `MIN_CHARS` int(11) DEFAULT NULL,
  `NAME` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container_labeling_scheme`
--

LOCK TABLES `container_labeling_scheme` WRITE;
/*!40000 ALTER TABLE `container_labeling_scheme` DISABLE KEYS */;
INSERT INTO `container_labeling_scheme` VALUES (1,0,384,3,24,16,2,'SBS Standard'),(2,0,576,2,NULL,NULL,2,'CBSR 2 char alphabetic'),(3,0,99,2,NULL,NULL,2,'2 char numeric'),(4,0,4,2,2,2,2,'Dewar'),(5,0,81,2,9,9,2,'CBSR SBS'),(6,0,676,2,NULL,NULL,2,'2 char alphabetic');
/*!40000 ALTER TABLE `container_labeling_scheme` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_position`
--

DROP TABLE IF EXISTS `container_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `container_position` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `COL` int(11) NOT NULL,
  `ROW` int(11) NOT NULL,
  `CONTAINER_ID` int(11) NOT NULL,
  `CONTAINER_TYPE_ID` int(11) NOT NULL,
  `PARENT_CONTAINER_ID` int(11) NOT NULL,
  `PARENT_CONTAINER_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PARENT_CONTAINER_ID` (`PARENT_CONTAINER_ID`,`ROW`,`COL`),
  KEY `FK_ContainerPosition_parentContainer` (`PARENT_CONTAINER_ID`,`PARENT_CONTAINER_TYPE_ID`),
  KEY `FK_ContainerPosition_container` (`CONTAINER_ID`,`CONTAINER_TYPE_ID`),
  KEY `FK_ContainerPosition_containerTypeContainerType` (`PARENT_CONTAINER_TYPE_ID`,`CONTAINER_TYPE_ID`),
  CONSTRAINT `FK_ContainerPosition_containerTypeContainerType` FOREIGN KEY (`PARENT_CONTAINER_TYPE_ID`, `CONTAINER_TYPE_ID`) REFERENCES `container_type_container_type` (`PARENT_CONTAINER_TYPE_ID`, `CHILD_CONTAINER_TYPE_ID`),
  CONSTRAINT `FK_ContainerPosition_container` FOREIGN KEY (`CONTAINER_ID`, `CONTAINER_TYPE_ID`) REFERENCES `container` (`ID`, `CONTAINER_TYPE_ID`) ON UPDATE CASCADE,
  CONSTRAINT `FK_ContainerPosition_parentContainer` FOREIGN KEY (`PARENT_CONTAINER_ID`, `PARENT_CONTAINER_TYPE_ID`) REFERENCES `container` (`ID`, `CONTAINER_TYPE_ID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container_position`
--

LOCK TABLES `container_position` WRITE;
/*!40000 ALTER TABLE `container_position` DISABLE KEYS */;
/*!40000 ALTER TABLE `container_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_type`
--

DROP TABLE IF EXISTS `container_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `container_type` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `COL_CAPACITY` int(11) NOT NULL,
  `ROW_CAPACITY` int(11) NOT NULL,
  `DEFAULT_TEMPERATURE` double DEFAULT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `NAME_SHORT` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `TOP_LEVEL` bit(1) DEFAULT NULL,
  `CHILD_LABELING_SCHEME_ID` int(11) NOT NULL,
  `SITE_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SITE_ID` (`SITE_ID`,`NAME_SHORT`),
  UNIQUE KEY `SITE_ID_2` (`SITE_ID`,`NAME`),
  KEY `FKB2C878585D63DFF0` (`CHILD_LABELING_SCHEME_ID`),
  KEY `FKB2C878583F52C885` (`SITE_ID`),
  KEY `ID` (`ID`,`SITE_ID`),
  CONSTRAINT `FKB2C878583F52C885` FOREIGN KEY (`SITE_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKB2C878585D63DFF0` FOREIGN KEY (`CHILD_LABELING_SCHEME_ID`) REFERENCES `container_labeling_scheme` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container_type`
--

LOCK TABLES `container_type` WRITE;
/*!40000 ALTER TABLE `container_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `container_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_type_comment`
--

DROP TABLE IF EXISTS `container_type_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `container_type_comment` (
  `CONTAINER_TYPE_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`CONTAINER_TYPE_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FK6657C158B3E77A12` (`CONTAINER_TYPE_ID`),
  KEY `FK6657C158CDA9FD4F` (`COMMENT_ID`),
  CONSTRAINT `FK6657C158CDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`),
  CONSTRAINT `FK6657C158B3E77A12` FOREIGN KEY (`CONTAINER_TYPE_ID`) REFERENCES `container_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container_type_comment`
--

LOCK TABLES `container_type_comment` WRITE;
/*!40000 ALTER TABLE `container_type_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `container_type_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `container_type_container_type`
--

DROP TABLE IF EXISTS `container_type_container_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `container_type_container_type` (
  `PARENT_CONTAINER_TYPE_ID` int(11) NOT NULL,
  `CHILD_CONTAINER_TYPE_ID` int(11) NOT NULL,
  `SITE_ID` int(11) NOT NULL,
  PRIMARY KEY (`PARENT_CONTAINER_TYPE_ID`,`CHILD_CONTAINER_TYPE_ID`),
  KEY `FK_ContainerType_parentContainerTypes` (`PARENT_CONTAINER_TYPE_ID`,`SITE_ID`),
  KEY `FK_ContainerType_childContainerTypes` (`CHILD_CONTAINER_TYPE_ID`,`SITE_ID`),
  CONSTRAINT `FK_ContainerType_childContainerTypes` FOREIGN KEY (`CHILD_CONTAINER_TYPE_ID`, `SITE_ID`) REFERENCES `container_type` (`ID`, `SITE_ID`),
  CONSTRAINT `FK_ContainerType_parentContainerTypes` FOREIGN KEY (`PARENT_CONTAINER_TYPE_ID`, `SITE_ID`) REFERENCES `container_type` (`ID`, `SITE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `container_type_specimen_type` (
  `CONTAINER_TYPE_ID` int(11) NOT NULL,
  `SPECIMEN_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`CONTAINER_TYPE_ID`,`SPECIMEN_TYPE_ID`),
  KEY `FKE2F4C26AB3E77A12` (`CONTAINER_TYPE_ID`),
  KEY `FKE2F4C26A38445996` (`SPECIMEN_TYPE_ID`),
  CONSTRAINT `FKE2F4C26A38445996` FOREIGN KEY (`SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`),
  CONSTRAINT `FKE2F4C26AB3E77A12` FOREIGN KEY (`CONTAINER_TYPE_ID`) REFERENCES `container_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `container_type_specimen_type`
--

LOCK TABLES `container_type_specimen_type` WRITE;
/*!40000 ALTER TABLE `container_type_specimen_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `container_type_specimen_type` ENABLE KEYS */;
UNLOCK TABLES;

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
INSERT INTO `csm_application` VALUES (1,'csmupt','CSM UPT Super Admin Application',0,0,'2009-07-22',NULL,NULL,NULL,NULL,NULL),(2,'biobank','biobank',0,0,'2009-07-22','jdbc:mysql://localhost:3306/biobank_stanford','dummy','4UlzrQJztJY=','org.hibernate.dialect.MySQLDialect','com.mysql.jdbc.Driver');
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
) ENGINE=InnoDB AUTO_INCREMENT=1485 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_pg_pe`
--

LOCK TABLES `csm_pg_pe` WRITE;
/*!40000 ALTER TABLE `csm_pg_pe` DISABLE KEYS */;
INSERT INTO `csm_pg_pe` VALUES (1428,1,10,'0000-00-00'),(1429,1,11,'0000-00-00'),(1430,1,12,'0000-00-00'),(1431,1,13,'0000-00-00'),(1432,1,15,'0000-00-00'),(1433,1,151,'0000-00-00'),(1434,1,16,'0000-00-00'),(1435,1,170,'0000-00-00'),(1437,1,171,'0000-00-00'),(1438,1,175,'0000-00-00'),(1439,1,176,'0000-00-00'),(1440,1,177,'0000-00-00'),(1441,1,178,'0000-00-00'),(1442,1,179,'0000-00-00'),(1443,1,18,'0000-00-00'),(1444,1,180,'0000-00-00'),(1445,1,181,'0000-00-00'),(1446,1,182,'0000-00-00'),(1447,1,183,'0000-00-00'),(1448,1,184,'0000-00-00'),(1449,1,185,'0000-00-00'),(1450,1,186,'0000-00-00'),(1451,1,187,'0000-00-00'),(1452,1,188,'0000-00-00'),(1453,1,19,'0000-00-00'),(1454,1,192,'0000-00-00'),(1455,1,193,'0000-00-00'),(1456,1,195,'0000-00-00'),(1457,1,196,'0000-00-00'),(1458,1,197,'0000-00-00'),(1459,1,198,'0000-00-00'),(1460,1,199,'0000-00-00'),(1462,1,20,'0000-00-00'),(1463,1,200,'0000-00-00'),(1464,1,201,'0000-00-00'),(1465,1,202,'0000-00-00'),(1466,1,203,'0000-00-00'),(1467,1,205,'0000-00-00'),(1468,1,21,'0000-00-00'),(1469,1,24,'0000-00-00'),(1470,1,25,'0000-00-00'),(1471,1,27,'0000-00-00'),(1472,1,3,'0000-00-00'),(1473,1,30,'0000-00-00'),(1474,1,32,'0000-00-00'),(1475,1,35,'0000-00-00'),(1476,1,36,'0000-00-00'),(1477,1,4,'0000-00-00'),(1478,1,5,'0000-00-00'),(1479,1,51,'0000-00-00'),(1480,1,6,'0000-00-00'),(1481,1,65,'0000-00-00'),(1482,1,7,'0000-00-00'),(1483,1,8,'0000-00-00'),(1484,1,207,'2012-11-26');
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
INSERT INTO `csm_privilege` VALUES (1,'CREATE','This privilege grants permission to a user to create an entity. This entity can be an object, a database entry, or a resource such as a network connection','2009-07-22'),(2,'ACCESS','This privilege allows a user to access a particular resource.  Examples of resources include a network or database connection, socket, module of the application, or even the application itself','2009-07-22'),(3,'READ','This privilege permits the user to read data from a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to read data about a particular entry','2009-07-22'),(4,'WRITE','This privilege allows a user to write data to a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to write data about a particular entity','2009-07-22'),(5,'UPDATE','This privilege grants permission at an entity level and signifies that the user is allowed to update data for a particular entity. Entities may include an object, object attribute, database row etc','2009-07-22'),(6,'DELETE','This privilege permits a user to delete a logical entity. This entity can be an object, a database entry, a resource such as a network connection, etc','2009-07-22'),(7,'EXECUTE','This privilege allows a user to execute a particular resource. The resource can be a method, function, behavior of the application, URL, button etc','2009-07-22');
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
) ENGINE=InnoDB AUTO_INCREMENT=208 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_protection_element`
--

LOCK TABLES `csm_protection_element` WRITE;
/*!40000 ALTER TABLE `csm_protection_element` DISABLE KEYS */;
INSERT INTO `csm_protection_element` VALUES (1,'csmupt','CSM UPT Super Admin Application Protection Element','csmupt',NULL,NULL,NULL,1,'2009-07-22'),(2,'biobank','biobank','biobank',NULL,NULL,NULL,1,'2009-07-22'),(3,'edu.ualberta.med.biobank.model.ActivityStatus','edu.ualberta.med.biobank.model.ActivityStatus','edu.ualberta.med.biobank.model.ActivityStatus','','','',2,'2010-03-04'),(4,'edu.ualberta.med.biobank.model.Address','edu.ualberta.med.biobank.model.Address','edu.ualberta.med.biobank.model.Address',NULL,NULL,NULL,2,'2009-07-22'),(5,'edu.ualberta.med.biobank.model.Capacity','edu.ualberta.med.biobank.model.Capacity','edu.ualberta.med.biobank.model.Capacity',NULL,NULL,NULL,2,'2009-07-22'),(6,'edu.ualberta.med.biobank.model.Clinic','edu.ualberta.med.biobank.model.Clinic','edu.ualberta.med.biobank.model.Clinic',NULL,NULL,NULL,2,'2009-07-22'),(7,'edu.ualberta.med.biobank.model.ContainerPosition','edu.ualberta.med.biobank.model.ContainerPosition','edu.ualberta.med.biobank.model.ContainerPosition',NULL,NULL,NULL,2,'2009-07-22'),(8,'edu.ualberta.med.biobank.model.Patient','edu.ualberta.med.biobank.model.Patient','edu.ualberta.med.biobank.model.Patient',NULL,NULL,NULL,2,'2009-07-22'),(10,'edu.ualberta.med.biobank.model.StudyEventAttr','edu.ualberta.med.biobank.model.StudyEventAttr','edu.ualberta.med.biobank.model.StudyEventAttr','','','',2,'2011-02-28'),(11,'edu.ualberta.med.biobank.model.EventAttr','edu.ualberta.med.biobank.model.EventAttr','edu.ualberta.med.biobank.model.EventAttr','','','',2,'2011-02-28'),(12,'edu.ualberta.med.biobank.model.GlobalEventAttr','edu.ualberta.med.biobank.model.GlobalEventAttr','edu.ualberta.med.biobank.model.GlobalEventAttr','','','',2,'2011-02-28'),(13,'edu.ualberta.med.biobank.model.EventAttrType','edu.ualberta.med.biobank.model.EventAttrType','edu.ualberta.med.biobank.model.EventAttrType','','','',2,'2011-02-28'),(15,'edu.ualberta.med.biobank.model.SpecimenPosition','edu.ualberta.med.biobank.model.SpecimenPosition','edu.ualberta.med.biobank.model.SpecimenPosition','','','',2,'2011-02-28'),(16,'edu.ualberta.med.biobank.model.OriginInfo','edu.ualberta.med.biobank.model.OriginInfo','edu.ualberta.med.biobank.model.OriginInfo','','','',2,'2011-02-28'),(18,'edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site',NULL,NULL,NULL,2,'2009-07-22'),(19,'edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','','','',2,'2010-08-19'),(20,'edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType',NULL,NULL,NULL,2,'2009-07-22'),(21,'edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study',NULL,NULL,NULL,2,'2009-07-22'),(24,'edu.ualberta.med.biobank.model.Specimen','edu.ualberta.med.biobank.model.Specimen','edu.ualberta.med.biobank.model.Specimen','','','',2,'2011-02-28'),(25,'edu.ualberta.med.biobank.model.AliquotedSpecimen','edu.ualberta.med.biobank.model.AliquotedSpecimen','edu.ualberta.med.biobank.model.AliquotedSpecimen','','','',2,'2011-02-28'),(27,'edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','','','',2,'2009-07-26'),(30,'edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','','','',2,'2009-08-24'),(32,'edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','','','',2,'2009-11-30'),(35,'edu.ualberta.med.biobank.model.SourceSpecimen','edu.ualberta.med.biobank.model.SourceSpecimen','edu.ualberta.med.biobank.model.SourceSpecimen','','','',2,'2011-02-28'),(36,'edu.ualberta.med.biobank.model.AbstractPosition','','edu.ualberta.med.biobank.model.AbstractPosition','','','',2,'2010-03-15'),(51,'edu.ualberta.med.biobank.model.Log','','edu.ualberta.med.biobank.model.Log','','','',2,'2010-05-25'),(65,'edu.ualberta.med.biobank.model.Dispatch','edu.ualberta.med.biobank.model.Dispatch','edu.ualberta.med.biobank.model.Dispatch','','','',2,'2010-08-18'),(151,'edu.ualberta.med.biobank.model.DispatchSpecimen','edu.ualberta.med.biobank.model.DispatchSpecimen','edu.ualberta.med.biobank.model.DispatchSpecimen','','','',2,'2011-02-28'),(170,'edu.ualberta.med.biobank.model.ResearchGroup','','edu.ualberta.med.biobank.model.ResearchGroup','','','',2,'2010-12-07'),(171,'edu.ualberta.med.biobank.model.Request','','edu.ualberta.med.biobank.model.Request','','','',2,'2010-12-08'),(175,'edu.ualberta.med.biobank.model.Report','','edu.ualberta.med.biobank.model.Report','','','',2,'2011-01-13'),(176,'edu.ualberta.med.biobank.model.ReportFilter','','edu.ualberta.med.biobank.model.ReportFilter','','','',2,'2011-01-13'),(177,'edu.ualberta.med.biobank.model.ReportFilterValue','','edu.ualberta.med.biobank.model.ReportFilterValue','','','',2,'2011-01-13'),(178,'edu.ualberta.med.biobank.model.ReportColumn','','edu.ualberta.med.biobank.model.ReportColumn','','','',2,'2011-01-13'),(179,'edu.ualberta.med.biobank.model.Entity','','edu.ualberta.med.biobank.model.Entity','','','',2,'2011-01-13'),(180,'edu.ualberta.med.biobank.model.EntityFilter','','edu.ualberta.med.biobank.model.EntityFilter','','','',2,'2011-01-13'),(181,'edu.ualberta.med.biobank.model.EntityColumn','','edu.ualberta.med.biobank.model.EntityColumn','','','',2,'2011-01-13'),(182,'edu.ualberta.med.biobank.model.EntityProperty','','edu.ualberta.med.biobank.model.EntityProperty','','','',2,'2011-01-13'),(183,'edu.ualberta.med.biobank.model.PropertyModifier','','edu.ualberta.med.biobank.model.PropertyModifier','','','',2,'2011-01-13'),(184,'edu.ualberta.med.biobank.model.PropertyType','','edu.ualberta.med.biobank.model.PropertyType','','','',2,'2011-01-13'),(185,'edu.ualberta.med.biobank.model.CollectionEvent','','edu.ualberta.med.biobank.model.CollectionEvent','','','',2,'2011-02-15'),(186,'edu.ualberta.med.biobank.model.ProcessingEvent','','edu.ualberta.med.biobank.model.ProcessingEvent','','','',2,'2011-02-15'),(187,'edu.ualberta.med.biobank.model.SpecimenType','edu.ualberta.med.biobank.model.SpecimenType','edu.ualberta.med.biobank.model.SpecimenType','','','',2,'2011-02-28'),(188,'edu.ualberta.med.biobank.model.Center','','edu.ualberta.med.biobank.model.Center','','','',2,'2011-02-15'),(192,'edu.ualberta.med.biobank.model.RequestSpecimen','','edu.ualberta.med.biobank.model.RequestSpecimen','','','',2,'2011-02-28'),(193,'edu.ualberta.med.biobank.model.ShipmentInfo','edu.ualberta.med.biobank.model.ShipmentInfo','edu.ualberta.med.biobank.model.ShipmentInfo','','','',2,'2011-02-28'),(195,'edu.ualberta.med.biobank.model.PrintedSsInvItem','','edu.ualberta.med.biobank.model.PrintedSsInvItem','','','',2,'2011-06-06'),(196,'edu.ualberta.med.biobank.model.PrinterLabelTemplate','','edu.ualberta.med.biobank.model.PrinterLabelTemplate','','','',2,'2011-06-06'),(197,'edu.ualberta.med.biobank.model.JasperTemplate','','edu.ualberta.med.biobank.model.JasperTemplate','','','',2,'2011-06-07'),(198,'edu.ualberta.med.biobank.model.User','','edu.ualberta.med.biobank.model.User','','','',2,'2011-08-15'),(199,'edu.ualberta.med.biobank.model.Group','','edu.ualberta.med.biobank.model.Group','','','',2,'2011-08-15'),(200,'edu.ualberta.med.biobank.model.Principal','','edu.ualberta.med.biobank.model.Principal','','','',2,'2011-08-15'),(201,'edu.ualberta.med.biobank.model.Membership','','edu.ualberta.med.biobank.model.Membership','','','',2,'2011-08-15'),(202,'edu.ualberta.med.biobank.model.Permission','','edu.ualberta.med.biobank.model.Permission','','','',2,'2011-08-15'),(203,'edu.ualberta.med.biobank.model.Role','','edu.ualberta.med.biobank.model.Role','','','',2,'2011-08-15'),(207,'edu.ualberta.med.biobank.model.Comment',NULL,'edu.ualberta.med.biobank.model.Comment',NULL,NULL,NULL,2,'2012-11-26');
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
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=latin1;
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
INSERT INTO `csm_role_privilege` VALUES (19,8,1),(18,8,3),(20,8,5),(17,8,6);
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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user`
--

LOCK TABLES `csm_user` WRITE;
/*!40000 ALTER TABLE `csm_user` DISABLE KEYS */;
INSERT INTO `csm_user` VALUES (1,'administrator',0,'Administrator','NoName',NULL,NULL,NULL,NULL,'zJPWCwDeSgG8j2uyHEABIQ==',NULL,NULL,NULL,'2009-07-22',NULL),(2,'bbadmin',0,'Biobank Administrator','NoName',NULL,NULL,NULL,NULL,'7Bg9siN5e7M=',NULL,NULL,NULL,'2009-07-22',NULL),(27,'testuser',0,'testuser','testuser',NULL,NULL,NULL,NULL,'orDBlaojDQE=',NULL,NULL,NULL,'2012-11-25',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=223 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_group_role_pg`
--

LOCK TABLES `csm_user_group_role_pg` WRITE;
/*!40000 ALTER TABLE `csm_user_group_role_pg` DISABLE KEYS */;
INSERT INTO `csm_user_group_role_pg` VALUES (222,27,NULL,8,1,'2012-11-25');
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
INSERT INTO `csm_user_pe` VALUES (1,1,1),(2,2,2);
/*!40000 ALTER TABLE `csm_user_pe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispatch`
--

DROP TABLE IF EXISTS `dispatch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dispatch` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `STATE` int(11) NOT NULL,
  `RECEIVER_CENTER_ID` int(11) NOT NULL,
  `SENDER_CENTER_ID` int(11) NOT NULL,
  `SHIPMENT_INFO_ID` int(11) DEFAULT NULL,
  `REQUEST_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SHIPMENT_INFO_ID` (`SHIPMENT_INFO_ID`),
  KEY `FK3F9F347AA2F14F4F` (`REQUEST_ID`),
  KEY `FK3F9F347A307B2CB5` (`RECEIVER_CENTER_ID`),
  KEY `FK3F9F347AF59D873A` (`SHIPMENT_INFO_ID`),
  KEY `FK3F9F347A91BC3D7B` (`SENDER_CENTER_ID`),
  CONSTRAINT `FK3F9F347A91BC3D7B` FOREIGN KEY (`SENDER_CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK3F9F347A307B2CB5` FOREIGN KEY (`RECEIVER_CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK3F9F347AA2F14F4F` FOREIGN KEY (`REQUEST_ID`) REFERENCES `request` (`ID`),
  CONSTRAINT `FK3F9F347AF59D873A` FOREIGN KEY (`SHIPMENT_INFO_ID`) REFERENCES `shipment_info` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dispatch`
--

LOCK TABLES `dispatch` WRITE;
/*!40000 ALTER TABLE `dispatch` DISABLE KEYS */;
/*!40000 ALTER TABLE `dispatch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispatch_comment`
--

DROP TABLE IF EXISTS `dispatch_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dispatch_comment` (
  `DISPATCH_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`DISPATCH_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FKAFC93B7ACDA9FD4F` (`COMMENT_ID`),
  KEY `FKAFC93B7ADE99CA25` (`DISPATCH_ID`),
  CONSTRAINT `FKAFC93B7ADE99CA25` FOREIGN KEY (`DISPATCH_ID`) REFERENCES `dispatch` (`ID`),
  CONSTRAINT `FKAFC93B7ACDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dispatch_comment`
--

LOCK TABLES `dispatch_comment` WRITE;
/*!40000 ALTER TABLE `dispatch_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `dispatch_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispatch_specimen`
--

DROP TABLE IF EXISTS `dispatch_specimen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dispatch_specimen` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `STATE` int(11) NOT NULL,
  `DISPATCH_ID` int(11) NOT NULL,
  `SPECIMEN_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `DISPATCH_ID` (`DISPATCH_ID`,`SPECIMEN_ID`),
  KEY `FKEE25592DEF199765` (`SPECIMEN_ID`),
  KEY `FKEE25592DDE99CA25` (`DISPATCH_ID`),
  CONSTRAINT `FKEE25592DDE99CA25` FOREIGN KEY (`DISPATCH_ID`) REFERENCES `dispatch` (`ID`),
  CONSTRAINT `FKEE25592DEF199765` FOREIGN KEY (`SPECIMEN_ID`) REFERENCES `specimen` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dispatch_specimen`
--

LOCK TABLES `dispatch_specimen` WRITE;
/*!40000 ALTER TABLE `dispatch_specimen` DISABLE KEYS */;
/*!40000 ALTER TABLE `dispatch_specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dispatch_specimen_comment`
--

DROP TABLE IF EXISTS `dispatch_specimen_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dispatch_specimen_comment` (
  `DISPATCH_SPECIMEN_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`DISPATCH_SPECIMEN_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FKC3C4FD2DBCCB06BA` (`DISPATCH_SPECIMEN_ID`),
  KEY `FKC3C4FD2DCDA9FD4F` (`COMMENT_ID`),
  CONSTRAINT `FKC3C4FD2DCDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`),
  CONSTRAINT `FKC3C4FD2DBCCB06BA` FOREIGN KEY (`DISPATCH_SPECIMEN_ID`) REFERENCES `dispatch_specimen` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dispatch_specimen_comment`
--

LOCK TABLES `dispatch_specimen_comment` WRITE;
/*!40000 ALTER TABLE `dispatch_specimen_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `dispatch_specimen_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domain`
--

DROP TABLE IF EXISTS `domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ALL_CENTERS` bit(1) DEFAULT NULL,
  `ALL_STUDIES` bit(1) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domain`
--

LOCK TABLES `domain` WRITE;
/*!40000 ALTER TABLE `domain` DISABLE KEYS */;
INSERT INTO `domain` VALUES (1,0,'','');
/*!40000 ALTER TABLE `domain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domain_center`
--

DROP TABLE IF EXISTS `domain_center`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_center` (
  `DOMAIN_ID` int(11) NOT NULL,
  `CENTER_ID` int(11) NOT NULL,
  PRIMARY KEY (`DOMAIN_ID`,`CENTER_ID`),
  KEY `FK8FE45030E3301CA5` (`DOMAIN_ID`),
  KEY `FK8FE4503092FAA705` (`CENTER_ID`),
  CONSTRAINT `FK8FE4503092FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK8FE45030E3301CA5` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domain` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domain_center`
--

LOCK TABLES `domain_center` WRITE;
/*!40000 ALTER TABLE `domain_center` DISABLE KEYS */;
/*!40000 ALTER TABLE `domain_center` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domain_study`
--

DROP TABLE IF EXISTS `domain_study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_study` (
  `DOMAIN_ID` int(11) NOT NULL,
  `CENTER_ID` int(11) NOT NULL,
  PRIMARY KEY (`DOMAIN_ID`,`CENTER_ID`),
  KEY `FK816B9E6EE3301CA5` (`DOMAIN_ID`),
  KEY `FK816B9E6E5BB96C43` (`CENTER_ID`),
  CONSTRAINT `FK816B9E6E5BB96C43` FOREIGN KEY (`CENTER_ID`) REFERENCES `study` (`ID`),
  CONSTRAINT `FK816B9E6EE3301CA5` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domain` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domain_study`
--

LOCK TABLES `domain_study` WRITE;
/*!40000 ALTER TABLE `domain_study` DISABLE KEYS */;
/*!40000 ALTER TABLE `domain_study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity`
--

DROP TABLE IF EXISTS `entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity` (
  `ID` int(11) NOT NULL,
  `CLASS_NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity`
--

LOCK TABLES `entity` WRITE;
/*!40000 ALTER TABLE `entity` DISABLE KEYS */;
INSERT INTO `entity` VALUES (1,'edu.ualberta.med.biobank.model.Specimen','Specimen',0),(2,'edu.ualberta.med.biobank.model.Container','Container',0),(3,'edu.ualberta.med.biobank.model.Patient','Patient',0),(4,'edu.ualberta.med.biobank.model.CollectionEvent','Collection Event',0),(5,'edu.ualberta.med.biobank.model.ProcessingEvent','Processing Event',0);
/*!40000 ALTER TABLE `entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_column`
--

DROP TABLE IF EXISTS `entity_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity_column` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `ENTITY_PROPERTY_ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK16BD7321698D6AC` (`ENTITY_PROPERTY_ID`),
  CONSTRAINT `FK16BD7321698D6AC` FOREIGN KEY (`ENTITY_PROPERTY_ID`) REFERENCES `entity_property` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_column`
--

LOCK TABLES `entity_column` WRITE;
/*!40000 ALTER TABLE `entity_column` DISABLE KEYS */;
INSERT INTO `entity_column` VALUES (1,'Inventory Id',1,0),(2,'Creation Time',2,0),(3,'Comment',3,0),(4,'Quantity',4,0),(5,'Activity Status',5,0),(6,'Container Product Barcode',7,0),(7,'Container Label',8,0),(8,'Specimen Type',9,0),(9,'Time Processed',10,0),(11,'Patient Number',12,0),(12,'Top Container Type',13,0),(13,'Aliquot Position',14,0),(14,'Current Center',15,0),(15,'Study',16,0),(16,'Shipment Time Received',17,0),(17,'Shipment Waybill',18,0),(18,'Shipment Time Sent',19,0),(19,'Shipment Box Number',20,0),(20,'Source Center',21,0),(21,'Dispatch Sender',22,0),(22,'Dispatch Receiver',23,0),(23,'Dispatch Time Received',24,0),(24,'Dispatch Time Sent',25,0),(25,'Dispatch Waybill',26,0),(26,'Dispatch Box Number',27,0),(27,'Visit Number',28,0),(28,'Source Specimen Inventory Id',29,0),(29,'Source Specimen Source Center',30,0),(30,'Time Drawn',31,0),(101,'Product Barcode',101,0),(102,'Comment',102,0),(103,'Label',103,0),(104,'Temperature',104,0),(105,'Top Container Type',110,0),(106,'Specimen Creation Time',106,0),(107,'Container Type',107,0),(108,'Site',109,0),(201,'Patient Number',201,0),(202,'Study',202,0),(203,'Specimen Time Processed',203,0),(204,'Specimen Creation Time',204,0),(205,'Source Center',205,0),(206,'Visit Number',207,0),(301,'Specimen Time Processed',301,0),(302,'Specimen Creation Time',302,0),(303,'Comment',303,0),(304,'Patient Number',304,0),(305,'Specimen Source Center',305,0),(306,'Study',306,0),(307,'Visit Number',307,0),(401,'Worksheet',401,0),(402,'Creation Time',402,0),(403,'Comment',403,0),(404,'Center',404,0),(405,'Activity Status',405,0),(406,'Specimen Inventory Id',406,0),(407,'Specimen Creation Time',407,0);
/*!40000 ALTER TABLE `entity_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_filter`
--

DROP TABLE IF EXISTS `entity_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity_filter` (
  `ID` int(11) NOT NULL,
  `FILTER_TYPE` int(11) DEFAULT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `ENTITY_PROPERTY_ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK635CF541698D6AC` (`ENTITY_PROPERTY_ID`),
  CONSTRAINT `FK635CF541698D6AC` FOREIGN KEY (`ENTITY_PROPERTY_ID`) REFERENCES `entity_property` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_filter`
--

LOCK TABLES `entity_filter` WRITE;
/*!40000 ALTER TABLE `entity_filter` DISABLE KEYS */;
INSERT INTO `entity_filter` VALUES (1,1,'Inventory Id',1,0),(2,3,'Creation Time',2,0),(3,1,'Comment',3,0),(4,2,'Quantity',4,0),(5,8,'Activity Status',5,0),(6,1,'Container Product Barcode',7,0),(7,1,'Container Label',8,0),(8,1,'Specimen Type',9,0),(9,3,'Time Processed',10,0),(11,1,'Patient Number',12,0),(12,4,'Top Container',6,0),(13,1,'Current Center',15,0),(14,1,'Study',16,0),(15,3,'Shipment Time Received',17,0),(16,1,'Shipment Waybill',18,0),(17,3,'Shipment Time Sent',19,0),(18,1,'Shipment Box Number',20,0),(19,1,'Source Center',21,0),(21,1,'Dispatch Sender',22,0),(22,1,'Dispatch Receiver',23,0),(23,3,'Dispatch Time Received',24,0),(24,3,'Dispatch Time Sent',25,0),(25,1,'Dispatch Waybill',26,0),(26,1,'Dispatch Box Number',27,0),(27,7,'Visit Number',28,0),(28,1,'Source Specimen Inventory Id',29,0),(29,1,'Source Specimen Source Center',30,0),(30,3,'Time Drawn',31,0),(101,1,'Product Box Number',101,0),(102,1,'Comment',102,0),(103,1,'Label',103,0),(104,2,'Temperature',104,0),(105,4,'Top Container',105,0),(106,3,'Specimen Creation Time',106,0),(107,1,'Container Type',107,0),(108,5,'Is Top Level',108,0),(109,1,'Site',109,0),(201,1,'Patient Number',201,0),(202,1,'Study',202,0),(203,3,'Specimen Time Processed',203,0),(204,3,'Specimen Creation Time',204,0),(205,1,'Source Center',205,0),(206,6,'First Time Processed',204,0),(207,1,'Inventory Id',206,0),(208,7,'Visit Number',207,0),(301,3,'Specimen Time Processed',301,0),(302,3,'Specimen Creation Time',302,0),(303,1,'Comment',303,0),(304,1,'Patient Number',304,0),(305,1,'Specimen Source Center',305,0),(306,1,'Study',306,0),(307,6,'First Time Processed',301,0),(308,7,'Visit Number',307,0),(401,1,'Worksheet',401,0),(402,3,'Creation Time',402,0),(403,1,'Comment',403,0),(404,1,'Center',404,0),(405,8,'Activity Status',405,0),(406,1,'Specimen Inventory Id',406,0),(407,3,'Specimen Creation Time',407,0);
/*!40000 ALTER TABLE `entity_filter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_property`
--

DROP TABLE IF EXISTS `entity_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity_property` (
  `ID` int(11) NOT NULL,
  `PROPERTY` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `PROPERTY_TYPE_ID` int(11) NOT NULL,
  `ENTITY_ID` int(11) DEFAULT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK3FC956B191CFD445` (`ENTITY_ID`),
  KEY `FK3FC956B157C0C3B0` (`PROPERTY_TYPE_ID`),
  CONSTRAINT `FK3FC956B157C0C3B0` FOREIGN KEY (`PROPERTY_TYPE_ID`) REFERENCES `property_type` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK3FC956B191CFD445` FOREIGN KEY (`ENTITY_ID`) REFERENCES `entity` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_property`
--

LOCK TABLES `entity_property` WRITE;
/*!40000 ALTER TABLE `entity_property` DISABLE KEYS */;
INSERT INTO `entity_property` VALUES (1,'inventoryId',1,1,0),(2,'createdAt',3,1,0),(3,'comments.message',1,1,0),(4,'quantity',2,1,0),(5,'activityStatus',1,1,0),(6,'specimenPosition.container.topContainer.id',2,1,0),(7,'specimenPosition.container.productBarcode',1,1,0),(8,'specimenPosition.container.label',1,1,0),(9,'specimenType.nameShort',1,1,0),(10,'parentSpecimen.processingEvent.createdAt',3,1,0),(12,'collectionEvent.patient.pnumber',1,1,0),(13,'specimenPosition.container.topContainer.containerType.nameShort',1,1,0),(14,'specimenPosition.positionString',1,1,0),(15,'currentCenter.nameShort',1,1,0),(16,'collectionEvent.patient.study.nameShort',1,1,0),(17,'originInfo.shipmentInfo.receivedAt',3,1,0),(18,'originInfo.shipmentInfo.waybill',1,1,0),(19,'originInfo.shipmentInfo.packedAt',3,1,0),(20,'originInfo.shipmentInfo.boxNumber',1,1,0),(21,'originInfo.center.nameShort',1,1,0),(22,'dispatchSpecimens.dispatch.senderCenter.nameShort',1,1,0),(23,'dispatchSpecimens.dispatch.receiverCenter.nameShort',1,1,0),(24,'dispatchSpecimens.dispatch.shipmentInfo.receivedAt',3,1,0),(25,'dispatchSpecimens.dispatch.shipmentInfo.packedAt',3,1,0),(26,'dispatchSpecimens.dispatch.shipmentInfo.waybill',1,1,0),(27,'dispatchSpecimens.dispatch.shipmentInfo.boxNumber',1,1,0),(28,'collectionEvent.visitNumber',2,1,0),(29,'topSpecimen.inventoryId',1,1,0),(30,'topSpecimen.originInfo.center.nameShort',1,1,0),(31,'topSpecimen.createdAt',3,1,0),(101,'productBarcode',1,2,0),(102,'comments.message',1,2,0),(103,'label',1,2,0),(104,'temperature',2,2,0),(105,'topContainer.id',2,2,0),(106,'specimenPositions.specimen.createdAt',3,2,0),(107,'containerType.nameShort',1,2,0),(108,'containerType.topLevel',4,2,0),(109,'site.nameShort',1,2,0),(110,'topContainer.containerType.nameShort',1,2,0),(201,'pnumber',1,3,0),(202,'study.nameShort',1,3,0),(203,'collectionEvents.allSpecimens.parentSpecimen.processingEvent.createdAt',3,3,0),(204,'collectionEvents.allSpecimens.createdAt',3,3,0),(205,'collectionEvents.allSpecimens.originInfo.center.nameShort',1,3,0),(206,'collectionEvents.allSpecimens.inventoryId',1,3,0),(207,'collectionEvents.visitNumber',2,3,0),(301,'allSpecimens.parentSpecimen.processingEvent.createdAt',3,4,0),(302,'allSpecimens.createdAt',3,4,0),(303,'comments.message',1,4,0),(304,'patient.pnumber',1,4,0),(305,'allSpecimens.originInfo.center.nameShort',1,4,0),(306,'patient.study.nameShort',1,4,0),(307,'visitNumber',2,4,0),(401,'worksheet',1,5,0),(402,'createdAt',3,5,0),(403,'comments.message',1,5,0),(404,'center.nameShort',1,5,0),(405,'activityStatus',1,5,0),(406,'specimens.inventoryId',1,5,0),(407,'specimens.createdAt',3,5,0);
/*!40000 ALTER TABLE `entity_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_attr`
--

DROP TABLE IF EXISTS `event_attr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_attr` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `VALUE` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `COLLECTION_EVENT_ID` int(11) NOT NULL,
  `STUDY_EVENT_ATTR_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK59508C96280272F2` (`COLLECTION_EVENT_ID`),
  KEY `FK59508C96A9CFCFDB` (`STUDY_EVENT_ATTR_ID`),
  CONSTRAINT `FK59508C96A9CFCFDB` FOREIGN KEY (`STUDY_EVENT_ATTR_ID`) REFERENCES `study_event_attr` (`ID`),
  CONSTRAINT `FK59508C96280272F2` FOREIGN KEY (`COLLECTION_EVENT_ID`) REFERENCES `collection_event` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_attr`
--

LOCK TABLES `event_attr` WRITE;
/*!40000 ALTER TABLE `event_attr` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_attr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_attr_type`
--

DROP TABLE IF EXISTS `event_attr_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_attr_type` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(50) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_attr_type`
--

LOCK TABLES `event_attr_type` WRITE;
/*!40000 ALTER TABLE `event_attr_type` DISABLE KEYS */;
INSERT INTO `event_attr_type` VALUES (1,0,'number'),(2,0,'text'),(3,0,'date_time'),(4,0,'select_single'),(5,0,'select_multiple');
/*!40000 ALTER TABLE `event_attr_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_data`
--

DROP TABLE IF EXISTS `file_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_data` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `COMPRESSED_BYTES` longblob NOT NULL,
  `COMPRESSED_SIZE` bigint(20) NOT NULL,
  `FILE_META_DATA_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `FILE_META_DATA_ID` (`FILE_META_DATA_ID`),
  KEY `FK595EC08DA965B18F` (`FILE_META_DATA_ID`),
  CONSTRAINT `FK595EC08DA965B18F` FOREIGN KEY (`FILE_META_DATA_ID`) REFERENCES `file_meta_data` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_data`
--

LOCK TABLES `file_data` WRITE;
/*!40000 ALTER TABLE `file_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `file_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_meta_data`
--

DROP TABLE IF EXISTS `file_meta_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file_meta_data` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CONTENT_TYPE` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `CREATED_AT` datetime NOT NULL,
  `DESCRIPTION` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `MD5_HASH` binary(16) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `SHA1_HASH` binary(20) NOT NULL,
  `SIZE` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_meta_data`
--

LOCK TABLES `file_meta_data` WRITE;
/*!40000 ALTER TABLE `file_meta_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `file_meta_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `global_event_attr`
--

DROP TABLE IF EXISTS `global_event_attr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `global_event_attr` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `LABEL` varchar(50) COLLATE latin1_general_cs NOT NULL,
  `EVENT_ATTR_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `LABEL` (`LABEL`),
  KEY `FKBE7ED6B25B770B31` (`EVENT_ATTR_TYPE_ID`),
  CONSTRAINT `FKBE7ED6B25B770B31` FOREIGN KEY (`EVENT_ATTR_TYPE_ID`) REFERENCES `event_attr_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `global_event_attr`
--

LOCK TABLES `global_event_attr` WRITE;
/*!40000 ALTER TABLE `global_event_attr` DISABLE KEYS */;
INSERT INTO `global_event_attr` VALUES (1,0,'PBMC Count (x10^6)',1),(3,0,'Consent',5),(4,0,'Phlebotomist',2),(5,0,'Visit Type',5),(6,0,'Biopsy Length',1),(7,0,'Patient Type',4);
/*!40000 ALTER TABLE `global_event_attr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_user`
--

DROP TABLE IF EXISTS `group_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_user` (
  `GROUP_ID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  PRIMARY KEY (`GROUP_ID`,`USER_ID`),
  KEY `FK6B1EC1ABB9634A05` (`USER_ID`),
  KEY `FK6B1EC1ABA04C028F` (`GROUP_ID`),
  CONSTRAINT `FK6B1EC1ABA04C028F` FOREIGN KEY (`GROUP_ID`) REFERENCES `principal` (`ID`),
  CONSTRAINT `FK6B1EC1ABB9634A05` FOREIGN KEY (`USER_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_user`
--

LOCK TABLES `group_user` WRITE;
/*!40000 ALTER TABLE `group_user` DISABLE KEYS */;
INSERT INTO `group_user` VALUES (1,2);
/*!40000 ALTER TABLE `group_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequences`
--

DROP TABLE IF EXISTS `hibernate_sequences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequences` (
  `sequence_name` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `next_val` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequences`
--

LOCK TABLES `hibernate_sequences` WRITE;
/*!40000 ALTER TABLE `hibernate_sequences` DISABLE KEYS */;
/*!40000 ALTER TABLE `hibernate_sequences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jasper_template`
--

DROP TABLE IF EXISTS `jasper_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jasper_template` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(50) COLLATE latin1_general_cs NOT NULL,
  `XML` text COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jasper_template`
--

LOCK TABLES `jasper_template` WRITE;
/*!40000 ALTER TABLE `jasper_template` DISABLE KEYS */;
INSERT INTO `jasper_template` VALUES (1,0,'Patient with Source Specimens Jasper Template','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" name=\"CBSR Template\" pageWidth=\"612\" pageHeight=\"792\" columnWidth=\"555\" leftMargin=\"0\" rightMargin=\"0\" topMargin=\"0\" bottomMargin=\"0\">\n	<property name=\"com.jasperassistant.designer.Units\" value=\"Millimeters\"/>\n	<property name=\"ireport.zoom\" value=\"0.8264462809917362\"/>\n	<property name=\"ireport.x\" value=\"0\"/>\n	<property name=\"ireport.y\" value=\"0\"/>\n	<style name=\"table\">\n		<box>\n			<pen lineWidth=\"1.0\" lineColor=\"#000000\"/>\n		</box>\n	</style>\n	<style name=\"table_TH\" mode=\"Opaque\" backcolor=\"#F0F8FF\">\n		<box>\n			<pen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n		</box>\n	</style>\n	<style name=\"table_CH\" mode=\"Opaque\" backcolor=\"#BFE1FF\">\n		<box>\n			<pen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n		</box>\n	</style>\n	<style name=\"table_TD\" mode=\"Opaque\" backcolor=\"#FFFFFF\">\n		<box>\n			<pen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n		</box>\n	</style>\n	<subDataset name=\"Table Dataset 1\"/>\n	<parameter name=\"PROJECT_TITLE\" class=\"java.lang.String\"/>\n	<parameter name=\"LOGO\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_INFO_IMG\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_0\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_1\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_2\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_3\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_4\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_5\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_6\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_7\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_8\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_9\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_10\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_11\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_12\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_13\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_14\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_15\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_16\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_17\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_18\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_19\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_20\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_21\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_22\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_23\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_24\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_25\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_26\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_27\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_28\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_29\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_30\" class=\"java.io.InputStream\"/>\n	<parameter name=\"PATIENT_BARCODE_31\" class=\"java.io.InputStream\"/>\n	<title>\n		<band height=\"280\">\n			<textField isBlankWhenNull=\"true\">\n				<reportElement x=\"246\" y=\"14\" width=\"132\" height=\"17\" isRemoveLineWhenBlank=\"true\"/>\n				<textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n					<font isPdfEmbedded=\"false\"/>\n				</textElement>\n				<textFieldExpression class=\"java.lang.String\"><![CDATA[$P{PROJECT_TITLE}]]></textFieldExpression>\n			</textField>\n			<image>\n				<reportElement x=\"490\" y=\"17\" width=\"100\" height=\"43\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{LOGO}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement key=\"PATIENT_INFO_IMG\" x=\"17\" y=\"102\" width=\"238\" height=\"160\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_INFO_IMG}]]></imageExpression>\n			</image>\n		</band>\n	</title>\n	<pageFooter>\n		<band height=\"512\">\n			<image>\n				<reportElement x=\"0\" y=\"24\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_0}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"153\" y=\"23\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_1}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"305\" y=\"23\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_2}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"458\" y=\"21\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_3}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"0\" y=\"84\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_4}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"153\" y=\"83\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_5}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"305\" y=\"83\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_6}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"458\" y=\"82\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_7}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"0\" y=\"145\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_8}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"153\" y=\"143\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_9}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"305\" y=\"143\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_10}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"458\" y=\"142\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_11}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"0\" y=\"205\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_12}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"153\" y=\"203\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_13}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"305\" y=\"204\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_14}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"458\" y=\"203\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_15}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"0\" y=\"265\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_16}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"153\" y=\"263\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_17}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"305\" y=\"264\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_18}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"458\" y=\"263\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_19}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"0\" y=\"325\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_20}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"153\" y=\"324\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_21}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"305\" y=\"324\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_22}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"458\" y=\"324\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_23}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"0\" y=\"385\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_24}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"153\" y=\"384\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_25}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"305\" y=\"384\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_26}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"458\" y=\"384\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_27}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"0\" y=\"444\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_28}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"153\" y=\"444\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_29}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"305\" y=\"444\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_30}]]></imageExpression>\n			</image>\n			<image>\n				<reportElement x=\"458\" y=\"444\" width=\"153\" height=\"61\"/>\n				<imageExpression class=\"java.io.InputStream\"><![CDATA[$P{PATIENT_BARCODE_31}]]></imageExpression>\n			</image>\n		</band>\n	</pageFooter>\n</jasperReport>');
/*!40000 ALTER TABLE `jasper_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACTION` varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  `CENTER` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `CREATED_AT` datetime DEFAULT NULL,
  `DETAILS` text COLLATE latin1_general_cs,
  `INVENTORY_ID` varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  `LOCATION_LABEL` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `PATIENT_NUMBER` varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  `TYPE` varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  `USERNAME` varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log`
--

LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership`
--

DROP TABLE IF EXISTS `membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `membership` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `EVERY_PERMISSION` bit(1) DEFAULT NULL,
  `USER_MANAGER` bit(1) DEFAULT NULL,
  `DOMAIN_ID` int(11) NOT NULL,
  `PRINCIPAL_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `DOMAIN_ID` (`DOMAIN_ID`),
  KEY `FKCD0773D6FF154DAF` (`PRINCIPAL_ID`),
  KEY `FKCD0773D6E3301CA5` (`DOMAIN_ID`),
  CONSTRAINT `FKCD0773D6E3301CA5` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domain` (`ID`),
  CONSTRAINT `FKCD0773D6FF154DAF` FOREIGN KEY (`PRINCIPAL_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership`
--

LOCK TABLES `membership` WRITE;
/*!40000 ALTER TABLE `membership` DISABLE KEYS */;
INSERT INTO `membership` VALUES (1,0,'','',1,1);
/*!40000 ALTER TABLE `membership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_permission`
--

DROP TABLE IF EXISTS `membership_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `membership_permission` (
  `ID` int(11) NOT NULL,
  `PERMISSION_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`,`PERMISSION_ID`),
  KEY `FK1350F1D815E6F8DC` (`ID`),
  CONSTRAINT `FK1350F1D815E6F8DC` FOREIGN KEY (`ID`) REFERENCES `membership` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_permission`
--

LOCK TABLES `membership_permission` WRITE;
/*!40000 ALTER TABLE `membership_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `membership_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membership_role`
--

DROP TABLE IF EXISTS `membership_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `membership_role` (
  `MEMBERSHIP_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEMBERSHIP_ID`,`ROLE_ID`),
  KEY `FKEF36B33F14388625` (`ROLE_ID`),
  KEY `FKEF36B33FD26ABDE5` (`MEMBERSHIP_ID`),
  CONSTRAINT `FKEF36B33FD26ABDE5` FOREIGN KEY (`MEMBERSHIP_ID`) REFERENCES `membership` (`ID`),
  CONSTRAINT `FKEF36B33F14388625` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membership_role`
--

LOCK TABLES `membership_role` WRITE;
/*!40000 ALTER TABLE `membership_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `membership_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `origin_info`
--

DROP TABLE IF EXISTS `origin_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `origin_info` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CENTER_ID` int(11) NOT NULL,
  `RECEIVER_SITE_ID` int(11) DEFAULT NULL,
  `SHIPMENT_INFO_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SHIPMENT_INFO_ID` (`SHIPMENT_INFO_ID`),
  KEY `FKE92E7A275598FA35` (`RECEIVER_SITE_ID`),
  KEY `FKE92E7A27F59D873A` (`SHIPMENT_INFO_ID`),
  KEY `FKE92E7A2792FAA705` (`CENTER_ID`),
  CONSTRAINT `FKE92E7A2792FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKE92E7A275598FA35` FOREIGN KEY (`RECEIVER_SITE_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKE92E7A27F59D873A` FOREIGN KEY (`SHIPMENT_INFO_ID`) REFERENCES `shipment_info` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `origin_info`
--

LOCK TABLES `origin_info` WRITE;
/*!40000 ALTER TABLE `origin_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `origin_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `origin_info_comment`
--

DROP TABLE IF EXISTS `origin_info_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `origin_info_comment` (
  `ORIGIN_INFO_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`ORIGIN_INFO_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FKFE82842712E55F12` (`ORIGIN_INFO_ID`),
  KEY `FKFE828427CDA9FD4F` (`COMMENT_ID`),
  CONSTRAINT `FKFE828427CDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`),
  CONSTRAINT `FKFE82842712E55F12` FOREIGN KEY (`ORIGIN_INFO_ID`) REFERENCES `origin_info` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `origin_info_comment`
--

LOCK TABLES `origin_info_comment` WRITE;
/*!40000 ALTER TABLE `origin_info_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `origin_info_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CREATED_AT` datetime NOT NULL,
  `PNUMBER` varchar(100) COLLATE latin1_general_cs NOT NULL,
  `STUDY_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PNUMBER` (`PNUMBER`),
  KEY `FKFB9F76E5F2A2464F` (`STUDY_ID`),
  KEY `NUMBER_IDX` (`PNUMBER`),
  CONSTRAINT `FKFB9F76E5F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_comment`
--

DROP TABLE IF EXISTS `patient_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_comment` (
  `PATIENT_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`PATIENT_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FK901E2E5B563F38F` (`PATIENT_ID`),
  KEY `FK901E2E5CDA9FD4F` (`COMMENT_ID`),
  CONSTRAINT `FK901E2E5CDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`),
  CONSTRAINT `FK901E2E5B563F38F` FOREIGN KEY (`PATIENT_ID`) REFERENCES `patient` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_comment`
--

LOCK TABLES `patient_comment` WRITE;
/*!40000 ALTER TABLE `patient_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `patient_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `principal`
--

DROP TABLE IF EXISTS `principal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `principal` (
  `DISCRIMINATOR` varchar(31) COLLATE latin1_general_cs NOT NULL,
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `DESCRIPTION` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `CSM_USER_ID` bigint(20) DEFAULT NULL,
  `EMAIL` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `FULL_NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `LOGIN` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `NEED_PWD_CHANGE` bit(1) DEFAULT NULL,
  `RECV_BULK_EMAILS` bit(1) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `CSM_USER_ID` (`CSM_USER_ID`),
  UNIQUE KEY `EMAIL` (`EMAIL`),
  UNIQUE KEY `LOGIN` (`LOGIN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `principal`
--

LOCK TABLES `principal` WRITE;
/*!40000 ALTER TABLE `principal` DISABLE KEYS */;
INSERT INTO `principal` VALUES ('BbGroup',1,0,1,NULL,'Global Administrators',NULL,NULL,NULL,NULL,NULL,NULL),('User',2,0,1,NULL,NULL,27,NULL,'testuser testuser','testuser','\0','');
/*!40000 ALTER TABLE `principal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `printed_ss_inv_item`
--

DROP TABLE IF EXISTS `printed_ss_inv_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `printed_ss_inv_item` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `TXT` varchar(15) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `TXT` (`TXT`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `printer_label_template` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CONFIG_DATA` text COLLATE latin1_general_cs,
  `NAME` varchar(50) COLLATE latin1_general_cs NOT NULL,
  `PRINTER_NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `JASPER_TEMPLATE_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  KEY `FKC6463C6AA4B878C8` (`JASPER_TEMPLATE_ID`),
  CONSTRAINT `FKC6463C6AA4B878C8` FOREIGN KEY (`JASPER_TEMPLATE_ID`) REFERENCES `jasper_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `printer_label_template`
--

LOCK TABLES `printer_label_template` WRITE;
/*!40000 ALTER TABLE `printer_label_template` DISABLE KEYS */;
INSERT INTO `printer_label_template` VALUES (1,0,'<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><configuration><settings><entry><key>Patient Info.Custom Field 1.Field Text</key><value><x>1</x><y>4</y><width>0</width><height>0</height></value></entry><entry><key>Patient Info.Custom Field 1.1D Barcode</key><value><x>38</x><y>1</y><width>29</width><height>8</height></value></entry><entry><key>Patient Info.Custom Field 2.Field Text</key><value><x>1</x><y>13</y><width>0</width><height>0</height></value></entry><entry><key>Patient Info.Custom Field 2.1D Barcode</key><value><x>38</x><y>13</y><width>29</width><height>8</height></value></entry><entry><key>Patient Info.Custom Field 3.Field Text</key><value><x>1</x><y>25</y><width>0</width><height>0</height></value></entry><entry><key>Patient Info.Custom Field 3.1D Barcode</key><value><x>38</x><y>25</y><width>29</width><height>8</height></value></entry><entry><key>Patient Info.Patient ID.1D Barcode</key><value><x>1</x><y>33</y><width>29</width><height>8</height></value></entry><entry><key>Barcodes.General.Barcode 1D</key><value><x>8</x><y>7</y><width>29</width><height>8</height></value></entry><entry><key>Barcodes.General.Barcode 2D</key><value><x>40</x><y>7</y><width>6</width><height>6</height></value></entry><entry><key>Barcodes.General.Specimen Text</key><value><x>8</x><y>2</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 001.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 001.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 001.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 002.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 002.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 002.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 003.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 003.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 003.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 004.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 004.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 004.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 005.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 005.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 005.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 006.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 006.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 006.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 007.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 007.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 007.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 008.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 008.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 008.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 009.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 009.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 009.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 010.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 010.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 010.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 011.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 011.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 011.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 012.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 012.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 012.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 013.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 013.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 013.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 014.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 014.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 014.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 015.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 015.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 015.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 016.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 016.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 016.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 017.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 017.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 017.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 018.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 018.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 018.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 019.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 019.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 019.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 020.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 020.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 020.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 021.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 021.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 021.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 022.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 022.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 022.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 023.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 023.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 023.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 024.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 024.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 024.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 025.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 025.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 025.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 026.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 026.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 026.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 027.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 027.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 027.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 028.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 028.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 028.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 029.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 029.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 029.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 030.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 030.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 030.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 031.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 031.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 031.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 032.Barcode 1D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 032.Barcode 2D</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry><entry><key>Barcodes.Individual.Barcode 032.Specimen Text</key><value><x>0</x><y>0</y><width>0</width><height>0</height></value></entry></settings></configuration>','Patient with Source Specimens Label Template','default printer',1);
/*!40000 ALTER TABLE `printer_label_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processing_event`
--

DROP TABLE IF EXISTS `processing_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processing_event` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `CREATED_AT` datetime NOT NULL,
  `PROCESSED_BY` varchar(63) COLLATE latin1_general_cs DEFAULT NULL,
  `WORKSHEET` varchar(150) COLLATE latin1_general_cs NOT NULL,
  `CENTER_ID` int(11) NOT NULL,
  `PERSON_USER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `WORKSHEET` (`WORKSHEET`),
  KEY `FK327B1E4E21C4671B` (`PERSON_USER_ID`),
  KEY `FK327B1E4E92FAA705` (`CENTER_ID`),
  KEY `CREATED_AT_IDX` (`CREATED_AT`),
  CONSTRAINT `FK327B1E4E92FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK327B1E4E21C4671B` FOREIGN KEY (`PERSON_USER_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `processing_event`
--

LOCK TABLES `processing_event` WRITE;
/*!40000 ALTER TABLE `processing_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `processing_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processing_event_comment`
--

DROP TABLE IF EXISTS `processing_event_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `processing_event_comment` (
  `PROCESSING_EVENT_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`PROCESSING_EVENT_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FKA958114E33126C8` (`PROCESSING_EVENT_ID`),
  KEY `FKA958114ECDA9FD4F` (`COMMENT_ID`),
  CONSTRAINT `FKA958114ECDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`),
  CONSTRAINT `FKA958114E33126C8` FOREIGN KEY (`PROCESSING_EVENT_ID`) REFERENCES `processing_event` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `processing_event_comment`
--

LOCK TABLES `processing_event_comment` WRITE;
/*!40000 ALTER TABLE `processing_event_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `processing_event_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_modifier`
--

DROP TABLE IF EXISTS `property_modifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_modifier` (
  `ID` int(11) NOT NULL,
  `NAME` text COLLATE latin1_general_cs,
  `PROPERTY_MODIFIER` text COLLATE latin1_general_cs,
  `PROPERTY_TYPE_ID` int(11) DEFAULT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK5DF9160157C0C3B0` (`PROPERTY_TYPE_ID`),
  CONSTRAINT `FK5DF9160157C0C3B0` FOREIGN KEY (`PROPERTY_TYPE_ID`) REFERENCES `property_type` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `property_modifier`
--

LOCK TABLES `property_modifier` WRITE;
/*!40000 ALTER TABLE `property_modifier` DISABLE KEYS */;
INSERT INTO `property_modifier` VALUES (1,'Year','YEAR({value})',3,0),(2,'Year, Quarter','CONCAT(YEAR({value}), CONCAT(\'-\', QUARTER({value})))',3,0),(3,'Year, Month','CONCAT(YEAR({value}), CONCAT(\'-\', MONTH({value})))',3,0),(4,'Year, Week','CONCAT(YEAR({value}), CONCAT(\'-\', WEEK({value})))',3,0);
/*!40000 ALTER TABLE `property_modifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_type`
--

DROP TABLE IF EXISTS `property_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_type` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `property_type`
--

LOCK TABLES `property_type` WRITE;
/*!40000 ALTER TABLE `property_type` DISABLE KEYS */;
INSERT INTO `property_type` VALUES (1,'String',0),(2,'Number',0),(3,'Date',0),(4,'Boolean',0);
/*!40000 ALTER TABLE `property_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `DESCRIPTION` text COLLATE latin1_general_cs,
  `IS_COUNT` bit(1) NOT NULL,
  `IS_PUBLIC` bit(1) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  `ENTITY_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK8FDF493491CFD445` (`ENTITY_ID`),
  CONSTRAINT `FK8FDF493491CFD445` FOREIGN KEY (`ENTITY_ID`) REFERENCES `entity` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_column` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `POSITION` int(11) DEFAULT NULL,
  `COLUMN_ID` int(11) NOT NULL,
  `PROPERTY_MODIFIER_ID` int(11) DEFAULT NULL,
  `REPORT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKF0B78C1BE9306A5` (`REPORT_ID`),
  KEY `FKF0B78C1C2DE3790` (`PROPERTY_MODIFIER_ID`),
  KEY `FKF0B78C1A946D8E8` (`COLUMN_ID`),
  CONSTRAINT `FKF0B78C1A946D8E8` FOREIGN KEY (`COLUMN_ID`) REFERENCES `entity_column` (`ID`),
  CONSTRAINT `FKF0B78C1BE9306A5` FOREIGN KEY (`REPORT_ID`) REFERENCES `report` (`ID`),
  CONSTRAINT `FKF0B78C1C2DE3790` FOREIGN KEY (`PROPERTY_MODIFIER_ID`) REFERENCES `property_modifier` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_filter` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `OPERATOR` int(11) DEFAULT NULL,
  `POSITION` int(11) DEFAULT NULL,
  `ENTITY_FILTER_ID` int(11) NOT NULL,
  `REPORT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK13D570E3445CEC4C` (`ENTITY_FILTER_ID`),
  KEY `FK13D570E3BE9306A5` (`REPORT_ID`),
  CONSTRAINT `FK13D570E3BE9306A5` FOREIGN KEY (`REPORT_ID`) REFERENCES `report` (`ID`),
  CONSTRAINT `FK13D570E3445CEC4C` FOREIGN KEY (`ENTITY_FILTER_ID`) REFERENCES `entity_filter` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_filter_value` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `POSITION` int(11) DEFAULT NULL,
  `SECOND_VALUE` text COLLATE latin1_general_cs,
  `VALUE` text COLLATE latin1_general_cs,
  `REPORT_FILTER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK691EF6F59FFD1CEE` (`REPORT_FILTER_ID`),
  CONSTRAINT `FK691EF6F59FFD1CEE` FOREIGN KEY (`REPORT_FILTER_ID`) REFERENCES `report_filter` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `request` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CREATED` datetime NOT NULL,
  `SUBMITTED` datetime DEFAULT NULL,
  `ADDRESS_ID` int(11) NOT NULL,
  `RESEARCH_GROUP_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK6C1A7E6F4BD922D8` (`RESEARCH_GROUP_ID`),
  KEY `FK6C1A7E6F6AF2992F` (`ADDRESS_ID`),
  CONSTRAINT `FK6C1A7E6F6AF2992F` FOREIGN KEY (`ADDRESS_ID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK6C1A7E6F4BD922D8` FOREIGN KEY (`RESEARCH_GROUP_ID`) REFERENCES `center` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `request_specimen` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `CLAIMED_BY` varchar(50) COLLATE latin1_general_cs DEFAULT NULL,
  `STATE` int(11) NOT NULL,
  `REQUEST_ID` int(11) NOT NULL,
  `SPECIMEN_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK579572D8A2F14F4F` (`REQUEST_ID`),
  KEY `FK579572D8EF199765` (`SPECIMEN_ID`),
  CONSTRAINT `FK579572D8EF199765` FOREIGN KEY (`SPECIMEN_ID`) REFERENCES `specimen` (`ID`),
  CONSTRAINT `FK579572D8A2F14F4F` FOREIGN KEY (`REQUEST_ID`) REFERENCES `request` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_specimen`
--

LOCK TABLES `request_specimen` WRITE;
/*!40000 ALTER TABLE `request_specimen` DISABLE KEYS */;
/*!40000 ALTER TABLE `request_specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `revision`
--

DROP TABLE IF EXISTS `revision`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `revision` (
  `ID` int(11) NOT NULL,
  `CREATED_AT` datetime DEFAULT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK1F1AA7DBB9634A05` (`USER_ID`),
  CONSTRAINT `FK1F1AA7DBB9634A05` FOREIGN KEY (`USER_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revision`
--

LOCK TABLES `revision` WRITE;
/*!40000 ALTER TABLE `revision` DISABLE KEYS */;
/*!40000 ALTER TABLE `revision` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `revision_entity_type`
--

DROP TABLE IF EXISTS `revision_entity_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `revision_entity_type` (
  `ID` int(11) NOT NULL,
  `TYPE` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `REVISION_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK74D6F3B29D2D0285` (`REVISION_ID`),
  CONSTRAINT `FK74D6F3B29D2D0285` FOREIGN KEY (`REVISION_ID`) REFERENCES `revision` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revision_entity_type`
--

LOCK TABLES `revision_entity_type` WRITE;
/*!40000 ALTER TABLE `revision_entity_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `revision_entity_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
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

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_permission` (
  `ID` int(11) NOT NULL,
  `PERMISSION_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`,`PERMISSION_ID`),
  KEY `FK9C6EC938C226FDBC` (`ID`),
  CONSTRAINT `FK9C6EC938C226FDBC` FOREIGN KEY (`ID`) REFERENCES `role` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment_info`
--

DROP TABLE IF EXISTS `shipment_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shipment_info` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `BOX_NUMBER` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `PACKED_AT` datetime DEFAULT NULL,
  `RECEIVED_AT` datetime DEFAULT NULL,
  `WAYBILL` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `SHIPPING_METHOD_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK95BCA433DCA49682` (`SHIPPING_METHOD_ID`),
  KEY `WAYBILL_IDX` (`WAYBILL`),
  KEY `RECEIVED_AT_IDX` (`RECEIVED_AT`),
  CONSTRAINT `FK95BCA433DCA49682` FOREIGN KEY (`SHIPPING_METHOD_ID`) REFERENCES `shipping_method` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipment_info`
--

LOCK TABLES `shipment_info` WRITE;
/*!40000 ALTER TABLE `shipment_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_method`
--

DROP TABLE IF EXISTS `shipping_method`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shipping_method` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `site_study` (
  `SITE_ID` int(11) NOT NULL,
  `STUDY_ID` int(11) NOT NULL,
  PRIMARY KEY (`SITE_ID`,`STUDY_ID`),
  KEY `FK7A197EB1F2A2464F` (`STUDY_ID`),
  KEY `FK7A197EB13F52C885` (`SITE_ID`),
  CONSTRAINT `FK7A197EB13F52C885` FOREIGN KEY (`SITE_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK7A197EB1F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `site_study`
--

LOCK TABLES `site_study` WRITE;
/*!40000 ALTER TABLE `site_study` DISABLE KEYS */;
/*!40000 ALTER TABLE `site_study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `source_specimen`
--

DROP TABLE IF EXISTS `source_specimen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `source_specimen` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NEED_ORIGINAL_VOLUME` bit(1) DEFAULT NULL,
  `SPECIMEN_TYPE_ID` int(11) NOT NULL,
  `STUDY_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK28D36ACF2A2464F` (`STUDY_ID`),
  KEY `FK28D36AC38445996` (`SPECIMEN_TYPE_ID`),
  CONSTRAINT `FK28D36AC38445996` FOREIGN KEY (`SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`),
  CONSTRAINT `FK28D36ACF2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `source_specimen`
--

LOCK TABLES `source_specimen` WRITE;
/*!40000 ALTER TABLE `source_specimen` DISABLE KEYS */;
/*!40000 ALTER TABLE `source_specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specimen`
--

DROP TABLE IF EXISTS `specimen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `specimen` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `CREATED_AT` datetime NOT NULL,
  `INVENTORY_ID` varchar(100) COLLATE latin1_general_cs NOT NULL,
  `PLATE_ERRORS` text COLLATE latin1_general_cs,
  `QUANTITY` decimal(20,10) DEFAULT NULL,
  `SAMPLE_ERRORS` text COLLATE latin1_general_cs,
  `COLLECTION_EVENT_ID` int(11) NOT NULL,
  `CURRENT_CENTER_ID` int(11) NOT NULL,
  `ORIGIN_INFO_ID` int(11) NOT NULL,
  `ORIGINAL_COLLECTION_EVENT_ID` int(11) DEFAULT NULL,
  `PARENT_SPECIMEN_ID` int(11) DEFAULT NULL,
  `PROCESSING_EVENT_ID` int(11) DEFAULT NULL,
  `SPECIMEN_TYPE_ID` int(11) NOT NULL,
  `TOP_SPECIMEN_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `INVENTORY_ID` (`INVENTORY_ID`),
  KEY `FKAF84F308FBB79BBF` (`CURRENT_CENTER_ID`),
  KEY `FKAF84F30886857784` (`ORIGINAL_COLLECTION_EVENT_ID`),
  KEY `FKAF84F308280272F2` (`COLLECTION_EVENT_ID`),
  KEY `FKAF84F30812E55F12` (`ORIGIN_INFO_ID`),
  KEY `FKAF84F30861674F50` (`PARENT_SPECIMEN_ID`),
  KEY `FKAF84F30833126C8` (`PROCESSING_EVENT_ID`),
  KEY `FKAF84F30838445996` (`SPECIMEN_TYPE_ID`),
  KEY `FKAF84F308C9EF5F7B` (`TOP_SPECIMEN_ID`),
  KEY `INV_ID_IDX` (`INVENTORY_ID`),
  KEY `ID` (`ID`,`SPECIMEN_TYPE_ID`),
  CONSTRAINT `FKAF84F308C9EF5F7B` FOREIGN KEY (`TOP_SPECIMEN_ID`) REFERENCES `specimen` (`ID`),
  CONSTRAINT `FKAF84F30812E55F12` FOREIGN KEY (`ORIGIN_INFO_ID`) REFERENCES `origin_info` (`ID`),
  CONSTRAINT `FKAF84F308280272F2` FOREIGN KEY (`COLLECTION_EVENT_ID`) REFERENCES `collection_event` (`ID`),
  CONSTRAINT `FKAF84F30833126C8` FOREIGN KEY (`PROCESSING_EVENT_ID`) REFERENCES `processing_event` (`ID`),
  CONSTRAINT `FKAF84F30838445996` FOREIGN KEY (`SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`),
  CONSTRAINT `FKAF84F30861674F50` FOREIGN KEY (`PARENT_SPECIMEN_ID`) REFERENCES `specimen` (`ID`),
  CONSTRAINT `FKAF84F30886857784` FOREIGN KEY (`ORIGINAL_COLLECTION_EVENT_ID`) REFERENCES `collection_event` (`ID`),
  CONSTRAINT `FKAF84F308FBB79BBF` FOREIGN KEY (`CURRENT_CENTER_ID`) REFERENCES `center` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specimen`
--

LOCK TABLES `specimen` WRITE;
/*!40000 ALTER TABLE `specimen` DISABLE KEYS */;
/*!40000 ALTER TABLE `specimen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specimen_comment`
--

DROP TABLE IF EXISTS `specimen_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `specimen_comment` (
  `SPECIMEN_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`SPECIMEN_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FK73068C08EF199765` (`SPECIMEN_ID`),
  KEY `FK73068C08CDA9FD4F` (`COMMENT_ID`),
  CONSTRAINT `FK73068C08CDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`),
  CONSTRAINT `FK73068C08EF199765` FOREIGN KEY (`SPECIMEN_ID`) REFERENCES `specimen` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specimen_comment`
--

LOCK TABLES `specimen_comment` WRITE;
/*!40000 ALTER TABLE `specimen_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `specimen_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specimen_position`
--

DROP TABLE IF EXISTS `specimen_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `specimen_position` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `COL` int(11) NOT NULL,
  `ROW` int(11) NOT NULL,
  `POSITION_STRING` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `CONTAINER_ID` int(11) NOT NULL,
  `CONTAINER_TYPE_ID` int(11) NOT NULL,
  `SPECIMEN_ID` int(11) NOT NULL,
  `SPECIMEN_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SPECIMEN_ID` (`SPECIMEN_ID`),
  UNIQUE KEY `CONTAINER_ID` (`CONTAINER_ID`,`ROW`,`COL`),
  KEY `FK_SpecimenPosition_container` (`CONTAINER_ID`,`CONTAINER_TYPE_ID`),
  KEY `FK_SpecimenPosition_specimen` (`SPECIMEN_ID`,`SPECIMEN_TYPE_ID`),
  KEY `FK_SpecimenPosition_containerTypeSpecimenType` (`CONTAINER_TYPE_ID`,`SPECIMEN_TYPE_ID`),
  CONSTRAINT `FK_SpecimenPosition_containerTypeSpecimenType` FOREIGN KEY (`CONTAINER_TYPE_ID`, `SPECIMEN_TYPE_ID`) REFERENCES `container_type_specimen_type` (`CONTAINER_TYPE_ID`, `SPECIMEN_TYPE_ID`),
  CONSTRAINT `FK_SpecimenPosition_container` FOREIGN KEY (`CONTAINER_ID`, `CONTAINER_TYPE_ID`) REFERENCES `container` (`ID`, `CONTAINER_TYPE_ID`) ON UPDATE CASCADE,
  CONSTRAINT `FK_SpecimenPosition_specimen` FOREIGN KEY (`SPECIMEN_ID`, `SPECIMEN_TYPE_ID`) REFERENCES `specimen` (`ID`, `SPECIMEN_TYPE_ID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specimen_position`
--

LOCK TABLES `specimen_position` WRITE;
/*!40000 ALTER TABLE `specimen_position` DISABLE KEYS */;
/*!40000 ALTER TABLE `specimen_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specimen_type`
--

DROP TABLE IF EXISTS `specimen_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `specimen_type` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `NAME_SHORT` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `NAME_SHORT` (`NAME_SHORT`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `specimen_type_specimen_type` (
  `PARENT_SPECIMEN_TYPE_ID` int(11) NOT NULL,
  `CHILD_SPECIMEN_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`PARENT_SPECIMEN_TYPE_ID`,`CHILD_SPECIMEN_TYPE_ID`),
  KEY `FKD95844635F3DC8B` (`PARENT_SPECIMEN_TYPE_ID`),
  KEY `FKD9584463D9672259` (`CHILD_SPECIMEN_TYPE_ID`),
  CONSTRAINT `FKD9584463D9672259` FOREIGN KEY (`CHILD_SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`),
  CONSTRAINT `FKD95844635F3DC8B` FOREIGN KEY (`PARENT_SPECIMEN_TYPE_ID`) REFERENCES `specimen_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specimen_type_specimen_type`
--

LOCK TABLES `specimen_type_specimen_type` WRITE;
/*!40000 ALTER TABLE `specimen_type_specimen_type` DISABLE KEYS */;
INSERT INTO `specimen_type_specimen_type` VALUES (83,67),(83,68),(83,69),(83,70),(83,71),(83,72),(86,2),(86,4),(86,7),(86,8),(86,23),(86,24),(86,47),(86,51),(86,54),(86,58),(86,62),(86,63),(86,65),(86,73),(86,81),(87,2),(87,4),(87,7),(87,8),(87,23),(87,24),(87,47),(87,51),(87,54),(87,58),(87,62),(87,63),(87,65),(87,73),(87,81),(88,2),(88,4),(88,7),(88,8),(88,23),(88,24),(88,47),(88,51),(88,54),(88,58),(88,62),(88,63),(88,65),(88,73),(88,81),(89,2),(89,4),(89,7),(89,8),(89,23),(89,24),(89,47),(89,51),(89,54),(89,58),(89,62),(89,63),(89,65),(89,73),(89,81),(90,36),(90,60),(90,76),(91,35),(91,74),(91,75),(92,14),(93,39),(94,4),(94,6),(94,8),(94,15),(94,22),(94,24),(94,25),(94,46),(94,48),(94,49),(94,50),(95,15),(95,18),(95,52),(95,56),(95,57),(95,59),(95,61),(96,20),(97,38),(97,64),(97,66),(98,42),(99,5),(99,11),(99,38),(99,45),(99,53),(99,64),(99,66),(100,12),(101,43),(102,13),(106,2),(106,4),(106,7),(106,8),(106,23),(106,24),(106,47),(106,51),(106,54),(106,58),(106,62),(106,63),(106,65),(106,73),(106,81),(109,19),(114,55),(115,3),(116,15),(116,18),(116,52),(116,56),(116,57),(116,59),(116,61),(117,34),(118,1),(118,9),(118,16),(118,26),(118,27),(118,29),(118,33),(118,44),(119,10),(119,28),(119,31),(119,32),(119,40),(119,41),(120,16),(120,17),(120,29),(120,30),(121,4),(121,6),(121,8),(121,15),(121,22),(121,24),(121,25),(121,46),(121,48),(121,49),(121,50),(122,36),(122,60),(122,76),(124,2),(124,4),(124,7),(124,8),(124,23),(124,24),(124,47),(124,51),(124,54),(124,58),(124,62),(124,63),(124,65),(124,73),(124,81),(125,77);
/*!40000 ALTER TABLE `specimen_type_specimen_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study`
--

DROP TABLE IF EXISTS `study`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `study` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  `NAME_SHORT` varchar(50) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `NAME_SHORT` (`NAME_SHORT`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study`
--

LOCK TABLES `study` WRITE;
/*!40000 ALTER TABLE `study` DISABLE KEYS */;
/*!40000 ALTER TABLE `study` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_comment`
--

DROP TABLE IF EXISTS `study_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `study_comment` (
  `STUDY_ID` int(11) NOT NULL,
  `COMMENT_ID` int(11) NOT NULL,
  PRIMARY KEY (`STUDY_ID`,`COMMENT_ID`),
  UNIQUE KEY `COMMENT_ID` (`COMMENT_ID`),
  KEY `FKAA027DA9F2A2464F` (`STUDY_ID`),
  KEY `FKAA027DA9CDA9FD4F` (`COMMENT_ID`),
  CONSTRAINT `FKAA027DA9CDA9FD4F` FOREIGN KEY (`COMMENT_ID`) REFERENCES `comment` (`ID`),
  CONSTRAINT `FKAA027DA9F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_comment`
--

LOCK TABLES `study_comment` WRITE;
/*!40000 ALTER TABLE `study_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_contact`
--

DROP TABLE IF EXISTS `study_contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `study_contact` (
  `STUDY_ID` int(11) NOT NULL,
  `CONTACT_ID` int(11) NOT NULL,
  PRIMARY KEY (`STUDY_ID`,`CONTACT_ID`),
  KEY `FKAA13B36AF2A2464F` (`STUDY_ID`),
  KEY `FKAA13B36AA07999AF` (`CONTACT_ID`),
  CONSTRAINT `FKAA13B36AA07999AF` FOREIGN KEY (`CONTACT_ID`) REFERENCES `contact` (`ID`),
  CONSTRAINT `FKAA13B36AF2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_contact`
--

LOCK TABLES `study_contact` WRITE;
/*!40000 ALTER TABLE `study_contact` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `study_event_attr`
--

DROP TABLE IF EXISTS `study_event_attr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `study_event_attr` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `PERMISSIBLE` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `REQUIRED` bit(1) DEFAULT NULL,
  `GLOBAL_EVENT_ATTR_ID` int(11) NOT NULL,
  `STUDY_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK3EACD8ECF2A2464F` (`STUDY_ID`),
  KEY `FK3EACD8EC44556025` (`GLOBAL_EVENT_ATTR_ID`),
  CONSTRAINT `FK3EACD8EC44556025` FOREIGN KEY (`GLOBAL_EVENT_ATTR_ID`) REFERENCES `global_event_attr` (`ID`),
  CONSTRAINT `FK3EACD8ECF2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_event_attr`
--

LOCK TABLES `study_event_attr` WRITE;
/*!40000 ALTER TABLE `study_event_attr` DISABLE KEYS */;
/*!40000 ALTER TABLE `study_event_attr` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-11-25 19:10:43
