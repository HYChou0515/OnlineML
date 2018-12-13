package io.hychou.libsvm.entity;

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
