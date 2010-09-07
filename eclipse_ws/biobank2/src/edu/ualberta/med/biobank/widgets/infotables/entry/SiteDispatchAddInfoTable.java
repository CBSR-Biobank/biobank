package edu.ualberta.med.biobank.widgets.infotables.entry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.SelectStudyDispatchSitesDialog;
import edu.ualberta.med.biobank.widgets.infotables.SiteDispatchInfoTable;

/**
 * Allows the user to select a study and dest sites for dispatch relations.
 */
public class SiteDispatchAddInfoTable extends SiteDispatchInfoTable {

    private SiteWrapper site;

    public SiteDispatchAddInfoTable(Composite parent, SiteWrapper site) {
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
        int res = dlg.open();
        if (res == Dialog.OK) {
            notifyListeners();
            reload();
        }
    }

}
