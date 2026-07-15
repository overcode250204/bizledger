-- =============================================================
-- Identity Service — Database Schema (Plural Aligned)
-- V1__init_identity.sql
-- =============================================================

CREATE TABLE tenants (
                         id          UUID PRIMARY KEY,
                         name        VARCHAR(255) NOT NULL,
                         code        VARCHAR(100) NOT NULL UNIQUE,
                         status      VARCHAR(50) NOT NULL,
                         created_at  TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_tenants_code ON tenants(code);

-- =============================================================

CREATE TABLE users (
                       id             UUID PRIMARY KEY,
                       tenant_id      UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
                       email          VARCHAR(255) NOT NULL UNIQUE,
                       password_hash  VARCHAR(255) NOT NULL,
                       full_name      VARCHAR(255) NOT NULL,
                       active         BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at     TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_users_tenant ON users(tenant_id);

-- =============================================================

CREATE TABLE roles (
                       id         UUID PRIMARY KEY,
                       tenant_id  UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
                       code       VARCHAR(100) NOT NULL,
                       name       VARCHAR(255) NOT NULL
);

CREATE INDEX idx_roles_tenant_code ON roles(tenant_id, code);

-- =============================================================

CREATE TABLE permissions (
                             id           UUID PRIMARY KEY,
                             code         VARCHAR(100) NOT NULL UNIQUE,
                             description  VARCHAR(255) NOT NULL
);

-- =============================================================

CREATE TABLE user_roles (
                            user_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            role_id  UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

-- =============================================================

CREATE TABLE role_permissions (
                                  role_id        UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                                  permission_id  UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
                                  PRIMARY KEY (role_id, permission_id)
);

-- =============================================================
-- Outbox Pattern
-- =============================================================
CREATE TABLE outbox_events (
                               id           UUID PRIMARY KEY,
                               event_id     VARCHAR(100) NOT NULL UNIQUE,
                               event_type   VARCHAR(100) NOT NULL,
                               topic        VARCHAR(200) NOT NULL,
                               payload      TEXT NOT NULL,
                               status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                               retry_count  INT NOT NULL DEFAULT 0,
                               created_at   TIMESTAMPTZ NOT NULL,
                               published_at TIMESTAMPTZ
);

CREATE INDEX idx_identity_outbox ON outbox_events(status, created_at);

-- =============================================================
-- API Keys (M2M authentication support - FR-EXT-02)
-- =============================================================
CREATE TABLE api_keys (
                          id            UUID PRIMARY KEY,
                          tenant_id     UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
                          user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          name          VARCHAR(255) NOT NULL,
                          key_hash      VARCHAR(255) NOT NULL UNIQUE,
                          scopes        VARCHAR(500) NOT NULL,
                          created_at    TIMESTAMPTZ NOT NULL,
                          expires_at    TIMESTAMPTZ,
                          last_used_at  TIMESTAMPTZ
);

CREATE INDEX idx_api_keys_hash ON api_keys(key_hash);
CREATE INDEX idx_api_keys_tenant ON api_keys(tenant_id);

-- =============================================================
-- Feature Flags (Tenant Rollouts & Kill switches - FR-EXT-04)
-- =============================================================
CREATE TABLE feature_flags (
                               id           UUID PRIMARY KEY,
                               tenant_id    UUID REFERENCES tenants(id) ON DELETE CASCADE, -- Null means Global
                               key          VARCHAR(255) NOT NULL,
                               description  TEXT,
                               enabled      BOOLEAN NOT NULL DEFAULT TRUE,
                               rules        TEXT, -- JSON payload for custom percentage rollouts / allow-lists
                               created_at   TIMESTAMPTZ NOT NULL,
                               updated_at   TIMESTAMPTZ,
                               UNIQUE(tenant_id, key)
);

CREATE INDEX idx_feature_flags_tenant ON feature_flags(tenant_id);
