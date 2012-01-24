package edu.ualberta.med.biobank.common.action.constraint;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.ActionContext;

public interface IConstraintValidator<T> {
    Set<IConstraintValidator<T>> validate(T value, ActionContext context);

    Set<Class<?>> getGroups();
}
