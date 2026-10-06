package com.eternax.recon.exception;

/** A reconciliation definition was moved through its lifecycle illegally. */
public final class DefinitionLifecycleException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public DefinitionLifecycleException(String message) {
        super(ErrorCode.ORC_6502_DEFINITION_LIFECYCLE_VIOLATION, ErrorCategory.CONFLICT, message);
    }
}
