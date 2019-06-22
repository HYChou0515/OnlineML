package io.hychou.common.datastructure.blob.entity;

import io.hychou.common.Constant;
import io.hychou.common.SignificantField;
import io.hychou.common.datastructure.AbstractCrudTimeVariantDataStructure;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class BlobEntity extends AbstractCrudTimeVariantDataStructure {
    @Id
    @GeneratedValue
    @Getter
    @Setter
    protected Long id;

    @Getter
    @Setter
    protected String name;

    @Column(length = Constant.GB)
    @Getter
    @Setter
    protected byte[] blobBytes;

    public BlobEntity() {
    }

    public BlobEntity(String name, byte[] blobBytes) {
        this.name = name;
        this.blobBytes = blobBytes;
    }

    @PrePersist
    @PreUpdate
    public void updateTimeStamps() {
        this.lastModified = new Date();
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("id", this.id));
        fields.add(new SignificantField("name", this.name));
        fields.add(new SignificantField("lastModified", this.lastModified));
        return fields;
    }
}
