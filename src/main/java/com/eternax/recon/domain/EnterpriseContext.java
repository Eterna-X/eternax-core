package com.eternax.recon.domain;

import java.util.Optional;

/** Where a record sits in the customer's organisation and business references (HLD 6.4). */
public record EnterpriseContext(
        Optional<String> entityId,
        Optional<String> businessUnit,
        Optional<String> counterpartyId,
        Optional<String> invoiceReference,
        Optional<String> externalReference,
        Optional<String> settlementReference) {

    public EnterpriseContext {
        entityId = entityId == null ? Optional.empty() : entityId;
        businessUnit = businessUnit == null ? Optional.empty() : businessUnit;
        counterpartyId = counterpartyId == null ? Optional.empty() : counterpartyId;
        invoiceReference = invoiceReference == null ? Optional.empty() : invoiceReference;
        externalReference = externalReference == null ? Optional.empty() : externalReference;
        settlementReference = settlementReference == null ? Optional.empty() : settlementReference;
    }

    public static EnterpriseContext empty() {
        return new EnterpriseContext(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
    }
}
