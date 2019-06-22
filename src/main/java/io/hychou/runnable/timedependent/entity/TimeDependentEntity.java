package io.hychou.runnable.timedependent.entity;

import io.hychou.common.SignificantField;
import io.hychou.common.datastructure.AbstractCrudDataStructure;
import io.hychou.common.datastructure.blob.entity.BlobEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class TimeDependentEntity<E extends BlobEntity> extends AbstractCrudDataStructure {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @ManyToOne(targetEntity = BlobEntity.class)
    private E timeVariantData;

    @Getter
    @Setter
    private Date requiredTimestamp;

    public TimeDependentEntity() {
    }

    public TimeDependentEntity(E timeVariantData) {
        this.timeVariantData = timeVariantData;
        this.requiredTimestamp = timeVariantData.getLastModified();
    }

    public boolean isDependencyValid() {
        return timeVariantData.getLastModified().equals(requiredTimestamp);
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("id", this.id));
        fields.add(new SignificantField("timeVariantData", this.timeVariantData));
        fields.add(new SignificantField("requiredTimestamp", this.requiredTimestamp));
        return fields;
    }
}
