# this script should be run on the bbpdb after is has been imported from MS
# Access

alter table patient add dec_chr_nr varchar(32);

alter table patient_visit add bb2_pv_id int(11);

CREATE TABLE FRZ_99_INV_ID (
       INVENTORY_ID varchar(255),
       index(INVENTORY_ID)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO FRZ_99_INV_ID (INVENTORY_ID)
SELECT distinct inventory_id FROM freezer where fnum=99 and inventory_id is not null;
