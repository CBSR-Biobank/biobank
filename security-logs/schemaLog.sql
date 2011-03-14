-- drop table if exists LOG_MESSAGE;
-- drop table if exists OBJECT_ATTRIBUTE;
-- drop table if exists OBJECTATTRIBUTES;

DROP TABLE IF EXISTS `log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `USERNAME` varchar(100) DEFAULT NULL,
  `CREATED_AT` datetime DEFAULT NULL,
  `SITE` varchar(50) DEFAULT NULL,
  `ACTION` varchar(100) DEFAULT NULL,
  `PATIENT_NUMBER` varchar(100) DEFAULT NULL,
  `INVENTORY_ID` varchar(100) DEFAULT NULL,
  `LOCATION_LABEL` varchar(255) DEFAULT NULL,
  `DETAILS` text,
  `TYPE` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
