package io.hychou.runnable.python.runner.entity;

import io.hychou.common.SignificantField;
import io.hychou.runnable.TimeDependentEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class TimeDependentAnacondaYamlEntity extends TimeDependentEntity<AnacondaYamlEntity> {
    public TimeDependentAnacondaYamlEntity() {
    }

    public TimeDependentAnacondaYamlEntity(AnacondaYamlEntity anacondaYamlEntity) {
        this.crudTimeVariantDataStructure = anacondaYamlEntity;
        this.requiredTimestamp = (Date) anacondaYamlEntity.getLastModified().clone();
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("name", this.crudTimeVariantDataStructure.getName()));
        fields.add(new SignificantField("requiredTimestamp", this.requiredTimestamp));
        return fields;
    }
}
