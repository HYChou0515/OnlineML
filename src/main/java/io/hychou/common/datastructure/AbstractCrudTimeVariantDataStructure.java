package io.hychou.common.datastructure;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public abstract class AbstractCrudTimeVariantDataStructure extends AbstractCrudDataStructure {
    @Getter
    @Setter
    protected Date lastModified;
}
