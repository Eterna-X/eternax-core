package com.eternax.recon.exception;

/** A request arrived without the tenant or user identity the gateway is required to supply. */
public final class MissingRequestContextException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public MissingRequestContextException(String header) {
        super(
                ErrorCode.REQ_7501_MISSING_CONTEXT,
                ErrorCategory.VALIDATION,
                "Required request header '" + header + "' is missing.");
    }
}
