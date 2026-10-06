package com.eternax.recon.exception;

/** Declared control totals (record count, checksum) disagree with what was actually read. */
public final class ControlTotalMismatchException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public ControlTotalMismatchException(String batchId, long declared, long actual) {
        super(
                ErrorCode.ING_2004_CONTROL_TOTAL_MISMATCH,
                ErrorCategory.VALIDATION,
                "Batch "
                        + batchId
                        + " declares "
                        + declared
                        + " records but "
                        + actual
                        + " were read; the batch is held, not partially processed.");
    }
}
