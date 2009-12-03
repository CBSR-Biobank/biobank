LOCK TABLES `PV_ATTR_TYPE` WRITE;
INSERT INTO `PV_ATTR_TYPE` (ID, NAME) VALUES
       ( 1,'number'),
       ( 2,'text'),
       ( 3,'date_time'),
       ( 4,'select_single'),
       ( 5,'select_multiple');
UNLOCK TABLES;
