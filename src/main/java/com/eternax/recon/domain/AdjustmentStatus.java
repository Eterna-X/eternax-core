package com.eternax.recon.domain;

/** Maker-checker and posting lifecycle of an adjustment. */
public enum AdjustmentStatus {
    PROPOSED,
    MAKER_APPROVED,
    CHECKER_APPROVED,
    POSTED,
    CONFIRMED,
    REJECTED
}
