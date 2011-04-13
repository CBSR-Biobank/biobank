LOCK TABLES `GLOBAL_EVENT_ATTR` WRITE;

INSERT INTO `global_event_attr` (ID, LABEL, EVENT_ATTR_TYPE_ID, LAST_UPDATED) VALUES
(1, "PBMC Count (x10^6)", 1, "1970-01-01"),
(3, "Consent",            5, "1970-01-01"),
(4, "Phlebotomist",       2, "1970-01-01"),
(6, "Biopsy Length",      1, "1970-01-01"),
(7, "Patient Type",       4, "1970-01-01");

UNLOCK TABLES;
