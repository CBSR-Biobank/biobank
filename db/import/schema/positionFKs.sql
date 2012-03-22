-- 
-- ContainerPosition FKs: ensure complete cooperation between ContainerType
-- definitions and the Container-s that use them.
--

-- add a key so other tables can have a FK on this combination
ALTER TABLE `CONTAINER` ADD KEY (`ID`, `CONTAINER_TYPE_ID`);

-- remove single-column parent container FK
ALTER TABLE `CONTAINER_POSITION` DROP FOREIGN KEY `FK_ContainerPosition_parentContainer`;
ALTER TABLE `CONTAINER_POSITION` DROP KEY `FK_ContainerPosition_parentContainer`;

-- add double-column parent Container FK, which stops the parent Container
-- from changing its ContainerType if it has a child Container
ALTER TABLE `CONTAINER_POSITION` ADD CONSTRAINT `FK_ContainerPosition_parentContainer`
  FOREIGN KEY `FK_ContainerPosition_parentContainer`
    (`PARENT_CONTAINER_ID`, `PARENT_CONTAINER_TYPE_ID`)
  REFERENCES `CONTAINER` (`ID`, `CONTAINER_TYPE_ID`);

-- add double-column Container FK, which stops the Container from changing
-- its ContainerType if it has a ContainerPosition
ALTER TABLE `CONTAINER_POSITION` ADD CONSTRAINT `FK_ContainerPosition_container`
  FOREIGN KEY `FK_ContainerPosition_Container` (`CONTAINER_ID`, `CONTAINER_TYPE_ID`)
  REFERENCES `CONTAINER` (`ID`, `CONTAINER_TYPE_ID`);

-- add a FK from the ContainerPosition to the `CONTAINER_TYPE_CONTAINER_TYPE`
-- table so that only ContainerPosition-s with an explicitly defined
-- ContainerType-ContainerType parent-child relationship can exist
ALTER TABLE `CONTAINER_POSITION` ADD CONSTRAINT `FK_ContainerPosition_containerTypeContainerType`
  FOREIGN KEY `FK_ContainerPosition_containerTypeContainerType`
    (`PARENT_CONTAINER_TYPE_ID`, `CONTAINER_TYPE_ID`)
  REFERENCES `CONTAINER_TYPE_CONTAINER_TYPE`
    (`PARENT_CONTAINER_TYPE_ID`, `CHILD_CONTAINER_TYPE_ID`);

-- 
-- SpecimenPosition FKs: ensure complete cooperation between ContainerType
-- definitions and the Specimen-s that use them.
--

-- add a key so other tables can have a FK on this combination
ALTER TABLE `SPECIMEN` ADD KEY (`ID`, `SPECIMEN_TYPE_ID`);

-- remove single-column Container FK
ALTER TABLE `SPECIMEN_POSITION` DROP FOREIGN KEY `FK_SpecimenPosition_container`;
ALTER TABLE `SPECIMEN_POSITION` DROP KEY `FK_SpecimenPosition_container`;

-- add double-column Container FK, which stops the Container from changing
-- its ContainerType if it has a child Specimen
ALTER TABLE `SPECIMEN_POSITION` ADD CONSTRAINT `FK_SpecimenPosition_container`
  FOREIGN KEY `FK_SpecimenPosition_container` (`CONTAINER_ID`, `CONTAINER_TYPE_ID`)
  REFERENCES `CONTAINER` (`ID`, `CONTAINER_TYPE_ID`);

-- add double-column Specimen FK, which stops the Specimen from changing
-- its SpecimenType if it has a SpecimenPosition
ALTER TABLE `SPECIMEN_POSITION` ADD CONSTRAINT `FK_SpecimenPosition_specimen`
  FOREIGN KEY `FK_SpecimenPosition_specimen` (`SPECIMEN_ID`, `SPECIMEN_TYPE_ID`)
  REFERENCES `SPECIMEN` (`ID`, `SPECIMEN_TYPE_ID`);

-- add a FK from the SpecimenPosition to the `CONTAINER_TYPE_SPECIMEN_TYPE`
-- table so that only SpecimenPosition-s with an explicitly defined
-- ContainerType-SpecimenType parent-child relationship can exist
ALTER TABLE `SPECIMEN_POSITION` ADD CONSTRAINT `FK_SpecimenPosition_containerTypeSpecimenType`
  FOREIGN KEY `FK_SpecimenPosition_containerTypeSpecimenType`
    (`CONTAINER_TYPE_ID`, `SPECIMEN_TYPE_ID`)
  REFERENCES `CONTAINER_TYPE_SPECIMEN_TYPE`
    (`CONTAINER_TYPE_ID`, `SPECIMEN_TYPE_ID`);
