package edu.ualberta.med.biobank.validators;

import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;

public class CabinetInventoryIDValidator extends AbstractValidator {

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z]{6}$"); //$NON-NLS-1$

    private static final Pattern PATTERN2 = Pattern.compile("^C[a-zA-Z]{4}$"); //$NON-NLS-1$

    private boolean manageOldInventoryIDs = false;

    public CabinetInventoryIDValidator() {
        super(Messages.CabinetInventoryIDValidator_error_msg);
    }

    @Override
    public IStatus validate(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException(
                Messages.CabinetInventoryIDValidator_nonstring_error_msg);
        }

        String v = (String) value;
        boolean matches = PATTERN.matcher(v).matches();
        if (manageOldInventoryIDs) {
            matches = matches || PATTERN2.matcher(v).matches();
        }
        if (matches) {
            hideDecoration();
            return Status.OK_STATUS;
        }
        showDecoration();
        return ValidationStatus.error(errorMessage);
    }

    public void setManageOldInventoryIDs(boolean manageOldInventoryIDs) {
        this.manageOldInventoryIDs = manageOldInventoryIDs;
    }
}
