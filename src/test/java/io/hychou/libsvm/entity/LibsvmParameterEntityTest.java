package io.hychou.libsvm.entity;

import io.hychou.common.DataStructureTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
public class LibsvmParameterEntityTest extends DataStructureTest {

    private LibsvmParameterEntity entity;
    private String entityToString = "LibsvmParameterEntity{svmType=C_SVC, kernelType=LINEAR, degree=1, gamma=2.0, coef0=3.0, cacheSize=4.0, eps=5.0, c=6.0, nu=7.0, p=8.0, shrinking=true, probability=false}";

    @Before
    public void setUp() {
        entity = new LibsvmParameterEntity();

        entity.setSvmType(SvmTypeEnum.C_SVC);
        entity.setKernelType(KernelTypeEnum.LINEAR);
        entity.setDegree(1);
        entity.setGamma(2.0);
        entity.setCoef0(3.0);
        entity.setCacheSize(4.0);
        entity.setEps(5.0);
        entity.setC(6.0);
        entity.setNu(7.0);
        entity.setP(8.0);
        entity.setShrinking(true);
        entity.setProbability(false);
    }

    @Test
    @Override
    public void equals_givenSelf_thenTrueShouldBeFound() {
        // given
        LibsvmParameterEntity e = entity;
        // when
        boolean found = equalsThenHashCodeEqual(entity, e);
        // assert
        assertTrue(found);
    }

    @Test
    @Override
    public void equals_givenSameObject_thenTrueShouldBeFound() {
        // given
        Object e = new LibsvmParameterEntity();

        ((LibsvmParameterEntity) e).setSvmType(entity.getSvmType());
        ((LibsvmParameterEntity) e).setKernelType(entity.getKernelType());
        ((LibsvmParameterEntity) e).setDegree(entity.getDegree());
        ((LibsvmParameterEntity) e).setGamma(entity.getGamma());
        ((LibsvmParameterEntity) e).setCoef0(entity.getCoef0());
        ((LibsvmParameterEntity) e).setCacheSize(entity.getCacheSize());
        ((LibsvmParameterEntity) e).setEps(entity.getEps());
        ((LibsvmParameterEntity) e).setC(entity.getC());
        ((LibsvmParameterEntity) e).setNu(entity.getNu());
        ((LibsvmParameterEntity) e).setP(entity.getP());
        ((LibsvmParameterEntity) e).setShrinking(entity.getShrinking());
        ((LibsvmParameterEntity) e).setProbability(entity.getProbability());

        // when
        boolean found = equalsThenHashCodeEqual(entity, e);
        // assert
        assertTrue(found);
    }

    @Test
    @Override
    public void equals_givenSame_thenTrueShouldBeFound() {
        // given
        LibsvmParameterEntity e = new LibsvmParameterEntity();

        e.setSvmType(entity.getSvmType());
        e.setKernelType(entity.getKernelType());
        e.setDegree(entity.getDegree());
        e.setGamma(entity.getGamma());
        e.setCoef0(entity.getCoef0());
        e.setCacheSize(entity.getCacheSize());
        e.setEps(entity.getEps());
        e.setC(entity.getC());
        e.setNu(entity.getNu());
        e.setP(entity.getP());
        e.setShrinking(entity.getShrinking());
        e.setProbability(entity.getProbability());

        // when
        boolean found = equalsThenHashCodeEqual(entity, e);
        // assert
        assertTrue(found);
    }

    @Test
    @Override
    public void equals_givenDiff_thenFalseShouldBeFound() {
        // given
        LibsvmParameterEntity e = new LibsvmParameterEntity();

        e.setSvmType(entity.getSvmType());
        e.setKernelType(entity.getKernelType());
        e.setDegree(entity.getDegree());
        e.setGamma(entity.getGamma());
        e.setCoef0(entity.getCoef0());
        e.setCacheSize(entity.getCacheSize());
        e.setEps(entity.getEps());
        e.setC(entity.getC()+1.0);
        e.setNu(entity.getNu());
        e.setP(entity.getP());
        e.setShrinking(entity.getShrinking());
        e.setProbability(entity.getProbability());

        // when
        boolean found = equalsThenHashCodeEqual(entity, e);
        // assert
        assertFalse(found);
    }

    @Test
    @Override
    public void equals_givenNull_thenFalseShouldBeFound() {
        // given
        LibsvmParameterEntity e = null;
        // when
        boolean found = equalsThenHashCodeEqual(entity, e);
        // assert
        assertFalse(found);
    }

    @Test
    @Override
    public void equals_givenAnotherObject_thenFalseShouldBeFound() {
        // given
        Integer n = new Integer(0);
        // when
        boolean found = equalsThenHashCodeEqual(entity, n);
        // assert
        assertFalse(found);
    }

    @Test
    @Override
    public void hashCode_thenCorrectHashShouldBeFound() {
    }

    @Test
    @Override
    public void toString_thenCorrectStringShouldBeFound() {
        String found = entity.toString();
        assertEquals(entityToString, found, "toString");
    }
}
