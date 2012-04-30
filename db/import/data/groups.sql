-- add a global domain
SELECT @domainId := COALESCE(MAX(id), 0) + 1 FROM `domain`;
INSERT INTO `domain` (id, version, all_centers, all_studies) VALUES (@domainId, 0, b'1', b'1');

-- Add a default super admin group.
SELECT @groupId := COALESCE(MAX(id), 0) + 1 FROM `principal`;
INSERT INTO `principal` (discriminator, id, version, name, activity_status_id)
 VALUES ('BbGroup', @groupId, 0, 'Global Administrators', 1);

-- add a membership to this super admin role
SELECT @membershipId := COALESCE(MAX(id), 0) + 1 FROM `membership`;
INSERT INTO membership(id, version, principal_id, domain_id, user_manager, every_permission)
  VALUES (@membershipId, 0, @groupId, @domainId, b'1', b'1');

