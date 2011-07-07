
-- the ContainerPath object was merged into Container, so removeit from the EntityProperty-s
-- that use it.

UPDATE entity_property SET property = 'specimenPosition.container.topContainer.id' WHERE id = '6';
UPDATE entity_property SET property = 'specimenPosition.container.topContainer.containerType.nameShort' WHERE id = '13';
UPDATE entity_property SET property = 'topContainer.id' WHERE id = '105';
UPDATE entity_property SET property = 'topContainer.containerType.nameShort' WHERE id = '110';

-- shipmentInfo.sentAt was replaced with shipmentInfo.packedAt

UPDATE entity_property SET property = 'originInfo.shipmentInfo.packedAt' WHERE id = 19;
UPDATE entity_property SET property = 'dispatchSpecimenCollection.dispatch.shipmentInfo.packedAt' WHERE id = 25;
