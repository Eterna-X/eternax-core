package com.eternax.recon.events;

import java.time.Instant;

/**
 * A record passed ingestion but could not be turned into a canonical transaction (negative amount,
 * unparseable date, unmapped code, ...). Published by the normalizer so that the originating batch
 * can show the failure instead of reporting a clean ACCEPTED (NRM-05).
 *
 * @param recordReference the {@code batchId:rowNumber} reference carried by the raw record
 * @param errorCode stable code from {@code ErrorCode}
 * @param reason human readable cause, without the record's data values
 */
public record RecordRejectedEvent(
        String tenantId,
        String sourceId,
        String batchId,
        String recordReference,
        String errorCode,
        String reason,
        Instant atUtc) {

    public String partitionKey() {
        return tenantId + "|" + sourceId;
    }
}
