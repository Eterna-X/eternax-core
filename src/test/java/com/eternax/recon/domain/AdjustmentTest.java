package com.eternax.recon.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eternax.recon.common.Money;
import com.eternax.recon.common.TypedId;
import com.eternax.recon.exception.ApprovalRequiredException;
import com.eternax.recon.exception.SeparationOfDutiesViolationException;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class AdjustmentTest {

    private Adjustment proposed(long amount) {
        return new Adjustment(
                TypedId.generate(),
                TypedId.of("bank1"),
                TypedId.generate(),
                AdjustmentType.GENERAL_LEDGER_JOURNAL,
                Money.inr(amount),
                "n",
                "maker",
                Instant.now());
    }

    @Test
    void proposerCannotApproveOwnAdjustment() {
        Adjustment a = proposed(500);
        assertThatThrownBy(() -> a.makerApprove("maker", false))
                .isInstanceOf(SeparationOfDutiesViolationException.class);
    }

    @Test
    void belowThreshold_singleApprovalIsEnough() {
        Adjustment a = proposed(500);
        a.makerApprove("approver1", false);
        assertThat(a.status()).isEqualTo(AdjustmentStatus.CHECKER_APPROVED);
        a.markPosted("J-1");
        assertThat(a.status()).isEqualTo(AdjustmentStatus.POSTED);
    }

    @Test
    void aboveThreshold_needsAThirdDistinctPerson() {
        Adjustment a = proposed(50_000_000);
        a.makerApprove("approver1", true);
        assertThat(a.status()).isEqualTo(AdjustmentStatus.MAKER_APPROVED);
        assertThatThrownBy(() -> a.checkerApprove("approver1"))
                .isInstanceOf(SeparationOfDutiesViolationException.class);
        assertThatThrownBy(() -> a.checkerApprove("maker"))
                .isInstanceOf(SeparationOfDutiesViolationException.class);
        a.checkerApprove("approver2");
        assertThat(a.status()).isEqualTo(AdjustmentStatus.CHECKER_APPROVED);
    }

    @Test
    void cannotPostBeforeFullApproval() {
        Adjustment a = proposed(500);
        assertThatThrownBy(() -> a.markPosted("J-1")).isInstanceOf(ApprovalRequiredException.class);
    }
}
