package edu.ualberta.med.biobank.common.wrappers;

public enum SpecimenAttrTypeEnum {
    SELECT_SINGLE("select_single"), SELECT_MULTIPLE("select_multiple"), NUMBER(
        "number"), DATE_TIME("date_time"), TEXT("text");

    private String name;

    private SpecimenAttrTypeEnum(String name) {
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

    public static SpecimenAttrTypeEnum getSpecimenAttrType(String name) {
        return valueOf(name.toUpperCase());
    }
}
