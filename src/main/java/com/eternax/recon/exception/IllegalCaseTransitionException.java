package com.eternax.recon.exception;

/** A case was asked to move to a state that is not reachable from its current state. */
public final class IllegalCaseTransitionException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public IllegalCaseTransitionException(String caseId, Object current, Object attempted) {
        super(
                ErrorCode.EXC_5002_ILLEGAL_CASE_TRANSITION,
                ErrorCategory.CONFLICT,
                "Case "
                        + caseId
                        + " cannot move from state "
                        + current
                        + " to "
                        + attempted
                        + "; the transition is not allowed by the case lifecycle (HLD 10.3).");
    }
}
