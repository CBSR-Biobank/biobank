package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.StudyPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.GlobalEventAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.exception.UserUIException;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.PvInfoWidget;
import edu.ualberta.med.biobank.widgets.infotables.entry.AliquotedSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.ClinicAddInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.entry.SourceSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;

public class StudyEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.StudyEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_STUDY_OK = Messages
        .getString("StudyEntryForm.creation.msg"); //$NON-NLS-1$

    private static final String MSG_STUDY_OK = Messages
        .getString("StudyEntryForm.edition.msg"); //$NON-NLS-1$

    private static final String DATE_PROCESSED_INFO_FIELD_NAME = Messages
        .getString("study.visit.info.dateProcessed"); //$NON-NLS-1$

    private StudyAdapter studyAdapter;

    private StudyWrapper study;

    private ClinicAddInfoTable contactEntryTable;

    private List<StudyPvAttrCustom> pvCustomInfoList;

    private AliquotedSpecimenEntryInfoTable aliquotedSpecimenEntryTable;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private ComboViewer activityStatusComboViewer;

    private SourceSpecimenEntryInfoTable sourceSpecimenEntryTable;

    private static class StudyPvAttrCustom extends PvAttrCustom {
        public PvInfoWidget widget;
        public boolean inStudy;
    }

    public StudyEntryForm() {
        super();
        pvCustomInfoList = new ArrayList<StudyPvAttrCustom>();
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
            tabName = Messages.getString("StudyEntryForm.title.new"); //$NON-NLS-1$
            study.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else {
            tabName = Messages.getString("StudyEntryForm.title.edit", //$NON-NLS-1$
                study.getNameShort());
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("StudyEntryForm.main.title")); //$NON-NLS-1$
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(createBoundWidgetWithLabel(client,
            BiobankText.class,
            SWT.NONE,
            Messages.getString("label.name"), null, study, //$NON-NLS-1$
            StudyPeer.NAME.getName(),
            new NonEmptyStringValidator(Messages
                .getString("StudyEntryForm.name.validator.msg")))); //$NON-NLS-1$

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            Messages.getString("label.nameShort"), null, study, //$NON-NLS-1$
            StudyPeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
                Messages.getString("StudyEntryForm.nameShort.validator.msg"))); //$NON-NLS-1$

        activityStatusComboViewer = createComboViewer(
            client,
            Messages.getString("label.activity"), //$NON-NLS-1$
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            study.getActivityStatus(),
            Messages.getString("StudyEntryForm.activity.validator.msg"), //$NON-NLS-1$
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    study
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            Messages.getString("label.comments"), null, study, //$NON-NLS-1$
            StudyPeer.COMMENT.getName(), null);

        createClinicSection();
        createSourceSpecimensSection();
        createAliquotedSpecimensSection();
        createPvCustomInfoSection();
        createButtonsSection();
    }

    private void createClinicSection() {
        Section section = createSection(Messages
            .getString("StudyEntryForm.contacts.title")); //$NON-NLS-1$
        contactEntryTable = new ClinicAddInfoTable(section, study);
        contactEntryTable.adaptToToolkit(toolkit, true);
        contactEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(section,
            Messages.getString("StudyEntryForm.contacts.button.add"), //$NON-NLS-1$
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    contactEntryTable.createClinicContact();
                }
            });
        section.setClient(contactEntryTable);
    }

    private void createAliquotedSpecimensSection() {
        Section section = createSection(Messages
            .getString("StudyEntryForm.aliquoted.specimens.title")); //$NON-NLS-1$
        aliquotedSpecimenEntryTable = new AliquotedSpecimenEntryInfoTable(
            section, study);
        aliquotedSpecimenEntryTable.adaptToToolkit(toolkit, true);
        aliquotedSpecimenEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(
            section,
            Messages.getString("StudyEntryForm.aliquoted.specimens.button.add"), //$NON-NLS-1$
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    aliquotedSpecimenEntryTable.addAliquotedSpecimen();
                }
            }, AliquotedSpecimenWrapper.class);
        section.setClient(aliquotedSpecimenEntryTable);
    }

    private void createSourceSpecimensSection() {
        Section section = createSection(Messages
            .getString("StudyEntryForm.source.specimens.title")); //$NON-NLS-1$
        sourceSpecimenEntryTable = new SourceSpecimenEntryInfoTable(section,
            study);
        sourceSpecimenEntryTable.adaptToToolkit(toolkit, true);
        sourceSpecimenEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(
            section,
            Messages.getString("StudyEntryForm.source.specimens.button.add"), new SelectionAdapter() { //$NON-NLS-1$
                @Override
                public void widgetSelected(SelectionEvent e) {
                    sourceSpecimenEntryTable.addSourceSpecimen();
                }
            });
        section.setClient(sourceSpecimenEntryTable);
    }

    private void createPvCustomInfoSection() throws Exception {
        Composite client = createSectionWithClient(Messages
            .getString("StudyEntryForm.visit.info.title")); //$NON-NLS-1$
        GridLayout gl = (GridLayout) client.getLayout();
        gl.numColumns = 1;

        // START KLUDGE
        //
        // create "date processed" attribute - actually an attribute in
        // PatientVisit - but we just want to show the user that this
        // information is collected by default.
        String[] defaultFields = new String[] { DATE_PROCESSED_INFO_FIELD_NAME };
        StudyPvAttrCustom studyPvAttrCustom;

        for (String field : defaultFields) {
            studyPvAttrCustom = new StudyPvAttrCustom();
            studyPvAttrCustom.setLabel(field);
            studyPvAttrCustom.setType(EventAttrTypeEnum.DATE_TIME); //$NON-NLS-1$
            studyPvAttrCustom.setIsDefault(true);
            studyPvAttrCustom.widget = new PvInfoWidget(client, SWT.NONE,
                studyPvAttrCustom, true);
            studyPvAttrCustom.inStudy = false;
            pvCustomInfoList.add(studyPvAttrCustom);
        }
        //
        // END KLUDGE

        List<String> studyEventInfoLabels = Arrays.asList(study
            .getStudyEventAttrLabels());

        for (GlobalEventAttrWrapper geAttr : GlobalEventAttrWrapper
            .getAllGlobalEventAttrs(appService)) {
            String label = geAttr.getLabel();
            boolean selected = false;
            studyPvAttrCustom = new StudyPvAttrCustom();
            studyPvAttrCustom.setLabel(label);
            studyPvAttrCustom.setType(geAttr.getTypeName());
            if (studyEventInfoLabels.contains(label)) {
                studyPvAttrCustom.setAllowedValues(study
                    .getStudyEventAttrPermissible(label));
                selected = true;
            }
            studyPvAttrCustom.setIsDefault(false);
            studyPvAttrCustom.widget = new PvInfoWidget(client, SWT.NONE,
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
        study.addToSourceSpecimenCollection(sourceSpecimenEntryTable
            .getAddedOrModifiedSourceSpecimens());
        study.removeFromSourceSpecimenCollection(sourceSpecimenEntryTable
            .getDeletedSourceSpecimens());

        // sample storage
        study.addToAliquotedSpecimenCollection(aliquotedSpecimenEntryTable
            .getAddedOrModifiedAliquotedSpecimens());
        study.removeFromAliquotedSpecimenCollection(aliquotedSpecimenEntryTable
            .getDeletedAliquotedSpecimens());
        study.persist();

        SessionManager.updateAllSimilarNodes(studyAdapter, true);
    }

    private void setStudyPvAttr() throws Exception, UserUIException {
        List<String> newPvInfoLabels = new ArrayList<String>();
        for (StudyPvAttrCustom studyPvAttrCustom : pvCustomInfoList) {
            String label = studyPvAttrCustom.getLabel();
            if (label.equals(DATE_PROCESSED_INFO_FIELD_NAME))
                continue;

            if (!studyPvAttrCustom.widget.getSelected()
                && studyPvAttrCustom.inStudy) {
                try {
                    study.deleteStudyEventAttr(studyPvAttrCustom.getLabel());
                } catch (BiobankCheckException e) {
                    throw new UserUIException(
                        "Cannot delete " //$NON-NLS-1$
                            + label
                            + " from study since it is already in use by patient visits.", //$NON-NLS-1$
                        e);
                }
            } else if (studyPvAttrCustom.widget.getSelected()) {
                newPvInfoLabels.add(studyPvAttrCustom.getLabel());
                String value = studyPvAttrCustom.widget.getValues();
                if (studyPvAttrCustom.getType() == EventAttrTypeEnum.SELECT_SINGLE
                    || studyPvAttrCustom.getType() == EventAttrTypeEnum.SELECT_MULTIPLE) { //$NON-NLS-1$
                    if (value.length() > 0) {
                        study.setStudyEventAttr(studyPvAttrCustom.getLabel(),
                            studyPvAttrCustom.getType(), value.split(";")); //$NON-NLS-1$
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
    protected void onReset() throws Exception {
        study.reset();

        if (study.isNew()) {
            study.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
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

        for (StudyPvAttrCustom studyPvAttrCustom : pvCustomInfoList) {
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
