package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;

public abstract class AbstractValidator implements IValidator {
    
    protected final String message;
    
    protected final ControlDecoration controlDecoration;
    
    public AbstractValidator(String message, ControlDecoration controlDecoration) {
        super();
        this.message = message;
        this.controlDecoration = controlDecoration;
    }

    @Override
    public abstract IStatus validate(Object value);

}
