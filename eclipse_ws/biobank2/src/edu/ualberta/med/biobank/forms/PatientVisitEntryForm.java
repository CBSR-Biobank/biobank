package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ClinicShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.validators.DateNotNulValidator;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.ComboAndQuantityWidget;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.SelectMultipleWidget;
import edu.ualberta.med.biobank.widgets.infotables.entry.PvSourceVesselEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class PatientVisitEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientVisitEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitEntryForm";

    public static final String MSG_NEW_PATIENT_VISIT_OK = "Creating a new patient visit record.";

    public static final String MSG_PATIENT_VISIT_OK = "Editing an existing patient visit record.";

    public static final String MSG_NO_VISIT_NUMBER = "Visit must have a number";

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisitWrapper patientVisit;

    private PatientWrapper patient;

    private class FormPvCustomInfo extends PvAttrCustom {
        private Control control;
    }

    private List<FormPvCustomInfo> pvCustomInfoList;

    private ComboViewer shipmentsComboViewer;

    private PvSourceVesselEntryInfoTable pvSourceVesseltable;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private List<ClinicShipmentWrapper> allShipments;

    private ArrayList<ClinicShipmentWrapper> recentShipments;

    @Override
    public void init() {
        Assert.isTrue(adapter instanceof PatientVisitAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientVisitAdapter = (PatientVisitAdapter) adapter;
        patientVisit = patientVisitAdapter.getWrapper();
        patient = patientVisit.getPatient();
        retrieve();
        try {
            patientVisit.logEdit(SessionManager.getInstance().getCurrentSite()
                .getNameShort());
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Log edit failed", e);
        }
        String tabName;
        if (patientVisit.isNew()) {
            tabName = "New Patient Visit";
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

        SiteWrapper site = SessionManager.getInstance().getCurrentSite();

        createReadOnlyLabelledField(client, SWT.NONE, "Site", site.getName());

        createReadOnlyLabelledField(client, SWT.NONE, "Study", patient
            .getStudy().getName());

        createReadOnlyLabelledField(client, SWT.NONE, "Patient",
            patient.getPnumber());

        createShipmentsCombo(client);

        if (patientVisit.getDateProcessed() == null) {
            patientVisit.setDateProcessed(new Date());
        }
        createDateTimeWidget(client, "Date Processed",
            patientVisit.getDateProcessed(), patientVisit, "dateProcessed",
            new DateNotNulValidator("Date processed should be set"));

        createDateTimeWidget(client, "Date Drawn", patientVisit.getDateDrawn(),
            patientVisit, "dateDrawn", new DateNotNulValidator(
                "Date Drawn should be set"));

        createPvDataSection(client);

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, patientVisit, "comment", null);
    }

    private void createShipmentsCombo(Composite client) {
        ClinicShipmentWrapper selectedShip = initShipmentsCollections();

        Label label = widgetCreator.createLabel(client, "Shipment");

        Composite composite = new Composite(client, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        composite.setLayoutData(gd);
        toolkit.adapt(composite);
        toolkit.paintBordersFor(composite);

        shipmentsComboViewer = widgetCreator.createComboViewer(composite,
            label, recentShipments, selectedShip,
            "A shipment should be selected", false, null,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    patientVisit
                        .setShipment((ClinicShipmentWrapper) selectedObject);
                }
            });
        setFirstControl(shipmentsComboViewer.getControl());

        if (SessionManager.getUser().isWebsiteAdministrator()) {
            final Button shipmentsListCheck = toolkit.createButton(composite,
                "Last 7 days", SWT.CHECK);
            shipmentsListCheck.setSelection(true);
            shipmentsListCheck.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    ISelection currentSelection = shipmentsComboViewer
                        .getSelection();
                    if (shipmentsListCheck.getSelection()) {
                        shipmentsComboViewer.setInput(recentShipments);
                    } else {
                        shipmentsComboViewer.setInput(allShipments);
                    }
                    shipmentsComboViewer.setSelection(currentSelection);
                }
            });
        }
    }

    private ClinicShipmentWrapper initShipmentsCollections() {
        allShipments = patient.getShipmentCollection(true, false);
        recentShipments = new ArrayList<ClinicShipmentWrapper>();
        // filter for last 7 days
        Calendar c = Calendar.getInstance();
        for (ClinicShipmentWrapper shipment : allShipments) {
            c.setTime(shipment.getDateReceived());
            c.add(Calendar.DAY_OF_MONTH, 7);
            if (c.getTime().after(new Date()))
                recentShipments.add(shipment);
        }
        ClinicShipmentWrapper selectedShip = null;
        if (!patientVisit.isNew()) {
            selectedShip = patientVisit.getShipment();
            // need to add into the list, to be able to see it.
            recentShipments.add(selectedShip);
        } else if (recentShipments.size() == 1) {
            selectedShip = recentShipments.get(0);
        }
        return selectedShip;
    }

    private void createSourcesSection() {
        Section section = createSection("Source Vessels");
        pvSourceVesseltable = new PvSourceVesselEntryInfoTable(section,
            patientVisit);
        pvSourceVesseltable.adaptToToolkit(toolkit, true);
        pvSourceVesseltable.addSelectionChangedListener(listener);
        pvSourceVesseltable.addBinding(widgetCreator);

        addSectionToolbar(section, "Add Source Vessel", new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                pvSourceVesseltable.addPvSourceVessel();
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
        IObservableValue valueObserved = BiobankFormBase.observeValue(
            pvCustomInfo, "value");
        if (pvCustomInfo.getType().equals("number")) {
            control = createBoundWidgetWithLabel(client, BiobankText.class,
                SWT.NONE, pvCustomInfo.getLabel(), null, valueObserved,
                new DoubleNumberValidator("You should select a valid number"));
        } else if (pvCustomInfo.getType().equals("text")) {
            control = createBoundWidgetWithLabel(client, BiobankText.class,
                SWT.NONE, pvCustomInfo.getLabel(), null, valueObserved, null);
        } else if (pvCustomInfo.getType().equals("date_time")) {
            control = createDateTimeWidget(client, pvCustomInfo.getLabel(),
                DateFormatter.parseToDateTime(pvCustomInfo.getValue()), null,
                null);
        } else if (pvCustomInfo.getType().equals("select_single")) {
            control = createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
                pvCustomInfo.getLabel(), pvCustomInfo.getAllowedValues(),
                valueObserved, null);
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
        patientVisit.setPatient(patientAdapter.getWrapper());

        patientVisit.addPvSourceVessels(pvSourceVesseltable
            .getAddedPvSourceVessels());
        patientVisit.removePvSourceVessels(pvSourceVesseltable
            .getRemovedPvSourceVessels());
        savePvCustomInfo();
    }

    @Override
    protected void saveForm() throws Exception {
        patientVisit.persist();
        PatientAdapter patientAdapter = (PatientAdapter) patientVisitAdapter
            .getParent();
        patientAdapter.performExpand();
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
        return PatientVisitViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        PatientWrapper patient = patientVisit.getPatient();
        patient.reload();
        patientVisit.reload();
        patientVisit.setPatient(patient);
        super.reset();

        if (patientVisit.getDateProcessed() == null) {
            patientVisit.setDateProcessed(new Date());
        }
        pvSourceVesseltable.reload();
        resetPvCustomInfo();
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
                }
            }
        }
    }
}
