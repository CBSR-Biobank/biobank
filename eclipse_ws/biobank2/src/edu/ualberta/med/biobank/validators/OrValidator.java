package edu.ualberta.med.biobank.validators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;

public class OrValidator extends AbstractValidator {
    List<AbstractValidator> validators;

    public OrValidator(List<AbstractValidator> validators, String message) {
        super(message);
        this.validators = new ArrayList<AbstractValidator>(validators);
    }

    @Override
    public IStatus validate(Object value) {
        for (AbstractValidator validator : this.validators) {
            if (validator.validate(value).isOK()) {
                hideDecoration();
                return Status.OK_STATUS;
            }
        }

        showDecoration();
        return ValidationStatus.error(errorMessage);
    }

}
