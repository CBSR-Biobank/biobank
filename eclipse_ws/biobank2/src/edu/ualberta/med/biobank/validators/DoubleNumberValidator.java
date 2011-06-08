package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;

public class DoubleNumberValidator extends AbstractValidator {

    private static final Pattern pattern = Pattern.compile("^[0-9\\.\\+-]*$");

    private boolean allowEmpty = true;

    public DoubleNumberValidator(String message) {
        super(message);
    }

    public DoubleNumberValidator(String message, boolean allowEmpty) {
        this(message);
        this.allowEmpty = allowEmpty;
    }

    public void setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    @Override
    public IStatus validate(Object value) {
        if ((value == null) || (value instanceof Double)) {
            hideDecoration();
            return Status.OK_STATUS;
        }

        if (((String) value).length() == 0) {
            if (allowEmpty) {
                hideDecoration();
                return Status.OK_STATUS;
            } else {
                showDecoration();
                return ValidationStatus.error(errorMessage);
            }
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
