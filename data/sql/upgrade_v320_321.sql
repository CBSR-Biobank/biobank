-- start bug#1698 fix
-- activityStatus needs to use the id now

UPDATE entity_property 
  SET property = 'activityStatus'
  WHERE id IN (5, 405);

UPDATE entity_filter
  SET filter_type = 8
  WHERE entity_property_id IN (5, 405);

-- end bug#1698 fix
