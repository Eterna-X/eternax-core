package com.eternax.recon.domain;

/** Matching tiers, evaluated cheapest and most certain first (HLD 7.1). */
public enum MatchTier {
    INTEGRITY,
    EXACT,
    COMPOSITE,
    AGGREGATE,
    AI_SUGGESTED
}
