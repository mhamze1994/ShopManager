/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Studio
 */
public class Calculator {

    public static final int DECIMAL_PRECISION = 0;

    public static final RoundingMode ROUNDING_MODE = RoundingMode.FLOOR;

    public static BigDecimal toPercent(int percent) {
        return new BigDecimal(percent).divide(new BigDecimal("100"));
    }

    public static BigDecimal mul(BigDecimal... all) {
        BigDecimal output = BigDecimal.ONE;
        for (int i = 0; i < all.length; i++) {
            output = output.multiply(all[i]);
        }
        return output.stripTrailingZeros();
    }

    public static BigDecimal add(BigDecimal... all) {
        BigDecimal output = all[0];
        for (int i = 1; i < all.length; i++) {
            output = output.add(all[i]);
        }
        return output.stripTrailingZeros();
    }

    public static BigDecimal sub(BigDecimal... all) {
        BigDecimal output = all[0];
        for (int i = 1; i < all.length; i++) {
            output = output.subtract(all[i]);
        }
        return output.stripTrailingZeros();
    }

    /**
     * Example : 20000 / 12 = 1666.66 and r = 0.08
     *
     * @param A
     * @param B
     * @return
     */
    public static BigDecimal[] divAndRem(BigDecimal A, BigDecimal B) {
        BigDecimal d = A.divide(B, Calculator.DECIMAL_PRECISION, Calculator.ROUNDING_MODE).stripTrailingZeros();
        BigDecimal r = A.subtract(d.multiply(B)).stripTrailingZeros();
        return new BigDecimal[]{d, r};
    }

    public static long toLong(Object aValue) {
        if (aValue == null || aValue.toString().isEmpty()) {
            return 0;
        }
        return Long.parseLong(aValue.toString().replace(",", ""));
    }

    public static int toInt(Object aValue) {
        if (aValue == null || aValue.toString().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(aValue.toString().replace(",", ""));
    }

    public static BigDecimal toBigDeciaml(Object aValue) {
        if (aValue == null || aValue.toString().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(aValue.toString().replace(",", "")).setScale(DECIMAL_PRECISION, BigDecimal.ROUND_HALF_UP);
    }

    public static boolean isLessOrEqual(BigDecimal a, BigDecimal b) {
        
        return a.compareTo(b) == -1 || a.equals(b);
        
    }
}
