package io.davlac.checkoutsystem.context.utils;

import io.davlac.checkoutsystem.utils.NumbersUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NumberUtilsTest {

    @Test
    void roundUpBy2Decimals_with2DecimalNumber_shouldChangeNothing() {
        assertEquals(1.12, NumbersUtils.roundUpBy2Decimals(1.12));
    }

    @Test
    void roundUpBy2Decimals_withTooMuchDecimals_shouldRoundUp() {
        assertEquals(1.13, NumbersUtils.roundUpBy2Decimals(1.125123456));
    }

    @Test
    void roundUpBy2Decimals_withNoDecimals_shouldNotAddDecimals() {
        assertEquals(1, NumbersUtils.roundUpBy2Decimals(1));
    }

}
