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
       ( 4,'Comments'),
       ( 5,'Aliquot Volume'),
       ( 6,'Blood Received'),
       ( 7,'WBC Count'),
       ( 8,'Time Arrived'),
       ( 9,'Biopsy Length'),
       (10,'Visit'),
       (11,'Shipped Date');
UNLOCK TABLES;

