package edu.ualberta.med.biobank.common.wrappers;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
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
public class Property<T, W> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String propertyChangeName;
    private final TypeInfo typeInfo;
    private final Accessor<T, W> accessor;

    private Property(String name, TypeReference<T> typeReference,
        Accessor<T, W> accessor) {
        this.name = name;
        this.propertyChangeName = name;
        this.typeInfo = new TypeInfo(typeReference);
        this.accessor = accessor;
    }

    private Property(String name, String propertyChangeName, TypeInfo typeInfo,
        Accessor<T, W> accessor) {
        this.name = name;
        this.propertyChangeName = propertyChangeName;
        this.typeInfo = typeInfo;
        this.accessor = accessor;
    }

    public String getName() {
        return name;
    }

    public String getPropertyChangeName() {
        return propertyChangeName;
    }

    /**
     * @return the {@code Class} of the elements in the {@code Collection}
     *         returned by this class, otherwise the {@code Class} itself.
     */
    public Class<?> getElementClass() {
        return typeInfo.elementClass;
    }

    public boolean isCollection() {
        return typeInfo.isCollection;
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
     * @param propertyChangeName a new name to use for the property when firing
     *            a change event (should correspond to the
     *            <code>ModelWrapper</code>'s method name)
     * @param property
     * @return
     */
    public <T2> Property<T2, W> wrap(String propertyChangeName,
        final Property<T2, ? super T> property) {
        Accessor<T2, W> accessor = new Accessor<T2, W>() {
            private static final long serialVersionUID = 1L;

            @Override
            public T2 get(W model) {
                T association = Property.this.accessor.get(model);
                return association == null ? null : property.get(association);
            }

            @Override
            public void set(W model, T2 value) {
                T association = Property.this.accessor.get(model);
                if (association != null) {
                    property.set(association, value);
                }
            }
        };

        return new Property<T2, W>(concatNames(this, property),
            propertyChangeName, property.typeInfo, accessor);
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

    @Override
    public String toString() {
        return name + "(" + typeInfo.toString + ")";
    }

    public interface Accessor<T, W> extends Serializable {
        public T get(W model);

        public void set(W model, T value);
    };

    /**
     * Because {@code TypeReference} is not necessarily {@code Serializable},
     * this internal class is used to extract all the necessary information,
     * encapsulate it, and all it to be serialized.
     * 
     * @author jferland
     * 
     */
    private static final class TypeInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private final Class<?> elementClass;
        private final boolean isCollection;
        private final String toString;

        public TypeInfo(TypeReference<?> typeReference) {
            Type type = typeReference.getType();
            this.elementClass = getElementClass(type);
            this.isCollection = isCollection(type);
            this.toString = type.toString();
        }

        private static Class<?> getElementClass(Type type) {
            Class<?> klazz = null;
            if (type instanceof Class<?>) {
                klazz = (Class<?>) type;
            } else if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                Type[] elementTypes = pType.getActualTypeArguments();
                if (elementTypes.length > 0) {
                    Type elementType = elementTypes[0];
                    klazz = getElementClass(elementType);
                }
            }
            return klazz;
        }

        private static boolean isCollection(Type type) {
            boolean isCollection = false;

            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                Type rawType = pType.getRawType();
                if (rawType instanceof Class) {
                    Class<?> rawTypeClass = (Class<?>) rawType;
                    if (rawTypeClass.isAssignableFrom(Collection.class)) {
                        isCollection = true;
                    }
                }
            }

            return isCollection;
        }
    }
}
