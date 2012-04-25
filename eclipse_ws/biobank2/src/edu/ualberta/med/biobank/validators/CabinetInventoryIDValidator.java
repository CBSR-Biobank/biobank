package edu.ualberta.med.biobank.validators;

import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;

public class CabinetInventoryIDValidator extends AbstractValidator {
    private static final I18n i18n = I18nFactory
        .getI18n(CabinetInventoryIDValidator.class);

    @SuppressWarnings("nls")
    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z]{6}$");

    @SuppressWarnings("nls")
    private static final Pattern PATTERN2 = Pattern.compile("^C[a-zA-Z]{4}$");

    private boolean manageOldInventoryIDs = false;

    @SuppressWarnings("nls")
    public CabinetInventoryIDValidator() {
        super(
            // validation error message.
            i18n.tr("Enter Inventory ID (6 letters for new samples, 4 allowed for old samples)"));
    }

    @SuppressWarnings("nls")
    @Override
    public IStatus validate(Object value) {
        if (!(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
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
