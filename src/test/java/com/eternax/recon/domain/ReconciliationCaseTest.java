package com.eternax.recon.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eternax.recon.common.Money;
import com.eternax.recon.common.TypedId;
import com.eternax.recon.exception.IllegalCaseTransitionException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ReconciliationCaseTest {

    private static final Instant NOW = Instant.parse("2026-09-30T10:00:00Z");

    private ReconciliationCase newCase() {
        return new ReconciliationCase(
                TypedId.generate(),
                TypedId.of("bank1"),
                "brk-1",
                List.of(TypedId.generate()),
                ReasonCode.MISSING_SOURCE,
                Money.inr(10_000),
                NOW,
                Optional.empty());
    }

    @Test
    void happyPath_walksTheWholeLifecycle() {
        ReconciliationCase c = newCase();
        c.transitionTo(CaseState.CATEGORIZED, "system", "classified", NOW);
        c.transitionTo(CaseState.ASSIGNED, "system", "rule", NOW);
        c.transitionTo(CaseState.INVESTIGATING, "analyst1", "started", NOW);
        c.transitionTo(CaseState.RESOLUTION_PROPOSED, "analyst1", "adjustment", NOW);
        c.transitionTo(CaseState.PENDING_APPROVAL, "analyst1", "submitted", NOW);
        c.transitionTo(CaseState.RESOLVED, "checker", "posted", NOW);
        c.transitionTo(CaseState.RECONCILED, "system", "seen in extract", NOW);
        c.transitionTo(CaseState.CLOSED, "system", "done", NOW);

        assertThat(c.state()).isEqualTo(CaseState.CLOSED);
        assertThat(c.history()).hasSize(9);
    }

    @Test
    void illegalTransition_isRejectedWithBothStatesInMessage() {
        ReconciliationCase c = newCase();
        assertThatThrownBy(() -> c.transitionTo(CaseState.CLOSED, "analyst1", "skip", NOW))
                .isInstanceOf(IllegalCaseTransitionException.class)
                .hasMessageContaining("DETECTED")
                .hasMessageContaining("CLOSED");
        assertThat(c.state()).isEqualTo(CaseState.DETECTED);
        assertThat(c.history()).hasSize(1);
    }

    @Test
    void closedCase_canOnlyBeReopenedToInvestigating() {
        ReconciliationCase c = newCase();
        c.transitionTo(CaseState.CATEGORIZED, "s", "r", NOW);
        c.transitionTo(CaseState.RESOLVED, "s", "auto", NOW);
        c.transitionTo(CaseState.CLOSED, "s", "auto", NOW);

        assertThatThrownBy(() -> c.transitionTo(CaseState.CLOSED, "a", "again", NOW))
                .isInstanceOf(IllegalCaseTransitionException.class);
        c.transitionTo(CaseState.INVESTIGATING, "a", "reopen", NOW);
        assertThat(c.state()).isEqualTo(CaseState.INVESTIGATING);
    }
}
