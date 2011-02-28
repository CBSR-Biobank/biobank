package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.ComboAndQuantityWidget;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.SelectMultipleWidget;
import edu.ualberta.med.biobank.widgets.infotables.entry.SourceVesselEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class CollectionEventEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(CollectionEventEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.CollectionEventEntryForm";

    public static final String MSG_NEW_PATIENT_VISIT_OK = "Creating a new patient visit record.";

    public static final String MSG_PATIENT_VISIT_OK = "Editing an existing patient visit record.";

    public static final String MSG_NO_VISIT_NUMBER = "Visit must have a number";

    private CollectionEventAdapter patientVisitAdapter;

    private CollectionEventWrapper patientVisit;

    private PatientWrapper patient;

    private class FormPvCustomInfo extends PvAttrCustom {
        private Control control;
    }

    private List<FormPvCustomInfo> pvCustomInfoList;

    private SourceVesselEntryInfoTable pvSourceVesseltable;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private DateTimeWidget dateDrawnWidget;

    private ComboViewer activityStatusComboViewer;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof CollectionEventAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientVisitAdapter = (CollectionEventAdapter) adapter;
        patientVisit = patientVisitAdapter.getWrapper();
        patient = patientVisit.getPatient();
        retrieve();
        try {
            patientVisit.logEdit(null);
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Log edit failed", e);
        }
        String tabName;
        if (patientVisit.isNew()) {
            tabName = "New Patient Visit";
            patientVisit.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else {
            tabName = "Visit " + patientVisit.getFormattedDateProcessed();
        }

        setPartName(tabName);
    }

    private void retrieve() {
        try {
            if (!patientVisit.isNew()) {
                patientVisit.reload();
            }
            patient.reload();
        } catch (Exception e) {
            logger.error("Error while retrieving patient visit "
                + patientVisitAdapter.getWrapper().getFormattedDateProcessed()
                + " (Patient " + patientVisit.getPatient() + ")", e);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Visit Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
        createSourcesSection();
        if (patientVisit.isNew()) {
            setDirty(true);
        }
    }

    private void createMainSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createReadOnlyLabelledField(client, SWT.NONE, "Study", patient
            .getStudy().getName());

        activityStatusComboViewer = createComboViewer(client,
            "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            patientVisit.getActivityStatus(),
            "Patient visit must have an activity status",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    patientVisit
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });
        if (patientVisit.getActivityStatus() != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                patientVisit.getActivityStatus()));
        }

        createReadOnlyLabelledField(client, SWT.NONE, "Patient",
            patient.getPnumber());

        dateDrawnWidget = createDateTimeWidget(client, "Date Drawn",
            patientVisit.getDateDrawn(), patientVisit, "dateDrawn",
            new NotNullValidator("Date Drawn should be set"));

        setFirstControl(dateDrawnWidget);

        createPvDataSection(client);

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, patientVisit, "comment", null);
    }

    private void createSourcesSection() {
        Section section = createSection("Source Vessels");
        pvSourceVesseltable = new SourceVesselEntryInfoTable(section,
            patientVisit.getSourceVesselCollection(true), "Add a Specimen",
            "Edit a Specimen");
        pvSourceVesseltable.adaptToToolkit(toolkit, true);
        pvSourceVesseltable.addSelectionChangedListener(listener);

        addSectionToolbar(section, "Add Source Vessel", new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                pvSourceVesseltable.addSourceVessel();
            }
        });
        section.setClient(pvSourceVesseltable);
    }

    private void createPvDataSection(Composite client) throws Exception {
        StudyWrapper study = patientVisit.getPatient().getStudy();
        String[] labels = study.getStudyPvAttrLabels();
        if (labels == null)
            return;

        pvCustomInfoList = new ArrayList<FormPvCustomInfo>();

        for (String label : labels) {
            FormPvCustomInfo pvCustomInfo = new FormPvCustomInfo();
            pvCustomInfo.setLabel(label);
            pvCustomInfo.setType(study.getStudyPvAttrType(label));
            pvCustomInfo.setAllowedValues(study
                .getStudyPvAttrPermissible(label));
            pvCustomInfo.setValue(patientVisit.getPvAttrValue(label));
            pvCustomInfo.control = getControlForLabel(client, pvCustomInfo);
            pvCustomInfoList.add(pvCustomInfo);
        }
    }

    private Control getControlForLabel(Composite client,
        FormPvCustomInfo pvCustomInfo) {
        Control control;
        if (pvCustomInfo.getType().equals("number")) {
            control = createBoundWidgetWithLabel(client, BiobankText.class,
                SWT.NONE, pvCustomInfo.getLabel(), null, pvCustomInfo, "value",
                new DoubleNumberValidator("You should select a valid number"));
        } else if (pvCustomInfo.getType().equals("text")) {
            control = createBoundWidgetWithLabel(client, BiobankText.class,
                SWT.NONE, pvCustomInfo.getLabel(), null, pvCustomInfo, "value",
                null);
        } else if (pvCustomInfo.getType().equals("date_time")) {
            control = createDateTimeWidget(client, pvCustomInfo.getLabel(),
                DateFormatter.parseToDateTime(pvCustomInfo.getValue()), null,
                null);
        } else if (pvCustomInfo.getType().equals("select_single")) {
            control = createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
                pvCustomInfo.getLabel(), pvCustomInfo.getAllowedValues(),
                pvCustomInfo, "value", null);
        } else if (pvCustomInfo.getType().equals("select_multiple")) {
            createFieldLabel(client, pvCustomInfo.getLabel());
            SelectMultipleWidget s = new SelectMultipleWidget(client,
                SWT.BORDER, pvCustomInfo.getAllowedValues(), selectionListener);
            s.adaptToToolkit(toolkit, true);
            if (pvCustomInfo.getValue() != null) {
                s.setSelections(pvCustomInfo.getValue().split(";"));
            }
            control = s;
        } else {
            Assert.isTrue(false,
                "Invalid pvInfo type: " + pvCustomInfo.getType());
            return null;
        }
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        control.setLayoutData(gd);
        return control;
    }

    private void createFieldLabel(Composite parent, String label) {
        Label labelWidget = toolkit.createLabel(parent, label + ":", SWT.LEFT);
        labelWidget.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
    }

    @Override
    protected String getOkMessage() {
        return (patientVisit.isNew()) ? MSG_NEW_PATIENT_VISIT_OK
            : MSG_PATIENT_VISIT_OK;
    }

    @Override
    protected void doBeforeSave() throws Exception {
        PatientAdapter patientAdapter = (PatientAdapter) patientVisitAdapter
            .getParent();
        if (patientAdapter != null)
            patientVisit.setPatient(patientAdapter.getWrapper());

        patientVisit.addToSourceVesselCollection(pvSourceVesseltable
            .getAddedOrModifiedSourceVessels());
        patientVisit.removeFromSourceVesselCollection(pvSourceVesseltable
            .getDeletedSourceVessels());
        savePvCustomInfo();
    }

    @Override
    protected void saveForm() throws Exception {
        patientVisit.persist();
        SessionManager.updateAllSimilarNodes(patientVisitAdapter, true);
    }

    private void savePvCustomInfo() throws Exception {
        for (FormPvCustomInfo combinedPvInfo : pvCustomInfoList) {
            savePvInfoValueFromControlType(combinedPvInfo);
            String value = combinedPvInfo.getValue();
            if (value == null)
                continue;

            patientVisit.setPvAttrValue(combinedPvInfo.getLabel(), value);
        }
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
            pvCustomInfo.setValue(StringUtils.join(values, ";"));
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return CollectionEventViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        PatientWrapper patient = patientVisit.getPatient();
        patient.reload();
        patientVisit.reload();
        super.reset();
        patientVisit.setPatient(patient);

        if (patientVisit.getDateDrawn() == null) {
            dateDrawnWidget.setDate(null);
        }
        pvSourceVesseltable.reload();
        resetPvCustomInfo();

        ActivityStatusWrapper activity = patientVisit.getActivityStatus();
        if (activity != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                activity));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }
    }

    private void resetPvCustomInfo() throws Exception {
        StudyWrapper study = patientVisit.getPatient().getStudy();
        String[] labels = study.getStudyPvAttrLabels();
        if (labels == null)
            return;

        for (FormPvCustomInfo pvCustomInfo : pvCustomInfoList) {
            pvCustomInfo.setValue(patientVisit.getPvAttrValue(pvCustomInfo
                .getLabel()));
            if (pvCustomInfo.getType().equals("date_time")) {
                DateTimeWidget dateWidget = (DateTimeWidget) pvCustomInfo.control;
                dateWidget.setDate(DateFormatter.parseToDateTime(pvCustomInfo
                    .getValue()));
            } else if (pvCustomInfo.getType().equals("select_multiple")) {
                SelectMultipleWidget s = (SelectMultipleWidget) pvCustomInfo.control;
                if (pvCustomInfo.getValue() != null) {
                    s.setSelections(pvCustomInfo.getValue().split(";"));
                } else
                    s.setSelections(new String[] {});
            }
        }
    }
}
