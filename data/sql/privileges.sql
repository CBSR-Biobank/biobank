LOCK TABLES `PRIVILEGE` WRITE;
INSERT INTO `PRIVILEGE` (ID, VERSION, NAME) VALUES
( 1, 0, 'read'),
( 2, 0, 'update'),
( 3, 0, 'delete'),
( 4, 0, 'create');

UNLOCK TABLES;

