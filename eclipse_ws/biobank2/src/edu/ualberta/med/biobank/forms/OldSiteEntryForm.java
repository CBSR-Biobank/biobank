package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.SitePeer;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.widgets.infotables.entry.StudyAddInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class OldSiteEntryForm extends AddressEntryFormCommon {

    public static final String ID = "edu.ualberta.med.biobank.forms.SiteEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_SITE_OK = Messages.SiteEntryForm_creation_msg;
    private static final String MSG_SITE_OK = Messages.SiteEntryForm_edition_msg;

    private SiteAdapter siteAdapter;

    private SiteWrapper site;

    protected Combo session;

    private ComboViewer activityStatusComboViewer;

    private StudyAddInfoTable studiesTable;

    private BgcEntryFormWidgetListener listener = new BgcEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        site = (SiteWrapper) getModelObject();

        String tabName;
        if (site.isNew()) {
            tabName = Messages.SiteEntryForm_title_new;
            site.setActivityStatus(ActivityStatus.ACTIVE);
        } else {
            tabName = NLS.bind(Messages.SiteEntryForm_title_edit,
                site.getNameShort());
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws ApplicationException {
        form.setText(Messages.SiteEntryForm_main_title);
        page.setLayout(new GridLayout(1, false));
        createSiteSection();
        createAddressArea(site);
        createStudySection();

        // When adding help uncomment line below
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
        // IJavaHelpContextIds.XXXXX);
    }

    private void createSiteSection() throws ApplicationException {
        toolkit.createLabel(page, Messages.SiteEntryForm_main_description,
            SWT.LEFT);

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.NONE, Messages.label_name, null, site, SitePeer.NAME.getName(),
            new NonEmptyStringValidator(
                Messages.SiteEntryForm_field_name_validation_msg)));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.label_nameShort, null, site,
            SitePeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
                Messages.SiteEntryForm_field_nameShort_validation_msg));

        activityStatusComboViewer = createComboViewer(client,
            Messages.label_activity,
            ActivityStatus.valuesList(), site.getActivityStatus(),
            Messages.SiteEntryForm_field_activity_validation_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    site.setActivityStatus((ActivityStatus) selectedObject);
                }
            });

    }

    private void createStudySection() {
        Section section = createSection(Messages.SiteEntryForm_studies_title);
        boolean superAdmin = SessionManager.getUser().isSuperAdmin();
        if (superAdmin) {
            addSectionToolbar(section, Messages.SiteEntryForm_studies_add,
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        studiesTable.createStudyDlg();
                    }
                }, ContactWrapper.class);
        }
        studiesTable = new StudyAddInfoTable(section, site, superAdmin);
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable.addClickListener(collectionDoubleClickListener);
        // TODO: the new style info table needs to support editing of items
        // via the context menu
        // studiesTable.createDefaultEditItem();
        studiesTable.addSelectionChangedListener(listener);
        section.setClient(studiesTable);
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
        site.persist();
        siteAdapter.getParent().performExpand();
        SessionManager.getUser().updateCurrentCenter(site);
    }

    @Override
    public String getNextOpenedFormID() {
        return SiteViewForm.ID;
    }

    @Override
    protected void onReset() throws Exception {
        site.reset();

        if (site.isNew()) {
            site.setActivityStatus(ActivityStatus.ACTIVE);
        }

        GuiUtil.reset(activityStatusComboViewer, site.getActivityStatus());

        studiesTable.reload();
    }
}
