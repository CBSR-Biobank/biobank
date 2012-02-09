LOCK TABLES `SPECIMEN_ATTR_TYPE` WRITE;
INSERT INTO `SPECIMEN_ATTR_TYPE` (ID, NAME, VERSION) VALUES
       ( 1, 'number',           0),
       ( 2, 'text',             0),
       ( 3, 'date_time',        0),
       ( 4, 'select_single',    0),
       ( 5, 'select_multiple',  0);
UNLOCK TABLES;

