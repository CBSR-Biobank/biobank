LOCK TABLES `SHIPPING_METHOD` WRITE;
INSERT INTO `SHIPPING_METHOD` (ID, NAME) VALUES
       ( 1,'unknown'),
       ( 2,'Drop-off'),
       ( 3,'Pick-up'),
       ( 4,'Inter-Hospital'),
       ( 5,'Canada Post'),
       ( 6,'DHL'),
       ( 7,'FedEx'),
       ( 8,'Hospital Courier'),
       ( 9,'Purolator');
UNLOCK TABLES;
