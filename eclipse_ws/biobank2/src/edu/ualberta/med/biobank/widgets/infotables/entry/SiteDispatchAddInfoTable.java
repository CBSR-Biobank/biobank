package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.SelectStudyDispatchSitesDialog;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.widgets.infotables.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableEvent;
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
        dlg.open();
        // SelectStudyDialog dlg;
        // try {
        // List<StudyWrapper> availableStudies = StudyWrapper
        // .getAllStudies(SessionManager.getAppService());
        // List<StudyWrapper> alreadyAddedStudies = site
        // .getStudyCollection(false);
        // if (alreadyAddedStudies != null) {
        // availableStudies.removeAll(alreadyAddedStudies);
        // }
        // dlg = new SelectStudyDialog(PlatformUI.getWorkbench()
        // .getActiveWorkbenchWindow().getShell(), availableStudies);
        // if (dlg.open() == Dialog.OK) {
        // notifyListeners();
        // StudyWrapper study = dlg.getSelection();
        // if (study != null) {
        // List<StudyWrapper> dummyList = new ArrayList<StudyWrapper>();
        // dummyList.add(study);
        // site.addStudies(dummyList);
        // setCollection(site.getStudyCollection(true));
        // }
        // }
        // } catch (Exception e) {
        // BioBankPlugin.openAsyncError(
        // "Unable to retrieve available contacts", e);
        // }
    }

    private void addDeleteSupport() {
        addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                createDispatchDialog();
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                // StudyWrapper study = getSelection();
                // if (study == null)
                // return;
                //
                // if (!BioBankPlugin.openConfirm(
                // "Remove Study",
                // "Are you sure you want to remove study \""
                // + study.getName() + "\"")) {
                // return;
                // }
                //
                // try {
                // site.removeStudies(Arrays.asList(study));
                // setCollection(site.getStudyCollection(true));
                // notifyListeners();
                // } catch (BiobankCheckException e) {
                // BioBankPlugin.openAsyncError("Delete failed", e);
                // }
            }
        });
    }

    public void setStudies(List<StudyWrapper> studies) {
        // setCollection(studies);
    }

    public void reload() {
        // setCollection(site.getStudyCollection(true));
    }

}
