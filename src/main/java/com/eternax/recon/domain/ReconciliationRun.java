package com.eternax.recon.domain;

import com.eternax.recon.common.Markers.DefinitionMarker;
import com.eternax.recon.common.Markers.RunMarker;
import com.eternax.recon.common.Markers.TenantMarker;
import com.eternax.recon.common.TypedId;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/** One execution of a reconciliation definition for a period (HLD 18.4). */
public final class ReconciliationRun {

    private final TypedId<RunMarker> runId;
    private final TypedId<TenantMarker> tenantId;
    private final TypedId<DefinitionMarker> definitionId;
    private final int definitionVersion;
    private final String periodKey;
    private final RunTrigger trigger;
    private final Instant startedAtUtc;
    private Optional<Instant> completedAtUtc = Optional.empty();
    private RunStatus status = RunStatus.RUNNING;
    private long matchedCount;
    private long exceptionCount;

    public ReconciliationRun(
            TypedId<RunMarker> runId,
            TypedId<TenantMarker> tenantId,
            TypedId<DefinitionMarker> definitionId,
            int definitionVersion,
            String periodKey,
            RunTrigger trigger,
            Instant startedAtUtc) {
        this.runId = Objects.requireNonNull(runId);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.definitionId = Objects.requireNonNull(definitionId);
        this.definitionVersion = definitionVersion;
        this.periodKey = Objects.requireNonNull(periodKey);
        this.trigger = Objects.requireNonNull(trigger);
        this.startedAtUtc = Objects.requireNonNull(startedAtUtc);
    }

    public static ReconciliationRun restore(
            ReconciliationRun base,
            RunStatus status,
            Optional<Instant> completedAtUtc,
            long matchedCount,
            long exceptionCount) {
        base.status = status;
        base.completedAtUtc = completedAtUtc;
        base.matchedCount = matchedCount;
        base.exceptionCount = exceptionCount;
        return base;
    }

    /** Finishes the run; the status is derived from the counts so it can never be misreported. */
    public void complete(long matched, long exceptions, Instant atUtc) {
        if (status != RunStatus.RUNNING) {
            throw new IllegalStateException("run " + runId + " is already " + status);
        }
        this.matchedCount = matched;
        this.exceptionCount = exceptions;
        this.status = exceptions > 0 ? RunStatus.COMPLETED_WITH_EXCEPTIONS : RunStatus.COMPLETED;
        this.completedAtUtc = Optional.of(Objects.requireNonNull(atUtc));
    }

    public void fail(Instant atUtc) {
        if (status != RunStatus.RUNNING) {
            throw new IllegalStateException("run " + runId + " is already " + status);
        }
        this.status = RunStatus.FAILED;
        this.completedAtUtc = Optional.of(Objects.requireNonNull(atUtc));
    }

    public TypedId<RunMarker> runId() {
        return runId;
    }

    public TypedId<TenantMarker> tenantId() {
        return tenantId;
    }

    public TypedId<DefinitionMarker> definitionId() {
        return definitionId;
    }

    public int definitionVersion() {
        return definitionVersion;
    }

    public String periodKey() {
        return periodKey;
    }

    public RunTrigger trigger() {
        return trigger;
    }

    public Instant startedAtUtc() {
        return startedAtUtc;
    }

    public Optional<Instant> completedAtUtc() {
        return completedAtUtc;
    }

    public RunStatus status() {
        return status;
    }

    public long matchedCount() {
        return matchedCount;
    }

    public long exceptionCount() {
        return exceptionCount;
    }
}
