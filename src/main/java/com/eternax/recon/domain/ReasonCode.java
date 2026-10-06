package com.eternax.recon.domain;

/** Break classification (HLD 10.1 / 10.6). */
public enum ReasonCode {
    MISSING_SOURCE,
    AMOUNT_DIFF,
    DUPLICATE,
    REFERENCE_DIFF,
    TIMING,
    STATUS_DIFF,
    LIFECYCLE_GAP,
    DATA_QUALITY,
    SETTLEMENT_DIFF,
    OTHER
}
