package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;

public class EmailAddressValidator extends AbstractValidator {

    private static final Pattern pattern = Pattern
        .compile("^([^@\\s]+)@((?:[-a-z0-9]+.)+[a-z]{2,})$"); 

    public EmailAddressValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        if (((String) value).length() == 0) {
            hideDecoration();
            return Status.OK_STATUS;
        }

        Matcher m = pattern.matcher((String) value);
        if (m.matches()) {
            hideDecoration();
            return Status.OK_STATUS;
        }

        showDecoration();
        return ValidationStatus.error(errorMessage);
    }
}
