package com.eternax.recon.exception;

/** A downstream dependency is failing or its circuit breaker is open. */
public final class DownstreamUnavailableException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public DownstreamUnavailableException(String dependency, Throwable cause) {
        super(
                ErrorCode.INF_8002_DOWNSTREAM_UNAVAILABLE,
                ErrorCategory.UNAVAILABLE,
                "Dependency '" + dependency + "' is unavailable; retry after backoff.",
                cause);
    }
}
