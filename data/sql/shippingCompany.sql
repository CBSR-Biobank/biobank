LOCK TABLES `SHIPPING_COMPANY` WRITE;
INSERT INTO `SHIPPING_COMPANY` (ID, NAME) VALUES
       ( 1,'Canada Post'),
       ( 2,'DHL'),
       ( 3,'FedEx'),
       ( 4,'Hospital Courier'),
       ( 5,'Purolator');
UNLOCK TABLES;
