package edu.ualberta.med.biobank.validators;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;

public class TelephoneNumberValidator extends AbstractValidator {

    public TelephoneNumberValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        String v = (String) value;
        int len = v.length();
        int numDigits = 0;

        if (len == 0) {
            hideDecoration();
            return Status.OK_STATUS;
        }

        for (int i = 0; i < len; ++i) {
            if (Character.isDigit(v.charAt(i))) {
                ++numDigits;
            }
        }

        if (numDigits >= 10) {
            hideDecoration();
            return Status.OK_STATUS;
        }

        showDecoration();
        return ValidationStatus.error(errorMessage);
    }
}
