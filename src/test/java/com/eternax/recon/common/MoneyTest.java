package com.eternax.recon.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eternax.recon.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void plusAndMinus_workInMinorUnits() {
        assertThat(Money.inr(10_050).plus(Money.inr(50))).isEqualTo(Money.inr(10_100));
        assertThat(Money.inr(100).minus(Money.inr(250))).isEqualTo(Money.inr(-150));
    }

    @Test
    void plus_rejectsCurrencyMismatch_withBothCurrenciesInMessage() {
        assertThatThrownBy(() -> Money.inr(1).plus(new Money(1, "USD")))
                .isInstanceOf(CurrencyMismatchException.class)
                .hasMessageContaining("INR")
                .hasMessageContaining("USD");
    }

    @Test
    void plus_failsLoudlyOnOverflowInsteadOfWrapping() {
        assertThatThrownBy(() -> Money.inr(Long.MAX_VALUE).plus(Money.inr(1)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void constructor_rejectsMalformedCurrency() {
        assertThatThrownBy(() -> new Money(1, "RUPEE"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
