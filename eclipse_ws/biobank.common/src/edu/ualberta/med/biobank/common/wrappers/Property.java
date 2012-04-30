package edu.ualberta.med.biobank.common.wrappers;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.property.GetterInterceptor;
import edu.ualberta.med.biobank.common.wrappers.property.PropertyLink;

/**
 * 
 * @author jferland
 * 
 * @param <P> the type of this {@link Property}
 * @param <M> the type of the model that has this {@link Property}
 */
public class Property<P, M> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Pattern NAME_SPLITTER = Pattern.compile("\\."); //$NON-NLS-1$

    private final String name;
    private final List<String> splitNames;
    private final String propertyChangeName;
    private final Class<M> modelClass;
    private final TypeInfo typeInfo;
    private final Accessor<P, M> accessor;
    private final PropertyLink<P, ?, M> link;

    private Property(String name, Class<M> modelClass,
        TypeReference<P> typeReference, Accessor<P, M> accessor) {
        this.name = name;
        this.splitNames = Arrays.asList(NAME_SPLITTER.split(name));
        this.propertyChangeName = name;
        this.modelClass = modelClass;
        this.typeInfo = new TypeInfo(typeReference);
        this.link = null;
        this.accessor = accessor;
    }

    private Property(String name, String propertyChangeName,
        Class<M> modelClass, TypeInfo typeInfo, PropertyLink<P, ?, M> link) {
        this.name = name;
        this.splitNames = Arrays.asList(NAME_SPLITTER.split(name));
        this.propertyChangeName = propertyChangeName;
        this.modelClass = modelClass;
        this.typeInfo = typeInfo;
        this.link = link;
        this.accessor = link;
    }

    public String getName() {
        return name;
    }

    private static <P, A, M> P get(PropertyLink<P, A, M> link, M model,
        GetterInterceptor getter) {
        P value = null;

        Property<A, M> fromProperty = link.getFrom();
        A from = fromProperty.get(model, getter);

        if (from != null) {
            Property<P, ? super A> toProperty = link.getTo();
            value = toProperty.get(from, getter);
        }

        return value;
    }

    public P get(M model, GetterInterceptor getter) {
        P value = null;

        if (link != null) {
            value = get(link, model, getter);
        } else {
            value = getter.get(this, model);
        }

        return value;
    }

    private static <P, A, M> void set(PropertyLink<P, A, M> link, M model,
        P value, GetterInterceptor getter) {
        Property<A, M> fromProperty = link.getFrom();
        A from = fromProperty.get(model, getter);

        Property<P, ? super A> toProperty = link.getTo();
        toProperty.set(from, value, getter);
    }

    public void set(M model, P value, GetterInterceptor getter) {
        if (link != null) {
            set(link, model, value, getter);
        } else {
            set(model, value);
        }
    }

    public String getPropertyChangeName() {
        return propertyChangeName;
    }

    /**
     * @return the {@link Class} of the elements in the {@link Collection}
     *         returned by this class, otherwise the {@link Class} itself.
     */
    public Class<?> getElementClass() {
        return typeInfo.elementClass;
    }

    public boolean isCollection() {
        return typeInfo.isCollection;
    }

    public Class<M> getModelClass() {
        return modelClass;
    }

    public P get(M model) {
        return accessor.get(model);
    }

    public void set(M model, P value) {
        accessor.set(model, value);
    }

    /**
     * An alias for the {@code wrap()} method. Behaves exactly the same, but
     * with a shorter method name for use such as
     * 
     * <pre>
     *      Property cThroughA = A.to(B.to(C))
     *      C c = cThroughA.get(a);
     * </pre>
     * 
     * @param <T2>
     * @param property
     * @returnAssociationAccessor
     */
    public <T2> Property<T2, M> to(final Property<T2, ? super P> property) {
        return wrap(property.name, property);
    }

    // TODO: write an "ofEach" method that could return a PropertyCollection
    // with get() and set() methods that return and take lists, respectively?
    // Could also make a PropertyName object that provides methods to construct
    // an at-compile-time checked list of properties?? COOOOOOOL! ;-) e.g.
    // PropertyName.start(SpecimenPeer.ID).ofEach(SpecimenPeer.CHILD_SPECIMEN_COLLECTION)).get();

    public <T2> Property<T2, M> wrap(final Property<T2, ? super P> property) {
        return wrap(property.name, property);
    }

    /**
     * Creates a new {@link Property} by treating the {@link Property} of an
     * association as a direct property.
     * 
     * @param <A>
     * @param propertyChangeName a new name to use for the property when firing
     *            a change event (should correspond to the {@link ModelWrapper}
     *            's method name)
     * @param property
     * @return
     */
    public <A> Property<A, M> wrap(String propertyChangeName,
        final Property<A, ? super P> property) {
        PropertyLink<A, ?, M> link = new PropertyLink<A, P, M>(this, property);

        return new Property<A, M>(concatNames(this, property),
            propertyChangeName, modelClass, property.typeInfo, link);
    }

    public Collection<Property<?, M>> wrap(
        Collection<Property<?, ? super P>> properties) {
        List<Property<?, M>> wrappedProperties = new ArrayList<Property<?, M>>();

        for (Property<?, ? super P> property : properties) {
            Property<?, M> wrappedProperty = wrap(property.name, property);
            wrappedProperties.add(wrappedProperty);
        }

        return wrappedProperties;
    }

    /**
     * Returns a copy of a {@link List} of the component names that make up the
     * name of this {@link Property}. For example, if a {@link Property} has a
     * name of "specimen" then this method would return ("specimen"). However,
     * if a {@link Property} has a name of "specimen.container.id" then this
     * method would return the list ("specimen", "container", "id").
     * 
     * @return
     */
    public List<String> getNames() {
        return new ArrayList<String>(splitNames);
    }

    public static String concatNames(Property<?, ?>... props) {
        String[] propNames = new String[props.length];
        int count = 0;
        for (Property<?, ?> prop : props) {
            propNames[count++] = prop.getName();
        }
        return StringUtils.join(propNames, '.');
    }

    public static <P, M> Property<P, M> create(String name,
        Class<M> modelClass, TypeReference<P> type, Accessor<P, M> accessor) {
        return new Property<P, M>(name, modelClass, type, accessor);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    // TODO: include model class or type info for equality check?
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
        return name + "(" + typeInfo.toString + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public interface Accessor<P, M> extends Serializable {
        public P get(M model);

        public void set(M model, P value);
    };

    /**
     * Because {@link TypeReference} is not necessarily {@link Serializable},
     * this internal class is used to extract all the necessary information,
     * encapsulate it, and all it to be serialized.
     * 
     * @author jferland
     * 
     */
    private static final class TypeInfo implements Serializable {
        // TODO: we don't need a TypeReference class to get this information, it
        // can be generated by another file that analyzes the uml and inserted
        // into the definitions of the peer classes
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
