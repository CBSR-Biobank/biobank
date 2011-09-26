package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.helpers.SiteQuery;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.widgets.infotables.ContainerInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContainerTypeInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewStudyInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SiteViewForm extends AddressViewFormCommon {
    public static final String ID = "edu.ualberta.med.biobank.forms.SiteViewForm"; //$NON-NLS-1$

    private SiteAdapter siteAdapter;

    private SiteWrapper site;

    private NewStudyInfoTable studiesTable;
    private ContainerTypeInfoTable containerTypesTable;
    private ContainerInfoTable topContainersTable;

    private BgcBaseText nameLabel;

    private BgcBaseText nameShortLabel;

    private BgcBaseText studyCountLabel;

    private BgcBaseText containerTypeCountLabel;

    private BgcBaseText topContainerCountLabel;

    private BgcBaseText patientCountLabel;

    private BgcBaseText patientVisitCountLabel;

    private BgcBaseText specimenCountLabel;

    private BgcBaseText activityStatusLabel;

    private BgcBaseText commentLabel;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        site = (SiteWrapper) getModelObject();
        retrieveSite();
        setPartName(NLS.bind(Messages.SiteViewForm_title, site.getNameShort()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.SiteViewForm_title, site.getName()));
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
            Messages.label_name);
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_nameShort);
        studyCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SiteViewForm_field_studyCount_label);
        containerTypeCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.site_field_type_label);
        topContainerCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SiteViewForm_field_topLevelCount_label);
        patientCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SiteViewForm_field_patientCount_label);
        patientVisitCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SiteViewForm_field_pvCount_label);
        specimenCountLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SiteViewForm_field_totalSpecimen);
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_activity);
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.label_comments);
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
        setTextValue(patientCountLabel, SiteQuery.getPatientCount(site));
        setTextValue(patientVisitCountLabel, site.getCollectionEventCount());
        setTextValue(specimenCountLabel, site.getAliquotedSpecimenCount());
        setTextValue(activityStatusLabel, site.getActivityStatus().getName());
        setTextValue(commentLabel, site.getComment());
    }

    private void createStudySection() throws ApplicationException {
        Section section = createSection(Messages.SiteViewForm_studies_title);
        studiesTable = new NewStudyInfoTable(section, site);
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable.addClickListener(collectionDoubleClickListener);

        // TODO: provide way to edit study with this table
        // studiesTable.createDefaultEditItem();

        section.setClient(studiesTable);
    }

    private void createContainerTypesSection() {
        Section section = createSection(Messages.SiteViewForm_types_title);
        addSectionToolbar(section, Messages.SiteViewForm_type_add,
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
        Section section = createSection(Messages.SiteViewForm_topContainers_title);
        addSectionToolbar(section, Messages.SiteViewForm_topContainers_add,
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
        setPartName(NLS.bind(Messages.SiteViewForm_title, site.getNameShort()));
        form.setText(NLS.bind(Messages.SiteViewForm_title, site.getName()));
        setSiteSectionValues();
        setAddressValues(site);
        studiesTable.reload();
        containerTypesTable
            .setCollection(site.getContainerTypeCollection(true));
        topContainersTable.setCollection(site.getTopContainerCollection());
    }

    private void retrieveSite() {
        try {
            site.reload();
        } catch (Exception e) {
            BgcPlugin.openAsyncError(Messages.SiteViewForm_reload_error_msg, e);
        }
    }

}
