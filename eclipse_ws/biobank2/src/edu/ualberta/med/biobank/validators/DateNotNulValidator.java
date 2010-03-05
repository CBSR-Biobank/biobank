package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class DateNotNulValidator extends AbstractValidator {

    public DateNotNulValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (value == null) {
            showDecoration();
            return ValidationStatus.error(errorMessage);
        } else {
            hideDecoration();
            return Status.OK_STATUS;
        }
    }

}
