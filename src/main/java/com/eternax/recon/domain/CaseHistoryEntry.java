package com.eternax.recon.domain;

import java.time.Instant;

/** One immutable entry in a case's history. */
public record CaseHistoryEntry(CaseState state, Instant atUtc, String actor, String reason) {}
