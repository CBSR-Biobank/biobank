package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.infotables.ClinicInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContainerInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContainerTypeInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.StudyInfoTable;

public class SiteViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.SiteViewForm";

    private SiteAdapter siteAdapter;

    private SiteWrapper site;

    private StudyInfoTable studiesTable;
    private ClinicInfoTable clinicsTable;
    private ContainerTypeInfoTable containerTypesTable;
    private ContainerInfoTable sContainersTable;

    private Text clinicCountLabel;

    private Text studyCountLabel;

    private Text containerTypeCountLabel;

    private Text topContainerCountLabel;

    private Text patientCountLabel;

    private Text patientVisitCountLabel;

    private Text sampleCountLabel;

    private Text activityStatusLabel;

    private Text commentLabel;

    private SelectionListener addStudySelectionListener = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            AdapterBase studiesNode = siteAdapter.getStudiesGroupNode();
            StudyAdapter studyAdapter = new StudyAdapter(studiesNode,
                new StudyWrapper(siteAdapter.getAppService()));
            AdapterBase
                .openForm(new FormInput(studyAdapter), StudyEntryForm.ID);
        }
    };

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        site = siteAdapter.getWrapper();
        retrieveSite();
        setPartName("Repository Site " + site.getName());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Repository Site: " + site.getName());
        form.getBody().setLayout(new GridLayout(1, false));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_SITE));

        createSiteSection();
        createAddressSection(site);
        createStudySection();
        createClinicSection();
        createContainerTypesSection();
        createContainerSection();
    }

    private void createSiteSection() throws Exception {
        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        clinicCountLabel = createReadOnlyField(client, SWT.NONE,
            "Total Clinics");
        studyCountLabel = createReadOnlyField(client, SWT.NONE, "Total Studies");
        containerTypeCountLabel = createReadOnlyField(client, SWT.NONE,
            "Container Types");
        topContainerCountLabel = createReadOnlyField(client, SWT.NONE,
            "Top Level Containers");
        patientCountLabel = createReadOnlyField(client, SWT.NONE,
            "Total Patients");
        patientVisitCountLabel = createReadOnlyField(client, SWT.NONE,
            "Total Patient Visits");
        sampleCountLabel = createReadOnlyField(client, SWT.NONE,
            "Total Samples");
        activityStatusLabel = createReadOnlyField(client, SWT.NONE,
            "Activity Status");
        commentLabel = createReadOnlyField(client, SWT.NONE, "Comments");
        setSiteSectionValues();
    }

    private void setSiteSectionValues() throws Exception {
        setTextValue(clinicCountLabel, site.getClinicCollection().size());
        setTextValue(studyCountLabel, site.getStudyCollection().size());
        setTextValue(containerTypeCountLabel, site.getContainerTypeCollection()
            .size());
        setTextValue(topContainerCountLabel, site.getTopContainerCollection()
            .size());
        setTextValue(patientCountLabel, site.getPatientCount());
        setTextValue(patientVisitCountLabel, site.getPatientVisitCount());
        setTextValue(sampleCountLabel, site.getSampleCount());
        setTextValue(activityStatusLabel, site.getActivityStatus());
        setTextValue(commentLabel, site.getComment());
    }

    private void createStudySection() {
        Section section = createSection("Studies");
        Composite client = sectionAddClient(section);

        ToolBar tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
        ToolItem titem = new ToolItem(tbar, SWT.NULL);
        titem.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_ADD));
        titem.setToolTipText("Add Study");
        titem.addSelectionListener(addStudySelectionListener);
        section.setTextClient(tbar);

        studiesTable = new StudyInfoTable(client, site.getStudyCollection());
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable.addDoubleClickListener(collectionDoubleClickListener);
    }

    public void createClinicSection() {
        Collection<ClinicWrapper> clinics = site.getClinicCollection(true);
        Section section = toolkit.createSection(form.getBody(), Section.TWISTIE
            | Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Clinics");
        section.setLayout(new GridLayout(1, false));
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        clinicsTable = new ClinicInfoTable(section, clinics);
        section.setClient(clinicsTable);
        clinicsTable.adaptToToolkit(toolkit, true);
        clinicsTable.addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createContainerTypesSection() {
        Composite client = createSectionWithClient("Container Types");

        containerTypesTable = new ContainerTypeInfoTable(client, site
            .getContainerTypeCollection());
        containerTypesTable.adaptToToolkit(toolkit, true);

        containerTypesTable
            .addDoubleClickListener(collectionDoubleClickListener);
    }

    private void createContainerSection() throws Exception {
        Section section = createSection("Top Level Containers");
        sContainersTable = new ContainerInfoTable(section, siteAdapter
            .getWrapper().getTopContainerCollection());
        section.setClient(sContainersTable);
        sContainersTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sContainersTable);

        sContainersTable.addDoubleClickListener(collectionDoubleClickListener);
    }

    @Override
    protected void reload() throws Exception {
        retrieveSite();
        setPartName("Repository Site " + site.getName());
        form.setText("Repository Site: " + site.getName());
        setSiteSectionValues();
        setAdressValues(site);
        studiesTable.setCollection(site.getStudyCollection());
        clinicsTable.setCollection(site.getClinicCollection(true));
        containerTypesTable
            .setCollection(site.getContainerTypeCollection(true));
        sContainersTable.setCollection(site.getContainerCollection());
    }

    private void retrieveSite() {
        try {
            site.reload();
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Can't reload site", e);
        }
    }

    @Override
    protected String getEntryFormId() {
        return SiteEntryForm.ID;
    }
}
