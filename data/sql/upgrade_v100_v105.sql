#
# MySQLDiff 1.5.0
#
# http://www.mysqldiff.org
# (c) 2001-2004, Lippe-Net Online-Service
#
# Create time: 01.09.2009 09:34
#
# --------------------------------------------------------
# Source info
# Host: localhost
# SQL-File: schema_v1.0.sql
# --------------------------------------------------------
# Target info
# Host: localhost
# SQL-File: schema_v1.05.sql
# --------------------------------------------------------
#

DROP TABLE IF EXISTS `pv_sample_source`;

SET FOREIGN_KEY_CHECKS = 0;

#
# DDL START
#
CREATE TABLE contact (
    ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    NAME varchar(255) NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci,
    TITLE varchar(255) NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci,
    PHONE_NUMBER varchar(255) NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci,
    FAX_NUMBER varchar(255) NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci,
    EMAIL_ADDRESS varchar(255) NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci,
    CLINIC_ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    PRIMARY KEY (ID),
    INDEX FK6382B00057F87A25 (CLINIC_ID)
) DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE pv_sample_source (
    ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    QUANTITY int(11) NULL DEFAULT NULL COMMENT '',
    PATIENT_VISIT_ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    SAMPLE_SOURCE_ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    PRIMARY KEY (ID),
    INDEX FK1E8175D74411AAFA (SAMPLE_SOURCE_ID),
    INDEX FK1E8175D7E5099AFA (PATIENT_VISIT_ID)
) DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE study_contact (
    STUDY_ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    CONTACT_ID int(11) NOT NULL DEFAULT 0 COMMENT '',
    PRIMARY KEY (STUDY_ID, CONTACT_ID),
    INDEX FKAA13B36AF2A2464F (STUDY_ID),
    INDEX FKAA13B36AA07999AF (CONTACT_ID)
) DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

ALTER TABLE abstract_position
    ADD ROW int(11) NULL DEFAULT NULL COMMENT '' AFTER ID,
    ADD COL int(11) NULL DEFAULT NULL COMMENT '' AFTER ROW;

UPDATE abstract_position SET ROW = POSITION_DIMENSION_ONE;
UPDATE abstract_position SET COL = POSITION_DIMENSION_TWO;

ALTER TABLE abstract_position
    DROP POSITION_DIMENSION_ONE,
    DROP POSITION_DIMENSION_TWO;


ALTER TABLE address
    DROP EMAIL,
    DROP PHONE_NUMBER,
    DROP FAX_NUMBER;


ALTER TABLE capacity
    ADD ROW_CAPACITY int(11) NULL DEFAULT NULL COMMENT '' AFTER ID,
    ADD COL_CAPACITY int(11) NULL DEFAULT NULL COMMENT '' AFTER ROW_CAPACITY;

UPDATE capacity SET ROW_CAPACITY = DIMENSION_ONE_CAPACITY;
UPDATE capacity SET COL_CAPACITY = DIMENSION_TWO_CAPACITY;

ALTER TABLE capacity
    DROP DIMENSION_ONE_CAPACITY,
    DROP DIMENSION_TWO_CAPACITY;


ALTER TABLE container
    DROP FULL;


ALTER TABLE container_type
    ADD NAME_SHORT varchar(255) NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci AFTER NAME,
    ADD TOP_LEVEL bit(1) NULL DEFAULT NULL COMMENT '' AFTER NAME_SHORT;


ALTER TABLE patient_visit
    ADD DATE_PROCESSSED datetime NULL DEFAULT NULL COMMENT '' AFTER DATE_DRAWN,
    ADD DATE_RECEIVED datetime NULL DEFAULT NULL COMMENT '' AFTER DATE_PROCESSSED;


ALTER TABLE sample
    ADD LINK_DATE datetime NULL DEFAULT NULL COMMENT '' AFTER INVENTORY_ID,
    ADD QUANTITY_USED double NULL DEFAULT NULL COMMENT '' AFTER QUANTITY,
    DROP PROCESS_DATE,
    DROP AVAILABLE;


DROP TABLE study_clinic;

ALTER TABLE study_sample_source
    DROP ID,
    DROP QUANTITY,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (STUDY_ID, SAMPLE_SOURCE_ID);

#
# PV INFO CHANGES
#

DELETE FROM study_pv_info WHERE pv_info_id IN (SELECT pv_info.id FROM pv_info
JOIN pv_info_possible ON pv_info.pv_info_possible_id=pv_info_possible.id
WHERE pv_info_possible.id IN (3,4,5,6,11));

DELETE FROM pv_info WHERE pv_info_possible_id IN (3,4,5,6,11);

DELETE FROM pv_info_possible WHERE id in (3,4,5,6,11);

UPDATE pv_info SET pv_info_possible_id=3 where pv_info_possible_id=7;
UPDATE pv_info SET pv_info_possible_id=4 where pv_info_possible_id=8;
UPDATE pv_info SET pv_info_possible_id=5 where pv_info_possible_id=9;
UPDATE pv_info SET pv_info_possible_id=6 where pv_info_possible_id=10;

#
# DDL END
#

SET FOREIGN_KEY_CHECKS = 1;

