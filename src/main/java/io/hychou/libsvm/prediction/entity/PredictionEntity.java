package io.hychou.libsvm.prediction.entity;

import io.hychou.common.AbstractDataStructure;
import io.hychou.common.Constant;
import io.hychou.common.SignificantField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PredictionEntity extends AbstractDataStructure {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = Constant.GB)
    private byte[] dataBytes;

    public PredictionEntity() {
    }

    public PredictionEntity(Long id, byte[] dataBytes) {
        this.id = id;
        this.dataBytes = dataBytes;
    }

    public PredictionEntity(byte[] dataBytes) {
        this.dataBytes = dataBytes;
    }

    public String getFileName() {
        return "prediction" + getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getDataBytes() {
        return dataBytes;
    }

    public void setDataBytes(byte[] dataBytes) {
        this.dataBytes = dataBytes;
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("id", id));
        return fields;
    }
}
