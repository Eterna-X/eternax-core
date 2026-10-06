package com.eternax.recon.exception;

import java.util.Objects;

/**
 * Single root of every exception thrown by eternaSync.
 *
 * <p>Unchecked on purpose: business-rule failures cross {@code @Transactional} boundaries, and
 * checked exceptions do not roll a Spring transaction back by default, which is a well-known source
 * of silently committed partial work. Callers that expect a specific failure catch its subtype;
 * everything else reaches the global handler, which maps {@link #category()} to a response.
 */
public abstract class EternaSyncException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;
    private final ErrorCategory category;

    protected EternaSyncException(ErrorCode errorCode, ErrorCategory category, String message) {
        this(errorCode, category, message, null);
    }

    protected EternaSyncException(
            ErrorCode errorCode, ErrorCategory category, String message, Throwable cause) {
        super(Objects.requireNonNull(message, "message must not be null"), cause);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
        this.category = Objects.requireNonNull(category, "category must not be null");
    }

    public final ErrorCode errorCode() {
        return errorCode;
    }

    public final ErrorCategory category() {
        return category;
    }
}
