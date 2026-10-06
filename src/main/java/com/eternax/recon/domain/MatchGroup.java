package com.eternax.recon.domain;

import com.eternax.recon.common.Markers.MatchGroupMarker;
import com.eternax.recon.common.Money;
import com.eternax.recon.common.TypedId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Records judged to be the same financial event (HLD 7). Supports N-way membership: one allocation
 * per participating source record.
 */
public record MatchGroup(
        TypedId<MatchGroupMarker> matchGroupId,
        MatchTier tier,
        String ruleName,
        String ruleVersion,
        double confidenceScore,
        List<Allocation> allocations,
        Instant matchedAtUtc) {

    public MatchGroup {
        Objects.requireNonNull(matchGroupId, "matchGroupId must not be null");
        Objects.requireNonNull(tier, "tier must not be null");
        Objects.requireNonNull(ruleName, "ruleName must not be null");
        Objects.requireNonNull(ruleVersion, "ruleVersion must not be null");
        if (confidenceScore < 0.0 || confidenceScore > 1.0) {
            throw new IllegalArgumentException(
                    "confidenceScore must be within [0,1], was " + confidenceScore);
        }
        allocations =
                List.copyOf(Objects.requireNonNull(allocations, "allocations must not be null"));
        if (allocations.size() < 2) {
            throw new IllegalArgumentException("a match group needs at least 2 allocations");
        }
        Objects.requireNonNull(matchedAtUtc, "matchedAtUtc must not be null");
    }

    /**
     * Spread between the largest and smallest allocated amount. Zero for a clean match; non-zero
     * only within a configured tolerance.
     */
    public Money amountSpread() {
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        String currency = allocations.get(0).allocatedAmount().currencyCode();
        for (Allocation allocation : allocations) {
            long value = allocation.allocatedAmount().amountMinor();
            max = Math.max(max, value);
            min = Math.min(min, value);
        }
        return new Money(max - min, currency);
    }
}
