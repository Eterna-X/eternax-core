package com.eternax.recon.domain;

/** Exception case lifecycle (HLD 10.3 / 10.6). */
public enum CaseState {
    DETECTED,
    CATEGORIZED,
    ASSIGNED,
    INVESTIGATING,
    RESOLUTION_PROPOSED,
    PENDING_APPROVAL,
    RESOLVED,
    RECONCILED,
    CLOSED
}
