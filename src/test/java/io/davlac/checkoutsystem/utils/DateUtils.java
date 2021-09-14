package io.davlac.checkoutsystem.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface DateUtils {
    static void assertInstantsEqualByMilli(Instant expected, Instant result) {
        assertEquals(expected.truncatedTo(ChronoUnit.MILLIS), result.truncatedTo(ChronoUnit.MILLIS));
    }
}
