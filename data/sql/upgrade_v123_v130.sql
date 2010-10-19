# use following command to run this script:
#    mysql --safe-updates=0 -uuser -ppwd biobank2

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
  KEY FKEDC41FEE2496A267 (PV_ATTR_TYPE_ID)
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

INSERT INTO `ACTIVITY_STATUS` (ID, NAME) VALUES
( 5, "Dispatched");

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
      MODIFY COLUMN LABEL VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL;

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
    MODIFY NAME varchar(100) NULL DEFAULT NULL COMMENT '',
    DROP SITE_ID;

#
#  Fieldformat of
#    sample_type.NAME changed from varchar(255) NULL DEFAULT NULL COMMENT '' to varchar(100) NULL DEFAULT NULL COMMENT ''.
#  Possibly data modifications needed!
#

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
      ADD `MIN_CHARS` int(11) NULL DEFAULT NULL COMMENT '' AFTER NAME,
      ADD `MAX_CHARS` int(11) NULL DEFAULT NULL COMMENT '' AFTER MIN_CHARS,
      ADD `MAX_ROWS` int(11) NULL DEFAULT NULL COMMENT '' AFTER MAX_CHARS,
      ADD `MAX_COLS` int(11) NULL DEFAULT NULL COMMENT '' AFTER MAX_ROWS,
      ADD `MAX_CAPACITY` int(11) NULL DEFAULT NULL COMMENT '' AFTER MAX_COLS;

UPDATE container_labeling_scheme set min_chars=2,max_chars=3,max_rows=16,max_cols=24,max_capacity=384 where id=1;
UPDATE container_labeling_scheme set min_chars=2,max_chars=2,max_capacity=576 where id=2;
UPDATE container_labeling_scheme set min_chars=2,max_chars=2,max_capacity=99 where id=3;
UPDATE container_labeling_scheme set min_chars=2,max_chars=2,max_rows=2,max_cols=2,max_capacity=4 where id=4;
INSERT INTO container_labeling_scheme values (5,"CBSR SBS",2,2,9,9,81);

UPDATE container_type set child_labeling_scheme_id=5 where name="Box 81";

DROP TABLE IF EXISTS  abstract_shipment;

CREATE TABLE abstract_shipment (
    ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    DISCRIMINATOR varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
    DATE_RECEIVED datetime NULL DEFAULT NULL COMMENT '',
    COMMENT text NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci,
    WAYBILL varchar(255) NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci,
    DATE_SHIPPED datetime NULL DEFAULT NULL COMMENT '',
    BOX_NUMBER varchar(255) NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci,
    STATE int(11) NULL DEFAULT NULL COMMENT '',
    SHIPPING_METHOD_ID INT(11) NULL DEFAULT NULL,
    ACTIVITY_STATUS_ID INT(11) NULL DEFAULT NULL,
    SITE_ID int(11) NULL DEFAULT NULL COMMENT '',
    STUDY_ID INT(11) NULL DEFAULT NULL COMMENT '',
    CLINIC_ID int(11) NULL DEFAULT NULL COMMENT '',
    DISPATCH_RECEIVER_ID int(11) NULL DEFAULT NULL COMMENT '',
    DISPATCH_SENDER_ID int(11) NULL DEFAULT NULL COMMENT '',
    PRIMARY KEY (ID),
    INDEX FK70F1B917FB659898 (DISPATCH_RECEIVER_ID),
    INDEX FK70F1B917DCA49682 (SHIPPING_METHOD_ID),
    INDEX FK70F1B917D84ED52 (DISPATCH_SENDER_ID),
    INDEX FK70F1B917F2A2464F (STUDY_ID),
    INDEX FK70F1B91757F87A25 (CLINIC_ID),
    INDEX FK70F1B917C449A4 (ACTIVITY_STATUS_ID),
    INDEX FK70F1B9173F52C885 (SITE_ID),
    INDEX DATE_RECV_IDX (DATE_RECEIVED),
    INDEX WAYBILL_IDX (WAYBILL)
) DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

INSERT INTO abstract_shipment (ID, DISCRIMINATOR, DATE_RECEIVED, COMMENT, WAYBILL, DATE_SHIPPED,
BOX_NUMBER, SHIPPING_METHOD_ID, CLINIC_ID, SITE_ID, ACTIVITY_STATUS_ID)
SELECT ID, 'ClinicShipment', DATE_RECEIVED, COMMENT, WAYBILL, DATE_SHIPPED,
BOX_NUMBER, SHIPPING_METHOD_ID, CLINIC_ID,
(SELECT clinic.SITE_ID FROM clinic WHERE clinic.id=shipment.clinic_id), 1
FROM shipment;

