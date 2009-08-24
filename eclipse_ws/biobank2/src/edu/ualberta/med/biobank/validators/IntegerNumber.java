package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class IntegerNumber extends AbstractValidator {

    private static final Pattern pattern = Pattern.compile("^[0-9\\+-]*$");

    private boolean allowEmpty = true;

    public IntegerNumber(String message) {
        super(message);
    }

    public IntegerNumber(String message, boolean allowEmpty) {
        this(message);
        this.allowEmpty = allowEmpty;
    }

    @Override
    public IStatus validate(Object value) {
        if ((value == null) || (value instanceof Integer)) {
            controlDecoration.hide();
            return Status.OK_STATUS;
        }

        if (((String) value).length() == 0) {
            if (allowEmpty) {
                controlDecoration.hide();
                return Status.OK_STATUS;
            } else {
                controlDecoration.show();
                return ValidationStatus.error(errorMessage);
            }
        }

        Matcher m = pattern.matcher((String) value);
        if (m.matches()) {
            controlDecoration.hide();
            return Status.OK_STATUS;
        }

        controlDecoration.show();
        return ValidationStatus.error(errorMessage);
    }

}
