package com.eternax.recon.exception;

/** No entity of the given type exists with the given identifier for the caller's tenant. */
public final class EntityNotFoundException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(String entityType, String entityId) {
        super(
                ErrorCode.PER_7002_ENTITY_NOT_FOUND,
                ErrorCategory.NOT_FOUND,
                entityType + " '" + entityId + "' was not found.");
    }
}
