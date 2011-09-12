-- Add a default super admin group. 
insert into principal (id, version) 
select coalesce(MAX(id), 0)+1, 0 from principal;

insert into bb_group (principal_id, name)
select max(id), 'Super Administrators' from principal;

-- add a membership to this super admin role
insert into membership(id, version, principal_id)
select 1, 0, max(id) from principal;

-- add a permission to this membership that contains right 'administrator' (id=24)
insert into permission(id, version, right_id, membership_id) values
(1, 0, 24, 1);

-- add privilege 'allowed' (id = 5) to this permission
insert into permission_privilege(permission_id, privilege_id) values
(1, 5);
