LOCK TABLES `PRIVILEGE` WRITE;
INSERT INTO `PRIVILEGE` (ID, VERSION, NAME) VALUES
( 1, 0, 'Read'),
( 2, 0, 'Update'),
( 3, 0, 'Delete'),
( 4, 0, 'Create');
( 5, 0, 'Allowed');
UNLOCK TABLES;

