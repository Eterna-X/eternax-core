package com.eternax.recon.exception;

import java.util.List;

/** A source record failed schema or mandatory-field validation (HLD 18.2). */
public final class SchemaValidationException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    private final transient List<String> violations;

    public SchemaValidationException(
            String sourceId, String recordReference, List<String> violations) {
        super(
                ErrorCode.ING_2001_SCHEMA_VALIDATION_FAILED,
                ErrorCategory.VALIDATION,
                "Record '"
                        + recordReference
                        + "' from source '"
                        + sourceId
                        + "' failed validation with "
                        + violations.size()
                        + " violation(s): "
                        + String.join("; ", violations));
        this.violations = List.copyOf(violations);
    }

    public List<String> violations() {
        return violations;
    }
}
