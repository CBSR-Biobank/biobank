-- MySQL dump 10.11
--
-- Host: localhost    Database: biobank2
-- ------------------------------------------------------
-- Server version	5.0.75-0ubuntu5


--
-- Table structure for table `STUDY_INFO_type`
--

DROP TABLE IF EXISTS `pv_info_type`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pv_info_type` (
  `ID` int(11) NOT NULL,
  `TYPE` varchar(255) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;
LOCK TABLES `PV_INFO_TYPE` WRITE;
INSERT INTO `PV_INFO_TYPE` VALUES
       ( 1,'number'),
       ( 2,'text'),
       ( 3,'date_time'),
       ( 4,'select_single'),
       ( 5,'select_multiple'),
       ( 6,'select_single');
UNLOCK TABLES;

DROP TABLE IF EXISTS `pv_info_possible`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pv_info_possible` (
  `ID` int(11) NOT NULL,
  `LABEL` varchar(255) default NULL,
  `IS_DEFAULT` bit(1) default NULL,
  `PV_INFO_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FK546E6B69B58C0461` (`PV_INFO_TYPE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

LOCK TABLES `PV_INFO_POSSIBLE` WRITE;
INSERT INTO `PV_INFO_POSSIBLE` VALUES
       ( 1,'Visit Type',b'0',4),
       ( 2,'Clinic Shipped Date',b'0',3),
       ( 3,'Consent',b'0',5),
       ( 4,'PBMC Count',b'0',1),
       ( 5,'Biopsy Length',b'0',1),
       ( 6,'Worksheet',b'0',2);
UNLOCK TABLES;

