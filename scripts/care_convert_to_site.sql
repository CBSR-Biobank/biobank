--
-- Will convert all data beloging to clinic 'CaRE ED1' to site 'CaRE'
--
-- tables referencing center
--
--    center_comment
--    contact
--    container
--    container_type
--    dispatch - receiver_center_id, sender_center_id
--    domain_center
--    membership
--    origin_info - receiver_site_id, center_id
--    processing_event
--    request
--    site_study
--    specimen

set @care_clinic_id = null;
set @care_site_id = null;

select id from center where name='CaRE ED1' and discriminator='Clinic' into @care_clinic_id;
select id from center where name='CaRE' and discriminator='Site' into @care_site_id;

update center_comment set center_id=@care_site_id where center_id=@care_clinic_id;

delete from study_contact where contact_id in (select id from contact where clinic_id=@care_clinic_id);
delete from contact where clinic_id=@care_clinic_id;

update dispatch set sender_center_id=@care_site_id where sender_center_id=@care_clinic_id;
update dispatch set receiver_center_id=@care_site_id where receiver_center_id=@care_clinic_id;

-- leave permissions alone

update origin_info set center_id=@care_site_id where center_id=@care_clinic_id;
update processing_event set center_id=@care_site_id where center_id=@care_clinic_id;
update specimen set current_center_id=@care_site_id where current_center_id=@care_clinic_id;
