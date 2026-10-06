package com.eternax.recon.domain;

import com.eternax.recon.common.Markers.CaseMarker;
import com.eternax.recon.common.Markers.TenantMarker;
import com.eternax.recon.common.Markers.TransactionMarker;
import com.eternax.recon.common.Money;
import com.eternax.recon.common.TypedId;
import com.eternax.recon.exception.IllegalCaseTransitionException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Work item raised for a break. The lifecycle is enforced here, not by callers, so an illegal
 * transition is a rejected operation rather than a silently corrupted status (HLD 10.3).
 *
 * <p>Not thread-safe; callers hold the row's optimistic lock while mutating it.
 */
public final class ReconciliationCase {

    private static final Map<CaseState, Set<CaseState>> ALLOWED = new EnumMap<>(CaseState.class);

    static {
        ALLOWED.put(CaseState.DETECTED, EnumSet.of(CaseState.CATEGORIZED));
        ALLOWED.put(CaseState.CATEGORIZED, EnumSet.of(CaseState.ASSIGNED, CaseState.RESOLVED));
        ALLOWED.put(CaseState.ASSIGNED, EnumSet.of(CaseState.INVESTIGATING));
        ALLOWED.put(
                CaseState.INVESTIGATING,
                EnumSet.of(CaseState.RESOLUTION_PROPOSED, CaseState.ASSIGNED));
        ALLOWED.put(
                CaseState.RESOLUTION_PROPOSED,
                EnumSet.of(
                        CaseState.PENDING_APPROVAL, CaseState.RESOLVED, CaseState.INVESTIGATING));
        ALLOWED.put(
                CaseState.PENDING_APPROVAL,
                EnumSet.of(CaseState.RESOLVED, CaseState.RESOLUTION_PROPOSED));
        ALLOWED.put(CaseState.RESOLVED, EnumSet.of(CaseState.RECONCILED, CaseState.CLOSED));
        ALLOWED.put(CaseState.RECONCILED, EnumSet.of(CaseState.CLOSED));
        ALLOWED.put(CaseState.CLOSED, EnumSet.of(CaseState.INVESTIGATING));
    }

    private final TypedId<CaseMarker> caseId;
    private final TypedId<TenantMarker> tenantId;
    private final String breakId;
    private final List<TypedId<TransactionMarker>> involvedTransactionIds;
    private final ReasonCode reasonCode;
    private final Money financialImpact;
    private final Instant detectedAtUtc;
    private final Optional<Instant> deadlineUtc;
    private final List<CaseHistoryEntry> history;
    private CaseState state;
    private Optional<String> assignedOwner;
    private long version;

    public ReconciliationCase(
            TypedId<CaseMarker> caseId,
            TypedId<TenantMarker> tenantId,
            String breakId,
            List<TypedId<TransactionMarker>> involvedTransactionIds,
            ReasonCode reasonCode,
            Money financialImpact,
            Instant detectedAtUtc,
            Optional<Instant> deadlineUtc) {
        this.caseId = Objects.requireNonNull(caseId, "caseId must not be null");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId must not be null");
        this.breakId = Objects.requireNonNull(breakId, "breakId must not be null");
        this.involvedTransactionIds = List.copyOf(Objects.requireNonNull(involvedTransactionIds));
        if (this.involvedTransactionIds.isEmpty()) {
            throw new IllegalArgumentException("a case must involve at least one transaction");
        }
        this.reasonCode = Objects.requireNonNull(reasonCode, "reasonCode must not be null");
        this.financialImpact =
                Objects.requireNonNull(financialImpact, "financialImpact must not be null");
        this.detectedAtUtc =
                Objects.requireNonNull(detectedAtUtc, "detectedAtUtc must not be null");
        this.deadlineUtc = Objects.requireNonNullElse(deadlineUtc, Optional.empty());
        this.state = CaseState.DETECTED;
        this.assignedOwner = Optional.empty();
        this.history = new ArrayList<>();
        this.history.add(
                new CaseHistoryEntry(CaseState.DETECTED, detectedAtUtc, "system", "case detected"));
    }

