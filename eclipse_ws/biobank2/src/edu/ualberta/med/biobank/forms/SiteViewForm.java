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
import edu.ualberta.med.biobank.common.action.info.SiteContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.info.SiteInfo;
import edu.ualberta.med.biobank.common.action.info.StudyCountInfo;
import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.admin.ContainerTypeAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContainerInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.ContainerTypeInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewStudyInfoTable;

public class SiteViewForm extends AddressViewFormCommon {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.SiteViewForm";

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

    private BgcBaseText processingEventCountLabel;

    private BgcBaseText specimenCountLabel;

    private BgcBaseText activityStatusLabel;

    private SiteInfo siteInfo;

    private final SiteWrapper site = new SiteWrapper(
        SessionManager.getAppService());

    private CommentsInfoTable commentTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof SiteAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        siteAdapter = (SiteAdapter) adapter;
        updateSiteInfo();
        setPartName(NLS.bind("Repository site {0}",
            siteInfo.getSite().getNameShort()));
    }

    private void updateSiteInfo() throws Exception {
        Assert.isNotNull(adapter.getId());
        siteInfo = SessionManager.getAppService().doAction(
            new SiteGetInfoAction(adapter.getId()));
        Assert.isNotNull(siteInfo.getSite());
        site.setWrappedObject(siteInfo.getSite());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind("Repository site {0}", site.getName()));
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
            createReadOnlyLabelledField(client, SWT.NONE,
                HasName.PropertyName.NAME.toString());
        nameShortLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                HasNameShort.PropertyName.NAME_SHORT.toString());
        studyCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Total studies");
        containerTypeCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                ContainerType.NAME.plural().toString());
        topContainerCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Top level containers");
        patientCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Total patients");
        processingEventCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Total processing events");
        specimenCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Total specimens");
        activityStatusLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                ActivityStatus.NAME.singular().toString());
        setSiteSectionValues();
    }

    private void setSiteSectionValues() {
        setTextValue(nameLabel, siteInfo.getSite().getName());
        setTextValue(nameShortLabel, siteInfo.getSite().getNameShort());
        setTextValue(studyCountLabel, siteInfo.getStudyCountInfos().size());
        setTextValue(containerTypeCountLabel, siteInfo.getContainerTypeInfos()
            .size());
        setTextValue(topContainerCountLabel, siteInfo.getTopContainerCount());
        setTextValue(patientCountLabel, siteInfo.getPatientCount());
        setTextValue(processingEventCountLabel,
            siteInfo.getProcessingEventCount());
        setTextValue(specimenCountLabel, siteInfo.getSpecimenCount());
        setTextValue(activityStatusLabel, siteInfo.getSite()
            .getActivityStatus()
            .getName());
    }

    private void createStudySection() {
        Section section = createSection(Study.NAME.plural().toString());
        studiesTable =
            new NewStudyInfoTable(section, siteInfo.getStudyCountInfos());
        studiesTable.adaptToToolkit(toolkit, true);
        studiesTable
            .addClickListener(new IInfoTableDoubleClickItemListener<StudyCountInfo>() {

                @Override
                public void doubleClick(InfoTableEvent<StudyCountInfo> event) {
                    Study s =
                        ((StudyCountInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).getStudy();
                    new StudyAdapter(null,
                        new StudyWrapper(SessionManager
                            .getAppService(), s)).openViewForm();

                }
            });
        studiesTable
            .addEditItemListener(new IInfoTableEditItemListener<StudyCountInfo>() {
                @Override
                public void editItem(InfoTableEvent<StudyCountInfo> event) {
                    Study s =
                        ((StudyCountInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).getStudy();
                    new StudyAdapter(null,
                        new StudyWrapper(SessionManager
                            .getAppService(), s)).openEntryForm();
                }
            });

        section.setClient(studiesTable);
    }

    private void createCommentsSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        commentTable =
            new CommentsInfoTable(client,
                site.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void createContainerTypesSection() {
        Section section = createSection(ContainerType.NAME.plural().toString());
        addSectionToolbar(section, "Add container type",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    siteAdapter.getContainerTypesGroupNode().addContainerType(
                        siteAdapter, true);
                }
            }, ContainerTypeWrapper.class);

        containerTypesTable =
            new ContainerTypeInfoTable(section, siteAdapter,
                siteInfo.getContainerTypeInfos());
        containerTypesTable.adaptToToolkit(toolkit, true);

        containerTypesTable
            .addClickListener(new IInfoTableDoubleClickItemListener<SiteContainerTypeInfo>() {

                @Override
                public void doubleClick(
                    InfoTableEvent<SiteContainerTypeInfo> event) {
                    ContainerType ct =
                        ((SiteContainerTypeInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).getContainerType();
                    new ContainerTypeAdapter(null, new ContainerTypeWrapper(
                        SessionManager.getAppService(), ct)).openViewForm();
                }
            });
        section.setClient(containerTypesTable);
    }

    private void createContainerSection() {
        Section section =
            createSection("Top level containers");
        addSectionToolbar(section, "Add container",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    siteAdapter.getContainersGroupNode().addContainer(
                        siteAdapter, true);
                }
            }, ContainerWrapper.class);

        topContainersTable =
            new ContainerInfoTable(section, siteAdapter,
                siteInfo.getTopContainers());
        topContainersTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(topContainersTable);

        topContainersTable
            .addClickListener(new IInfoTableDoubleClickItemListener<Container>() {

                @Override
                public void doubleClick(InfoTableEvent<Container> event) {
                    ContainerWrapper ct =
                        (ContainerWrapper) ((InfoTableSelection) event
                            .getSelection()).getObject();
                    new ContainerAdapter(null, ct).openViewForm();
                }
            });
        section.setClient(topContainersTable);
    }

    @Override
    public void setValues() throws Exception {
        setPartName(NLS.bind("Repository site {0}",
            siteInfo.getSite().getNameShort()));
        form.setText(NLS.bind("Repository site {0}",
            siteInfo.getSite().getName()));
        setSiteSectionValues();
        setAddressValues(site);

        studiesTable.setList(siteInfo.getStudyCountInfos());
        containerTypesTable.setList(siteInfo.getContainerTypeInfos());
        topContainersTable.setList(siteInfo.getTopContainers());
        // TODO: load comments?
        // commentTable.setList((List<?>) siteInfo.site
        // .getCommentCollection());
    }
}
