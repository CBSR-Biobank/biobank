/*------------------------------------------------------------------------------
 *
 *  BioBank2 MySQL upgrade script for model version 1.3.1 to 1.4.0
 *
 *----------------------------------------------------------------------------*/


/*****************************************************
 * Address table
 ****************************************************/

ALTER TABLE address
      ADD COLUMN EMAIL_ADDRESS VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL COMMENT '',
      ADD COLUMN PHONE_NUMBER VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL COMMENT '',
      ADD COLUMN FAX_NUMBER VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL COMMENT '';

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
      CHANGE COLUMN center_id SITE_ID INT(11) NOT NULL,
      ADD INDEX FK7A197EB13F52C885 (SITE_ID),
      ADD PRIMARY KEY (SITE_ID,STUDY_ID);

ALTER TABLE contact
      ADD COLUMN CENTER_ID int(11) COMMENT '' NOT NULL;

update contact set contact.center_id=(select center.id
       from clinic
       join center on center.name=clinic.name
       where clinic.id=contact.clinic_id);

alter table contact
      drop index FK6382B00057F87A25,
      drop column clinic_id,
      change column CENTER_ID CLINIC_ID int(11) COMMENT '' NOT NULL,
      add index FK6382B00057F87A25 (CLINIC_ID);

ALTER TABLE center MODIFY COLUMN ID INT(11) NOT NULL;

/*****************************************************
 * specimens
 ****************************************************/

