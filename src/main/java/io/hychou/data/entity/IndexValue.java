package io.hychou.data.entity;
import io.hychou.common.AbstractDataStructure;
import io.hychou.common.SignificantField;

import java.util.ArrayList;
import java.util.List;

public class IndexValue extends AbstractDataStructure {
    private int index;
    private double value;

    public IndexValue() {
    }

    public IndexValue(int index, double value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("index", index));
        fields.add(new SignificantField("value", value));
        return fields;
    }
}