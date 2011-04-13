LOCK TABLES `EVENT_ATTR_TYPE` WRITE;
INSERT INTO `EVENT_ATTR_TYPE` (ID, NAME, LAST_UPDATED) VALUES
       ( 1, 'number',           "1970-01-01"),
       ( 2, 'text',             "1970-01-01"),
       ( 3, 'date_time',        "1970-01-01"),
       ( 4, 'select_single',    "1970-01-01"),
       ( 5, 'select_multiple',  "1970-01-01");
UNLOCK TABLES;
