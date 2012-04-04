package edu.ualberta.med.biobank.validators;

import java.util.List;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.others.CheckNoDuplicateAction;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.AbstractValidator;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class InventoryIdValidator extends AbstractValidator {

    private boolean duplicate;
    private List<String> inventoryIdExcludeList;

    /**
     * Edited specimen. Null if new specimen.
     */
    private Specimen editedSpecimen;

    public InventoryIdValidator(List<String> inventoryIdExcludeList,
        String message, Specimen editedSpecimen) {
        super(message);
        this.inventoryIdExcludeList = inventoryIdExcludeList;
        this.editedSpecimen = editedSpecimen;
    }

    @Override
    public IStatus validate(final Object testedValue) {
        if ((testedValue != null) && !(testedValue instanceof String)) {
            throw new RuntimeException(
                "Not supposed to be called for non-strings.");
        }

        if (testedValue == null || (((String) testedValue).length() == 0)) {
            showDecoration();
            return ValidationStatus.error(errorMessage);
        }
        final String testedInventoryId = (String) testedValue;
        duplicate = false;

        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                try {
                    Boolean unique = SessionManager.getAppService().doAction(
                        new CheckNoDuplicateAction(Specimen.class,
                            editedSpecimen == null ? null : editedSpecimen
                                .getId(), SpecimenPeer.INVENTORY_ID.getName(),
                            testedInventoryId)).isTrue();
                    duplicate = !unique;
                    if (unique)
                        if (inventoryIdExcludeList.contains(testedInventoryId)) {
                            duplicate = true;
                        }
                } catch (ApplicationException e) {
                    BgcPlugin.openAsyncError(
                        "Error checking inventory id", e);
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
