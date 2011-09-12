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
( 8, 0, 'Container', 1, 0, 0, 0, 'Container'),
( 9, 0, 'Container Type', 1, 0, 0, 0, 'ContainerType'),
( 10, 0, 'Patient', 1, 1, 1, 1, 'Patient'),
( 11, 0, 'Collection Event', 1, 1, 0, 0, 'CollectionEvent'),
( 12, 0, 'Processing Event', 1, 1, 0, 0, 'ProcessingEvent'),
( 13, 0, 'Send Dispatch', 1, 1, 0, 0, 'send-Dispatch'),
( 14, 0, 'Receive Dispatch', 1, 1, 1, 0, 'receive-Dispatch'),
( 15, 0, 'Create Specimen Request', 0, 0, 1, 1, 'create-spec-request'),
( 16, 0, 'Receive Specimen Request', 1, 0, 0, 0, 'receive-spec-request'),
( 17, 0, 'Clinic Shipment (CBSR special)', 1, 0, 0, 0, 'cs-OriginInfo'),
( 18, 0, 'Print labels', 1, 1, 0, 0, 'print-labels'),
( 19, 0, 'Specimen Types', 1, 1, 1, 1, 'SpecimenType'),
( 20, 0, 'Shipping Methods', 1, 1, 1, 1, 'ShippingMethod'),
( 21, 0, 'Activity Status', 1, 1,1, 1, 'ActivityStatus'),
( 22, 0, 'Specimen', 1, 1, 1, 1, 'Specimen'),
( 23, 0, 'User Management', 1, 1, 1, 1, 'user-mgt'),
( 24, 0, 'Administrator', 1, 1, 1, 1, 'admin');
UNLOCK TABLES;

-- privileges:
-- read = 1
-- update = 2
-- delete = 3
-- create = 4
-- allowed = 5

LOCK TABLES `right_privilege` WRITE;
INSERT INTO `right_privilege` (right_id, privilege_id) VALUES
-- specimen link
( 0, 5),
-- specimen assign
( 1, 5),
-- study
( 2, 1),
( 2, 2),
( 2, 3),
( 2, 4),
-- clinic
( 3, 1),
( 3, 2),
( 3, 3),
( 3, 4),
-- Site
( 4, 1),
( 4, 2),
( 4, 3),
( 4, 4),
-- Research Group
( 5, 1),
( 5, 2),
( 5, 3),
( 5, 4),
-- Logging
( 6, 5),
-- reports
( 7, 5),
-- Container
( 8, 1),
( 8, 2),
( 8, 3),
( 8, 4),
-- Container Type
( 9, 1),
( 9, 2),
( 9, 3),
( 9, 4),
-- Patient
( 10, 1),
( 10, 2),
( 10, 3),
( 10, 4),
-- Collection Event
( 11, 1),
( 11, 2),
( 11, 3),
( 11, 4),
-- Processing Event
( 12, 1),
( 12, 2),
( 12, 3),
( 12, 4),
-- Send Dispatch
( 13, 5),
-- Receive Dispatch
( 14, 5),
-- Create specimen request
( 15, 5),
-- Receive specimen request
( 16, 5),
-- Clinic shipment
( 17, 1),
( 17, 2),
( 17, 3),
( 17, 4),
-- Print labels
( 18, 5),
-- Specimen types
( 19, 1),
( 19, 2),
( 19, 3),
( 19, 4),
-- Shipping methods
( 20, 1),
( 20, 2),
( 20, 3),
( 20, 4),
-- Activity status
( 21, 1),
( 21, 2),
( 21, 3),
( 21, 4),
-- Specimen
( 22, 1),
( 22, 2),
( 22, 3),
( 22, 4),
-- User management
( 23, 5),
-- Administrator
( 24, 5);
UNLOCK TABLES;


