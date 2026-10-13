package com.eternax.recon.events;

import java.util.List;

/**
 * Snapshot of an active reconciliation definition published on a compacted topic, so matching
 * workers never call the orchestration service at runtime (no synchronous coupling on the hot
 * path).
 */
public record DefinitionEvent(
        String definitionId,
        String tenantId,
        String name,
        int version,
        String status,
        String rail,
        List<String> requiredSources,
        long waitWindowSeconds,
        long compositeWindowSeconds,
        long amountToleranceMinor,
        boolean requireOppositeDirection,
        List<com.eternax.recon.domain.MatchingRule> rules) {

    /** Form used before 1.2: no explicit rule list, so the default plan applies. */
    public DefinitionEvent(
            String definitionId,
            String tenantId,
            String name,
            int version,
            String status,
            String rail,
            List<String> requiredSources,
            long waitWindowSeconds,
            long compositeWindowSeconds,
            long amountToleranceMinor,
            boolean requireOppositeDirection) {
        this(
                definitionId,
                tenantId,
                name,
                version,
                status,
                rail,
                requiredSources,
                waitWindowSeconds,
                compositeWindowSeconds,
                amountToleranceMinor,
                requireOppositeDirection,
                null);
    }

    public String partitionKey() {
        return tenantId + "|" + definitionId;
    }
}
