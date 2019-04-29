package io.jyasonx.jsxpay.util;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Utility methods for formatting amount values.
 */
public class AmountUtils {
    private static final int NO_SCALE = 0;
    private static final int STANDARD_SCALE = 2;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private AmountUtils() {
        // private constructor for util class...
    }

    /**
     * Converts a string represented amount to {@code BigDecimal} e.g. an amount string '123456' will
     * be translated to '1234.56'.
     *
     * @param amount the amount string without decimal point
     * @return the {@code BigDecimal} whose value is {@code amount} with a decimal point.
     */
    public static BigDecimal toDecimal(String amount) {
        return Strings.isNullOrEmpty(amount) ? BigDecimal.ZERO
                : BigDecimal
                .valueOf(Long.parseLong(amount))
                .setScale(STANDARD_SCALE, RoundingMode.DOWN)
                .divide(HUNDRED, RoundingMode.DOWN);
    }

    /**
     * Converts a long represented amount to {@code BigDecimal} e.g. an amount string '123456' will
     * be translated to '1234.56'.
     *
     * @param amount the amount long without decimal point
     * @return the {@code BigDecimal} whose value is {@code amount} with a decimal point.
     */
    public static BigDecimal toDecimal(Long amount) {
        return Objects.isNull(amount) ? BigDecimal.ZERO
                : BigDecimal
                .valueOf(amount)
                .setScale(STANDARD_SCALE, RoundingMode.DOWN)
                .divide(HUNDRED, RoundingMode.DOWN);
    }

    /**
     * Converts a string represented amount to {@code BigDecimal} e.g. an amount string '1234.56' will
     * be translated to '1234.56'.
     *
     * @param amount the amount string without decimal point
     * @return the {@code BigDecimal} whose value is {@code amount} with a decimal point.
     */
    public static BigDecimal toOriginalDecimal(String amount) {
        return Strings.isNullOrEmpty(amount) ? BigDecimal.ZERO
                : BigDecimal
                .valueOf(Double.parseDouble(amount))
                .setScale(STANDARD_SCALE, RoundingMode.DOWN);
    }

    /**
     * Removes the decimal point from {@code BigDecimal} e.g. an amount '1234.56' will be translated
     * as '123456'.
     *
     * @param amount the value to be removed decimal point from
     * @return the {@code BigDecimal} whose value has no decimal point
     */
    public static BigDecimal withoutDecimalPoint(BigDecimal amount) {
        return amount
                .multiply(HUNDRED)
                .setScale(NO_SCALE, BigDecimal.ROUND_DOWN);
    }
}
