package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;

public class PostalCodeValidator extends AbstractValidator {

    private static final Pattern pattern = Pattern
        .compile("^[abceghjklmnprstvxyABCEGHJKLMNPRSTVXY]\\d[a-zA-Z]-?\\d[a-zA-Z]\\d$"); //$NON-NLS-1$

    public PostalCodeValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException(
                Messages.PostalCodeValidator_nonstring_error_msg);
        }

        String v = (String) value;

        if (v.length() == 0) {
            hideDecoration();
            return Status.OK_STATUS;
        }

        Matcher m = pattern.matcher(v);
        if (m.matches()) {
            hideDecoration();
            return Status.OK_STATUS;
        }

        showDecoration();
        return ValidationStatus.error(errorMessage);
    }
}
