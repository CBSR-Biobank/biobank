package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;

public abstract class AbstractValidator implements IValidator {

    protected final String errorMessage;

    protected ControlDecoration controlDecoration;

    public AbstractValidator(String message) {
        super();
        this.errorMessage = message;
    }

    public void setControlDecoration(ControlDecoration controlDecoration) {
        this.controlDecoration = controlDecoration;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public abstract IStatus validate(Object value);

}
