package com.eternax.recon.domain;

/** Reconciliation progress of one record (HLD 6.4). */
public enum ReconciliationStatus {
    UNMATCHED,
    WAITING,
    PARTIALLY_MATCHED,
    MATCHED,
    EXCEPTION,
    /** Arrived inside a closed period: stored but frozen until the period is reopened. */
    PERIOD_LOCKED
}
