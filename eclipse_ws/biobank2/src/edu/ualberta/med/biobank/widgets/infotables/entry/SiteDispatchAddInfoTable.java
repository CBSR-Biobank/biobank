package edu.ualberta.med.biobank.widgets.infotables.entry;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.SelectStudyDispatchSitesDialog;
import edu.ualberta.med.biobank.widgets.infotables.SiteDispatchInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

/**
 * Allows the user to select a study and dest sites for dispatch relations.
 */
public class SiteDispatchAddInfoTable extends SiteDispatchInfoTable {

    private SiteWrapper site;

    public SiteDispatchAddInfoTable(Composite parent, SiteWrapper site)
        throws ApplicationException {
        super(parent, site);
        this.site = site;
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void createDispatchDialog() {
        SelectStudyDispatchSitesDialog dlg = new SelectStudyDispatchSitesDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            site);
        dlg.open();
        try {
            loadStudyDestSites();
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("Error loading dialog", e);
        }
    }

}
