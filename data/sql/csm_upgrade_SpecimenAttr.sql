-- -----------------------------------------------------------------------
--
-- New security / user management
--
-- -----------------------------------------------------------------------

-- add new objects into the csm database:
insert into csm_protection_element (protection_element_name, object_id, application_id, update_date) values
('edu.ualberta.med.biobank.model.StudySpecimenAttr','edu.ualberta.med.biobank.model.StudySpecimenAttr',2,sysdate()),
('edu.ualberta.med.biobank.model.SpecimenAttr','edu.ualberta.med.biobank.model.SpecimenAttr',2,sysdate()),
('edu.ualberta.med.biobank.model.SpecimenAttrType','edu.ualberta.med.biobank.model.SpecimenAttrType',2,sysdate()),
('edu.ualberta.med.biobank.model.GlobalSpecimenAttr','edu.ualberta.med.biobank.model.GlobalSpecimenAttr',2,sysdate());

-- add the new object into the protection group with id 1 (the one containing all objects protection elements)
insert into csm_pg_pe (protection_group_id, protection_element_id, update_date)
select 1, protection_element_id, sysdate() from csm_protection_element
where protection_element_name = 'edu.ualberta.med.biobank.model.StudySpecimenAttr'
or  protection_element_name = 'edu.ualberta.med.biobank.model.SpecimenAttr'
or  protection_element_name = 'edu.ualberta.med.biobank.model.SpecimenAttrType'
or  protection_element_name = 'edu.ualberta.med.biobank.model.GlobalSpecimenAttr';
