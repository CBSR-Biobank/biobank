package edu.ualberta.med.biobank.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

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
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

public class SelectStudyDispatchSitesDialog extends BiobankDialog {

    private static final String TITLE = "Site/Study dispatch relation";

    private SiteWrapper srcSite;

    private MultiSelectWidget siteMultiSelect;

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
                "Choose a study",
                StudyWrapper.getAllStudies(SessionManager.getAppService()),
                null, null);

        studyCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                StudyWrapper selectedStudy = (StudyWrapper) ((IStructuredSelection) studyCombo
                    .getSelection()).getFirstElement();
                setSitesSelection(selectedStudy);
            }
        });

        siteMultiSelect = new MultiSelectWidget(parent, SWT.NONE,
            "Selected Destination Sites", "Available Sites", 100);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        siteMultiSelect.setLayoutData(gd);
    }

    private void setSitesSelection(StudyWrapper study) {
        Collection<SiteWrapper> currentDestSites = srcSite
            .getStudyDispachSites(study);
        LinkedHashMap<Integer, String> availSites = new LinkedHashMap<Integer, String>();
        List<Integer> selectedSites = new ArrayList<Integer>();
        if (currentDestSites != null) {
            for (SiteWrapper site : currentDestSites) {
                selectedSites.add(site.getId());
            }
        }
        List<SiteWrapper> sites = null;
        try {
            sites = SiteWrapper.getSites(SessionManager.getAppService());
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error", e);
            return;
        }
        for (SiteWrapper site : sites) {
            availSites.put(site.getId(), site.getNameShort());
        }
        siteMultiSelect.setSelections(availSites, selectedSites);
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

}
