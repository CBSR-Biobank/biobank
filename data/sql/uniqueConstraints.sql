-- unique constraints on multiple column. For unique constraint on the whole database, see uml.

ALTER TABLE container
  ADD CONSTRAINT uc_label UNIQUE (label,container_type_id),
  ADD CONSTRAINT uc_productbarcode UNIQUE (product_barcode,site_id);

ALTER TABLE container_type
  ADD CONSTRAINT uc_name UNIQUE (name,site_id),
  ADD CONSTRAINT uc_nameshort UNIQUE (name_short,site_id);

ALTER TABLE study_event_attr
  ADD CONSTRAINT uc_label UNIQUE (label,study_id);
