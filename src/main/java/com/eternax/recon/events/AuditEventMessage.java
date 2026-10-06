package com.eternax.recon.events;

import java.time.Instant;

/** An audit fact emitted by any service; the audit service chains and stores it (HLD 15.2). */
public record AuditEventMessage(
        String tenantId,
        String actor,
        String action,
        String entityType,
        String entityId,
        String beforeStateJson,
        String afterStateJson,
        String correlationId,
        String sourceService,
        Instant atUtc) {}
