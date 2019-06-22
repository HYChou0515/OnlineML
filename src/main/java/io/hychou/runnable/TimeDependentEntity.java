package io.hychou.runnable;

import io.hychou.common.SignificantField;
import io.hychou.common.datastructure.AbstractCrudDataStructure;
import io.hychou.common.datastructure.AbstractCrudTimeVariantDataStructure;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MappedSuperclass
public class TimeDependentEntity<E extends AbstractCrudTimeVariantDataStructure> extends AbstractCrudDataStructure {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    protected Long id;

    @Getter
    @Setter
    @ManyToOne()
    protected E crudTimeVariantDataStructure;

    @Getter
    @Setter
    protected Date requiredTimestamp;

    public TimeDependentEntity() {
    }

    public TimeDependentEntity(Long id, E crudTimeVariantDataStructure, Date requiredTimestamp) {
        this.id = id;
        this.crudTimeVariantDataStructure = crudTimeVariantDataStructure;
        this.requiredTimestamp = requiredTimestamp;
    }

    public TimeDependentEntity(E crudTimeVariantDataStructure) {
        this.crudTimeVariantDataStructure = crudTimeVariantDataStructure;
        this.requiredTimestamp = (Date) crudTimeVariantDataStructure.getLastModified().clone();
    }

    public boolean isDenpendencyValid() {
        return crudTimeVariantDataStructure.getLastModified().equals(requiredTimestamp);
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("id", this.id));
        fields.add(new SignificantField("crudTimeVariantDataStructure", this.crudTimeVariantDataStructure));
        fields.add(new SignificantField("requiredTimestamp", this.requiredTimestamp));
        return fields;
    }
}
