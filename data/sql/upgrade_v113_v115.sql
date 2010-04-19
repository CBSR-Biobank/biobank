SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE shipping_method (
    ID int(11) NOT NULL,
    NAME varchar(255) NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci,
    PRIMARY KEY (ID)
) DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

INSERT INTO shipping_method (ID, NAME)
SELECT DISTINCT id, name FROM shipping_company;

INSERT INTO shipping_method (ID, NAME)
VALUES (7, 'Drop-off'), (8, 'Pick-up'), (9, 'Inter-hospital');

DROP TABLE shipping_company;

ALTER TABLE clinic
    ADD SENDS_SHIPMENTS bit(1) NULL DEFAULT NULL COMMENT '' AFTER COMMENT;

UPDATE clinic set sends_shipments=1 where name_short='CL1-Foothills';
UPDATE clinic set sends_shipments=1 where name_short='CL1-Heritage';
UPDATE clinic set sends_shipments=1 where name_short='CL1-Sunridge';
UPDATE clinic set sends_shipments=1 where name_short='CL2-Children Hosp';
UPDATE clinic set sends_shipments=0 where name_short='ED1-UofA';
UPDATE clinic set sends_shipments=1 where name_short='FM1-King';
UPDATE clinic set sends_shipments=1 where name_short='GP1-QE Hosp';
UPDATE clinic set sends_shipments=1 where name_short='HL1-QE II';
UPDATE clinic set sends_shipments=1 where name_short='HL2-IWK';
UPDATE clinic set sends_shipments=1 where name_short='HM1-McMaster';
UPDATE clinic set sends_shipments=1 where name_short='KN1-Cancer Ctr';
UPDATE clinic set sends_shipments=1 where name_short='LM1-Lloyd Hosp';
UPDATE clinic set sends_shipments=1 where name_short='LN1-St Joseph';
UPDATE clinic set sends_shipments=1 where name_short='MC1-Moncton Hosp';
UPDATE clinic set sends_shipments=1 where name_short='MN1-Ste-Justine';
UPDATE clinic set sends_shipments=1 where name_short='MN2-Children Hosp';
UPDATE clinic set sends_shipments=1 where name_short='OL1-Hingst';
UPDATE clinic set sends_shipments=1 where name_short='OT1-Ottawa Hosp';
UPDATE clinic set sends_shipments=1 where name_short='OT2-Children Hosp';
UPDATE clinic set sends_shipments=1 where name_short='QB1-Enfant-Jesus';
UPDATE clinic set sends_shipments=1 where name_short='RD1-Red Deer Hosp';
UPDATE clinic set sends_shipments=1 where name_short='SB1-St John NB Hosp';
UPDATE clinic set sends_shipments=1 where name_short='SD1-Sudbury Hosp';
UPDATE clinic set sends_shipments=1 where name_short='SF1-Health NFLD';
UPDATE clinic set sends_shipments=0 where name_short='SP1-St Therese Hosp';
UPDATE clinic set sends_shipments=1 where name_short='SS1-Royal Hosp';
UPDATE clinic set sends_shipments=1 where name_short='TH1-Regional Hosp';
UPDATE clinic set sends_shipments=1 where name_short='TR1-St Mikes';
UPDATE clinic set sends_shipments=1 where name_short='VN1-St Paul';
UPDATE clinic set sends_shipments=1 where name_short='VN2-Childrens Hosp';
UPDATE clinic set sends_shipments=0 where name_short='WL1-Westlock Hosp';
UPDATE clinic set sends_shipments=1 where name_short='WN1-Cancer Care';

ALTER TABLE contact
    ADD MOBILE_NUMBER varchar(50) NULL DEFAULT NULL COMMENT '' AFTER TITLE,
    ADD PAGER_NUMBER varchar(50) NULL DEFAULT NULL COMMENT '' AFTER EMAIL_ADDRESS,
    CHANGE COLUMN PHONE_NUMBER OFFICE_NUMBER varchar(50);

# create temp table
CREATE TABLE tmp_pv_date_drawn (
    PATIENT_VISIT_ID int(11) NOT NULL,
    DATE_DRAWN datetime NULL DEFAULT NULL,
    index(PATIENT_VISIT_ID)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO tmp_pv_date_drawn (PATIENT_VISIT_ID, DATE_DRAWN)
SELECT PATIENT_VISIT_ID, DATE_DRAWN FROM pv_source_vessel;

ALTER TABLE patient_visit
    ADD DATE_DRAWN datetime NULL DEFAULT NULL COMMENT '' AFTER DATE_PROCESSED,
    DROP USERNAME;

UPDATE patient_visit, tmp_pv_date_drawn
SET patient_visit.date_drawn = tmp_pv_date_drawn.date_drawn
WHERE patient_visit.id = tmp_pv_date_drawn.patient_visit_id;

ALTER TABLE pv_source_vessel
    CHANGE COLUMN DATE_DRAWN TIME_DRAWN datetime,
    ADD VOLUME varchar(255) NULL DEFAULT NULL COMMENT '' AFTER TIME_DRAWN;

DROP TABLE tmp_pv_date_drawn;

ALTER TABLE shipment
    CHANGE COLUMN SHIPPING_COMPANY_ID SHIPPING_METHOD_ID int(11),
    DROP INDEX FKFDF619AB02D7532,
    ADD INDEX FKFDF619ADCA49682 (SHIPPING_METHOD_ID);

RENAME TABLE study_source_vessel TO tmp_study_source_vessel;

CREATE TABLE study_source_vessel (
  ID int(11) NOT NULL AUTO_INCREMENT,
  NEED_TIME_DRAWN bit(1) DEFAULT NULL,
  NEED_REAL_VOLUME bit(1) DEFAULT NULL,
  STUDY_ID int(11) NOT NULL,
  SOURCE_VESSEL_ID int(11) NOT NULL,
  PRIMARY KEY (ID),
  KEY FK59583EC467CCA3FC (SOURCE_VESSEL_ID),
  KEY FK59583EC4F2A2464F (STUDY_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO study_source_vessel (study_id, source_vessel_id)
SELECT study_id, study_id FROM tmp_study_source_vessel;

DROP TABLE tmp_study_source_vessel;

INSERT INTO `csm_protection_element` VALUES (35,'edu.ualberta.med.biobank.model.StudySourceVessel','edu.ualberta.med.biobank.model.StudySourceVessel','edu.ualberta.med.biobank.model.StudySourceVessel','','','',2,'2010-04-13');

SET FOREIGN_KEY_CHECKS = 1;
