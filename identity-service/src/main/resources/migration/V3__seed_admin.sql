-- =============================================================
-- Identity Service — Seed Default Admin Account and Permissions
-- V2__seed_admin.sql
-- =============================================================

-- 1. Seed Default Tenant
INSERT INTO tenant (id, name, code, status, created_at)
VALUES (
           'd3b07384-d113-4c92-a1b6-d3aef6d9ef59',
           'Northwind Traders',
           'tn_bizledger',
           'ACTIVE',
           NOW()
       ) ON CONFLICT (code) DO NOTHING;

-- 2. Seed Default Owner User (minh.tran@bizledger.io / password123)
-- BCrypt password hash: $2a$10$tZ20k9X0n5nL.z/N5L4mB.3tQzUpu2p.56.yK.HkFhK9N9z3QzO3m
INSERT INTO "user" (id, tenant_id, email, password_hash, full_name, active, created_at, mfa_enabled)
VALUES (
           'c04e223b-09bb-4c28-971a-28952cc29352',
           'd3b07384-d113-4c92-a1b6-d3aef6d9ef59',
           'minh.tran@bizledger.io',
           '$2a$10$tZ20k9X0n5nL.z/N5L4mB.3tQzUpu2p.56.yK.HkFhK9N9z3QzO3m',
           'Minh Tran',
           TRUE,
           NOW(),
           FALSE
       ) ON CONFLICT (email) DO NOTHING;

-- 3. Seed Permissions
INSERT INTO permission (id, code, description) VALUES
                                                    ('00000000-0000-0000-0000-000000000001', 'user:read', 'Read users info'),
                                                    ('00000000-0000-0000-0000-000000000002', 'user:write', 'Write users info'),
                                                    ('00000000-0000-0000-0000-000000000003', 'role:read', 'Read role settings'),
                                                    ('00000000-0000-0000-0000-000000000004', 'role:write', 'Write role settings'),
                                                    ('00000000-0000-0000-0000-000000000005', 'product:read', 'Read product catalog'),
                                                    ('00000000-0000-0000-0000-000000000006', 'product:write', 'Write product catalog'),
                                                    ('00000000-0000-0000-0000-000000000007', 'inventory:read', 'Read inventory status'),
                                                    ('00000000-0000-0000-0000-000000000008', 'inventory:write', 'Write/adjust inventory'),
                                                    ('00000000-0000-0000-0000-000000000009', 'order:read', 'Read customer orders'),
                                                    ('00000000-0000-0000-0000-000000000010', 'order:write', 'Write customer orders'),
                                                    ('00000000-0000-0000-0000-000000000011', 'payment:read', 'Read payments/invoices'),
                                                    ('00000000-0000-0000-0000-000000000012', 'payment:write', 'Write/refund payments'),
                                                    ('00000000-0000-0000-0000-000000000013', 'audit:read', 'Read audit trail logs'),
                                                    ('00000000-0000-0000-0000-000000000014', 'compliance:read', 'Read GDPR/compliance'),
                                                    ('00000000-0000-0000-0000-000000000015', 'compliance:write', 'Write compliance setups'),
                                                    ('00000000-0000-0000-0000-000000000016', 'featureflag:read', 'Read feature flags'),
                                                    ('00000000-0000-0000-0000-000000000017', 'featureflag:write', 'Write feature flags'),
                                                    ('00000000-0000-0000-0000-000000000018', 'apikey:read', 'Read api key info'),
                                                    ('00000000-0000-0000-0000-000000000019', 'apikey:write', 'Write api key credentials')
    ON CONFLICT (code) DO NOTHING;

-- 4. Seed Owner Role
INSERT INTO role (id, tenant_id, code, name)
VALUES (
           '8f26df80-a61c-4b5c-8977-9be7bd39ca5a',
           'd3b07384-d113-4c92-a1b6-d3aef6d9ef59',
           'role_owner',
           'Owner'
       ) ON CONFLICT (id) DO NOTHING;

-- 5. Map all permissions to Owner role
INSERT INTO role_permission (role_id, permission_id) VALUES
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000001'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000002'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000003'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000004'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000005'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000006'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000007'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000008'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000009'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000010'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000011'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000012'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000013'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000014'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000015'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000016'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000017'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000018'),
                                                          ('8f26df80-a61c-4b5c-8977-9be7bd39ca5a', '00000000-0000-0000-0000-000000000019')
    ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 6. Link User to Owner Role
INSERT INTO user_role (user_id, role_id)
VALUES (
           'c04e223b-09bb-4c28-971a-28952cc29352',
           '8f26df80-a61c-4b5c-8977-9be7bd39ca5a'
       ) ON CONFLICT (user_id, role_id) DO NOTHING;
