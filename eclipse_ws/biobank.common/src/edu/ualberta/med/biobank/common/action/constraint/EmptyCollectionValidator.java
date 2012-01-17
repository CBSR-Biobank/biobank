package edu.ualberta.med.biobank.common.action.constraint;

import edu.ualberta.med.biobank.common.action.ActionContext;

public class EmptyCollectionValidator implements IConstraintValidator<Object> {
    private final String property;

    public EmptyCollectionValidator(String property) {
        this.property = property;
    }

    @Override
    public void validate(Object value, ActionContext context) {

    }
}
