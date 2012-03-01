LOCK TABLES `GLOBAL_SPECIMEN_ATTR` WRITE;

INSERT INTO `global_specimen_attr` (ID, LABEL, SPECIMEN_ATTR_TYPE_ID, VERSION) VALUES
(1, "Volume", 1, 0),
(2, "Concentration", 1, 0),
(3, "startProcess", 3, 0),
(4, "endProcess", 3, 0),
(5, "SampleErrors", 2, 0);

UNLOCK TABLES;
