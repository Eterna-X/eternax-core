package com.eternax.recon.common;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed immutable identifier. A dedicated type per identifier kind makes it a compile
 * error to pass a tenant id where a transaction id is expected.
 *
 * @param <T> marker type naming the entity the identifier belongs to
 */
public final class TypedId<T> {

    private final String value;

    private TypedId(String value) {
        Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("identifier value must not be blank");
        }
        this.value = value;
    }

    public static <T> TypedId<T> generate() {
        return new TypedId<>(UUID.randomUUID().toString());
    }

    public static <T> TypedId<T> of(String value) {
        return new TypedId<>(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TypedId<?> otherId && value.equals(otherId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
