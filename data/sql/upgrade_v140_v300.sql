/*------------------------------------------------------------------------------
 *
 *  MySQL upgrade script for model version BioBank v1.4.0 to v3.0.0
 *
 *----------------------------------------------------------------------------*/

ALTER TABLE dispatch
      MODIFY COLUMN RECEIVER_CENTER_ID INT(11) NOT NULL,
      MODIFY COLUMN SENDER_CENTER_ID INT(11) NOT NULL;

ALTER TABLE dispatch_specimen
      MODIFY COLUMN SPECIMEN_ID INT(11) NOT NULL,
      MODIFY COLUMN DISPATCH_ID INT(11) NOT NULL;

ALTER TABLE request_specimen
      MODIFY COLUMN SPECIMEN_ID INT(11) NOT NULL,
      MODIFY COLUMN AREQUEST_ID INT(11) NOT NULL;

ALTER TABLE request
      ADD COLUMN REQUESTER_ID INT(11) NOT NULL COMMENT '',
      ADD INDEX FK6C1A7E6F80AB67E (REQUESTER_ID),
      ADD CONSTRAINT FK6C1A7E6F80AB67E FOREIGN KEY FK6C1A7E6F80AB67E (REQUESTER_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

/*****************************************************
 * Timezones
 ****************************************************/

update log set created_at = convert_tz(created_at, 'Canada/Mountain', 'GMT');
update patient set created_at = convert_tz(created_at, 'Canada/Mountain', 'GMT');
update processing_event set created_at = convert_tz(created_at, 'Canada/Mountain', 'GMT');
update shipment_info set packed_at = convert_tz(packed_at, 'Canada/Mountain', 'GMT');
update shipment_info set received_at = convert_tz(received_at, 'Canada/Mountain', 'GMT');
update specimen set created_at = convert_tz(created_at, 'Canada/Mountain', 'GMT');

/*****************************************************
 * Container
 ****************************************************/

ALTER TABLE container
      ADD COLUMN TOP_CONTAINER_ID INT(11) NULL DEFAULT NULL,
      ADD COLUMN PATH varchar(255) COLLATE latin1_general_cs DEFAULT NULL,
      ADD INDEX PATH_IDX (PATH),
      ADD INDEX FK8D995C611BE0C379 (TOP_CONTAINER_ID),
      ADD CONSTRAINT FK8D995C611BE0C379 FOREIGN KEY FK8D995C611BE0C379 (TOP_CONTAINER_ID) REFERENCES container (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

update container c, container_path cp
       set c.path=if(locate('/',cp.path) > 0,
           substr(cp.path,1,length(cp.path) - locate('/',reverse(cp.path))),''),
       c.top_container_id=cp.top_container_id
       where c.id=cp.container_id;

DROP TABLE IF EXISTS container_path;

/*****************************************************
 * Printer Labels
 ****************************************************/

CREATE TABLE printed_ss_inv_item (
    ID INT(11) NOT NULL,
    VERSION INT(11) NOT NULL,
    TXT VARCHAR(15) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE printer_label_template (
    ID INT(11) NOT NULL,
    VERSION INT(11) NOT NULL,
    NAME VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    PRINTER_NAME VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    CONFIG_DATA TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    JASPER_TEMPLATE_ID INT(11) NOT NULL COMMENT '',
    CONSTRAINT NAME UNIQUE KEY(NAME),
    PRIMARY KEY (ID),
    INDEX FKC6463C6AA4B878C8 (JASPER_TEMPLATE_ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

CREATE TABLE jasper_template (
    ID INT(11) NOT NULL,
    VERSION INT(11) NOT NULL,
    NAME VARCHAR(50) CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    XML TEXT CHARACTER SET latin1 COLLATE latin1_general_cs NULL DEFAULT NULL,
    CONSTRAINT NAME UNIQUE KEY(NAME),
    PRIMARY KEY (ID)
) ENGINE=InnoDB COLLATE=latin1_general_cs;

ALTER TABLE printer_label_template
    ADD CONSTRAINT FKC6463C6AA4B878C8 FOREIGN KEY FKC6463C6AA4B878C8 (JASPER_TEMPLATE_ID) REFERENCES jasper_template (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

/*****************************************************
 * Origin info
 ****************************************************/

ALTER TABLE origin_info
      ADD COLUMN RECEIVER_SITE_ID INT(11) NULL DEFAULT NULL COMMENT '',
      ADD INDEX FKE92E7A275598FA35 (RECEIVER_SITE_ID),
      ADD CONSTRAINT FKE92E7A275598FA35 FOREIGN KEY FKE92E7A275598FA35 (RECEIVER_SITE_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION;

update origin_info oi, shipment_info si, specimen spc
       set oi.receiver_site_id=spc.current_center_id
       where oi.id=spc.origin_info_id
       and si.id=oi.shipment_info_id;


/*****************************************************
 * Source specimen
 ****************************************************/

ALTER TABLE source_specimen
    DROP COLUMN need_time_drawn;

/*****************************************************
 * Container labeling scheme
 ****************************************************/
INSERT INTO `CONTAINER_LABELING_SCHEME` (ID, NAME, MIN_CHARS, MAX_CHARS, MAX_ROWS, MAX_COLS, MAX_CAPACITY, VERSION) VALUES
( 6, "2 char alphabetic",      2, 2, null, null, 676, 0);

/*****************************************************
 * Specimen Hierarchy
 ****************************************************/

ALTER TABLE specimen_type_specimen_type
      ADD PRIMARY KEY (PARENT_SPECIMEN_TYPE_ID, CHILD_SPECIMEN_TYPE_ID);

set @ss = null;

select id from specimen_type where name='10mL lavender top EDTA tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Whole Blood EDTA'));

select id from specimen_type where name='10ml green top Lithium Heparin tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,
	(select id from specimen_type where name='DNA L 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Heparin Blood'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='LH PFP 200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='LH PFP 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Lithium Heparin Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='PlasmaL200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='PlasmaL500'));

select id from specimen_type where name='10ml green top sodium heparin tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Cord Blood Mononuclear Cells'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Heparin Blood'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Plasma (Na Heparin) - DAD'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='RNA CBMC'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='WB - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='WB Plasma - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='WB RNA - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='WB Serum - BABY'));

select id from specimen_type where name='10ml orange top PAXgene tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='Paxgene800'));

select id from specimen_type where name='15ml centrifuge tube (sodium azide urine)' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='SodiumAzideUrine'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='UrineSA700'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss, (select id from specimen_type where name='UrineSA900'));

select id from specimen_type where name='3mL lavender top EDTA tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Whole Blood EDTA'));

select id from specimen_type where name='3mL red top tube (hemodialysate)' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Hemodialysate'));

select id from specimen_type where name='3ml lavender top EDTA tube w BHT and Desferal' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE BHT200'));

select id from specimen_type where name='3ml red top tube (source water)' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Source Water'));

select id from specimen_type where name='4ml gold top serum tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='SerumG200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='SerumG400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='SerumG500'));

select id from specimen_type where name='4ml green top sodium heparin BD 367871' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Cord Blood Mononuclear Cells'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Heparin Blood'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Plasma (Na Heparin) - DAD'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA CBMC'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='WB - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='WB Plasma - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='WB RNA - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='WB Serum - BABY'));

select id from specimen_type where name='4ml lavender top EDTA tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Whole Blood EDTA'));

select id from specimen_type where name='5mL gold top serum tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='SerumG200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='SerumG400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='SerumG500'));

select id from specimen_type where name='6mL beige top tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Serum (Beige top)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='SerumB400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='SerumB900'));

