package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ContainerLabelValidator extends AbstractValidator {

    private static final Pattern pattern = Pattern
        .compile("^([\\w&&[^_]]{2}){2,}$");

    public ContainerLabelValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
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
