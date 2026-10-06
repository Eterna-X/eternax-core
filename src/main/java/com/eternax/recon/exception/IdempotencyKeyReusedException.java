package com.eternax.recon.exception;

/** An Idempotency-Key was reused with a different request body. */
public final class IdempotencyKeyReusedException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public IdempotencyKeyReusedException(String key, String detail) {
        super(
                ErrorCode.REQ_7502_IDEMPOTENCY_KEY_REUSED,
                ErrorCategory.CONFLICT,
                "Idempotency-Key '" + key + "': " + detail);
    }

    public IdempotencyKeyReusedException(String key) {
        super(
                ErrorCode.REQ_7502_IDEMPOTENCY_KEY_REUSED,
                ErrorCategory.CONFLICT,
                "Idempotency-Key '" + key + "' was already used with a different request body.");
    }
}
