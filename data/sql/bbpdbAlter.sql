# this script should be run on the bbpdb after is has been imported from MS
# Access

alter table patient add dec_chr_nr varchar(32);

CREATE TABLE FRZ_INV_ID_CNT (
       INVENTORY_ID varchar(255),
       CNT int unsigned not null,
       index(INVENTORY_ID)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO FRZ_INV_ID_CNT (INVENTORY_ID, CNT)
SELECT inventory_id, count(*) AS cnt FROM freezer GROUP BY inventory_id;
