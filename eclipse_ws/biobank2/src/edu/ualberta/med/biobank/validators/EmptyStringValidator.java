package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class EmptyStringValidator extends AbstractValidator {
    public EmptyStringValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if ((value != null) && !(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        if ((value == null) || ((String) value).isEmpty()) {
            hideDecoration();
            return Status.OK_STATUS;
        }
        showDecoration();
        return ValidationStatus.error(errorMessage);
    }
}
