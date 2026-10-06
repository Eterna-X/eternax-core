package com.eternax.recon.exception;

/** The same person tried to act twice on one maker-checker item. */
public final class SeparationOfDutiesViolationException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public SeparationOfDutiesViolationException(String adjustmentId, String actor) {
        super(
                ErrorCode.ADJ_6002_SEPARATION_OF_DUTIES_VIOLATION,
                ErrorCategory.CONFLICT,
                "User '"
                        + actor
                        + "' already acted on adjustment "
                        + adjustmentId
                        + "; maker-checker requires a different person at each step (HLD 20.2).");
    }
}
