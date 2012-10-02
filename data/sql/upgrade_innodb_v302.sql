--
-- script to convert all tables present in a v3.0.2 Biobank database to InnoDB
--
-- this was required to upgrade the OHS database
--
alter table abstract_position engine=InnoDB;
alter table activity_status engine=InnoDB;
alter table address engine=InnoDB;
alter table aliquoted_specimen engine=InnoDB;
alter table capacity engine=InnoDB;
alter table center engine=InnoDB;
alter table collection_event engine=InnoDB;
alter table contact engine=InnoDB;
alter table container engine=InnoDB;
alter table container_labeling_scheme engine=InnoDB;
alter table container_type engine=InnoDB;
alter table container_type_container_type engine=InnoDB;
alter table container_type_specimen_type engine=InnoDB;
alter table csm_application engine=InnoDB;
alter table csm_filter_clause engine=InnoDB;
alter table csm_group engine=InnoDB;
alter table csm_pg_pe engine=InnoDB;
alter table csm_privilege engine=InnoDB;
alter table csm_protection_element engine=InnoDB;
alter table csm_protection_group engine=InnoDB;
alter table csm_role engine=InnoDB;
alter table csm_role_privilege engine=InnoDB;
alter table csm_user engine=InnoDB;
alter table csm_user_group engine=InnoDB;
alter table csm_user_group_role_pg engine=InnoDB;
alter table csm_user_pe engine=InnoDB;
alter table dispatch engine=InnoDB;
alter table dispatch_specimen engine=InnoDB;
alter table entity engine=InnoDB;
alter table entity_column engine=InnoDB;
alter table entity_filter engine=InnoDB;
alter table entity_property engine=InnoDB;
alter table event_attr engine=InnoDB;
alter table event_attr_type engine=InnoDB;
alter table global_event_attr engine=InnoDB;
alter table jasper_template engine=InnoDB;
alter table log engine=InnoDB;
alter table origin_info engine=InnoDB;
alter table patient engine=InnoDB;
alter table printed_ss_inv_item engine=InnoDB;
alter table printer_label_template engine=InnoDB;
alter table processing_event engine=InnoDB;
alter table property_modifier engine=InnoDB;
alter table property_type engine=InnoDB;
alter table report engine=InnoDB;
alter table report_column engine=InnoDB;
alter table report_filter engine=InnoDB;
alter table report_filter_value engine=InnoDB;
alter table request engine=InnoDB;
alter table request_specimen engine=InnoDB;
alter table shipment_info engine=InnoDB;
alter table shipping_method engine=InnoDB;
alter table site_study engine=InnoDB;
alter table source_specimen engine=InnoDB;
alter table specimen engine=InnoDB;
alter table specimen_type engine=InnoDB;
alter table specimen_type_specimen_type engine=InnoDB;
alter table study engine=InnoDB;
alter table study_contact engine=InnoDB;
alter table study_event_attr engine=InnoDB;

