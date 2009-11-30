-- MySQL dump 10.11
--
-- Host: localhost    Database: biobank2
-- ------------------------------------------------------
-- Server version	5.0.75-0ubuntu5


--
-- Table structure for table `STUDY_INFO_type`
--

LOCK TABLES `PV_INFO_TYPE` WRITE;
INSERT INTO `PV_INFO_TYPE` (ID, TYPE) VALUES
       ( 1,'number'),
       ( 2,'text'),
       ( 3,'date_time'),
       ( 4,'select_single'),
       ( 5,'select_multiple');
UNLOCK TABLES;

LOCK TABLES `PV_INFO_POSSIBLE` WRITE;
INSERT INTO `PV_INFO_POSSIBLE` (ID, LABEL, IS_DEFAULT, SITE_ID, PV_INFO_TYPE_ID)
VALUES
       ( 1,'Visit Type',b'0',NULL, 4),
       ( 2,'Consent',b'0',NULL, 5),
       ( 3,'PBMC Count',b'0',NULL, 1),
       ( 4,'Biopsy Length',b'0',NULL, 1),
       ( 5,'Worksheet',b'0',NULL, 2);
UNLOCK TABLES;