DROP TABLE shipment;

DROP TABLE IF EXISTS  clinic_shipment_patient;

CREATE TABLE clinic_shipment_patient (
    ID int(11) NOT NULL AUTO_INCREMENT,
    PATIENT_ID int(11) NOT NULL,
    CLINIC_SHIPMENT_ID int(11) NOT NULL,
    PRIMARY KEY (ID),
    INDEX FKF4B18BB7E5B2B216 (CLINIC_SHIPMENT_ID),
    INDEX FKF4B18BB7B563F38F (PATIENT_ID)
) DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

INSERT INTO clinic_shipment_patient (PATIENT_ID, CLINIC_SHIPMENT_ID)
SELECT PATIENT_ID, SHIPMENT_ID FROM shipment_patient;

ALTER TABLE clinic_shipment_patient
      MODIFY COLUMN ID int(11) NOT NULL DEFAULT 0 COMMENT '';

ALTER TABLE patient_visit
    CHANGE COLUMN SHIPMENT_ID CLINIC_SHIPMENT_PATIENT_ID int(11) NOT NULL AFTER PATIENT_ID,
    DROP INDEX FKA09CAF51B1D3625,
    ADD INDEX FKA09CAF5183AE7BBB (CLINIC_SHIPMENT_PATIENT_ID);

UPDATE patient_visit
    SET clinic_shipment_patient_id=(
        SELECT id FROM clinic_shipment_patient
        WHERE clinic_shipment_patient.patient_id=patient_visit.patient_id
        AND clinic_shipment_id=clinic_shipment_patient_id);

ALTER TABLE patient_visit
    DROP COLUMN PATIENT_ID,
    DROP INDEX FKA09CAF51B563F38F;

DROP TABLE shipment_patient;

DROP TABLE IF EXISTS  dispatch_info;

CREATE TABLE dispatch_info (
    ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    STUDY_ID int(11) NOT NULL,
    SRC_SITE_ID int(11) NOT NULL,
    PRIMARY KEY (ID),
    INDEX FK3D4D9D53CDCA092A (SRC_SITE_ID),
    INDEX FK3D4D9D53F2A2464F (STUDY_ID)
) DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

DROP TABLE IF EXISTS  dispatch_info_site;

CREATE TABLE dispatch_info_site (
    SITE_ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    DISPATCH_INFO_ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    PRIMARY KEY (DISPATCH_INFO_ID, SITE_ID),
    INDEX FK86B04EB33F52C885 (SITE_ID),
    INDEX FK86B04EB3A927DCFA (DISPATCH_INFO_ID)
) DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

DROP TABLE IF EXISTS dispatch_shipment_aliquot;

CREATE TABLE dispatch_shipment_aliquot (
	ID integer not null,
	STATE integer,
	COMMENT text,
	ALIQUOT_ID integer not null,
	DISPATCH_SHIPMENT_ID integer not null,
	PRIMARY KEY (ID),
        INDEX FKB1B76907D8CEA57A (DISPATCH_SHIPMENT_ID),
        INDEX FKB1B76907898584F (ALIQUOT_ID)
) DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

ALTER TABLE clinic
    DROP COLUMN SITE_ID,
    DROP INDEX FK76A608E83F52C885;

ALTER TABLE container_path
    DROP INDEX PATH_IDC,
    ADD INDEX PATH_IDX (PATH);

ALTER TABLE abstract_position
      ADD COLUMN DISCRIMINATOR VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '' AFTER ID,
      ADD COLUMN PARENT_CONTAINER_ID INT(11) NULL DEFAULT NULL COMMENT '',
      ADD COLUMN CONTAINER_ID INT(11) NULL DEFAULT NULL COMMENT '',
      ADD COLUMN ALIQUOT_ID INT(11) NULL DEFAULT NULL COMMENT '',
      ADD INDEX FKBC4AE0A6898584F (ALIQUOT_ID),
      ADD INDEX FKBC4AE0A69BFD88CF (CONTAINER_ID),
      ADD CONSTRAINT ALIQUOT_ID UNIQUE KEY(ALIQUOT_ID),
      ADD INDEX FKBC4AE0A67366CE44 (PARENT_CONTAINER_ID);

UPDATE abstract_position,aliquot_position
       SET abstract_position.aliquot_id=aliquot_position.aliquot_id,
       abstract_position.container_id=aliquot_position.container_id,
       abstract_position.discriminator='AliquotPosition'
       WHERE abstract_position.id=aliquot_position.abstract_position_id;

