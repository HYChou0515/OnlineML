package io.hychou.common;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public abstract class DataStructureTest {

    protected static void assertEqualsAndHaveSameHashCode(Object expected, Object actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(expected.equals(actual) && actual.equals(expected));
        assertEquals(expected.hashCode(), actual.hashCode(), "HashCode");
    }
    protected static void assertNotEqualAndHaveDifferentHashCode(Object expected, Object actual) {
        if(Objects.nonNull(expected))
            assertFalse(expected.equals(actual));
        if(Objects.nonNull(actual))
            assertFalse(actual.equals(expected));
        if(Objects.nonNull(expected) && Objects.nonNull(actual)) {
            assertNotEquals(expected.hashCode(), actual.hashCode(), "HashCode");
        }
    }

    // equals

    abstract public void equals_givenSelf_thenTrueShouldBeFound();

    abstract public void equals_givenSameObject_thenTrueShouldBeFound();

    abstract public void equals_givenSame_thenTrueShouldBeFound();

    abstract public void equals_givenDiff_thenFalseShouldBeFound();

    abstract public void equals_givenNull_thenFalseShouldBeFound();

    abstract public void equals_givenAnotherObject_thenFalseShouldBeFound();

    // toString

    abstract public void toString_thenCorrectStringShouldBeFound();
}