LOCK TABLES `SHIPPING_COMPANY` WRITE;
INSERT INTO `SHIPPING_COMPANY` (ID, NAME) VALUES
       ( 1,'unknown'),
       ( 2,'Canada Post'),
       ( 3,'DHL'),
       ( 4,'FedEx'),
       ( 5,'Hospital Courier'),
       ( 6,'Purolator');
UNLOCK TABLES;
