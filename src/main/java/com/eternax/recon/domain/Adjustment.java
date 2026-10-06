package com.eternax.recon.domain;

import com.eternax.recon.common.Markers.AdjustmentMarker;
import com.eternax.recon.common.Markers.CaseMarker;
import com.eternax.recon.common.Markers.TenantMarker;
import com.eternax.recon.common.Money;
import com.eternax.recon.common.TypedId;
import com.eternax.recon.exception.ApprovalRequiredException;
import com.eternax.recon.exception.InvalidDomainStateException;
import com.eternax.recon.exception.SeparationOfDutiesViolationException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * A proposed or posted correction (HLD 10.5 / 10.7). Maker-checker and separation of duties are
 * enforced by the aggregate itself: the proposer (maker) and each approver must be different
 * people.
 */
public final class Adjustment {

    private final TypedId<AdjustmentMarker> adjustmentId;
    private final TypedId<TenantMarker> tenantId;
    private final TypedId<CaseMarker> caseId;
    private final AdjustmentType type;
    private final Money amount;
    private final String narrative;
    private final String proposedBy;
    private final Instant proposedAtUtc;
    private Optional<String> firstApprover = Optional.empty();
    private Optional<String> secondApprover = Optional.empty();
    private Optional<String> journalReference = Optional.empty();
    private AdjustmentStatus status = AdjustmentStatus.PROPOSED;
    private long version;

    public Adjustment(
            TypedId<AdjustmentMarker> adjustmentId,
            TypedId<TenantMarker> tenantId,
            TypedId<CaseMarker> caseId,
            AdjustmentType type,
            Money amount,
            String narrative,
            String proposedBy,
            Instant proposedAtUtc) {
        this.adjustmentId = Objects.requireNonNull(adjustmentId);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.caseId = Objects.requireNonNull(caseId);
        this.type = Objects.requireNonNull(type);
        this.amount = Objects.requireNonNull(amount);
        if (amount.amountMinor() <= 0) {
            throw new InvalidDomainStateException("adjustment amount must be positive");
        }
        this.narrative = narrative == null ? "" : narrative;
        if (proposedBy == null || proposedBy.isBlank()) {
            throw new IllegalArgumentException("proposedBy must not be blank");
        }
        this.proposedBy = proposedBy;
        this.proposedAtUtc = Objects.requireNonNull(proposedAtUtc);
    }

    public static Adjustment restore(
            Adjustment base,
            AdjustmentStatus status,
            Optional<String> firstApprover,
            Optional<String> secondApprover,
            Optional<String> journalReference,
            long version) {
        base.status = status;
        base.firstApprover = firstApprover;
        base.secondApprover = secondApprover;
        base.journalReference = journalReference;
        base.version = version;
        return base;
    }

    /**
     * First approval (the "maker approval" of the LLD). When {@code requiresSecondApproval} is
     * false the adjustment is fully approved after this step.
     */
    public void makerApprove(String approver, boolean requiresSecondApproval) {
        requireStatus(AdjustmentStatus.PROPOSED, "maker-approve");
        if (proposedBy.equals(approver)) {
            throw new SeparationOfDutiesViolationException(adjustmentId.value(), approver);
        }
        firstApprover = Optional.of(approver);
        status =
                requiresSecondApproval
                        ? AdjustmentStatus.MAKER_APPROVED
                        : AdjustmentStatus.CHECKER_APPROVED;
    }

    /** Second approval, required only above the configured value threshold. */
    public void checkerApprove(String approver) {
        requireStatus(AdjustmentStatus.MAKER_APPROVED, "checker-approve");
        if (proposedBy.equals(approver) || firstApprover.filter(approver::equals).isPresent()) {
            throw new SeparationOfDutiesViolationException(adjustmentId.value(), approver);
        }
        secondApprover = Optional.of(approver);
        status = AdjustmentStatus.CHECKER_APPROVED;
    }

    public void assertPostable() {
        if (status != AdjustmentStatus.CHECKER_APPROVED) {
            throw new ApprovalRequiredException(
                    "Adjustment "
                            + adjustmentId
                            + " is "
                            + status
                            + "; it must be fully approved before posting.");
        }
    }

    public void markPosted(String journalRef) {
        assertPostable();
        this.journalReference = Optional.of(Objects.requireNonNull(journalRef));
        this.status = AdjustmentStatus.POSTED;
    }

    public void markConfirmed() {
        requireStatus(AdjustmentStatus.POSTED, "confirm");
        this.status = AdjustmentStatus.CONFIRMED;
    }

    public void reject(String actor) {
        if (status == AdjustmentStatus.POSTED
                || status == AdjustmentStatus.CONFIRMED
                || status == AdjustmentStatus.REJECTED) {
            throw new InvalidDomainStateException(
                    "Adjustment "
                            + adjustmentId
                            + " is "
                            + status
                            + " and can no longer be rejected.");
        }
        Objects.requireNonNull(actor);
        this.status = AdjustmentStatus.REJECTED;
    }

    private void requireStatus(AdjustmentStatus expected, String action) {
        if (status != expected) {
            throw new InvalidDomainStateException(
                    "Cannot "
                            + action
                            + " adjustment "
                            + adjustmentId
                            + " in status "
                            + status
                            + "; expected "
                            + expected
                            + ".");
        }
    }

    public TypedId<AdjustmentMarker> adjustmentId() {
        return adjustmentId;
    }

    public TypedId<TenantMarker> tenantId() {
        return tenantId;
    }

    public TypedId<CaseMarker> caseId() {
        return caseId;
    }

    public AdjustmentType type() {
        return type;
    }

    public Money amount() {
        return amount;
    }

    public String narrative() {
        return narrative;
    }

    public String proposedBy() {
        return proposedBy;
    }

    public Instant proposedAtUtc() {
        return proposedAtUtc;
    }

    public Optional<String> firstApprover() {
        return firstApprover;
    }

    public Optional<String> secondApprover() {
        return secondApprover;
    }

    public Optional<String> journalReference() {
        return journalReference;
    }

    public AdjustmentStatus status() {
        return status;
    }

    public long version() {
        return version;
    }
}
