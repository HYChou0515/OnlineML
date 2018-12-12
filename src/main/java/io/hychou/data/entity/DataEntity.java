package io.hychou.data.entity;

import io.hychou.common.AbstractDataStructure;
import io.hychou.common.Constant;
import io.hychou.common.SignificantField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DataEntity extends AbstractDataStructure {
    @Id
    private String name;

    @Column(length= Constant.GB)
    private byte[] dataBytes;

    public DataEntity(){}

    public DataEntity(String name, byte[] dataBytes) {
        this.name = name;
        this.dataBytes = dataBytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        fields.add(new SignificantField("name", name));
        return fields;
    }
}