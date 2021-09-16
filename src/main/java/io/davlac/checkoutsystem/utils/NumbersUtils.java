package io.davlac.checkoutsystem.utils;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.RoundingMode;

public interface NumbersUtils {
    static double roundUpBy2Decimals(double number) {
        return NumberUtils.toScaledBigDecimal(number, 2, RoundingMode.UP).doubleValue();
    }
}
