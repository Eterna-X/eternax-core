package com.eternax.recon.exception;

/** A domain invariant was violated while constructing or mutating a domain object. */
public final class InvalidDomainStateException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public InvalidDomainStateException(String message) {
        super(ErrorCode.DOM_1001_INVALID_STATE, ErrorCategory.VALIDATION, message);
    }
}
