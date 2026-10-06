package com.eternax.recon.events;

import java.time.Instant;

/** A counterpart arrived after a break was raised and now matches (HLD 10.7: closes as TIMING). */
public record LateMatchEvent(
        String breakId, String tenantId, String matchGroupId, Instant matchedAtUtc) {}
