LOCK TABLES `GLOBAL_PV_ATTR` WRITE;

INSERT INTO `global_pv_attr` (ID, LABEL, PV_ATTR_TYPE_ID) VALUES
(1,"PBMC Count (x10^6)",1),
(2,"Worksheet",2),
(3,"Consent",5),
(4,"Phlebotomist",2),
(5,"Visit Type",4),
(6,"Biopsy Length",1);

UNLOCK TABLES;
