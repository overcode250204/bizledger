package com.overcode250204.auditservice.repository;

import com.overcode250204.auditservice.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
    Page<AuditEvent> findByTenantId(UUID tenantId, Pageable pageable);
}
