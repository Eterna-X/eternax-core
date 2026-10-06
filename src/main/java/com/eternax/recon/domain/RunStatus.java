package com.eternax.recon.domain;

/** Reconciliation run status (HLD 18.4). */
public enum RunStatus {
    SCHEDULED,
    RUNNING,
    COMPLETED,
    COMPLETED_WITH_EXCEPTIONS,
    FAILED
}
