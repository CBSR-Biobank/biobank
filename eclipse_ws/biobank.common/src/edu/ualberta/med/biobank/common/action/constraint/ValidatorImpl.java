package edu.ualberta.med.biobank.common.action.constraint;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ValidatorImpl implements IValidator {
    private final Map<Class<?>, Object> validators = null;

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T object,
        Class<?>... groups) {
        Set<ConstraintViolation<T>> violations =
            new HashSet<ConstraintViolation<T>>();

        for (Entry<Class<?>, Object> entry : validators.entrySet()) {
            Class<?> klazz = entry.getKey();
            if (klazz.isAssignableFrom(object.getClass())) {
                // if is in group, then run validators.
            }
        }

        return violations;
    }
}
