-- MySQL dump 10.13  Distrib 5.1.41, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: biobank
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.10

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
INSERT INTO `entity_filter` VALUES (1,1,'Inventory Id',1,0),(2,3,'Creation Time',2,0),(3,1,'Comment',3,0),(4,2,'Quantity',4,0),(5,1,'Activity Status',5,0),(6,1,'Container Product Barcode',7,0),(7,1,'Container Label',8,0),(8,1,'Specimen Type',9,0),(9,3,'Time Processed',10,0),(11,1,'Patient Number',12,0),(12,4,'Top Container',6,0),(13,1,'Current Center',15,0),(14,1,'Study',16,0),(15,3,'Shipment Time Received',17,0),(16,1,'Shipment Waybill',18,0),(17,3,'Shipment Time Sent',19,0),(18,1,'Shipment Box Number',20,0),(19,1,'Source Center',21,0),(21,1,'Dispatch Sender',22,0),(22,1,'Dispatch Receiver',23,0),(23,3,'Dispatch Time Received',24,0),(24,3,'Dispatch Time Sent',25,0),(25,1,'Dispatch Waybill',26,0),(26,1,'Dispatch Box Number',27,0),(27,7,'Visit Number',28,0),(28,1,'Source Specimen Inventory Id',29,0),(29,1,'Source Specimen Source Center',30,0),(30,3,'Time Drawn',31,0),(101,1,'Product Box Number',101,0),(102,1,'Comment',102,0),(103,1,'Label',103,0),(104,2,'Temperature',104,0),(105,4,'Top Container',105,0),(106,3,'Specimen Creation Time',106,0),(107,1,'Container Type',107,0),(108,5,'Is Top Level',108,0),(109,1,'Site',109,0),(201,1,'Patient Number',201,0),(202,1,'Study',202,0),(203,3,'Specimen Time Processed',203,0),(204,3,'Specimen Creation Time',204,0),(205,1,'Source Center',205,0),(206,6,'First Time Processed',204,0),(207,1,'Inventory Id',206,0),(208,7,'Visit Number',207,0),(301,3,'Specimen Time Processed',301,0),(302,3,'Specimen Creation Time',302,0),(303,1,'Comment',303,0),(304,1,'Patient Number',304,0),(305,1,'Specimen Source Center',305,0),(306,1,'Study',306,0),(307,6,'First Time Processed',301,0),(308,7,'Visit Number',307,0),(401,1,'Worksheet',401,0),(402,3,'Creation Time',402,0),(403,1,'Comment',403,0),(404,1,'Center',404,0),(405,1,'Activity Status',405,0),(406,1,'Specimen Inventory Id',406,0),(407,3,'Specimen Creation Time',407,0);
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
INSERT INTO `entity_property` VALUES (1,'inventoryId',1,1,0),(2,'createdAt',3,1,0),(3,'comments.message',1,1,0),(4,'quantity',2,1,0),(5,'activityStatus.name',1,1,0),(6,'specimenPosition.container.topContainer.id',2,1,0),(7,'specimenPosition.container.productBarcode',1,1,0),(8,'specimenPosition.container.label',1,1,0),(9,'specimenType.nameShort',1,1,0),(10,'parentSpecimen.processingEvent.createdAt',3,1,0),(12,'collectionEvent.patient.pnumber',1,1,0),(13,'specimenPosition.container.topContainer.containerType.nameShort',1,1,0),(14,'specimenPosition.positionString',1,1,0),(15,'currentCenter.nameShort',1,1,0),(16,'collectionEvent.patient.study.nameShort',1,1,0),(17,'originInfo.shipmentInfo.receivedAt',3,1,0),(18,'originInfo.shipmentInfo.waybill',1,1,0),(19,'originInfo.shipmentInfo.packedAt',3,1,0),(20,'originInfo.shipmentInfo.boxNumber',1,1,0),(21,'originInfo.center.nameShort',1,1,0),(22,'dispatchSpecimens.dispatch.senderCenter.nameShort',1,1,0),(23,'dispatchSpecimens.dispatch.receiverCenter.nameShort',1,1,0),(24,'dispatchSpecimens.dispatch.shipmentInfo.receivedAt',3,1,0),(25,'dispatchSpecimens.dispatch.shipmentInfo.packedAt',3,1,0),(26,'dispatchSpecimens.dispatch.shipmentInfo.waybill',1,1,0),(27,'dispatchSpecimens.dispatch.shipmentInfo.boxNumber',1,1,0),(28,'collectionEvent.visitNumber',2,1,0),(29,'topSpecimen.inventoryId',1,1,0),(30,'topSpecimen.originInfo.center.nameShort',1,1,0),(31,'topSpecimen.createdAt',3,1,0),(101,'productBarcode',1,2,0),(102,'comments.message',1,2,0),(103,'label',1,2,0),(104,'temperature',2,2,0),(105,'topContainer.id',2,2,0),(106,'specimenPositions.specimen.createdAt',3,2,0),(107,'containerType.nameShort',1,2,0),(108,'containerType.topLevel',4,2,0),(109,'site.nameShort',1,2,0),(110,'topContainer.containerType.nameShort',1,2,0),(201,'pnumber',1,3,0),(202,'study.nameShort',1,3,0),(203,'collectionEvents.allSpecimens.parentSpecimen.processingEvent.createdAt',3,3,0),(204,'collectionEvents.allSpecimens.createdAt',3,3,0),(205,'collectionEvents.allSpecimens.originInfo.center.nameShort',1,3,0),(206,'collectionEvents.allSpecimens.inventoryId',1,3,0),(207,'collectionEvents.visitNumber',2,3,0),(301,'allSpecimens.parentSpecimen.processingEvent.createdAt',3,4,0),(302,'allSpecimens.createdAt',3,4,0),(303,'comments.message',1,4,0),(304,'patient.pnumber',1,4,0),(305,'allSpecimens.originInfo.center.nameShort',1,4,0),(306,'patient.study.nameShort',1,4,0),(307,'visitNumber',2,4,0),(401,'worksheet',1,5,0),(402,'createdAt',3,5,0),(403,'comments.message',1,5,0),(404,'center.nameShort',1,5,0),(405,'activityStatus.name',1,5,0),(406,'specimens.inventoryId',1,5,0),(407,'specimens.createdAt',3,5,0);
/*!40000 ALTER TABLE `entity_property` ENABLE KEYS */;
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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-07-27 15:23:01
