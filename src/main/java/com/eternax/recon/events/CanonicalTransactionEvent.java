package com.eternax.recon.events;

import java.time.Instant;
import java.util.List;

/**
 * Wire form of {@code CanonicalTransaction}. Flat and free of domain types so that a domain
 * refactor never silently changes the wire contract.
 */
public record CanonicalTransactionEvent(
        String transactionId,
        String tenantId,
        String sourceId,
        String feedId,
        String recordHash,
        String rail,
        String direction,
        long amountMinor,
        String currency,
        Instant transactionTimeUtc,
        String sourceTimeZone,
        String valueDate,
        String postingDate,
        String settlementDate,
        String rrn,
        String utr,
        String stan,
        String authCode,
        String arn,
        String batchReference,
        String terminalId,
        String merchantId,
        String channel,
        String transactionStatus,
        String settlementStatus,
        String accountReferenceToken,
        String parentTransactionId,
        String vaultLocation,
        String vaultChecksum,
        List<Fee> fees) {

    /** A fee component in minor units. */
    public record Fee(String feeType, long amountMinor) {}

    /** Business key shared by all counterparts: RRN, else UTR, else this record alone. */
    public String matchKey() {
        if (rrn != null && !rrn.isBlank()) {
            return rrn;
        }
        if (utr != null && !utr.isBlank()) {
            return utr;
        }
        return "txn:" + transactionId;
    }

    /**
     * Kafka key. Records of one business transaction share (tenant, rail, match key) so all
     * counterparts are consumed by the same partition (HLD 13.2).
     */
    public String partitionKey() {
        return tenantId + "|" + rail + "|" + matchKey();
    }
}
