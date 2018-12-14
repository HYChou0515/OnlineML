package io.hychou.libsvm.parameter;

import io.hychou.common.AbstractDataStructure;
import io.hychou.common.SignificantField;

import java.util.ArrayList;
import java.util.List;

public class LibsvmParameterEntity extends AbstractDataStructure {

    private SvmTypeEnum svmType;
    private KernelTypeEnum kernelType;
    private Integer degree;
    private Double gamma;
    private Double coef0;

    private Double cacheSize;
    private Double eps;
    private Double c;
    private Double nu;
    private Double p;
    private Boolean shrinking;
    private Boolean probability;

    // Statuc builder methods

    public static ParameterBuilder build() {
        return new DefaultBuilder();
    }


    public interface ParameterBuilder {
        ParameterBuilder svmType(SvmTypeEnum svmType);
        ParameterBuilder kernelType(KernelTypeEnum kernelType);
        ParameterBuilder degree(Integer degree);
        ParameterBuilder gamma(Double gamma);
        ParameterBuilder coef0(Double coef0);

        ParameterBuilder cacheSize(Double cacheSize);
        ParameterBuilder eps(Double eps);
        ParameterBuilder c(Double c);
        ParameterBuilder nu(Double nu);
        ParameterBuilder p(Double p);
        ParameterBuilder shrinking(Boolean shrinking);
        ParameterBuilder probability(Boolean probability);
        LibsvmParameterEntity done();
    }

    private static class DefaultBuilder implements ParameterBuilder {
        private LibsvmParameterEntity libsvmParameterEntity;

        @Override
        public ParameterBuilder svmType(SvmTypeEnum svmType) {
            libsvmParameterEntity.setSvmType(svmType);
            return this;
        }

        @Override
        public ParameterBuilder kernelType(KernelTypeEnum kernelType) {
            libsvmParameterEntity.setKernelType(kernelType);
            return this;
        }

        @Override
        public ParameterBuilder degree(Integer degree) {
            libsvmParameterEntity.setDegree(degree);
            return this;
        }

        @Override
        public ParameterBuilder gamma(Double gamma) {
            libsvmParameterEntity.setGamma(gamma);
            return this;
        }

        @Override
        public ParameterBuilder coef0(Double coef0) {
            libsvmParameterEntity.setCoef0(coef0);
            return this;
        }

        @Override
        public ParameterBuilder cacheSize(Double cacheSize) {
            libsvmParameterEntity.setCacheSize(cacheSize);
            return this;
        }

        @Override
        public ParameterBuilder eps(Double eps) {
            libsvmParameterEntity.setEps(eps);
            return this;
        }

        @Override
        public ParameterBuilder c(Double c) {
            libsvmParameterEntity.setC(c);
            return this;
        }

        @Override
        public ParameterBuilder nu(Double nu) {
            libsvmParameterEntity.setNu(nu);
            return this;
        }

        @Override
        public ParameterBuilder p(Double p) {
            libsvmParameterEntity.setP(p);
            return this;
        }

        @Override
        public ParameterBuilder shrinking(Boolean shrinking) {
            libsvmParameterEntity.setShrinking(shrinking);
            return this;
        }

        @Override
        public ParameterBuilder probability(Boolean probability) {
            libsvmParameterEntity.setProbability(probability);
            return this;
        }

        @Override
        public LibsvmParameterEntity done() {
            return this.libsvmParameterEntity;
        }
    }

    public SvmTypeEnum getSvmType() {
        return svmType;
    }

    public void setSvmType(SvmTypeEnum svmType) {
        this.svmType = svmType;
    }

    public KernelTypeEnum getKernelType() {
        return kernelType;
    }

    public void setKernelType(KernelTypeEnum kernelType) {
        this.kernelType = kernelType;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public Double getGamma() {
        return gamma;
    }

    public void setGamma(Double gamma) {
        this.gamma = gamma;
    }

    public Double getCoef0() {
        return coef0;
    }

    public void setCoef0(Double coef0) {
        this.coef0 = coef0;
    }

    public Double getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(Double cacheSize) {
        this.cacheSize = cacheSize;
    }

    public Double getEps() {
        return eps;
    }

    public void setEps(Double eps) {
        this.eps = eps;
    }

    public Double getC() {
        return c;
    }

    public void setC(Double c) {
        this.c = c;
    }

    public Double getNu() {
        return nu;
    }

    public void setNu(Double nu) {
        this.nu = nu;
    }

    public Double getP() {
        return p;
    }

    public void setP(Double p) {
        this.p = p;
    }

    public Boolean getShrinking() {
        return shrinking;
    }

    public void setShrinking(Boolean shrinking) {
        this.shrinking = shrinking;
    }

    public Boolean getProbability() {
        return probability;
    }

    public void setProbability(Boolean probability) {
        this.probability = probability;
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("svmType", svmType));
        fields.add(new SignificantField("kernelType", kernelType));
        fields.add(new SignificantField("degree", degree));
        fields.add(new SignificantField("gamma", gamma));
        fields.add(new SignificantField("coef0", coef0));
        fields.add(new SignificantField("cacheSize", cacheSize));
        fields.add(new SignificantField("eps", eps));
        fields.add(new SignificantField("c", c));
        fields.add(new SignificantField("nu", nu));
        fields.add(new SignificantField("p", p));
        fields.add(new SignificantField("shrinking", shrinking));
        fields.add(new SignificantField("probability", probability));
        return fields;
    }
}
