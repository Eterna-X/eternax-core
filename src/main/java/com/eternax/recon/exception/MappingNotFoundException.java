package com.eternax.recon.exception;

/** No active field mapping exists for a tenant's source. */
public final class MappingNotFoundException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public MappingNotFoundException(String tenantId, String sourceId) {
        super(
                ErrorCode.NRM_3001_MAPPING_NOT_FOUND,
                ErrorCategory.NOT_FOUND,
                "No active mapping for source '" + sourceId + "' of tenant '" + tenantId + "'.");
    }
}
