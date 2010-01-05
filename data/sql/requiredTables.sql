DROP TABLE IF EXISTS `log_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log_message` (
  `LOG_ID` bigint(200) NOT NULL AUTO_INCREMENT,
  `APPLICATION` varchar(25) DEFAULT NULL,
  `SERVER` varchar(50) DEFAULT NULL,
  `CATEGORY` varchar(255) DEFAULT NULL,
  `THREAD` varchar(255) DEFAULT NULL,
  `USERNAME` varchar(255) DEFAULT NULL,
  `SESSION_ID` varchar(255) DEFAULT NULL,
  `MSG` text,
  `THROWABLE` text,
  `NDC` text,
  `CREATED_ON` bigint(20) NOT NULL,
  `OBJECT_ID` varchar(255) DEFAULT NULL,
  `OBJECT_NAME` varchar(255) DEFAULT NULL,
  `ORGANIZATION` varchar(255) DEFAULT NULL,
  `OPERATION` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`LOG_ID`),
  KEY `APPLICATION_LOGTAB_INDX` (`APPLICATION`),
  KEY `SERVER_LOGTAB_INDX` (`SERVER`),
  KEY `THREAD_LOGTAB_INDX` (`THREAD`),
  KEY `CREATED_ON_LOGTAB_INDX` (`CREATED_ON`),
  KEY `LOGID_LOGTAB_INDX` (`LOG_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=163921 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `object_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `object_attribute` (
  `OBJECT_ATTRIBUTE_ID` bigint(200) NOT NULL AUTO_INCREMENT,
  `CURRENT_VALUE` varchar(255) DEFAULT NULL,
  `PREVIOUS_VALUE` varchar(255) DEFAULT NULL,
  `ATTRIBUTE` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`OBJECT_ATTRIBUTE_ID`),
  KEY `OAID_INDX` (`OBJECT_ATTRIBUTE_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=212492 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `objectattributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `objectattributes` (
  `LOG_ID` bigint(200) NOT NULL DEFAULT '0',
  `OBJECT_ATTRIBUTE_ID` bigint(200) NOT NULL DEFAULT '0',
  KEY `Index_2` (`LOG_ID`),
  KEY `FK_objectattributes_2` (`OBJECT_ATTRIBUTE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

UNLOCK TABLES;