    /** Rehydrates a persisted case without replaying transitions. */
    public static ReconciliationCase restore(
            TypedId<CaseMarker> caseId,
            TypedId<TenantMarker> tenantId,
            String breakId,
            List<TypedId<TransactionMarker>> involvedTransactionIds,
            ReasonCode reasonCode,
            Money financialImpact,
            Instant detectedAtUtc,
            Optional<Instant> deadlineUtc,
            CaseState state,
            Optional<String> assignedOwner,
            List<CaseHistoryEntry> history,
            long version) {
        ReconciliationCase restored =
                new ReconciliationCase(
                        caseId,
                        tenantId,
                        breakId,
                        involvedTransactionIds,
                        reasonCode,
                        financialImpact,
                        detectedAtUtc,
                        deadlineUtc);
        restored.state = state;
        restored.assignedOwner = assignedOwner;
        restored.history.clear();
        restored.history.addAll(history);
        restored.version = version;
        return restored;
    }

    public void transitionTo(CaseState newState, String actor, String reason, Instant atUtc) {
        Objects.requireNonNull(newState, "newState must not be null");
        Objects.requireNonNull(atUtc, "atUtc must not be null");
        if (actor == null || actor.isBlank()) {
            throw new IllegalArgumentException("actor must not be blank");
        }
        if (!ALLOWED.getOrDefault(state, Set.of()).contains(newState)) {
            throw new IllegalCaseTransitionException(caseId.value(), state, newState);
        }
        state = newState;
        history.add(new CaseHistoryEntry(newState, atUtc, actor, reason == null ? "" : reason));
    }

    /**
     * Closes the case directly, used only for system-decided closures such as a late counterpart
     * that arrives after a break was raised (HLD 10.7: closes as TIMING). Any open state may be
     * closed this way; a closed case may not.
     */
    public void autoClose(String actor, String reason, Instant atUtc) {
        if (state == CaseState.CLOSED) {
            throw new IllegalCaseTransitionException(caseId.value(), state, CaseState.CLOSED);
        }
        state = CaseState.CLOSED;
        history.add(new CaseHistoryEntry(CaseState.CLOSED, atUtc, actor, reason));
    }

    /**
     * Walks forward through the canonical lifecycle to {@code target}, recording every intermediate
     * step, so external events (an adjustment being posted) can advance a case without skipping the
     * audit trail. Does nothing if the case is already at or beyond {@code target}.
     */
    public void advanceTo(CaseState target, String actor, String reason, Instant atUtc) {
        java.util.List<CaseState> path =
                java.util.List.of(
                        CaseState.DETECTED,
                        CaseState.CATEGORIZED,
                        CaseState.ASSIGNED,
                        CaseState.INVESTIGATING,
                        CaseState.RESOLUTION_PROPOSED,
                        CaseState.PENDING_APPROVAL,
                        CaseState.RESOLVED,
                        CaseState.RECONCILED,
                        CaseState.CLOSED);
        int from = path.indexOf(state);
        int to = path.indexOf(target);
        for (int i = from + 1; i <= to; i++) {
            transitionTo(path.get(i), actor, reason, atUtc);
        }
    }

    public void assignTo(String ownerOrTeam) {
        if (ownerOrTeam == null || ownerOrTeam.isBlank()) {
            throw new IllegalArgumentException("ownerOrTeam must not be blank");
        }
        this.assignedOwner = Optional.of(ownerOrTeam);
    }

    public boolean isEligibleForClosure() {
        return state == CaseState.RECONCILED || state == CaseState.RESOLVED;
    }

    public boolean isOpen() {
        return state != CaseState.CLOSED;
    }

    public static Set<CaseState> allowedFrom(CaseState from) {
        return Set.copyOf(ALLOWED.getOrDefault(from, Set.of()));
    }

    public TypedId<CaseMarker> caseId() {
        return caseId;
    }

    public TypedId<TenantMarker> tenantId() {
        return tenantId;
    }

    public String breakId() {
        return breakId;
    }

    public List<TypedId<TransactionMarker>> involvedTransactionIds() {
        return involvedTransactionIds;
    }

    public ReasonCode reasonCode() {
        return reasonCode;
    }

    public Money financialImpact() {
        return financialImpact;
    }

    public Instant detectedAtUtc() {
        return detectedAtUtc;
    }

    public Optional<Instant> deadlineUtc() {
        return deadlineUtc;
    }

    public CaseState state() {
        return state;
    }

    public Optional<String> assignedOwner() {
        return assignedOwner;
    }

    public List<CaseHistoryEntry> history() {
        return List.copyOf(history);
    }

    public long version() {
        return version;
    }
}
