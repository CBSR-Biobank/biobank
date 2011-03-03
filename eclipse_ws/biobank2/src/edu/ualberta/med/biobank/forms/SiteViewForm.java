package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
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

    private BiobankText patientCountLabel;

    private BiobankText patientVisitCountLabel;

    private BiobankText specimenCountLabel;

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

        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.name"));
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.nameShort"));
        studyCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("SiteViewForm.field.studyCount.label"));
        containerTypeCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("site.field.type.label"));
        topContainerCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("SiteViewForm.field.topLevelCount.label"));
        patientCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("SiteViewForm.field.patientCount.label"));
        patientVisitCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("SiteViewForm.field.pvCount.label"));
        specimenCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("SiteViewForm.field.totalSpecimen"));
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.activity"));
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.getString("label.comments"));
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
        setTextValue(patientCountLabel, site.getPatientCount());
        setTextValue(patientVisitCountLabel, site.getCollectionEventCount());
        setTextValue(specimenCountLabel, site.getSpecimenCount());
        setTextValue(activityStatusLabel, site.getActivityStatus().getName());
        setTextValue(commentLabel, site.getComment());
    }

    private void createStudySection() {
        Section section = createSection("Studies");
        studiesTable = new StudyInfoTable(section, site.getStudyCollection());
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable.addClickListener(collectionDoubleClickListener);
        section.setClient(studiesTable);
    }

    private void createContainerTypesSection() {
        Section section = createSection(Messages
            .getString("SiteViewForm.types.title"));
        addSectionToolbar(section, Messages.getString("SiteViewForm.type.add"),
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    siteAdapter.getContainerTypesGroupNode().addContainerType(
                        siteAdapter, true);
                }
            }, ContainerTypeWrapper.class);

        containerTypesTable = new ContainerTypeInfoTable(section, siteAdapter);
        containerTypesTable.adaptToToolkit(toolkit, true);

        containerTypesTable.addClickListener(collectionDoubleClickListener);
        section.setClient(containerTypesTable);
    }

    private void createContainerSection() throws Exception {
        Section section = createSection(Messages
            .getString("SiteViewForm.topContainers.title"));
        addSectionToolbar(section,
            Messages.getString("SiteViewForm.topContainers.add"),
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    siteAdapter.getContainersGroupNode().addContainer(
                        siteAdapter, true);
                }
            }, ContainerWrapper.class);

        topContainersTable = new ContainerInfoTable(section, siteAdapter);
        topContainersTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(topContainersTable);

        topContainersTable.addClickListener(collectionDoubleClickListener);
        section.setClient(topContainersTable);
    }

    @Override
    public void reload() throws Exception {
        retrieveSite();
        setPartName(Messages.getString("SiteViewForm.title",
            site.getNameShort()));
        form.setText(Messages.getString("SiteViewForm.title", site.getName()));
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
            BiobankPlugin.openAsyncError("Can't reload site", e);
        }
    }

}
