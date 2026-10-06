package com.eternax.recon.exception;

/** A configuration record (rule, mapping, clock, policy) is malformed. */
public final class ConfigurationInvalidException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    /** Stored or built-in configuration is malformed: a server-side fault. */
    public ConfigurationInvalidException(ErrorCode code, String message) {
        super(code, ErrorCategory.INTERNAL, message);
    }

    /** Configuration submitted by a user is malformed: a client-side fault (HTTP 400). */
    public static ConfigurationInvalidException rejected(ErrorCode code, String message) {
        return new ConfigurationInvalidException(code, ErrorCategory.VALIDATION, message);
    }

    private ConfigurationInvalidException(ErrorCode code, ErrorCategory category, String message) {
        super(code, category, message);
    }
}
