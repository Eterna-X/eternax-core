package com.eternax.recon.domain;

import java.util.Objects;

/** Points to the untouched original in the raw vault (HLD 6.1, 18.2). */
public record RawRecordPointer(String vaultLocation, String checksum) {
    public RawRecordPointer {
        Objects.requireNonNull(vaultLocation, "vaultLocation must not be null");
        Objects.requireNonNull(checksum, "checksum must not be null");
    }
}
