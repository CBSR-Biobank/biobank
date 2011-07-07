
-- start bug#1286 fix
-- the ContainerPath object was merged into Container, so removeit from the EntityProperty-s
-- that use it.

UPDATE entity_property SET property = 'specimenPosition.container.topContainer.id' WHERE id = '6';
UPDATE entity_property SET property = 'specimenPosition.container.topContainer.containerType.nameShort' WHERE id = '13';
UPDATE entity_property SET property = 'topContainer.id' WHERE id = '105';
UPDATE entity_property SET property = 'topContainer.containerType.nameShort' WHERE id = '110';

-- shipmentInfo.sentAt was replaced with shipmentInfo.packedAt

UPDATE entity_property SET property = 'originInfo.shipmentInfo.packedAt' WHERE id = 19;
UPDATE entity_property SET property = 'dispatchSpecimenCollection.dispatch.shipmentInfo.packedAt' WHERE id = 25;
-- end bug#1286 fix

-- start bug#1279 fix
-- add properties for columns and filters for collectionEvent-s and topSpecimen-s

INSERT INTO entity_property VALUES (29, 'topSpecimen.inventoryId', 1, 1, 0);
INSERT INTO entity_property VALUES (30, 'topSpecimen.originInfo.center.nameShort', 1, 1, 0);
INSERT INTO entity_property VALUES (31, 'topSpecimen.createdAt', 3, 1, 0);

INSERT INTO entity_column VALUES (28, 'Source Specimen Inventory Id', 29, 0);
INSERT INTO entity_column VALUES (29, 'Source Specimen Source Center', 30, 0);
INSERT INTO entity_column VALUES (30, 'Time Drawn', 31, 0);

INSERT INTO entity_filter VALUES (27, 7, 'Visit Number', 28, 0);
INSERT INTO entity_filter VALUES (28, 1, 'Source Specimen Inventory Id', 29, 0);
INSERT INTO entity_filter VALUES (29, 1, 'Source Specimen Source Center', 30, 0);
INSERT INTO entity_filter VALUES (30, 3, 'Time Drawn', 31, 0);

-- end bug#1279 fix
