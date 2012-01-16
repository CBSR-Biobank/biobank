package edu.ualberta.med.biobank.common.action.constraint;

import edu.ualberta.med.biobank.common.action.ActionContext;

public class NotEmptyValidator implements IConstraintValidator<String> {
    public NotEmptyValidator(String message) {
    }

    @Override
    public void validate(String value, ActionContext context) {
        if (value == null || value.isEmpty()) {

        }
    }
}
