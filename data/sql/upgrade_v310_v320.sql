-- start bug#1405 fix
-- add properties for columns and filters for collectionEvent-s and topSpecimen-s

INSERT INTO entity_property VALUES (32, 'processingEvent.worksheet', 1, 1, 0);

INSERT INTO entity_column VALUES (31, 'Processing Event Worksheet', 32, 0);

INSERT INTO entity_filter VALUES (31, 1, 'Processing Event Worksheet', 32, 0);

-- end bug#1405 fix
