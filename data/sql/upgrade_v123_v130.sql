SET FOREIGN_KEY_CHECKS = 0;

#
# DDL START
#

DROP TABLE IF EXISTS  site_pv_attr;

DROP TABLE IF EXISTS  global_pv_attr;

CREATE TABLE `global_pv_attr` (
  `ID` int(11) NOT NULL,
  `LABEL` varchar(50) DEFAULT NULL,
  `PV_ATTR_TYPE_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FKBEF71B922496A267` (`PV_ATTR_TYPE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `global_pv_attr` (ID, LABEL, PV_ATTR_TYPE_ID) VALUES
(1,"PBMC Count (x10^6)",1),
(2,"Worksheet",2),
(3,"Consent",5),
(4,"Phlebotomist",2),
(5,"Visit Type",4),
(6,"Biopsy Length",1);

ALTER TABLE study_pv_attr
      ADD `REQUIRED` bit(1) NULL DEFAULT NULL COMMENT '' AFTER PERMISSIBLE;

DROP TABLE IF EXISTS  site_study;

CREATE TABLE site_study (
    STUDY_ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    SITE_ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    PRIMARY KEY (SITE_ID, STUDY_ID),
    INDEX FK7A197EB1F2A2464F (STUDY_ID),
    INDEX FK7A197EB13F52C885 (SITE_ID)
);

INSERT INTO site_study (STUDY_ID, SITE_ID)
SELECT DISTINCT id, site_id FROM study;

ALTER TABLE activity_status
    MODIFY NAME varchar(50) NULL DEFAULT NULL COMMENT '';
#
#  Fieldformat of
#    activity_status.NAME changed from varchar(255) NULL DEFAULT NULL COMMENT '' to varchar(50) NULL DEFAULT NULL COMMENT ''.
#  Possibly data modifications needed!
#

ALTER TABLE aliquot
    ADD INDEX FKF4502987C449A4 (ACTIVITY_STATUS_ID);


ALTER TABLE contact
    MODIFY NAME varchar(100) NULL DEFAULT NULL COMMENT '';
#
#  Fieldformat of
#    contact.NAME changed from varchar(255) NULL DEFAULT NULL COMMENT '' to varchar(100) NULL DEFAULT NULL COMMENT ''.
#  Possibly data modifications needed!
#

ALTER TABLE container
    MODIFY LABEL varchar(255) NULL DEFAULT NULL COMMENT '';
#
#  Fieldformat of
#    container.LABEL changed from varchar(50) NULL DEFAULT NULL COMMENT '' to varchar(255) NULL DEFAULT NULL COMMENT ''.
#  Possibly data modifications needed!
#

ALTER TABLE container_labeling_scheme
    MODIFY NAME varchar(50) NULL DEFAULT NULL COMMENT '';
#
#  Fieldformat of
#    container_labeling_scheme.NAME changed from varchar(255) NULL DEFAULT NULL COMMENT '' to varchar(50) NULL DEFAULT NULL COMMENT ''.
#  Possibly data modifications needed!
#

DROP TABLE log_message;

DROP TABLE object_attribute;

DROP TABLE objectattributes;

ALTER TABLE pv_attr
    MODIFY VALUE varchar(255) NULL DEFAULT NULL COMMENT '';
#
#  Fieldformat of
#    pv_attr.VALUE changed from text NULL DEFAULT NULL COMMENT '' to varchar(255) NULL DEFAULT NULL COMMENT ''.
#  Possibly data modifications needed!
#

ALTER TABLE pv_attr_type
    MODIFY NAME varchar(50) NULL DEFAULT NULL COMMENT '';
#
#  Fieldformat of
#    pv_attr_type.NAME changed from varchar(255) NULL DEFAULT NULL COMMENT '' to varchar(50) NULL DEFAULT NULL COMMENT ''.
#  Possibly data modifications needed!
#

ALTER TABLE sample_type
    MODIFY NAME varchar(100) NULL DEFAULT NULL COMMENT '';
#
#  Fieldformat of
#    sample_type.NAME changed from varchar(255) NULL DEFAULT NULL COMMENT '' to varchar(100) NULL DEFAULT NULL COMMENT ''.
#  Possibly data modifications needed!
#

ALTER TABLE shipment
    ADD SITE_ID int(11) NOT NULL DEFAULT 0 COMMENT '' AFTER BOX_NUMBER,
    ADD INDEX FKFDF619A3F52C885 (SITE_ID);

UPDATE shipment set SITE_ID=(SELECT id FROM site WHERE NAME_SHORT='CBSR');


ALTER TABLE source_vessel
    MODIFY NAME varchar(100) NULL DEFAULT NULL COMMENT '';
#
#  Fieldformat of
#    source_vessel.NAME changed from varchar(255) NULL DEFAULT NULL COMMENT '' to varchar(100) NULL DEFAULT NULL COMMENT ''.
#  Possibly data modifications needed!
#

ALTER TABLE study
    DROP SITE_ID,
    DROP INDEX FK4B915A93F52C885;


ALTER TABLE study_source_vessel
    MODIFY ID int(11) NOT NULL DEFAULT 0 COMMENT '';
#
#  Fieldformat of
#    study_source_vessel.ID changed from int(11) NOT NULL DEFAULT 0 COMMENT '' auto_increment to int(11) NOT NULL DEFAULT 0 COMMENT ''.
#  Possibly data modifications needed!
#

ALTER TABLE container_labeling_scheme
      ADD `MAX_ROWS` int(11) NULL DEFAULT NULL COMMENT '' AFTER NAME,
      ADD `MAX_COLS` int(11) NULL DEFAULT NULL COMMENT '' AFTER MAX_ROWS,
      ADD `MAX_CAPACITY` int(11) NULL DEFAULT NULL COMMENT '' AFTER MAX_COLS;

UPDATE container_labeling_scheme set max_rows=16,max_cols=24,max_capacity=384 where id=1;
UPDATE container_labeling_scheme set max_capacity=576 where id=2;
UPDATE container_labeling_scheme set max_capacity=99 where id=3;
UPDATE container_labeling_scheme set max_rows=2,max_cols=2,max_capacity=4 where id=4;

#
# DDL END
#

SET FOREIGN_KEY_CHECKS = 1;

