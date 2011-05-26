LOCK TABLES `specimen_type` WRITE;
/*!40000 ALTER TABLE `specimen_type` DISABLE KEYS */;
INSERT INTO `specimen_type` (id,name,name_short,version) VALUES
(1,'Ascending Colon','Colon, A',0),
(2,'Buffy coat','BC',0),
(3,'CDPA Plasma','CDPA Plasma',0),
(4,'Cells500','Cells500',0),
(5,'Centrifuged Urine','C Urine',0),
(6,'Cord Blood Mononuclear Cells','CBMC',0),
(7,'DNA (Blood)','DNA(Blood)',0),
(8,'DNA (White blood cells)','DNA (WBC)',0),
(9,'Descending Colon','Colon, D',0),
(10,'Duodenum','Duodenum',0),
(11,'Filtered Urine','F Urine',0),
(12,'Finger Nails','F Nails',0),
(13,'Hair','Hair',0),
(14,'Hemodialysate','Dialysate',0),
(15,'Heparin Blood','HB',0),
(16,'Ileum','Ileum',0),
(17,'Jejunum','Jejunum',0),
(18,'Lithium Heparin Plasma','Lith Hep Plasma',0),
(19,'Meconium - BABY','Meconium',0),
(20,'Paxgene800','Paxgene800',0),
(21,'Peritoneal Dialysate','Effluent',0),
(22,'Plasma (Na Heparin) - DAD','Plasma SH',0),
(23,'Plasma','Plasma',0),
(24,'Platelet free plasma','PF Plasma',0),
(25,'RNA CBMC','CBMC RNA',0),
(26,'RNA-Ascending Colon','R-ColonA',0),
(27,'RNA-Descending Colon','R-ColonD',0),
(28,'RNA-Duodenum','R-Duodenum',0),
(29,'RNA-Ileum','R-Ilieum',0),
(30,'RNA-Jejunum','R-Jejunum',0),
(31,'RNA-Stomach, Antrum','R-StomachA',0),
(32,'RNA-Stomach, Body','R-StomachB',0),
(33,'RNA-Transverse Colon','R-ColonT',0),
(34,'RNAlater Biopsies','RNA Biopsy',0),
(35,'Serum (Beige top)','Serum B',0),
(36,'SerumG400','SerumG400',0),
(37,'SerumPellet - BABY','Serum Pel',0),
(38,'SodiumAzideUrine','ZUrine',0),
(39,'Source Water','S Water',0),
(40,'Stomach, Antrum','Stomach, A',0),
(41,'Stomach, Body','Stomach, B',0),
(42,'Tap Water','T Water',0),
(43,'Toe Nails','T Nails',0),
(44,'Transverse Colon','Colon, T',0),
(45,'Urine','Urine',0),
(46,'WB - BABY','WBlood',0),
(47,'WB DMSO','WB DMSO',0),
(48,'WB Plasma - BABY','WB Plasma',0),
(49,'WB RNA - BABY','WB RNA',0),
(50,'WB Serum - BABY','WB Serum',0),
(51,'Whole Blood EDTA','WBE',0),
(52,'LH PFP 200','LH PFP 200',0),
(53,'UrineC900','UrineC900',0),
(54,'PlasmaE800','PlasmaE800',0),
(55,'P100 500','P100 500',0),
(56,'PlasmaL500','PlasmaL500',0),
(57,'LH PFP 500','LH PFP 500',0),
(58,'PlasmaE200','PlasmaE200',0),
(59,'DNA L 1000','DNA L 1000',0),
(60,'SerumG500','SerumG500',0),
(61,'PlasmaL200','PlasmaL200',0),
(62,'DNA E 1000','DNA E 1000',0),
(63,'PlasmaE500','PlasmaE500',0),
(64,'UrineSA900','UrineSA900',0),
(65,'PlasmaE250','PlasmaE250',0),
(66,'UrineSA700','UrineSA700',0),
(67,'RNA-normal rectum biopsy','RNA-normal rectum b',0),
(68,'RNA-normal left biopsy','RNA-normal L b',0),
(69,'RNA-normal right biopsy','RNA-normal R b',0),
(70,'RNA-adjacent diseased biopsy','RNA-adjacent diseased b',0),
(71,'RNA-diseased biopsy','RNA-diseased b',0),
(72,'RNA-normal biopsy','RNA-normal b',0),
(73,'PlasmaE400','PlasmaE400',0),
(74,'SerumB900','SerumB900',0),
(75,'SerumB400','SerumB400',0),
(76,'SerumG200','SerumG200',0),
(77,'PlasmaE BHT200','PlasmaE BHT200',0),
(78,'PlasmaE300','PlasmaE300',0),
(79,'PlasmaE125','PlasmaE125',0),
(80,'PlasmaE75','PlasmaE75',0),
(81,'DNA E 500','DNA E 500',0),
(82,'N/A','N/A',0),
(83,'Unknown / import','Unknown / import',0),
(84,'Damaged','Damaged',0),
(85,'Unusable','Unusable',0),
(86,'10mL lavender top EDTA tube','10mL lavender top EDTA tube',0),
(87,'6mL lavender top EDTA tube','6mL lavender top EDTA tube',0),
(88,'4ml lavender top EDTA tube','4ml lavender top EDTA tube',0),
(89,'3mL lavender top EDTA tube','3mL lavender top EDTA tube',0),
(90,'5mL gold top serum tube','5mL gold top serum tube',0),
(91,'6mL beige top tube','6mL beige top tube',0),
(92,'3mL red top tube (hemodialysate)','3mL red top tube (hemodialysate)',0),
(93,'3ml red top tube (source water)','3ml red top tube (source water)',0),
(94,'10ml green top sodium heparin tube','10ml green top sodium heparin tube',0),
(95,'6ml light green top lithium heparin tube','6ml light green top lithium heparin tube',0),
(96,'10ml orange top PAXgene tube','10ml orange top PAXgene tube',0),
(97,'15ml centrifuge tube (sodium azide urine)','15ml centrifuge tube (sodium azide urine)',0),
(98,'6ml beige top tube (tap water)','6ml beige top tube (tap water)',0),
(99,'urine cup','urine cup',0),
(100,'fingernail tube','fingernail tube',0),
(101,'toenail tube','toenail tube',0),
(102,'hair bagette','hair bagette',0),
(103,'4.5mL blue top Sodium citrate tube','4.5mL blue top Sodium citrate tube',0),
(104,'2.7mL blue top Sodium citrate tube','2.7mL blue top Sodium citrate tube',0),
(105,'15ml centrifuge tube (ascites fluid)','15ml centrifuge tube (ascites fluid)',0),
(106,'EDTA cryovial','EDTA cryovial',0),
(107,'Nasal Swab','Nasal Swab',0),
(108,'Breast milk','Breast milk',0),
(109,'CHILD Meconium','CHILD Meconium',0),
(110,'Stool','Stool',0),
(111,'ERCIN Serum processing pallet','ERCIN Serum processing pallet',0),
(112,'ERCIN Urine processing pallet','ERCIN Urine processing pallet',0),
(113,'AHFEM processing pallet ','AHFEM processing pallet ',0),
(114,'8.5ml P100 orange top tube','8.5ml P100 orange top tube',0),
(115,'9ml CPDA yellow top tube','9ml CPDA yellow top tube',0),
(116,'10ml green top Lithium Heparin tube','10ml green top Lithium Heparin tube',0),
(117,'Biopsy, RNA later','Biopsy, RNA later',0),
(118,'Colonoscopy Kit','Colonoscopy Kit',0),
(119,'Gastroscopy Kit','Gastroscopy Kit',0),
(120,'Enteroscopy Kit','Enteroscopy Kit',0),
(121,'4ml green top sodium heparin BD 367871','4ml green top sodium heparin BD 367871',0),
(122,'4ml gold top serum tube','4ml gold top serum tube',0),
(123,'RVS Nitric Oxide processing pallet','RVS Nitric Oxide processing pallet',0),
(124,'7ml EDTA conventional top','7ml EDTA conventional top',0),
(125,'3ml lavender top EDTA tube w BHT and Desferal','3ml lavender top EDTA tube w BHT and Desferal',0);
/*!40000 ALTER TABLE `specimen_type` ENABLE KEYS */;
UNLOCK TABLES;

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
