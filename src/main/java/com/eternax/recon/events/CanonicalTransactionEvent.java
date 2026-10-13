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
        List<Fee> fees,
        String settlementKey,
        String settlementRef,
        String recordKind,
        java.util.Map<String, String> attributes) {

    /** What the record is: a payment transaction, or one side of a settlement comparison. */
    public static final String KIND_TRANSACTION = "TRANSACTION";

    public static final String KIND_SETTLEMENT_EXPECTED = "SETTLEMENT_EXPECTED";
    public static final String KIND_SETTLEMENT_ACTUAL = "SETTLEMENT_ACTUAL";

    /** Form used before 1.2: an ordinary transaction without settlement attributes. */
    public CanonicalTransactionEvent(
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
        this(
                transactionId,
                tenantId,
                sourceId,
                feedId,
                recordHash,
                rail,
                direction,
                amountMinor,
                currency,
                transactionTimeUtc,
                sourceTimeZone,
                valueDate,
                postingDate,
                settlementDate,
                rrn,
                utr,
                stan,
                authCode,
                arn,
                batchReference,
                terminalId,
                merchantId,
                channel,
                transactionStatus,
                settlementStatus,
                accountReferenceToken,
                parentTransactionId,
                vaultLocation,
                vaultChecksum,
                fees,
                null,
                null,
                null,
                null);
    }

    /** Form used before attributes existed: settlement fields, no extra attributes. */
    public CanonicalTransactionEvent(
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
            List<Fee> fees,
            String settlementKey,
            String settlementRef,
            String recordKind) {
        this(
                transactionId,
                tenantId,
                sourceId,
                feedId,
                recordHash,
                rail,
                direction,
                amountMinor,
                currency,
                transactionTimeUtc,
                sourceTimeZone,
                valueDate,
                postingDate,
                settlementDate,
                rrn,
                utr,
                stan,
                authCode,
                arn,
                batchReference,
                terminalId,
                merchantId,
                channel,
                transactionStatus,
                settlementStatus,
                accountReferenceToken,
                parentTransactionId,
                vaultLocation,
                vaultChecksum,
                fees,
                settlementKey,
                settlementRef,
                recordKind,
                null);
    }

    /** True for either side of a settlement comparison. */
    public boolean isSettlement() {
        return recordKind != null && recordKind.startsWith("SETTLEMENT_");
    }

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
