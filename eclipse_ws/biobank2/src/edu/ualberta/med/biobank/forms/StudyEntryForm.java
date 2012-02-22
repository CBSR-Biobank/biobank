package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.GlobalEventAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.exception.UserUIException;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.EventAttrCustom;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.widgets.EventAttrWidget;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
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

    private StudyAdapter studyAdapter;

    private StudyWrapper study;

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

    private CommentCollectionInfoTable commentEntryTable;

    private static class StudyEventAttrCustom extends EventAttrCustom {
        public EventAttrWidget widget;
        public boolean inStudy;
    }

    public StudyEntryForm() {
        super();
        pvCustomInfoList = new ArrayList<StudyEventAttrCustom>();
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof StudyAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        studyAdapter = (StudyAdapter) adapter;
        study = (StudyWrapper) getModelObject();

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
                    study
                        .setActivityStatus((ActivityStatus) selectedObject);
                }
            });

        createCommentSection();
        createClinicSection();
        createSourceSpecimensSection();
        createAliquotedSpecimensSection();
        createPvCustomInfoSection();
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
        commentEntryTable = new CommentCollectionInfoTable(client,
            study.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createLabelledWidget(client, BgcBaseText.class, SWT.MULTI,
            Messages.Comments_add);

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

    private void createSourceSpecimensSection() {
        Section section =
            createSection(Messages.StudyEntryForm_source_specimens_title);
        sourceSpecimenEntryTable = new SourceSpecimenEntryInfoTable(section,
            study);
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

    private void createPvCustomInfoSection() throws Exception {
        Composite client =
            createSectionWithClient(Messages.StudyEntryForm_visit_info_title);
        GridLayout gl = (GridLayout) client.getLayout();
        gl.numColumns = 1;

        // START KLUDGE
        //
        // create "date processed" attribute - actually an attribute in
        // PatientVisit - but we just want to show the user that this
        // information is collected by default.
        String[] defaultFields =
            new String[] { DATE_PROCESSED_INFO_FIELD_NAME };
        StudyEventAttrCustom studyPvAttrCustom;

        for (String field : defaultFields) {
            studyPvAttrCustom = new StudyEventAttrCustom();
            studyPvAttrCustom.setLabel(field);
            studyPvAttrCustom.setType(EventAttrTypeEnum.DATE_TIME);
            studyPvAttrCustom.setIsDefault(true);
            studyPvAttrCustom.widget = new EventAttrWidget(client, SWT.NONE,
                studyPvAttrCustom, true);
            studyPvAttrCustom.inStudy = false;
            pvCustomInfoList.add(studyPvAttrCustom);
        }
        //
        // END KLUDGE

        List<String> studyEventInfoLabels = Arrays.asList(study
            .getStudyEventAttrLabels());

        for (GlobalEventAttrWrapper geAttr : GlobalEventAttrWrapper
            .getAllGlobalEventAttrs(SessionManager.getAppService())) {
            String label = geAttr.getLabel();
            boolean selected = false;
            studyPvAttrCustom = new StudyEventAttrCustom();
            studyPvAttrCustom.setLabel(label);
            studyPvAttrCustom.setType(geAttr.getTypeName());
            if (studyEventInfoLabels.contains(label)) {
                studyPvAttrCustom.setAllowedValues(study
                    .getStudyEventAttrPermissible(label));
                selected = true;
            }
            studyPvAttrCustom.setIsDefault(false);
            studyPvAttrCustom.widget = new EventAttrWidget(client, SWT.NONE,
                studyPvAttrCustom, selected);
            studyPvAttrCustom.widget.addSelectionChangedListener(listener);
            studyPvAttrCustom.inStudy = studyEventInfoLabels.contains(label);
            pvCustomInfoList.add(studyPvAttrCustom);
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
    protected void doBeforeSave() throws Exception {
        setStudyPvAttr();
    }

    @Override
    protected void saveForm() throws Exception {
        // save of source specimen is made inside the entryinfotable

        // aliquoted Specimen :
        study.addToAliquotedSpecimenCollection(aliquotedSpecimenEntryTable
            .getAddedOrModifiedAliquotedSpecimens());
        study.removeFromAliquotedSpecimenCollection(aliquotedSpecimenEntryTable
            .getDeletedAliquotedSpecimens());
        study.persist();

        SessionManager.updateAllSimilarNodes(studyAdapter, true);
    }

    private void setStudyPvAttr() throws Exception, UserUIException {
        List<String> newPvInfoLabels = new ArrayList<String>();
        for (StudyEventAttrCustom studyPvAttrCustom : pvCustomInfoList) {
            String label = studyPvAttrCustom.getLabel();
            if (label.equals(DATE_PROCESSED_INFO_FIELD_NAME))
                continue;

            if (!studyPvAttrCustom.widget.getSelected()
                && studyPvAttrCustom.inStudy) {
                try {
                    study.deleteStudyEventAttr(studyPvAttrCustom.getLabel());
                } catch (BiobankCheckException e) {
                    throw new UserUIException(NLS.bind(
                        Messages.StudyEntryForm_delete_error_msg, label), e);
                }
            } else if (studyPvAttrCustom.widget.getSelected()) {
                newPvInfoLabels.add(studyPvAttrCustom.getLabel());
                String value = studyPvAttrCustom.widget.getValues();
                if (studyPvAttrCustom.getType() == EventAttrTypeEnum.SELECT_SINGLE
                    || studyPvAttrCustom.getType() == EventAttrTypeEnum.SELECT_MULTIPLE) {
                    if (value.length() > 0) {
                        study
                            .setStudyEventAttr(
                                studyPvAttrCustom.getLabel(),
                                studyPvAttrCustom.getType(),
                                value
                                    .split(EventAttrCustom.VALUE_MULTIPLE_SEPARATOR));
                    } else if (value.length() == 0) {
                        study.setStudyEventAttr(studyPvAttrCustom.getLabel(),
                            studyPvAttrCustom.getType(), null);
                    }
                } else {
                    study.setStudyEventAttr(studyPvAttrCustom.getLabel(),
                        studyPvAttrCustom.getType());
                }
            }
        }
    }

    @Override
    public String getNextOpenedFormID() {
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
