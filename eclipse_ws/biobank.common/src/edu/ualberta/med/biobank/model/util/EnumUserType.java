package edu.ualberta.med.biobank.model.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * An {@link Enum} {@link UserType} that maintains its own internal map of
 * identifier to enum. An exception will be thrown if the same identifier is
 * used more than once. The identifier method is determined by an optional
 * configuration parameter {@link #ID_METHOD_NAME_PARAM}.
 * <p>
 * The enum class <em>must</em> be specified via the configuration parameter
 * {@link #ENUM_CLASS_NAME_PARAM}.
 * 
 * @author Jonathan Ferland
 * 
 * @param <T> the enum type
 */
@SuppressWarnings("nls")
public class EnumUserType<T extends Enum<T>>
    implements UserType, ParameterizedType {

    public static final String ID_METHOD_NAME_PARAM = "identifierMethod";
    public static final String DEF_ID_METHOD_NAME = "getId";

    public static final String ENUM_CLASS_NAME_PARAM = "enumClass";

    private Class<T> enumClass;
    private Class<?> idType;
    private Method idMethod;
    private AbstractSingleColumnStandardBasicType<?> type;
    private int[] sqlTypes;
    private Map<Object, T> values;

    private Map<Object, T> getValuesMap() throws HibernateException {
        if (values == null) {
            Map<Object, T> tmp = new HashMap<Object, T>();

            for (T value : enumClass.getEnumConstants()) {
                Object id = getId(value);
                T oldValue = tmp.put(id, value);

                if (oldValue != null) {
                    throw new HibernateException("Duplicate id " + id
                        + " in enum " + enumClass);
                }
            }

            values = tmp;
        }
        return values;
    }

    private Object getId(Object value) {
        try {
            Object id = value != null
                ? idMethod.invoke(value, new Object[0])
                : null;
            return id;
        } catch (Exception exception) {
            throw new HibernateException(
                "Exception while invoking identifierMethod of enum" + enumClass,
                exception);
        }
    }

    @Override
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty(ENUM_CLASS_NAME_PARAM);
        try {
            @SuppressWarnings("unchecked")
            Class<T> tmp = (Class<T>) Class.forName(enumClassName)
                .asSubclass(Enum.class);
            enumClass = tmp;
        } catch (ClassNotFoundException exception) {
            throw new HibernateException("Enum not found: " + enumClassName,
                exception);
        }

        String idMethodName = parameters.getProperty(ID_METHOD_NAME_PARAM,
            DEF_ID_METHOD_NAME);

        try {
            idMethod = enumClass.getMethod(idMethodName, new Class[0]);
            idType = idMethod.getReturnType();
        } catch (Exception exception) {
            throw new HibernateException("Failed to obtain identifier method",
                exception);
        }

        TypeResolver tr = new TypeResolver();
        type = (AbstractSingleColumnStandardBasicType<?>) tr
            .basic(idType.getName());
        if (type == null) {
            throw new HibernateException("Unsupported identifier type "
                + idType.getName());
        }
        sqlTypes = new int[] { type.sqlType() };
    }

    @Override
    public Class<?> returnedClass() {
        return enumClass;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
        throws HibernateException, SQLException {
        // TODO: hibernate4 adds SessionImplementor to the parameters, so we can
        // call the correct (non-deprecated) type method.
        @SuppressWarnings("deprecation")
        Object id = type.get(rs, names[0]);
        T value = getValuesMap().get(id);
        return value;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index)
        throws HibernateException, SQLException {
        Object id = getId(value);
        st.setObject(index, id);
    }

    @Override
    public int[] sqlTypes() {
        return sqlTypes;
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
        throws HibernateException {
        return cached;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
        throws HibernateException {
        return original;
    }
}