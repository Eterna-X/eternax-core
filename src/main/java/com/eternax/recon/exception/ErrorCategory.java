package com.eternax.recon.exception;

/**
 * Transport-neutral failure classification; adapters (REST, Kafka) map it to their own semantics.
 */
public enum ErrorCategory {
    VALIDATION,
    NOT_FOUND,
    CONFLICT,
    FORBIDDEN,
    UNAVAILABLE,
    INTERNAL
}
