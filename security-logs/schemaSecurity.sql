-- MySQL dump 10.13  Distrib 5.1.41, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: biobank2
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.6

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
-- Table structure for table `csm_application`
--

DROP TABLE IF EXISTS `csm_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_application` (
  `APPLICATION_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `APPLICATION_NAME` varchar(255) NOT NULL,
  `APPLICATION_DESCRIPTION` varchar(200) NOT NULL,
  `DECLARATIVE_FLAG` tinyint(1) NOT NULL DEFAULT '0',
  `ACTIVE_FLAG` tinyint(1) NOT NULL DEFAULT '0',
  `UPDATE_DATE` date DEFAULT '0000-00-00',
  `DATABASE_URL` varchar(100) DEFAULT NULL,
  `DATABASE_USER_NAME` varchar(100) DEFAULT NULL,
  `DATABASE_PASSWORD` varchar(100) DEFAULT NULL,
  `DATABASE_DIALECT` varchar(100) DEFAULT NULL,
  `DATABASE_DRIVER` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`APPLICATION_ID`),
  UNIQUE KEY `UQ_APPLICATION_NAME` (`APPLICATION_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_application`
--

LOCK TABLES `csm_application` WRITE;
/*!40000 ALTER TABLE `csm_application` DISABLE KEYS */;
INSERT INTO `csm_application` VALUES (1,'csmupt','CSM UPT Super Admin Application',0,0,'2009-07-22',NULL,NULL,NULL,NULL,NULL);
INSERT INTO `csm_application` VALUES (2,'biobank2','biobank2',0,0,'2009-07-22','jdbc:mysql://localhost:3306/biobank2','dummy','4UlzrQJztJY=','org.hibernate.dialect.MySQLDialect','com.mysql.jdbc.Driver');
/*!40000 ALTER TABLE `csm_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_filter_clause`
--

DROP TABLE IF EXISTS `csm_filter_clause`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_filter_clause` (
  `FILTER_CLAUSE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CLASS_NAME` varchar(100) NOT NULL,
  `FILTER_CHAIN` varchar(2000) NOT NULL,
  `TARGET_CLASS_NAME` varchar(100) NOT NULL,
  `TARGET_CLASS_ATTRIBUTE_NAME` varchar(100) NOT NULL,
  `TARGET_CLASS_ATTRIBUTE_TYPE` varchar(100) NOT NULL,
  `TARGET_CLASS_ALIAS` varchar(100) DEFAULT NULL,
  `TARGET_CLASS_ATTRIBUTE_ALIAS` varchar(100) DEFAULT NULL,
  `GENERATED_SQL_USER` varchar(4000) NOT NULL,
  `GENERATED_SQL_GROUP` varchar(4000) NOT NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`FILTER_CLAUSE_ID`),
  KEY `FK_APPLICATION_FILTER_CLAUSE` (`APPLICATION_ID`),
  CONSTRAINT `FK_APPLICATION_FILTER_CLAUSE` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_filter_clause`
--

LOCK TABLES `csm_filter_clause` WRITE;
/*!40000 ALTER TABLE `csm_filter_clause` DISABLE KEYS */;
INSERT INTO `csm_filter_clause` VALUES (1,'edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site - self','id','java.lang.Integer','','','id in (select s.ID from site s where s.id in ( select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.user_id = u.user_id and ugrpg.protection_group_id = any (select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id = (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= \'edu.ualberta.med.biobank.model.Site\' and pe.attribute=\'id\' and p.privilege_name=\'READ\' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID) or s.id in (select distinct pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_group g, csm_role_privilege rp, csm_role r, csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.group_id = g.group_id and ugrpg.protection_group_id = any ( select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id =  (select pg2.parent_protection_group_id from csm_protection_group pg2  where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= \'edu.ualberta.med.biobank.model.Site\' and p.privilege_name=\'READ\' and g.group_name in (select grp.group_name from csm_group grp, csm_user_group userGrp, csm_user u where u.user_id = userGrp.user_id and userGrp.group_id = grp.group_id and u.login_name=:USER_NAME) and pe.application_id=:APPLICATION_ID))','id in (select table_name_csm_.id   from site table_name_csm_ where table_name_csm_.id in \r\n( select distinct pe.attribute_value from csm_protection_group pg, 	csm_protection_element pe, 	csm_pg_pe pgpe,	csm_user_group_role_pg ugrpg, 	csm_group g, 	csm_role_privilege rp, 	csm_role r, 	csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.group_id = g.group_id and ugrpg.protection_group_id = any ( select pg1.protection_group_id from csm_protection_group pg1  where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id =  (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id) ) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= \'edu.ualberta.med.biobank.model.Site\' and p.privilege_name=\'READ\' and g.group_name in (:GROUP_NAMES ) and pe.application_id=:APPLICATION_ID))',2,'2010-01-27');
/*!40000 ALTER TABLE `csm_filter_clause` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_group`
--

DROP TABLE IF EXISTS `csm_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_group` (
  `GROUP_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `GROUP_NAME` varchar(255) NOT NULL,
  `GROUP_DESC` varchar(200) DEFAULT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  `APPLICATION_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`GROUP_ID`),
  UNIQUE KEY `UQ_GROUP_GROUP_NAME` (`APPLICATION_ID`,`GROUP_NAME`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  CONSTRAINT `FK_APPLICATION_GROUP` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_group`
--

LOCK TABLES `csm_group` WRITE;
/*!40000 ALTER TABLE `csm_group` DISABLE KEYS */;
INSERT INTO `csm_group` VALUES (5,'Website Administrator','','2010-04-26',2);
INSERT INTO `csm_group` VALUES (6,'CBSR Technician Level 1','','2010-01-28',2);
INSERT INTO `csm_group` VALUES (7,'CBSR Technician Level 2','','2010-01-28',2);
INSERT INTO `csm_group` VALUES (8,'Calgary Technicians','','2010-04-20',2);
INSERT INTO `csm_group` VALUES (9,'Calgary Administrator','','2010-04-20',2);
/*!40000 ALTER TABLE `csm_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_pg_pe`
--

DROP TABLE IF EXISTS `csm_pg_pe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_pg_pe` (
  `PG_PE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PROTECTION_GROUP_ID` bigint(20) NOT NULL,
  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date DEFAULT '0000-00-00',
  PRIMARY KEY (`PG_PE_ID`),
  UNIQUE KEY `UQ_PROTECTION_GROUP_PROTECTION_ELEMENT_PROTECTION_GROUP_ID` (`PROTECTION_ELEMENT_ID`,`PROTECTION_GROUP_ID`),
  KEY `idx_PROTECTION_ELEMENT_ID` (`PROTECTION_ELEMENT_ID`),
  KEY `idx_PROTECTION_GROUP_ID` (`PROTECTION_GROUP_ID`),
  CONSTRAINT `FK_PROTECTION_ELEMENT_PROTECTION_GROUP` FOREIGN KEY (`PROTECTION_ELEMENT_ID`) REFERENCES `csm_protection_element` (`PROTECTION_ELEMENT_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PROTECTION_GROUP_PROTECTION_ELEMENT` FOREIGN KEY (`PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=748 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_pg_pe`
--

LOCK TABLES `csm_pg_pe` WRITE;
/*!40000 ALTER TABLE `csm_pg_pe` DISABLE KEYS */;
INSERT INTO `csm_pg_pe` VALUES (106,12,27,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (117,5,8,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (125,19,9,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (128,9,14,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (131,23,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (136,29,31,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (137,30,32,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (139,31,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (140,31,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (144,32,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (145,32,12,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (277,14,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (278,14,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (279,35,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (280,35,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (281,35,14,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (282,35,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (283,22,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (284,22,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (285,34,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (286,34,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (287,34,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (288,34,33,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (289,34,9,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (290,34,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (291,34,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (292,34,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (293,34,14,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (294,34,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (295,3,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (296,3,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (297,3,34,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (301,6,4,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (302,6,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (303,6,30,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (304,6,6,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (305,15,5,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (306,15,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (307,15,20,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (309,26,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (310,26,25,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (311,24,18,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (312,24,4,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (313,24,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (314,7,21,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (315,7,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (403,36,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (404,36,35,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (435,39,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (436,39,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (437,39,33,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (438,39,21,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (439,39,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (440,39,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (441,39,6,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (442,39,8,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (443,39,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (444,39,32,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (445,39,27,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (447,39,20,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (448,39,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (449,39,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (450,39,31,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (451,39,35,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (452,39,5,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (453,39,30,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (454,39,14,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (455,39,34,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (456,39,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (457,39,9,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (458,39,4,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (459,39,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (460,39,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (461,39,25,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (462,40,47,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (463,41,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (464,41,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (465,41,14,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (466,41,34,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (467,41,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (468,41,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (546,18,33,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (547,18,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (548,20,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (549,20,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (550,20,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (711,1,18,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (712,1,10,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (713,1,7,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (714,1,33,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (715,1,21,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (716,1,11,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (717,1,36,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (719,1,6,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (720,1,12,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (721,1,51,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (722,1,8,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (723,1,31,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (724,1,16,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (725,1,32,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (726,1,27,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (727,1,20,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (728,1,24,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (729,1,61,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (730,1,19,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (731,1,35,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (732,1,5,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (733,1,64,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (734,1,30,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (735,1,14,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (736,1,15,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (737,1,34,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (738,1,150,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (739,1,65,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (740,1,9,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (741,1,4,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (742,1,13,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (743,1,3,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (744,1,25,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (745,1,62,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (746,1,145,'0000-00-00');
INSERT INTO `csm_pg_pe` VALUES (747,1,151,'0000-00-00');
/*!40000 ALTER TABLE `csm_pg_pe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_privilege`
--

DROP TABLE IF EXISTS `csm_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_privilege` (
  `PRIVILEGE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PRIVILEGE_NAME` varchar(100) NOT NULL,
  `PRIVILEGE_DESCRIPTION` varchar(200) DEFAULT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`PRIVILEGE_ID`),
  UNIQUE KEY `UQ_PRIVILEGE_NAME` (`PRIVILEGE_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_privilege`
--

LOCK TABLES `csm_privilege` WRITE;
/*!40000 ALTER TABLE `csm_privilege` DISABLE KEYS */;
INSERT INTO `csm_privilege` VALUES (1,'CREATE','This privilege grants permission to a user to create an entity. This entity can be an object, a database entry, or a resource such as a network connection','2009-07-22');
INSERT INTO `csm_privilege` VALUES (2,'ACCESS','This privilege allows a user to access a particular resource.  Examples of resources include a network or database connection, socket, module of the application, or even the application itself','2009-07-22');
INSERT INTO `csm_privilege` VALUES (3,'READ','This privilege permits the user to read data from a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to read data about a particular entry','2009-07-22');
INSERT INTO `csm_privilege` VALUES (4,'WRITE','This privilege allows a user to write data to a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to write data about a particular entity','2009-07-22');
INSERT INTO `csm_privilege` VALUES (5,'UPDATE','This privilege grants permission at an entity level and signifies that the user is allowed to update data for a particular entity. Entities may include an object, object attribute, database row etc','2009-07-22');
INSERT INTO `csm_privilege` VALUES (6,'DELETE','This privilege permits a user to delete a logical entity. This entity can be an object, a database entry, a resource such as a network connection, etc','2009-07-22');
INSERT INTO `csm_privilege` VALUES (7,'EXECUTE','This privilege allows a user to execute a particular resource. The resource can be a method, function, behavior of the application, URL, button etc','2009-07-22');
/*!40000 ALTER TABLE `csm_privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_protection_element`
--

DROP TABLE IF EXISTS `csm_protection_element`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_protection_element` (
  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PROTECTION_ELEMENT_NAME` varchar(100) NOT NULL,
  `PROTECTION_ELEMENT_DESCRIPTION` varchar(200) DEFAULT NULL,
  `OBJECT_ID` varchar(100) NOT NULL,
  `ATTRIBUTE` varchar(100) DEFAULT NULL,
  `ATTRIBUTE_VALUE` varchar(100) DEFAULT NULL,
  `PROTECTION_ELEMENT_TYPE` varchar(100) DEFAULT NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`PROTECTION_ELEMENT_ID`),
  UNIQUE KEY `UQ_PE_PE_NAME_ATTRIBUTE_VALUE_APP_ID` (`OBJECT_ID`,`ATTRIBUTE`,`ATTRIBUTE_VALUE`,`APPLICATION_ID`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  CONSTRAINT `FK_PE_APPLICATION` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_protection_element`
--

LOCK TABLES `csm_protection_element` WRITE;
/*!40000 ALTER TABLE `csm_protection_element` DISABLE KEYS */;
INSERT INTO `csm_protection_element` VALUES (1,'csmupt','CSM UPT Super Admin Application Protection Element','csmupt',NULL,NULL,NULL,1,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (2,'biobank2','biobank2','biobank2',NULL,NULL,NULL,1,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (3,'edu.ualberta.med.biobank.model.ActivityStatus','edu.ualberta.med.biobank.model.ActivityStatus','edu.ualberta.med.biobank.model.ActivityStatus','','','',2,'2010-03-04');
INSERT INTO `csm_protection_element` VALUES (4,'edu.ualberta.med.biobank.model.Address','edu.ualberta.med.biobank.model.Address','edu.ualberta.med.biobank.model.Address',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (5,'edu.ualberta.med.biobank.model.Capacity','edu.ualberta.med.biobank.model.Capacity','edu.ualberta.med.biobank.model.Capacity',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (6,'edu.ualberta.med.biobank.model.Clinic','edu.ualberta.med.biobank.model.Clinic','edu.ualberta.med.biobank.model.Clinic',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (7,'edu.ualberta.med.biobank.model.ContainerPosition','edu.ualberta.med.biobank.model.ContainerPosition','edu.ualberta.med.biobank.model.ContainerPosition',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (8,'edu.ualberta.med.biobank.model.Patient','edu.ualberta.med.biobank.model.Patient','edu.ualberta.med.biobank.model.Patient',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (9,'edu.ualberta.med.biobank.model.PatientVisit','edu.ualberta.med.biobank.model.PatientVisit','edu.ualberta.med.biobank.model.PatientVisit',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (10,'edu.ualberta.med.biobank.model.StudyPvAttr','edu.ualberta.med.biobank.model.StudyPvAttr','edu.ualberta.med.biobank.model.StudyPvAttr','','','',2,'2009-12-03');
INSERT INTO `csm_protection_element` VALUES (11,'edu.ualberta.med.biobank.model.PvAttr','edu.ualberta.med.biobank.model.PvAttr','edu.ualberta.med.biobank.model.PvAttr','','','',2,'2009-12-03');
INSERT INTO `csm_protection_element` VALUES (12,'edu.ualberta.med.biobank.model.GlobalPvAttr','edu.ualberta.med.biobank.model.GlobalPvAttr','edu.ualberta.med.biobank.model.GlobalPvAttr','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (13,'edu.ualberta.med.biobank.model.PvAttrType','edu.ualberta.med.biobank.model.PvAttrType','edu.ualberta.med.biobank.model.PvAttrType','','','',2,'2009-12-03');
INSERT INTO `csm_protection_element` VALUES (14,'edu.ualberta.med.biobank.model.Aliquot','edu.ualberta.med.biobank.model.Aliquot','edu.ualberta.med.biobank.model.Aliquot',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (15,'edu.ualberta.med.biobank.model.AliquotPosition','edu.ualberta.med.biobank.model.AliquotPosition','edu.ualberta.med.biobank.model.AliquotPosition',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (16,'edu.ualberta.med.biobank.model.SampleType','edu.ualberta.med.biobank.model.SampleType','edu.ualberta.med.biobank.model.SampleType',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (18,'edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site','edu.ualberta.med.biobank.model.Site',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (19,'edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','edu.ualberta.med.biobank.model.Container','','','',2,'2010-08-19');
INSERT INTO `csm_protection_element` VALUES (20,'edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType','edu.ualberta.med.biobank.model.ContainerType',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (21,'edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study','edu.ualberta.med.biobank.model.Study',NULL,NULL,NULL,2,'2009-07-22');
INSERT INTO `csm_protection_element` VALUES (24,'edu.ualberta.med.biobank.model.SourceVessel','edu.ualberta.med.biobank.model.SourceVessel','edu.ualberta.med.biobank.model.SourceVessel','','','',2,'2009-07-23');
INSERT INTO `csm_protection_element` VALUES (25,'edu.ualberta.med.biobank.model.SampleStorage','edu.ualberta.med.biobank.model.SampleStorage','edu.ualberta.med.biobank.model.SampleStorage','','','',2,'2009-07-23');
INSERT INTO `csm_protection_element` VALUES (27,'edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','edu.ualberta.med.biobank.model.ContainerLabelingScheme','','','',2,'2009-07-26');
INSERT INTO `csm_protection_element` VALUES (30,'edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','edu.ualberta.med.biobank.model.Contact','','','',2,'2009-08-24');
INSERT INTO `csm_protection_element` VALUES (31,'edu.ualberta.med.biobank.model.ClinicShipment','edu.ualberta.med.biobank.model.ClinicShipment','edu.ualberta.med.biobank.model.ClinicShipment','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (32,'edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','edu.ualberta.med.biobank.model.ShippingMethod','','','',2,'2009-11-30');
INSERT INTO `csm_protection_element` VALUES (33,'edu.ualberta.med.biobank.model.PvSourceVessel','edu.ualberta.med.biobank.model.PvSourceVessel','edu.ualberta.med.biobank.model.PvSourceVessel','','','',2,'2009-11-30');
INSERT INTO `csm_protection_element` VALUES (34,'edu.ualberta.med.biobank.model.ContainerPath','edu.ualberta.med.biobank.model.ContainerPath','edu.ualberta.med.biobank.model.ContainerPath','','','',2,'2010-01-11');
INSERT INTO `csm_protection_element` VALUES (35,'edu.ualberta.med.biobank.model.StudySourceVessel','edu.ualberta.med.biobank.model.StudySourceVessel','edu.ualberta.med.biobank.model.StudySourceVessel','','','',2,'2010-04-13');
INSERT INTO `csm_protection_element` VALUES (36,'edu.ualberta.med.biobank.model.AbstractPosition','','edu.ualberta.med.biobank.model.AbstractPosition','','','',2,'2010-03-15');
INSERT INTO `csm_protection_element` VALUES (47,'biobank.cbsr.container.administration','','biobank.cbsr.container.administration','','','',2,'2010-04-26');
INSERT INTO `csm_protection_element` VALUES (51,'edu.ualberta.med.biobank.model.Log','','edu.ualberta.med.biobank.model.Log','','','',2,'2010-05-25');
INSERT INTO `csm_protection_element` VALUES (61,'edu.ualberta.med.biobank.model.AbstractContainer','edu.ualberta.med.biobank.model.AbstractContainer','edu.ualberta.med.biobank.model.AbstractContainer','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (62,'edu.ualberta.med.biobank.model.AbstractShipment','edu.ualberta.med.biobank.model.AbstractShipment','edu.ualberta.med.biobank.model.AbstractShipment','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (64,'edu.ualberta.med.biobank.model.DispatchInfo','edu.ualberta.med.biobank.model.DispatchInfo','edu.ualberta.med.biobank.model.DispatchInfo','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (65,'edu.ualberta.med.biobank.model.DispatchShipment','edu.ualberta.med.biobank.model.DispatchShipment','edu.ualberta.med.biobank.model.DispatchShipment','','','',2,'2010-08-18');
INSERT INTO `csm_protection_element` VALUES (145,'edu.ualberta.med.biobank.model.DispatchPosition','edu.ualberta.med.biobank.model.DispatchPosition','edu.ualberta.med.biobank.model.DispatchPosition','','','',2,'2010-08-19');
INSERT INTO `csm_protection_element` VALUES (150,'edu.ualberta.med.biobank.model.ClinicShipmentPatient','edu.ualberta.med.biobank.model.ClinicShipmentPatient','edu.ualberta.med.biobank.model.ClinicShipmentPatient','','','',2,'2010-09-29');
INSERT INTO `csm_protection_element` VALUES (151,'edu.ualberta.med.biobank.model.DispatchShipmentAliquot','edu.ualberta.med.biobank.model.DispatchShipmentAliquot','edu.ualberta.med.biobank.model.DispatchShipmentAliquot','','','',2,'2010-09-30');
/*!40000 ALTER TABLE `csm_protection_element` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_protection_group`
--

DROP TABLE IF EXISTS `csm_protection_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_protection_group` (
  `PROTECTION_GROUP_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PROTECTION_GROUP_NAME` varchar(100) NOT NULL,
  `PROTECTION_GROUP_DESCRIPTION` varchar(200) DEFAULT NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `LARGE_ELEMENT_COUNT_FLAG` tinyint(1) NOT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  `PARENT_PROTECTION_GROUP_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`PROTECTION_GROUP_ID`),
  UNIQUE KEY `UQ_PROTECTION_GROUP_PROTECTION_GROUP_NAME` (`APPLICATION_ID`,`PROTECTION_GROUP_NAME`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  KEY `idx_PARENT_PROTECTION_GROUP_ID` (`PARENT_PROTECTION_GROUP_ID`),
  CONSTRAINT `FK_PG_APPLICATION` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PROTECTION_GROUP` FOREIGN KEY (`PARENT_PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_protection_group`
--

LOCK TABLES `csm_protection_group` WRITE;
/*!40000 ALTER TABLE `csm_protection_group` DISABLE KEYS */;
INSERT INTO `csm_protection_group` VALUES (1,'pg-biobank-all','Contains all Protection Element of each model object',2,0,'2010-01-29',NULL);
INSERT INTO `csm_protection_group` VALUES (3,'pg-containers','',2,0,'2009-07-22',13);
INSERT INTO `csm_protection_group` VALUES (5,'pg-patients','',2,0,'2009-07-23',16);
INSERT INTO `csm_protection_group` VALUES (6,'pg-clinics','',2,0,'2009-07-23',27);
INSERT INTO `csm_protection_group` VALUES (7,'pg-study','',2,0,'2009-07-23',25);
INSERT INTO `csm_protection_group` VALUES (9,'pg-aliquots','',2,0,'2010-03-15',21);
INSERT INTO `csm_protection_group` VALUES (11,'CBSR Site Access','CBSR access to all added sites',2,0,'2010-04-20',NULL);
INSERT INTO `csm_protection_group` VALUES (12,'pg-labelingscheme','',2,0,'2010-01-29',13);
INSERT INTO `csm_protection_group` VALUES (13,'pg-containers-parent','Children=pg-container-type, pg-container, pg-labelingScheme, pg-container-position',2,0,'2010-01-29',NULL);
INSERT INTO `csm_protection_group` VALUES (14,'pg-containers-position','',2,0,'2010-01-29',13);
INSERT INTO `csm_protection_group` VALUES (15,'pg-container-type','',2,0,'2010-01-29',13);
INSERT INTO `csm_protection_group` VALUES (16,'pg-patients-parent','Children= pg-patients, pg-visits-samples-all',2,0,'2010-01-29',NULL);
INSERT INTO `csm_protection_group` VALUES (17,'pg-visits-parent','Children=pg-pvAttr, pg-visits, pg-samples-all, pg-sample-source',2,0,'2010-01-29',NULL);
INSERT INTO `csm_protection_group` VALUES (18,'pg-pv-sourcevessels-pvAttr','',2,0,'2010-05-18',17);
INSERT INTO `csm_protection_group` VALUES (19,'pg-visits','',2,0,'2010-01-29',17);
INSERT INTO `csm_protection_group` VALUES (20,'pg-globalAttr-sourceVessel-attrType','',2,0,'2010-05-18',17);
INSERT INTO `csm_protection_group` VALUES (21,'pg-aliquots-parent','Children= pg-aliquot-type, pg-aliquot, pg-aliquot-position',2,0,'2010-04-20',NULL);
INSERT INTO `csm_protection_group` VALUES (22,'pg-aliquot-position','',2,0,'2010-03-15',21);
INSERT INTO `csm_protection_group` VALUES (23,'pg-sample-type','',2,0,'2010-01-29',21);
INSERT INTO `csm_protection_group` VALUES (24,'pg-site','',2,0,'2010-01-29',33);
INSERT INTO `csm_protection_group` VALUES (25,'pg-studies-parent','Children= pg-study, pg-sampleStorage, pg-StudyPvAttr, pg-study-samplesource?',2,0,'2010-01-29',NULL);
INSERT INTO `csm_protection_group` VALUES (26,'pg-sampleStorage','',2,0,'2010-01-29',25);
INSERT INTO `csm_protection_group` VALUES (27,'pg-clinics-parent','Children=pg-shipments, pg-shippingCompany, pg-clinics',2,0,'2010-01-29',NULL);
INSERT INTO `csm_protection_group` VALUES (29,'pg-shipment','',2,0,'2010-01-29',27);
INSERT INTO `csm_protection_group` VALUES (30,'pg-shipping-method','',2,0,'2010-04-16',27);
INSERT INTO `csm_protection_group` VALUES (31,'pg-StudyPvAttr','',2,0,'2010-01-29',25);
INSERT INTO `csm_protection_group` VALUES (32,'pg-sitePvAttr','',2,0,'2010-01-29',33);
INSERT INTO `csm_protection_group` VALUES (33,'pg-site-parent','Children=pg-site, pg-sitePvAttr',2,0,'2010-01-29',NULL);
INSERT INTO `csm_protection_group` VALUES (34,'pg-visits-aliquots-all','',2,0,'2010-03-15',16);
INSERT INTO `csm_protection_group` VALUES (35,'pg-aliquot-all','',2,0,'2010-03-15',17);
INSERT INTO `csm_protection_group` VALUES (36,'pg-study-sourcevessel','',2,0,'2010-04-16',25);
INSERT INTO `csm_protection_group` VALUES (39,'pg-all-except-site','Contains all PE except sites specific and Site Classes',2,0,'2010-04-20',NULL);
INSERT INTO `csm_protection_group` VALUES (40,'biobank.cbsr.container.administration','',2,0,'2010-04-26',NULL);
INSERT INTO `csm_protection_group` VALUES (41,'pg-link-assign','',2,0,'2010-04-26',NULL);
/*!40000 ALTER TABLE `csm_protection_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_role`
--

DROP TABLE IF EXISTS `csm_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_role` (
  `ROLE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ROLE_NAME` varchar(100) NOT NULL,
  `ROLE_DESCRIPTION` varchar(200) DEFAULT NULL,
  `APPLICATION_ID` bigint(20) NOT NULL,
  `ACTIVE_FLAG` tinyint(1) NOT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`ROLE_ID`),
  UNIQUE KEY `UQ_ROLE_ROLE_NAME` (`APPLICATION_ID`,`ROLE_NAME`),
  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),
  CONSTRAINT `FK_APPLICATION_ROLE` FOREIGN KEY (`APPLICATION_ID`) REFERENCES `csm_application` (`APPLICATION_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_role`
--

LOCK TABLES `csm_role` WRITE;
/*!40000 ALTER TABLE `csm_role` DISABLE KEYS */;
INSERT INTO `csm_role` VALUES (1,'read','',2,1,'2009-07-22');
INSERT INTO `csm_role` VALUES (2,'create-delete-update','',2,1,'2009-07-22');
INSERT INTO `csm_role` VALUES (4,'create','',2,1,'2010-01-28');
INSERT INTO `csm_role` VALUES (5,'delete','',2,1,'2010-01-28');
INSERT INTO `csm_role` VALUES (6,'update','',2,1,'2010-01-28');
/*!40000 ALTER TABLE `csm_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_role_privilege`
--

DROP TABLE IF EXISTS `csm_role_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_role_privilege` (
  `ROLE_PRIVILEGE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ROLE_ID` bigint(20) NOT NULL,
  `PRIVILEGE_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ROLE_PRIVILEGE_ID`),
  UNIQUE KEY `UQ_ROLE_PRIVILEGE_ROLE_ID` (`PRIVILEGE_ID`,`ROLE_ID`),
  KEY `idx_PRIVILEGE_ID` (`PRIVILEGE_ID`),
  KEY `idx_ROLE_ID` (`ROLE_ID`),
  CONSTRAINT `FK_PRIVILEGE_ROLE` FOREIGN KEY (`PRIVILEGE_ID`) REFERENCES `csm_privilege` (`PRIVILEGE_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_ROLE` FOREIGN KEY (`ROLE_ID`) REFERENCES `csm_role` (`ROLE_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_role_privilege`
--

LOCK TABLES `csm_role_privilege` WRITE;
/*!40000 ALTER TABLE `csm_role_privilege` DISABLE KEYS */;
INSERT INTO `csm_role_privilege` VALUES (10,2,1);
INSERT INTO `csm_role_privilege` VALUES (13,4,1);
INSERT INTO `csm_role_privilege` VALUES (8,1,3);
INSERT INTO `csm_role_privilege` VALUES (11,2,5);
INSERT INTO `csm_role_privilege` VALUES (15,6,5);
INSERT INTO `csm_role_privilege` VALUES (9,2,6);
INSERT INTO `csm_role_privilege` VALUES (14,5,6);
/*!40000 ALTER TABLE `csm_role_privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user`
--

DROP TABLE IF EXISTS `csm_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_user` (
  `USER_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `LOGIN_NAME` varchar(500) NOT NULL,
  `MIGRATED_FLAG` tinyint(1) NOT NULL DEFAULT '0',
  `FIRST_NAME` varchar(100) NOT NULL,
  `LAST_NAME` varchar(100) NOT NULL,
  `ORGANIZATION` varchar(100) DEFAULT NULL,
  `DEPARTMENT` varchar(100) DEFAULT NULL,
  `TITLE` varchar(100) DEFAULT NULL,
  `PHONE_NUMBER` varchar(15) DEFAULT NULL,
  `PASSWORD` varchar(100) DEFAULT NULL,
  `EMAIL_ID` varchar(100) DEFAULT NULL,
  `START_DATE` date DEFAULT NULL,
  `END_DATE` date DEFAULT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  `PREMGRT_LOGIN_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `UQ_LOGIN_NAME` (`LOGIN_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user`
--

LOCK TABLES `csm_user` WRITE;
/*!40000 ALTER TABLE `csm_user` DISABLE KEYS */;
INSERT INTO `csm_user` VALUES (1,'administrator',0,'Administrator','NoName',NULL,NULL,NULL,NULL,'zJPWCwDeSgG8j2uyHEABIQ==',NULL,NULL,NULL,'2009-07-22',NULL);
INSERT INTO `csm_user` VALUES (2,'bbadmin',0,'Biobank Administrator','NoName',NULL,NULL,NULL,NULL,'7Bg9siN5e7M=',NULL,NULL,NULL,'2009-07-22',NULL);
INSERT INTO `csm_user` VALUES (8,'miniaci',0,'Jessica','Miniaci','Canadian Biosample Repository','','Laboratory Technician','780-919-6735','ACrDFGBVCHOq4RawigB4Ig==','jessica.miniaci@ualberta.ca',NULL,NULL,'2010-07-08','');
INSERT INTO `csm_user` VALUES (9,'elizabeth',0,'Elizabeth','Taylor','','','','','Vzk3xic4SKi8j2uyHEABIQ==','',NULL,NULL,'2010-04-16','');
INSERT INTO `csm_user` VALUES (10,'peck',0,'Aaron','Peck','Canadian Biosample Repository','','Lab Technician','','zs4yUro9LDo=','aaron.peck@ualberta.ca',NULL,NULL,'2010-07-08','');
INSERT INTO `csm_user` VALUES (11,'holland',0,'Charity','Holland','Canadian Biosample Repository','','','','jgw6x+HUai0=','charity.holland@ualberta.ca',NULL,NULL,'2010-07-08','');
INSERT INTO `csm_user` VALUES (12,'Meagen',0,'Meagen','LaFave','','','','','Z2+3QVFL27DxoZuHa6AoWA==','cbsr.financial@me.com',NULL,NULL,'2010-06-08','');
INSERT INTO `csm_user` VALUES (13,'degrisda',0,'Delphine','Degris-Dard','','','','','CFu6ZPVAO+S8j2uyHEABIQ==','',NULL,NULL,'2010-06-30','');
INSERT INTO `csm_user` VALUES (15,'loyola',0,'Nelson','Loyola','','','','','Um6QXDsC3vs=','loyola@ualberta.ca',NULL,NULL,'2010-07-14','');
INSERT INTO `csm_user` VALUES (17,'tpolasek',0,'thomas','polasek','','','','','8y8jUYdY0sg=','',NULL,NULL,'2010-06-30','');
INSERT INTO `csm_user` VALUES (18,'aaron_aicml',0,'Aaron','Young','','','','','','aaron.young@ualberta.ca',NULL,NULL,'2010-06-30','');
INSERT INTO `csm_user` VALUES (19,'Andrijana',0,'Andrijana','Lawton','','','','','V4PzQj6by/Q=','',NULL,NULL,'2010-08-11','');
INSERT INTO `csm_user` VALUES (20,'Virginia',0,'Virginia','Doe','','','','','tsjSShkZ7qC8j2uyHEABIQ==','',NULL,NULL,'2010-08-11','');
/*!40000 ALTER TABLE `csm_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user_group`
--

DROP TABLE IF EXISTS `csm_user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_user_group` (
  `USER_GROUP_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` bigint(20) NOT NULL,
  `GROUP_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`USER_GROUP_ID`),
  KEY `idx_USER_ID` (`USER_ID`),
  KEY `idx_GROUP_ID` (`GROUP_ID`),
  CONSTRAINT `FK_UG_GROUP` FOREIGN KEY (`GROUP_ID`) REFERENCES `csm_group` (`GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_group`
--

LOCK TABLES `csm_user_group` WRITE;
/*!40000 ALTER TABLE `csm_user_group` DISABLE KEYS */;
INSERT INTO `csm_user_group` VALUES (23,9,5);
INSERT INTO `csm_user_group` VALUES (26,8,5);
INSERT INTO `csm_user_group` VALUES (27,13,5);
INSERT INTO `csm_user_group` VALUES (28,17,7);
INSERT INTO `csm_user_group` VALUES (29,11,7);
INSERT INTO `csm_user_group` VALUES (32,15,5);
INSERT INTO `csm_user_group` VALUES (33,18,5);
INSERT INTO `csm_user_group` VALUES (35,10,5);
INSERT INTO `csm_user_group` VALUES (36,12,6);
INSERT INTO `csm_user_group` VALUES (42,20,6);
INSERT INTO `csm_user_group` VALUES (43,19,6);
/*!40000 ALTER TABLE `csm_user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user_group_role_pg`
--

DROP TABLE IF EXISTS `csm_user_group_role_pg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_user_group_role_pg` (
  `USER_GROUP_ROLE_PG_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` bigint(20) DEFAULT NULL,
  `GROUP_ID` bigint(20) DEFAULT NULL,
  `ROLE_ID` bigint(20) NOT NULL,
  `PROTECTION_GROUP_ID` bigint(20) NOT NULL,
  `UPDATE_DATE` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`USER_GROUP_ROLE_PG_ID`),
  KEY `idx_GROUP_ID` (`GROUP_ID`),
  KEY `idx_ROLE_ID` (`ROLE_ID`),
  KEY `idx_PROTECTION_GROUP_ID` (`PROTECTION_GROUP_ID`),
  KEY `idx_USER_ID` (`USER_ID`),
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_GROUPS` FOREIGN KEY (`GROUP_ID`) REFERENCES `csm_group` (`GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_PROTECTION_GROUP` FOREIGN KEY (`PROTECTION_GROUP_ID`) REFERENCES `csm_protection_group` (`PROTECTION_GROUP_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_ROLE` FOREIGN KEY (`ROLE_ID`) REFERENCES `csm_role` (`ROLE_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_USER_GROUP_ROLE_PROTECTION_GROUP_USER` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_group_role_pg`
--

LOCK TABLES `csm_user_group_role_pg` WRITE;
/*!40000 ALTER TABLE `csm_user_group_role_pg` DISABLE KEYS */;
INSERT INTO `csm_user_group_role_pg` VALUES (32,NULL,5,2,1,'2010-01-28');
INSERT INTO `csm_user_group_role_pg` VALUES (33,NULL,5,1,1,'2010-01-28');
INSERT INTO `csm_user_group_role_pg` VALUES (34,NULL,5,2,11,'2010-01-28');
INSERT INTO `csm_user_group_role_pg` VALUES (35,NULL,5,1,11,'2010-01-28');
INSERT INTO `csm_user_group_role_pg` VALUES (36,NULL,6,1,1,'2010-01-28');
INSERT INTO `csm_user_group_role_pg` VALUES (37,NULL,6,1,11,'2010-01-28');
INSERT INTO `csm_user_group_role_pg` VALUES (46,NULL,7,1,1,'2010-01-28');
INSERT INTO `csm_user_group_role_pg` VALUES (51,NULL,7,1,11,'2010-01-28');
INSERT INTO `csm_user_group_role_pg` VALUES (54,NULL,6,2,13,'2010-01-29');
INSERT INTO `csm_user_group_role_pg` VALUES (55,NULL,6,2,29,'2010-01-29');
INSERT INTO `csm_user_group_role_pg` VALUES (56,NULL,6,2,16,'2010-01-29');
INSERT INTO `csm_user_group_role_pg` VALUES (57,NULL,7,2,5,'2010-01-29');
INSERT INTO `csm_user_group_role_pg` VALUES (58,NULL,7,2,9,'2010-01-29');
INSERT INTO `csm_user_group_role_pg` VALUES (59,NULL,7,2,22,'2010-01-29');
INSERT INTO `csm_user_group_role_pg` VALUES (60,NULL,7,2,29,'2010-01-29');
INSERT INTO `csm_user_group_role_pg` VALUES (61,NULL,7,2,19,'2010-01-29');
INSERT INTO `csm_user_group_role_pg` VALUES (64,NULL,8,1,1,'2010-04-20');
INSERT INTO `csm_user_group_role_pg` VALUES (68,NULL,8,2,13,'2010-04-20');
INSERT INTO `csm_user_group_role_pg` VALUES (69,NULL,8,2,27,'2010-04-20');
INSERT INTO `csm_user_group_role_pg` VALUES (70,NULL,8,2,25,'2010-04-20');
INSERT INTO `csm_user_group_role_pg` VALUES (71,NULL,8,2,16,'2010-04-20');
INSERT INTO `csm_user_group_role_pg` VALUES (72,NULL,8,2,17,'2010-04-20');
INSERT INTO `csm_user_group_role_pg` VALUES (73,NULL,8,2,21,'2010-04-20');
INSERT INTO `csm_user_group_role_pg` VALUES (74,NULL,9,2,39,'2010-04-20');
INSERT INTO `csm_user_group_role_pg` VALUES (75,NULL,9,1,1,'2010-04-20');
INSERT INTO `csm_user_group_role_pg` VALUES (76,NULL,5,2,40,'2010-04-26');
INSERT INTO `csm_user_group_role_pg` VALUES (77,NULL,6,2,40,'2010-04-26');
INSERT INTO `csm_user_group_role_pg` VALUES (79,NULL,7,4,41,'2010-04-26');
INSERT INTO `csm_user_group_role_pg` VALUES (80,NULL,7,6,41,'2010-04-26');
INSERT INTO `csm_user_group_role_pg` VALUES (81,NULL,7,2,18,'2010-05-18');
/*!40000 ALTER TABLE `csm_user_group_role_pg` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `csm_user_pe`
--

DROP TABLE IF EXISTS `csm_user_pe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `csm_user_pe` (
  `USER_PROTECTION_ELEMENT_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL,
  `USER_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`USER_PROTECTION_ELEMENT_ID`),
  UNIQUE KEY `UQ_USER_PROTECTION_ELEMENT_PROTECTION_ELEMENT_ID` (`USER_ID`,`PROTECTION_ELEMENT_ID`),
  KEY `idx_USER_ID` (`USER_ID`),
  KEY `idx_PROTECTION_ELEMENT_ID` (`PROTECTION_ELEMENT_ID`),
  CONSTRAINT `FK_PE_USER` FOREIGN KEY (`USER_ID`) REFERENCES `csm_user` (`USER_ID`) ON DELETE CASCADE,
  CONSTRAINT `FK_PROTECTION_ELEMENT_USER` FOREIGN KEY (`PROTECTION_ELEMENT_ID`) REFERENCES `csm_protection_element` (`PROTECTION_ELEMENT_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `csm_user_pe`
--

LOCK TABLES `csm_user_pe` WRITE;
/*!40000 ALTER TABLE `csm_user_pe` DISABLE KEYS */;
INSERT INTO `csm_user_pe` VALUES (1,1,1);
INSERT INTO `csm_user_pe` VALUES (2,2,2);
/*!40000 ALTER TABLE `csm_user_pe` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-10-13 16:32:20
