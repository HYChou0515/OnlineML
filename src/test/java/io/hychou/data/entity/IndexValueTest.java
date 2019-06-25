package io.hychou.data.entity;

import io.hychou.common.DataStructureTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexValueTest extends DataStructureTest {

    private IndexValue s1;
    private String s1ToString = "IndexValue{index=1, value=2.0}";

    @Before
    public void setUp() {
        s1 = new IndexValue();
        s1.setIndex(1);
        s1.setValue(2.0);
    }

    @Test
    @Override
    public void equals_givenSelf_thenTrueShouldBeFound() {
        // given
        IndexValue s = s1;
        // when
        assertEqualsAndHaveSameHashCode(s1, s);
    }

    @Test
    @Override
    public void equals_givenSameObject_thenTrueShouldBeFound() {
        // given
        Object s = new IndexValue();
        ((IndexValue) s).setIndex(s1.getIndex());
        ((IndexValue) s).setValue(s1.getValue());
        // when
        assertEqualsAndHaveSameHashCode(s1, s);
    }

    @Test
    @Override
    public void equals_givenSame_thenTrueShouldBeFound() {
        // given
        IndexValue s = new IndexValue();
        s.setIndex(s1.getIndex());
        s.setValue(s1.getValue());
        // when
        assertEqualsAndHaveSameHashCode(s1, s);
    }

    @Test
    @Override
    public void equals_givenDiff_thenFalseShouldBeFound() {
        // given
        IndexValue s = new IndexValue();
        s.setIndex(s1.getIndex());
        s.setValue(s1.getValue() + 1);
        // when
        assertNotEqualAndHaveDifferentHashCode(s1, s);
    }

    @Test
    @Override
    public void equals_givenNull_thenFalseShouldBeFound() {
        // given
        IndexValue n = null;
        // when
        assertNotEqualAndHaveDifferentHashCode(s1, n);
    }

    @Test
    @Override
    public void equals_givenAnotherObject_thenFalseShouldBeFound() {
        // given
        Integer n = new Integer(0);
        // when
        assertNotEqualAndHaveDifferentHashCode(s1, n);
    }

    @Test
    @Override
    public void toString_thenCorrectStringShouldBeFound() {
        String found = s1.toString();
        assertEquals(s1ToString, found, "toString");
    }
}