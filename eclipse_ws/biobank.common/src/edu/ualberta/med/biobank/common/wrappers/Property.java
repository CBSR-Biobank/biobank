package edu.ualberta.med.biobank.common.wrappers;

import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.util.TypeReference;

public class Property<T, W> {
    private final String name;
    private final TypeReference<T> type;

    // TODO: include the model class as a type parameter and check this
    // against what it's called on?

    private Property(String name, TypeReference<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type.getType();
    }

    public static <E, W> Property<E, W> create(String name,
        TypeReference<E> type) {
        return new Property<E, W>(name, type);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Property<?, ?> other = (Property<?, ?>) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;

    public static String concatNames(Property<?>... props) {
        String[] propNames = new String[props.length];
        int count = 0;
        for (Property<?> prop : props) {
            propNames[count++] = prop.getName();
        }
        return StringUtils.join(propNames, '.');
    }
}
