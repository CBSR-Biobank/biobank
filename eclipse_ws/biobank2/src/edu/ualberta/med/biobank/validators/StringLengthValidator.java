package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;

/**
 * This validator will evaluate to false if the string is less than the
 * specified length.
 * 
 */
public class StringLengthValidator extends AbstractValidator {

    private int length;

    public StringLengthValidator(int length, String message) {
        super(message);
        this.length = length;
    }

    @Override
    public IStatus validate(Object value) {
        if ((value != null) && !(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        if ((value != null) && ((String) value).length() >= length) {
            hideDecoration();
            return Status.OK_STATUS;
        }
        showDecoration();
        return ValidationStatus.error(errorMessage);
    }

}
