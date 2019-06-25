package io.hychou.common.util;

import io.hychou.data.util.DataUtils;
import org.springframework.data.util.Pair;

import java.util.List;

import static io.hychou.common.utilities.TransformUtilities.mergeTwoListToListOfPairs;
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
}
