package com.eternax.recon.exception;

/** Delivery of a journal to the General Ledger failed. */
public final class PostingFailedException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public PostingFailedException(String adjustmentId, String detail, Throwable cause) {
        super(
                ErrorCode.ADJ_6003_POSTING_FAILED,
                ErrorCategory.UNAVAILABLE,
                "Posting adjustment " + adjustmentId + " to the General Ledger failed: " + detail,
                cause);
    }
}
