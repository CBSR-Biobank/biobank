package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.forms.linkassign.SpecimenAssignEntryForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.processing.SpecimenAssignAdapter;

public class SpecimenAssignHandler extends LinkAssignCommonHandler {
    private static final I18n i18n = I18nFactory.getI18n(SiteAddHandler.class);

    @SuppressWarnings("nls")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        if (!SpecimenLinkHandler.checkActivityLogSavePathValid()) {
            BgcPlugin
                .openAsyncError(
                    i18n.tr("Activity Log Location"),
                    i18n.tr("Invalid path selected. Cannot proceed with specimen assign."));
            return null;
        }

        openLinkAssignPerspective(SpecimenAssignEntryForm.ID,
            new SpecimenAssignAdapter(null, 0,
                // tooltip.
                i18n.tr("Specimen Assign"),
                false));
        return null;
    }
}