CREATE TABLE specimen_type (
  ID int(11) NOT NULL auto_increment,
  NAME varchar(100) NOT NULL,
  NAME_SHORT varchar(50) NOT NULL,
  PRIMARY KEY (ID),
  UNIQUE KEY NAME (NAME),
  UNIQUE KEY NAME_SHORT (NAME_SHORT)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

UPDATE source_vessel set name='CHILD Meconium' where name='Meconium';

INSERT INTO specimen_type (name,name_short)
       SELECT name,name_short FROM sample_type;

INSERT INTO specimen_type (name,name_short)
        SELECT name,name FROM source_vessel;

ALTER TABLE specimen_type MODIFY COLUMN ID INT(11) NOT NULL;

CREATE TABLE specimen (
  ID int(11) NOT NULL auto_increment,
  INVENTORY_ID varchar(100) COLLATE latin1_general_cs NOT NULL,
  COMMENT text COLLATE latin1_general_cs,
  QUANTITY double DEFAULT NULL,
  CREATED_AT datetime NOT NULL,
  ACTIVITY_STATUS_ID int(11) NOT NULL,
  ORIGINAL_COLLECTION_EVENT_ID int(11) DEFAULT NULL,
  PROCESSING_EVENT_ID int(11) DEFAULT NULL,
  ORIGIN_INFO_ID int(11) NOT NULL,
  SPECIMEN_TYPE_ID int(11) NOT NULL,
  COLLECTION_EVENT_ID int(11) NOT NULL,
  PARENT_SPECIMEN_ID int(11) DEFAULT NULL,
  TOP_SPECIMEN_ID int(11) NULL DEFAULT NULL,
  CURRENT_CENTER_ID int(11) DEFAULT NULL,
  PV_ID INT(11),
  PV_SV_ID INT(11),
  PRIMARY KEY (ID),
  UNIQUE KEY INVENTORY_ID (INVENTORY_ID),
  KEY FKAF84F30886857784 (ORIGINAL_COLLECTION_EVENT_ID),
  KEY FKAF84F308280272F2 (COLLECTION_EVENT_ID),
  KEY FKAF84F308C449A4 (ACTIVITY_STATUS_ID),
  KEY FKAF84F30812E55F12 (ORIGIN_INFO_ID),
  KEY FKAF84F30861674F50 (PARENT_SPECIMEN_ID),
  KEY FKAF84F30833126C8 (PROCESSING_EVENT_ID),
  KEY FKAF84F308FBB79BBF (CURRENT_CENTER_ID),
  KEY FKAF84F30838445996 (SPECIMEN_TYPE_ID),
  KEY FKAF84F308C9EF5F7B (TOP_SPECIMEN_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;


create index pv_id_idx on specimen(pv_id);
create index pv_sv_id_idx on specimen(pv_sv_id);

-- add an aliquoted specimen for each patient visit

INSERT INTO specimen (inventory_id,comment,quantity,created_at,specimen_type_id,
activity_status_id,original_collection_event_id,pv_id)
        SELECT inventory_id,comment,quantity,link_date,specimen_type.id,activity_status_id,0,
        patient_visit_id
        FROM aliquot
        JOIN sample_type ON sample_type.id=aliquot.sample_type_id
        JOIN specimen_type ON specimen_type.name=sample_type.name;

-- add a source specimen for each patient visit
--
-- if the pvsv.time_drawn is null the specimen created at time is the date from
-- pv.date_drawn,
--
-- if the pvsv.time_drawn is not null specimen created at time is the date from
-- pv.date_drawn plus the time from pvsv.time_drawn

INSERT INTO specimen (inventory_id,quantity,created_at,activity_status_id,
       collection_event_id,original_collection_event_id,specimen_type_id,
       parent_specimen_id,top_specimen_id,origin_info_id,pv_id,pv_sv_id)
       SELECT concat("sw upgrade ",pvsv.id),volume,
       if(pvsv.time_drawn is null,pv.date_drawn,
               addtime(timestamp(date(pv.date_drawn)), time(pvsv.time_drawn))),
       (select id from activity_status where name='Active'),0,0,specimen_type.id,
       null,null,0,pv.id,pvsv.id
       FROM pv_source_vessel as pvsv
       join patient_visit as pv on pv.id=pvsv.patient_visit_id
       JOIN source_vessel as sv on sv.id=pvsv.source_vessel_id
       join specimen_type on specimen_type.name=sv.name;

-- initialize the current center on source specimens to be where they were
-- created, dispatches are handled in the next step

UPDATE specimen,patient_visit
       as pv, clinic_shipment_patient as csp,abstract_shipment as aship,
       clinic,center,site
       SET current_center_id=center.id
       where csp.id=pv.CLINIC_SHIPMENT_PATIENT_ID
       and aship.id=csp.CLINIC_SHIPMENT_ID
       and clinic.id=aship.clinic_id
       and site.id=aship.site_id
       and center.name=site.name
       and pv.id=specimen.pv_id
       and aship.discriminator='ClinicShipment';

-- set the current center for aliquots that have been disptached

update specimen spc, aliquot aq,dispatch_shipment_aliquot dsa,abstract_shipment aship,
       site,center
       set current_center_id=center.id
       where spc.inventory_id=aq.inventory_id
       and dsa.aliquot_id=aq.id
       and aship.id=dsa.dispatch_shipment_id
       and site.id=aship.dispatch_receiver_id
       and center.name=site.name
       and aship.discriminator='DispatchShipment';

-- set the "top specimen" on source specimens to point to themselves

update specimen as spc
       set spc.top_specimen_id=spc.id
       where spc.pv_sv_id is not null;

-- set the aliquoted specimens to point to their parent specimen

update specimen as spc_a, specimen as spc_b
       set spc_a.parent_specimen_id=spc_b.id, spc_a.top_specimen_id=spc_b.id
       where spc_a.pv_id=spc_b.pv_id and spc_b.pv_sv_id is not null and spc_a.pv_sv_id is null;

ALTER TABLE specimen MODIFY COLUMN ID INT(11) NOT NULL;

CREATE TABLE specimen_type_specimen_type (
    CHILD_SPECIMEN_TYPE_ID INT(11) NOT NULL,
    PARENT_SPECIMEN_TYPE_ID INT(11) NOT NULL,
    INDEX FKD95844635F3DC8B (PARENT_SPECIMEN_TYPE_ID),
    INDEX FKD9584463D9672259 (CHILD_SPECIMEN_TYPE_ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;


/*****************************************************
 * shipments and disptaches
 ****************************************************/

-- aship_id is temporary

CREATE TABLE shipment_info (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    RECEIVED_AT DATETIME NULL DEFAULT NULL,
    PACKED_AT DATETIME NULL DEFAULT NULL,
    WAYBILL VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    BOX_NUMBER VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    SHIPPING_METHOD_ID INT(11) NOT NULL,
    ASHIP_ID INT(11) NOT NULL,
    INDEX FK95BCA433DCA49682 (SHIPPING_METHOD_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

create index WAYBILL_IDX on shipment_info(WAYBILL);
create index RECEIVED_AT_IDX on shipment_info(RECEIVED_AT);

INSERT INTO shipment_info (aship_id,received_at,packed_at,waybill,box_number,shipping_method_id)
SELECT id,date_received,date_shipped,waybill,box_number,shipping_method_id FROM abstract_shipment
WHERE discriminator='ClinicShipment';

-- aship_type=0 for source specimens, aship_type=1 for aliquoted_specimens

CREATE TABLE origin_info (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    SHIPMENT_INFO_ID INT(11) NULL DEFAULT NULL,
    CENTER_ID INT(11) NOT NULL,
    ASHIP_ID INT(11) NOT NULL,
    ASHIP_TYPE INT(11) NOT NULL,
    CONSTRAINT SHIPMENT_INFO_ID UNIQUE KEY(SHIPMENT_INFO_ID),
    INDEX FKE92E7A2792FAA705 (CENTER_ID),
    INDEX FKE92E7A27F59D873A (SHIPMENT_INFO_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

-- create origin info for source specimens

INSERT INTO origin_info (center_id,shipment_info_id,aship_id,aship_type)
SELECT center.id,shipment_info.id,abstract_shipment.id,0
       FROM abstract_shipment
       JOIN clinic ON clinic.id=abstract_shipment.clinic_id
       JOIN center ON center.name=clinic.name
       join shipment_info on shipment_info.aship_id=abstract_shipment.id
       WHERE abstract_shipment.discriminator='ClinicShipment';

-- create origin info for aliquoted specimens

INSERT INTO origin_info (center_id,shipment_info_id,aship_id,aship_type)
SELECT center.id,null,abstract_shipment.id,1
       FROM abstract_shipment
       JOIN site ON site.id=abstract_shipment.site_id
       JOIN center ON center.name=site.name
       join shipment_info on shipment_info.aship_id=abstract_shipment.id
       WHERE abstract_shipment.discriminator='ClinicShipment';

create index aship_id_idx on origin_info(aship_id);

-- set origin info for source specimens

UPDATE specimen,patient_visit as pv, clinic_shipment_patient as csp,
abstract_shipment as aship, origin_info as oi
       set specimen.origin_info_id=oi.id
       where csp.id=pv.CLINIC_SHIPMENT_PATIENT_ID
       and aship.id=csp.CLINIC_SHIPMENT_ID
       and oi.aship_id=aship.id
       and aship.discriminator='ClinicShipment'
       and oi.aship_type=0
       and specimen.pv_id=pv.id
       and specimen.pv_sv_id is not null;

-- set origin info for aliquoted specimens

UPDATE specimen,patient_visit as pv, clinic_shipment_patient as csp,
abstract_shipment as aship, origin_info as oi
       set specimen.origin_info_id=oi.id
       where csp.id=pv.CLINIC_SHIPMENT_PATIENT_ID
       and aship.id=csp.CLINIC_SHIPMENT_ID
       and oi.aship_id=aship.id
       and aship.discriminator='ClinicShipment'
       and oi.aship_type=1
       and specimen.pv_id=pv.id
       and specimen.pv_sv_id is null;

drop index aship_id_idx on origin_info;

ALTER TABLE origin_info MODIFY COLUMN ID INT(11) NOT NULL;

CREATE TABLE dispatch (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    STATE INT(11) NULL DEFAULT NULL,
    COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    RECEIVER_CENTER_ID INT(11) NULL DEFAULT NULL,
    SHIPMENT_INFO_ID INT(11)  NULL DEFAULT NULL,
    SENDER_CENTER_ID INT(11) NULL DEFAULT NULL,
    REQUEST_ID INT(11) NULL DEFAULT NULL,
    ASHIP_ID INT(11) NOT NULL,
    INDEX FK3F9F347A91BC3D7B (SENDER_CENTER_ID),
    INDEX FK3F9F347AA2F14F4F (REQUEST_ID),
    INDEX FK3F9F347A307B2CB5 (RECEIVER_CENTER_ID),
    CONSTRAINT SHIPMENT_INFO_ID UNIQUE KEY(SHIPMENT_INFO_ID),
    INDEX FK3F9F347AF59D873A (SHIPMENT_INFO_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO shipment_info (aship_id,received_at,packed_at,waybill,box_number,
       shipping_method_id)
       SELECT id,date_received,date_shipped,waybill,box_number,shipping_method_id
       FROM abstract_shipment
       WHERE discriminator='DispatchShipment';

INSERT INTO dispatch (sender_center_id,receiver_center_id,state,comment,shipment_info_id,
       aship_id)
       SELECT sender_center.id,receiver_center.id,state,abstract_shipment.comment,
       shipment_info.id,abstract_shipment.id
       FROM abstract_shipment
       JOIN site as sender_site on sender_site.id=abstract_shipment.dispatch_sender_id
       JOIN center as sender_center on sender_center.name=sender_site.name
       JOIN site as receiver_site on receiver_site.id=abstract_shipment.dispatch_receiver_id
       JOIN center as receiver_center on receiver_center.name=receiver_site.name
       JOIN shipment_info on shipment_info.aship_id=abstract_shipment.id
       WHERE abstract_shipment.discriminator='DispatchShipment';

CREATE TABLE dispatch_specimen (
    ID INT(11) NOT NULL auto_increment,
    STATE INT(11) NULL DEFAULT NULL,
    COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    SPECIMEN_ID INT(11) NULL DEFAULT NULL,
    DISPATCH_ID INT(11) NULL DEFAULT NULL,
    INDEX FKEE25592DEF199765 (SPECIMEN_ID),
    INDEX FKEE25592DDE99CA25 (DISPATCH_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

insert into dispatch_specimen (state,comment,specimen_id,dispatch_id)
       select dsa.state,dsa.comment,specimen.id,dispatch.id
       from dispatch_shipment_aliquot as dsa
       join abstract_shipment as aship on aship.id=dsa.dispatch_shipment_id
       join aliquot on aliquot.id=dsa.aliquot_id
       join specimen on specimen.inventory_id=aliquot.inventory_id
       join dispatch on dispatch.aship_id=aship.id
       where discriminator='DispatchShipment';

ALTER TABLE dispatch_specimen MODIFY COLUMN ID INT(11) NOT NULL;

ALTER TABLE dispatch MODIFY COLUMN ID INT(11) NOT NULL;

ALTER TABLE shipment_info
      MODIFY COLUMN ID INT(11) NOT NULL,
      DROP COLUMN aship_id;

ALTER TABLE shipping_method
      CHANGE COLUMN name NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL COMMENT '',
      ADD CONSTRAINT NAME UNIQUE KEY(NAME);

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

-- fix database errors so that foreign key constraints do not fail

INSERT INTO aliquoted_specimen (quantity,volume,activity_status_id,study_id,specimen_type_id)
       SELECT quantity,volume,sample_storage.activity_status_id,study_id,specimen_type.id
       FROM sample_storage
       JOIN sample_type ON sample_type.id=sample_storage.sample_type_id
       JOIN specimen_type ON specimen_type.name=sample_type.name;

delete aspc from aliquoted_specimen as aspc
       left join study on study.id=aspc.study_id
       where study.id is null;

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
       FROM study_source_vessel as ssv
       JOIN source_vessel on source_vessel.id=ssv.source_vessel_id
       JOIN specimen_type ON specimen_type.name=source_vessel.name;

ALTER TABLE source_specimen MODIFY COLUMN ID INT(11) NOT NULL;

delete ss from source_specimen as ss
       left join study on study.id=ss.study_id
       where study.id is null;

ALTER TABLE STUDY
      CHANGE COLUMN NAME NAME VARCHAR(255) NOT NULL UNIQUE,
      CHANGE COLUMN NAME_SHORT NAME_SHORT VARCHAR(50) NOT NULL UNIQUE;

/*****************************************************
 * collection events
 ****************************************************/

CREATE TABLE collection_event (
    ID INT(11) NOT NULL AUTO_INCREMENT,
    VISIT_NUMBER INT(11) NOT NULL,
    COMMENT TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PATIENT_ID INT(11) NOT NULL,
    ACTIVITY_STATUS_ID INT(11) NOT NULL,
    PV_ID INT(11) NOT NULL,
    PV_DATE_DRAWN datetime NOT NULL,
    INDEX FKEDAD8999C449A4 (ACTIVITY_STATUS_ID),
    INDEX FKEDAD8999B563F38F (PATIENT_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

INSERT INTO collection_event (visit_number,comment,patient_id,activity_status_id,pv_id,
       pv_date_drawn)
       SELECT -1,pv.comment,csp.patient_id,
       (select id from activity_status where name='Active'),pv.id,pv.date_drawn
       FROM patient_visit as pv
       join clinic_shipment_patient as csp on csp.id=pv.CLINIC_SHIPMENT_PATIENT_ID;

create index pv_id_idx on collection_event(pv_id);

-- number the collection event 'visit number' according the patient visit 'date drawn'
-- in chronological order

set @pp = null;
set @pv = null;

update collection_event ce
       set ce.visit_number = if(@pp <> ce.patient_id or @pp is null,
           if(@pp := ce.patient_id, @pv := 1, @pv := 1), @pv := @pv + 1)
       order by patient_id, pv_date_drawn;

ALTER TABLE collection_event
  ADD CONSTRAINT uc_visit_number UNIQUE (VISIT_NUMBER,PATIENT_ID);

-- set specimen.original_collection_event_id for source specimens

update specimen,collection_event as ce
       set specimen.original_collection_event_id=ce.id,specimen.collection_event_id=ce.id
       where ce.pv_id=specimen.pv_id and specimen.pv_sv_id is not null;

-- set specimen.collection_event_id for aliquoted specimens

update specimen,collection_event as ce
       set specimen.collection_event_id=ce.id,specimen.original_collection_event_id=null
       where ce.pv_id=specimen.pv_id and specimen.pv_sv_id is null;

ALTER TABLE collection_event
      MODIFY COLUMN ID INT(11) NOT NULL,
      drop column PV_DATE_DRAWN;

/*****************************************************
 *  EVENT ATTRIBUTES
 ****************************************************/

insert into global_pv_attr (id,label,pv_attr_type_id) values (7,"Patient Type",4);

update study_pv_attr spa, study set spa.label="Patient Type"
       where study.id=spa.study_id and study.name_short='CRM'
       and spa.label="Visit Type";

RENAME TABLE global_pv_attr TO global_event_attr;
RENAME TABLE study_pv_attr TO study_event_attr;
RENAME TABLE pv_attr_type TO event_attr_type;

-- Study CRM should have 'Visit Type' changed to 'Patient Type'

ALTER TABLE global_event_attr
      CHANGE COLUMN PV_ATTR_TYPE_ID EVENT_ATTR_TYPE_ID INT(11) NOT NULL,
      DROP INDEX FKEDC41FEE2496A267,
      ADD INDEX FKBE7ED6B25B770B31 (EVENT_ATTR_TYPE_ID);

ALTER TABLE study_event_attr
      MODIFY COLUMN LABEL VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
      CHANGE COLUMN PV_ATTR_TYPE_ID EVENT_ATTR_TYPE_ID INT(11) NOT NULL,
      DROP INDEX FK669DD7F4F2A2464F,
      DROP INDEX FK669DD7F42496A267,
      DROP INDEX FK669DD7F4C449A4,
      ADD CONSTRAINT uc_label UNIQUE KEY(LABEL, STUDY_ID),
      ADD INDEX FK3EACD8ECF2A2464F (STUDY_ID),
      ADD INDEX FK3EACD8ECC449A4 (ACTIVITY_STATUS_ID),
      ADD INDEX FK3EACD8EC5B770B31 (EVENT_ATTR_TYPE_ID);

-- need to remove duplicate rows in pv_attr

CREATE TABLE event_attr (
  ID int(11) NOT NULL auto_increment,
  VALUE varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  COLLECTION_EVENT_ID int(11) NOT NULL,
  STUDY_EVENT_ATTR_ID int(11) NOT NULL,
  PRIMARY KEY (ID),
  KEY FK59508C96280272F2 (COLLECTION_EVENT_ID),
  KEY FK59508C96A9CFCFDB (STUDY_EVENT_ATTR_ID),
  CONSTRAINT FK59508C96A9CFCFDB FOREIGN KEY (STUDY_EVENT_ATTR_ID) REFERENCES study_event_attr (ID),
  CONSTRAINT FK59508C96280272F2 FOREIGN KEY (COLLECTION_EVENT_ID) REFERENCES collection_event (ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

insert into event_attr (value,collection_event_id,study_event_attr_id)
       select pa.value,ce.id,sea.id
       from pv_attr pa
       join (select patient_visit_id,max(id) max_id
            from pv_attr
            group by patient_visit_id,study_pv_attr_id) a on a.max_id=pa.id
       join collection_event ce on ce.pv_id=pa.patient_visit_id
       join study_event_attr sea on sea.id=pa.study_pv_attr_id;

ALTER TABLE event_attr MODIFY COLUMN ID INT(11) NOT NULL;

/*****************************************************
 * processing events
 ****************************************************/

CREATE TABLE processing_event (
  ID int(11) NOT NULL auto_increment,
  WORKSHEET varchar(100) COLLATE latin1_general_cs DEFAULT NULL,
  CREATED_AT datetime NOT NULL,
  COMMENT text COLLATE latin1_general_cs,
  CENTER_ID int(11) NOT NULL,
  ACTIVITY_STATUS_ID int(11) NOT NULL,
  PV_ID INT(11),
  PRIMARY KEY (ID),
  KEY FK327B1E4EC449A4 (ACTIVITY_STATUS_ID),
  KEY FK327B1E4E92FAA705 (CENTER_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;

create index CREATED_AT_IDX on processing_event(CREATED_AT);

-- add a processing event for each patient visit
-- (more than one processing event will have the same worksheet and same date/time.
-- This won't be allowed anymore in future entries)

insert into processing_event (created_at,comment,activity_status_id,center_id,pv_id)
       select pv.date_processed,pv.comment,
       (select id from activity_status where name='Active'),center.id,pv.id
       from patient_visit as pv
       join clinic_shipment_patient as csp on csp.id=pv.CLINIC_SHIPMENT_PATIENT_ID
       join abstract_shipment as aship on aship.id=csp.CLINIC_SHIPMENT_ID
       join site s on s.id=aship.site_id
       join center on center.name=s.name;

update processing_event pe,collection_event ce,patient_visit pv,event_attr,study_event_attr
       set worksheet=event_attr.value
       where pe.pv_id=pv.id
       and event_attr.collection_event_id=ce.id
       and ce.pv_id=pv.id
       and study_event_attr.id=event_attr.study_event_attr_id
       and label='Worksheet';

ALTER TABLE processing_event MODIFY COLUMN ID INT(11) NOT NULL;

create index pv_id_idx on processing_event(pv_id);

-- set the processing event for all source specimens

update specimen as spc,processing_event as pe
       set processing_event_id=pe.id
       where pe.pv_id=spc.pv_id
       and spc.pv_sv_id is not null;


/*****************************************************
 * container types and containers
 ****************************************************/

ALTER TABLE container
      CHANGE COLUMN label LABEL VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL COMMENT '',
      ADD COLUMN CENTER_ID int(11) COMMENT '' NOT NULL,
      ADD CONSTRAINT uc_label UNIQUE KEY(LABEL, CONTAINER_TYPE_ID);

update container set container.center_id=(select center.id
       from site
       join center on center.name=site.name
       where site.id=container.site_id);

alter table container
      drop index FK8D995C613F52C885,
      drop column site_id,
      change column CENTER_ID SITE_ID int(11) COMMENT '' NOT NULL,
      add index FK8D995C613F52C885 (SITE_ID),
      ADD CONSTRAINT uc_productbarcode UNIQUE KEY(PRODUCT_BARCODE, SITE_ID);

ALTER TABLE container_type
      CHANGE COLUMN name NAME VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL COMMENT '',
      CHANGE COLUMN name_short NAME_SHORT VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL COMMENT '',
      CHANGE COLUMN child_labeling_scheme_id CHILD_LABELING_SCHEME_ID INT(11) NOT NULL COMMENT '',
      ADD COLUMN CENTER_ID int(11) COMMENT '' NOT NULL,
      ADD CONSTRAINT uc_name UNIQUE KEY(NAME, SITE_ID),
      ADD CONSTRAINT uc_nameshort UNIQUE KEY(NAME_SHORT, SITE_ID);

update container_type set container_type.center_id=(select center.id
       from site
       join center on center.name=site.name
       where site.id=container_type.site_id);

alter table container_type
      drop key uc_name,
      drop key uc_nameshort,
      drop index FKB2C878583F52C885,
      drop column site_id,
      change column CENTER_ID SITE_ID int(11) COMMENT '' NOT NULL,
      ADD INDEX FKB2C878583F52C885 (SITE_ID),
      ADD CONSTRAINT uc_name UNIQUE KEY(NAME, SITE_ID),
      ADD CONSTRAINT uc_nameshort UNIQUE KEY(NAME_SHORT, SITE_ID);

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
      DROP KEY ALIQUOT_ID,
      CHANGE COLUMN row ROW INT(11) NOT NULL COMMENT '',
      CHANGE COLUMN col COL INT(11) NOT NULL COMMENT '',
      ADD COLUMN SPECIMEN_ID INT(11) NULL DEFAULT NULL COMMENT '',
      ADD COLUMN POSITION_STRING VARCHAR(50) NULL DEFAULT NULL COMMENT '',
      ADD INDEX FKBC4AE0A6EF199765 (SPECIMEN_ID),
      ADD CONSTRAINT SPECIMEN_ID UNIQUE KEY(SPECIMEN_ID);

update abstract_position ap ,aliquot aq,specimen spc
       set ap.specimen_id=spc.id
       where ap.aliquot_id=aq.id
       and aq.inventory_id=spc.inventory_id;

alter table abstract_position
      drop column ALIQUOT_ID;

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
 * log
 ****************************************************/

alter table log
      change column SITE CENTER VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '';

/*****************************************************
 * research groups and sample orders
 ****************************************************/

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
    CREATED DATETIME NULL DEFAULT NULL,
    STATE INT(11) NULL DEFAULT NULL,
    ADDRESS_ID INT(11) NOT NULL,
    STUDY_ID INT(11) NOT NULL,
    CONSTRAINT ADDRESS_ID UNIQUE KEY(ADDRESS_ID),
    INDEX FK6C1A7E6FF2A2464F (STUDY_ID),
    INDEX FK6C1A7E6F6AF2992F (ADDRESS_ID),
    PRIMARY KEY (ID)
) ENGINE=MyISAM COLLATE=latin1_general_cs;

CREATE TABLE request_specimen (
    ID INT(11) NOT NULL,
    STATE INT(11) NULL DEFAULT NULL,
    CLAIMED_BY VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    SPECIMEN_ID INT(11) NULL DEFAULT NULL,
    AREQUEST_ID INT(11) NULL DEFAULT NULL,
    INDEX FK579572D8D990A70 (AREQUEST_ID),
    INDEX FK579572D8EF199765 (SPECIMEN_ID),
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

/*****************************************************
 * advanced reports
 ****************************************************/

-- MySQL dump 10.13  Distrib 5.1.41, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: biobank2
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.10

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

--
-- Table structure for table `entity`
--

DROP TABLE IF EXISTS `entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity` (
  `ID` int(11) NOT NULL,
  `CLASS_NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity`
--

LOCK TABLES `entity` WRITE;
/*!40000 ALTER TABLE `entity` DISABLE KEYS */;
INSERT INTO `entity` VALUES (1,'edu.ualberta.med.biobank.model.Specimen','Specimen'),
(2,'edu.ualberta.med.biobank.model.Container','Container'),
(3,'edu.ualberta.med.biobank.model.Patient','Patient'),
(4,'edu.ualberta.med.biobank.model.CollectionEvent','Collection Event'),
(5,'edu.ualberta.med.biobank.model.ProcessingEvent','Processing Event');
/*!40000 ALTER TABLE `entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_column`
--

DROP TABLE IF EXISTS `entity_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity_column` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `ENTITY_PROPERTY_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),

  KEY `FK16BD7321698D6AC` (`ENTITY_PROPERTY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_column`
--

LOCK TABLES `entity_column` WRITE;
/*!40000 ALTER TABLE `entity_column` DISABLE KEYS */;
INSERT INTO `entity_column` VALUES (1,'Inventory Id',1),
(2,'Creation Time',2),
(3,'Comment',3),
(4,'Quantity',4),
(5,'Activity Status',5),
(6,'Container Product Barcode',7),
(7,'Container Label',8),
(8,'Specimen Type',9),
(9,'Time Processed',10),
(11,'Patient Number',12),
(12,'Top Container Type',13),
(13,'Aliquot Position',14),
(14,'Current Center',15),
(15,'Study',16),
(16,'Shipment Time Received',17),
(17,'Shipment Waybill',18),
(18,'Shipment Time Sent',19),
(19,'Shipment Box Number',20),
(20,'Source Center',21),
(21,'Dispatch Sender',22),
(22,'Dispatch Receiver',23),
(23,'Dispatch Time Received',24),
(24,'Dispatch Time Sent',25),
(25,'Dispatch Waybill',26),
(26,'Dispatch Box Number',27),
(27,'Visit Number',28),
(101,'Product Barcode',101),
(102,'Comment',102),
(103,'Label',103),
(104,'Temperature',104),
(105,'Top Container Type',110),
(106,'Specimen Creation Time',106),
(107,'Container Type',107),
(108,'Site',109),
(201,'Patient Number',201),
(202,'Study',202),
(203,'Specimen Time Processed',203),
(204,'Specimen Creation Time',204),
(205,'Source Center',205),
(206,'Visit Number',207),
(301,'Specimen Time Processed',301),
(302,'Specimen Creation Time',302),
(303,'Comment',303),
(304,'Patient Number',304),
(305,'Specimen Source Center',305),
(306,'Study',306),
(307,'Visit Number',307),
(401,'Worksheet',401),
(402,'Creation Time',402),
(403,'Comment',403),
(404,'Center',404),
(405,'Activity Status',405),
(406,'Specimen Inventory Id',406),
(407,'Specimen Creation Time',407);
/*!40000 ALTER TABLE `entity_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_filter`
--

DROP TABLE IF EXISTS `entity_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity_filter` (
  `ID` int(11) NOT NULL,
  `FILTER_TYPE` int(11) DEFAULT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `ENTITY_PROPERTY_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),

  KEY `FK635CF541698D6AC` (`ENTITY_PROPERTY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_filter`
--

LOCK TABLES `entity_filter` WRITE;
/*!40000 ALTER TABLE `entity_filter` DISABLE KEYS */;
INSERT INTO `entity_filter` VALUES (1,1,'Inventory Id',1),
(2,3,'Creation Time',2),
(3,1,'Comment',3),
(4,2,'Quantity',4),
(5,1,'Activity Status',5),
(6,1,'Container Product Barcode',7),
(7,1,'Container Label',8),
(8,1,'Specimen Type',9),
(9,3,'Time Processed',10),
(11,1,'Patient Number',12),
(12,4,'Top Container',6),
(13,1,'Current Center',15),
(14,1,'Study',16),
(15,3,'Shipment Time Received',17),
(16,1,'Shipment Waybill',18),
(17,3,'Shipment Time Sent',19),
(18,1,'Shipment Box Number',20),
(19,1,'Source Center',21),
(21,1,'Dispatch Sender',22),
(22,1,'Dispatch Receiver',23),
(23,3,'Dispatch Time Received',24),
(24,3,'Dispatch Time Sent',25),
(25,1,'Dispatch Waybill',26),
(26,1,'Dispatch Box Number',27),
(101,1,'Product Box Number',101),
(102,1,'Comment',102),
(103,1,'Label',103),
(104,2,'Temperature',104),
(105,4,'Top Container',105),
(106,3,'Specimen Creation Time',106),
(107,1,'Container Type',107),
(108,5,'Is Top Level',108),
(109,1,'Site',109),
(201,1,'Patient Number',201),
(202,1,'Study',202),
(203,3,'Specimen Time Processed',203),
(204,3,'Specimen Creation Time',204),
(205,1,'Source Center',205),
(206,6,'First Time Processed',204),
(207,1,'Inventory Id',206),
(208,7,'Visit Number',207),
(301,3,'Specimen Time Processed',301),
(302,3,'Specimen Creation Time',302),
(303,1,'Comment',303),
(304,1,'Patient Number',304),
(305,1,'Specimen Source Center',305),
(306,1,'Study',306),
(307,6,'First Time Processed',301),
(308,7,'Visit Number',307),
(401,1,'Worksheet',401),
(402,3,'Creation Time',402),
(403,1,'Comment',403),
(404,1,'Center',404),
(405,1,'Activity Status',405),
(406,1,'Specimen Inventory Id',406),
(407,3,'Specimen Creation Time',407);
/*!40000 ALTER TABLE `entity_filter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entity_property`
--

DROP TABLE IF EXISTS `entity_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity_property` (
  `ID` int(11) NOT NULL,
  `PROPERTY` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `PROPERTY_TYPE_ID` int(11) NOT NULL,
  `ENTITY_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),

  KEY `FK3FC956B191CFD445` (`ENTITY_ID`),

  KEY `FK3FC956B157C0C3B0` (`PROPERTY_TYPE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entity_property`
--

LOCK TABLES `entity_property` WRITE;
/*!40000 ALTER TABLE `entity_property` DISABLE KEYS */;
INSERT INTO `entity_property` VALUES (1,'inventoryId',1,1),
(2,'createdAt',3,1),
(3,'comment',1,1),
(4,'quantity',2,1),
(5,'activityStatus.name',1,1),
(6,'specimenPosition.container.containerPath.topContainer.id',2,1),
(7,'specimenPosition.container.productBarcode',1,1),
(8,'specimenPosition.container.label',1,1),
(9,'specimenType.nameShort',1,1),
(10,'parentSpecimen.processingEvent.createdAt',3,1),
(12,'collectionEvent.patient.pnumber',1,1),
(13,'specimenPosition.container.containerPath.topContainer.containerType.nameShort',1,1),
(14,'specimenPosition.positionString',1,1),
(15,'currentCenter.nameShort',1,1),
(16,'collectionEvent.patient.study.nameShort',1,1),
(17,'originInfo.shipmentInfo.receivedAt',3,1),
(18,'originInfo.shipmentInfo.waybill',1,1),
(19,'originInfo.shipmentInfo.sentAt',3,1),
(20,'originInfo.shipmentInfo.boxNumber',1,1),
(21,'originInfo.center.nameShort',1,1),
(22,'dispatchSpecimenCollection.dispatch.senderCenter.nameShort',1,1),
(23,'dispatchSpecimenCollection.dispatch.receiverCenter.nameShort',1,1),
(24,'dispatchSpecimenCollection.dispatch.shipmentInfo.receivedAt',3,1),
(25,'dispatchSpecimenCollection.dispatch.shipmentInfo.sentAt',3,1),
(26,'dispatchSpecimenCollection.dispatch.shipmentInfo.waybill',1,1),
(27,'dispatchSpecimenCollection.dispatch.shipmentInfo.boxNumber',1,1),
(28,'collectionEvent.visitNumber',2,1),
(101,'productBarcode',1,2),
(102,'comment',1,2),
(103,'label',1,2),
(104,'temperature',2,2),
(105,'containerPath.topContainer.id',2,2),
(106,'specimenPositionCollection.specimen.createdAt',3,2),
(107,'containerType.nameShort',1,2),
(108,'containerType.topLevel',4,2),
(109,'site.nameShort',1,2),
(110,'containerPath.topContainer.containerType.nameShort',1,2),
(201,'pnumber',1,3),
(202,'study.nameShort',1,3),
(203,'collectionEventCollection.allSpecimenCollection.parentSpecimen.processingEvent.createdAt',3,3),
(204,'collectionEventCollection.allSpecimenCollection.createdAt',3,3),
(205,'collectionEventCollection.allSpecimenCollection.originInfo.center.nameShort',1,3),
(206,'collectionEventCollection.allSpecimenCollection.inventoryId',1,3),
(207,'collectionEventCollection.visitNumber',2,3),
(301,'allSpecimenCollection.parentSpecimen.processingEvent.createdAt',3,4),
(302,'allSpecimenCollection.createdAt',3,4),
(303,'comment',1,4),
(304,'patient.pnumber',1,4),
(305,'allSpecimenCollection.originInfo.center.nameShort',1,4),
(306,'patient.study.nameShort',1,4),
(307,'visitNumber',2,4),
(401,'worksheet',1,5),
(402,'createdAt',3,5),
(403,'comment',1,5),
(404,'center.nameShort',1,5),
(405,'activityStatus.name',1,5),
(406,'specimenCollection.inventoryId',1,5),
(407,'specimenCollection.createdAt',3,5);
/*!40000 ALTER TABLE `entity_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_modifier`
--

DROP TABLE IF EXISTS `property_modifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_modifier` (
  `ID` int(11) NOT NULL,
  `NAME` text COLLATE latin1_general_cs,
  `PROPERTY_MODIFIER` text COLLATE latin1_general_cs,
  `PROPERTY_TYPE_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),

  KEY `FK5DF9160157C0C3B0` (`PROPERTY_TYPE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `property_modifier`
--

LOCK TABLES `property_modifier` WRITE;
/*!40000 ALTER TABLE `property_modifier` DISABLE KEYS */;
INSERT INTO `property_modifier` VALUES (1,'Year','YEAR({value})',3),
(2,'Year, Quarter','CONCAT(YEAR({value}), CONCAT(\'-\', QUARTER({value})))',3),
(3,'Year, Month','CONCAT(YEAR({value}), CONCAT(\'-\', MONTH({value})))',3),
(4,'Year, Week','CONCAT(YEAR({value}), CONCAT(\'-\', WEEK({value})))',3);
/*!40000 ALTER TABLE `property_modifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `property_type`
--

DROP TABLE IF EXISTS `property_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `property_type` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `property_type`
--

LOCK TABLES `property_type` WRITE;
/*!40000 ALTER TABLE `property_type` DISABLE KEYS */;
INSERT INTO `property_type` VALUES (1,'String'),
(2,'Number'),
(3,'Date'),
(4,'Boolean');
/*!40000 ALTER TABLE `property_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
  `DESCRIPTION` text COLLATE latin1_general_cs,
  `USER_ID` int(11) DEFAULT NULL,
  `IS_PUBLIC` bit(1) DEFAULT NULL,
  `IS_COUNT` bit(1) DEFAULT NULL,
  `ENTITY_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),

  KEY `FK8FDF493491CFD445` (`ENTITY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
INSERT INTO `report` VALUES (1,'Specimen Totals per Type','',24,'','',1),
(2,'Specimens per Clinic by Date','Lists all specimens linked in a particular date range, excluding specimens in the SS freezer.',24,'',NULL,1),
(3,'Specimens per Patient by Date','Lists all specimens linked in a particular date range, excluding specimens in the SS freezer.',24,'',NULL,1),
(4,'Specimens by Container','Lists all specimens located in a given bottom-level container.',24,'',NULL,1),
(5,'Specimen Totals per Study and Type','Lists the total number of each specimen by type and study.',24,'','',1),
(6,'Specimen Totals per Study and Source',NULL,24,'','',1),
(7,'Specimens per Study and Source by Date','Displays the total number of specimens per Study and Source Center, grouped by Creation Time in a calendar week/month/quarter/year for given top-level containers.',24,'','',1),
(8,'Specimen Invoicing Report',NULL,24,'','',1),
(10,'Collection Event Invoicing Report',NULL,24,'','',4),
(11,'Collection Events per Study and Clinic by Date','Displays the total number of collection events added per study per clinic grouped by time processed in a calendar week/month/quarter/year.',24,'','',4),
(12,'New Patients per Study and Clinic by Date','Displays the total number of patients added per study per clinic grouped by time processed in a calendar week/month/quarter/year.',24,'','',3),
(13,'Patients per Study by Date','Displays the total number of patients per study with at least one patient visit grouped by date processed in a calendar week/month/quarter/year.',24,'','',3),
(14,'Collection Events per Study by Date','Displays the total number of collection events per study grouped by time processed in a calendar week/month/quarter/year.',24,'','',4),
(15,'Specimen Totals per Study','Displays the total number of specimens per study for given top-level containers.',24,'','',1);
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_column`
--

DROP TABLE IF EXISTS `report_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_column` (
  `ID` int(11) NOT NULL,
  `POSITION` int(11) DEFAULT NULL,
  `COLUMN_ID` int(11) NOT NULL,
  `PROPERTY_MODIFIER_ID` int(11) DEFAULT NULL,
  `REPORT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),

  KEY `FKF0B78C1BE9306A5` (`REPORT_ID`),

  KEY `FKF0B78C1C2DE3790` (`PROPERTY_MODIFIER_ID`),

  KEY `FKF0B78C1A946D8E8` (`COLUMN_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_column`
--

LOCK TABLES `report_column` WRITE;
/*!40000 ALTER TABLE `report_column` DISABLE KEYS */;
INSERT INTO `report_column` VALUES (1,0,8,NULL,1),
(2,4,8,NULL,2),
(3,3,2,NULL,2),
(4,2,11,NULL,2),
(5,1,20,NULL,2),
(6,0,1,NULL,2),
(7,4,8,NULL,3),
(8,3,2,NULL,3),
(9,2,20,NULL,3),
(10,1,11,NULL,3),
(11,0,1,NULL,3),
(12,4,15,NULL,4),
(13,3,11,NULL,4),
(14,2,1,NULL,4),
(15,1,7,NULL,4),
(16,0,13,NULL,4),
(17,1,8,NULL,5),
(18,0,15,NULL,5),
(19,1,20,NULL,6),
(20,0,15,NULL,6),
(21,2,2,3,7),
(22,1,20,NULL,7),
(23,0,15,NULL,7),
(24,2,8,NULL,8),
(25,1,20,NULL,8),
(26,0,15,NULL,8),
(27,1,305,NULL,10),
(28,0,306,NULL,10),
(29,2,301,3,11),
(30,1,305,NULL,11),
(31,0,306,NULL,11),
(32,2,204,3,12),
(33,1,205,NULL,12),
(34,0,202,NULL,12),
(35,1,203,3,13),
(36,0,202,NULL,13),
(37,1,301,3,14),
(38,0,306,NULL,14),
(39,0,15,NULL,15);
/*!40000 ALTER TABLE `report_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_filter`
--

DROP TABLE IF EXISTS `report_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_filter` (
  `ID` int(11) NOT NULL,
  `POSITION` int(11) DEFAULT NULL,
  `OPERATOR` int(11) DEFAULT NULL,
  `ENTITY_FILTER_ID` int(11) NOT NULL,
  `REPORT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),

  KEY `FK13D570E3445CEC4C` (`ENTITY_FILTER_ID`),

  KEY `FK13D570E3BE9306A5` (`REPORT_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_filter`
--

LOCK TABLES `report_filter` WRITE;
/*!40000 ALTER TABLE `report_filter` DISABLE KEYS */;
INSERT INTO `report_filter` VALUES (1,0,102,7,NULL),
(2,0,101,5,NULL),
(3,1,102,7,NULL),
(4,0,101,5,NULL),
(5,1,4,12,NULL),
(6,0,1,2,NULL),
(7,0,1,2,NULL),
(8,0,1,2,NULL),
(9,1,4,12,NULL),
(10,0,101,5,NULL),
(11,1,4,12,NULL),
(12,0,1,2,2),
(13,1,4,12,2),
(14,0,1,2,NULL),
(15,1,4,12,NULL),
(16,0,1,2,3),
(17,1,4,12,3),
(18,0,3,12,4),
(19,1,101,7,4),
(20,0,101,5,1),
(21,1,4,12,1),
(22,0,4,12,NULL),
(23,1,1,2,NULL),
(24,0,4,12,5),
(25,1,1,2,5),
(26,0,3,12,6),
(27,1,1,2,6),
(28,0,1,2,7),
(29,1,3,12,7),
(30,0,4,12,NULL),
(31,1,1,2,NULL),
(32,0,4,12,8),
(33,1,1,2,8),
(34,0,1,302,10),
(35,0,1,301,NULL),
(36,0,1,301,NULL),
(37,0,1,301,11),
(38,0,NULL,206,NULL),
(39,1,1,203,NULL),
(40,0,NULL,206,12),
(41,1,1,203,12),
(42,0,1,203,13),
(43,0,1,301,14),
(44,0,1,2,15),
(45,1,3,12,15);
/*!40000 ALTER TABLE `report_filter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_filter_value`
--

DROP TABLE IF EXISTS `report_filter_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `report_filter_value` (
  `ID` int(11) NOT NULL,
  `POSITION` int(11) DEFAULT NULL,
  `VALUE` text COLLATE latin1_general_cs,
  `SECOND_VALUE` text COLLATE latin1_general_cs,
  `REPORT_FILTER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),

  KEY `FK691EF6F59FFD1CEE` (`REPORT_FILTER_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_filter_value`
--

LOCK TABLES `report_filter_value` WRITE;
/*!40000 ALTER TABLE `report_filter_value` DISABLE KEYS */;
INSERT INTO `report_filter_value` VALUES (1,0,'SS%',NULL,1),
(2,0,'Active',NULL,2),
(3,0,'SS%',NULL,3),
(4,0,'Active',NULL,4),
(5,0,'2799',NULL,21),
(6,0,'2799',NULL,13),
(7,0,'Active',NULL,10),
(8,0,'2799',NULL,17),
(9,0,'Active',NULL,20),
(10,0,'2799',NULL,24),
(11,0,'2799',NULL,32);
/*!40000 ALTER TABLE `report_filter_value` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-03-30 13:52:46

/*****************************************************
 * cleantup and drop tables that are no longer required
 ****************************************************/

-- remove "Worksheet" from event attributes

delete ea from event_attr as ea
       join study_event_attr as sea on sea.id=ea.study_event_attr_id
       where sea.label='Worksheet';

delete from global_event_attr where label='Worksheet';

delete from study_event_attr where label='Worksheet';

drop index pv_id_idx on collection_event;
drop index pv_id_idx on processing_event;
drop index pv_id_idx on specimen;


ALTER TABLE origin_info DROP COLUMN ASHIP_ID, DROP COLUMN ASHIP_TYPE;

ALTER TABLE collection_event DROP COLUMN PV_ID;

ALTER TABLE processing_event DROP COLUMN PV_ID;

ALTER TABLE dispatch DROP COLUMN ASHIP_ID;

ALTER TABLE specimen DROP COLUMN PV_ID, DROP COLUMN PV_SV_ID;

DROP TABLE abstract_shipment;
DROP TABLE aliquot;
DROP TABLE clinic;
DROP TABLE clinic_shipment_patient;
DROP TABLE container_type_sample_type;
DROP TABLE dispatch_info;
DROP TABLE dispatch_info_site;
DROP TABLE dispatch_shipment_aliquot;
DROP TABLE patient_visit;
DROP TABLE pv_attr;
DROP TABLE pv_source_vessel;
DROP TABLE research_group;
DROP TABLE research_group_researcher;
DROP TABLE researcher;
DROP TABLE sample_storage;
DROP TABLE sample_type;
DROP TABLE site;
DROP TABLE source_vessel;
DROP TABLE study_source_vessel;

-- convert all tables to InnoDB

alter table abstract_position engine=InnoDB;
alter table activity_status engine=InnoDB;
alter table address engine=InnoDB;
alter table aliquoted_specimen engine=InnoDB;
alter table capacity engine=InnoDB;
alter table center engine=InnoDB;
alter table collection_event engine=InnoDB;
alter table contact engine=InnoDB;
alter table container engine=InnoDB;
alter table container_labeling_scheme engine=InnoDB;
alter table container_path engine=InnoDB;
alter table container_type engine=InnoDB;
alter table container_type_container_type engine=InnoDB;
alter table container_type_specimen_type engine=InnoDB;
alter table csm_application engine=InnoDB;
alter table dispatch engine=InnoDB;
alter table dispatch_specimen engine=InnoDB;
alter table entity engine=InnoDB;
alter table entity_column engine=InnoDB;
alter table entity_filter engine=InnoDB;
alter table entity_property engine=InnoDB;
alter table event_attr engine=InnoDB;
alter table event_attr_type engine=InnoDB;
alter table global_event_attr engine=InnoDB;
alter table log engine=InnoDB;
alter table origin_info engine=InnoDB;
alter table patient engine=InnoDB;
alter table processing_event engine=InnoDB;
alter table property_modifier engine=InnoDB;
alter table property_type engine=InnoDB;
alter table report engine=InnoDB;
alter table report_column engine=InnoDB;
alter table report_filter engine=InnoDB;
alter table report_filter_value engine=InnoDB;
alter table request engine=InnoDB;
alter table request_specimen engine=InnoDB;
alter table shipment_info engine=InnoDB;
alter table shipping_method engine=InnoDB;
alter table site_study engine=InnoDB;
alter table source_specimen engine=InnoDB;
alter table specimen engine=InnoDB;
alter table specimen_type engine=InnoDB;
alter table specimen_type_specimen_type engine=InnoDB;
alter table study engine=InnoDB;
alter table study_contact engine=InnoDB;
alter table study_event_attr engine=InnoDB;

-- mysql-diff changes to fully convert to InnoDB

ALTER TABLE abstract_position
      ADD CONSTRAINT FKBC4AE0A69BFD88CF FOREIGN KEY FKBC4AE0A69BFD88CF (CONTAINER_ID) REFERENCES container (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKBC4AE0A67366CE44 FOREIGN KEY FKBC4AE0A67366CE44 (PARENT_CONTAINER_ID) REFERENCES container (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKBC4AE0A6EF199765 FOREIGN KEY FKBC4AE0A6EF199765 (SPECIMEN_ID) REFERENCES specimen (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE aliquoted_specimen
      ADD CONSTRAINT FK75EACAC138445996 FOREIGN KEY FK75EACAC138445996 (SPECIMEN_TYPE_ID) REFERENCES specimen_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK75EACAC1C449A4 FOREIGN KEY FK75EACAC1C449A4 (ACTIVITY_STATUS_ID) REFERENCES activity_status (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK75EACAC1F2A2464F FOREIGN KEY FK75EACAC1F2A2464F (STUDY_ID) REFERENCES study (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE center
      ADD CONSTRAINT FK7645C0556AF2992F FOREIGN KEY FK7645C0556AF2992F (ADDRESS_ID) REFERENCES address (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK7645C055C449A4 FOREIGN KEY FK7645C055C449A4 (ACTIVITY_STATUS_ID) REFERENCES activity_status (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK7645C055F2A2464F FOREIGN KEY FK7645C055F2A2464F (STUDY_ID) REFERENCES study (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE collection_event
      ADD CONSTRAINT FKEDAD8999B563F38F FOREIGN KEY FKEDAD8999B563F38F (PATIENT_ID) REFERENCES patient (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKEDAD8999C449A4 FOREIGN KEY FKEDAD8999C449A4 (ACTIVITY_STATUS_ID) REFERENCES activity_status (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE contact
      ADD CONSTRAINT FK6382B00057F87A25 FOREIGN KEY FK6382B00057F87A25 (CLINIC_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE container
      ADD CONSTRAINT FK8D995C613F52C885 FOREIGN KEY FK8D995C613F52C885 (SITE_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK8D995C61AC528270 FOREIGN KEY FK8D995C61AC528270 (POSITION_ID) REFERENCES abstract_position (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK8D995C61B3E77A12 FOREIGN KEY FK8D995C61B3E77A12 (CONTAINER_TYPE_ID) REFERENCES container_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK8D995C61C449A4 FOREIGN KEY FK8D995C61C449A4 (ACTIVITY_STATUS_ID) REFERENCES activity_status (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE container_path
      ADD CONSTRAINT FKB2C64D431BE0C379 FOREIGN KEY FKB2C64D431BE0C379 (TOP_CONTAINER_ID) REFERENCES container (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKB2C64D439BFD88CF FOREIGN KEY FKB2C64D439BFD88CF (CONTAINER_ID) REFERENCES container (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE container_type
      ADD CONSTRAINT FKB2C878581764E225 FOREIGN KEY FKB2C878581764E225 (CAPACITY_ID) REFERENCES capacity (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKB2C878583F52C885 FOREIGN KEY FKB2C878583F52C885 (SITE_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKB2C878585D63DFF0 FOREIGN KEY FKB2C878585D63DFF0 (CHILD_LABELING_SCHEME_ID) REFERENCES container_labeling_scheme (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKB2C87858C449A4 FOREIGN KEY FKB2C87858C449A4 (ACTIVITY_STATUS_ID) REFERENCES activity_status (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE container_type_container_type
      ADD CONSTRAINT FK5991B31F371DC9AF FOREIGN KEY FK5991B31F371DC9AF (CHILD_CONTAINER_TYPE_ID) REFERENCES container_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK5991B31F9C2855BD FOREIGN KEY FK5991B31F9C2855BD (PARENT_CONTAINER_TYPE_ID) REFERENCES container_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE container_type_specimen_type
      ADD CONSTRAINT FKE2F4C26A38445996 FOREIGN KEY FKE2F4C26A38445996 (SPECIMEN_TYPE_ID) REFERENCES specimen_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKE2F4C26AB3E77A12 FOREIGN KEY FKE2F4C26AB3E77A12 (CONTAINER_TYPE_ID) REFERENCES container_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE dispatch
      ADD CONSTRAINT FK3F9F347A91BC3D7B FOREIGN KEY FK3F9F347A91BC3D7B (SENDER_CENTER_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK3F9F347A307B2CB5 FOREIGN KEY FK3F9F347A307B2CB5 (RECEIVER_CENTER_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK3F9F347AA2F14F4F FOREIGN KEY FK3F9F347AA2F14F4F (REQUEST_ID) REFERENCES request (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK3F9F347AF59D873A FOREIGN KEY FK3F9F347AF59D873A (SHIPMENT_INFO_ID) REFERENCES shipment_info (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE dispatch_specimen
      ADD CONSTRAINT FKEE25592DDE99CA25 FOREIGN KEY FKEE25592DDE99CA25 (DISPATCH_ID) REFERENCES dispatch (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKEE25592DEF199765 FOREIGN KEY FKEE25592DEF199765 (SPECIMEN_ID) REFERENCES specimen (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE entity_column
      ADD CONSTRAINT FK16BD7321698D6AC FOREIGN KEY FK16BD7321698D6AC (ENTITY_PROPERTY_ID) REFERENCES entity_property (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE entity_filter
      ADD CONSTRAINT FK635CF541698D6AC FOREIGN KEY FK635CF541698D6AC (ENTITY_PROPERTY_ID) REFERENCES entity_property (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE entity_property
      ADD CONSTRAINT FK3FC956B157C0C3B0 FOREIGN KEY FK3FC956B157C0C3B0 (PROPERTY_TYPE_ID) REFERENCES property_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK3FC956B191CFD445 FOREIGN KEY FK3FC956B191CFD445 (ENTITY_ID) REFERENCES entity (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE event_attr
      ADD CONSTRAINT FK59508C96A9CFCFDB FOREIGN KEY FK59508C96A9CFCFDB (STUDY_EVENT_ATTR_ID) REFERENCES study_event_attr (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK59508C96280272F2 FOREIGN KEY FK59508C96280272F2 (COLLECTION_EVENT_ID) REFERENCES collection_event (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE global_event_attr
      ADD CONSTRAINT FKBE7ED6B25B770B31 FOREIGN KEY FKBE7ED6B25B770B31 (EVENT_ATTR_TYPE_ID) REFERENCES event_attr_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE origin_info
      ADD CONSTRAINT FKE92E7A27F59D873A FOREIGN KEY FKE92E7A27F59D873A (SHIPMENT_INFO_ID) REFERENCES shipment_info (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKE92E7A2792FAA705 FOREIGN KEY FKE92E7A2792FAA705 (CENTER_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE patient
      ADD CONSTRAINT FKFB9F76E5F2A2464F FOREIGN KEY FKFB9F76E5F2A2464F (STUDY_ID) REFERENCES study (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE processing_event
      ADD CONSTRAINT FK327B1E4E92FAA705 FOREIGN KEY FK327B1E4E92FAA705 (CENTER_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK327B1E4EC449A4 FOREIGN KEY FK327B1E4EC449A4 (ACTIVITY_STATUS_ID) REFERENCES activity_status (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE property_modifier
      ADD CONSTRAINT FK5DF9160157C0C3B0 FOREIGN KEY FK5DF9160157C0C3B0 (PROPERTY_TYPE_ID) REFERENCES property_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE report
      ADD CONSTRAINT FK8FDF493491CFD445 FOREIGN KEY FK8FDF493491CFD445 (ENTITY_ID) REFERENCES entity (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE report_column
      ADD CONSTRAINT FKF0B78C1A946D8E8 FOREIGN KEY FKF0B78C1A946D8E8 (COLUMN_ID) REFERENCES entity_column (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKF0B78C1BE9306A5 FOREIGN KEY FKF0B78C1BE9306A5 (REPORT_ID) REFERENCES report (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKF0B78C1C2DE3790 FOREIGN KEY FKF0B78C1C2DE3790 (PROPERTY_MODIFIER_ID) REFERENCES property_modifier (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE report_filter
      ADD CONSTRAINT FK13D570E3BE9306A5 FOREIGN KEY FK13D570E3BE9306A5 (REPORT_ID) REFERENCES report (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK13D570E3445CEC4C FOREIGN KEY FK13D570E3445CEC4C (ENTITY_FILTER_ID) REFERENCES entity_filter (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE report_filter_value
      ADD CONSTRAINT FK691EF6F59FFD1CEE FOREIGN KEY FK691EF6F59FFD1CEE (REPORT_FILTER_ID) REFERENCES report_filter (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE request
      ADD CONSTRAINT FK6C1A7E6F6AF2992F FOREIGN KEY FK6C1A7E6F6AF2992F (ADDRESS_ID) REFERENCES address (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK6C1A7E6FF2A2464F FOREIGN KEY FK6C1A7E6FF2A2464F (STUDY_ID) REFERENCES study (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE request_specimen
      ADD CONSTRAINT FK579572D8EF199765 FOREIGN KEY FK579572D8EF199765 (SPECIMEN_ID) REFERENCES specimen (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK579572D8D990A70 FOREIGN KEY FK579572D8D990A70 (AREQUEST_ID) REFERENCES request (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE shipment_info
      ADD CONSTRAINT FK95BCA433DCA49682 FOREIGN KEY FK95BCA433DCA49682 (SHIPPING_METHOD_ID) REFERENCES shipping_method (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE site_study
      ADD CONSTRAINT FK7A197EB13F52C885 FOREIGN KEY FK7A197EB13F52C885 (SITE_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK7A197EB1F2A2464F FOREIGN KEY FK7A197EB1F2A2464F (STUDY_ID) REFERENCES study (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE source_specimen
      ADD CONSTRAINT FK28D36AC38445996 FOREIGN KEY FK28D36AC38445996 (SPECIMEN_TYPE_ID) REFERENCES specimen_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK28D36ACF2A2464F FOREIGN KEY FK28D36ACF2A2464F (STUDY_ID) REFERENCES study (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE specimen
      ADD CONSTRAINT FKAF84F308280272F2 FOREIGN KEY FKAF84F308280272F2 (COLLECTION_EVENT_ID) REFERENCES collection_event (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKAF84F30838445996 FOREIGN KEY FKAF84F30838445996 (SPECIMEN_TYPE_ID) REFERENCES specimen_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKAF84F308C449A4 FOREIGN KEY FKAF84F308C449A4 (ACTIVITY_STATUS_ID) REFERENCES activity_status (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKAF84F30886857784 FOREIGN KEY FKAF84F30886857784 (ORIGINAL_COLLECTION_EVENT_ID) REFERENCES collection_event (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKAF84F308FBB79BBF FOREIGN KEY FKAF84F308FBB79BBF (CURRENT_CENTER_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKAF84F30833126C8 FOREIGN KEY FKAF84F30833126C8 (PROCESSING_EVENT_ID) REFERENCES processing_event (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKAF84F30861674F50 FOREIGN KEY FKAF84F30861674F50 (PARENT_SPECIMEN_ID) REFERENCES specimen (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKAF84F30812E55F12 FOREIGN KEY FKAF84F30812E55F12 (ORIGIN_INFO_ID) REFERENCES origin_info (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKAF84F308C9EF5F7B FOREIGN KEY FKAF84F308C9EF5F7B (TOP_SPECIMEN_ID) REFERENCES specimen (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE specimen_type_specimen_type
      ADD CONSTRAINT FKD9584463D9672259 FOREIGN KEY FKD9584463D9672259 (CHILD_SPECIMEN_TYPE_ID) REFERENCES specimen_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKD95844635F3DC8B FOREIGN KEY FKD95844635F3DC8B (PARENT_SPECIMEN_TYPE_ID) REFERENCES specimen_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE study_contact
      ADD CONSTRAINT FKAA13B36AA07999AF FOREIGN KEY FKAA13B36AA07999AF (CONTACT_ID) REFERENCES contact (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FKAA13B36AF2A2464F FOREIGN KEY FKAA13B36AF2A2464F (STUDY_ID) REFERENCES study (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE study
      ADD CONSTRAINT FK4B915A9C449A4 FOREIGN KEY FK4B915A9C449A4 (ACTIVITY_STATUS_ID) REFERENCES activity_status (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE study_event_attr
      ADD CONSTRAINT FK3EACD8EC5B770B31 FOREIGN KEY FK3EACD8EC5B770B31 (EVENT_ATTR_TYPE_ID) REFERENCES event_attr_type (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK3EACD8ECC449A4 FOREIGN KEY FK3EACD8ECC449A4 (ACTIVITY_STATUS_ID) REFERENCES activity_status (ID) ON UPDATE NO ACTION ON DELETE NO ACTION,
      ADD CONSTRAINT FK3EACD8ECF2A2464F FOREIGN KEY FK3EACD8ECF2A2464F (STUDY_ID) REFERENCES study (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;
