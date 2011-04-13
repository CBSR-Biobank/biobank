package edu.ualberta.med.biobank.common.wrappers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.util.TypeReference;

/**
 * 
 * @author jferland
 * 
 * @param <T> the type of the Property
 * @param <W> the type that has the Property
 */
public class Property<T, W> {
    private final String name;
    private final TypeReference<T> type;
    private final Accessor<T, W> accessor;

    // TODO: include the model class as a type parameter and check this
    // against what it's called on?

    private Property(String name, TypeReference<T> type, Accessor<T, W> accessor) {
        this.name = name;
        this.type = type;
        this.accessor = accessor;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type.getType();
    }

    public T get(W model) {
        return accessor.get(model);
    }

    public void set(W model, T value) {
        accessor.set(model, value);
    }

    public <T2> Property<T2, W> wrap(final Property<T2, ? super T> property) {
        return wrap(property.name, property);
    }

    /**
     * Creates a new <code>Property</code> by treating the <code>Property</code>
     * of an association as a direct property.
     * 
     * @param <T2>
     * @param name a new name to use for the property (should correspond to the
     *            <code>ModelWrapper</code>'s method name)
     * @param property
     * @return
     */
    public <T2> Property<T2, W> wrap(String name,
        final Property<T2, ? super T> property) {
        Accessor<T2, W> accessor = new Accessor<T2, W>() {
            @Override
            public T2 get(W model) {
                T association = Property.this.accessor.get(model);
                return property.get(association);
            }

            @Override
            public void set(W model, T2 value) {
                T association = Property.this.accessor.get(model);
                property.set(association, value);
            }
        };

        return new Property<T2, W>(name, property.type, accessor);
    }

    public Collection<Property<?, W>> wrap(
        Collection<Property<?, ? super T>> properties) {
        List<Property<?, W>> wrappedProperties = new ArrayList<Property<?, W>>();

        for (Property<?, ? super T> property : properties) {
            Property<?, W> wrappedProperty = wrap(property.name, property);
            wrappedProperties.add(wrappedProperty);
        }

        return wrappedProperties;
    }

    public static String concatNames(Property<?, ?>... props) {
        String[] propNames = new String[props.length];
        int count = 0;
        for (Property<?, ?> prop : props) {
            propNames[count++] = prop.getName();
        }
        return StringUtils.join(propNames, '.');
    }

    public static <T, W> Property<T, W> create(String name,
        TypeReference<T> type, Accessor<T, W> accessor) {
        return new Property<T, W>(name, type, accessor);
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
    }

    public interface Accessor<T, W> {
        public T get(W model);

        public void set(W model, T value);
    }
}
