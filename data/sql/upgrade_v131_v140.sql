/*------------------------------------------------------------------------------
 *
 *  BioBank2 MySQL upgrade script for model version 1.3.1 to 1.4.0
 *
 *----------------------------------------------------------------------------*/


/*****************************************************
 *  EVENT ATTRIBUTES
 ****************************************************/

RENAME TABLE global_pv_attr TO global_event_attr;
RENAME TABLE study_pv_attr TO study_event_attr;
RENAME TABLE pv_attr TO event_attr;
RENAME TABLE pv_attr_type TO event_attr_type;

ALTER TABLE global_event_attr
      CHANGE COLUMN PV_ATTR_TYPE_ID EVENT_ATTR_TYPE_ID INT(11) NOT NULL,
      DROP INDEX FKEDC41FEE2496A267,
      ADD INDEX FKBE7ED6B25B770B31 (EVENT_ATTR_TYPE_ID);

ALTER TABLE study_event_attr
      CHANGE COLUMN PV_ATTR_TYPE_ID EVENT_ATTR_TYPE_ID INT(11) NOT NULL,
      ADD CONSTRAINT uc_study_event_attr_label UNIQUE (label,study_id),
      DROP INDEX FK669DD7F4F2A2464F,
      DROP INDEX FK669DD7F42496A267,
      DROP INDEX FK669DD7F4C449A4,
      ADD INDEX FK3EACD8ECF2A2464F (STUDY_ID),
      ADD INDEX FK3EACD8ECC449A4 (ACTIVITY_STATUS_ID),
      ADD INDEX FK3EACD8EC5B770B31 (EVENT_ATTR_TYPE_ID);

ALTER TABLE event_attr
      CHANGE COLUMN STUDY_PV_ATTR_ID STUDY_EVENT_ATTR_ID INT(11) NOT NULL,
      CHANGE COLUMN PATIENT_VISIT_ID COLLECTION_EVENT_ID INT(11) NOT NULL COMMENT '',
      DROP INDEX FK200CD48ABFED96DB,
      DROP INDEX FK200CD48AE5099AFA,
      ADD INDEX FK59508C96280272F2 (COLLECTION_EVENT_ID),
      ADD INDEX FK59508C96A9CFCFDB (STUDY_EVENT_ATTR_ID);


/*****************************************************
 * specimen
 ****************************************************/

