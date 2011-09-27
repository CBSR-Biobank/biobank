package edu.ualberta.med.biobank.common.wrappers;

public enum EventAttrTypeEnum {
    SELECT_SINGLE("select_single"), SELECT_MULTIPLE("select_multiple"), NUMBER( //$NON-NLS-1$ //$NON-NLS-2$
        "number"), DATE_TIME("date_time"), TEXT("text"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

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
