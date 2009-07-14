package edu.ualberta.med.biobank.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;

public class DoubleNumber extends AbstractValidator {
    
    private static final Pattern pattern = Pattern.compile(
            "^[0-9\\.\\+-]*$");
    
    public DoubleNumber(String message, ControlDecoration controlDecoration) {
        super(message, controlDecoration);
    }

    @Override
    public IStatus validate(Object value) {
        if (((String) value).length() == 0) {
            controlDecoration.hide();
            return Status.OK_STATUS;
        }

        Matcher m = pattern.matcher((String) value);
        if (m.matches()) {
            controlDecoration.hide();
            return Status.OK_STATUS;
        }
        
        controlDecoration.show();
        return ValidationStatus.error(message);
    }
}
