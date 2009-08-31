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
            controlDecoration.show();
            return ValidationStatus.error(errorMessage);
        } else {
            controlDecoration.hide();
            return Status.OK_STATUS;
        }
    }

}
