package edu.ualberta.med.biobank.common.action.constraint;

import java.util.Set;

public interface IValidator {
    public <T> Set<ConstraintViolation<T>> validate(T object,
        Class<?>... groups);
}
