package com.eternax.recon.events;

/** Kafka topic names. The version suffix changes only on a breaking schema change. */
public final class Topics {
    private Topics() {}

    public static final String RAW_RECORDS = "eternax.raw-records.v1";
    public static final String CANONICAL_TRANSACTIONS = "eternax.canonical-transactions.v1";
    public static final String BREAKS = "eternax.breaks.v1";
    public static final String LATE_MATCHES = "eternax.late-matches.v1";
    public static final String CASE_EVENTS = "eternax.case-events.v1";
    public static final String ADJUSTMENT_EVENTS = "eternax.adjustment-events.v1";
    public static final String DEFINITIONS = "eternax.definitions.v1";
    public static final String CLOSURE_EVENTS = "eternax.closure-events.v1";
    public static final String AUDIT_EVENTS = "eternax.audit-events.v1";
    public static final String DEAD_LETTER_SUFFIX = ".dlt";
}
