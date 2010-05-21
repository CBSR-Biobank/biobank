package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class PalletBarCodeValidator extends AbstractValidator {

    private static final Pattern PATTERN = Pattern.compile("^([A-Z0-9]{2})+");

    public PalletBarCodeValidator(String message) {
        super(message);
    }

    @Override
    public IStatus validate(Object value) {
        if (value != null && !(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        if (value != null) {
            String v = (String) value;
            Matcher m = PATTERN.matcher(v);
            if (m.matches()) {
                hideDecoration();
                return Status.OK_STATUS;
            }
        }
        showDecoration();
        return ValidationStatus.error(errorMessage);
    }

}
