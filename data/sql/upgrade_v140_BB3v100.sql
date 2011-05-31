/*------------------------------------------------------------------------------
 *
 *  MySQL upgrade script for model version BB2 v1.4.0 to BB3 v1.5.0
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

insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10mL lavender top EDTA tube'),
	(select id from specimen_type where name='Whole Blood EDTA'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top Lithium Heparin tube'),
	(select id from specimen_type where name='DNA L 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top Lithium Heparin tube'),
	(select id from specimen_type where name='Heparin Blood'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top Lithium Heparin tube'),
	(select id from specimen_type where name='LH PFP 200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top Lithium Heparin tube'),
	(select id from specimen_type where name='LH PFP 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top Lithium Heparin tube'),
	(select id from specimen_type where name='Lithium Heparin Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top Lithium Heparin tube'),
	(select id from specimen_type where name='PlasmaL200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top Lithium Heparin tube'),
	(select id from specimen_type where name='PlasmaL500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='Cord Blood Mononuclear Cells'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='Heparin Blood'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='Plasma (Na Heparin) - DAD'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='RNA CBMC'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='WB - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='WB Plasma - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='WB RNA - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml green top sodium heparin tube'),
	(select id from specimen_type where name='WB Serum - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='10ml orange top PAXgene tube'),
	(select id from specimen_type where name='Paxgene800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='15ml centrifuge tube (sodium azide urine)'),
	(select id from specimen_type where name='SodiumAzideUrine'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='15ml centrifuge tube (sodium azide urine)'),
	(select id from specimen_type where name='UrineSA700'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='15ml centrifuge tube (sodium azide urine)'),
	(select id from specimen_type where name='UrineSA900'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL lavender top EDTA tube'),
	(select id from specimen_type where name='Whole Blood EDTA'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3mL red top tube (hemodialysate)'),
	(select id from specimen_type where name='Hemodialysate'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3ml lavender top EDTA tube w BHT and Desferal'),
	(select id from specimen_type where name='PlasmaE BHT200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='3ml red top tube (source water)'),
	(select id from specimen_type where name='Source Water'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml gold top serum tube'),
	(select id from specimen_type where name='SerumG200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml gold top serum tube'),
	(select id from specimen_type where name='SerumG400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml gold top serum tube'),
	(select id from specimen_type where name='SerumG500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='Cord Blood Mononuclear Cells'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='Heparin Blood'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='Plasma (Na Heparin) - DAD'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='RNA CBMC'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='WB - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='WB Plasma - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='WB RNA - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml green top sodium heparin BD 367871'),
	(select id from specimen_type where name='WB Serum - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='4ml lavender top EDTA tube'),
	(select id from specimen_type where name='Whole Blood EDTA'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='5mL gold top serum tube'),
	(select id from specimen_type where name='SerumG200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='5mL gold top serum tube'),
	(select id from specimen_type where name='SerumG400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='5mL gold top serum tube'),
	(select id from specimen_type where name='SerumG500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL beige top tube'),
	(select id from specimen_type where name='Serum (Beige top)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL beige top tube'),
	(select id from specimen_type where name='SerumB400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL beige top tube'),
	(select id from specimen_type where name='SerumB900'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6mL lavender top EDTA tube'),
	(select id from specimen_type where name='Whole Blood EDTA'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6ml beige top tube (tap water)'),
	(select id from specimen_type where name='Tap Water'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6ml light green top lithium heparin tube'),
	(select id from specimen_type where name='DNA L 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6ml light green top lithium heparin tube'),
	(select id from specimen_type where name='Heparin Blood'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6ml light green top lithium heparin tube'),
	(select id from specimen_type where name='LH PFP 200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6ml light green top lithium heparin tube'),
	(select id from specimen_type where name='LH PFP 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6ml light green top lithium heparin tube'),
	(select id from specimen_type where name='Lithium Heparin Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6ml light green top lithium heparin tube'),
	(select id from specimen_type where name='PlasmaL200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='6ml light green top lithium heparin tube'),
	(select id from specimen_type where name='PlasmaL500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='7ml EDTA conventional top'),
	(select id from specimen_type where name='Whole Blood EDTA'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='8.5ml P100 orange top tube'),
	(select id from specimen_type where name='P100 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='9ml CPDA yellow top tube'),
	(select id from specimen_type where name='CDPA Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Biopsy, RNA later'),
	(select id from specimen_type where name='RNAlater Biopsies'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='CHILD Meconium'),
	(select id from specimen_type where name='Meconium - BABY'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Colonoscopy Kit'),
	(select id from specimen_type where name='Ascending Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Colonoscopy Kit'),
	(select id from specimen_type where name='Descending Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Colonoscopy Kit'),
	(select id from specimen_type where name='Ileum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Colonoscopy Kit'),
	(select id from specimen_type where name='RNA-Ascending Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Colonoscopy Kit'),
	(select id from specimen_type where name='RNA-Descending Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Colonoscopy Kit'),
	(select id from specimen_type where name='RNA-Ileum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Colonoscopy Kit'),
	(select id from specimen_type where name='RNA-Transverse Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Colonoscopy Kit'),
	(select id from specimen_type where name='Transverse Colon'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='Buffy coat'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='Cells500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='DNA (Blood)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='DNA (White blood cells)'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='DNA E 1000'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='DNA E 500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='Plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='PlasmaE200'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='PlasmaE250'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='PlasmaE400'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='PlasmaE500'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='PlasmaE800'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='Platelet free plasma'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='WB DMSO'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='EDTA cryovial'),
	(select id from specimen_type where name='Whole Blood EDTA'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Enteroscopy Kit'),
	(select id from specimen_type where name='Ileum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Enteroscopy Kit'),
	(select id from specimen_type where name='Jejunum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Enteroscopy Kit'),
	(select id from specimen_type where name='RNA-Ileum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Enteroscopy Kit'),
	(select id from specimen_type where name='RNA-Jejunum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Gastroscopy Kit'),
	(select id from specimen_type where name='Duodenum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Gastroscopy Kit'),
	(select id from specimen_type where name='RNA-Duodenum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Gastroscopy Kit'),
	(select id from specimen_type where name='RNA-Stomach, Antrum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Gastroscopy Kit'),
	(select id from specimen_type where name='RNA-Stomach, Body'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Gastroscopy Kit'),
	(select id from specimen_type where name='Stomach, Antrum'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Gastroscopy Kit'),
	(select id from specimen_type where name='Stomach, Body'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Unknown / import'),
	(select id from specimen_type where name='RNA-adjacent diseased biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Unknown / import'),
	(select id from specimen_type where name='RNA-diseased biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Unknown / import'),
	(select id from specimen_type where name='RNA-normal biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Unknown / import'),
	(select id from specimen_type where name='RNA-normal left biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Unknown / import'),
	(select id from specimen_type where name='RNA-normal rectum biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='Unknown / import'),
	(select id from specimen_type where name='RNA-normal right biopsy'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='fingernail tube'),
	(select id from specimen_type where name='Finger Nails'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='hair bagette'),
	(select id from specimen_type where name='Hair'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='toenail tube'),
	(select id from specimen_type where name='Toe Nails'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='urine cup'),
	(select id from specimen_type where name='Centrifuged Urine'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='urine cup'),
	(select id from specimen_type where name='Filtered Urine'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='urine cup'),
	(select id from specimen_type where name='SodiumAzideUrine'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='urine cup'),
	(select id from specimen_type where name='Urine'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='urine cup'),
	(select id from specimen_type where name='UrineC900'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='urine cup'),
	(select id from specimen_type where name='UrineSA700'));
insert into specimen_type_specimen_type (parent_specimen_type_id,child_specimen_type_id)
	values ((select id from specimen_type where name='urine cup'),
	(select id from specimen_type where name='UrineSA900'));

/*****************************************************
 * Source specimen
 ****************************************************/

ALTER TABLE aliquoted_specimen
    ADD COLUMN source_specimen_id INT(11) NOT NULL;


update aliquoted_specimen als
       set als.source_specimen_id=(select ss.id from source_specimen as ss where als.study_id = ss.study_id 
and ss.specimen_type_id in 
(select stst.parent_specimen_type_id 
from specimen_type as child join specimen_type_specimen_type as stst on child.id = stst.child_specimen_type_id 
where child.id = als.specimen_type_id) limit 1)
where als.id not in (2,5, 19, 33,35,40,44,47,51,53,64,73,80,96,102,103,104,107,109,110,113,126,129,131,132,134,163,208,210,211,212,213,214,215,218,219,220)


