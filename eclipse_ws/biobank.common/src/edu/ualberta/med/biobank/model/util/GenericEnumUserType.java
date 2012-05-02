package edu.ualberta.med.biobank.model.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

@SuppressWarnings("nls")
public class GenericEnumUserType implements UserType, ParameterizedType {
    private static final String DEFAULT_IDENTIFIER_METHOD_NAME = "getId";
    private static final String DEFAULT_VALUE_OF_METHOD_NAME = "fromId";

    @SuppressWarnings("rawtypes")
    private Class<? extends Enum> enumClass;
    private Class<?> identifierType;
    private Method identifierMethod;
    private Method valueOfMethod;
    private AbstractSingleColumnStandardBasicType<?> type;
    private int[] sqlTypes;

    @Override
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        try {
            enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
        } catch (ClassNotFoundException exception) {
            throw new HibernateException("Enum class not found", exception);
        }

        String identifierMethodName =
            parameters.getProperty("identifierMethod",
                DEFAULT_IDENTIFIER_METHOD_NAME);

        try {
            identifierMethod = enumClass.getMethod(identifierMethodName,
                new Class[0]);
            identifierType = identifierMethod.getReturnType();
        } catch (Exception exception) {
            throw new HibernateException("Failed to optain identifier method",
                exception);
        }

        TypeResolver tr = new TypeResolver();
        type =
            (AbstractSingleColumnStandardBasicType<?>) tr.basic(identifierType
                .getName());
        if (type == null) {
            throw new HibernateException("Unsupported identifier type "
                + identifierType.getName());
        }
        sqlTypes = new int[] { type.sqlType() };

        String valueOfMethodName = parameters.getProperty("valueOfMethod",
            DEFAULT_VALUE_OF_METHOD_NAME);

        try {
            valueOfMethod = enumClass.getMethod(valueOfMethodName,
                new Class[] { identifierType });
        } catch (Exception exception) {
            throw new HibernateException("Failed to optain valueOf method",
                exception);
        }
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
        Object identifier = type.get(rs, names[0]);
        try {
            return valueOfMethod.invoke(enumClass, new Object[] { identifier });
        } catch (Exception exception) {
            throw new HibernateException(
                "Exception while invoking valueOfMethod of enumeration class: ",
                exception);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index)
        throws HibernateException, SQLException {
        try {
            Object identifier =
                value != null ? identifierMethod.invoke(value, new Object[0])
                    : null;
            st.setObject(index, identifier);
        } catch (Exception exception) {
            throw new HibernateException(
                "Exception while invoking identifierMethod of enumeration class: ",
                exception);

        }
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