-- add foreign keys on ContainerPosition to ensure that (1) the parent
-- container and its type, (2) the container and its type, and (3) there
-- is a parent-child relationship between the parent container's container
-- type and the container's container type, respectively. Note that there
-- must be a key on container (id, container_type_id) to make a foreign
-- key to it
ALTER TABLE `CONTAINER` ADD KEY (`ID`, `CONTAINER_TYPE_ID`);
ALTER TABLE `CONTAINER_POSITION` ADD FOREIGN KEY (`PARENT_CONTAINER_ID`, `PARENT_CONTAINER_TYPE_ID`) REFERENCES `CONTAINER` (`ID`, `CONTAINER_TYPE_ID`);
ALTER TABLE `CONTAINER_POSITION` ADD FOREIGN KEY (`CONTAINER_ID`, `CONTAINER_TYPE_ID`) REFERENCES `CONTAINER` (`ID`, `CONTAINER_TYPE_ID`);
ALTER TABLE `CONTAINER_POSITION` ADD FOREIGN KEY (`PARENT_CONTAINER_TYPE_ID`, `CONTAINER_TYPE_ID`) REFERENCES `CONTAINER_TYPE_CONTAINER_TYPE` (`PARENT_CONTAINER_TYPE_ID`, `CHILD_CONTAINER_TYPE_ID`);
