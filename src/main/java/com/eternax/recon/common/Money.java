package com.eternax.recon.common;

import com.eternax.recon.exception.CurrencyMismatchException;
import java.util.Objects;

/**
 * Immutable amount in minor units (paise for INR). Never backed by floating point (HLD 6.1).
 *
 * @param amountMinor amount in the currency's minor unit; negative values are allowed
 * @param currencyCode ISO 4217 code such as {@code INR}
 */
public record Money(long amountMinor, String currencyCode) implements Comparable<Money> {

    public static final String INR = "INR";

    public Money {
        Objects.requireNonNull(currencyCode, "currencyCode must not be null");
        if (currencyCode.length() != 3) {
            throw new IllegalArgumentException(
                    "currencyCode must be a 3-letter ISO 4217 code, was: " + currencyCode);
        }
    }

    public static Money zero(String currencyCode) {
        return new Money(0L, currencyCode);
    }

    public static Money inr(long paise) {
        return new Money(paise, INR);
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.addExact(amountMinor, other.amountMinor), currencyCode);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.subtractExact(amountMinor, other.amountMinor), currencyCode);
    }

    public Money times(long factor) {
        return new Money(Math.multiplyExact(amountMinor, factor), currencyCode);
    }

    public Money absolute() {
        return new Money(Math.abs(amountMinor), currencyCode);
    }

    public boolean isZero() {
        return amountMinor == 0L;
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return amountMinor > other.amountMinor;
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return Long.compare(amountMinor, other.amountMinor);
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other must not be null");
        if (!currencyCode.equals(other.currencyCode)) {
            throw new CurrencyMismatchException(currencyCode, other.currencyCode);
        }
    }
}
