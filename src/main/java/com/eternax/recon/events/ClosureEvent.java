package com.eternax.recon.events;

import java.time.Instant;

/**
 * Period closure lifecycle notification (HLD 19). Other services may use it to lock a closed
 * period.
 */
public record ClosureEvent(
        String periodId,
        String tenantId,
        String rail,
        String periodKey,
        String state,
        String snapshotId,
        String actor,
        Instant fromUtc,
        Instant toUtc,
        Instant atUtc) {}
