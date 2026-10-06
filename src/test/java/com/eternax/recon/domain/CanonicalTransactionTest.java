package com.eternax.recon.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eternax.recon.common.Money;
import com.eternax.recon.common.TypedId;
import com.eternax.recon.exception.InvalidDomainStateException;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CanonicalTransactionTest {

    private CanonicalTransaction.Builder valid() {
        return CanonicalTransaction.builder()
                .transactionId(TypedId.generate())
                .tenantId(TypedId.of("bank1"))
                .sourceId("bank_switch")
                .feedId("f1")
                .recordHash("h")
                .rail(Rail.UPI)
                .direction(Direction.DEBIT)
                .amount(Money.inr(10_000))
                .transactionTimeUtc(Instant.parse("2026-09-30T10:00:00Z"))
                .valueDate(LocalDate.of(2026, 9, 30))
                .postingDate(LocalDate.of(2026, 9, 30))
                .accountReferenceToken("tok")
                .rawRecordPointer(new RawRecordPointer("vault://x", "abc"));
    }

    @Test
    void build_succeedsWithRequiredFields() {
        assertThat(valid().build().reconciliationStatus())
                .isEqualTo(ReconciliationStatus.UNMATCHED);
    }

    @Test
    void build_rejectsZeroAmountOnRealRail() {
        assertThatThrownBy(() -> valid().amount(Money.inr(0)).build())
                .isInstanceOf(InvalidDomainStateException.class);
    }

    @Test
    void withReconciliationStatus_returnsNewInstance() {
        CanonicalTransaction original = valid().build();
        CanonicalTransaction matched =
                original.withReconciliationStatus(ReconciliationStatus.MATCHED);
        assertThat(original.reconciliationStatus()).isEqualTo(ReconciliationStatus.UNMATCHED);
        assertThat(matched.reconciliationStatus()).isEqualTo(ReconciliationStatus.MATCHED);
    }
}