CREATE TABLE specimen_type (
  ID int(11) NOT NULL auto_increment,
  NAME varchar(100) NOT NULL,
  NAME_SHORT varchar(50) NOT NULL,
  PRIMARY KEY (ID),
  UNIQUE KEY NAME (NAME),
  UNIQUE KEY NAME_SHORT (NAME_SHORT)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

INSERT INTO specimen_type (name,name_short)
       SELECT name,name_short FROM sample_type;

INSERT INTO specimen_type (name,name_short)
       SELECT name,name FROM source_vessel WHERE name!='N/A' && name!='Meconium';

INSERT INTO specimen_type (name,name_short) VALUES
	('Meconium','CHILD Meconium');

ALTER TABLE specimen_type MODIFY COLUMN ID INT(11) NOT NULL;

CREATE TABLE specimen (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    INVENTORY_ID VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
    COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    QUANTITY DOUBLE NULL DEFAULT NULL,
    CREATED_AT DATETIME NULL DEFAULT NULL,
    CENTER_ID INT(11) NULL DEFAULT NULL,
    SPECIMEN_TYPE_ID INT(11) NOT NULL,
    ORIGIN_INFO_ID INT(11) NULL DEFAULT NULL,
    PROCESSING_EVENT_ID INT(11) NULL DEFAULT NULL,
    COLLECTION_EVENT_ID INT(11) NOT NULL,
    ACTIVITY_STATUS_ID INT(11) NOT NULL,
    PV_ID INT(11) NOT NULL,
    SV_ID INT(11) NOT NULL,
    INDEX FKAF84F30838445996 (SPECIMEN_TYPE_ID),
    INDEX FKAF84F308280272F2 (COLLECTION_EVENT_ID),
    INDEX FKAF84F308C449A4 (ACTIVITY_STATUS_ID),
    INDEX FKAF84F30812E55F12 (ORIGIN_INFO_ID),
    CONSTRAINT INVENTORY_ID UNIQUE KEY(INVENTORY_ID),
    INDEX FKAF84F30892FAA705 (CENTER_ID),
    INDEX FKAF84F30833126C8 (PROCESSING_EVENT_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO specimen (inventory_id,comment,quantity,created_at,center_id,specimen_type_id,
activity_status_id,pv_id)
SELECT inventory_id,comment,quantity,created_at,center_id,specimen_type_id,
activity_status_id,id FROM aliquot;

ALTER TABLE specimen MODIFY COLUMN ID INT(11) NOT NULL;

/*****************************************************
 * Merge clinics and sites into centers
 ****************************************************/

CREATE TABLE center (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    DISCRIMINATOR VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
    NAME_SHORT VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
    COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    ADDRESS_ID INT(11) NOT NULL,
    ACTIVITY_STATUS_ID INT(11) NOT NULL,
    STUDY_ID INT(11) NULL DEFAULT NULL,
    SENDS_SHIPMENTS TINYINT(1) NULL DEFAULT NULL,
    CONSTRAINT STUDY_ID UNIQUE KEY(STUDY_ID),
    INDEX FK7645C055C449A4 (ACTIVITY_STATUS_ID),
    INDEX FK7645C055F2A2464F (STUDY_ID),
    CONSTRAINT NAME UNIQUE KEY(NAME),
    CONSTRAINT NAME_SHORT UNIQUE KEY(NAME_SHORT),
    INDEX FK7645C0556AF2992F (ADDRESS_ID),
    CONSTRAINT ADDRESS_ID UNIQUE KEY(ADDRESS_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO center (discriminator,name,name_short,comment,address_id,activity_status_id,sends_shipments)
SELECT 'Clinic',name,name_short,comment,address_id,activity_status_id,sends_shipments FROM clinic;

INSERT INTO center (discriminator,name,name_short,comment,address_id,activity_status_id)
SELECT 'Site',name,name_short,comment,address_id,activity_status_id FROM site;

-- update site-study correlation table
-- create center_id column which will alter be renamed to site_id

ALTER TABLE site_study
      ADD COLUMN CENTER_ID int(11) COMMENT '' NOT NULL;

UPDATE site_study,center
       SET center_id=(SELECT center.id FROM center
       WHERE name=(SELECT name FROM site where id=site_study.site_id));

ALTER TABLE site_study
      DROP PRIMARY KEY,
      DROP INDEX FK7A197EB13F52C885,
      DROP COLUMN site_id,
      CHANGE COLUMN center_id site_id int(11) NOT NULL,
      ADD INDEX FK7A197EB13F52C885 (SITE_ID),
      ADD PRIMARY KEY (`SITE_ID`,`STUDY_ID`);

ALTER TABLE center MODIFY COLUMN ID INT(11) NOT NULL;

/*****************************************************
 * shipments and disptaches
 ****************************************************/

-- absship_id is temporary

CREATE TABLE shipment_info (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    RECEIVED_AT DATETIME NULL DEFAULT NULL,
    SENT_AT DATETIME NULL DEFAULT NULL,
    WAYBILL VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    BOX_NUMBER VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    SHIPPING_METHOD_ID INT(11) NOT NULL,
    ABSSHIP_ID INT(11) NOT NULL,
    INDEX FK95BCA433DCA49682 (SHIPPING_METHOD_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO shipment_info (absship_id,received_at,sent_at,waybill,box_number,shipping_method_id)
SELECT id,date_received,date_shipped,waybill,box_number,shipping_method_id FROM abstract_shipment
WHERE discriminator='ClinicShipment';

CREATE TABLE origin_info (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    SHIPMENT_INFO_ID INT(11) NULL DEFAULT NULL,
    CENTER_ID INT(11) NOT NULL,
    CONSTRAINT SHIPMENT_INFO_ID UNIQUE KEY(SHIPMENT_INFO_ID),
    INDEX FKE92E7A2792FAA705 (CENTER_ID),
    INDEX FKE92E7A27F59D873A (SHIPMENT_INFO_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO origin_info (center_id,shipment_info_id)
SELECT center.id,shipment_info.id FROM abstract_shipment
       JOIN clinic ON clinic.id=abstract_shipment.clinic_id
       JOIN center ON center.name=clinic.name
       join shipment_info on shipment_info.absship_id=abstract_shipment.id
       WHERE abstract_shipment.discriminator='ClinicShipment';

ALTER TABLE origin_info MODIFY COLUMN ID INT(11) NOT NULL;

CREATE TABLE dispatch (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    STATE INT(11) NULL DEFAULT NULL,
    COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    DEPARTED_AT DATETIME NULL DEFAULT NULL,
    ACTIVITY_STATUS_ID INT(11) NOT NULL,
    RECEIVER_CENTER_ID INT(11) NULL DEFAULT NULL,
    SHIPMENT_INFO_ID INT(11) NOT NULL,
    SENDER_CENTER_ID INT(11) NULL DEFAULT NULL,
    REQUEST_ID INT(11) NULL DEFAULT NULL,
    INDEX FK3F9F347AC449A4 (ACTIVITY_STATUS_ID),
    INDEX FK3F9F347A91BC3D7B (SENDER_CENTER_ID),
    INDEX FK3F9F347AA2F14F4F (REQUEST_ID),
    INDEX FK3F9F347A307B2CB5 (RECEIVER_CENTER_ID),
    CONSTRAINT SHIPMENT_INFO_ID UNIQUE KEY(SHIPMENT_INFO_ID),
    INDEX FK3F9F347AF59D873A (SHIPMENT_INFO_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO shipment_info (absship_id,received_at,sent_at,waybill,box_number,shipping_method_id)
SELECT id,date_received,date_shipped,waybill,box_number,shipping_method_id FROM abstract_shipment
WHERE discriminator='DispatchShipment';

INSERT INTO dispatch (sender_center_id,receiver_center_id,state,comment,activity_status_id,
shipment_info_id)
SELECT sender_center.id,receiver_center.id,state,abstract_shipment.comment,
abstract_shipment.activity_status_id,shipment_info.id
	FROM abstract_shipment
        JOIN site as sender_site on sender_site.id=abstract_shipment.dispatch_sender_id
        JOIN center as sender_center on sender_center.name=sender_site.name
        JOIN site as receiver_site on receiver_site.id=abstract_shipment.dispatch_receiver_id
        JOIN center as receiver_center on receiver_center.name=receiver_site.name
	JOIN shipment_info on shipment_info.absship_id=abstract_shipment.id
        WHERE abstract_shipment.discriminator='DispatchShipment';

ALTER TABLE dispatch MODIFY COLUMN ID INT(11) NOT NULL;

ALTER TABLE shipment_info
      MODIFY COLUMN ID INT(11) NOT NULL,
      DROP COLUMN absship_id;

ALTER TABLE shipping_method
      CHANGE COLUMN name name VARCHAR(255) NOT NULL UNIQUE;

/*****************************************************
 * study changes
 ****************************************************/

CREATE TABLE aliquoted_specimen (
    ID INT(11) NOT NULL auto_increment,
    QUANTITY INT(11) NULL DEFAULT NULL,
    VOLUME DOUBLE NULL DEFAULT NULL,
    SPECIMEN_TYPE_ID INT(11) NOT NULL,
    ACTIVITY_STATUS_ID INT(11) NOT NULL,
    STUDY_ID INT(11) NOT NULL,
    INDEX FK75EACAC1F2A2464F (STUDY_ID),
    INDEX FK75EACAC1C449A4 (ACTIVITY_STATUS_ID),
    INDEX FK75EACAC138445996 (SPECIMEN_TYPE_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO aliquoted_specimen (quantity,volume,activity_status_id,study_id,specimen_type_id)
       SELECT quantity,volume,activity_status_id,study_id,specimen_type.id
       FROM sample_storage
       JOIN sample_type ON sample_type.id=sample_storage.sample_type_id
       JOIN specimen_type ON specimen_type.name=sample_type.name;

ALTER TABLE aliquoted_specimen MODIFY COLUMN ID INT(11) NOT NULL;

CREATE TABLE source_specimen (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    NEED_TIME_DRAWN TINYINT(1) NULL DEFAULT NULL,
    NEED_ORIGINAL_VOLUME TINYINT(1) NULL DEFAULT NULL,
    STUDY_ID INT(11) NOT NULL,
    SPECIMEN_TYPE_ID INT(11) NOT NULL,
    INDEX FK28D36ACF2A2464F (STUDY_ID),
    INDEX FK28D36AC38445996 (SPECIMEN_TYPE_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO source_specimen (need_time_drawn,need_original_volume,study_id,specimen_type_id)
       SELECT need_time_drawn,need_original_volume,study_id,specimen_type.id
       FROM study_source_vessel
       JOIN source_vessel on source_vessel.id=study_source_vessel.source_vessel_id
       JOIN specimen_type ON specimen_type.name=source_vessel.name;

ALTER TABLE source_specimen MODIFY COLUMN ID INT(11) NOT NULL;

ALTER TABLE STUDY
      CHANGE COLUMN NAME NAME VARCHAR(255) NOT NULL UNIQUE,
      CHANGE COLUMN NAME_SHORT NAME_SHORT VARCHAR(50) NOT NULL UNIQUE;

/*****************************************************
 * collection events
 ****************************************************/

CREATE TABLE collection_event (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    VISIT_NUMBER INT(11) NULL DEFAULT NULL,
    COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PATIENT_ID INT(11) NOT NULL,
    ACTIVITY_STATUS_ID INT(11) NOT NULL,
    PV_ID INT(11) NOT NULL,
    INDEX FKEDAD8999C449A4 (ACTIVITY_STATUS_ID),
    INDEX FKEDAD8999B563F38F (PATIENT_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO collection_event (visit_number,comment,pv_id)
       SELECT -1,patient_visit.comment,patient_visit.id
       FROM patient_visit;

-- source vessels

ALTER TABLE dispatch MODIFY COLUMN ID INT(11) NOT NULL;

/*****************************************************
 * container types and containers
 ****************************************************/

ALTER TABLE container
      CHANGE COLUMN label label VARCHAR(255) NOT NULL;

ALTER TABLE container_type
      CHANGE COLUMN name name VARCHAR(255) NOT NULL,
      CHANGE COLUMN name_short name_short VARCHAR(50) NOT NULL,
      CHANGE COLUMN child_labeling_scheme_id child_labeling_scheme_id INTEGER NOT NULL;

CREATE TABLE container_type_specimen_type (
    CONTAINER_TYPE_ID INT(11) NOT NULL,
    SPECIMEN_TYPE_ID INT(11) NOT NULL,
    INDEX FKE2F4C26AB3E77A12 (CONTAINER_TYPE_ID),
    INDEX FKE2F4C26A38445996 (SPECIMEN_TYPE_ID),
    PRIMARY KEY (CONTAINER_TYPE_ID, SPECIMEN_TYPE_ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO container_type_specimen_type (container_type_id,specimen_type_id)
       SELECT container_type_id,specimen_type.id
       FROM container_type_sample_type
       JOIN sample_type ON sample_type.id=container_type_sample_type.sample_type_id
       JOIN specimen_type ON specimen_type.name=sample_type.name;

-- unique constraint on multiple columns
ALTER TABLE container
      ADD CONSTRAINT uc_container_label UNIQUE KEY(label,container_type_id),
      ADD CONSTRAINT uc_container_productbarcode UNIQUE KEY(product_barcode,site_id);

ALTER TABLE container_type
      ADD CONSTRAINT uc_containertype_name UNIQUE KEY(name,site_id),
      ADD CONSTRAINT uc_containertype_nameshort UNIQUE KEY(name_short,site_id);

ALTER TABLE container_path
      ADD COLUMN TOP_CONTAINER_ID INT(11) NOT NULL COMMENT '',
      ADD INDEX FKB2C64D431BE0C379 (TOP_CONTAINER_ID);

UPDATE container_path
       SET top_container_id = IF(LOCATE('/', path) = 0, path, SUBSTR(path, 1, LOCATE('/', path)));

/*****************************************************
 * positions
 ****************************************************/

ALTER TABLE abstract_position
      DROP INDEX FKBC4AE0A6898584F,
      CHANGE COLUMN row row INT(11) NOT NULL,
      CHANGE COLUMN col col INT(11) NOT NULL,
      CHANGE COLUMN ALIQUOT_ID SPECIMEN_ID INT(11) NULL DEFAULT NULL COMMENT '',
      ADD COLUMN POSITION_STRING VARCHAR(50) NULL DEFAULT NULL COMMENT '',
      ADD INDEX FKBC4AE0A6EF199765 (SPECIMEN_ID),
      ADD CONSTRAINT SPECIMEN_ID UNIQUE KEY(SPECIMEN_ID);

-- update position_string values
-- SBS Standard

UPDATE abstract_position ap, container c, container_type ct
       SET position_string = CONCAT(SUBSTR("ABCDEFGH", row + 1, 1), col + 1)
       WHERE ap.container_id = c.id AND ap.discriminator = 'AliquotPosition'
       AND c.container_type_id = ct.id and ct.child_labeling_scheme_id = 1;

-- CBSR 2 Char Alphabetic

UPDATE abstract_position ap, container c, container_type ct
       SET position_string = CONCAT(SUBSTR("ABCDEFGHJKLMNPQRSTUVWXYZ", row div 24 + 1, 1),
           SUBSTR("ABCDEFGHJKLMNPQRSTUVWXYZ", mod(row, 24) + 1, 1))
       WHERE ap.container_id = c.id AND ap.discriminator = 'AliquotPosition'
       AND c.container_type_id = ct.id and ct.child_labeling_scheme_id = 2;

-- CBSR SBS

UPDATE abstract_position ap, container c, container_type ct
       SET position_string = CONCAT(SUBSTR("ABCDEFGHJ", row + 1, 1), col + 1)
       WHERE ap.container_id = c.id AND ap.discriminator = 'AliquotPosition'
       AND c.container_type_id = ct.id and ct.child_labeling_scheme_id = 5;


/*****************************************************
 *
 ****************************************************/

CREATE TABLE entity (
    ID INT(11) NOT NULL,
    CLASS_NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE entity_column (
    ID INT(11) NOT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    ENTITY_PROPERTY_ID INT(11) NOT NULL,
    INDEX FK16BD7321698D6AC (ENTITY_PROPERTY_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE entity_filter (
    ID INT(11) NOT NULL,
    FILTER_TYPE INT(11) NULL DEFAULT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    ENTITY_PROPERTY_ID INT(11) NOT NULL,
    INDEX FK635CF541698D6AC (ENTITY_PROPERTY_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE entity_property (
    ID INT(11) NOT NULL,
    PROPERTY VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PROPERTY_TYPE_ID INT(11) NOT NULL,
    ENTITY_ID INT(11) NULL DEFAULT NULL,
    INDEX FK3FC956B191CFD445 (ENTITY_ID),
    INDEX FK3FC956B157C0C3B0 (PROPERTY_TYPE_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE property_modifier (
    ID INT(11) NOT NULL,
    NAME TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PROPERTY_MODIFIER TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PROPERTY_TYPE_ID INT(11) NULL DEFAULT NULL,
    INDEX FK5DF9160157C0C3B0 (PROPERTY_TYPE_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;



CREATE TABLE property_type (
    ID INT(11) NOT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE report (
    ID INT(11) NOT NULL,
    NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    DESCRIPTION TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    USER_ID INT(11) NULL DEFAULT NULL,
    IS_PUBLIC TINYINT(1) NULL DEFAULT NULL,
    IS_COUNT TINYINT(1) NULL DEFAULT NULL,
    ENTITY_ID INT(11) NOT NULL,
    INDEX FK8FDF493491CFD445 (ENTITY_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE report_column (
    ID INT(11) NOT NULL,
    POSITION INT(11) NULL DEFAULT NULL,
    COLUMN_ID INT(11) NOT NULL,
    PROPERTY_MODIFIER_ID INT(11) NULL DEFAULT NULL,
    REPORT_ID INT(11) NULL DEFAULT NULL,
    INDEX FKF0B78C1BE9306A5 (REPORT_ID),
    INDEX FKF0B78C1C2DE3790 (PROPERTY_MODIFIER_ID),
    INDEX FKF0B78C1A946D8E8 (COLUMN_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE report_filter (
    ID INT(11) NOT NULL,
    POSITION INT(11) NULL DEFAULT NULL,
    OPERATOR INT(11) NULL DEFAULT NULL,
    ENTITY_FILTER_ID INT(11) NOT NULL,
    REPORT_ID INT(11) NULL DEFAULT NULL,
    INDEX FK13D570E3445CEC4C (ENTITY_FILTER_ID),
    INDEX FK13D570E3BE9306A5 (REPORT_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE report_filter_value (
    ID INT(11) NOT NULL,
    POSITION INT(11) NULL DEFAULT NULL,
    VALUE TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    SECOND_VALUE TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    REPORT_FILTER_ID INT(11) NULL DEFAULT NULL,
    INDEX FK691EF6F59FFD1CEE (REPORT_FILTER_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

# sample order tables

DROP TABLE IF EXISTS research_group;

CREATE TABLE research_group (
    ID INT(11) NOT NULL,
    NAME VARCHAR(150) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    NAME_SHORT VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    STUDY_ID INT(11) NOT NULL,
    ADDRESS_ID INT(11) NOT NULL,
    CONSTRAINT STUDY_ID UNIQUE KEY(STUDY_ID),
    INDEX FK7E0432BB6AF2992F (ADDRESS_ID),
    INDEX FK7E0432BBF2A2464F (STUDY_ID),
    CONSTRAINT ADDRESS_ID UNIQUE KEY(ADDRESS_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

DROP TABLE IF EXISTS research_group_researcher;

CREATE TABLE research_group_researcher (
    RESEARCH_GROUP_ID INT(11) NOT NULL,
    RESEARCHER_ID INT(11) NOT NULL,
    INDEX FK83006F8C213C8A5 (RESEARCHER_ID),
    INDEX FK83006F8C4BD922D8 (RESEARCH_GROUP_ID),
    PRIMARY KEY (RESEARCHER_ID, RESEARCH_GROUP_ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

DROP TABLE IF EXISTS researcher;

CREATE TABLE researcher (
    ID INT(11) NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

ALTER TABLE patient ADD COLUMN CREATED_AT DATETIME NULL DEFAULT NULL COMMENT '';

CREATE TABLE request (
    ID INT(11) NOT NULL,
    SUBMITTED DATETIME NULL DEFAULT NULL,
    ACCEPTED DATETIME NULL DEFAULT NULL,
    SHIPPED DATETIME NULL DEFAULT NULL,
    WAYBILL VARCHAR(150) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    STATE INT(11) NULL DEFAULT NULL,
    SITE_ID INT(11) NOT NULL,
    STUDY_ID INT(11) NOT NULL,
    ADDRESS_ID INT(11) NOT NULL,
    INDEX FK6C1A7E6FF2A2464F (STUDY_ID),
    INDEX FK6C1A7E6F3F52C885 (SITE_ID),
    CONSTRAINT ADDRESS_ID UNIQUE KEY(ADDRESS_ID),
    INDEX FK6C1A7E6F6AF2992F (ADDRESS_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;
CREATE TABLE request_aliquot (
    ID INT(11) NOT NULL,
    STATE INT(11) NULL DEFAULT NULL,
    CLAIMED_BY VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    REQUEST_ID INT(11) NOT NULL,
    ALIQUOT_ID INT(11) NOT NULL,
    INDEX FK2B486FB7A2F14F4F (REQUEST_ID),
    INDEX FK2B486FB7898584F (ALIQUOT_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

ALTER TABLE PATIENT
      CHANGE COLUMN PNUMBER PNUMBER VARCHAR(100) NOT NULL UNIQUE;

ALTER TABLE CAPACITY
      CHANGE COLUMN ROW_CAPACITY ROW_CAPACITY INTEGER NOT NULL,
      CHANGE COLUMN COL_CAPACITY COL_CAPACITY INTEGER NOT NULL;

ALTER TABLE ACTIVITY_STATUS
      CHANGE COLUMN NAME NAME VARCHAR(50) NOT NULL UNIQUE;

ALTER TABLE ALIQUOT
      CHANGE COLUMN INVENTORY_ID INVENTORY_ID VARCHAR(100) NOT NULL UNIQUE;



DROP TABLE IF EXISTS report;

CREATE TABLE report (
  ID int(11) NOT NULL,
  NAME varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  DESCRIPTION text COLLATE latin1_general_cs,
  USER_ID int(11) DEFAULT NULL,
  IS_PUBLIC bit(1) DEFAULT NULL,
  IS_COUNT bit(1) DEFAULT NULL,
  ENTITY_ID int(11) NOT NULL,
  PRIMARY KEY (ID),
  KEY FK8FDF493491CFD445 (ENTITY_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

--
-- Dumping data for table report
--

LOCK TABLES report WRITE;
/*!40000 ALTER TABLE report DISABLE KEYS */;
INSERT INTO report VALUES (7,'05 - Aliquots per Study',NULL,15,NULL,'',1),(8,'03 - Aliquots per Study per Clinic',NULL,15,NULL,'',1),(6,'01 - Aliquots',NULL,15,'\0','\0',1),(9,'04A - Aliquots per Study per Clinic by Year',NULL,15,'\0','',1),(16,'11 - New Patient Visits per Study by Date',NULL,15,NULL,'',4),(15,'14 - Patients per Study by Date',NULL,15,NULL,'',3),(14,'12 - New Patients per Study per Clinic by Date',NULL,15,NULL,'',3),(13,'07 - Aliquots by Container',NULL,15,NULL,NULL,1),(17,'13 - Patient Visits per Study by Date',NULL,15,NULL,'',4),(18,'18A - Invoicing Report',NULL,15,NULL,'',1),(19,'18B - Invoicing Report','',15,NULL,'',4),(20,'19 - Sample Type Totals by Patient Visit and Study',NULL,15,NULL,'',1);
/*!40000 ALTER TABLE report ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table report_filter
--

DROP TABLE IF EXISTS report_filter;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE report_filter (
  ID int(11) NOT NULL,
  POSITION int(11) DEFAULT NULL,
  OPERATOR int(11) DEFAULT NULL,
  ENTITY_FILTER_ID int(11) NOT NULL,
  REPORT_ID int(11) DEFAULT NULL,
  PRIMARY KEY (ID),
  KEY FK13D570E3445CEC4C (ENTITY_FILTER_ID),
  KEY FK13D570E3BE9306A5 (REPORT_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table report_filter
--

LOCK TABLES report_filter WRITE;
/*!40000 ALTER TABLE report_filter DISABLE KEYS */;
INSERT INTO report_filter VALUES (184,0,4,12,6),(185,1,1,2,6),(189,0,101,7,13),(190,1,3,12,13),(191,2,101,13,13),(193,0,4,12,8),(194,1,1,2,8),(195,0,4,12,9),(196,1,1,2,9),(197,0,4,12,7),(198,1,1,2,7),(199,0,NULL,206,14),(200,1,1,203,14),(201,0,1,203,15),(203,0,1,301,17),(204,0,4,12,18),(205,1,1,2,18),(208,0,4,12,20),(209,1,101,14,20),(210,0,1,301,16),(211,1,NULL,311,16),(212,0,1,301,19);
/*!40000 ALTER TABLE report_filter ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table report_filter_value
--

DROP TABLE IF EXISTS report_filter_value;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE report_filter_value (
  ID int(11) NOT NULL,
  POSITION int(11) DEFAULT NULL,
  VALUE text COLLATE latin1_general_cs,
  SECOND_VALUE text COLLATE latin1_general_cs,
  REPORT_FILTER_ID int(11) DEFAULT NULL,
  PRIMARY KEY (ID),
  KEY FK691EF6F59FFD1CEE (REPORT_FILTER_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table report_filter_value
--

LOCK TABLES report_filter_value WRITE;
/*!40000 ALTER TABLE report_filter_value DISABLE KEYS */;
INSERT INTO report_filter_value VALUES (138,0,'2799',NULL,208),(133,0,'2799',NULL,184),(134,0,'2799',NULL,193),(135,0,'2799',NULL,195),(136,0,'2799',NULL,197),(137,0,'2799',NULL,204),(140,0,'%',NULL,209);
/*!40000 ALTER TABLE report_filter_value ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table report_column
--

DROP TABLE IF EXISTS report_column;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE report_column (
  ID int(11) NOT NULL,
  POSITION int(11) DEFAULT NULL,
  COLUMN_ID int(11) NOT NULL,
  PROPERTY_MODIFIER_ID int(11) DEFAULT NULL,
  REPORT_ID int(11) DEFAULT NULL,
  PRIMARY KEY (ID),
  KEY FKF0B78C1BE9306A5 (REPORT_ID),
  KEY FKF0B78C1C2DE3790 (PROPERTY_MODIFIER_ID),
  KEY FKF0B78C1A946D8E8 (COLUMN_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table report_column
--

LOCK TABLES report_column WRITE;
/*!40000 ALTER TABLE report_column DISABLE KEYS */;
INSERT INTO report_column VALUES (22,2,11,NULL,6),(21,3,2,NULL,6),(20,4,8,NULL,6),(23,1,20,NULL,6),(24,0,1,NULL,6),(29,1,20,NULL,9),(30,0,15,NULL,9),(27,0,15,NULL,8),(28,2,2,1,9),(25,0,15,NULL,7),(26,1,20,NULL,8),(51,1,309,NULL,16),(53,2,301,3,17),(52,0,310,NULL,16),(50,2,301,3,16),(49,0,202,NULL,15),(48,1,203,3,15),(45,2,203,3,14),(47,0,202,NULL,14),(46,1,205,NULL,14),(40,4,8,NULL,13),(41,3,11,NULL,13),(42,2,1,NULL,13),(43,1,7,NULL,13),(44,0,13,NULL,13),(54,1,309,NULL,17),(55,0,310,NULL,17),(56,2,8,NULL,18),(57,1,20,NULL,18),(58,0,15,NULL,18),(59,1,309,NULL,19),(60,0,310,NULL,19),(63,3,8,NULL,20),(64,0,11,NULL,20),(65,2,10,NULL,20),(66,1,9,NULL,20);
/*!40000 ALTER TABLE report_column ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table entity_filter
--

DROP TABLE IF EXISTS entity_filter;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE entity_filter (
  ID int(11) NOT NULL,
  FILTER_TYPE int(11) DEFAULT NULL,
  NAME varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  ENTITY_PROPERTY_ID int(11) NOT NULL,
  PRIMARY KEY (ID),
  KEY FK635CF541698D6AC (ENTITY_PROPERTY_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table entity_filter
--

LOCK TABLES entity_filter WRITE;
/*!40000 ALTER TABLE entity_filter DISABLE KEYS */;
INSERT INTO entity_filter VALUES (1,1,'Inventory Id',1),(2,3,'Link Date',2),(3,1,'Comment',3),(4,2,'Quantity',4),(5,1,'Activity Status',5),(6,1,'Container Product Barcode',7),(7,1,'Container Label',8),(8,1,'Sample Type',9),(9,3,'Date Processed',10),(10,3,'Date Drawn',11),(11,1,'Patient Number',12),(12,4,'Top Container',6),(13,1,'Site',15),(14,1,'Study',16),(15,3,'Date Received',17),(16,1,'Waybill',18),(17,3,'Shipment Departure Date',19),(18,1,'Shipment Box Number',20),(19,1,'Clinic',21),(20,6,'First Patient Visit',10),(101,1,'Product Barcode',101),(102,1,'Comment',102),(103,1,'Label',103),(104,2,'Temperature',104),(105,4,'Top Container',105),(106,3,'Aliquot Link Date',106),(107,1,'Container Type',107),(108,5,'Is Top Level',108),(109,1,'Site',109),(201,1,'Patient Number',201),(202,1,'Study',202),(203,3,'Patient Visit Date Processed',203),(204,3,'Patient Visit Date Drawn',204),(205,1,'Clinic',205),(206,6,'First Patient Visit',203),(301,3,'Date Processed',301),(302,3,'Date Drawn',302),(303,1,'Comment',303),(304,1,'Patient Number',304),(305,3,'Shipment Date Received',305),(306,1,'Shipment Waybill',306),(307,3,'Shipment Date Departed',307),(308,1,'Shipment Box Number',308),(309,1,'Clinic',309),(310,1,'Study',310),(311,6,'First Patient Visit',301);
/*!40000 ALTER TABLE entity_filter ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table entity_column
--

DROP TABLE IF EXISTS entity_column;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE entity_column (
  ID int(11) NOT NULL,
  NAME varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  ENTITY_PROPERTY_ID int(11) NOT NULL,
  PRIMARY KEY (ID),
  KEY FK16BD7321698D6AC (ENTITY_PROPERTY_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table entity_column
--

LOCK TABLES entity_column WRITE;
/*!40000 ALTER TABLE entity_column DISABLE KEYS */;
INSERT INTO entity_column VALUES (1,'Inventory Id',1),(2,'Link Date',2),(3,'Comment',3),(4,'Quantity',4),(5,'Activity Status',5),(6,'Container Product Barcode',7),(7,'Container Label',8),(8,'Sample Type',9),(9,'Date Processed',10),(10,'Date Drawn',11),(11,'Patient Number',12),(12,'Top Container Type',13),(13,'Aliquot Position',14),(14,'Site',15),(15,'Study',16),(16,'Date Received',17),(17,'Waybill',18),(18,'Shipment Departure Date',19),(19,'Shipment Box Number',20),(20,'Clinic',21),(101,'Product Barcode',101),(102,'Comment',102),(103,'Label',103),(104,'Temperature',104),(105,'Top Container Type',110),(106,'Aliquot Link Date',106),(107,'Container Type',107),(108,'Site',109),(201,'Patient Number',201),(202,'Study',202),(203,'Patient Visit Date Processed',203),(204,'Patient Visit Date Drawn',204),(205,'Clinic',205),(301,'Date Processed',301),(302,'Date Drawn',302),(303,'Comment',303),(304,'Patient Number',304),(305,'Shipment Date Received',305),(306,'Shipment Waybill',306),(307,'Shipment Date Departed',307),(308,'Shipment Box Number',308),(309,'Clinic',309),(310,'Study',310);
/*!40000 ALTER TABLE entity_column ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table entity_property
--

DROP TABLE IF EXISTS entity_property;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE entity_property (
  ID int(11) NOT NULL,
  PROPERTY varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PROPERTY_TYPE_ID int(11) NOT NULL,
  ENTITY_ID int(11) DEFAULT NULL,
  PRIMARY KEY (ID),
  KEY FK3FC956B191CFD445 (ENTITY_ID),
  KEY FK3FC956B157C0C3B0 (PROPERTY_TYPE_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table entity_property
--

LOCK TABLES entity_property WRITE;
/*!40000 ALTER TABLE entity_property DISABLE KEYS */;
INSERT INTO entity_property VALUES (1,'inventoryId',1,1),(2,'linkDate',3,1),(3,'comment',1,1),(4,'quantity',2,1),(5,'activityStatus.name',1,1),(6,'aliquotPosition.container.containerPath.topContainer.id',2,1),(7,'aliquotPosition.container.productBarcode',1,1),(8,'aliquotPosition.container.label',1,1),(9,'sampleType.nameShort',1,1),(10,'patientVisit.dateProcessed',3,1),(11,'patientVisit.dateDrawn',3,1),(12,'patientVisit.shipmentPatient.patient.pnumber',1,1),(13,'aliquotPosition.container.containerPath.topContainer.containerType.nameShort',1,1),(14,'aliquotPosition.positionString',1,1),(15,'aliquotPosition.container.site.nameShort',1,1),(16,'patientVisit.shipmentPatient.patient.study.nameShort',1,1),(17,'patientVisit.shipmentPatient.shipment.dateReceived',3,1),(18,'patientVisit.shipmentPatient.shipment.waybill',1,1),(19,'patientVisit.shipmentPatient.shipment.departed',3,1),(20,'patientVisit.shipmentPatient.shipment.boxNumber',1,1),(21,'patientVisit.shipmentPatient.shipment.clinic.nameShort',1,1),(101,'productBarcode',1,2),(102,'comment',1,2),(103,'label',1,2),(104,'temperature',2,2),(105,'containerPath.topContainer.id',2,2),(106,'aliquotPositionCollection.aliquot.linkDate',3,2),(107,'containerType.nameShort',1,2),(108,'containerType.topLevel',4,2),(109,'site.nameShort',1,2),(110,'containerPath.topContainer.containerType.nameShort',1,2),(201,'pnumber',1,3),(202,'study.nameShort',1,3),(203,'shipmentPatientCollection.patientVisitCollection.dateProcessed',3,3),(204,'shipmentPatientCollection.patientVisitCollection.dateDrawn',3,3),(205,'shipmentPatientCollection.shipment.clinic.nameShort',1,3),(301,'dateProcessed',3,4),(302,'dateDrawn',3,4),(303,'comment',1,4),(304,'shipmentPatient.patient.pnumber',1,4),(305,'shipmentPatient.shipment.dateReceived',3,4),(306,'shipmentPatient.shipment.waybill',1,4),(307,'shipmentPatient.shipment.departed',3,4),(308,'shipmentPatient.shipment.boxNumber',1,4),(309,'shipmentPatient.shipment.clinic.nameShort',1,4),(310,'shipmentPatient.patient.study.nameShort',1,4);
/*!40000 ALTER TABLE entity_property ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table property_type
--

DROP TABLE IF EXISTS property_type;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE property_type (
  ID int(11) NOT NULL,
  NAME varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table property_type
--

LOCK TABLES property_type WRITE;
/*!40000 ALTER TABLE property_type DISABLE KEYS */;
INSERT INTO property_type VALUES (1,'String'),(2,'Number'),(3,'Date'),(4,'Boolean');
/*!40000 ALTER TABLE property_type ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table property_modifier
--

DROP TABLE IF EXISTS property_modifier;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE property_modifier (
  ID int(11) NOT NULL,
  NAME text COLLATE latin1_general_cs,
  PROPERTY_MODIFIER text COLLATE latin1_general_cs,
  PROPERTY_TYPE_ID int(11) DEFAULT NULL,
  PRIMARY KEY (ID),
  KEY FK5DF9160157C0C3B0 (PROPERTY_TYPE_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table property_modifier
--

LOCK TABLES property_modifier WRITE;
/*!40000 ALTER TABLE property_modifier DISABLE KEYS */;
INSERT INTO property_modifier VALUES (1,'Year','YEAR({value})',3),(2,'Year, Quarter','CONCAT(YEAR({value}), CONCAT(\'-\', QUARTER({value})))',3),(3,'Year, Month','CONCAT(YEAR({value}), CONCAT(\'-\', MONTH({value})))',3),(4,'Year, Week','CONCAT(YEAR({value}), CONCAT(\'-\', WEEK({value})))',3);
/*!40000 ALTER TABLE property_modifier ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table entity
--

DROP TABLE IF EXISTS entity;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE entity (
  ID int(11) NOT NULL,
  CLASS_NAME varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  NAME varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table entity
--

LOCK TABLES entity WRITE;
/*!40000 ALTER TABLE entity DISABLE KEYS */;
INSERT INTO entity VALUES (1,'edu.ualberta.med.biobank.model.Aliquot','Aliquot'),(2,'edu.ualberta.med.biobank.model.Container','Container'),(3,'edu.ualberta.med.biobank.model.Patient','Patient'),(4,'edu.ualberta.med.biobank.model.PatientVisit','PatientVisit');
/*!40000 ALTER TABLE entity ENABLE KEYS */;
UNLOCK TABLES;

-- update constraints (unique and not-null):
-- also update ContainerType -> ContainerLabelingScheme relation replace 0..1 by 1 (so cannot be null)

/*****************************************************
 * drop tables that are no longer required
 ****************************************************/

#DROP TABLE clinic;
#DROP TABLE site;
#DROP TABLE abstract_shipment;
#DROP TABLE sample_type;
#DROP TABLE source_vessel;
#DROP TABLE sample_storage
#DROP TABLE study_source_vessel
