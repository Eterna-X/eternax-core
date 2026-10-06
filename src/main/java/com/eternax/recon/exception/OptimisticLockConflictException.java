package com.eternax.recon.exception;

/** A write lost a race with a concurrent update of the same entity; re-fetch and retry. */
public final class OptimisticLockConflictException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public OptimisticLockConflictException(
            String entityType, String entityId, long expectedVersion) {
        super(
                ErrorCode.PER_7001_OPTIMISTIC_LOCK_CONFLICT,
                ErrorCategory.CONFLICT,
                entityType
                        + " "
                        + entityId
                        + " was modified concurrently (expected version "
                        + expectedVersion
                        + "). Re-fetch the current state and retry.");
    }
}
