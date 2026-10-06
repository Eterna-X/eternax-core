package com.eternax.recon.exception;

/** A field transformation could not be applied to a record's data. */
public final class TransformationFailedException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public TransformationFailedException(String field, String detail, Throwable cause) {
        super(
                ErrorCode.NRM_3002_TRANSFORMATION_FAILED,
                ErrorCategory.VALIDATION,
                "Transformation of field '" + field + "' failed: " + detail,
                cause);
    }
}
