-- Add a default super admin group.
insert into principal (discriminator, id, version, name, activity_status_id)
select 'BbGroup', coalesce(MAX(id), 0)+1, 0, 'Super Administrators', 1 from principal;

-- add a membership to this super admin role
insert into membership(id, version, principal_id)
select (select coalesce(MAX(id), 0)+1 from membership),
0, id from principal where name='Super Administrators';

-- add a 'AdministratorPersmission permission (id = 1 ) to this membership
insert into membership_permission(id, permission_id) values
((select max(id) from membership), 1);

