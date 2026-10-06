package com.eternax.recon.domain;

/** Payment rail a transaction belongs to (HLD 9.1). */
public enum Rail {
    UPI,
    IMPS,
    NEFT,
    RTGS,
    AEPS,
    CARD_ATM,
    BBPS,
    NACH,
    AGGREGATOR,
    PPI,
    CLOSED_LOOP,
    OTHER
}
