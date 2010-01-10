package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class CabinetInventoryIDValidator extends AbstractValidator {

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z]{6}$");

    private static final Pattern PATTERN2 = Pattern.compile("^C[a-zA-Z]{4}$");

    public CabinetInventoryIDValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        String v = (String) value;
        Matcher m = PATTERN.matcher(v);
        Matcher m2 = PATTERN2.matcher(v);
        if (m.matches() || m2.matches()) {
            controlDecoration.hide();
            return Status.OK_STATUS;
        }
        controlDecoration.show();
        return ValidationStatus.error(errorMessage);
    }

}
