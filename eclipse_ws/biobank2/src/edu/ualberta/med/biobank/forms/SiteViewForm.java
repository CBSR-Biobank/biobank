package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.ClinicGroup;
import edu.ualberta.med.biobank.treeview.ContainerGroup;
import edu.ualberta.med.biobank.treeview.ContainerTypeGroup;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyGroup;
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
    private ContainerInfoTable topContainersTable;

    private Text clinicCountLabel;

    private Text studyCountLabel;

    private Text containerTypeCountLabel;

    private Text topContainerCountLabel;

    private Text patientCountLabel;

    private Text patientVisitCountLabel;

    private Text sampleCountLabel;

    private Text activityStatusLabel;

    private Text commentLabel;

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
        addSectionToolbar(section, "Add Study", new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StudyGroup.addStudy(siteAdapter);
            }
        });

        studiesTable = new StudyInfoTable(section, site.getStudyCollection());
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable.addDoubleClickListener(collectionDoubleClickListener);
        section.setClient(studiesTable);
    }

    public void createClinicSection() {
        Section section = createSection("Clinics");
        addSectionToolbar(section, "Add Clinic", new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ClinicGroup.addClinic(siteAdapter);
            }
        });

        Collection<ClinicWrapper> clinics = site.getClinicCollection(true);
        clinicsTable = new ClinicInfoTable(section, clinics);
        clinicsTable.adaptToToolkit(toolkit, true);
        clinicsTable.addDoubleClickListener(collectionDoubleClickListener);
        section.setClient(clinicsTable);
    }

    private void createContainerTypesSection() {
        Section section = createSection("Container Types");
        addSectionToolbar(section, "Add Container Type",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    ContainerTypeGroup.addContainerType(siteAdapter);
                }
            });

        containerTypesTable = new ContainerTypeInfoTable(section, site
            .getContainerTypeCollection());
        containerTypesTable.adaptToToolkit(toolkit, true);

        containerTypesTable
            .addDoubleClickListener(collectionDoubleClickListener);
        section.setClient(containerTypesTable);
    }

    private void createContainerSection() throws Exception {
        Section section = createSection("Top Level Containers");
        addSectionToolbar(section, "Add Container", new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ContainerGroup.addContainer(siteAdapter);
            }
        });

        topContainersTable = new ContainerInfoTable(section, siteAdapter
            .getWrapper().getTopContainerCollection());
        topContainersTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(topContainersTable);

        topContainersTable
            .addDoubleClickListener(collectionDoubleClickListener);
        section.setClient(topContainersTable);
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
        topContainersTable.setCollection(site.getTopContainerCollection());
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
