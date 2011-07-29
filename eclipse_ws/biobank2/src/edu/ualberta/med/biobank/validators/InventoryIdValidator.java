package edu.ualberta.med.biobank.validators;

import java.util.List;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.DuplicateEntryException;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;

public class InventoryIdValidator extends AbstractValidator {

    private boolean duplicate;
    private List<SpecimenWrapper> excludeList;

    /**
     * Edited specimen. Null if new specimen.
     */
    private SpecimenWrapper editedSpecimen;

    public InventoryIdValidator(List<SpecimenWrapper> excludeList,
        String message, SpecimenWrapper editedSpecimen) {
        super(message);
        this.excludeList = excludeList;
        this.editedSpecimen = editedSpecimen;
    }

    @Override
    public IStatus validate(Object value) {
        if ((value != null) && !(value instanceof String)) {
            throw new RuntimeException(
                Messages.InventoryIdValidator_nonstring_error_msg);
        }

        if (value == null || (((String) value).length() == 0)) {
            showDecoration();
            return ValidationStatus.error(errorMessage);
        }

        final SpecimenWrapper spc = new SpecimenWrapper(
            SessionManager.getAppService());
        spc.setInventoryId((String) value);
        if (editedSpecimen != null)
            // need to do that to know the object in database is not the same we
            // are editing
            spc.getWrappedObject().setId(editedSpecimen.getId());

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
                    BgcPlugin.openAsyncError(Messages.InventoryIdValidator_checking_error_msg,
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
