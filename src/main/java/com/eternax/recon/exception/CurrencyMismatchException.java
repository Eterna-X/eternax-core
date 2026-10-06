package com.eternax.recon.exception;

/** Two {@code Money} values of different currencies were combined; always a programming error. */
public final class CurrencyMismatchException extends EternaSyncException {
    private static final long serialVersionUID = 1L;

    public CurrencyMismatchException(String expectedCurrency, String actualCurrency) {
        super(
                ErrorCode.DOM_1002_CURRENCY_MISMATCH,
                ErrorCategory.INTERNAL,
                "Cannot combine amounts in different currencies: expected '"
                        + expectedCurrency
                        + "' but got '"
                        + actualCurrency
                        + "'. Convert explicitly before combining amounts.");
    }
}
