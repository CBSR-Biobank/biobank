INSERT INTO principal (id, time_inserted, time_updated, version, is_admin, is_enabled, inserted_by_user_id, updated_by_user_id)
VALUES (1,0,0,0,1,1,null,null);

INSERT INTO user (id,login,email,full_name,is_mailing_list_subscriber,is_password_change_needed)
VALUES (1,'superadmin','','superadmin',0,0);

