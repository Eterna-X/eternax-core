package com.eternax.recon.events;

import java.time.Instant;

/**
 * Adjustment lifecycle notification; POSTED and CONFIRMED drive case resolution.
 *
 * @param caseCovered added in 1.2: true when the adjustments of the case, counted up to this
 *     event's status, cover the whole break and no other adjustment of the case is still open. The
 *     case is resolved (POSTED) or closed (CONFIRMED) only when this is true. Null on events from
 *     older producers, which keeps the previous behaviour.
 * @param reason free text given with a rejection
 */
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
        Instant atUtc,
        Boolean caseCovered,
        String reason) {

    /** Constructor used before 1.2: no coverage information. */
    public AdjustmentEvent(
            String adjustmentId,
            String tenantId,
            String caseId,
            String type,
            String status,
            long amountMinor,
            String currency,
            String actor,
            String journalReference,
            Instant atUtc) {
        this(
                adjustmentId,
                tenantId,
                caseId,
                type,
                status,
                amountMinor,
                currency,
                actor,
                journalReference,
                atUtc,
                null,
                null);
    }
}
