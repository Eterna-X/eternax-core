package com.eternax.recon.domain;

import com.eternax.recon.common.Markers.TransactionMarker;
import com.eternax.recon.common.Money;
import com.eternax.recon.common.TypedId;
import com.eternax.recon.exception.OverAllocationException;
import java.util.Objects;

/** One record's participation in a {@link MatchGroup} (HLD 7.6). */
public record Allocation(
        TypedId<TransactionMarker> transactionId, AllocationSide side, Money allocatedAmount) {

    public Allocation {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(side, "side must not be null");
        Objects.requireNonNull(allocatedAmount, "allocatedAmount must not be null");
    }

    /**
     * Enforces HLD 7.6 item 2: the total allocated to a record across all groups never exceeds its
     * amount.
     */
    public void assertWithinRemainingAmount(Money alreadyAllocated, Money transactionAmount) {
        Money projected = alreadyAllocated.plus(allocatedAmount);
        if (projected.amountMinor() > transactionAmount.amountMinor()) {
            throw new OverAllocationException(
                    transactionId.value(),
                    transactionAmount.amountMinor(),
                    projected.amountMinor());
        }
    }
}
