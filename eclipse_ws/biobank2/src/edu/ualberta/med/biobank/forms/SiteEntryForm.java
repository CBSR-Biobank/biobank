package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.entry.SiteDispatchAddInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.StudyAddInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SiteEntryForm extends AddressEntryFormCommon {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SiteEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.SiteEntryForm";

    private static final String MSG_NEW_SITE_OK = "Create a new BioBank site.";
    private static final String MSG_SITE_OK = "Edit a BioBank site.";
    private static final String MSG_NO_SITE_NAME = "Site must have a name";

    private SiteAdapter siteAdapter;

    private SiteWrapper site;

    protected Combo session;

    private ComboViewer activityStatusComboViewer;

    private StudyAddInfoTable studiesTable;

    private SiteDispatchAddInfoTable dispatchTable;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        site = siteAdapter.getWrapper();
        try {
            site.reload();
        } catch (Exception e) {
            logger.error("Can't reload site", e);
        }

        String tabName;
        if (site.isNew()) {
            tabName = "New Repository Site";
            site.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else {
            tabName = "Repository Site " + site.getNameShort();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws ApplicationException {
        form.setText("Repository Site Information");
        page.setLayout(new GridLayout(1, false));
        // currentActivityStatus = site.getActivityStatus();
        createSiteSection();
        createAddressArea(site);
        createStudySection();
        createDispatchSection();

        // When adding help uncomment line below
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
        // IJavaHelpContextIds.XXXXX);
    }

    private void createSiteSection() throws ApplicationException {
        toolkit
            .createLabel(
                page,
                "Studies, Clinics, and Container Types can be added after submitting this information.",
                SWT.LEFT);

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client, BiobankText.class,
            SWT.NONE, "Name", null, site, "name", new NonEmptyStringValidator(
                MSG_NO_SITE_NAME)));

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            "Short Name", null, site, "nameShort", new NonEmptyStringValidator(
                "Site short name cannot be blank"));

        activityStatusComboViewer = createComboViewer(client,
            "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            site.getActivityStatus(), "Site must have an activity status",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    site.setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, site, "comment", null);
    }

    private void createStudySection() {
        Section section = createSection("Studies");
        addSectionToolbar(section, "Add Study", new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                studiesTable.createStudyDlg();
            }
        }, ContactWrapper.class);
        studiesTable = new StudyAddInfoTable(section, site);
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable.addDoubleClickListener(collectionDoubleClickListener);
        studiesTable.addSelectionChangedListener(listener);
        section.setClient(studiesTable);
    }

    private void createDispatchSection() {
        Section section = createSection("Dispatches");
        addSectionToolbar(section, "Add Dispatch Relation",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    dispatchTable.createDispatchDialog();
                }
            }, ContactWrapper.class);
        dispatchTable = new SiteDispatchAddInfoTable(section, site);
        dispatchTable.adaptToToolkit(toolkit, true);
        dispatchTable.addDoubleClickListener(collectionDoubleClickListener);
        dispatchTable.addSelectionChangedListener(listener);
        section.setClient(dispatchTable);
    }

    @Override
    protected String getOkMessage() {
        if (site.getId() == null) {
            return MSG_NEW_SITE_OK;
        }
        return MSG_SITE_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        final boolean newSite = site.isNew();
        if (siteAdapter.getParent() == null) {
            siteAdapter.setParent(SessionManager.getInstance().getSession());
        }
        site.persist();

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                SessionManager.getInstance().updateSites();
                if (newSite
                    && !SessionManager.getInstance().isAllSitesSelected()) {
                    SessionManager.getInstance().getSiteCombo()
                        .setSelection(site);
                }
            }
        });
    }

    @Override
    public String getNextOpenedFormID() {
        return SiteViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        ActivityStatusWrapper currentActivityStatus = site.getActivityStatus();
        if (currentActivityStatus != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                currentActivityStatus));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }
        studiesTable.reload();
        dispatchTable.reload();
        super.reset();
    }
}
