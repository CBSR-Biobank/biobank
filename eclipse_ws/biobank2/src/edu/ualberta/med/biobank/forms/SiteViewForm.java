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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContainerInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContainerTypeInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewStudyInfoTable;

public class SiteViewForm extends AddressViewFormCommon {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.SiteViewForm"; //$NON-NLS-1$

    private SiteAdapter siteAdapter;

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

    private SiteInfo siteInfo;

    private SiteWrapper site;

    private CommentCollectionInfoTable commentTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        updateSiteInfo();
        setPartName(NLS.bind(Messages.SiteViewForm_title,
            siteInfo.site.getNameShort()));
    }

    private void updateSiteInfo() throws Exception {
        siteInfo = SessionManager.getAppService().doAction(
            new SiteGetInfoAction(adapter.getId()));
        site =
            new SiteWrapper(SessionManager.getAppService(), siteInfo.getSite());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.SiteViewForm_title, site.getName()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createSiteSection();
        createCommentsSection();
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

        nameLabel =
            createReadOnlyLabelledField(client, SWT.NONE, Messages.label_name);
        nameShortLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_nameShort);
        studyCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SiteViewForm_field_studyCount_label);
        containerTypeCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.site_field_type_label);
        topContainerCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SiteViewForm_field_topLevelCount_label);
        patientCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SiteViewForm_field_patientCount_label);
        patientVisitCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SiteViewForm_field_pvCount_label);
        specimenCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SiteViewForm_field_totalSpecimen);
        activityStatusLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_activity);
        setSiteSectionValues();
    }

    private void setSiteSectionValues() {
        setTextValue(nameLabel, siteInfo.site.getName());
        setTextValue(nameShortLabel, siteInfo.site.getNameShort());
        setTextValue(studyCountLabel, siteInfo.studyCountInfo.size());
        setTextValue(containerTypeCountLabel, siteInfo.containerTypes.size());
        setTextValue(topContainerCountLabel, siteInfo.topContainers.size());
        setTextValue(patientCountLabel, siteInfo.patientCount);
        setTextValue(patientVisitCountLabel, siteInfo.collectionEventCount);
        setTextValue(specimenCountLabel, siteInfo.aliquotedSpecimenCount);
        setTextValue(activityStatusLabel, siteInfo.site.getActivityStatus()
            .getName());
    }

    private void createStudySection() {
        Section section = createSection(Messages.SiteViewForm_studies_title);
        studiesTable = new NewStudyInfoTable(section, siteInfo.studyCountInfo);
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable.addClickListener(collectionDoubleClickListener);

        // TODO: provide way to edit study with this table
        // studiesTable.createDefaultEditItem();

        section.setClient(studiesTable);
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Messages.label_comments);
        commentTable =
            new CommentCollectionInfoTable(client,
                site.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
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

        containerTypesTable =
            new ContainerTypeInfoTable(section, siteAdapter,
                siteInfo.containerTypes);
        containerTypesTable.adaptToToolkit(toolkit, true);

        containerTypesTable.addClickListener(collectionDoubleClickListener);
        section.setClient(containerTypesTable);
    }

    private void createContainerSection() {
        Section section =
            createSection(Messages.SiteViewForm_topContainers_title);
        addSectionToolbar(section, Messages.SiteViewForm_topContainers_add,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    siteAdapter.getContainersGroupNode().addContainer(
                        siteAdapter, true);
                }
            }, ContainerWrapper.class);

        topContainersTable =
            new ContainerInfoTable(section, siteAdapter, siteInfo.topContainers);
        topContainersTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(topContainersTable);

        topContainersTable.addClickListener(collectionDoubleClickListener);
        section.setClient(topContainersTable);
    }

    @Override
    public void setValues() throws Exception {
        setPartName(NLS.bind(Messages.SiteViewForm_title,
            siteInfo.site.getNameShort()));
        form.setText(NLS.bind(Messages.SiteViewForm_title,
            siteInfo.site.getName()));
        setSiteSectionValues();
        setAddressValues(site);

        studiesTable.setCollection(siteInfo.studyCountInfo);
        containerTypesTable.setList(siteInfo.containerTypes);
        topContainersTable.setList(siteInfo.topContainers);
        // TODO: load comments?
        // commentTable.setList((List<?>) siteInfo.site
        // .getCommentCollection());
    }
}
