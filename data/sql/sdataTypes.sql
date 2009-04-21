-- MySQL dump 10.11
--
-- Host: localhost    Database: biobank2
-- ------------------------------------------------------
-- Server version	5.0.75-0ubuntu5


--
-- Table structure for table `sdata_type`
--

LOCK TABLES `sdata_type` WRITE;
INSERT INTO `sdata_type` VALUES
       ( 1,'Date Drawn'),
       ( 2,'Date Received'),
       ( 3,'Date Processed'),
       ( 4,'Shipped Date'),
       ( 5,'Aliquot Volume'),
       ( 6,'Blood Received'),
       ( 7,'Visit'),
       ( 8,'WBC Count'),
       ( 9,'Time Arrived'),
       (10,'Biopsy Length'),
       -- comments should be last
       (11,'Comments');
UNLOCK TABLES;

