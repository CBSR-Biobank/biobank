package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimenType.SpecimenTypeGetAllAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetClinicInfoAction.ClinicInfo;
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
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.EventAttrCustom;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.widgets.EventAttrWidget;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.AliquotedSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.ClinicAddInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.SourceSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;

public class StudyEntryForm extends BiobankEntryForm {
    private static final I18n i18n = I18nFactory
        .getI18n(StudyEntryForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.StudyEntryForm";

    @SuppressWarnings("nls")
    private static final String MSG_NEW_STUDY_OK =
        i18n.tr("Creating a new study.");

    @SuppressWarnings("nls")
    private static final String MSG_STUDY_OK =
        i18n.tr("Editing an existing study.");

    @SuppressWarnings("nls")
    private static final String DATE_PROCESSED_INFO_FIELD_NAME =
        i18n.tr("Date Processed");

    protected static BgcLogger log = BgcLogger
        .getLogger(StudyEntryForm.class.getName());

    private static class StudyEventAttrCustom extends EventAttrCustom {
        public EventAttrWidget widget;
        public boolean inStudy;
    }

    private final StudyWrapper study = new StudyWrapper(
        SessionManager.getAppService());

    private ClinicAddInfoTable contactEntryTable;

    private final List<StudyEventAttrCustom> pvCustomInfoList;

    private AliquotedSpecimenEntryInfoTable aliquotedSpecimenEntryTable;

    private final BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    private ComboViewer activityStatusComboViewer;

    private SourceSpecimenEntryInfoTable sourceSpecimenEntryTable;

    private CommentsInfoTable commentEntryTable;

    private final CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private StudyInfo studyInfo;

    private List<SpecimenTypeWrapper> specimenTypeWrappers;

    public StudyEntryForm() {
        super();
        pvCustomInfoList = new ArrayList<StudyEventAttrCustom>();
    }

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof StudyAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        updateStudyInfo(adapter.getId());

        String tabName;
        if (study.isNew()) {
            tabName = i18n.tr("New study");
            study.setActivityStatus(ActivityStatus.ACTIVE);
        } else {
            tabName = i18n.tr("Study {0}",
                study.getNameShort());
        }
        setPartName(tabName);
    }

    private void updateStudyInfo(Integer id) throws Exception {
        if (id != null) {
            studyInfo =
                SessionManager.getAppService().doAction(
                    new StudyGetInfoAction(id));
            study.setWrappedObject(studyInfo.getStudy());
        } else {
            studyInfo = new StudyInfo();
            study.setWrappedObject(new Study());
        }

        comment.setWrappedObject(new Comment());

        List<SpecimenType> specimenTypes =
            SessionManager.getAppService()
                .doAction(new SpecimenTypeGetAllAction()).getList();
        specimenTypeWrappers =
            ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
                specimenTypes, SpecimenTypeWrapper.class);

