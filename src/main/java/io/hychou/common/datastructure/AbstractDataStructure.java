package io.hychou.common.datastructure;

import com.google.common.base.MoreObjects;
import io.hychou.common.SignificantField;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractDataStructure {

    @Override
    public boolean equals(Object aThat) {
        if (this == aThat) return true;
        if (null == aThat) return false;
        if ( aThat.getClass() != this.getClass()) return false;
        AbstractDataStructure that = (AbstractDataStructure) aThat;
        List<SignificantField> thisFields = this.significantFields();
        List<SignificantField> thatFields = that.significantFields();
        for(int i = 0; i<thisFields.size(); i++) {
            if (!Objects.equals(thisFields.get(i), thatFields.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);
        for(SignificantField significantField : significantFields()) {
            toStringHelper.add(significantField.getName(), significantField.getField());
        }
        return toStringHelper.toString();
    }

    @Override
    public int hashCode() {
        return significantFields().stream()
                .map(SignificantField::getField)
                .collect(Collectors.toList()).hashCode();
    }

    public abstract List<SignificantField> significantFields();
}
