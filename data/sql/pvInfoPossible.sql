-- MySQL dump 10.11
--
-- Host: localhost    Database: biobank2
-- ------------------------------------------------------
-- Server version	5.0.75-0ubuntu5


--
-- Table structure for table `STUDY_INFO_type`
--

LOCK TABLES `PV_INFO_TYPE` WRITE;
INSERT INTO `PV_INFO_TYPE` VALUES
       ( 1,'number'),
       ( 2,'text'),
       ( 3,'date_time'),
       ( 4,'select_single'),
       ( 5,'select_single_and_quantity'),
       ( 6,'select_multiple');
UNLOCK TABLES;

LOCK TABLES `PV_INFO_POSSIBLE` WRITE;
INSERT INTO `PV_INFO_POSSIBLE` VALUES
       ( 1,'Date Drawn',b'1',3),
       ( 2,'Shipped Date',b'1',3),
       ( 3,'Date Received',b'0',3),
       ( 4,'Date Processed',b'0',3),
       ( 5,'Aliquot Volume',b'0',4),
       ( 6,'Blood Received',b'0',5),
       ( 7,'Consent',b'0',6),
       ( 8,'Visit',b'0',4),
       ( 9,'WBC Count',b'0',1),
       (10,'Biopsy Length',b'0',1),
       -- comments should be last
       (11,'Comments',b'0',2);
UNLOCK TABLES;

