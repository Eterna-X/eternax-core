package com.eternax.recon.exception;

import java.time.Duration;

/** A downstream dependency timed out after all retries. */
public final class DownstreamTimeoutException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public DownstreamTimeoutException(
            String dependency, Duration timeout, int attempts, Throwable cause) {
        super(
                ErrorCode.INF_8001_DOWNSTREAM_TIMEOUT,
                ErrorCategory.UNAVAILABLE,
                "Call to '"
                        + dependency
                        + "' timed out after "
                        + attempts
                        + " attempt(s), each bounded at "
                        + timeout
                        + ".",
                cause);
    }
}
