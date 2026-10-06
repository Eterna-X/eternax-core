package com.eternax.recon.domain;

/** Outcome reported by the source (HLD 6.4); independent of reconciliation status. */
public enum TransactionStatus {
    SUCCESS,
    FAILED,
    PENDING,
    REVERSED,
    UNKNOWN
}
