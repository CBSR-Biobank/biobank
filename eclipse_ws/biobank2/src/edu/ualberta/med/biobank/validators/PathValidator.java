package edu.ualberta.med.biobank.validators;

import java.io.File;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class PathValidator extends AbstractValidator {

    public PathValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (value != null && !(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }
        if (value != null && ((String) value).length() != 0
            && new File((String) value).exists()) {
            hideDecoration();
            return Status.OK_STATUS;
        }
        showDecoration();
        return ValidationStatus.error(errorMessage);
    }

}