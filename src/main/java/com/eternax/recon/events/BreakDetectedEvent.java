package com.eternax.recon.events;

import java.time.Instant;
import java.util.List;

/**
 * A reconciliation break whose waiting window has closed. {@code breakId} is deterministic (tenant,
 * rail, match key, reason) so downstream consumers stay idempotent under redelivery.
 */
public record BreakDetectedEvent(
        String breakId,
        String tenantId,
        String rail,
        String matchKey,
        String reasonCode,
        String detailCode,
        List<String> involvedTransactionIds,
        List<String> missingSourceIds,
        long financialImpactMinor,
        String currency,
        Instant transactionTimeUtc,
        Instant detectedAtUtc,
        String definitionId,
        String explanation) {}
