-- allow FKs to be create on a ContainerType's (ID, SITE_ID)
ALTER TABLE `CONTAINER_TYPE` ADD KEY (`ID`, `SITE_ID`);

-- make it impossible for ContainerType-s to be related to other ContainerType-s
-- if they are not in the same site.
ALTER TABLE `CONTAINER_TYPE_CONTAINER_TYPE` ADD COLUMN `SITE_ID` int(11) NOT NULL;

ALTER TABLE `CONTAINER_TYPE_CONTAINER_TYPE` DROP FOREIGN KEY `FK_ContainerType_childContainerTypes`;
ALTER TABLE `CONTAINER_TYPE_CONTAINER_TYPE` DROP KEY `FK_ContainerType_childContainerTypes`;
ALTER TABLE `CONTAINER_TYPE_CONTAINER_TYPE` DROP FOREIGN KEY `FK_ContainerType_parentContainerTypes`;
ALTER TABLE `CONTAINER_TYPE_CONTAINER_TYPE` DROP KEY `FK_ContainerType_parentContainerTypes`;

ALTER TABLE `CONTAINER_TYPE_CONTAINER_TYPE` ADD CONSTRAINT `FK_ContainerType_parentContainerTypes`
  FOREIGN KEY `FK_ContainerType_parentContainerTypes` (`PARENT_CONTAINER_TYPE_ID`, `SITE_ID`)
  REFERENCES `CONTAINER_TYPE` (`ID`, `SITE_ID`);
ALTER TABLE `CONTAINER_TYPE_CONTAINER_TYPE` ADD CONSTRAINT `FK_ContainerType_childContainerTypes`
  FOREIGN KEY `FK_ContainerType_childContainerTypes` (`CHILD_CONTAINER_TYPE_ID`, `SITE_ID`)
  REFERENCES `CONTAINER_TYPE` (`ID`, `SITE_ID`);

-- make it impossible for Container's to have a ContainerType with a different
-- Site
ALTER TABLE `CONTAINER` DROP FOREIGN KEY `FK_Container_containerType`;
ALTER TABLE `CONTAINER` DROP KEY `FK_Container_containerType`;
ALTER TABLE `CONTAINER` ADD CONSTRAINT `FK_Container_containerType`
  FOREIGN KEY `FK_Container_containerType` (`CONTAINER_TYPE_ID`, `SITE_ID`)
  REFERENCES `CONTAINER_TYPE` (`ID`, `SITE_ID`);
