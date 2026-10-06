package com.eternax.recon.exception;

/** An action needs approvals that have not been completed yet. */
public final class ApprovalRequiredException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public ApprovalRequiredException(String message) {
        super(ErrorCode.ADJ_6001_APPROVAL_REQUIRED, ErrorCategory.FORBIDDEN, message);
    }
}
