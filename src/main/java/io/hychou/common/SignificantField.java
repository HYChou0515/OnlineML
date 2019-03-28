package io.hychou.common;

import java.util.Objects;

public class SignificantField {
    private String name;
    private Object field;

    public SignificantField(String name, Object field) {
        this.name = name;
        this.field = field;
    }

    public String getName() {
        if(name == null){
            return Constant.NULL_STRING;
        }
        return name;
    }

    public Object getField() {
        if(field == null){
            return Constant.NULL_STRING;
        }
        return field;
    }

    @Override
    public boolean equals(Object aThat) {
        if (this == aThat) return true;
        if (null == aThat) return false;
        if ( aThat.getClass() != this.getClass()) return false;
        SignificantField that = (SignificantField) aThat;
        return Objects.equals(this.getName(), that.getName()) &&
                Objects.equals(this.getField(), that.getField());
    }
}
