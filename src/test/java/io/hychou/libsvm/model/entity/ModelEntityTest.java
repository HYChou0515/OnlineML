package io.hychou.libsvm.model.entity;

import io.hychou.test.common.DataStructureTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class ModelEntityTest extends DataStructureTest {

    private ModelEntity entity;
    private String entityToString = "ModelEntity{id=2}";

    @BeforeEach
    public void setUp() {
        entity = new ModelEntity(2L, "this is entity".getBytes());
    }

    @Test
    @Override
    public void equals_givenSelf_thenTrueShouldBeFound() {
        // given
        ModelEntity e = entity;
        // when
        assertEqualsAndHaveSameHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenSameObject_thenTrueShouldBeFound() {
        // given
        Object e = new ModelEntity();

        ((ModelEntity) e).setId(entity.getId());
        ((ModelEntity) e).setDataBytes(entity.getDataBytes());

        // when
        assertEqualsAndHaveSameHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenSame_thenTrueShouldBeFound() {
        // given
        ModelEntity e = new ModelEntity();

        e.setId(entity.getId());
        e.setDataBytes(entity.getDataBytes());

        // when
        assertEqualsAndHaveSameHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenDiff_thenFalseShouldBeFound() {
        // given
        ModelEntity e = new ModelEntity();

        e.setId(entity.getId() + 1L);
        e.setDataBytes(entity.getDataBytes());

        // when
        assertNotEqualAndHaveDifferentHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenNull_thenFalseShouldBeFound() {
        // given
        ModelEntity e = null;
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
