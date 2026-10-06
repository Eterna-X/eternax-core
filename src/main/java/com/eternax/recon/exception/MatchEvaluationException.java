package com.eternax.recon.exception;

/** A matching strategy could not evaluate its candidates (malformed data, not "no match"). */
public final class MatchEvaluationException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public MatchEvaluationException(String message, Throwable cause) {
        super(ErrorCode.MCH_4001_MATCH_EVALUATION_FAILED, ErrorCategory.INTERNAL, message, cause);
    }
}
