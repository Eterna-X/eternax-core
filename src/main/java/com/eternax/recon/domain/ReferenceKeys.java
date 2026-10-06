package com.eternax.recon.domain;

import java.util.Optional;

/** Reference keys used for matching (HLD 6.1). Any key may be absent. */
public record ReferenceKeys(
        Optional<String> retrievalReferenceNumber,
        Optional<String> uniqueTransactionReference,
        Optional<String> systemTraceAuditNumber,
        Optional<String> authorizationCode,
        Optional<String> acquirerReferenceNumber,
        Optional<String> batchId) {

    public ReferenceKeys {
        retrievalReferenceNumber = orEmpty(retrievalReferenceNumber);
        uniqueTransactionReference = orEmpty(uniqueTransactionReference);
        systemTraceAuditNumber = orEmpty(systemTraceAuditNumber);
        authorizationCode = orEmpty(authorizationCode);
        acquirerReferenceNumber = orEmpty(acquirerReferenceNumber);
        batchId = orEmpty(batchId);
    }

    /** The key counterparts share: RRN for card and instant rails, UTR for bank transfers. */
    public Optional<String> primaryKey() {
        return retrievalReferenceNumber.or(() -> uniqueTransactionReference);
    }

    public static ReferenceKeys none() {
        return new ReferenceKeys(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
    }

    public static ReferenceKeys ofRrn(String rrn) {
        return new ReferenceKeys(
                Optional.ofNullable(rrn),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
    }

    private static Optional<String> orEmpty(Optional<String> value) {
        return value == null ? Optional.empty() : value;
    }
}
