package com.eternax.recon.domain;

import com.eternax.recon.common.Markers.TenantMarker;
import com.eternax.recon.common.Markers.TransactionMarker;
import com.eternax.recon.common.Money;
import com.eternax.recon.common.TypedId;
import com.eternax.recon.exception.InvalidDomainStateException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The canonical financial record every source is mapped onto (HLD 6.1). Immutable: a change such as
 * a new reconciliation status yields a new instance, preserving the append-only principle (HLD
 * 5.1).
 */
public record CanonicalTransaction(
        TypedId<TransactionMarker> transactionId,
        TypedId<TenantMarker> tenantId,
        String sourceId,
        String feedId,
        String recordHash,
        Rail rail,
        Direction direction,
        Money amount,
        Instant transactionTimeUtc,
        ZoneId sourceTimeZone,
        LocalDate valueDate,
        LocalDate postingDate,
        Optional<LocalDate> settlementDate,
        ReferenceKeys referenceKeys,
        Optional<String> terminalId,
        Optional<String> merchantId,
        String channel,
        TransactionStatus transactionStatus,
        SettlementStatus settlementStatus,
        ReconciliationStatus reconciliationStatus,
        List<FeeComponent> feeComponents,
        String accountReferenceToken,
        Optional<TypedId<TransactionMarker>> parentTransactionId,
        EnterpriseContext enterpriseContext,
        RawRecordPointer rawRecordPointer) {

    public CanonicalTransaction {
        Objects.requireNonNull(transactionId, "transactionId must not be null");
        Objects.requireNonNull(tenantId, "tenantId must not be null");
        requireNonBlank(sourceId, "sourceId");
        requireNonBlank(feedId, "feedId");
        requireNonBlank(recordHash, "recordHash");
        Objects.requireNonNull(rail, "rail must not be null");
        Objects.requireNonNull(direction, "direction must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(transactionTimeUtc, "transactionTimeUtc must not be null");
        Objects.requireNonNull(sourceTimeZone, "sourceTimeZone must not be null");
        Objects.requireNonNull(valueDate, "valueDate must not be null");
        Objects.requireNonNull(postingDate, "postingDate must not be null");
        settlementDate = settlementDate == null ? Optional.empty() : settlementDate;
        referenceKeys = referenceKeys == null ? ReferenceKeys.none() : referenceKeys;
        terminalId = terminalId == null ? Optional.empty() : terminalId;
        merchantId = merchantId == null ? Optional.empty() : merchantId;
        requireNonBlank(channel, "channel");
        Objects.requireNonNull(transactionStatus, "transactionStatus must not be null");
        settlementStatus =
                settlementStatus == null ? SettlementStatus.NOT_APPLICABLE : settlementStatus;
        reconciliationStatus =
                reconciliationStatus == null
                        ? ReconciliationStatus.UNMATCHED
                        : reconciliationStatus;
        feeComponents = feeComponents == null ? List.of() : List.copyOf(feeComponents);
        requireNonBlank(accountReferenceToken, "accountReferenceToken");
        parentTransactionId = parentTransactionId == null ? Optional.empty() : parentTransactionId;
        enterpriseContext =
                enterpriseContext == null ? EnterpriseContext.empty() : enterpriseContext;
        Objects.requireNonNull(rawRecordPointer, "rawRecordPointer must not be null");
        if (amount.amountMinor() < 0) {
            throw new InvalidDomainStateException(
                    "CanonicalTransaction "
                            + transactionId
                            + " has a negative amount; use direction.");
        }
        if (amount.isZero() && rail != Rail.OTHER) {
            throw new InvalidDomainStateException(
                    "CanonicalTransaction "
                            + transactionId
                            + " has a zero amount, invalid for rail "
                            + rail);
        }
    }

    private static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidDomainStateException(field + " must not be null or blank");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .transactionId(transactionId)
                .tenantId(tenantId)
                .sourceId(sourceId)
                .feedId(feedId)
                .recordHash(recordHash)
                .rail(rail)
                .direction(direction)
                .amount(amount)
                .transactionTimeUtc(transactionTimeUtc)
                .sourceTimeZone(sourceTimeZone)
                .valueDate(valueDate)
                .postingDate(postingDate)
                .settlementDate(settlementDate)
                .referenceKeys(referenceKeys)
                .terminalId(terminalId)
                .merchantId(merchantId)
                .channel(channel)
                .transactionStatus(transactionStatus)
                .settlementStatus(settlementStatus)
                .reconciliationStatus(reconciliationStatus)
                .feeComponents(feeComponents)
                .accountReferenceToken(accountReferenceToken)
                .parentTransactionId(parentTransactionId)
                .enterpriseContext(enterpriseContext)
                .rawRecordPointer(rawRecordPointer);
    }

    /** Returns a copy with a new reconciliation status; this instance is never mutated. */
    public CanonicalTransaction withReconciliationStatus(ReconciliationStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus must not be null");
        return toBuilder().reconciliationStatus(newStatus).build();
    }

    /** Fluent builder; all validation happens once, in {@link #build()}. */
    public static final class Builder {
        private TypedId<TransactionMarker> transactionId;
        private TypedId<TenantMarker> tenantId;
        private String sourceId;
        private String feedId;
        private String recordHash;
        private Rail rail;
        private Direction direction;
        private Money amount;
        private Instant transactionTimeUtc;
        private ZoneId sourceTimeZone = ZoneId.of("Asia/Kolkata");
        private LocalDate valueDate;
        private LocalDate postingDate;
        private Optional<LocalDate> settlementDate = Optional.empty();
        private ReferenceKeys referenceKeys = ReferenceKeys.none();
        private Optional<String> terminalId = Optional.empty();
        private Optional<String> merchantId = Optional.empty();
        private String channel = "UNKNOWN";
        private TransactionStatus transactionStatus = TransactionStatus.UNKNOWN;
        private SettlementStatus settlementStatus = SettlementStatus.NOT_APPLICABLE;
        private ReconciliationStatus reconciliationStatus = ReconciliationStatus.UNMATCHED;
        private List<FeeComponent> feeComponents = List.of();
        private String accountReferenceToken;
        private Optional<TypedId<TransactionMarker>> parentTransactionId = Optional.empty();
        private EnterpriseContext enterpriseContext = EnterpriseContext.empty();
        private RawRecordPointer rawRecordPointer;

        public Builder transactionId(TypedId<TransactionMarker> v) {
            this.transactionId = v;
            return this;
        }

        public Builder tenantId(TypedId<TenantMarker> v) {
            this.tenantId = v;
            return this;
        }

        public Builder sourceId(String v) {
            this.sourceId = v;
            return this;
        }

        public Builder feedId(String v) {
            this.feedId = v;
            return this;
        }

        public Builder recordHash(String v) {
            this.recordHash = v;
            return this;
        }

        public Builder rail(Rail v) {
            this.rail = v;
            return this;
        }

        public Builder direction(Direction v) {
            this.direction = v;
            return this;
        }

        public Builder amount(Money v) {
            this.amount = v;
            return this;
        }

        public Builder transactionTimeUtc(Instant v) {
            this.transactionTimeUtc = v;
            return this;
        }

        public Builder sourceTimeZone(ZoneId v) {
            this.sourceTimeZone = v;
            return this;
        }

        public Builder valueDate(LocalDate v) {
            this.valueDate = v;
            return this;
        }

        public Builder postingDate(LocalDate v) {
            this.postingDate = v;
            return this;
        }

        public Builder settlementDate(Optional<LocalDate> v) {
            this.settlementDate = v;
            return this;
        }

        public Builder referenceKeys(ReferenceKeys v) {
            this.referenceKeys = v;
            return this;
        }

        public Builder terminalId(Optional<String> v) {
            this.terminalId = v;
            return this;
        }

        public Builder merchantId(Optional<String> v) {
            this.merchantId = v;
            return this;
        }

        public Builder channel(String v) {
            this.channel = v;
            return this;
        }

        public Builder transactionStatus(TransactionStatus v) {
            this.transactionStatus = v;
            return this;
        }

        public Builder settlementStatus(SettlementStatus v) {
            this.settlementStatus = v;
            return this;
        }

        public Builder reconciliationStatus(ReconciliationStatus v) {
            this.reconciliationStatus = v;
            return this;
        }

        public Builder feeComponents(List<FeeComponent> v) {
            this.feeComponents = v;
            return this;
        }

        public Builder accountReferenceToken(String v) {
            this.accountReferenceToken = v;
            return this;
        }

        public Builder parentTransactionId(Optional<TypedId<TransactionMarker>> v) {
            this.parentTransactionId = v;
            return this;
        }

        public Builder enterpriseContext(EnterpriseContext v) {
            this.enterpriseContext = v;
            return this;
        }

        public Builder rawRecordPointer(RawRecordPointer v) {
            this.rawRecordPointer = v;
            return this;
        }

        public CanonicalTransaction build() {
            return new CanonicalTransaction(
                    transactionId,
                    tenantId,
                    sourceId,
                    feedId,
                    recordHash,
                    rail,
                    direction,
                    amount,
                    transactionTimeUtc,
                    sourceTimeZone,
                    valueDate,
                    postingDate,
                    settlementDate,
                    referenceKeys,
                    terminalId,
                    merchantId,
                    channel,
                    transactionStatus,
                    settlementStatus,
                    reconciliationStatus,
                    feeComponents,
                    accountReferenceToken,
                    parentTransactionId,
                    enterpriseContext,
                    rawRecordPointer);
        }
    }
}
