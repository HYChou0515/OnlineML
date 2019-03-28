package io.hychou.common.datastructure;

public abstract class AbstractCrudDataStructure extends AbstractDataStructure {
    private static final String ENTITY_STRING = "Entity";
    private String getEntityName() {
        String className = getClass().getName();
        assert className.endsWith(ENTITY_STRING);
        return className.substring(0, className.length()-ENTITY_STRING.length());
    }
    public String getStringQueryWithNullParam(String paramString) {
        return "Trying to query with null "+paramString;
    }
    public String getStringDeleteWithNullParam(String paramString) {
        return "Trying to delete data with empty "+paramString;
    }
    public String getStringCreateNull() {
        return "Trying to create null "+ getEntityName();
    }
    public String getStringUpdateNull() {
        return "Trying to update null "+ getEntityName();
    }
    public String getStringNotExistForParam(String paramString, Object param) {
        return getEntityName() + "with "+ paramString + "=" + param.toString() + " does not exist";
    }
    public String getStringCreateExistingForParam(String paramString, Object param) {
        return "Trying to create "+getEntityName()+" with existing "+ paramString + "=" + param.toString();
    }
}
