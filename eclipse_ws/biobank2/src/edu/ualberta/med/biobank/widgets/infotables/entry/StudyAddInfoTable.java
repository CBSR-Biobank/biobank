package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.select.SelectStudyDialog;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableAddItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.widgets.infotables.StudyInfoTable;

/**
 * Allows the user to select a clinic and a contact from a clinic. Note that
 * some clinics may have more than one contact.
 */
public class StudyAddInfoTable extends StudyInfoTable {

    private SiteWrapper site;

    public StudyAddInfoTable(Composite parent, SiteWrapper site,
        boolean createDeleteSupport) {
        super(parent, site.getStudyCollection(true));
        this.site = site;
        if (createDeleteSupport)
            addDeleteCreateSupport();
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void createStudyDlg() {
        SelectStudyDialog dlg;
        try {
            List<StudyWrapper> availableStudies = StudyWrapper
                .getAllStudies(SessionManager.getAppService());
            List<StudyWrapper> alreadyAddedStudies = site
                .getStudyCollection(false);
            if (alreadyAddedStudies != null) {
                availableStudies.removeAll(alreadyAddedStudies);
            }
            dlg = new SelectStudyDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), availableStudies);
            if (dlg.open() == Dialog.OK) {
                notifyListeners();
                StudyWrapper study = dlg.getSelection();
                if (study != null) {
                    List<StudyWrapper> dummyList = new ArrayList<StudyWrapper>();
                    dummyList.add(study);
                    site.addToStudyCollection(dummyList);
                    setCollection(site.getStudyCollection(true));
                }
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.StudyAddInfoTable_retrieve_error_title, e);
        }
    }

    private void addDeleteCreateSupport() {
        addAddItemListener(new IInfoTableAddItemListener() {
            @Override
            public void addItem(InfoTableEvent event) {
                createStudyDlg();
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                StudyWrapper study = getSelection();
                if (study == null)
                    return;

                if (!BgcPlugin.openConfirm(
                    Messages.StudyAddInfoTable_remove_confirm_title, NLS.bind(
                        Messages.StudyAddInfoTable_remove_confirm_msg,
                        study.getName()))) {
                    return;
                }

                try {
                    site.removeFromStudyCollectionWithCheck(Arrays
                        .asList(study));
                    setCollection(site.getStudyCollection(true));
                    notifyListeners();
                } catch (BiobankCheckException e) {
                    BgcPlugin.openAsyncError(
                        Messages.StudyAddInfoTable_delete_error_title, e);
                }
            }
        });
    }

    public void setStudies(List<StudyWrapper> studies) {
        setCollection(studies);
    }

    public void reload() {
        setCollection(site.getStudyCollection(true));
    }

}
