package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.dialogs.select.SelectStudyDispatchSitesDialog;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.SiteDispatchInfoTable;

/**
 * Allows the user to select a study and dest sites for dispatch relations.
 */
public class SiteDispatchAddInfoTable extends SiteDispatchInfoTable {

    private SiteWrapper site;

    public SiteDispatchAddInfoTable(Composite parent, SiteWrapper site) {
        super(parent, site);
        this.site = site;
        addDeleteSupport();
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

    private void addDeleteSupport() {
        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                StudySiteDispatch ssd = getSelection();
                if (ssd != null) {
                    if (!BioBankPlugin.openConfirm(
                        "Delete Dispatch to this site",
                        "Are you sure you want to delete this dispatch configuration to \""
                            + ssd.destSite.getNameShort() + "\" ?")) {
                        return;
                    }
                    site.removeStudyDispatchSites(ssd.study,
                        Arrays.asList(ssd.destSite));
                    notifyListeners();
                    reload();
                }
            }
        });
    }
}
