package edu.ualberta.med.biobank.validator.constraint.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import edu.ualberta.med.biobank.validator.constraint.UniqueElements;

@SuppressWarnings("nls")
public class UniqueElementsValidator implements
    ConstraintValidator<UniqueElements, Object> {

    private static final String NOT_A_COLLECTION =
        "{edu.ualberta.med.biobank.validator.constraint.UniqueElements.notACollection}";
    private static final String MISSING_PROPERTY =
        "{edu.ualberta.med.biobank.validator.constraint.UniqueElements.missingProperty}";

    private String[] properties;

    @Override
    public void initialize(UniqueElements annotation) {
        this.properties = annotation.properties();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value instanceof Collection) {
            try {
                boolean unique = isUniquePropertyValues((Collection<?>) value);
                return unique;
            } catch (Exception e) {
                context.buildConstraintViolationWithTemplate(MISSING_PROPERTY)
                    .addConstraintViolation();
                return false;
            }
        }
        context.buildConstraintViolationWithTemplate(NOT_A_COLLECTION)
            .addConstraintViolation();
        return false;
    }

    private <T> boolean isUniquePropertyValues(Collection<T> collection)
        throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException, SecurityException, NoSuchMethodException {
        if (collection.isEmpty()) return true;
        Set<List<Object>> usedPropertyValues = new HashSet<List<Object>>();
        Class<?> elementClass = collection.iterator().next().getClass();
        List<Method> propertyGetters = getPropertyGetters(elementClass);
        for (T element : collection) {
            List<Object> propertyValues = getValues(propertyGetters, element);
            if (!usedPropertyValues.add(propertyValues)) return false;
        }
        return true;
    }

    private List<Object> getValues(List<Method> getters, Object object)
        throws IllegalArgumentException, IllegalAccessException,
        InvocationTargetException {
        List<Object> propertyValues = new ArrayList<Object>();
        for (Method getter : getters) {
            Object propertyValue = getter.invoke(object);
            propertyValues.add(propertyValue);
        }
        return propertyValues;
    }

    private List<Method> getPropertyGetters(Class<?> klazz)
        throws SecurityException, NoSuchMethodException {
        List<Method> getters = new ArrayList<Method>(properties.length);
        for (String property : properties) {
            Method getter = getPropertyGetter(klazz, property);
            getters.add(getter);
        }
        return getters;
    }

    private static Method getPropertyGetter(Class<?> klazz, String propertyName)
        throws SecurityException, NoSuchMethodException {
        try {
            String methodName = "get" + ucFirst(propertyName);
            return klazz.getMethod(methodName);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }

        String methodName = "is" + ucFirst(propertyName);
        return klazz.getMethod(methodName);
    }

    private static String ucFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        if (s.length() == 1) return s.toUpperCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
