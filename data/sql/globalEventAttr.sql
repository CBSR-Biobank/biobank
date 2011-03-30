LOCK TABLES `GLOBAL_EVENT_ATTR` WRITE;

INSERT INTO `global_event_attr` (ID, LABEL, EVENT_ATTR_TYPE_ID) VALUES
(1,"PBMC Count (x10^6)",1),
(3,"Consent",5),
(4,"Phlebotomist",2),
(6,"Biopsy Length",1),
(7,"Patient Type",4);

UNLOCK TABLES;
