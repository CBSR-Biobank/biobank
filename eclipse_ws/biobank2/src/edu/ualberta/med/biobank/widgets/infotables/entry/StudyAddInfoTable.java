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
                    List<StudyWrapper> dummyList =
                        new ArrayList<StudyWrapper>();
                    dummyList.add(study);
                    site.addToStudyCollection(dummyList);
                    setList(site.getStudyCollection(true));
                }
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                "Unable to retrieve available studies", e);
        }
    }

    private void addDeleteCreateSupport() {
        addAddItemListener(new IInfoTableAddItemListener<StudyWrapper>() {
            @Override
            public void addItem(InfoTableEvent<StudyWrapper> event) {
                createStudyDlg();
            }
        });

        addDeleteItemListener(new IInfoTableDeleteItemListener<StudyWrapper>() {
            @Override
            public void deleteItem(InfoTableEvent<StudyWrapper> event) {
                StudyWrapper study = getSelection();
                if (study == null)
                    return;

                if (!BgcPlugin.openConfirm(
                    "Remove Study", NLS.bind(
                        "Are you sure you want to remove study \"{0}\"?",
                        study.getName()))) {
                    return;
                }

                try {
                    site.removeFromStudyCollectionWithCheck(Arrays
                        .asList(study));
                    setList(site.getStudyCollection(true));
                    notifyListeners();
                } catch (BiobankCheckException e) {
                    BgcPlugin.openAsyncError(
                        "Delete failed", e);
                }
            }
        });
    }

    public void setStudies(List<StudyWrapper> studies) {
        setList(studies);
    }

    @Override
    public void reload() {
        setList(site.getStudyCollection(true));
    }

}
