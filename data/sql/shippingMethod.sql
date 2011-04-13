LOCK TABLES `SHIPPING_METHOD` WRITE;
INSERT INTO `SHIPPING_METHOD` (ID, NAME, LAST_MODIFIY_DATE_TIME) VALUES
       ( 1, 'unknown',          "1970-01-01"),
       ( 2, 'Drop-off',         "1970-01-01"),
       ( 3, 'Pick-up',          "1970-01-01"),
       ( 4, 'Inter-Hospital',   "1970-01-01"),
       ( 5, 'Canada Post',      "1970-01-01"),
       ( 6, 'DHL',              "1970-01-01"),
       ( 7,' FedEx',            "1970-01-01"),
       ( 8, 'Hospital Courier', "1970-01-01"),
       ( 9, 'Purolator',        "1970-01-01");
UNLOCK TABLES;
