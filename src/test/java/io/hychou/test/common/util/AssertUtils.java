package io.hychou.test.common.util;

import io.hychou.data.util.DataUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.util.Pair;

import java.util.List;

import static io.hychou.common.utilities.TransformUtilities.mergeTwoListToListOfPairs;
import static org.apache.commons.lang3.RandomUtils.nextBytes;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class AssertUtils {

    public static void assertListOfLibsvmTokenEquals(List<String> expected, List<String> actual, double tolerance) {
        assertEquals(expected.size(), actual.size(), "number of libsvm tokens");
        List<Pair> listExpAct = mergeTwoListToListOfPairs(expected, actual);
        assertAll("each token must be the same",
                listExpAct.stream().map(key ->
                        () -> {
                            String exp = (String) key.getFirst();
                            String act = (String) key.getSecond();
                            assertTrue(String.format("expected: <%s> but was: <%s>", exp, act), DataUtils.libsvmTokenCompare(exp, act, tolerance) == 0);
                        }
                )
        );
    }

    public static void assertListOfLibsvmTokenEquals(List<String> expected, List<String> actual) {
        assertListOfLibsvmTokenEquals(expected, actual, 0);
    }

    public static Long createAnyLong() {
        return nextLong();
    }

    public static String createAnyString(int length) {
        return RandomStringUtils.random(length);
    }

    public static String createAnyString() {
        return createAnyString(10);
    }

    public static byte[] createAnyBytes(int length) {
        return nextBytes(length);
    }

    public static byte[] createAnyBytes() {
        return createAnyBytes(10);
    }
}
