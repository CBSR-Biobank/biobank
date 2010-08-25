package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.ContainerGroup;
import edu.ualberta.med.biobank.treeview.ContainerTypeGroup;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.ContainerInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContainerTypeInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.StudyInfoTable;

public class SiteViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.SiteViewForm";

    private SiteAdapter siteAdapter;

    private SiteWrapper site;

    private StudyInfoTable studiesTable;
    private ContainerTypeInfoTable containerTypesTable;
    private ContainerInfoTable topContainersTable;

    private BiobankText nameLabel;

    private BiobankText nameShortLabel;

    private BiobankText studyCountLabel;

    private BiobankText containerTypeCountLabel;

    private BiobankText topContainerCountLabel;

    private BiobankText shipmentCountLabel;

    private BiobankText patientCountLabel;

    private BiobankText patientVisitCountLabel;

    private BiobankText sampleCountLabel;

    private BiobankText activityStatusLabel;

    private BiobankText commentLabel;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        site = siteAdapter.getWrapper();
        retrieveSite();
        setPartName("Repository Site " + site.getNameShort());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Repository Site: " + site.getName());
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_SITE));

        createSiteSection();
        createAddressSection(site);
        createStudySection();
        createContainerTypesSection();
        createContainerSection();
    }

    private void createSiteSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel = createReadOnlyLabelledField(client, SWT.NONE, "Name");
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Short Name");
        studyCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Total Studies");
        containerTypeCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Container Types");
        topContainerCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Top Level Containers");
        shipmentCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Total Shipments");
        patientCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Total Patients");
        patientVisitCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Total Patient Visits");
        sampleCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Total Samples");
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Activity Status");
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            "Comments");
        setSiteSectionValues();
    }

    private void setSiteSectionValues() throws Exception {
        setTextValue(nameLabel, site.getName());
        setTextValue(nameShortLabel, site.getNameShort());
        setTextValue(studyCountLabel, site.getStudyCollection().size());
        setTextValue(containerTypeCountLabel, site.getContainerTypeCollection()
            .size());
        setTextValue(topContainerCountLabel, site.getTopContainerCollection()
            .size());
        setTextValue(shipmentCountLabel, site.getShipmentCount());
        setTextValue(patientCountLabel, site.getPatientCount());
        setTextValue(patientVisitCountLabel, site.getPatientVisitCount());
        setTextValue(sampleCountLabel, site.getAliquotCount());
        setTextValue(activityStatusLabel, site.getActivityStatus().getName());
        setTextValue(commentLabel, site.getComment());
    }

    private void createStudySection() {
        Section section = createSection("Studies");
        studiesTable = new StudyInfoTable(section, site.getStudyCollection());
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable.addDoubleClickListener(collectionDoubleClickListener);
        section.setClient(studiesTable);
    }

    private void createContainerTypesSection() {
        Section section = createSection("Container Types");
        addSectionToolbar(section, "Add Container Type",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    ContainerTypeGroup.addContainerType(siteAdapter, true);
                }
            }, ContainerTypeWrapper.class);

        containerTypesTable = new ContainerTypeInfoTable(section,
            site.getContainerTypeCollection());
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
                ContainerGroup.addContainer(siteAdapter, true);
            }
        }, ContainerWrapper.class);

        topContainersTable = new ContainerInfoTable(section, siteAdapter
            .getWrapper().getTopContainerCollection());
        topContainersTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(topContainersTable);

        topContainersTable
            .addDoubleClickListener(collectionDoubleClickListener);
        section.setClient(topContainersTable);
    }

    @Override
    public void reload() throws Exception {
        retrieveSite();
        setPartName("Repository Site " + site.getNameShort());
        form.setText("Repository Site: " + site.getName());
        setSiteSectionValues();
        setAdressValues(site);
        studiesTable.setCollection(site.getStudyCollection());
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

}
