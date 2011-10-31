package edu.ualberta.med.biobank.common.wrappers;

public enum EventAttrTypeEnum {
    SELECT_SINGLE("select_single"), //$NON-NLS-1$
    SELECT_MULTIPLE("select_multiple"), //$NON-NLS-1$
    NUMBER("number"), //$NON-NLS-1$
    DATE_TIME("date_time"), //$NON-NLS-1$
    TEXT("text"); //$NON-NLS-1$

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
        return name.startsWith("select_"); //$NON-NLS-1$
    }

    public static EventAttrTypeEnum getEventAttrType(String name) {
        return valueOf(name.toUpperCase());
    }
}
