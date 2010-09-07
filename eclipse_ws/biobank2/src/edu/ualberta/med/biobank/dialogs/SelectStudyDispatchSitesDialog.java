package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

public class SelectStudyDispatchSitesDialog extends BiobankDialog {

    private static final String TITLE = "Site/Study dispatch relation";

    private SiteWrapper srcSite;

    private MultiSelectWidget siteMultiSelect;

    private List<SiteWrapper> currentAllSitesForStudy;

    private Map<Integer, StudySites> studiesDispatchRelations = new HashMap<Integer, SelectStudyDispatchSitesDialog.StudySites>();

    private StudyWrapper currentStudy;

    private class StudySites {
        public StudyWrapper study;
        public List<SiteWrapper> addedSites = new ArrayList<SiteWrapper>();
        public List<SiteWrapper> removedSites = new ArrayList<SiteWrapper>();
    }

    public SelectStudyDispatchSitesDialog(Shell parentShell, SiteWrapper srcSite) {
        super(parentShell);
        this.srcSite = srcSite;
    }

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final ComboViewer studyCombo = getWidgetCreator()
            .createComboViewerWithNoSelectionValidator(contents,
                "Choose a study", srcSite.getStudyCollection(), null, null);

        studyCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                StudyWrapper study = (StudyWrapper) ((IStructuredSelection) studyCombo
                    .getSelection()).getFirstElement();
                setSitesSelection(study);
            }
        });

        siteMultiSelect = new MultiSelectWidget(parent, SWT.NONE,
            "Selected Destination Sites", "Available Sites", 100);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        siteMultiSelect.setLayoutData(gd);
        siteMultiSelect
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    List<SiteWrapper> addedSites = new ArrayList<SiteWrapper>();
                    List<SiteWrapper> removedSites = new ArrayList<SiteWrapper>();
                    List<Integer> addedSitesIds = siteMultiSelect
                        .getAddedToSelection();
                    List<Integer> removedSitesIds = siteMultiSelect
                        .getRemovedToSelection();
                    for (SiteWrapper site : currentAllSitesForStudy) {
                        if (addedSitesIds.contains(site.getId()))
                            addedSites.add(site);
                        if (removedSitesIds.contains(site.getId()))
                            removedSites.add(site);
                    }
                    StudySites ss = studiesDispatchRelations.get(currentStudy
                        .getId());
                    ss.addedSites.addAll(addedSites);
                    ss.removedSites.removeAll(removedSites);
                    ss.removedSites.addAll(removedSites);
                    ss.removedSites.removeAll(addedSites);
                }
            });
    }

    private void setSitesSelection(StudyWrapper study) {
        currentStudy = study;
        StudySites ss = studiesDispatchRelations.get(currentStudy.getId());
        if (ss == null) {
            ss = new StudySites();
            ss.study = study;
            studiesDispatchRelations.put(study.getId(), ss);
        }

        Collection<SiteWrapper> currentDestSites = srcSite
            .getStudyDispachSites(study);
        LinkedHashMap<Integer, String> availableSites = new LinkedHashMap<Integer, String>();
        List<Integer> selectedSites = new ArrayList<Integer>();
        if (currentDestSites != null) {
            for (SiteWrapper site : currentDestSites) {
                selectedSites.add(site.getId());
            }
        }
        for (SiteWrapper site : ss.addedSites) {
            selectedSites.add(site.getId());
        }
        try {
            currentAllSitesForStudy = new ArrayList<SiteWrapper>(
                study.getSiteCollection());
            currentAllSitesForStudy.remove(srcSite);
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error", e);
            return;
        }
        for (SiteWrapper site : currentAllSitesForStudy) {
            availableSites.put(site.getId(), site.getNameShort());
        }
        siteMultiSelect.setSelections(availableSites, selectedSites);
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Select a study and then choose which site can receive aliquots from this "
            + srcSite.getNameShort();
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected void okPressed() {
        for (StudySites ss : studiesDispatchRelations.values()) {
            try {
                srcSite.addStudyDispatchSites(ss.study, ss.addedSites);
            } catch (BiobankCheckException e) {
                BioBankPlugin.openAsyncError("Error adding dispatch relation",
                    e);
            }
            srcSite.removeStudyDispatchSites(ss.study, ss.removedSites);
        }
        super.okPressed();
    }

}
