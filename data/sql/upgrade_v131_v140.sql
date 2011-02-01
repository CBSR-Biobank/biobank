RENAME TABLE clinic_shipment_patient TO shipment_patient;
RENAME TABLE dispatch_shipment_aliquot TO dispatch_aliquot;

ALTER TABLE abstract_shipment
      CHANGE COLUMN DATE_SHIPPED DEPARTED DATETIME NULL DEFAULT NULL COMMENT '';

ALTER TABLE dispatch_aliquot
      CHANGE COLUMN DISPATCH_SHIPMENT_ID DISPATCH_ID INT(11) NOT NULL COMMENT '',
      DROP INDEX FKB1B76907D8CEA57A,
      DROP INDEX FKB1B76907898584F,
      ADD INDEX FK40A7EAC2898584F (ALIQUOT_ID),
      ADD INDEX FK40A7EAC2DE99CA25 (DISPATCH_ID);

ALTER TABLE patient_visit
      CHANGE COLUMN CLINIC_SHIPMENT_PATIENT_ID SHIPMENT_PATIENT_ID INT(11) NOT NULL COMMENT '',
      DROP INDEX FKA09CAF5183AE7BBB,
      ADD INDEX FKA09CAF51859BF35A (SHIPMENT_PATIENT_ID);

ALTER TABLE shipment_patient
      CHANGE COLUMN CLINIC_SHIPMENT_ID SHIPMENT_ID INT(11) NOT NULL COMMENT '',
      DROP INDEX FKF4B18BB7E5B2B216,
      DROP INDEX FKF4B18BB7B563F38F,
      ADD INDEX FK68484540B1D3625 (SHIPMENT_ID),
      ADD INDEX FK68484540B563F38F (PATIENT_ID);