select id from specimen_type where name='6mL lavender top EDTA tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Whole Blood EDTA'));

select id from specimen_type where name='6ml beige top tube (tap water)' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Tap Water'));

select id from specimen_type where name='6ml light green top lithium heparin tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA L 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Heparin Blood'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='LH PFP 200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='LH PFP 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Lithium Heparin Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaL200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaL500'));

select id from specimen_type where name='7ml EDTA conventional top' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Whole Blood EDTA'));

select id from specimen_type where name='8.5ml P100 orange top tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='P100 500'));

select id from specimen_type where name='9ml CPDA yellow top tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='CDPA Plasma'));

select id from specimen_type where name='Biopsy, RNA later' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNAlater Biopsies'));

select id from specimen_type where name='CHILD Meconium' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Meconium - BABY'));

select id from specimen_type where name='Colonoscopy Kit' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Ascending Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Descending Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Ileum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-Ascending Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-Descending Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-Ileum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-Transverse Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Transverse Colon'));

select id from specimen_type where name='EDTA cryovial' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Whole Blood EDTA'));

select id from specimen_type where name='Enteroscopy Kit' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Ileum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Jejunum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-Ileum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-Jejunum'));

select id from specimen_type where name='Gastroscopy Kit' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Duodenum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-Duodenum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-Stomach, Antrum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-Stomach, Body'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Stomach, Antrum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Stomach, Body'));

select id from specimen_type where name='Unknown / import' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-adjacent diseased biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-diseased biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-normal biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-normal left biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-normal rectum biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='RNA-normal right biopsy'));

select id from specimen_type where name='fingernail tube' into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Finger Nails'));

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='hair bagette'),
	(select id from specimen_type where name='Hair'));

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='toenail tube'),
	(select id from specimen_type where name='Toe Nails'));

select id from specimen_type where name='urine cup'	into @ss;

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Centrifuged Urine'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Filtered Urine'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='SodiumAzideUrine'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='Urine'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='UrineC900'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='UrineSA700'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values (@ss,(select id from specimen_type where name='UrineSA900'));

