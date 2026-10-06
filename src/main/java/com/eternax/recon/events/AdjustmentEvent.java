package com.eternax.recon.events;

import java.time.Instant;

/** Adjustment lifecycle notification; POSTED and CONFIRMED drive case resolution. */
public record AdjustmentEvent(
        String adjustmentId,
        String tenantId,
        String caseId,
        String type,
        String status,
        long amountMinor,
        String currency,
        String actor,
        String journalReference,
        Instant atUtc) {}
