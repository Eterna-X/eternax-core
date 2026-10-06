package com.eternax.recon.exception;

/** An allocation would push a transaction's total allocated amount above its own amount. */
public final class OverAllocationException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public OverAllocationException(
            String transactionId, long transactionAmountMinor, long attemptedTotalMinor) {
        super(
                ErrorCode.DOM_1003_OVER_ALLOCATION,
                ErrorCategory.CONFLICT,
                "Transaction "
                        + transactionId
                        + " cannot be allocated "
                        + attemptedTotalMinor
                        + " minor units: its total amount is only "
                        + transactionAmountMinor
                        + " (HLD 7.6: a record must not be matched beyond its amount).");
    }
}
