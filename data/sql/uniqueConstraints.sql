-- unique constraints on multiple column. For unique constraint on the whole database, see uml.

ALTER TABLE container
  ADD CONSTRAINT uc_c_label UNIQUE (label,container_type_id),
  ADD CONSTRAINT uc_c_productbarcode UNIQUE (product_barcode,site_id);

ALTER TABLE container_type
  ADD CONSTRAINT uc_ct_name UNIQUE (name,site_id),
  ADD CONSTRAINT uc_ct_nameshort UNIQUE (name_short,site_id);

ALTER TABLE collection_event
  ADD CONSTRAINT uc_ce_visit_number UNIQUE (VISIT_NUMBER,PATIENT_ID);

-- this is working only when no value is null. Unfortunately, center_id or/and study_id can be null
ALTER TABLE membership
  ADD CONSTRAINT uc_membership UNIQUE (PRINCIPAL_ID, CENTER_ID, STUDY_ID);
