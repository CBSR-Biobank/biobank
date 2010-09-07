LOCK TABLES `ACTIVITY_STATUS` WRITE;
INSERT INTO `ACTIVITY_STATUS` (ID, NAME) VALUES
( 1, "Active"),
( 2, "Closed"),
( 3, "Disabled"),
( 4, "Flagged"),
( 5, "Dispatched");
UNLOCK TABLES;

