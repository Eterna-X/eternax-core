package com.eternax.recon.exception;

/** Another worker currently holds the run for the same definition and period. */
public final class RunAlreadyInProgressException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public RunAlreadyInProgressException(String definitionId, String periodKey) {
        super(
                ErrorCode.ORC_6501_RUN_ALREADY_IN_PROGRESS,
                ErrorCategory.CONFLICT,
                "A run for definition "
                        + definitionId
                        + " and period "
                        + periodKey
                        + " is already executing; it will complete the work.");
    }
}
