package io.hychou.libsvm.prediction.entity;

import io.hychou.common.datastructure.AbstractCrudDataStructure;
import io.hychou.common.Constant;
import io.hychou.common.SignificantField;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PredictionEntity extends AbstractCrudDataStructure {
    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Column(length = Constant.GB)
    @Getter
    @Setter
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
        return "prediction" + id;
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("id", id));
        return fields;
    }
}
