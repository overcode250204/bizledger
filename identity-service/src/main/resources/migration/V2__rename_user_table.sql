ALTER TABLE tenants RENAME TO tenant;

-- Bắt buộc bọc nháy kép do trùng từ khóa hệ thống
ALTER TABLE users RENAME TO "user";

ALTER TABLE roles RENAME TO role;
ALTER TABLE permissions RENAME TO permission;
ALTER TABLE user_roles RENAME TO user_role;
ALTER TABLE role_permissions RENAME TO role_permission;
ALTER TABLE outbox_events RENAME TO outbox_event;
ALTER TABLE api_keys RENAME TO api_key;
ALTER TABLE feature_flags RENAME TO feature_flag;