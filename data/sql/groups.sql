-- Add a default super admin group. 
insert into principal (id, version) 
select coalesce(MAX(id), 0)+1, 0 from principal;

insert into bb_group (principal_id, name)
select max(id), 'Super Administrators' from principal;

-- add a membership to this super admin role
insert into membership(id, version, principal_id)
select 1, 0, max(id) from principal;

-- add a 'AdministratorPersmission permission (id = 1 ) to this membership 
insert into membership_permission(membership_id, permission_id) values
(1, 1);

