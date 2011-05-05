package edu.ualberta.med.biobank.validators;

import java.util.List;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.DuplicateEntryException;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class InventoryIdValidator extends AbstractValidator {

    private boolean duplicate;
    private List<SpecimenWrapper> excludeList;

    public InventoryIdValidator(List<SpecimenWrapper> excludeList,
        String message) {
        super(message);
        this.excludeList = excludeList;
    }

    @Override
    public IStatus validate(Object value) {
        if ((value != null) && !(value instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        if (value == null || (((String) value).length() == 0)) {
            showDecoration();
            return ValidationStatus.error(errorMessage);
        }

        final SpecimenWrapper spc = new SpecimenWrapper(
            SessionManager.getAppService());
        spc.setInventoryId((String) value);

        duplicate = false;

        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                try {
                    spc.checkInventoryIdUnique();
                    if (excludeList.contains(spc)) {
                        duplicate = true;
                    }
                } catch (DuplicateEntryException e) {
                    duplicate = true;
                } catch (Exception e) {
                    BiobankPlugin.openAsyncError("Error checking inventory id",
                        e);
                }
            }
        });

        if (duplicate) {
            showDecoration();
            return ValidationStatus.error(errorMessage);
        }

        hideDecoration();
        return Status.OK_STATUS;
    }
}
