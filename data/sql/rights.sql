LOCK TABLES `BB_RIGHT` WRITE;
INSERT INTO `BB_RIGHT` (ID, VERSION, NAME, FOR_SITE, FOR_CLINIC, FOR_RESEARCH_GROUP, FOR_STUDY, KEY_DESC) VALUES
( 0, 0, 'Specimen Link', 1, 1, 0, 0, 'specimen-link'),
( 1, 0, 'Specimen Assign', 1, 0, 0, 0, 'specimen-assign'),
( 2, 0, 'Study', 1, 1, 1, 1, 'Study'),
( 3, 0, 'Clinic', 1, 1, 1, 1, 'Clinic'),
( 4, 0, 'Site', 1, 1, 1, 1, 'Site'),
( 5, 0, 'Research Group', 1, 1, 1, 1, 'ResearchGroup'),
( 6, 0, 'Logging', 1, 1, 1, 1, 'logging'),
( 7, 0, 'Reports', 1, 1, 1, 1, 'reports'),
( 8, 0, 'Center administration', 1, 1, 1, 1, 'center-admin'),
( 9, 0, 'Container', 1, 0, 0, 0, 'Container'),
( 10, 0, 'Container Type', 1, 0, 0, 0, 'ContainerType'),
( 11, 0, 'Patient', 1, 1, 1, 1, 'Patient'),
( 12, 0, 'Collection Event', 1, 1, 0, 0, 'CollectionEvent'),
( 13, 0, 'Processing Event', 1, 1, 0, 0, 'ProcessingEvent'),
( 14, 0, 'Send Dispatch', 1, 1, 0, 0, 'send-Dispatch'),
( 15, 0, 'Receive Dispatch', 1, 1, 1, 0, 'receive-Dispatch'),
( 16, 0, 'Ask Request', 0, 0, 1, 1, 'ask-request'),
( 17, 0, 'Receive Request', 1, 0, 0, 0, 'receive-request'),
( 18, 0, 'Clinic Shipment (CBSR special)', 1, 0, 0, 0, 'cs-OriginInfo'),
( 19, 0, 'Print labels', 1, 1, 0, 0, 'print-labels'),
( 20, 0, 'Specimen Types', 1, 1, 1, 1, 'SpecimenType'),
( 21, 0, 'Shipping Methods', 1, 1, 1, 1, 'ShippingMethod'),
( 22, 0, 'Activity Status', 1, 1,1, 1, 'ActivityStatus');
UNLOCK TABLES;

