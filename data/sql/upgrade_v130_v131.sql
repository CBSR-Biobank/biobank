update aliquot c
       set c.activity_status_id = (select id from activity_status where name = 'Closed')
       where c.activity_status_id = (select id from activity_status where name = 'Disabled');
update container c
       set c.activity_status_id = (select id from activity_status where name = 'Closed')
       where c.activity_status_id = (select id from activity_status where name = 'Disabled');
update clinic c
       set c.activity_status_id = (select id from activity_status where name = 'Closed')
       where c.activity_status_id = (select id from activity_status where name = 'Disabled');
update study c
       set c.activity_status_id = (select id from activity_status where name = 'Closed')
       where c.activity_status_id = (select id from activity_status where name = 'Disabled');
update site c
       set c.activity_status_id = (select id from activity_status where name = 'Closed')
       where c.activity_status_id = (select id from activity_status where name = 'Disabled');
update container_type c
       set c.activity_status_id = (select id from activity_status where name = 'Closed')
       where c.activity_status_id = (select id from activity_status where name = 'Disabled');
update sample_storage c
       set c.activity_status_id = (select id from activity_status where name = 'Closed')
       where c.activity_status_id = (select id from activity_status where name = 'Disabled');
update study_pv_attr c
       set c.activity_status_id = (select id from activity_status where name = 'Closed')
       where c.activity_status_id = (select id from activity_status where name = 'Disabled');
update abstract_shipment c
       set c.activity_status_id = (select id from activity_status where name = 'Closed')
       where c.activity_status_id = (select id from activity_status where name = 'Disabled');
delete from activity_status where name='Disabled';
