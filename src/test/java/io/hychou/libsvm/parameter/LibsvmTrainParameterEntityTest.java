package io.hychou.libsvm.parameter;

import io.hychou.test.common.DataStructureTest;
import libsvm.svm_parameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class LibsvmTrainParameterEntityTest extends DataStructureTest {

    private LibsvmTrainParameterEntity entity;
    private svm_parameter defaultParam;
    private String entityToString = "LibsvmTrainParameterEntity{svmType=C_SVC, kernelType=LINEAR, degree=1, gamma=2.0, coef0=3.0, cacheSize=4.0, eps=5.0, c=6.0, nu=7.0, p=8.0, shrinking=true, probability=false}";

    @BeforeEach
    public void setUp() {
        entity = new LibsvmTrainParameterEntity();

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

        defaultParam = new svm_parameter();
        defaultParam.svm_type = 0;
        defaultParam.kernel_type = 0;
        defaultParam.degree = 1;
        defaultParam.gamma = 2.0;
        defaultParam.coef0 = 3.0;
        defaultParam.cache_size = 4.0;
        defaultParam.eps = 5.0;
        defaultParam.C = 6.0;
        defaultParam.nu = 7.0;
        defaultParam.p = 8.0;
        defaultParam.shrinking = 1;
        defaultParam.probability = 0;
    }

    @Test
    @Override
    public void equals_givenSelf_thenTrueShouldBeFound() {
        // given
        LibsvmTrainParameterEntity e = entity;
        // when
        assertEqualsAndHaveSameHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenSameObject_thenTrueShouldBeFound() {
        // given
        Object e = new LibsvmTrainParameterEntity();

        ((LibsvmTrainParameterEntity) e).setSvmType(entity.getSvmType());
        ((LibsvmTrainParameterEntity) e).setKernelType(entity.getKernelType());
        ((LibsvmTrainParameterEntity) e).setDegree(entity.getDegree());
        ((LibsvmTrainParameterEntity) e).setGamma(entity.getGamma());
        ((LibsvmTrainParameterEntity) e).setCoef0(entity.getCoef0());
        ((LibsvmTrainParameterEntity) e).setCacheSize(entity.getCacheSize());
        ((LibsvmTrainParameterEntity) e).setEps(entity.getEps());
        ((LibsvmTrainParameterEntity) e).setC(entity.getC());
        ((LibsvmTrainParameterEntity) e).setNu(entity.getNu());
        ((LibsvmTrainParameterEntity) e).setP(entity.getP());
        ((LibsvmTrainParameterEntity) e).setShrinking(entity.getShrinking());
        ((LibsvmTrainParameterEntity) e).setProbability(entity.getProbability());

        // when
        assertEqualsAndHaveSameHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenSame_thenTrueShouldBeFound() {
        // given
        LibsvmTrainParameterEntity e = new LibsvmTrainParameterEntity();

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
        assertEqualsAndHaveSameHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenDiff_thenFalseShouldBeFound() {
        // given
        LibsvmTrainParameterEntity e = new LibsvmTrainParameterEntity();

        e.setSvmType(entity.getSvmType());
        e.setKernelType(entity.getKernelType());
        e.setDegree(entity.getDegree());
        e.setGamma(entity.getGamma());
        e.setCoef0(entity.getCoef0());
        e.setCacheSize(entity.getCacheSize());
        e.setEps(entity.getEps());
        e.setC(entity.getC() + 1.0);
        e.setNu(entity.getNu());
        e.setP(entity.getP());
        e.setShrinking(entity.getShrinking());
        e.setProbability(entity.getProbability());

        // when
        assertNotEqualAndHaveDifferentHashCode(entity, e);
    }

    @Test
    @Override
    public void equals_givenNull_thenFalseShouldBeFound() {
        // given
        LibsvmTrainParameterEntity e = null;
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

    @Test
    public void toSvmParameter_givenNullDafualtParameter_thenReturnCorrectSvmParameter() {
        // arrange
        svm_parameter expected = new svm_parameter();
        expected.svm_type = 0;
        expected.kernel_type = 0;
        expected.degree = 1;
        expected.gamma = 2.0;
        expected.coef0 = 3.0;
        expected.cache_size = 4.0;
        expected.eps = 5.0;
        expected.C = 6.0;
        expected.nu = 7.0;
        expected.p = 8.0;
        expected.shrinking = 1;
        expected.probability = 0;
        // apply
        svm_parameter actual = entity.toSvmParameter(null);
        // assert
        assertAll(
                () -> assertEquals(expected.svm_type, actual.svm_type),
                () -> assertEquals(expected.kernel_type, actual.kernel_type),
                () -> assertEquals(expected.degree, actual.degree),
                () -> assertEquals(expected.gamma, actual.gamma),
                () -> assertEquals(expected.coef0, actual.coef0),
                () -> assertEquals(expected.cache_size, actual.cache_size),
                () -> assertEquals(expected.eps, actual.eps),
                () -> assertEquals(expected.C, actual.C),
                () -> assertEquals(expected.nu, actual.nu),
                () -> assertEquals(expected.p, actual.p),
                () -> assertEquals(expected.shrinking, actual.shrinking),
                () -> assertEquals(expected.probability, actual.probability)
        );
    }

    @Test
    public void toSvmParameter_givenDefaultParameter_thenReturnCorrectSvmParameter() {
        // arrange
        LibsvmTrainParameterEntity param = LibsvmTrainParameterEntity.build()
                .svmType(SvmTypeEnum.ONE_CLASS).c(3.2)
                .probability(false).shrinking(false).done();
        svm_parameter expected = (svm_parameter) defaultParam.clone();
        expected.svm_type = 2;
        expected.C = 3.2;
        expected.shrinking = 0;
        expected.probability = 0;
        // apply
        svm_parameter actual = param.toSvmParameter(defaultParam);
        // assert
        assertAll(
                () -> assertEquals(expected.svm_type, actual.svm_type),
                () -> assertEquals(expected.kernel_type, actual.kernel_type),
                () -> assertEquals(expected.degree, actual.degree),
                () -> assertEquals(expected.gamma, actual.gamma),
                () -> assertEquals(expected.coef0, actual.coef0),
                () -> assertEquals(expected.cache_size, actual.cache_size),
                () -> assertEquals(expected.eps, actual.eps),
                () -> assertEquals(expected.C, actual.C),
                () -> assertEquals(expected.nu, actual.nu),
                () -> assertEquals(expected.p, actual.p),
                () -> assertEquals(expected.shrinking, actual.shrinking),
                () -> assertEquals(expected.probability, actual.probability)
        );
    }

    @Test
    public void toSvmParameter_givenDefaultParameter2_thenReturnCorrectSvmParameter() {
        // arrange
        LibsvmTrainParameterEntity param = LibsvmTrainParameterEntity.build()
                .kernelType(KernelTypeEnum.RBF)
                .degree(3).gamma(4.0).coef0(5.0).cacheSize(6.0)
                .eps(7.0).nu(9.0).p(10.0).done();
        svm_parameter expected = (svm_parameter) defaultParam.clone();
        expected.kernel_type = 2;
        expected.degree = 3;
        expected.gamma = 4.0;
        expected.coef0 = 5.0;
        expected.cache_size = 6.0;
        expected.eps = 7.0;
        expected.nu = 9.0;
        expected.p = 10.0;
        // apply
        svm_parameter actual = param.toSvmParameter(defaultParam);
        // assert
        assertAll(
                () -> assertEquals(expected.svm_type, actual.svm_type),
                () -> assertEquals(expected.kernel_type, actual.kernel_type),
                () -> assertEquals(expected.degree, actual.degree),
                () -> assertEquals(expected.gamma, actual.gamma),
                () -> assertEquals(expected.coef0, actual.coef0),
                () -> assertEquals(expected.cache_size, actual.cache_size),
                () -> assertEquals(expected.eps, actual.eps),
                () -> assertEquals(expected.C, actual.C),
                () -> assertEquals(expected.nu, actual.nu),
                () -> assertEquals(expected.p, actual.p),
                () -> assertEquals(expected.shrinking, actual.shrinking),
                () -> assertEquals(expected.probability, actual.probability)
        );
    }

    @Test
    public void toSvmParameter_givenDefaultParameter3_thenReturnCorrectSvmParameter() {
        // arrange
        LibsvmTrainParameterEntity param = LibsvmTrainParameterEntity.build()
                .probability(true).done();
        svm_parameter expected = (svm_parameter) defaultParam.clone();
        expected.probability = 1;
        // apply
        svm_parameter actual = param.toSvmParameter(defaultParam);
        // assert
        assertAll(
                () -> assertEquals(expected.svm_type, actual.svm_type),
                () -> assertEquals(expected.kernel_type, actual.kernel_type),
                () -> assertEquals(expected.degree, actual.degree),
                () -> assertEquals(expected.gamma, actual.gamma),
                () -> assertEquals(expected.coef0, actual.coef0),
                () -> assertEquals(expected.cache_size, actual.cache_size),
                () -> assertEquals(expected.eps, actual.eps),
                () -> assertEquals(expected.C, actual.C),
                () -> assertEquals(expected.nu, actual.nu),
                () -> assertEquals(expected.p, actual.p),
                () -> assertEquals(expected.shrinking, actual.shrinking),
                () -> assertEquals(expected.probability, actual.probability)
        );
    }
}
