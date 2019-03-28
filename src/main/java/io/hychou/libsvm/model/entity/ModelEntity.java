package io.hychou.libsvm.model.entity;

import io.hychou.common.Constant;
import io.hychou.common.SignificantField;
import io.hychou.common.datastructure.AbstractCrudDataStructure;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ModelEntity extends AbstractCrudDataStructure {
    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Column(length= Constant.GB)
    @Getter
    @Setter
    private byte[] dataBytes;

    public ModelEntity(){}

    public ModelEntity(Long id, byte[] dataBytes) {
        this.id = id;
        this.dataBytes = dataBytes;
    }

    public ModelEntity(byte[] dataBytes) {
        this.dataBytes = dataBytes;
    }

    public String getFileName() {
        return "model"+id;
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("id", id));
        return fields;
    }
}