UPDATE abstract_position,container_position
       SET abstract_position.parent_container_id=container_position.parent_container_id,
       abstract_position.discriminator='ContainerPosition'
       WHERE abstract_position.id = container_position.abstract_position_id;

DROP TABLE aliquot_position;

DROP TABLE container_position;

/* rename the HEART study assoc with Calgary-F to "HEART2" */
UPDATE study,site_study,site SET study.name_short="HEART2"
WHERE study.id=site_study.study_id AND site.id=site_study.site_id
AND site.name_short="Calgary-F";

/* move contacts from HEART2 to HEART */
UPDATE study_contact,contact,clinic,study SET study_contact.study_id=(SELECT id FROM study WHERE name_short="HEART")
WHERE study_contact.study_id=study.id AND study_contact.contact_id=contact.id
AND contact.clinic_id=clinic.id AND study.name_short="HEART2"
AND clinic.name_short="CL1-Foothills TRW";

/* move all patients from HEART2 to HEART */
UPDATE patient,study SET patient.study_id=(SELECT id FROM study WHERE name_short="HEART")
WHERE study.id=patient.study_id AND study.name_short="HEART2";

/* delete assoc between HEART2 and Calgary-F */
DELETE site_study FROM site_study INNER JOIN site ON site.id=site_study.site_id
INNER JOIN study ON study.id=site_study.study_id
WHERE site.name_short='Calgary-F' AND study.name_short='HEART2';

/* remove HEART2 */
DELETE study FROM STUDY LEFT JOIN site_study on site_study.study_id=study.id
WHERE name_short='HEART2' and study_id is null;

/* create link between Calgary-F site and original entry for HEART */
INSERT INTO site_study (site_id, study_id)
       SELECT site.id,study.id
       FROM site JOIN study
       WHERE site.name_short='Calgary-F' AND study.name_short='HEART';

/* fix upper case - lower case problems */
ALTER TABLE abstract_position
      MODIFY COLUMN DISCRIMINATOR VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL, COLLATE=latin1_general_cs;
ALTER TABLE abstract_shipment
      MODIFY COLUMN DISCRIMINATOR VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
      MODIFY COLUMN COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN WAYBILL VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN BOX_NUMBER VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      COLLATE=latin1_general_cs;
ALTER TABLE activity_status MODIFY COLUMN NAME VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE address MODIFY COLUMN STREET1 VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN STREET2 VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN CITY VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN PROVINCE VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN POSTAL_CODE VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE aliquot MODIFY COLUMN INVENTORY_ID VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE capacity COLLATE=latin1_general_cs;
ALTER TABLE clinic MODIFY COLUMN NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN NAME_SHORT VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE clinic_shipment_patient COLLATE=latin1_general_cs;
ALTER TABLE contact MODIFY COLUMN NAME VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN TITLE VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN MOBILE_NUMBER VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN OFFICE_NUMBER VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN FAX_NUMBER VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN EMAIL_ADDRESS VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN PAGER_NUMBER VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE container MODIFY COLUMN LABEL VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN PRODUCT_BARCODE VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE container_labeling_scheme MODIFY COLUMN NAME VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE container_path MODIFY COLUMN PATH VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE container_type MODIFY COLUMN NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN NAME_SHORT VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE container_type_container_type COLLATE=latin1_general_cs;
ALTER TABLE container_type_sample_type COLLATE=latin1_general_cs;
ALTER TABLE dispatch_info COLLATE=latin1_general_cs;
ALTER TABLE dispatch_info_site COLLATE=latin1_general_cs;
ALTER TABLE global_pv_attr MODIFY COLUMN LABEL VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE patient MODIFY COLUMN PNUMBER VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE patient_visit MODIFY COLUMN COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE pv_attr MODIFY COLUMN VALUE VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE pv_attr_type MODIFY COLUMN NAME VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE pv_source_vessel MODIFY COLUMN VOLUME VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE sample_storage COLLATE=latin1_general_cs;
ALTER TABLE sample_type MODIFY COLUMN NAME VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN NAME_SHORT VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE shipping_method MODIFY COLUMN NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE site MODIFY COLUMN NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN NAME_SHORT VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE source_vessel MODIFY COLUMN NAME VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE study MODIFY COLUMN NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN NAME_SHORT VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE study_contact COLLATE=latin1_general_cs;
ALTER TABLE study_pv_attr MODIFY COLUMN LABEL VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
      MODIFY COLUMN PERMISSIBLE TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL, COLLATE=latin1_general_cs;
ALTER TABLE study_source_vessel COLLATE=latin1_general_cs;

#
# DDL END
#

SET FOREIGN_KEY_CHECKS = 1;

