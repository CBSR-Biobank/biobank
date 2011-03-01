package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.ComboAndQuantityWidget;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.SelectMultipleWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class CollectionEventEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(CollectionEventEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.CollectionEventEntryForm";

    public static final String MSG_NEW_PATIENT_VISIT_OK = "Creating a new patient visit record.";

    public static final String MSG_PATIENT_VISIT_OK = "Editing an existing patient visit record.";

    public static final String MSG_NO_VISIT_NUMBER = "Visit must have a number";

    private CollectionEventAdapter ceventAdapter;

    private CollectionEventWrapper cevent;

    private PatientWrapper patient;

    private class FormPvCustomInfo extends PvAttrCustom {
        private Control control;
    }

    private List<FormPvCustomInfo> pvCustomInfoList;

    private SpecimenEntryWidget specimensWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private ComboViewer activityStatusComboViewer;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof CollectionEventAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        ceventAdapter = (CollectionEventAdapter) adapter;
        cevent = ceventAdapter.getWrapper();
        patient = cevent.getPatient();
        retrieve();
        patient = cevent.getPatient();
        try {
            cevent.logEdit(null);
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Log edit failed", e);
        }
        String tabName;
        if (cevent.isNew()) {
            tabName = "New Collection Event";
        } else {
            tabName = "Collection Event ";
        }

        setPartName(tabName);
    }

    private void retrieve() {
        try {
            if (!cevent.isNew()) {
                cevent.reload();
            }
        } catch (Exception e) {
            logger.error(
                "Error while retrieving patient visit "
                    + cevent.getVisitNumber() + " (Patient "
                    + cevent.getPatient() + ")", e);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Visit Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
        createSourceSpecimensSection();
        if (cevent.isNew()) {
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

        createReadOnlyLabelledField(client, SWT.NONE, "Patient",
            patient.getPnumber());

        // FIXME first control should be patient visit number
        // setFirstControl(dateDrawnWidget);

        createPvDataSection(client);

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, cevent, "comment", null);
    }

    private void createSourceSpecimensSection() {
        // FIXME: specimens should be diplayed here
        // Section section = createSection("Source Vessels");
        // pvSourceVesseltable = new SourceVesselEntryInfoTable(section,
        // cevent.getSpecimenCollection(true), "Add a Specimen",
        // "Edit a Specimen");
        // pvSourceVesseltable.adaptToToolkit(toolkit, true);
        // pvSourceVesseltable.addSelectionChangedListener(listener);
        //
        // addSectionToolbar(section, "Add Source Vessel", new
        // SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // pvSourceVesseltable.addSourceVessel();
        // }
        // });
        // section.setClient(pvSourceVesseltable);
    }

    private void createPvDataSection(Composite client) throws Exception {
        StudyWrapper study = cevent.getPatient().getStudy();
        String[] labels = study.getStudyEventAttrLabels();
        if (labels == null)
            return;

        pvCustomInfoList = new ArrayList<FormPvCustomInfo>();

        for (String label : labels) {
            FormPvCustomInfo pvCustomInfo = new FormPvCustomInfo();
            pvCustomInfo.setLabel(label);
            pvCustomInfo.setType(study.getStudyEventAttrType(label));
            pvCustomInfo.setAllowedValues(study
                .getStudyEventAttrPermissible(label));
            pvCustomInfo.setValue(cevent.getEventAttrValue(label));
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
        return (cevent.isNew()) ? MSG_NEW_PATIENT_VISIT_OK
            : MSG_PATIENT_VISIT_OK;
    }

    @Override
    protected void doBeforeSave() throws Exception {
        PatientAdapter patientAdapter = (PatientAdapter) ceventAdapter
            .getParent();
        if (patientAdapter != null)
            cevent.setPatient(patientAdapter.getWrapper());

        // FIXME should be source specimens
        // cevent.addToSourceVesselCollection(pvSourceVesseltable
        // .getAddedOrModifiedSourceVessels());
        // cevent.removeFromSourceVesselCollection(pvSourceVesseltable
        // .getDeletedSourceVessels());
        savePvCustomInfo();
    }

    @Override
    protected void saveForm() throws Exception {
        cevent.persist();
        SessionManager.updateAllSimilarNodes(ceventAdapter, true);
    }

    private void savePvCustomInfo() throws Exception {
        for (FormPvCustomInfo combinedPvInfo : pvCustomInfoList) {
            savePvInfoValueFromControlType(combinedPvInfo);
            String value = combinedPvInfo.getValue();
            if (value == null)
                continue;

            cevent.setEventAttrValue(combinedPvInfo.getLabel(), value);
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
        PatientWrapper patient = cevent.getPatient();
        patient.reload();
        cevent.reload();
        super.reset();
        cevent.setPatient(patient);

        specimensWidget.updateList();
        resetPvCustomInfo();
    }

    private void resetPvCustomInfo() throws Exception {
        StudyWrapper study = cevent.getPatient().getStudy();
        String[] labels = study.getStudyEventAttrLabels();
        if (labels == null)
            return;

        for (FormPvCustomInfo pvCustomInfo : pvCustomInfoList) {
            pvCustomInfo.setValue(cevent.getEventAttrValue(pvCustomInfo
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
