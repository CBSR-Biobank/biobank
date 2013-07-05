package edu.ualberta.med.biobank.common.action.eventattr;

@SuppressWarnings("nls")
public enum EventAttrTypeEnum {
    SELECT_SINGLE("select_single"),
    SELECT_MULTIPLE("select_multiple"),
    NUMBER("number"),
    DATE_TIME("date_time"),
    TEXT("text");

    private String name;

    private EventAttrTypeEnum(String name) {
        this.name = name;
    }

    public boolean isSameType(String name) {
        return this.name.equals(name);
    }

    public String getName() {
        return name;
    }

    public boolean isSelectType() {
        return name.startsWith("select_");
    }

    public static EventAttrTypeEnum getEventAttrType(String name) {
        return valueOf(name.toUpperCase());
    }
}
