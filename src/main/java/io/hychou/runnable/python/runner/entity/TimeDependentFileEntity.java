package io.hychou.runnable.python.runner.entity;

import io.hychou.common.SignificantField;
import io.hychou.file.entity.FileEntity;
import io.hychou.runnable.TimeDependentEntity;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class TimeDependentFileEntity extends TimeDependentEntity<FileEntity> {

    public TimeDependentFileEntity() {
    }

    public TimeDependentFileEntity(FileEntity fileEntity) {
        this.crudTimeVariantDataStructure = fileEntity;
        this.requiredTimestamp = (Date) fileEntity.getLastModified().clone();
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("name", this.crudTimeVariantDataStructure.getName()));
        fields.add(new SignificantField("requiredTimestamp", this.requiredTimestamp));
        return fields;
    }
}
