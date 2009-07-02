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
       ( 1,'Date Drawn',NULL,3),
       ( 2,'Shipped Date',NULL,3),
       ( 3,'Date Received',NULL,3),
       ( 4,'Date Processed',NULL,3),
       ( 5,'Aliquot Volume',NULL,4),
       ( 6,'Blood Received',NULL,5),
       ( 7,'Consent',NULL,6),
       ( 8,'Visit',NULL,4),
       ( 9,'WBC Count',NULL,1),
       (10,'Biopsy Length',NULL,1),
       -- comments should be last
       (11,'Comments',NULL,2);
UNLOCK TABLES;

