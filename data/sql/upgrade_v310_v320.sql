-- start bug#1405 fix
-- add properties for columns and filters for collectionEvent-s and topSpecimen-s

INSERT INTO entity_property (id, property, property_type_id, entity_id, version) VALUES 
  (32, 'processingEvent.worksheet', 1, 1, 0);

INSERT INTO entity_column (id, name, entity_property_id, version) VALUES
  (31, 'Processing Event Worksheet', 32, 0);

INSERT INTO entity_filter (id, filter_type, name, entity_property_id, version) VALUES
  (31, 1, 'Processing Event Worksheet', 32, 0);

-- end bug#1405 fix

-- start renaming of collection properties (to replace with plural)

UPDATE entity_property SET property = 'dispatchSpecimens.dispatch.senderCenter.nameShort' WHERE id = 22;
UPDATE entity_property SET property = 'dispatchSpecimens.dispatch.receiverCenter.nameShort' WHERE id = 23;
UPDATE entity_property SET property = 'dispatchSpecimens.dispatch.shipmentInfo.receivedAt' WHERE id = 24;
UPDATE entity_property SET property = 'dispatchSpecimens.dispatch.shipmentInfo.packedAt' WHERE id = 25;
UPDATE entity_property SET property = 'dispatchSpecimens.dispatch.shipmentInfo.waybill' WHERE id = 26;
UPDATE entity_property SET property = 'dispatchSpecimens.dispatch.shipmentInfo.boxNumber' WHERE id = 27;
UPDATE entity_property SET property = 'specimenPositions.specimen.createdAt' WHERE id = 106;
UPDATE entity_property SET property = 'collectionEvents.allSpecimens.parentSpecimen.processingEvent.createdAt' WHERE id = 203;
UPDATE entity_property SET property = 'collectionEvents.allSpecimens.createdAt' WHERE id = 204;
UPDATE entity_property SET property = 'collectionEvents.allSpecimens.originInfo.center.nameShort' WHERE id = 205;
UPDATE entity_property SET property = 'collectionEvents.allSpecimens.inventoryId' WHERE id = 206;
UPDATE entity_property SET property = 'collectionEvents.visitNumber' WHERE id = 207;
UPDATE entity_property SET property = 'allSpecimens.parentSpecimen.processingEvent.createdAt' WHERE id = 301;
UPDATE entity_property SET property = 'allSpecimens.createdAt' WHERE id = 302;
UPDATE entity_property SET property = 'allSpecimens.originInfo.center.nameShort' WHERE id = 305;
UPDATE entity_property SET property = 'specimens.inventoryId' WHERE id = 406;
UPDATE entity_property SET property = 'specimens.createdAt' WHERE id = 407;

-- end renaming of collection properties

-- -----------------------------------------------------------------------
--
-- New security / user management
--
-- -----------------------------------------------------------------------

-- add new objects into the csm database:
insert into csm_protection_element (protection_element_name, object_id, application_id, update_date) values
('edu.ualberta.med.biobank.model.User','edu.ualberta.med.biobank.model.User',2,sysdate()),
('edu.ualberta.med.biobank.model.Group','edu.ualberta.med.biobank.model.Group',2,sysdate()),
('edu.ualberta.med.biobank.model.Principal','edu.ualberta.med.biobank.model.Principal',2,sysdate()),
('edu.ualberta.med.biobank.model.Membership','edu.ualberta.med.biobank.model.Membership',2,sysdate()),
('edu.ualberta.med.biobank.model.Permission','edu.ualberta.med.biobank.model.Permission',2,sysdate()),
('edu.ualberta.med.biobank.model.Role','edu.ualberta.med.biobank.model.Role',2,sysdate()),
('edu.ualberta.med.biobank.model.Comment','edu.ualberta.med.biobank.model.Comment',2,sysdate());

-- add the new object into the protection group with id 1 (the one containing all objects protection elements)
insert into csm_pg_pe (protection_group_id, protection_element_id, update_date)
select 1, protection_element_id, sysdate() from csm_protection_element
where protection_element_name = 'edu.ualberta.med.biobank.model.User'
or protection_element_name = 'edu.ualberta.med.biobank.model.Group'
or protection_element_name = 'edu.ualberta.med.biobank.model.Principal'
or protection_element_name = 'edu.ualberta.med.biobank.model.Membership'
or protection_element_name = 'edu.ualberta.med.biobank.model.Permission'
or protection_element_name = 'edu.ualberta.med.biobank.model.Role'
or protection_element_name = 'edu.ualberta.med.biobank.model.Comment';


-- add new security tables

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- *****************************************************************

