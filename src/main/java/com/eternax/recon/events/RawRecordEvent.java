package com.eternax.recon.events;

import java.time.Instant;
import java.util.Map;

/**
 * One validated, non-duplicate source record leaving ingestion. {@code fields} carries the source
 * columns verbatim; interpretation is the normalizer's job, so ingestion never hardcodes a layout.
 */
public record RawRecordEvent(
        String tenantId,
        String sourceId,
        String feedId,
        String batchId,
        String recordReference,
        String rail,
        Map<String, String> fields,
        String contentHash,
        String vaultLocation,
        String vaultChecksum,
        Instant ingestedAt) {

    /** Kafka key: keeps a source's records ordered per tenant and source. */
    public String partitionKey() {
        return tenantId + "|" + sourceId;
    }
}
