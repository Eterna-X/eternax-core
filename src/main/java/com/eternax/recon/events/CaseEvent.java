package com.eternax.recon.events;

import java.time.Instant;

/** Case lifecycle notification for dashboards and other consumers. */
public record CaseEvent(
        String caseId,
        String tenantId,
        String breakId,
        String type,
        String state,
        String reasonCode,
        String actor,
        Instant atUtc) {}
