package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.IntegerNumberValidator;
import edu.ualberta.med.biobank.widgets.ComboAndQuantityWidget;
import edu.ualberta.med.biobank.widgets.SelectMultipleWidget;
import edu.ualberta.med.biobank.widgets.infotables.entry.CEventSpecimenEntryInfoTable;

public class CollectionEventEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CollectionEventEntryForm"; //$NON-NLS-1$

    public static final String MSG_NEW_PATIENT_VISIT_OK = Messages.CollectionEventEntryForm_creation_msg;

    public static final String MSG_PATIENT_VISIT_OK = Messages.CollectionEventEntryForm_edition_msg;

    private CollectionEventAdapter ceventAdapter;

    // will use something else ? an action object ? a ceventData ?
    private CollectionEvent cevent;

    private Patient patient;

    private static class FormPvCustomInfo extends PvAttrCustom {
        private Control control;
    }

    private List<FormPvCustomInfo> pvCustomInfoList;

    private BgcEntryFormWidgetListener listener = new BgcEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private ComboViewer activityStatusComboViewer;

    private CEventSpecimenEntryInfoTable specimensTable;
    private BgcBaseText visitNumberText;

    private DateTimeWidget timeDrawnWidget;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof CollectionEventAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        ceventAdapter = (CollectionEventAdapter) adapter;
        // load cevent infos for entry form
        // cevent = ceventAdapter.getModelObject();
        patient = cevent.getPatient();

        // SessionManager.logEdit(cevent);
        String tabName;
        // FIXME
        // if (cevent.isNew()) {
        // tabName = Messages.CollectionEventEntryForm_title_new;
        // cevent.setActivityStatus(ActivityStatusWrapper
        // .getActiveActivityStatus(appService));
        // cevent.setVisitNumber(PatientWrapper.getNextVisitNumber(appService,
        // cevent.getPatient()));
        // } else {
        tabName = NLS.bind(Messages.CollectionEventEntryForm_title_edit,
            cevent.getVisitNumber());
        // }

        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.CollectionEventEntryForm_main_title);
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
        createSpecimensSection();
        // FIXME
        // if (cevent.isNew()) {
        // setDirty(true);
        // }
    }

    private void createMainSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createReadOnlyLabelledField(client, SWT.NONE,
            Messages.CollectionEventEntryForm_field_study_label, patient
                .getStudy().getName());

        createReadOnlyLabelledField(client, SWT.NONE,
            Messages.CollectionEventEntryForm_field_patient_label,
            patient.getPnumber());

        visitNumberText = (BgcBaseText) createBoundWidgetWithLabel(
            client,
            BgcBaseText.class,
            SWT.NONE,
            Messages.CollectionEventEntryForm_field_visitNumber_label,
            null,
            cevent,
            CollectionEventPeer.VISIT_NUMBER.getName(),
            new IntegerNumberValidator(
                Messages.CollectionEventEntryForm_field_visitNumber_validation_msg,
                false));

        visitNumberText.addSelectionChangedListener(listener);
        setFirstControl(visitNumberText);

        activityStatusComboViewer = createComboViewer(client,
            Messages.label_activity,
            ActivityStatusWrapper.getAllActivityStatuses(SessionManager
                .getAppService()),
            new ActivityStatusWrapper(null, cevent.getActivityStatus()),
            Messages.CollectionEventEntryForm_field_activity_validation_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    if (!selectedObject.equals(cevent.getActivityStatus()))
                        setDirty(true);
                    cevent
                        .setActivityStatus(((ActivityStatusWrapper) selectedObject)
                            .getWrappedObject());
                }
            });

        widgetCreator.createLabel(client,
            Messages.CollectionEventEntryForm_timeDrawn_label);
        timeDrawnWidget = new DateTimeWidget(client, SWT.DATE | SWT.TIME,
            new Date());
        toolkit.adapt(timeDrawnWidget);

        createPvDataSection(client);

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            Messages.label_comments, null, cevent,
            CollectionEventPeer.COMMENT.getName(), null);
    }

    private void createSpecimensSection() {
        Section section = createSection(Messages.CollectionEventEntryForm_specimens_title);
        // FIXME
        // specimensTable = new CEventSpecimenEntryInfoTable(section,
        // cevent.getOriginalSpecimenCollection(true),
        // ColumnsShown.SOURCE_SPECIMENS);
        // specimensTable.adaptToToolkit(toolkit, true);
        // specimensTable.addSelectionChangedListener(listener);
        //
        // try {
        // final List<SpecimenTypeWrapper> allSpecimenTypes =
        // SpecimenTypeWrapper
        // .getAllSpecimenTypes(SessionManager.getAppService(), true);
        // specimensTable.addEditSupport(cevent.getPatient().getStudy()
        // .getSourceSpecimenCollection(true), allSpecimenTypes);
        // addSectionToolbar(section,
        // Messages.CollectionEventEntryForm_specimens_add_title,
        // new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // form.setFocus();
        // specimensTable.addOrEditSpecimen(true, null, cevent
        // .getPatient().getStudy()
        // .getSourceSpecimenCollection(true),
        // allSpecimenTypes, cevent, timeDrawnWidget.getDate());
        // }
        // });
        // } catch (ApplicationException e) {
        // BgcPlugin.openAsyncError(
        // Messages.CollectionEventEntryForm_specimenstypes_error_msg, e);
        // }
        section.setClient(specimensTable);
    }

    private void createPvDataSection(Composite client) throws Exception {
        StudyWrapper study = new StudyWrapper(SessionManager.getAppService(),
            cevent.getPatient().getStudy());
        String[] labels = study.getStudyEventAttrLabels();
        if (labels == null)
            return;

        pvCustomInfoList = new ArrayList<FormPvCustomInfo>();

        // FIXME
        // for (String label : labels) {
        // FormPvCustomInfo pvCustomInfo = new FormPvCustomInfo();
        // pvCustomInfo.setLabel(label);
        // pvCustomInfo.setType(study.getStudyEventAttrType(label));
        // pvCustomInfo.setAllowedValues(study
        // .getStudyEventAttrPermissible(label));
        // pvCustomInfo.setValue(cevent.getEventAttrValue(label));
        // pvCustomInfo.control = getControlForLabel(client, pvCustomInfo);
        // pvCustomInfoList.add(pvCustomInfo);
        // }
    }

    private Control getControlForLabel(Composite client,
        FormPvCustomInfo pvCustomInfo) {
        Control control;
        if (EventAttrTypeEnum.NUMBER == pvCustomInfo.getType()) {
            control = createBoundWidgetWithLabel(client, BgcBaseText.class,
                SWT.NONE, pvCustomInfo.getLabel(), null, pvCustomInfo,
                FormPvCustomInfo.VALUE_BIND_STRING, new DoubleNumberValidator(
                    Messages.CollectionEventEntryForm_number_validation_msg));
        } else if (EventAttrTypeEnum.TEXT == pvCustomInfo.getType()) {
            control = createBoundWidgetWithLabel(client, BgcBaseText.class,
                SWT.NONE, pvCustomInfo.getLabel(), null, pvCustomInfo,
                FormPvCustomInfo.VALUE_BIND_STRING, null);
        } else if (EventAttrTypeEnum.DATE_TIME == pvCustomInfo.getType()) {
            control = createDateTimeWidget(client, pvCustomInfo.getLabel(),
                DateFormatter.parseToDateTime(pvCustomInfo.getValue()), null,
                null);
        } else if (EventAttrTypeEnum.SELECT_SINGLE == pvCustomInfo.getType()) {
            control = createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
                pvCustomInfo.getLabel(), pvCustomInfo.getAllowedValues(),
                pvCustomInfo, FormPvCustomInfo.VALUE_BIND_STRING, null);
        } else if (EventAttrTypeEnum.SELECT_MULTIPLE == pvCustomInfo.getType()) {
            widgetCreator.createLabel(client, pvCustomInfo.getLabel());
            SelectMultipleWidget s = new SelectMultipleWidget(client,
                SWT.BORDER, pvCustomInfo.getAllowedValues(), selectionListener);
            s.adaptToToolkit(toolkit, true);
            if (pvCustomInfo.getValue() != null) {
                s.setSelections(pvCustomInfo.getValue().split(
                    FormPvCustomInfo.VALUE_MULTIPLE_SEPARATOR));
            }
            control = s;
        } else {
            Assert.isTrue(false,
                "Invalid pvInfo type: " + pvCustomInfo.getType()); //$NON-NLS-1$
            return null;
        }
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        control.setLayoutData(gd);
        return control;
    }

    @Override
    protected String getOkMessage() {
        return "default ok";
        // FIXME
        // return (cevent.isNew()) ? MSG_NEW_PATIENT_VISIT_OK
        // : MSG_PATIENT_VISIT_OK;
    }

    @Override
    protected void doBeforeSave() throws Exception {
        PatientAdapter patientAdapter = (PatientAdapter) ceventAdapter
            .getParent();
        // FIXME this should be done before the entry form is opened.
        // if (patientAdapter != null)
        // cevent.setPatient(patientAdapter.getModelObject());

        // ACTIOn ? Presenter?
        // cevent.addToOriginalSpecimenCollection(specimensTable
        // .getAddedOrModifiedSpecimens());
        // cevent.removeFromOriginalSpecimenCollection(specimensTable
        // .getRemovedSpecimens());
        savePvCustomInfo();
    }

    @Override
    protected void saveForm() throws Exception {
        // FIXME need to use batchquery for OriginInfo + Collection Event

        // create the origin info to be used
        if (specimensTable.getAddedOrModifiedSpecimens().size() > 0) {
            OriginInfoWrapper originInfo = new OriginInfoWrapper(
                SessionManager.getAppService());
            originInfo.setCenter(SessionManager.getUser()
                .getCurrentWorkingCenter());
            originInfo.persist();
            for (SpecimenWrapper spec : specimensTable
                .getAddedOrModifiedSpecimens()) {
                spec.setOriginInfo(originInfo);
            }
        }
        // save the collection event
        // FIXME action !
        // cevent.persist();
        // SessionManager.updateAllSimilarNodes(ceventAdapter, true);
    }

    private void savePvCustomInfo() throws Exception {
        // FIXME
        // for (FormPvCustomInfo combinedPvInfo : pvCustomInfoList) {
        // savePvInfoValueFromControlType(combinedPvInfo);
        // String value = combinedPvInfo.getValue();
        // if (value == null)
        // continue;
        //
        // cevent.setEventAttrValue(combinedPvInfo.getLabel(), value);
        // }
    }

    private void savePvInfoValueFromControlType(FormPvCustomInfo pvCustomInfo) {
        // for text and combo, the databinding is used
        if (pvCustomInfo.control instanceof DateTimeWidget) {
            pvCustomInfo.setValue(((DateTimeWidget) pvCustomInfo.control)
                .getText());
        } else if (pvCustomInfo.control instanceof ComboAndQuantityWidget) {
            pvCustomInfo
                .setValue(((ComboAndQuantityWidget) pvCustomInfo.control)
                    .getText());
        } else if (pvCustomInfo.control instanceof SelectMultipleWidget) {
            String[] values = ((SelectMultipleWidget) pvCustomInfo.control)
                .getSelections();
            pvCustomInfo.setValue(StringUtils.join(values,
                FormPvCustomInfo.VALUE_MULTIPLE_SEPARATOR));
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return CollectionEventViewForm.ID;
    }

    @Override
    public void reset() {
        super.reset();
        // FIXME
        // if (cevent.isNew())
        // // because we set the visit number and the activity status default
        // setDirty(true);
    }

    @Override
    protected void onReset() throws Exception {
        // FIXME
        // cevent.reset();
        // cevent.setPatient(patient);
        //
        // if (cevent.isNew()) {
        // cevent.setActivityStatus(ActivityStatusWrapper
        // .getActiveActivityStatus(SessionManager.getAppService()));
        // Integer next = PatientWrapper.getNextVisitNumber(
        // SessionManager.getAppService(), cevent.getPatient());
        // cevent.setVisitNumber(next);
        // }
        //
        // patient.reset();
        //
        // GuiUtil.reset(activityStatusComboViewer, cevent.getActivityStatus());
        //
        // specimensTable.reload(cevent.getOriginalSpecimenCollection(true));
        // resetPvCustomInfo();
    }

    private void resetPvCustomInfo() throws Exception {
        // FIXME
        // StudyWrapper study = cevent.getPatient().getStudy();
        // String[] labels = study.getStudyEventAttrLabels();
        // if (labels == null)
        // return;
        //
        // for (FormPvCustomInfo pvCustomInfo : pvCustomInfoList) {
        // pvCustomInfo.setValue(cevent.getEventAttrValue(pvCustomInfo
        // .getLabel()));
        // if (EventAttrTypeEnum.DATE_TIME == pvCustomInfo.getType()) {
        // DateTimeWidget dateWidget = (DateTimeWidget) pvCustomInfo.control;
        // dateWidget.setDate(DateFormatter.parseToDateTime(pvCustomInfo
        // .getValue()));
        // } else if (EventAttrTypeEnum.SELECT_MULTIPLE == pvCustomInfo
        // .getType()) {
        // SelectMultipleWidget s = (SelectMultipleWidget) pvCustomInfo.control;
        // if (pvCustomInfo.getValue() != null) {
        // s.setSelections(pvCustomInfo.getValue().split(
        // FormPvCustomInfo.VALUE_MULTIPLE_SEPARATOR));
        // } else
        // s.setSelections(new String[] {});
        // }
        // }
    }
}
