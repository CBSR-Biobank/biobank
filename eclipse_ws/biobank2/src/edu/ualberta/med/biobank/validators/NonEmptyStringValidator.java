package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class NonEmptyStringValidator extends AbstractValidator {

    public NonEmptyStringValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        if (((String) value).length() != 0) {
            controlDecoration.hide();
            return Status.OK_STATUS;
        }

        controlDecoration.show();
        return ValidationStatus.error(errorMessage);
    }

}
