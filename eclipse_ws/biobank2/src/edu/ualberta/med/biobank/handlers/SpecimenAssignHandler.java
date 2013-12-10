package edu.ualberta.med.biobank.handlers;

import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.forms.linkassign.SpecimenAssignEntryForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.treeview.processing.SpecimenAssignAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenAssignHandler extends LinkAssignCommonHandler {
    private static final I18n i18n = I18nFactory.getI18n(SiteAddHandler.class);

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (!SpecimenLinkHandler.checkActivityLogSavePathValid()) {
            BgcPlugin.openAsyncError(
                i18n.tr("Activity Log Location"),
                i18n.tr("Invalid path selected. Cannot proceed with specimen assign."));
            return null;
        }

        try {
            Set<ContainerType> ctypes = SpecimenAssignEntryForm.getPalletContainerTypes();

            if (ctypes.isEmpty()) {
                BgcPlugin.openAsyncError(
                    // TR: dialog title
                    i18n.tr("Container Type Error"),
                    // TR: dialog message
                    i18n.tr("No child container types found with valid dimensions for scanning and decoding."));
            } else {
                openLinkAssignPerspective(SpecimenAssignEntryForm.ID, new SpecimenAssignAdapter(
                    null,
                    0,
                    // tooltip.
                    i18n.tr("Specimen Assign"),
                    false));
            }
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // dialog title
                i18n.tr("Unable to query container types"),
                e.getMessage());
        }
        return null;
    }
}
