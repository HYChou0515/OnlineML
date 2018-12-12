package io.hychou.data.entity;
import io.hychou.common.DataStructureTest;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexValueTest extends DataStructureTest {

    private IndexValue s1;
    private int s1HashCode = 1073742816;
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
        boolean found = equalsThenHashCodeEqual(s1, s);
        assertThat(found).isTrue();
    }

    @Test
    @Override
    public void equals_givenSameObject_thenTrueShouldBeFound() {
        // given
        Object s = new IndexValue();
        ((IndexValue) s).setIndex(s1.getIndex());
        ((IndexValue) s).setValue(s1.getValue());
        // when
        boolean found = equalsThenHashCodeEqual(s1, s);
        assertThat(found).isTrue();
    }

    @Test
    @Override
    public void equals_givenSame_thenTrueShouldBeFound() {
        // given
        IndexValue s = new IndexValue();
        s.setIndex(s1.getIndex());
        s.setValue(s1.getValue());
        // when
        boolean found = equalsThenHashCodeEqual(s1, s);
        assertThat(found).isTrue();
    }

    @Test
    @Override
    public void equals_givenDiff_thenFalseShouldBeFound() {
        // given
        IndexValue s = new IndexValue();
        s.setIndex(s1.getIndex());
        s.setValue(s1.getValue()+1);
        // when
        boolean found = equalsThenHashCodeEqual(s1, s);
        assertThat(found).isFalse();
    }

    @Test
    @Override
    public void equals_givenNull_thenFalseShouldBeFound() {
        // given
        IndexValue n = null;
        // when
        boolean found = equalsThenHashCodeEqual(s1, n);
        assertThat(found).isFalse();
    }

    @Test
    @Override
    public void equals_givenAnotherObject_thenFalseShouldBeFound() {
        // given
        Integer n = new Integer(0);
        // when
        boolean found = equalsThenHashCodeEqual(s1, n);
        assertThat(found).isFalse();
    }

    @Test
    @Override
    public void hashCode_thenCorrectHashShouldBeFound() {
        int found = s1.hashCode();
        assertThat(found).as("Hashcode").isEqualTo(s1HashCode);
    }

    @Test
    @Override
    public void toString_thenCorrectStringShouldBeFound() {
        String found = s1.toString();
        assertThat(found).as("toString").isEqualTo(s1ToString);
    }
}