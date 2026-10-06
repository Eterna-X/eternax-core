package com.eternax.recon.common;

/** Marker types used only as {@link TypedId} type parameters; never instantiated. */
public final class Markers {
    private Markers() {}

    public interface TenantMarker {}

    public interface TransactionMarker {}

    public interface MatchGroupMarker {}

    public interface CaseMarker {}

    public interface RunMarker {}

    public interface DefinitionMarker {}

    public interface AdjustmentMarker {}
}
