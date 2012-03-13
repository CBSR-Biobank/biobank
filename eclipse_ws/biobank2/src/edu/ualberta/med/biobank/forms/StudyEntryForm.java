package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeGetAllAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.AliquotedSpecimenSaveInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.SourceSpecimenSaveInfo;
import edu.ualberta.med.biobank.common.action.study.StudySaveAction.StudyEventAttrSaveInfo;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.GlobalEventAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.EventAttrCustom;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.widgets.EventAttrWidget;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.AliquotedSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.ClinicAddInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.SourceSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;

public class StudyEntryForm extends BiobankEntryForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StudyEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_STUDY_OK =
        Messages.StudyEntryForm_creation_msg;

    private static final String MSG_STUDY_OK =
        Messages.StudyEntryForm_edition_msg;

    private static final String DATE_PROCESSED_INFO_FIELD_NAME =
        Messages.study_visit_info_dateProcessed;

    protected static BgcLogger LOGGER = BgcLogger
        .getLogger(StudyEntryForm.class.getName());

    private static class StudyEventAttrCustom extends EventAttrCustom {
        public EventAttrWidget widget;
        public boolean inStudy;
    }

    private StudyWrapper study = new StudyWrapper(
        SessionManager.getAppService());

    private ClinicAddInfoTable contactEntryTable;

    private List<StudyEventAttrCustom> pvCustomInfoList;

    private AliquotedSpecimenEntryInfoTable aliquotedSpecimenEntryTable;

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    private ComboViewer activityStatusComboViewer;

    private SourceSpecimenEntryInfoTable sourceSpecimenEntryTable;

    private CommentsInfoTable commentEntryTable;

    private CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private StudyInfo studyInfo;

    private List<SpecimenTypeWrapper> specimenTypeWrappers;

    public StudyEntryForm() {
        super();
        pvCustomInfoList = new ArrayList<StudyEventAttrCustom>();
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof StudyAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        updateStudyInfo(adapter.getId());

        String tabName;
        if (study.isNew()) {
            tabName = Messages.StudyEntryForm_title_new;
            study.setActivityStatus(ActivityStatus.ACTIVE);
        } else {
            tabName = NLS.bind(Messages.StudyEntryForm_title_edit,
                study.getNameShort());
        }
        setPartName(tabName);
    }

    private void updateStudyInfo(Integer id) throws Exception {
        if (id != null) {
            studyInfo = SessionManager.getAppService().doAction(
                new StudyGetInfoAction(id));
            study.setWrappedObject(studyInfo.getStudy());
        } else {
            studyInfo = new StudyInfo();
            study.setWrappedObject(new Study());
        }

        comment.setWrappedObject(new Comment());

        List<SpecimenType> specimenTypes =
            SessionManager.getAppService().doAction(
                new SpecimenTypeGetAllAction()).getList();
        specimenTypeWrappers =
            ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
                specimenTypes, SpecimenTypeWrapper.class);

        ((AdapterBase) adapter).setModelObject(study);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.StudyEntryForm_main_title);
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.NONE, Messages.label_name, null, study,
            StudyPeer.NAME.getName(), new NonEmptyStringValidator(
                Messages.StudyEntryForm_name_validator_msg)));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.label_nameShort, null, study,
            StudyPeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
                Messages.StudyEntryForm_nameShort_validator_msg));

        activityStatusComboViewer = createComboViewer(client,
            Messages.label_activity, ActivityStatus.valuesList(),
            study.getActivityStatus(),
            Messages.StudyEntryForm_activity_validator_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    study.setActivityStatus((ActivityStatus) selectedObject);
                }
            });

        createCommentSection();
        createClinicSection();
        createSourceSpecimensSection();
        createAliquotedSpecimensSection();
        createEventAttrSection();
        createButtonsSection();
    }

    private void createClinicSection() {
        Section section = createSection(Messages.StudyEntryForm_contacts_title);
        contactEntryTable = new ClinicAddInfoTable(section, study);
        contactEntryTable.adaptToToolkit(toolkit, true);
        contactEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(section, Messages.StudyEntryForm_contacts_button_add,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    contactEntryTable.createClinicContact();
                }
            });
        section.setClient(contactEntryTable);
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentsInfoTable(client,
            study.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.MULTI, Messages.Comments_add, null, comment, "message", null);
    }

    private void createSourceSpecimensSection() {
        Section section =
            createSection(Messages.StudyEntryForm_source_specimens_title);
        sourceSpecimenEntryTable = new SourceSpecimenEntryInfoTable(section,
            study, specimenTypeWrappers);
        sourceSpecimenEntryTable.adaptToToolkit(toolkit, true);
        sourceSpecimenEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(section,
            Messages.StudyEntryForm_source_specimens_button_add,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    sourceSpecimenEntryTable.addSourceSpecimen();
                }
            });
        section.setClient(sourceSpecimenEntryTable);
    }

    private void createAliquotedSpecimensSection() {
        Section section =
            createSection(Messages.StudyEntryForm_aliquoted_specimens_title);
        aliquotedSpecimenEntryTable = new AliquotedSpecimenEntryInfoTable(
            section, study);
        aliquotedSpecimenEntryTable.adaptToToolkit(toolkit, true);
        aliquotedSpecimenEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(section,
            Messages.StudyEntryForm_aliquoted_specimens_button_add,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    aliquotedSpecimenEntryTable.addAliquotedSpecimen();
                }
            }, AliquotedSpecimenWrapper.class);
        section.setClient(aliquotedSpecimenEntryTable);
    }

    private void createEventAttrSection() throws Exception {
        Composite client =
            createSectionWithClient(Messages.StudyEntryForm_visit_info_title);
        GridLayout gl = (GridLayout) client.getLayout();
        gl.numColumns = 1;

        toolkit.createLabel(client, "Date Processed is collected by default.",
            SWT.LEFT);

        StudyEventAttrCustom studyEventAttrCustom;

        List<String> studyEventInfoLabels = Arrays.asList(study
            .getStudyEventAttrLabels());

        for (GlobalEventAttrWrapper geAttr : GlobalEventAttrWrapper
            .getAllGlobalEventAttrs(SessionManager.getAppService())) {
            String label = geAttr.getLabel();
            boolean selected = false;
            studyEventAttrCustom = new StudyEventAttrCustom();
            studyEventAttrCustom.setGlobalEventAttr(geAttr.getWrappedObject());
            studyEventAttrCustom.setLabel(label);
            studyEventAttrCustom.setType(geAttr.getTypeName());
            if (studyEventInfoLabels.contains(label)) {
                studyEventAttrCustom.setStudyEventAttrId(study
                    .getStudyEventAttr(label).getId());
                studyEventAttrCustom.setAllowedValues(study
                    .getStudyEventAttrPermissible(label));
                selected = study.getStudyEventAttrActivityStatus(label).equals(
                    ActivityStatus.ACTIVE);
            }
            studyEventAttrCustom.setIsDefault(false);
            studyEventAttrCustom.widget = new EventAttrWidget(client, SWT.NONE,
                studyEventAttrCustom, selected);
            studyEventAttrCustom.widget.addSelectionChangedListener(listener);
            studyEventAttrCustom.inStudy = studyEventInfoLabels.contains(label);
            pvCustomInfoList.add(studyEventAttrCustom);
        }
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
    }

    @Override
    protected String getOkMessage() {
        if (study.getId() == null) {
            return MSG_NEW_STUDY_OK;
        }
        return MSG_STUDY_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        // save of source specimen is made inside the entryinfotable

        final StudySaveAction saveAction = new StudySaveAction();
        saveAction.setId(study.getId());
        saveAction.setName(study.getName());
        saveAction.setNameShort(study.getNameShort());
        saveAction.setActivityStatus(study.getActivityStatus());
        saveAction.setSiteIds(getSiteInfos());
        saveAction.setContactIds(getContactInfos());
        saveAction.setSourceSpecimenSaveInfo(getSourceSpecimenInfos());
        saveAction.setAliquotSpecimenSaveInfo(getAliquotedSpecimenInfos());
        saveAction.setStudyEventAttrSaveInfo(getStudyEventAttrInfos());
        saveAction.setCommentText(comment.getMessage());

        Integer id =
            SessionManager.getAppService().doAction(saveAction).getId();
        updateStudyInfo(id);
    }

    private HashSet<Integer> getSiteInfos() {
        HashSet<Integer> siteIds = new HashSet<Integer>();

        for (SiteWrapper wrapper : study.getSiteCollection(false)) {
            siteIds.add(wrapper.getId());
        }
        return siteIds;
    }

    private HashSet<Integer> getContactInfos() {
        HashSet<Integer> contactIds = new HashSet<Integer>();

        for (ContactWrapper wrapper : study.getContactCollection(false)) {
            contactIds.add(wrapper.getId());
        }
        return contactIds;
    }

    private HashSet<SourceSpecimenSaveInfo> getSourceSpecimenInfos() {
        study.addToSourceSpecimenCollection(sourceSpecimenEntryTable
            .getAddedOrModifiedSourceSpecimens());
        study.removeFromSourceSpecimenCollection(sourceSpecimenEntryTable
            .getDeletedSourceSpecimens());

        HashSet<SourceSpecimenSaveInfo> sourceSpecimenSaveInfos =
            new HashSet<SourceSpecimenSaveInfo>();

        for (SourceSpecimenWrapper wrapper : study
            .getSourceSpecimenCollection(false)) {
            SourceSpecimenSaveInfo sourceSpecimenSaveInfo =
                new SourceSpecimenSaveInfo(wrapper.getWrappedObject());
            LOGGER.debug(sourceSpecimenSaveInfo.toString());
            sourceSpecimenSaveInfos.add(sourceSpecimenSaveInfo);
        }
        return sourceSpecimenSaveInfos;
    }

    private HashSet<AliquotedSpecimenSaveInfo> getAliquotedSpecimenInfos() {
        study.addToAliquotedSpecimenCollection(aliquotedSpecimenEntryTable
            .getAddedOrModifiedAliquotedSpecimens());
        study.removeFromAliquotedSpecimenCollection(aliquotedSpecimenEntryTable
            .getDeletedAliquotedSpecimens());

        HashSet<AliquotedSpecimenSaveInfo> aliquotedSpecimenSaveInfos =
            new HashSet<AliquotedSpecimenSaveInfo>();

        for (AliquotedSpecimenWrapper wrapper : study
            .getAliquotedSpecimenCollection(false)) {
            AliquotedSpecimenSaveInfo aliquotedSpecimenSaveInfo =
                new AliquotedSpecimenSaveInfo(wrapper.getWrappedObject());
            LOGGER.debug(aliquotedSpecimenSaveInfo.toString());
            aliquotedSpecimenSaveInfos.add(aliquotedSpecimenSaveInfo);
        }
        return aliquotedSpecimenSaveInfos;
    }

    private HashSet<StudyEventAttrSaveInfo> getStudyEventAttrInfos() {
        final HashSet<StudyEventAttrSaveInfo> studyEventAttrSaveInfos =
            new HashSet<StudyEventAttrSaveInfo>();

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {

                for (StudyEventAttrCustom studyEventAttrCustom : pvCustomInfoList) {
                    String label = studyEventAttrCustom.getLabel();
                    if (label.equals(DATE_PROCESSED_INFO_FIELD_NAME)
                        || (!studyEventAttrCustom.widget.getSelected() && !studyEventAttrCustom.inStudy))
                        continue;

                    StudyEventAttrSaveInfo studyEventAttrSaveInfo =
                        new StudyEventAttrSaveInfo();

                    studyEventAttrSaveInfo.id =
                        studyEventAttrCustom.getStudyEventAttrId();
                    studyEventAttrSaveInfo.globalEventAttrId =
                        studyEventAttrCustom.getGlobalEventAttrId();
                    studyEventAttrSaveInfo.permissible =
                        studyEventAttrCustom.widget.getValues();

                    // TODO: required not used at the moment
                    studyEventAttrSaveInfo.required = false;

                    if (!studyEventAttrCustom.widget.getSelected()
                        && studyEventAttrCustom.inStudy) {
                        studyEventAttrSaveInfo.activityStatus =
                            ActivityStatus.CLOSED;
                    } else if (studyEventAttrCustom.widget.getSelected()) {
                        studyEventAttrSaveInfo.activityStatus =
                            ActivityStatus.ACTIVE;
                    }

                    LOGGER.debug(studyEventAttrSaveInfo.toString());
                    studyEventAttrSaveInfos.add(studyEventAttrSaveInfo);
                }
            }
        });
        return studyEventAttrSaveInfos;
    }

    @Override
    public String getNextOpenedFormId() {
        return StudyViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        if (study.isNew()) {
            study.setActivityStatus(ActivityStatus.ACTIVE);
        }

        GuiUtil.reset(activityStatusComboViewer, study.getActivityStatus());
        contactEntryTable.reload();
        aliquotedSpecimenEntryTable.reload();
        sourceSpecimenEntryTable.reload();
        commentEntryTable.setList(ModelWrapper.wrapModelCollection(
            SessionManager.getAppService(), studyInfo.getStudy().getComments(),
            CommentWrapper.class));
        resetPvCustomInfo();
    }

    private void resetPvCustomInfo() throws Exception {
        List<String> studyPvInfoLabels = Arrays.asList(study
            .getStudyEventAttrLabels());

        for (StudyEventAttrCustom studyPvAttrCustom : pvCustomInfoList) {
            boolean selected = false;
            String label = studyPvAttrCustom.getLabel();
            if (studyPvInfoLabels.contains(studyPvAttrCustom.getLabel())) {
                studyPvAttrCustom.setAllowedValues(study
                    .getStudyEventAttrPermissible(label));
                selected = true;
                studyPvAttrCustom.inStudy = true;
            }
            selected |= (studyPvAttrCustom.getAllowedValues() != null);
            studyPvAttrCustom.widget.setSelected(selected);
            studyPvAttrCustom.widget.reloadAllowedValues(studyPvAttrCustom);
        }
    }
}
