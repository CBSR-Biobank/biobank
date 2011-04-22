LOCK TABLES `SHIPPING_METHOD` WRITE;
INSERT INTO `SHIPPING_METHOD` (ID, NAME, VERSION) VALUES
       ( 1, 'unknown',          0),
       ( 2, 'Drop-off',         0),
       ( 3, 'Pick-up',          0),
       ( 4, 'Inter-Hospital',   0),
       ( 5, 'Canada Post',      0),
       ( 6, 'DHL',              0),
       ( 7,' FedEx',            0),
       ( 8, 'Hospital Courier', 0),
       ( 9, 'Purolator',        0);
UNLOCK TABLES;