        ((AdapterBase) adapter).setModelObject(study);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Study Information"));
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.NONE, HasName.PropertyName.NAME.toString(), null, study,
            StudyPeer.NAME.getName(), new NonEmptyStringValidator(
                i18n.tr("Study name cannot be blank"))));

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            HasNameShort.PropertyName.NAME_SHORT.toString(), null, study,
            StudyPeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
                i18n.tr("Study short name cannot be blank")));

        activityStatusComboViewer =
            createComboViewer(client,
                ActivityStatus.NAME.singular().toString(),
                ActivityStatus.valuesList(), study.getActivityStatus(),
                i18n.tr("Study must have an activity status"),
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        study
                            .setActivityStatus((ActivityStatus) selectedObject);
                    }
                });

        createCommentSection();
        createClinicSection();
        createSourceSpecimensSection();
        createAliquotedSpecimensSection();
        createEventAttrSection();
        createButtonsSection();
    }

    @SuppressWarnings("nls")
    private void createClinicSection() {
        Section section = createSection(i18n.tr("Clinics / Contacts"));
        List<Contact> contacts = new ArrayList<Contact>();
        for (ClinicInfo clinicInfo : studyInfo.getClinicInfos())
            contacts.addAll(clinicInfo.getContacts());
        contactEntryTable =
            new ClinicAddInfoTable(section, contacts);
        contactEntryTable.adaptToToolkit(toolkit, true);
        contactEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(section, i18n.tr("Add Clinic Contact"),
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    contactEntryTable.createClinicContact();
                }
            });
        section.setClient(contactEntryTable);
    }

    @SuppressWarnings("nls")
    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentsInfoTable(client, study.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            i18n.tr("Add a comment"), null, comment, "message", null);
    }

    @SuppressWarnings("nls")
    private void createSourceSpecimensSection() {
        Section section =
            createSection(i18n.tr("Source specimen types"));
        sourceSpecimenEntryTable =
            new SourceSpecimenEntryInfoTable(
                section,
                ModelWrapper.wrapModelCollection(
                    SessionManager.getAppService(),
                    studyInfo.getSourceSpecimens(), SourceSpecimenWrapper.class),
                specimenTypeWrappers);
        sourceSpecimenEntryTable.adaptToToolkit(toolkit, true);
        sourceSpecimenEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(section,
            i18n.tr("Add source specimen types"),
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    sourceSpecimenEntryTable.addSourceSpecimen();
                    aliquotedSpecimenEntryTable
                        .setAvailableSpecimenTypes(sourceSpecimenEntryTable
                            .getList());
                }
            });
        section.setClient(sourceSpecimenEntryTable);
    }

    @SuppressWarnings("nls")
    private void createAliquotedSpecimensSection() {
        Composite client =
            createSectionWithClient(i18n.tr("Aliquoted specimen types"));
        GridLayout layout = (GridLayout) client.getLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 0;

        toolkit
            .createLabel(
                client,
                i18n.tr("Source Specimen types must be added before any aliquoted specimen types can be added."),
                SWT.LEFT);

        aliquotedSpecimenEntryTable =
            new AliquotedSpecimenEntryInfoTable(client,
                ModelWrapper.wrapModelCollection(
                    SessionManager.getAppService(),
                    studyInfo.getAliquotedSpcs(),
                    AliquotedSpecimenWrapper.class), true, true);
        aliquotedSpecimenEntryTable.adaptToToolkit(toolkit, true);
        aliquotedSpecimenEntryTable.addSelectionChangedListener(listener);

        aliquotedSpecimenEntryTable
            .setAvailableSpecimenTypes(sourceSpecimenEntryTable
                .getList());

        addSectionToolbar((Section) client.getParent(),
            i18n.tr("Add aliquoted Specimen type"),
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    aliquotedSpecimenEntryTable.addAliquotedSpecimen();
                }
            }, AliquotedSpecimenWrapper.class);
    }

    @SuppressWarnings("nls")
    private void createEventAttrSection() throws Exception {
        Composite client =
            createSectionWithClient(i18n
                .tr("Patient Visit Information Collected"));
        GridLayout gl = (GridLayout) client.getLayout();
        gl.numColumns = 1;

        toolkit.createLabel(client,
            i18n.tr("Date Processed is collected by default."),
            SWT.LEFT);

        StudyEventAttrCustom studyEventAttrCustom;

        Map<String, StudyEventAttr> studyEventAttrLabelMap =
            new HashMap<String, StudyEventAttr>();
        for (StudyEventAttr sea : studyInfo.getStudyEventAttrs()) {
            studyEventAttrLabelMap.put(sea.getGlobalEventAttr().getLabel(),
                sea);
        }

        for (GlobalEventAttrWrapper geAttr : GlobalEventAttrWrapper
            .getAllGlobalEventAttrs(SessionManager.getAppService())) {
            String label = geAttr.getLabel();
            boolean selected = false;
            studyEventAttrCustom = new StudyEventAttrCustom();
            studyEventAttrCustom.setGlobalEventAttr(geAttr.getWrappedObject());
            studyEventAttrCustom.setLabel(label);
            studyEventAttrCustom.setType(geAttr.getTypeName());
            StudyEventAttr sea = studyEventAttrLabelMap.get(label);
            if (sea != null) {
                studyEventAttrCustom.setStudyEventAttrId(sea.getId());
                String permissible = sea.getPermissible();
                if ((permissible != null) && !permissible.isEmpty()) {
                    studyEventAttrCustom.setAllowedValues(permissible
                        .split(";"));
                }
                selected =
                    sea.getActivityStatus().equals(ActivityStatus.ACTIVE);
            }
            studyEventAttrCustom.setIsDefault(false);
            studyEventAttrCustom.widget =
                new EventAttrWidget(client, SWT.NONE, studyEventAttrCustom,
                    selected);
            studyEventAttrCustom.widget.addSelectionChangedListener(listener);
            studyEventAttrCustom.inStudy = (sea != null);
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

        study.getWrappedObject().setContacts(
            new HashSet<Contact>(contactEntryTable.getList()));

        final StudySaveAction saveAction = new StudySaveAction();
        saveAction.setId(study.getId());
        saveAction.setName(study.getName());
        saveAction.setNameShort(study.getNameShort());
        saveAction.setActivityStatus(study.getActivityStatus());
        saveAction.setContactIds(getContactInfos());
        saveAction.setSourceSpecimenSaveInfo(getSourceSpecimenInfos());
        saveAction.setAliquotSpecimenSaveInfo(getAliquotedSpecimenInfos());
        saveAction.setStudyEventAttrSaveInfo(getStudyEventAttrInfos());
        saveAction.setCommentText(comment.getMessage());

        Integer id =
            SessionManager.getAppService().doAction(saveAction).getId();
        study.setId(id);
        ((AdapterBase) adapter).setModelObject(study);
    }

    private HashSet<Integer> getContactInfos() {
        HashSet<Integer> contactIds = new HashSet<Integer>();

        for (ContactWrapper wrapper : study.getContactCollection(false)) {
            contactIds.add(wrapper.getId());
        }
        return contactIds;
    }

    private HashSet<SourceSpecimenSaveInfo> getSourceSpecimenInfos() {
        HashSet<SourceSpecimenSaveInfo> sourceSpecimenSaveInfos =
            new HashSet<SourceSpecimenSaveInfo>();

        Set<SourceSpecimen> newSourceSpcs =
            new HashSet<SourceSpecimen>(studyInfo.getSourceSpecimens());

        // remove the ones deleted
        for (SourceSpecimenWrapper wrapper : sourceSpecimenEntryTable
            .getDeletedSourceSpecimens()) {
            newSourceSpcs.remove(wrapper.getWrappedObject());
        }

        // add the ones not modified
        for (SourceSpecimen ss : newSourceSpcs) {
            if (!sourceSpecimenEntryTable
                .getAddedOrModifiedSourceSpecimens().contains(ss)) {
                sourceSpecimenSaveInfos.add(new SourceSpecimenSaveInfo(ss));
            }
        }

        // add the modified ones
        for (SourceSpecimenWrapper wrapper : sourceSpecimenEntryTable
            .getAddedOrModifiedSourceSpecimens()) {
            sourceSpecimenSaveInfos.add(new SourceSpecimenSaveInfo(
                wrapper.getWrappedObject()));
        }
        return sourceSpecimenSaveInfos;
    }

    private HashSet<AliquotedSpecimenSaveInfo> getAliquotedSpecimenInfos() {
        HashSet<AliquotedSpecimenSaveInfo> aliquotedSpecimenSaveInfos =
            new HashSet<AliquotedSpecimenSaveInfo>();

        Set<AliquotedSpecimen> newAliquotedSpcs =
            new HashSet<AliquotedSpecimen>(studyInfo.getAliquotedSpcs());

        // remove the ones deleted
        for (AliquotedSpecimenWrapper wrapper : aliquotedSpecimenEntryTable
            .getDeletedAliquotedSpecimens()) {
            newAliquotedSpcs.remove(wrapper.getWrappedObject());
        }

        // add the ones not modified
        for (AliquotedSpecimen as : newAliquotedSpcs) {
            if (!aliquotedSpecimenEntryTable
                .getAddedOrModifiedAliquotedSpecimens().contains(as)) {
                aliquotedSpecimenSaveInfos
                    .add(new AliquotedSpecimenSaveInfo(as));
            }
        }

        // add the modified ones
        for (AliquotedSpecimenWrapper wrapper : aliquotedSpecimenEntryTable
            .getAddedOrModifiedAliquotedSpecimens()) {
            aliquotedSpecimenSaveInfos.add(new AliquotedSpecimenSaveInfo(
                wrapper.getWrappedObject()));
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

                    log.debug(studyEventAttrSaveInfo.toString());
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
        sourceSpecimenEntryTable.reload(ModelWrapper.wrapModelCollection(
            SessionManager.getAppService(),
            studyInfo.getSourceSpecimens(), SourceSpecimenWrapper.class));
        commentEntryTable.setList(study.getCommentCollection(false));
        resetPvCustomInfo();
    }

    @SuppressWarnings("nls")
    private void resetPvCustomInfo() throws Exception {
        Map<String, StudyEventAttr> studyEventAttrLabelMap =
            new HashMap<String, StudyEventAttr>();
        for (StudyEventAttr sea : studyInfo.getStudyEventAttrs()) {
            studyEventAttrLabelMap.put(sea.getGlobalEventAttr().getLabel(),
                sea);
        }

        for (StudyEventAttrCustom studyPvAttrCustom : pvCustomInfoList) {
            boolean selected = false;
            StudyEventAttr sea =
                studyEventAttrLabelMap.get(studyPvAttrCustom.getLabel());
            if (sea != null) {
                String permissible = sea.getPermissible();
                if ((permissible != null) && !permissible.isEmpty()) {
                    studyPvAttrCustom.setAllowedValues(permissible
                        .split(";"));
                }
                selected = true;
                studyPvAttrCustom.inStudy = true;
            }
            selected |= (studyPvAttrCustom.getAllowedValues() != null);
            studyPvAttrCustom.widget.setSelected(selected);
            studyPvAttrCustom.widget.reloadAllowedValues(studyPvAttrCustom);
        }
    }
}
