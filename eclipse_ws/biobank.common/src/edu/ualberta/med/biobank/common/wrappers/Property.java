package edu.ualberta.med.biobank.common.wrappers;

import java.lang.reflect.Type;

import edu.ualberta.med.biobank.common.util.TypeReference;

public class Property<T> {

    private final String name;

    private final TypeReference<T> type;

    private Property(String name, TypeReference<T> tr) {
        this.name = name;
        this.type = tr;
    }

    public static <T> Property<T> create(String propname, TypeReference<T> tr) {
        return new Property<T>(propname, tr);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type.getType();
    }
}
