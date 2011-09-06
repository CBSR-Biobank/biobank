package edu.ualberta.med.biobank.gui.common.validators;

import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class EmailValidator extends NonEmptyStringValidator {

    public static final String EMAIL_PATTERN = "^.*@.*\\..*";

    public EmailValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        IStatus status = super.validate(value);
        if (status.isOK())
            if (Pattern.matches(EMAIL_PATTERN, (String) value)) {
                return Status.OK_STATUS;
            } else {
                showDecoration();
                return ValidationStatus.error(errorMessage);
            }
        else
            return status;
    }
}
