package io.hychou.libsvm.parameter;

import io.hychou.common.AbstractDataStructure;
import io.hychou.common.SignificantField;

import java.util.ArrayList;
import java.util.List;

public class LibsvmPredictParameterEntity extends AbstractDataStructure {

    private Boolean probabilityEstimates;

    public LibsvmPredictParameterEntity() {
        this.probabilityEstimates = false;
    }

    public LibsvmPredictParameterEntity(Boolean probabilityEstimates) {
        this.probabilityEstimates = probabilityEstimates;
    }

    public Boolean getProbabilityEstimates() {
        return probabilityEstimates;
    }

    public void setProbabilityEstimates(Boolean probabilityEstimates) {
        this.probabilityEstimates = probabilityEstimates;
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("probabilityEstimates", probabilityEstimates));
        return fields;
    }
}
