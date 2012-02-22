--
-- This script resolves request #195 on the CBSR Help Desk site
-- see https://cbsr.zendesk.com/requests/195
--

-- delete all specimen positions derived from specimens from centers to be deleted
delete abstract_position from specimen, center, abstract_position
where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study')
and center.id=specimen.current_center_id
and abstract_position.specimen_id=specimen.id;

-- delete all dispatch_specimens derived from specimens from centers to be deleted
delete dispatch_specimen from dispatch_specimen, specimen, center
where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study')
and center.id=specimen.current_center_id
and dispatch_specimen.specimen_id=specimen.id;

-- delete all dispatch_specimens derived from specimens with a parent specimen from centers to be deleted
delete dispatch_specimen from specimen,dispatch_specimen,(
    select specimen.id from specimen
    join center on center.id=specimen.current_center_id
    where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study')) a
where specimen.parent_specimen_id=a.id
and dispatch_specimen.specimen_id=specimen.id;

-- delete all specimens that have a parent specimen from the centers to be deleted
delete specimen from specimen,(
    select specimen.id from specimen
    join center on center.id=specimen.current_center_id
    where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study')) a
where specimen.parent_specimen_id=a.id;

-- delete all collection events that have a parent specimen from the centers to be deletedfrom specimen
delete ea
from specimen
join (select specimen.id from specimen
    join center on center.id=specimen.current_center_id
    where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study')) a on specimen.parent_specimen_id=a.id
join collection_event ce on ce.id=specimen.collection_event_id
join event_attr ea on ea.collection_event_id=ce.id;

SET FOREIGN_KEY_CHECKS = 0;
delete pt
from specimen
join (select specimen.id from specimen
    join center on center.id=specimen.current_center_id
    where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study')) a on specimen.parent_specimen_id=a.id
join collection_event ce on ce.id=specimen.collection_event_id
join patient pt on pt.id=ce.patient_id;
SET FOREIGN_KEY_CHECKS = 1;

SET FOREIGN_KEY_CHECKS = 0;
delete ce
from specimen
join (select specimen.id from specimen
    join center on center.id=specimen.current_center_id
    where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study')) a on specimen.parent_specimen_id=a.id
join collection_event ce on ce.id=specimen.collection_event_id;
SET FOREIGN_KEY_CHECKS = 1;

-- update top specimen on specimens to be deleted
update specimen,center set specimen.top_specimen_id=null
where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study')
and center.id=specimen.current_center_id;

delete specimen from specimen, center
where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study')
and center.id=specimen.current_center_id;

-- delete processing events that originate at these centers
delete pe from processing_event pe
join center ct on ct.id=pe.center_id
where ct.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');

delete ds from dispatch ds
join center ct on (ct.id=ds.receiver_center_id or ct.id=ds.sender_center_id)
where ct.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');

SET FOREIGN_KEY_CHECKS = 0;
delete shp from shipment_info shp
join origin_info oi on oi.shipment_info_id=shp.id
join center ct on (ct.id=oi.center_id or ct.id=oi.receiver_site_id)
where ct.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');
SET FOREIGN_KEY_CHECKS = 1;

delete oi from origin_info oi
join center ct on (ct.id=oi.center_id or ct.id=oi.receiver_site_id)
where ct.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');

SET FOREIGN_KEY_CHECKS = 0;
delete container from container
join center ct on ct.id=container.site_id
where ct.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');
SET FOREIGN_KEY_CHECKS = 1;

delete ctype_ctype from container_type_container_type ctype_ctype
join container_type ctype on ctype.id=ctype_ctype.child_container_type_id
join center ct on ct.id=ctype.site_id
where ct.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');

delete ctype_stype from container_type_specimen_type ctype_stype
join container_type ctype on ctype.id=ctype_stype.container_type_id
join center ct on ct.id=ctype.site_id
where ct.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');

delete ctype from container_type ctype
join center ct on ct.id=ctype.site_id
where ct.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');

-- delete association between the centers, that are sites, and any associated studies
delete site_study from site_study,center
where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study')
and discriminator='Site'
and site_study.site_id=center.id;

-- delete contact associated with centers
delete stct from study_contact stct
join contact on stct.contact_id=contact.id
join center ct on ct.id=contact.clinic_id
where ct.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');

delete contact from contact
join center ct on ct.id=contact.clinic_id
where ct.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');

-- delete the centers
delete center from center
where center.name_short in ('RVH - RIMUHC', 'Research Center Montreal', 'QPCS', 'QPC study');

delete aq
from  aliquoted_specimen aq
join specimen_type st on st.id=aq.specimen_type_id
where st.name in ('10ml ACD tube', 'ACD Plasma', 'White Blood Cells', '10ml yellow top ACD tube', '0.2ml Plasma',
    '1ml Plasma', 'Blood FTA spot', 'Lymphocytes - pellet', 'White Blood Cells', 'DNA (FFPE) - normal', 'DNA (FFPE) - tumour',
    'DNA (mouse tissue P1)', 'DNA (mouse tissue P2)', 'DNA (mouse tissue P3)', 'Fresh frozen pancreas - normal',
    'Fresh frozen pancreas - tumour', 'Fresh frozen pancreas H&E slide', 'Fresh frozen pancreas tumour sample',
    'Fresh tissue pancreas - normal', 'Fresh tissue pancreas - tumour', 'Mouse xeno normal P1', 'Mouse xeno normal P2',
    'Mouse xeno normal P3', 'Mouse xeno tissue P1', 'Mouse xeno normal P1', 'Mouse xeno tumour P1', 'Mouse xeno tissue P2',
    'Mouse xeno normal P2', 'Mouse xeno tumour P2', 'Mouse xeno tissue P3', 'Mouse xeno normal P3', 'Mouse xeno tumour P3',
    'Mouse xeno tumor P1', 'Mouse xeno tumor P2', 'Mouse xeno tumor P3', 'Paraffin block panreas - mixed', 'Paraffin slide - LCM',
    'Paraffin slide - mixed', 'Paraffin block panreas - normal', 'Paraffin slide- LCM', 'Paraffin slide - normal',
    'Paraffin block pancreas - tumour ', 'Paraffin slide - LCM', 'Paraffin slide - tumour');

delete ss
from  source_specimen ss
join specimen_type st on st.id=ss.specimen_type_id
where st.name in ('10ml ACD tube', 'ACD Plasma', 'White Blood Cells', '10ml yellow top ACD tube', '0.2ml Plasma',
    '1ml Plasma', 'Blood FTA spot', 'Lymphocytes - pellet', 'White Blood Cells', 'DNA (FFPE) - normal', 'DNA (FFPE) - tumour',
    'DNA (mouse tissue P1)', 'DNA (mouse tissue P2)', 'DNA (mouse tissue P3)', 'Fresh frozen pancreas - normal',
    'Fresh frozen pancreas - tumour', 'Fresh frozen pancreas H&E slide', 'Fresh frozen pancreas tumour sample',
    'Fresh tissue pancreas - normal', 'Fresh tissue pancreas - tumour', 'Mouse xeno normal P1', 'Mouse xeno normal P2',
    'Mouse xeno normal P3', 'Mouse xeno tissue P1', 'Mouse xeno normal P1', 'Mouse xeno tumour P1', 'Mouse xeno tissue P2',
    'Mouse xeno normal P2', 'Mouse xeno tumour P2', 'Mouse xeno tissue P3', 'Mouse xeno normal P3', 'Mouse xeno tumour P3',
    'Mouse xeno tumor P1', 'Mouse xeno tumor P2', 'Mouse xeno tumor P3', 'Paraffin block panreas - mixed', 'Paraffin slide - LCM',
    'Paraffin slide - mixed', 'Paraffin block panreas - normal', 'Paraffin slide- LCM', 'Paraffin slide - normal',
    'Paraffin block pancreas - tumour ', 'Paraffin slide - LCM', 'Paraffin slide - tumour');

delete stst
from  specimen_type_specimen_type stst
join specimen_type st on (st.id=stst.parent_specimen_type_id or st.id=stst.child_specimen_type_id)
where st.name in ('10ml ACD tube', 'ACD Plasma', 'White Blood Cells', '10ml yellow top ACD tube', '0.2ml Plasma',
    '1ml Plasma', 'Blood FTA spot', 'Lymphocytes - pellet', 'White Blood Cells', 'DNA (FFPE) - normal', 'DNA (FFPE) - tumour',
    'DNA (mouse tissue P1)', 'DNA (mouse tissue P2)', 'DNA (mouse tissue P3)', 'Fresh frozen pancreas - normal',
    'Fresh frozen pancreas - tumour', 'Fresh frozen pancreas H&E slide', 'Fresh frozen pancreas tumour sample',
    'Fresh tissue pancreas - normal', 'Fresh tissue pancreas - tumour', 'Mouse xeno normal P1', 'Mouse xeno normal P2',
    'Mouse xeno normal P3', 'Mouse xeno tissue P1', 'Mouse xeno normal P1', 'Mouse xeno tumour P1', 'Mouse xeno tissue P2',
    'Mouse xeno normal P2', 'Mouse xeno tumour P2', 'Mouse xeno tissue P3', 'Mouse xeno normal P3', 'Mouse xeno tumour P3',
    'Mouse xeno tumor P1', 'Mouse xeno tumor P2', 'Mouse xeno tumor P3', 'Paraffin block panreas - mixed', 'Paraffin slide - LCM',
    'Paraffin slide - mixed', 'Paraffin block panreas - normal', 'Paraffin slide- LCM', 'Paraffin slide - normal',
    'Paraffin block pancreas - tumour ', 'Paraffin slide - LCM', 'Paraffin slide - tumour');

delete st
from  specimen_type st
where st.name in ('10ml ACD tube', 'ACD Plasma', 'White Blood Cells', '10ml yellow top ACD tube', '0.2ml Plasma',
    '1ml Plasma', 'Blood FTA spot', 'Lymphocytes - pellet', 'White Blood Cells', 'DNA (FFPE) - normal', 'DNA (FFPE) - tumour',
    'DNA (mouse tissue P1)', 'DNA (mouse tissue P2)', 'DNA (mouse tissue P3)', 'Fresh frozen pancreas - normal',
    'Fresh frozen pancreas - tumour', 'Fresh frozen pancreas H&E slide', 'Fresh frozen pancreas tumour sample',
    'Fresh tissue pancreas - normal', 'Fresh tissue pancreas - tumour', 'Mouse xeno normal P1', 'Mouse xeno normal P2',
    'Mouse xeno normal P3', 'Mouse xeno tissue P1', 'Mouse xeno normal P1', 'Mouse xeno tumour P1', 'Mouse xeno tissue P2',
    'Mouse xeno normal P2', 'Mouse xeno tumour P2', 'Mouse xeno tissue P3', 'Mouse xeno normal P3', 'Mouse xeno tumour P3',
    'Mouse xeno tumor P1', 'Mouse xeno tumor P2', 'Mouse xeno tumor P3', 'Paraffin block panreas - mixed', 'Paraffin slide - LCM',
    'Paraffin slide - mixed', 'Paraffin block panreas - normal', 'Paraffin slide- LCM', 'Paraffin slide - normal',
    'Paraffin block pancreas - tumour ', 'Paraffin slide - LCM', 'Paraffin slide - tumour');
