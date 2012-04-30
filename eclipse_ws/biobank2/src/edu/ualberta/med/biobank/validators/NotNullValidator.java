package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;

public class NotNullValidator extends AbstractValidator {

    public NotNullValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (value == null) {
            showDecoration();
            return ValidationStatus.error(errorMessage);
        }
        hideDecoration();
        return Status.OK_STATUS;
    }

}
