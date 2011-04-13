LOCK TABLES `CONTAINER_LABELING_SCHEME` WRITE;
INSERT INTO `CONTAINER_LABELING_SCHEME` (ID, NAME, MIN_CHARS, MAX_CHARS, MAX_ROWS, MAX_COLS, MAX_CAPACITY, LAST_MODIFIY_DATE_TIME) VALUES
( 1, "SBS Standard",           2, 3, 16,   24,   384, "1970-01-01"),
( 2, "CBSR 2 char alphabetic", 2, 2, null, null, 576, "1970-01-01"),
( 3, "2 char numeric",         2, 2, null, null, 99,  "1970-01-01"),
( 4, "Dewar",                  2, 2, 2,    2,    4,   "1970-01-01"),
( 5, "CBSR SBS",               2, 2, 9,    9,    81,  "1970-01-01");
UNLOCK TABLES;
