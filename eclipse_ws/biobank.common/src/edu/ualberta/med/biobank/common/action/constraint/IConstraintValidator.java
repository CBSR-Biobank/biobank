package edu.ualberta.med.biobank.common.action.constraint;

import edu.ualberta.med.biobank.common.action.ActionContext;

public interface IConstraintValidator<T> {
    void validate(T value, ActionContext context);
}
