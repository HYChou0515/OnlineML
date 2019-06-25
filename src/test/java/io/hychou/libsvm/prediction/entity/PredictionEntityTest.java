package io.hychou.libsvm.prediction.entity;

import io.hychou.test.common.DataStructureTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PredictionEntityTest extends DataStructureTest {

    private PredictionEntity entity;
    private String entityToString = "PredictionEntity{id=2}";

    @BeforeEach
    public void setUp() {
        entity = new PredictionEntity(2L, "this is entity".getBytes());
    }

    @Test
    @Override
    public void equals_givenSelf_thenTrueShouldBeFound() {
        // given
        PredictionEntity e = entity;
        // when
        assertEqualsAndHaveSameHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenSameObject_thenTrueShouldBeFound() {
        // given
        Object e = new PredictionEntity();

        ((PredictionEntity) e).setId(entity.getId());
        ((PredictionEntity) e).setDataBytes(entity.getDataBytes());

        // when
        assertEqualsAndHaveSameHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenSame_thenTrueShouldBeFound() {
        // given
        PredictionEntity e = new PredictionEntity();

        e.setId(entity.getId());
        e.setDataBytes(entity.getDataBytes());

        // when
        assertEqualsAndHaveSameHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenDiff_thenFalseShouldBeFound() {
        // given
        PredictionEntity e = new PredictionEntity();

        e.setId(entity.getId() + 1L);
        e.setDataBytes(entity.getDataBytes());

        // when
        assertNotEqualAndHaveDifferentHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenNull_thenFalseShouldBeFound() {
        // given
        PredictionEntity e = null;
        // when
        assertNotEqualAndHaveDifferentHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenAnotherObject_thenFalseShouldBeFound() {
        // given
        Integer e = new Integer(0);
        // when
        assertNotEqualAndHaveDifferentHashCode(entity, e);
    }

    @Test
    @Override
    public void toString_thenCorrectStringShouldBeFound() {
        String found = entity.toString();
        assertEquals(entityToString, found, "toString");
    }
}
