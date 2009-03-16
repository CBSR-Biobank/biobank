-- MySQL dump 10.11
--
-- Host: localhost    Database: biobank2
-- ------------------------------------------------------
-- Server version	5.0.75-0ubuntu5


--
-- Table structure for table `sdata_type`
--

DROP TABLE IF EXISTS `sdata_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `sdata_type` (
  `ID` int(11) NOT NULL,
  `TYPE` varchar(255) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `sdata_type`
--

LOCK TABLES `sdata_type` WRITE;
/*!40000 ALTER TABLE `sdata_type` DISABLE KEYS */;
INSERT INTO `sdata_type` VALUES (1,'Date Drawn'),(2,'Date Received'),(3,'Date Processed'),(4,'Comments'),(5,'Worksheet'),(6,'Aliquot Volume'),(7,'Blood Received'),(8,'WBC Count'),(9,'Time Arrived'),(10,'Biopsy Length'),(11,'Visit'),(12,'Shipped Date'),(13,'Consent');
/*!40000 ALTER TABLE `sdata_type` ENABLE KEYS */;
UNLOCK TABLES;

