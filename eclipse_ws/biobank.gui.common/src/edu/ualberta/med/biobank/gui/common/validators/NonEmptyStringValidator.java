package edu.ualberta.med.biobank.gui.common.validators;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


public class NonEmptyStringValidator extends AbstractValidator {

    public NonEmptyStringValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (value != null && !(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        if (value != null && ((String) value).length() != 0) {
            hideDecoration();
            return Status.OK_STATUS;
        }
        showDecoration();
        return ValidationStatus.error(errorMessage);
    }

}
