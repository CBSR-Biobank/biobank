package edu.ualberta.med.biobank.gui.common.validators;

import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Value can be empty but if is not empty, should be something like 'ddd@dd.dd'
 * 
 * @author delphine
 */
public class EmailValidator extends AbstractValidator {

    public static final String EMAIL_PATTERN = ".+@.+\\..+"; //$NON-NLS-1$

    public EmailValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if ((value != null) && !(value instanceof String))
            throw new RuntimeException(
                Messages.NonEmptyStringValidator_non_string_error);
        String email = (String) value;
        if ((value == null) || (email.length() == 0)
            || Pattern.matches(EMAIL_PATTERN, email)) {
            hideDecoration();
            return Status.OK_STATUS;
        }
        showDecoration();
        return ValidationStatus.error(errorMessage);
    }
}
