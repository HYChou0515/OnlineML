package io.hychou.data.entity;

import io.hychou.common.DataStructureTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
public class DataEntityTest extends DataStructureTest {

    private DataEntity d1;
    private int d1HashCode = 3180;
    private String d1ToString = "DataEntity{name=d1}";

    @Before
    public void setUp() {
        d1 = new DataEntity("d1", "This is d1".getBytes());
    }

    @Test
    @Override
    public void equals_givenSelf_thenTrueShouldBeFound() {
        // given
        DataEntity d = d1;
        // when
        boolean found = equalsThenHashCodeEqual(d1, d);
        assertTrue(found);
    }

    @Test
    @Override
    public void equals_givenSameObject_thenTrueShouldBeFound() {
        // given
        Object d = new DataEntity();
        ((DataEntity) d).setName(d1.getName());
        ((DataEntity) d).setDataBytes(d1.getDataBytes());
        // when
        boolean found = equalsThenHashCodeEqual(d1, d);
        assertTrue(found);
    }

    @Test
    @Override
    public void equals_givenSame_thenTrueShouldBeFound() {
        // given
        DataEntity d = new DataEntity();
        d.setName(d1.getName());
        d.setDataBytes(d1.getDataBytes());
        // when
        boolean found = equalsThenHashCodeEqual(d1, d);
        assertTrue(found);
    }

    @Test
    @Override
    public void equals_givenDiff_thenFalseShouldBeFound() {
        // given
        DataEntity d = new DataEntity();
        d.setName(d1.getName()+" another");
        d.setDataBytes(d1.getDataBytes());
        // when
        boolean found = equalsThenHashCodeEqual(d1, d);
        assertFalse(found);
    }

    @Test
    @Override
    public void equals_givenNull_thenFalseShouldBeFound() {
        // given
        DataEntity n = null;
        // when
        boolean found = equalsThenHashCodeEqual(d1, n);
        assertFalse(found);
    }

    @Test
    @Override
    public void equals_givenAnotherObject_thenFalseShouldBeFound() {
        // given
        Integer n = new Integer(0);
        // when
        boolean found = equalsThenHashCodeEqual(d1, n);
        assertFalse(found);
    }

    @Test
    @Override
    public void hashCode_thenCorrectHashShouldBeFound() {
        int found = d1.hashCode();
        assertEquals(d1HashCode, found, "Hashcode");
    }

    @Test
    @Override
    public void toString_thenCorrectStringShouldBeFound() {
        String found = d1.toString();
        assertEquals(d1ToString, found, "toString");
    }

}