CREATE TABLE `group_user` (
  `GROUP_ID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  PRIMARY KEY (`GROUP_ID`,`USER_ID`),
  KEY `FK6B1EC1ABB9634A05` (`USER_ID`),
  KEY `FK6B1EC1ABA04C028F` (`GROUP_ID`),
  CONSTRAINT `FK6B1EC1ABA04C028F` FOREIGN KEY (`GROUP_ID`) REFERENCES `principal` (`ID`),
  CONSTRAINT `FK6B1EC1ABB9634A05` FOREIGN KEY (`USER_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `domain` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ALL_CENTERS` bit(1) DEFAULT NULL,
  `ALL_STUDIES` bit(1) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `domain_center` (
  `DOMAIN_ID` int(11) NOT NULL,
  `CENTER_ID` int(11) NOT NULL,
  PRIMARY KEY (`DOMAIN_ID`,`CENTER_ID`),
  KEY `FK8FE45030E3301CA5` (`DOMAIN_ID`),
  KEY `FK8FE4503092FAA705` (`CENTER_ID`),
  CONSTRAINT `FK8FE4503092FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FK8FE45030E3301CA5` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domain` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `domain_study` (
  `DOMAIN_ID` int(11) NOT NULL,
  `CENTER_ID` int(11) NOT NULL,
  PRIMARY KEY (`DOMAIN_ID`,`CENTER_ID`),
  KEY `FK816B9E6EE3301CA5` (`DOMAIN_ID`),
  KEY `FK816B9E6E5BB96C43` (`CENTER_ID`),
  CONSTRAINT `FK816B9E6E5BB96C43` FOREIGN KEY (`CENTER_ID`) REFERENCES `study` (`ID`),
  CONSTRAINT `FK816B9E6EE3301CA5` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domain` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `membership` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `EVERY_PERMISSION` bit(1) DEFAULT NULL,
  `USER_MANAGER` bit(1) DEFAULT NULL,
  `DOMAIN_ID` int(11) NOT NULL,
  `PRINCIPAL_ID` int(11) NOT NULL,
  `STUDY_ID` int(11) DEFAULT NULL,
  `CENTER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `DOMAIN_ID` (`DOMAIN_ID`),
  KEY `FKCD0773D6F2A2464F` (`STUDY_ID`),
  KEY `FKCD0773D6FF154DAF` (`PRINCIPAL_ID`),
  KEY `FKCD0773D6E3301CA5` (`DOMAIN_ID`),
  KEY `FKCD0773D692FAA705` (`CENTER_ID`),
  CONSTRAINT `FKCD0773D692FAA705` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`),
  CONSTRAINT `FKCD0773D6E3301CA5` FOREIGN KEY (`DOMAIN_ID`) REFERENCES `domain` (`ID`),
  CONSTRAINT `FKCD0773D6F2A2464F` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`),
  CONSTRAINT `FKCD0773D6FF154DAF` FOREIGN KEY (`PRINCIPAL_ID`) REFERENCES `principal` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `membership_permission` (
  `ID` int(11) NOT NULL,
  `PERMISSION_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`,`PERMISSION_ID`),
  KEY `FK1350F1D815E6F8DC` (`ID`),
  CONSTRAINT `FK1350F1D815E6F8DC` FOREIGN KEY (`ID`) REFERENCES `membership` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `principal` (
  `DISCRIMINATOR` varchar(31) COLLATE latin1_general_cs NOT NULL,
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `DESCRIPTION` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `CSM_USER_ID` bigint(20) DEFAULT NULL,
  `EMAIL` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `FULL_NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `LOGIN` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `NEED_PWD_CHANGE` bit(1) DEFAULT NULL,
  `RECV_BULK_EMAILS` bit(1) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`),
  UNIQUE KEY `CSM_USER_ID` (`CSM_USER_ID`),
  UNIQUE KEY `EMAIL` (`EMAIL`),
  UNIQUE KEY `LOGIN` (`LOGIN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `membership_role` (
  `MEMBERSHIP_ID` int(11) NOT NULL,
  `ROLE_ID` int(11) NOT NULL,
  PRIMARY KEY (`MEMBERSHIP_ID`,`ROLE_ID`),
  KEY `FKEF36B33F14388625` (`ROLE_ID`),
  KEY `FKEF36B33FD26ABDE5` (`MEMBERSHIP_ID`),
  CONSTRAINT `FKEF36B33FD26ABDE5` FOREIGN KEY (`MEMBERSHIP_ID`) REFERENCES `membership` (`ID`),
  CONSTRAINT `FKEF36B33F14388625` FOREIGN KEY (`ROLE_ID`) REFERENCES `role` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `role` (
  `ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

CREATE TABLE `role_permission` (
  `ID` int(11) NOT NULL,
  `PERMISSION_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`,`PERMISSION_ID`),
  KEY `FK9C6EC938C226FDBC` (`ID`),
  CONSTRAINT `FK9C6EC938C226FDBC` FOREIGN KEY (`ID`) REFERENCES `role` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;


-- *****************************************************************

-- add 'Unknown commenter' which is used in upgrade scripts
insert into principal (id, version, discriminator, login, csm_user_id, recv_bulk_emails, full_name, email, need_pwd_change,activity_status_id)
values (1, 0, 'User', 'Unknown commenter', -1, 0, 'Unknown commenter', '', 0, 1);

-- -----------------------------------------------------------------------
--
-- Global event attributes
--
-- -----------------------------------------------------------------------

ALTER TABLE global_event_attr MODIFY COLUMN LABEL VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE study_event_attr ADD COLUMN GLOBAL_EVENT_ATTR_ID INT(11) NOT NULL COMMENT '', ADD INDEX FK3EACD8EC44556025 (GLOBAL_EVENT_ATTR_ID);

ALTER TABLE study_event_attr
      ADD CONSTRAINT FK3EACD8EC44556025 FOREIGN KEY FK3EACD8EC44556025 (GLOBAL_EVENT_ATTR_ID) REFERENCES global_event_attr (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

UPDATE study_event_attr sea, global_event_attr gea
    SET sea.global_event_attr_id=gea.id
    WHERE sea.label=gea.label;

ALTER TABLE study_event_attr DROP KEY uc_label;
ALTER TABLE study_event_attr DROP FOREIGN KEY FK3EACD8EC5B770B31;
ALTER TABLE study_event_attr DROP INDEX FK3EACD8EC5B770B31;
ALTER TABLE study_event_attr DROP COLUMN LABEL, DROP COLUMN EVENT_ATTR_TYPE_ID;
ALTER TABLE global_event_attr ADD CONSTRAINT LABEL UNIQUE KEY(LABEL);
ALTER TABLE event_attr_type MODIFY COLUMN NAME VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE event_attr_type ADD CONSTRAINT NAME UNIQUE KEY(NAME);

-- -----------------------------------------------------------------------
--
-- Comment field changes
--
-- Needs to run after User and Principal tables are created since
-- it uses the 'Unknown commenter' for comments.
--
-- -----------------------------------------------------------------------

CREATE TABLE comment (
    ID INT(11) NOT NULL auto_increment,
    VERSION INT(11) NOT NULL,
    MESSAGE TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    CREATED_AT    DATETIME NULL DEFAULT NULL,
    USER_ID INT(11) NOT NULL,
    INDEX FK63717A3FB9634A05 (USER_ID),
    PRIMARY KEY (ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE center_comment (
    CENTER_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FKDF3FBC55CDA9FD4F (COMMENT_ID),
    INDEX FKDF3FBC5592FAA705 (CENTER_ID),
    PRIMARY KEY (CENTER_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE collection_event_comment (
    COLLECTION_EVENT_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FK1CFC0199280272F2 (COLLECTION_EVENT_ID),
    INDEX FK1CFC0199CDA9FD4F (COMMENT_ID),
    PRIMARY KEY (COLLECTION_EVENT_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE container_comment (
    CONTAINER_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FK9A6C8C619BFD88CF (CONTAINER_ID),
    INDEX FK9A6C8C61CDA9FD4F (COMMENT_ID),
    PRIMARY KEY (CONTAINER_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE container_type_comment (
    CONTAINER_TYPE_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FK6657C158B3E77A12 (CONTAINER_TYPE_ID),
    INDEX FK6657C158CDA9FD4F (COMMENT_ID),
    PRIMARY KEY (CONTAINER_TYPE_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE dispatch_comment (
    DISPATCH_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FKAFC93B7ACDA9FD4F (COMMENT_ID),
    INDEX FKAFC93B7ADE99CA25 (DISPATCH_ID),
    PRIMARY KEY (DISPATCH_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE dispatch_specimen_comment (
    DISPATCH_SPECIMEN_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FKC3C4FD2DCDA9FD4F (COMMENT_ID),
    INDEX FKC3C4FD2DBCCB06BA (DISPATCH_SPECIMEN_ID),
    PRIMARY KEY (DISPATCH_SPECIMEN_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE origin_info_comment (
    ORIGIN_INFO_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FKFE82842712E55F12 (ORIGIN_INFO_ID),
    INDEX FKFE828427CDA9FD4F (COMMENT_ID),
    PRIMARY KEY (ORIGIN_INFO_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE patient_comment (
    PATIENT_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FK901E2E5B563F38F (PATIENT_ID),
    INDEX FK901E2E5CDA9FD4F (COMMENT_ID),
    PRIMARY KEY (PATIENT_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE processing_event_comment (
    PROCESSING_EVENT_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FKA958114E33126C8 (PROCESSING_EVENT_ID),
    INDEX FKA958114ECDA9FD4F (COMMENT_ID),
    PRIMARY KEY (PROCESSING_EVENT_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE specimen_comment (
    SPECIMEN_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FK73068C08EF199765 (SPECIMEN_ID),
    INDEX FK73068C08CDA9FD4F (COMMENT_ID),
    PRIMARY KEY (SPECIMEN_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE study_comment (
    STUDY_ID INT(11) NOT NULL,
    COMMENT_ID INT(11) NOT NULL,
    CONSTRAINT COMMENT_ID UNIQUE KEY(COMMENT_ID),
    INDEX FKAA027DA9F2A2464F (STUDY_ID),
    INDEX FKAA027DA9CDA9FD4F (COMMENT_ID),
    PRIMARY KEY (STUDY_ID, COMMENT_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

ALTER TABLE center_comment
      ADD CONSTRAINT FKDF3FBC5592FAA705 FOREIGN KEY (CENTER_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKDF3FBC55CDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE collection_event_comment ADD CONSTRAINT FK1CFC0199CDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FK1CFC0199280272F2 FOREIGN KEY (COLLECTION_EVENT_ID) REFERENCES collection_event (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE comment ADD CONSTRAINT FK63717A3FB9634A05 FOREIGN KEY (USER_ID) REFERENCES user (PRINCIPAL_ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE container_comment ADD CONSTRAINT FK9A6C8C61CDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FK9A6C8C619BFD88CF FOREIGN KEY (CONTAINER_ID) REFERENCES container (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE container_type_comment ADD CONSTRAINT FK6657C158CDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FK6657C158B3E77A12 FOREIGN KEY (CONTAINER_TYPE_ID) REFERENCES container_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE dispatch_comment ADD CONSTRAINT FKAFC93B7ADE99CA25 FOREIGN KEY (DISPATCH_ID) REFERENCES dispatch (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FKAFC93B7ACDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE dispatch_specimen_comment ADD CONSTRAINT FKC3C4FD2DBCCB06BA FOREIGN KEY (DISPATCH_SPECIMEN_ID) REFERENCES dispatch_specimen (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FKC3C4FD2DCDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE origin_info_comment ADD CONSTRAINT FKFE828427CDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FKFE82842712E55F12 FOREIGN KEY (ORIGIN_INFO_ID) REFERENCES origin_info (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE patient_comment ADD CONSTRAINT FK901E2E5CDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FK901E2E5B563F38F FOREIGN KEY (PATIENT_ID) REFERENCES patient (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE processing_event_comment ADD CONSTRAINT FKA958114ECDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FKA958114E33126C8 FOREIGN KEY (PROCESSING_EVENT_ID) REFERENCES processing_event (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE specimen_comment ADD CONSTRAINT FK73068C08CDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FK73068C08EF199765 FOREIGN KEY (SPECIMEN_ID) REFERENCES specimen (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE study_comment ADD CONSTRAINT FKAA027DA9CDA9FD4F FOREIGN KEY (COMMENT_ID) REFERENCES comment (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FKAA027DA9F2A2464F FOREIGN KEY (STUDY_ID) REFERENCES study (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- add temp column into comment table to store source id, this id will then be inserted
-- to the corresponding correlation table

ALTER TABLE comment ADD COLUMN SRC_ID INT(11);

-- center comments

insert into comment (version, message, created_at, user_id, src_id)
select 0, comment, convert_tz('1970-01-01 00:00', 'Canada/Mountain', 'GMT'), 1, id
from center where comment is not null and length(comment)>0;

insert into center_comment (center_id, comment_id)
select src_id,id from comment where src_id is not null;

update comment set src_id=null;

-- collection_event comments

insert into comment (version, message, created_at, user_id, src_id)
select 0, comment, '1970-01-01 00:00', 1, id
from collection_event where comment is not null and length(comment)>0;

insert collection_event_comment (collection_event_id, comment_id)
select src_id,id from comment where src_id is not null;

update comment set src_id=null;

-- container comments

insert into comment (version, message, created_at, user_id, src_id)
select 0, comment, '1970-01-01 00:00', 1, id
from container where comment is not null and length(comment)>0;

insert container_comment (container_id, comment_id)
select src_id,id from comment where src_id is not null;

update comment set src_id=null;

-- container_type comments

insert into comment (version, message, created_at, user_id, src_id)
select 0, comment, '1970-01-01 00:00', 1, id
from container_type where comment is not null and length(comment)>0;

insert container_type_comment (container_type_id, comment_id)
select src_id,id from comment where src_id is not null;

update comment set src_id=null;

-- dispatch comments

insert into comment (version, message, created_at, user_id, src_id)
select 0, comment, '1970-01-01 00:00', 1, id
from dispatch where comment is not null and length(comment)>0;

insert dispatch_comment (dispatch_id, comment_id)
select src_id,id from comment where src_id is not null;

update comment set src_id=null;

-- dispatch_specimen comments

insert into comment (version, message, created_at, user_id, src_id)
select 0, comment, '1970-01-01 00:00', 1, id
from dispatch_specimen where comment is not null and length(comment)>0;

insert dispatch_specimen_comment (dispatch_specimen_id, comment_id)
select src_id,id from comment where src_id is not null;

update comment set src_id=null;

-- patient comments -> this column was not in version 3.1.1

-- processing_event comments

insert into comment (version, message, created_at, user_id, src_id)
select 0, comment, '1970-01-01 00:00', 1, id
from processing_event where comment is not null and length(comment)>0;

insert processing_event_comment (processing_event_id, comment_id)
select src_id,id from comment where src_id is not null;

update comment set src_id=null;

-- shipment_info comments

insert into comment (version, message, created_at, user_id, src_id)
select 0, comment, '1970-01-01 00:00', 1, id
from shipment_info where comment is not null and length(comment)>0;

insert origin_info_comment (origin_info_id, comment_id)
select oi.id,comment.id from comment
join shipment_info si on si.id=comment.src_id
join origin_info oi on oi.shipment_info_id=si.id
where comment.src_id is not null;

update comment set src_id=null;

-- specimen comments

insert into comment (version, message, created_at, user_id, src_id)
select 0, comment, '1970-01-01 00:00', 1, id
from specimen where comment is not null and length(comment)>0;

insert specimen_comment (specimen_id, comment_id)
select src_id,id from comment where src_id is not null;

update comment set src_id=null;

-- study comments

insert into comment (version, message, created_at, user_id, src_id)
select 0, comment, '1970-01-01 00:00', 1, id
from study where comment is not null and length(comment)>0;

insert study_comment (study_id, comment_id)
select src_id,id from comment where src_id is not null;

update comment set src_id=null;

-- clean up

ALTER TABLE center DROP COLUMN COMMENT;
ALTER TABLE collection_event DROP COLUMN COMMENT;
ALTER TABLE container DROP COLUMN COMMENT;
ALTER TABLE container_type DROP COLUMN COMMENT;
ALTER TABLE dispatch DROP COLUMN COMMENT;
ALTER TABLE dispatch_specimen DROP COLUMN COMMENT;
ALTER TABLE processing_event DROP COLUMN COMMENT;
ALTER TABLE shipment_info DROP COLUMN COMMENT;
ALTER TABLE specimen DROP COLUMN COMMENT;
ALTER TABLE study DROP COLUMN COMMENT;

ALTER TABLE comment
      MODIFY COLUMN ID INT(11) NOT NULL,
      DROP COLUMN SRC_ID;


-- -----------------------------------------------------------------------
--
-- Other changes
--
-- -----------------------------------------------------------------------

ALTER TABLE collection_event DROP KEY uc_visit_number;

ALTER TABLE container DROP KEY uc_label, DROP KEY uc_productbarcode;

ALTER TABLE container_type DROP KEY uc_name, DROP KEY uc_nameshort;

ALTER TABLE address
      ADD COLUMN NAME VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL COMMENT '';

ALTER TABLE collection_event
      ADD CONSTRAINT uc_ce_visit_number UNIQUE KEY(VISIT_NUMBER, PATIENT_ID);

ALTER TABLE container
      ADD CONSTRAINT uc_c_label UNIQUE KEY(LABEL, CONTAINER_TYPE_ID),
      ADD CONSTRAINT uc_c_productbarcode UNIQUE KEY(PRODUCT_BARCODE, SITE_ID);

ALTER TABLE container_type
      ADD CONSTRAINT uc_ct_nameshort UNIQUE KEY(NAME_SHORT, SITE_ID),
      ADD CONSTRAINT uc_ct_name UNIQUE KEY(NAME, SITE_ID);

ALTER TABLE request DROP FOREIGN KEY FK6C1A7E6FF2A2464F;
ALTER TABLE request DROP INDEX FK6C1A7E6FF2A2464F, DROP COLUMN STUDY_ID;

ALTER TABLE request
      ADD COLUMN RESEARCH_GROUP_ID INT(11) NOT NULL COMMENT '', ADD INDEX FK6C1A7E6F4BD922D8 (RESEARCH_GROUP_ID),
      ADD CONSTRAINT FK6C1A7E6F4BD922D8 FOREIGN KEY FK6C1A7E6F4BD922D8 (RESEARCH_GROUP_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE report
      MODIFY COLUMN DESCRIPTION LONGTEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE report_filter_value
      MODIFY COLUMN VALUE LONGTEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, MODIFY COLUMN SECOND_VALUE LONGTEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;

ALTER TABLE comment MODIFY COLUMN MESSAGE LONGTEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE jasper_template MODIFY COLUMN XML LONGTEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE log MODIFY COLUMN DETAILS LONGTEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE printer_label_template MODIFY COLUMN CONFIG_DATA LONGTEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE study_event_attr MODIFY COLUMN PERMISSIBLE LONGTEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;

--
-- Changes now that we are using annotation on the model objects
--

-- ABS_ID in container_position is temporary and only used for upgrading the data

CREATE TABLE container_position (
    ID INT(11) NOT NULL auto_increment,
    ABS_ID INT(11) NOT NULL,
    VERSION INT(11) NOT NULL,
    COL INT(11) NOT NULL,
    ROW INT(11) NOT NULL,
    PARENT_CONTAINER_ID INT(11) NOT NULL,
    CONSTRAINT PARENT_CONTAINER_ID UNIQUE KEY(PARENT_CONTAINER_ID, `ROW`, COL),
    INDEX FK39FBB477366CE44 (PARENT_CONTAINER_ID),
    PRIMARY KEY (ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE specimen_position (
    ID INT(11) NOT NULL auto_increment,
    VERSION INT(11) NOT NULL,
    COL INT(11) NOT NULL,
    ROW INT(11) NOT NULL,
    POSITION_STRING VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
    CONTAINER_ID INT(11) NOT NULL,
    SPECIMEN_ID INT(11) NOT NULL,
    CONSTRAINT CONTAINER_ID UNIQUE KEY(CONTAINER_ID, `ROW`, COL),
    INDEX FK3E45B080EF199765 (SPECIMEN_ID),
    INDEX FK3E45B0809BFD88CF (CONTAINER_ID),
    CONSTRAINT SPECIMEN_ID UNIQUE KEY(SPECIMEN_ID),
    PRIMARY KEY (ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

ALTER TABLE container_position ADD CONSTRAINT FK39FBB477366CE44 FOREIGN KEY (PARENT_CONTAINER_ID) REFERENCES container (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE specimen_position ADD CONSTRAINT FK3E45B0809BFD88CF FOREIGN KEY (CONTAINER_ID) REFERENCES container (ID) ON UPDATE NO ACTION ON DELETE NO ACTION, ADD CONSTRAINT FK3E45B080EF199765 FOREIGN KEY (SPECIMEN_ID) REFERENCES specimen (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE abstract_position DROP FOREIGN KEY FKBC4AE0A69BFD88CF, DROP FOREIGN KEY FKBC4AE0A67366CE44, DROP FOREIGN KEY FKBC4AE0A6EF199765;

INSERT INTO container_position (ABS_ID, VERSION, `ROW`, COL, PARENT_CONTAINER_ID)
       SELECT ID, 0, `ROW`, COL, PARENT_CONTAINER_ID FROM abstract_position
       WHERE discriminator='ContainerPosition';

UPDATE container ct, container_position ctpos
       SET ct.position_id=ctpos.id
       WHERE ct.position_id=ctpos.abs_id;

INSERT INTO specimen_position (VERSION, `ROW`, COL, POSITION_STRING, CONTAINER_ID, SPECIMEN_ID)
       SELECT 0, `ROW`, COL, POSITION_STRING, CONTAINER_ID, SPECIMEN_ID FROM abstract_position
       WHERE discriminator='SpecimenPosition';

ALTER TABLE container
      DROP FOREIGN KEY FK8D995C61AC528270;

ALTER TABLE container
      ADD CONSTRAINT FK8D995C61AC528270
      FOREIGN KEY FK8D995C61AC528270 (POSITION_ID)
      REFERENCES container_position (ID)
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE container_position
      MODIFY COLUMN ID INT(11) NOT NULL,
      DROP COLUMN ABS_ID;

ALTER TABLE specimen_position
      MODIFY COLUMN ID INT(11) NOT NULL;

DROP TABLE abstract_position;


ALTER TABLE comment DROP FOREIGN KEY FK63717A3FB9634A05;
ALTER TABLE address DROP COLUMN NAME;
ALTER TABLE collection_event DROP KEY uc_ce_visit_number;
ALTER TABLE container DROP KEY uc_c_label, DROP KEY uc_c_productbarcode;
ALTER TABLE container_type DROP KEY uc_ct_nameshort, DROP KEY uc_ct_name;

ALTER TABLE center MODIFY COLUMN DISCRIMINATOR VARCHAR(31) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE container MODIFY COLUMN PATH VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE container_type MODIFY COLUMN NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, MODIFY COLUMN NAME_SHORT VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE printer_label_template MODIFY COLUMN PRINTER_NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE request MODIFY COLUMN CREATED DATETIME NOT NULL;
ALTER TABLE request_specimen MODIFY COLUMN STATE INT(11) NOT NULL;
ALTER TABLE specimen MODIFY COLUMN CURRENT_CENTER_ID INT(11) NOT NULL;
ALTER TABLE specimen_type MODIFY COLUMN NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, MODIFY COLUMN NAME_SHORT VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE study_event_attr MODIFY COLUMN PERMISSIBLE VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE collection_event ADD CONSTRAINT PATIENT_ID UNIQUE KEY(PATIENT_ID, VISIT_NUMBER);
ALTER TABLE container ADD CONSTRAINT SITE_ID_2 UNIQUE KEY(SITE_ID, CONTAINER_TYPE_ID, LABEL), ADD CONSTRAINT SITE_ID UNIQUE KEY(SITE_ID, PRODUCT_BARCODE);
ALTER TABLE container_labeling_scheme ADD CONSTRAINT NAME UNIQUE KEY(NAME);
ALTER TABLE container_type ADD CONSTRAINT SITE_ID_2 UNIQUE KEY(SITE_ID, NAME), ADD CONSTRAINT SITE_ID UNIQUE KEY(SITE_ID, NAME_SHORT);

ALTER TABLE comment ADD CONSTRAINT FK63717A3FB9634A05 FOREIGN KEY FK63717A3FB9634A05 (USER_ID) REFERENCES principal (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

-- upgrade the unknown commenter user
UPDATE principal
       SET discriminator='User', login='Unknown commenter', csm_user_id=0, recv_bulk_emails=0,
       full_name='Unknown commenter', email='', need_pwd_change=0, activity_status_id=@asactive
       WHERE id=1;

ALTER TABLE comment MODIFY COLUMN MESSAGE TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE jasper_template MODIFY COLUMN XML TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE log MODIFY COLUMN DETAILS TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE printer_label_template MODIFY COLUMN CONFIG_DATA TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE report MODIFY COLUMN DESCRIPTION TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE report_filter_value MODIFY COLUMN VALUE TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, MODIFY COLUMN SECOND_VALUE TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL;
ALTER TABLE printed_ss_inv_item ADD CONSTRAINT TXT UNIQUE KEY(TXT);

-- apply new constraints based on annotations

update study_event_attr
set required=0 where required is null;

update source_specimen
set need_original_volume=0 where need_original_volume is null;

-- more constraints

ALTER TABLE address MODIFY COLUMN CITY VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE comment MODIFY COLUMN MESSAGE TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE contact MODIFY COLUMN NAME VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE container_type MODIFY COLUMN NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL, MODIFY COLUMN NAME_SHORT VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE dispatch MODIFY COLUMN STATE INT(11) NOT NULL;
ALTER TABLE dispatch_specimen MODIFY COLUMN STATE INT(11) NOT NULL;
ALTER TABLE jasper_template MODIFY COLUMN NAME VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL, MODIFY COLUMN XML TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE patient MODIFY COLUMN CREATED_AT DATETIME NOT NULL;
ALTER TABLE printed_ss_inv_item MODIFY COLUMN TXT VARCHAR(15) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE printer_label_template MODIFY COLUMN NAME VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE processing_event MODIFY COLUMN WORKSHEET VARCHAR(150) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE report MODIFY COLUMN NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;
ALTER TABLE specimen_type MODIFY COLUMN NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL, MODIFY COLUMN NAME_SHORT VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL;

-- remove activity_status table

ALTER TABLE aliquoted_specimen DROP FOREIGN KEY FK75EACAC1C449A4;
ALTER TABLE center DROP FOREIGN KEY FK7645C055C449A4;
ALTER TABLE collection_event DROP FOREIGN KEY FKEDAD8999C449A4;
ALTER TABLE container DROP FOREIGN KEY FK8D995C61C449A4;
ALTER TABLE container_type DROP FOREIGN KEY FKB2C87858C449A4;
ALTER TABLE processing_event DROP FOREIGN KEY FK327B1E4EC449A4;
ALTER TABLE specimen DROP FOREIGN KEY FKAF84F308C449A4;
ALTER TABLE study DROP FOREIGN KEY FK4B915A9C449A4;
ALTER TABLE study_event_attr DROP FOREIGN KEY FK3EACD8ECC449A4;
DROP TABLE activity_status;
ALTER TABLE aliquoted_specimen DROP INDEX FK75EACAC1C449A4;
ALTER TABLE center DROP INDEX FK7645C055C449A4;
ALTER TABLE collection_event DROP INDEX FKEDAD8999C449A4;
ALTER TABLE container DROP INDEX FK8D995C61C449A4;
ALTER TABLE container_type DROP INDEX FKB2C87858C449A4;
ALTER TABLE processing_event DROP INDEX FK327B1E4EC449A4;
ALTER TABLE specimen DROP INDEX FKAF84F308C449A4;
ALTER TABLE study DROP INDEX FK4B915A9C449A4;
ALTER TABLE study_event_attr DROP INDEX FK3EACD8ECC449A4;

ALTER TABLE aliquoted_specimen MODIFY COLUMN VOLUME DECIMAL(20, 10) NULL DEFAULT NULL;
ALTER TABLE specimen MODIFY COLUMN QUANTITY DECIMAL(20, 10) NULL DEFAULT NULL;

-- merge processing_events that share the same worksheet and created_at time

-- Set all worsheets with 'N/A' or 'n/a' to empty string.
-- These are given the date as the worksheet numbers below
update processing_event set worksheet='' where worksheet='N/A' or worksheet='n/a';


CREATE TABLE `new_processing_event` (
  `ID` int(11) NOT NULL auto_increment,
  `WORKSHEET` varchar(150) COLLATE latin1_general_cs NOT NULL,
  `CREATED_AT` datetime NOT NULL,
  `DATE_CREATED_AT` date NOT NULL,
  `CENTER_ID` int(11) NOT NULL,
  `ACTIVITY_STATUS_ID` int(11) NOT NULL,
  `VERSION` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK327B1E4E92FAA70E` (`CENTER_ID`),
  KEY `CREATED_AT_IDX` (`CREATED_AT`),
  CONSTRAINT `FK327B1E4E92FAA70E` FOREIGN KEY (`CENTER_ID`) REFERENCES `center` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

insert into new_processing_event (worksheet, created_at, date_created_at, center_id, activity_status_id, version)
select worksheet, min(created_at), date(convert_tz(min(created_at),'GMT','US/Mountain')), center_id, activity_status_id, 0
from processing_event
group by worksheet,date(convert_tz(created_at,'GMT','US/Mountain'));

ALTER TABLE processing_event ADD COLUMN NPE_ID int(11) NOT NULL;

update processing_event pe, new_processing_event npe
set pe.npe_id=npe.id
where pe.worksheet=npe.worksheet
and date(convert_tz(pe.created_at,'GMT','US/Mountain'))=npe.date_created_at;

update processing_event_comment pec, processing_event pe
set pec.processing_event_id=pe.npe_id
where pec.processing_event_id=pe.id;

update specimen spc, processing_event pe
set spc.processing_event_id=pe.npe_id
where spc.processing_event_id=pe.id;

ALTER TABLE new_processing_event
      MODIFY COLUMN ID INT(11) NOT NULL,
      DROP COLUMN date_created_at;

update new_processing_event set worksheet=date(created_at) where length(worksheet)=0;

SET FOREIGN_KEY_CHECKS = 0;
drop table processing_event;
rename table new_processing_event to processing_event;
ALTER TABLE processing_event DROP FOREIGN KEY FK327B1E4E92FAA70E;
ALTER TABLE processing_event DROP INDEX FK327B1E4E92FAA70E;

ALTER TABLE processing_event ADD INDEX FK327B1E4E92FAA705 (CENTER_ID);
ALTER TABLE processing_event
      ADD CONSTRAINT FK327B1E4E92FAA705 FOREIGN KEY FK3A16800EC449A4 (CENTER_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
SET FOREIGN_KEY_CHECKS = 1;

-- fix the remaining duplicate worsheet numbers by appending a '-#' to them

CREATE TABLE `tmp_dup_worksheets` (
  `ID` int(11) NOT NULL auto_increment,
  `CREATED_AT` datetime NOT NULL,
  `ORIG_WORKSHEET` varchar(150) COLLATE latin1_general_cs NOT NULL,
  `NEW_WORKSHEET` varchar(150) COLLATE latin1_general_cs NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

insert into tmp_dup_worksheets (orig_worksheet,new_worksheet,created_at)
select pe.worksheet,'',pe.created_at
from processing_event pe
join (select worksheet,count(*) cnt from processing_event group by worksheet having cnt > 1) A
    on A.worksheet=pe.worksheet
order by pe.worksheet,pe.created_at;

set @dwct = null;
set @worksheet = null;

update tmp_dup_worksheets
    set new_worksheet =
        if(@worksheet <> orig_worksheet or @worksheet is null,
            if(@worksheet := orig_worksheet, orig_worksheet, orig_worksheet),
            if(@worksheet = orig_worksheet,
                   if(@dwct := 2, concat(@worksheet, '-', @dwct), concat(@worksheet, '-', @dwct)),
                   if(@dwct := @dwct + 1, concat(@worksheet, '-', @dwct), concat(@worksheet, '-', @dwct))))
       order by orig_worksheet, created_at;

update processing_event pe, tmp_dup_worksheets dup
    set pe.worksheet=dup.new_worksheet
    where pe.worksheet=dup.orig_worksheet
    and pe.created_at=dup.created_at;

drop table tmp_dup_worksheets;

ALTER TABLE processing_event ADD CONSTRAINT WORKSHEET UNIQUE KEY(WORKSHEET);

update report r set r.IS_COUNT=b'0' where IS_COUNT is null;
update report r set r.IS_PUBLIC=b'0' where IS_PUBLIC is null;
alter table report change is_count IS_COUNT bit(1) NOT NULL;
alter table report change is_public IS_PUBLIC bit(1) NOT NULL;

-- *************************************************
-- *
-- * Container and Containter type foreign keys
-- *
-- *************************************************

--
-- this section deals with container_type changes
--

ALTER TABLE container_type
      ADD COLUMN COL_CAPACITY INT(11) NOT NULL COMMENT '',
      ADD COLUMN ROW_CAPACITY INT(11) NOT NULL COMMENT '',
      ADD INDEX ID (ID, SITE_ID);

update container_type ct,capacity cap
       set ct.row_capacity=cap.row_capacity,ct.col_capacity=cap.col_capacity
       where ct.capacity_id=cap.id;

ALTER TABLE container_type DROP FOREIGN KEY FKB2C878581764E225;
DROP TABLE capacity;
ALTER TABLE container_type
      DROP INDEX FKB2C878581764E225,
      DROP KEY CAPACITY_ID, DROP COLUMN CAPACITY_ID;

ALTER TABLE container ADD INDEX ID (ID, CONTAINER_TYPE_ID),
      ADD INDEX FK_Container_containerType (CONTAINER_TYPE_ID, SITE_ID);

ALTER TABLE container
      ADD CONSTRAINT FK_Container_containerType
          FOREIGN KEY FK_Container_containerType (CONTAINER_TYPE_ID, SITE_ID)
          REFERENCES container_type (ID, SITE_ID)
          ON UPDATE NO ACTION ON DELETE NO ACTION;

--
-- this section deals with container_position changes
--

ALTER TABLE container_position
      ADD COLUMN CONTAINER_ID INT(11) NOT NULL COMMENT '',
      ADD COLUMN CONTAINER_TYPE_ID INT(11) NOT NULL COMMENT '',
      ADD COLUMN PARENT_CONTAINER_TYPE_ID INT(11) NOT NULL COMMENT '';

update container_position cpos,container ctr
       set cpos.container_id=ctr.id,cpos.container_type_id=ctr.container_type_id
       where cpos.id=ctr.position_id;

update container_position cpos,container ctr
       set cpos.parent_container_type_id=ctr.container_type_id
       where cpos.parent_container_id=ctr.id;

ALTER TABLE container DROP FOREIGN KEY FK8D995C61AC528270, DROP FOREIGN KEY FK8D995C61B3E77A12;
ALTER TABLE container_position DROP FOREIGN KEY FK39FBB477366CE44;
ALTER TABLE container
      DROP INDEX FK8D995C61AC528270,
      DROP KEY POSITION_ID,
      DROP INDEX FK8D995C61B3E77A12,
      DROP COLUMN POSITION_ID;
ALTER TABLE container_position DROP INDEX FK39FBB477366CE44;
ALTER TABLE container_position
      ADD INDEX FK_ContainerPosition_container (CONTAINER_ID, CONTAINER_TYPE_ID),
      ADD INDEX FK_ContainerPosition_parentContainer (PARENT_CONTAINER_ID, PARENT_CONTAINER_TYPE_ID),
      ADD INDEX FK_ContainerPosition_containerTypeContainerType (PARENT_CONTAINER_TYPE_ID, CONTAINER_TYPE_ID);

ALTER TABLE container_position
      ADD CONSTRAINT FK_ContainerPosition_containerTypeContainerType
          FOREIGN KEY FK_ContainerPosition_containerTypeContainerType (PARENT_CONTAINER_TYPE_ID, CONTAINER_TYPE_ID)
          REFERENCES container_type_container_type (PARENT_CONTAINER_TYPE_ID, CHILD_CONTAINER_TYPE_ID)
          ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE container_position
      ADD CONSTRAINT FK_ContainerPosition_container
          FOREIGN KEY FK_ContainerPosition_container (CONTAINER_ID, CONTAINER_TYPE_ID)
          REFERENCES container (ID, CONTAINER_TYPE_ID)
          ON UPDATE CASCADE ON DELETE NO ACTION;

ALTER TABLE container_position
      ADD CONSTRAINT FK_ContainerPosition_parentContainer
          FOREIGN KEY FK_ContainerPosition_parentContainer (PARENT_CONTAINER_ID, PARENT_CONTAINER_TYPE_ID)
          REFERENCES container (ID, CONTAINER_TYPE_ID)
          ON UPDATE CASCADE ON DELETE NO ACTION;

--
-- this section deals with container_type_container_type changes
--

ALTER TABLE container_type_container_type
      ADD COLUMN SITE_ID INT(11) NOT NULL COMMENT '';

update container_type_container_type ctct, container_type ct
       set ctct.site_id=ct.site_id
       where ctct.parent_container_type_id=ct.id;

ALTER TABLE container_type_container_type
      DROP FOREIGN KEY FK5991B31F371DC9AF,
      DROP FOREIGN KEY FK5991B31F9C2855BD;
ALTER TABLE container_type_container_type
      DROP INDEX FK5991B31F9C2855BD,
      DROP INDEX FK5991B31F371DC9AF;
ALTER TABLE container_type_container_type
      ADD INDEX FK_ContainerType_parentContainerTypes (PARENT_CONTAINER_TYPE_ID, SITE_ID),
      ADD INDEX FK_ContainerType_childContainerTypes (CHILD_CONTAINER_TYPE_ID, SITE_ID);

ALTER TABLE container_type_container_type
      ADD CONSTRAINT FK_ContainerType_childContainerTypes
          FOREIGN KEY FK_ContainerType_childContainerTypes (CHILD_CONTAINER_TYPE_ID, SITE_ID)
          REFERENCES container_type (ID, SITE_ID)
          ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK_ContainerType_parentContainerTypes
          FOREIGN KEY FK_ContainerType_parentContainerTypes (PARENT_CONTAINER_TYPE_ID, SITE_ID)
          REFERENCES container_type (ID, SITE_ID)
          ON UPDATE NO ACTION ON DELETE NO ACTION;

--
-- this section deals with specimen_position changes
--

ALTER TABLE specimen_position
      ADD COLUMN CONTAINER_TYPE_ID INT(11) NOT NULL COMMENT '',
      ADD COLUMN SPECIMEN_TYPE_ID INT(11) NOT NULL COMMENT '';

update specimen_position spos,specimen spc,container ctr
       set spos.container_type_id=ctr.container_type_id,
       spos.specimen_type_id=spc.specimen_type_id
       where spos.container_id=ctr.id
       and  spos.specimen_id=spc.id;

ALTER TABLE specimen_position
      DROP FOREIGN KEY FK3E45B0809BFD88CF,
      DROP FOREIGN KEY FK3E45B080EF199765;
ALTER TABLE specimen_position DROP INDEX FK3E45B080EF199765,
      DROP INDEX FK3E45B0809BFD88CF;
ALTER TABLE specimen ADD INDEX ID (ID, SPECIMEN_TYPE_ID);

ALTER TABLE specimen_position
      ADD INDEX FK_SpecimenPosition_containerTypeSpecimenType (CONTAINER_TYPE_ID, SPECIMEN_TYPE_ID),
      ADD INDEX FK_SpecimenPosition_container (CONTAINER_ID, CONTAINER_TYPE_ID),
      ADD INDEX FK_SpecimenPosition_specimen (SPECIMEN_ID, SPECIMEN_TYPE_ID);

ALTER TABLE specimen_position
      ADD CONSTRAINT FK_SpecimenPosition_containerTypeSpecimenType
          FOREIGN KEY FK_SpecimenPosition_containerTypeSpecimenType (CONTAINER_TYPE_ID, SPECIMEN_TYPE_ID)
          REFERENCES container_type_specimen_type (CONTAINER_TYPE_ID, SPECIMEN_TYPE_ID)
          ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK_SpecimenPosition_container
          FOREIGN KEY FK_SpecimenPosition_container (CONTAINER_ID, CONTAINER_TYPE_ID)
          REFERENCES container (ID, CONTAINER_TYPE_ID)
          ON UPDATE CASCADE ON DELETE NO ACTION,
      ADD CONSTRAINT FK_SpecimenPosition_specimen
          FOREIGN KEY FK_SpecimenPosition_specimen (SPECIMEN_ID, SPECIMEN_TYPE_ID)
          REFERENCES specimen (ID, SPECIMEN_TYPE_ID)
          ON UPDATE CASCADE ON DELETE NO ACTION;

--
-- fix comment searches for advanced reports (resolves #1651)
--
UPDATE entity_property SET property = 'comments.message' where property = 'comment';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

