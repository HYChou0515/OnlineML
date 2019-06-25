package io.hychou.data.entity;

import io.hychou.test.common.DataStructureTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class DataEntityTest extends DataStructureTest {

    private DataEntity d1;
    private String d1ToString = "DataEntity{name=d1}";

    @BeforeEach
    public void setUp() {
        d1 = new DataEntity("d1", "This is d1".getBytes());
    }

    @Test
    @Override
    public void equals_givenSelf_thenTrueShouldBeFound() {
        // given
        DataEntity d = d1;
        // when
        assertEqualsAndHaveSameHashCode(d1, d);
    }

    @Test
    @Override
    public void equals_givenSameObject_thenTrueShouldBeFound() {
        // given
        Object d = new DataEntity();
        ((DataEntity) d).setName(d1.getName());
        ((DataEntity) d).setDataBytes(d1.getDataBytes());
        // when
        assertEqualsAndHaveSameHashCode(d1, d);
    }

    @Test
    @Override
    public void equals_givenSame_thenTrueShouldBeFound() {
        // given
        DataEntity d = new DataEntity();
        d.setName(d1.getName());
        d.setDataBytes(d1.getDataBytes());
        // when
        assertEqualsAndHaveSameHashCode(d1, d);
    }

    @Test
    @Override
    public void equals_givenDiff_thenFalseShouldBeFound() {
        // given
        DataEntity d = new DataEntity();
        d.setName(d1.getName() + " another");
        d.setDataBytes(d1.getDataBytes());
        // when
        assertNotEqualAndHaveDifferentHashCode(d1, d);
    }

    @Test
    @Override
    public void equals_givenNull_thenFalseShouldBeFound() {
        // given
        DataEntity n = null;
        // when
        assertNotEqualAndHaveDifferentHashCode(d1, n);
    }

    @Test
    @Override
    public void equals_givenAnotherObject_thenFalseShouldBeFound() {
        // given
        Integer n = new Integer(0);
        // when
        assertNotEqualAndHaveDifferentHashCode(d1, n);
    }

    @Test
    @Override
    public void toString_thenCorrectStringShouldBeFound() {
        String found = d1.toString();
        assertEquals(d1ToString, found, "toString");
    }

}
