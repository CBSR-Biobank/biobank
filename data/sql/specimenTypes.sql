LOCK TABLES `SPECIMEN_TYPE` WRITE;
INSERT INTO `SPECIMEN_TYPE` (ID, NAME, NAME_SHORT, VERSION) VALUES
(01, "Ascending Colon",              "Colon, A",        0),
(02, "Buffy coat",                   "BC",              0),
(03, "CDPA Plasma",                  "CDPA Plasma",     0),
(04, "Cells",                        "Cells",           0),
(05, "Centrifuged Urine",            "C Urine",         0),
(06, "Cord Blood Mononuclear Cells", "CBMC",            0),
(07, "DNA (Blood)",                  "DNA(Blood)",      0),
(08, "DNA (White blood cells)",      "DNA (WBC)",       0),
(09, "Descending Colon",             "Colon, D",        0),
(10, "Duodenum",                     "Duodenum",        0),
(11, "Filtered Urine",               "F Urine",         0),
(12, "Finger Nails",                 "F Nails",         0),
(13, "Hair",                         "Hair",            0),
(14, "Hemodialysate",                "Dialysate",       0),
(15, "Heparin Blood",                "HB",              0),
(16, "Ileum",                        "Ileum",           0),
(17, "Jejunum",                      "Jejunum",         0),
(18, "Lithium Heparin Plasma",       "Lith Hep Plasma", 0),
(19, "Meconium - BABY",              "Meconium",        0),
(20, "Paxgene",                      "Paxgene",         0),
(21, "Peritoneal Dialysate",         "Effluent",        0),
(22, "Plasma (Na Heparin) - DAD",    "Plasma SH",       0),
(23, "Plasma",                       "Plasma",          0),
(24, "Platelet free plasma",         "PF Plasma",       0),
(25, "RNA CBMC",                     "CBMC RNA",        0),
(27, "RNA-Ascending Colon",          "R-ColonA",        0),
(28, "RNA-Descending Colon",         "R-ColonD",        0),
(29, "RNA-Duodenum",                 "R-Duodenum",      0),
(30, "RNA-Ileum",                    "R-Ilieum",        0),
(31, "RNA-Jejunum",                  "R-Jejunum",       0),
(32, "RNA-Stomach, Antrum",          "R-StomachA",      0),
(33, "RNA-Stomach, Body",            "R-StomachB",      0),
(34, "RNA-Transverse Colon",         "R-ColonT",        0),
(35, "RNAlater Biopsies",            "RNA Biopsy",      0),
(36, "Serum (Beige top)",            "Serum B",         0),
(37, "Serum",                        "Serum",           0),
(38, "SerumPellet - BABY",           "Serum Pel",       0),
(39, "Sodium Azide Urine",           "Z Urine",         0),
(40, "Source Water",                 "S Water",         0),
(41, "Stomach, Antrum",              "Stomach, A",      0),
(42, "Stomach, Body",                "Stomach, B",      0),
(43, "Tap Water",                    "T Water",         0),
(44, "Toe Nails",                    "T Nails",         0),
(45, "Transverse Colon",             "Colon, T",        0),
(46, "Urine",                        "Urine",           0),
(47, "WB - BABY",                    "WBlood",          0),
(48, "WB DMSO",                      "WB DMSO",         0),
(49, "WB Plasma - BABY",             "WB Plasma",       0),
(50, "WB RNA - BABY",                "WB RNA",          0),
(51, "WB Serum - BABY",              "WB Serum",        0),
(52, "Whole Blood EDTA",             "WBE",             0),
(53, "LH PFP 200",                   "LH PFP 200",      0),
(54, "UrineC900",                    "UrineC900",       0),
(55, "PlasmaE800",                   "PlasmaE800",      0),
(56, "P100 500",                     "P100 500",        0),
(57, "PlasmaL500",                   "PlasmaL500",      0),
(58, "LH PFP 500",                   "LH PFP 500",      0),
(59, "PlasmaE200",                   "PlasmaE200",      0),
(60, "DNA L 1000",                   "DNA L 1000",      0),
(61, "SerumG500",                    "SerumG500",       0),
(62, "PlasmaL200",                   "PlasmaL200",      0),
(63, "DNA E 1000",                   "DNA E 1000",      0),
(64, "PlasmaE500",                   "PlasmaE500",      0),
(65, "UrineSA900",                   "UrineSA900",      0),
(66, "PlasmaE250",                   "PlasmaE250",      0),
(67, "UrineSA700",                   "UrineSA700",      0);

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

UNLOCK TABLES;

