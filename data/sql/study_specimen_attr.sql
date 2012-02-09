LOCK TABLES `STUDY_SPECIMEN_ATTR` WRITE;

INSERT INTO `study_specimen_attr` (ID, VERSION, LABEL, PERMISSIBLE, REQUIRED, ACTIVITY_STATUS_ID, SPECIMEN_ATTR_TYPE_ID, STUDY_ID) VALUES
(1, 0, "Volume", NULL, "", 1, 1, 2),
(2, 0, "Concentration", NULL, "", 1, 1, 2),
(3, 0, "startProcess", NULL, "", 1, 3, 2),
(4, 0, "endProcess", NULL, "", 1, 3, 2),
(5, 0, "SampleErrors", NULL, "", 1, 2, 2);

UNLOCK TABLES;
