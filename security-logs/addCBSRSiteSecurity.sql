-- add a membership to testuser for center CBSR
insert into membership(id, version, principal_id, center_id)
select max(membership.id) + 1, 0, max(principal.id), center.id from principal, membership, center where center.name_short = 'CBSR';

-- add a permissions for all rights (except user management and administrator)
-- and add all privileges to all permissions added to testuser
-- specimen link
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 0, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 5 from permission;

-- specimen assign
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 1, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 5 from permission;

-- study
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 2, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- clinic
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 3, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- site
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 4, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- research group
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 5, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- logging
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 6, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 5 from permission;

-- reports 
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 7, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 5 from permission;

-- container
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 8, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- container type
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 9, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- patient
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 10, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- collection event
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 11, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- processing event
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 12, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- send dispatch
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 13, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 5 from permission;

-- receive dispatch
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 14, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 5 from permission;

-- create specimen request
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 15, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 5 from permission;

-- receive specimen request	
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 16, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 5 from permission;

-- clinic shipment
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 17, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- print labels	
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 18, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 5 from permission;

-- specimen types
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 19, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- shipping methods
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 20, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- activity status
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 21, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

-- specimen
insert into permission(id, version, right_id, membership_id) 
select max(permission.id) + 1, 0, 22, max(membership.id) from permission, bb_right, membership;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 1 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 2 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 3 from permission;
insert into permission_privilege(permission_id, privilege_id) 
select max(permission.id), 4 from permission;

