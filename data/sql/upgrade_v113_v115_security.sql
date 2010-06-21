INSERT INTO csm_protection_element (protection_element_name, protection_element_description, object_id, attribute, attribute_value, application_id, update_date)
select "edu.ualberta.med.biobank.model.Site/CBSR", "CBSR", "edu.ualberta.med.biobank.model.Site", "id", id, 2, sysdate() 
from site where name_short="CBSR";

INSERT INTO csm_pg_pe (protection_group_id, protection_element_id, update_date) 
select 11, protection_element_id, sysdate() from csm_protection_element where protection_element_description="CBSR";
