package com.eternax.recon.domain;

import com.eternax.recon.common.Money;
import java.util.Objects;

/** One component of a settlement fee: MDR, GST or TCS (HLD 6.1). */
public record FeeComponent(FeeType feeType, Money amount) {
    public FeeComponent {
        Objects.requireNonNull(feeType, "feeType must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
    }
}