CREATE TABLE entity (
    ID INT(11) NOT NULL,
    CLASS_NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE entity_column (
    ID INT(11) NOT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    ENTITY_PROPERTY_ID INT(11) NOT NULL,
    INDEX FK16BD7321698D6AC (ENTITY_PROPERTY_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE entity_filter (
    ID INT(11) NOT NULL,
    FILTER_TYPE INT(11) NULL DEFAULT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    ENTITY_PROPERTY_ID INT(11) NOT NULL,
    INDEX FK635CF541698D6AC (ENTITY_PROPERTY_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE entity_property (
    ID INT(11) NOT NULL,
    PROPERTY VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PROPERTY_TYPE_ID INT(11) NOT NULL,
    ENTITY_ID INT(11) NULL DEFAULT NULL,
    INDEX FK3FC956B191CFD445 (ENTITY_ID),
    INDEX FK3FC956B157C0C3B0 (PROPERTY_TYPE_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE property_modifier (
    ID INT(11) NOT NULL,
    NAME TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PROPERTY_MODIFIER TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PROPERTY_TYPE_ID INT(11) NULL DEFAULT NULL,
    INDEX FK5DF9160157C0C3B0 (PROPERTY_TYPE_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE property_type (
    ID INT(11) NOT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE report (
    ID INT(11) NOT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    DESCRIPTION TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    USER_ID INT(11) NULL DEFAULT NULL,
    IS_PUBLIC TINYINT(1) NULL DEFAULT NULL,
    IS_COUNT TINYINT(1) NULL DEFAULT NULL,
    ENTITY_ID INT(11) NOT NULL,
    INDEX FK8FDF493491CFD445 (ENTITY_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE report_column (
    ID INT(11) NOT NULL,
    POSITION INT(11) NULL DEFAULT NULL,
    COLUMN_ID INT(11) NOT NULL,
    PROPERTY_MODIFIER_ID INT(11) NULL DEFAULT NULL,
    REPORT_ID INT(11) NULL DEFAULT NULL,
    INDEX FKF0B78C1BE9306A5 (REPORT_ID),
    INDEX FKF0B78C1C2DE3790 (PROPERTY_MODIFIER_ID),
    INDEX FKF0B78C1A946D8E8 (COLUMN_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE report_filter (
    ID INT(11) NOT NULL,
    POSITION INT(11) NULL DEFAULT NULL,
    OPERATOR INT(11) NULL DEFAULT NULL,
    ENTITY_FILTER_ID INT(11) NOT NULL,
    REPORT_ID INT(11) NULL DEFAULT NULL,
    INDEX FK13D570E3445CEC4C (ENTITY_FILTER_ID),
    INDEX FK13D570E3BE9306A5 (REPORT_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE report_filter_value (
    ID INT(11) NOT NULL,
    POSITION INT(11) NULL DEFAULT NULL,
    VALUE TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    SECOND_VALUE TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    REPORT_FILTER_ID INT(11) NULL DEFAULT NULL,
    INDEX FK691EF6F59FFD1CEE (REPORT_FILTER_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

# sample order tables

DROP TABLE IF EXISTS order_aliquot;

CREATE TABLE order_aliquot (
    ALIQUOT_ID INT(11) NOT NULL,
    ORDER_ID INT(11) NOT NULL,
    INDEX FK74AC5176898584F (ALIQUOT_ID),
    PRIMARY KEY (ORDER_ID, ALIQUOT_ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

DROP TABLE IF EXISTS research_group;

CREATE TABLE research_group (
    ID INT(11) NOT NULL,
    NAME VARCHAR(150) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    NAME_SHORT VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    STUDY_ID INT(11) NOT NULL,
    ADDRESS_ID INT(11) NOT NULL,
    CONSTRAINT STUDY_ID UNIQUE KEY(STUDY_ID),
    INDEX FK7E0432BB6AF2992F (ADDRESS_ID),
    INDEX FK7E0432BBF2A2464F (STUDY_ID),
    CONSTRAINT ADDRESS_ID UNIQUE KEY(ADDRESS_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

DROP TABLE IF EXISTS research_group_researcher;

CREATE TABLE research_group_researcher (
    RESEARCH_GROUP_ID INT(11) NOT NULL,
    RESEARCHER_ID INT(11) NOT NULL,
    INDEX FK83006F8C213C8A5 (RESEARCHER_ID),
    INDEX FK83006F8C4BD922D8 (RESEARCH_GROUP_ID),
    PRIMARY KEY (RESEARCHER_ID, RESEARCH_GROUP_ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

DROP TABLE IF EXISTS researcher;

CREATE TABLE researcher (
    ID INT(11) NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

DROP TABLE order_aliquot;
ALTER TABLE abstract_position ADD COLUMN POSITION_STRING VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL COMMENT '';
ALTER TABLE container_path ADD COLUMN TOP_CONTAINER_ID INT(11) NOT NULL COMMENT '', ADD INDEX FKB2C64D431BE0C379 (TOP_CONTAINER_ID);
ALTER TABLE patient ADD COLUMN CREATED_AT DATETIME NULL DEFAULT NULL COMMENT '';
ALTER TABLE patient_visit ADD COLUMN ACTIVITY_STATUS_ID INT(11) NOT NULL COMMENT '', ADD INDEX FKA09CAF51C449A4 (ACTIVITY_STATUS_ID);
CREATE TABLE request (
    ID INT(11) NOT NULL,
    SUBMITTED DATETIME NULL DEFAULT NULL,
    ACCEPTED DATETIME NULL DEFAULT NULL,
    SHIPPED DATETIME NULL DEFAULT NULL,
    WAYBILL VARCHAR(150) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    STATE INT(11) NULL DEFAULT NULL,
    SITE_ID INT(11) NOT NULL,
    STUDY_ID INT(11) NOT NULL,
    ADDRESS_ID INT(11) NOT NULL,
    INDEX FK6C1A7E6FF2A2464F (STUDY_ID),
    INDEX FK6C1A7E6F3F52C885 (SITE_ID),
    CONSTRAINT ADDRESS_ID UNIQUE KEY(ADDRESS_ID),
    INDEX FK6C1A7E6F6AF2992F (ADDRESS_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;
CREATE TABLE request_aliquot (
    ID INT(11) NOT NULL,
    STATE INT(11) NULL DEFAULT NULL,
    CLAIMED_BY VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    REQUEST_ID INT(11) NOT NULL,
    ALIQUOT_ID INT(11) NOT NULL,
    INDEX FK2B486FB7A2F14F4F (REQUEST_ID),
    INDEX FK2B486FB7898584F (ALIQUOT_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

UPDATE `patient_visit` SET ACTIVITY_STATUS_ID = (SELECT ID FROM activity_status WHERE NAME = 'Active');

-- start CREATED_AT update

DROP TABLE IF EXISTS tmp;

CREATE TABLE tmp AS
	SELECT p.ID, MIN(pv.DATE_PROCESSED) as created_at
	FROM `patient` p, `shipment_patient` sp, `patient_visit` pv
WHERE p.ID = sp.PATIENT_ID AND sp.ID = pv.SHIPMENT_PATIENT_ID
GROUP BY p.ID;


ALTER TABLE tmp ADD PRIMARY KEY(ID);

UPDATE `patient` p
INNER JOIN tmp ON p.ID = tmp.ID
SET p.CREATED_AT = tmp.CREATED_AT;

DROP TABLE tmp;

-- end CREATED_AT update

UPDATE container_path SET top_container_id = IF(LOCATE('/', path) = 0, path, SUBSTR(path, 1, LOCATE('/', path)));

-- update `POSITION_STRING` values
-- SBS Standard

UPDATE abstract_position ap, container c, container_type ct SET position_string = CONCAT(SUBSTR("ABCDEFGH", row + 1, 1), col + 1) WHERE ap.container_id = c.id AND ap.discriminator = 'AliquotPosition' AND c.container_type_id = ct.id and ct.child_labeling_scheme_id = 1;

-- CBSR 2 Char Alphabetic

UPDATE abstract_position ap, container c, container_type ct SET position_string = CONCAT(SUBSTR("ABCDEFGHJKLMNPQRSTUVWXYZ", row div 24 + 1, 1), SUBSTR("ABCDEFGHJKLMNPQRSTUVWXYZ", mod(row, 24) + 1, 1)) WHERE ap.container_id = c.id AND ap.discriminator = 'AliquotPosition' AND c.container_type_id = ct.id and ct.child_labeling_scheme_id = 2;

-- CBSR SBS

UPDATE abstract_position ap, container c, container_type ct SET position_string = CONCAT(SUBSTR("ABCDEFGHJ", row + 1, 1), col + 1) WHERE ap.container_id = c.id AND ap.discriminator = 'AliquotPosition' AND c.container_type_id = ct.id and ct.child_labeling_scheme_id = 5;

-- insert report data

-- MySQL dump 10.13  Distrib 5.1.41, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: biobank2
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.8

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
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `DESCRIPTION` text COLLATE latin1_general_cs,
  `USER_ID` int(11) DEFAULT NULL,
  `IS_PUBLIC` bit(1) DEFAULT NULL,
  `IS_COUNT` bit(1) DEFAULT NULL,
  `ENTITY_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK8FDF493491CFD445` (`ENTITY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
INSERT INTO `report` VALUES (7,'05 - Aliquots per Study',NULL,15,NULL,'',1),(8,'03 - Aliquots per Study per Clinic',NULL,15,NULL,'',1),(6,'01 - Aliquots',NULL,15,'\0','\0',1),(9,'04A - Aliquots per Study per Clinic by Year',NULL,15,'\0','',1),(16,'11 - New Patient Visits per Study by Date',NULL,15,NULL,'',4),(15,'14 - Patients per Study by Date',NULL,15,NULL,'',3),(14,'12 - New Patients per Study per Clinic by Date',NULL,15,NULL,'',3),(13,'07 - Aliquots by Container',NULL,15,NULL,NULL,1),(17,'13 - Patient Visits per Study by Date',NULL,15,NULL,'',4),(18,'18A - Invoicing Report',NULL,15,NULL,'',1),(19,'18B - Invoicing Report','',15,NULL,'',4),(20,'19 - Sample Type Totals by Patient Visit and Study',NULL,15,NULL,'',1);
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_filter`
--

DROP TABLE IF EXISTS `report_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_filter` (
  `ID` int(11) NOT NULL,
  `POSITION` int(11) DEFAULT NULL,
  `OPERATOR` int(11) DEFAULT NULL,
  `ENTITY_FILTER_ID` int(11) NOT NULL,
  `REPORT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK13D570E3445CEC4C` (`ENTITY_FILTER_ID`),
  KEY `FK13D570E3BE9306A5` (`REPORT_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_filter`
--

LOCK TABLES `report_filter` WRITE;
/*!40000 ALTER TABLE `report_filter` DISABLE KEYS */;
INSERT INTO `report_filter` VALUES (184,0,4,12,6),(185,1,1,2,6),(189,0,101,7,13),(190,1,3,12,13),(191,2,101,13,13),(193,0,4,12,8),(194,1,1,2,8),(195,0,4,12,9),(196,1,1,2,9),(197,0,4,12,7),(198,1,1,2,7),(199,0,NULL,206,14),(200,1,1,203,14),(201,0,1,203,15),(203,0,1,301,17),(204,0,4,12,18),(205,1,1,2,18),(208,0,4,12,20),(209,1,101,14,20),(210,0,1,301,16),(211,1,NULL,311,16),(212,0,1,301,19);
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
  `POSITION` int(11) DEFAULT NULL,
  `VALUE` text COLLATE latin1_general_cs,
  `SECOND_VALUE` text COLLATE latin1_general_cs,
  `REPORT_FILTER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK691EF6F59FFD1CEE` (`REPORT_FILTER_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_filter_value`
--

LOCK TABLES `report_filter_value` WRITE;
/*!40000 ALTER TABLE `report_filter_value` DISABLE KEYS */;
INSERT INTO `report_filter_value` VALUES (138,0,'2799',NULL,208),(133,0,'2799',NULL,184),(134,0,'2799',NULL,193),(135,0,'2799',NULL,195),(136,0,'2799',NULL,197),(137,0,'2799',NULL,204),(140,0,'%',NULL,209);
/*!40000 ALTER TABLE `report_filter_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_column`
--

DROP TABLE IF EXISTS `report_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_column` (
  `ID` int(11) NOT NULL,
  `POSITION` int(11) DEFAULT NULL,
  `COLUMN_ID` int(11) NOT NULL,
  `PROPERTY_MODIFIER_ID` int(11) DEFAULT NULL,
  `REPORT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKF0B78C1BE9306A5` (`REPORT_ID`),
  KEY `FKF0B78C1C2DE3790` (`PROPERTY_MODIFIER_ID`),
  KEY `FKF0B78C1A946D8E8` (`COLUMN_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_column`
--

LOCK TABLES `report_column` WRITE;
/*!40000 ALTER TABLE `report_column` DISABLE KEYS */;
INSERT INTO `report_column` VALUES (22,2,11,NULL,6),(21,3,2,NULL,6),(20,4,8,NULL,6),(23,1,20,NULL,6),(24,0,1,NULL,6),(29,1,20,NULL,9),(30,0,15,NULL,9),(27,0,15,NULL,8),(28,2,2,1,9),(25,0,15,NULL,7),(26,1,20,NULL,8),(51,1,309,NULL,16),(53,2,301,3,17),(52,0,310,NULL,16),(50,2,301,3,16),(49,0,202,NULL,15),(48,1,203,3,15),(45,2,203,3,14),(47,0,202,NULL,14),(46,1,205,NULL,14),(40,4,8,NULL,13),(41,3,11,NULL,13),(42,2,1,NULL,13),(43,1,7,NULL,13),(44,0,13,NULL,13),(54,1,309,NULL,17),(55,0,310,NULL,17),(56,2,8,NULL,18),(57,1,20,NULL,18),(58,0,15,NULL,18),(59,1,309,NULL,19),(60,0,310,NULL,19),(63,3,8,NULL,20),(64,0,11,NULL,20),(65,2,10,NULL,20),(66,1,9,NULL,20);
/*!40000 ALTER TABLE `report_column` ENABLE KEYS */;
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
  PRIMARY KEY (`ID`),
  KEY `FK635CF541698D6AC` (`ENTITY_PROPERTY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_filter`
--

LOCK TABLES `entity_filter` WRITE;
/*!40000 ALTER TABLE `entity_filter` DISABLE KEYS */;
INSERT INTO `entity_filter` VALUES (1,1,'Inventory Id',1),(2,3,'Link Date',2),(3,1,'Comment',3),(4,2,'Quantity',4),(5,1,'Activity Status',5),(6,1,'Container Product Barcode',7),(7,1,'Container Label',8),(8,1,'Sample Type',9),(9,3,'Date Processed',10),(10,3,'Date Drawn',11),(11,1,'Patient Number',12),(12,4,'Top Container',6),(13,1,'Site',15),(14,1,'Study',16),(15,3,'Date Received',17),(16,1,'Waybill',18),(17,3,'Shipment Departure Date',19),(18,1,'Shipment Box Number',20),(19,1,'Clinic',21),(20,6,'First Patient Visit',10),(101,1,'Product Barcode',101),(102,1,'Comment',102),(103,1,'Label',103),(104,2,'Temperature',104),(105,4,'Top Container',105),(106,3,'Aliquot Link Date',106),(107,1,'Container Type',107),(108,5,'Is Top Level',108),(109,1,'Site',109),(201,1,'Patient Number',201),(202,1,'Study',202),(203,3,'Patient Visit Date Processed',203),(204,3,'Patient Visit Date Drawn',204),(205,1,'Clinic',205),(206,6,'First Patient Visit',203),(301,3,'Date Processed',301),(302,3,'Date Drawn',302),(303,1,'Comment',303),(304,1,'Patient Number',304),(305,3,'Shipment Date Received',305),(306,1,'Shipment Waybill',306),(307,3,'Shipment Date Departed',307),(308,1,'Shipment Box Number',308),(309,1,'Clinic',309),(310,1,'Study',310),(311,6,'First Patient Visit',301);
/*!40000 ALTER TABLE `entity_filter` ENABLE KEYS */;
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
  PRIMARY KEY (`ID`),
  KEY `FK16BD7321698D6AC` (`ENTITY_PROPERTY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_column`
--

LOCK TABLES `entity_column` WRITE;
/*!40000 ALTER TABLE `entity_column` DISABLE KEYS */;
INSERT INTO `entity_column` VALUES (1,'Inventory Id',1),(2,'Link Date',2),(3,'Comment',3),(4,'Quantity',4),(5,'Activity Status',5),(6,'Container Product Barcode',7),(7,'Container Label',8),(8,'Sample Type',9),(9,'Date Processed',10),(10,'Date Drawn',11),(11,'Patient Number',12),(12,'Top Container Type',13),(13,'Aliquot Position',14),(14,'Site',15),(15,'Study',16),(16,'Date Received',17),(17,'Waybill',18),(18,'Shipment Departure Date',19),(19,'Shipment Box Number',20),(20,'Clinic',21),(101,'Product Barcode',101),(102,'Comment',102),(103,'Label',103),(104,'Temperature',104),(105,'Top Container Type',110),(106,'Aliquot Link Date',106),(107,'Container Type',107),(108,'Site',109),(201,'Patient Number',201),(202,'Study',202),(203,'Patient Visit Date Processed',203),(204,'Patient Visit Date Drawn',204),(205,'Clinic',205),(301,'Date Processed',301),(302,'Date Drawn',302),(303,'Comment',303),(304,'Patient Number',304),(305,'Shipment Date Received',305),(306,'Shipment Waybill',306),(307,'Shipment Date Departed',307),(308,'Shipment Box Number',308),(309,'Clinic',309),(310,'Study',310);
/*!40000 ALTER TABLE `entity_column` ENABLE KEYS */;
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
  PRIMARY KEY (`ID`),
  KEY `FK3FC956B191CFD445` (`ENTITY_ID`),
  KEY `FK3FC956B157C0C3B0` (`PROPERTY_TYPE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_property`
--

LOCK TABLES `entity_property` WRITE;
/*!40000 ALTER TABLE `entity_property` DISABLE KEYS */;
INSERT INTO `entity_property` VALUES (1,'inventoryId',1,1),(2,'linkDate',3,1),(3,'comment',1,1),(4,'quantity',2,1),(5,'activityStatus.name',1,1),(6,'aliquotPosition.container.containerPath.topContainer.id',2,1),(7,'aliquotPosition.container.productBarcode',1,1),(8,'aliquotPosition.container.label',1,1),(9,'sampleType.nameShort',1,1),(10,'patientVisit.dateProcessed',3,1),(11,'patientVisit.dateDrawn',3,1),(12,'patientVisit.shipmentPatient.patient.pnumber',1,1),(13,'aliquotPosition.container.containerPath.topContainer.containerType.nameShort',1,1),(14,'aliquotPosition.positionString',1,1),(15,'aliquotPosition.container.site.nameShort',1,1),(16,'patientVisit.shipmentPatient.patient.study.nameShort',1,1),(17,'patientVisit.shipmentPatient.shipment.dateReceived',3,1),(18,'patientVisit.shipmentPatient.shipment.waybill',1,1),(19,'patientVisit.shipmentPatient.shipment.departed',3,1),(20,'patientVisit.shipmentPatient.shipment.boxNumber',1,1),(21,'patientVisit.shipmentPatient.shipment.clinic.nameShort',1,1),(101,'productBarcode',1,2),(102,'comment',1,2),(103,'label',1,2),(104,'temperature',2,2),(105,'containerPath.topContainer.id',2,2),(106,'aliquotPositionCollection.aliquot.linkDate',3,2),(107,'containerType.nameShort',1,2),(108,'containerType.topLevel',4,2),(109,'site.nameShort',1,2),(110,'containerPath.topContainer.containerType.nameShort',1,2),(201,'pnumber',1,3),(202,'study.nameShort',1,3),(203,'shipmentPatientCollection.patientVisitCollection.dateProcessed',3,3),(204,'shipmentPatientCollection.patientVisitCollection.dateDrawn',3,3),(205,'shipmentPatientCollection.shipment.clinic.nameShort',1,3),(301,'dateProcessed',3,4),(302,'dateDrawn',3,4),(303,'comment',1,4),(304,'shipmentPatient.patient.pnumber',1,4),(305,'shipmentPatient.shipment.dateReceived',3,4),(306,'shipmentPatient.shipment.waybill',1,4),(307,'shipmentPatient.shipment.departed',3,4),(308,'shipmentPatient.shipment.boxNumber',1,4),(309,'shipmentPatient.shipment.clinic.nameShort',1,4),(310,'shipmentPatient.patient.study.nameShort',1,4);
/*!40000 ALTER TABLE `entity_property` ENABLE KEYS */;
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
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `property_type`
--

LOCK TABLES `property_type` WRITE;
/*!40000 ALTER TABLE `property_type` DISABLE KEYS */;
INSERT INTO `property_type` VALUES (1,'String'),(2,'Number'),(3,'Date'),(4,'Boolean');
/*!40000 ALTER TABLE `property_type` ENABLE KEYS */;
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
  PRIMARY KEY (`ID`),
  KEY `FK5DF9160157C0C3B0` (`PROPERTY_TYPE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `property_modifier`
--

LOCK TABLES `property_modifier` WRITE;
/*!40000 ALTER TABLE `property_modifier` DISABLE KEYS */;
INSERT INTO `property_modifier` VALUES (1,'Year','YEAR({value})',3),(2,'Year, Quarter','CONCAT(YEAR({value}), CONCAT(\'-\', QUARTER({value})))',3),(3,'Year, Month','CONCAT(YEAR({value}), CONCAT(\'-\', MONTH({value})))',3),(4,'Year, Week','CONCAT(YEAR({value}), CONCAT(\'-\', WEEK({value})))',3);
/*!40000 ALTER TABLE `property_modifier` ENABLE KEYS */;
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
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity`
--

LOCK TABLES `entity` WRITE;
/*!40000 ALTER TABLE `entity` DISABLE KEYS */;
INSERT INTO `entity` VALUES (1,'edu.ualberta.med.biobank.model.Aliquot','Aliquot'),(2,'edu.ualberta.med.biobank.model.Container','Container'),(3,'edu.ualberta.med.biobank.model.Patient','Patient'),(4,'edu.ualberta.med.biobank.model.PatientVisit','PatientVisit');
/*!40000 ALTER TABLE `entity` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-01-12 14:16:56


-- update constraints (unique and not-null):
-- also update ContainerType -> ContainerLabelingScheme relation replace 0..1 by 1 (so cannot be null)

alter table Abstract_Position
 change column row row integer not null,
 change column col col integer not null;
alter table Capacity
 change column ROW_CAPACITY ROW_CAPACITY integer not null,
 change column COL_CAPACITY COL_CAPACITY integer not null;
alter table Abstract_Shipment
 change column DATE_RECEIVED DATE_RECEIVED datetime not null;
alter table Activity_Status
 change column NAME NAME varchar(50) not null unique;
alter table Aliquot
 change column INVENTORY_ID INVENTORY_ID varchar(100) not null unique;
alter table Clinic
 change column NAME NAME varchar(255) not null unique, 
 change column NAME_SHORT NAME_SHORT varchar(50) not null unique;
alter table Container
 change column LABEL LABEL varchar(255) not null;
alter table Container_Type
 change column NAME NAME varchar(255) not null, 
 change column NAME_SHORT NAME_SHORT varchar(50) not null,
 change column CHILD_LABELING_SCHEME_ID CHILD_LABELING_SCHEME_ID integer not null;
alter table Patient
 change column PNUMBER PNUMBER varchar(100) not null unique;
alter table Sample_Type
 change column NAME NAME varchar(100) not null unique, 
 change column NAME_SHORT NAME_SHORT varchar(50) not null unique;
alter table Shipping_Method
 change column NAME NAME varchar(255) not null unique;
alter table Site
 change column NAME NAME varchar(255) not null unique,
 change column NAME_SHORT NAME_SHORT varchar(50) not null unique;
alter table Source_Vessel
 change column NAME NAME varchar(100) not null unique;
alter table Study
 change column NAME NAME varchar(255) not null unique, 
 change column NAME_SHORT NAME_SHORT varchar(50) not null unique;

-- unique constraint on multiple columns
ALTER TABLE container
  ADD CONSTRAINT uc_label UNIQUE (label,container_type_id),
  ADD CONSTRAINT uc_productbarcode UNIQUE (product_barcode,site_id);

ALTER TABLE container_type
  ADD CONSTRAINT uc_name UNIQUE (name,site_id),
  ADD CONSTRAINT uc_nameshort UNIQUE (name_short,site_id);

