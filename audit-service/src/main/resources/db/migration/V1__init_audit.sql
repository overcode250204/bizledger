-- =============================================================
-- Centralized Audit Service — Database Schema
-- V1__init_audit.sql
-- =============================================================

CREATE TABLE audit_logs (
                            id             UUID PRIMARY KEY,
                            event_id       VARCHAR(100) NOT NULL UNIQUE,
                            event_type     VARCHAR(100) NOT NULL,
                            service_name   VARCHAR(100) NOT NULL,
                            trace_id       VARCHAR(100),
                            tenant_id      UUID NOT NULL,
                            user_id        VARCHAR(100),
                            payload        TEXT NOT NULL,
                            timestamp      TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_audit_tenant ON audit_logs(tenant_id);
CREATE INDEX idx_audit_service ON audit_logs(service_name);
CREATE INDEX idx_audit_event_type ON audit_logs(event_type);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp DESC);
