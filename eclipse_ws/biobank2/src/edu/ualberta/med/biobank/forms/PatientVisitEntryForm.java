package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.widgets.ComboAndQuantityWidget;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.PvSampleSourceEntryWidget;
import edu.ualberta.med.biobank.widgets.SelectMultipleWidget;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public class PatientVisitEntryForm extends BiobankEntryForm {

    private static Logger LOGGER = Logger.getLogger(PatientVisitEntryForm.class
        .getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitEntryForm";

    public static final String MSG_NEW_PATIENT_VISIT_OK = "Creating a new patient visit record.";

    public static final String MSG_PATIENT_VISIT_OK = "Editing an existing patient visit record.";

    public static final String MSG_NO_VISIT_NUMBER = "Visit must have a number";

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisitWrapper patientVisit;

    private DateTimeWidget dateProcessed;

    private PatientWrapper patient;

    private class FormPvCustomInfo extends PvAttrCustom {
        Control control;
    }

    private List<FormPvCustomInfo> pvCustomInfoList;

    private ComboViewer shipmentsComboViewer;

    private PvSampleSourceEntryWidget pvSampleSourceEntryWidget;

    @Override
    public void init() {
        Assert.isTrue(adapter instanceof PatientVisitAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientVisitAdapter = (PatientVisitAdapter) adapter;
        patientVisit = patientVisitAdapter.getWrapper();
        patient = ((PatientAdapter) patientVisitAdapter.getParent())
            .getWrapper();
        retrieve();
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
            patientVisit.reload();
            patient.reload();
        } catch (Exception e) {
            LOGGER.error("Error while retrieving patient visit "
                + patientVisitAdapter.getWrapper().getFormattedDateProcessed()
                + " (Patient " + patientVisit.getPatient() + ")", e);
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Visit Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_PATIENT_VISIT));
        createMainSection();
        createSourcesSection();
        if (patientVisit.isNew()) {
            setDirty(true);
        }
    }

    private void createMainSection() throws Exception {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Text siteLabel = createReadOnlyField(client, SWT.NONE, "Site");
        setTextValue(siteLabel, patient.getStudy().getSite().getName());

        List<ShipmentWrapper> patientShipments = patient
            .getShipmentCollection();
        ShipmentWrapper selectedShip = patientVisit.getShipment();
        if (patientShipments.size() == 1) {
            selectedShip = patientShipments.get(0);
        }
        shipmentsComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Shipment", patientShipments, selectedShip,
            "A shipment should be selected");

        if (patientVisit.getDateProcessed() == null) {
            patientVisit.setDateProcessed(new Date());
        }
        dateProcessed = createDateTimeWidget(client, "Date Processed",
            patientVisit.getDateProcessed(), patientVisit, "dateProcessed",
            "Date processed should be set");
        firstControl = dateProcessed;

        createPvDataSection(client);

        createBoundWidgetWithLabel(client, Text.class, SWT.MULTI, "Comments",
            null, BeansObservables.observeValue(patientVisit, "comment"), null);
    }

    private void createSourcesSection() {
        Composite client = createSectionWithClient("Source Vessels");

        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        pvSampleSourceEntryWidget = new PvSampleSourceEntryWidget(client,
            SWT.NONE, patientVisit, toolkit);
        pvSampleSourceEntryWidget
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
        pvSampleSourceEntryWidget.addBinding(widgetCreator);

    }

    private void createPvDataSection(Composite client) throws Exception {
        StudyWrapper study = patient.getStudy();
        String[] labels = study.getStudyPvAttrLabels();
        if (labels == null)
            return;

        pvCustomInfoList = new ArrayList<FormPvCustomInfo>();

        for (String label : labels) {
            FormPvCustomInfo pvCustomInfo = new FormPvCustomInfo();
            pvCustomInfo.setLabel(label);
            pvCustomInfo.setType(study.getStudyPvAttrType(label));
            pvCustomInfo.setPermissible(study.getStudyPvAttrPermissible(label));
            pvCustomInfo.setValue(patientVisit.getPvAttrValue(label));
            pvCustomInfo.control = getControlForLabel(client, pvCustomInfo);
            pvCustomInfoList.add(pvCustomInfo);
        }
    }

    private Control getControlForLabel(Composite client,
        FormPvCustomInfo pvCustomInfo) {
        Control control;
        if (pvCustomInfo.getType().equals("number")) {
            control = createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
                pvCustomInfo.getLabel(), null, PojoObservables.observeValue(
                    pvCustomInfo, "value"), new DoubleNumberValidator(
                    "You should select a valid number"));
        } else if (pvCustomInfo.getType().equals("text")) {
            control = createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
                pvCustomInfo.getLabel(), null, PojoObservables.observeValue(
                    pvCustomInfo, "value"), null);
        } else if (pvCustomInfo.getType().equals("date_time")) {
            control = createDateTimeWidget(client, pvCustomInfo.getLabel(),
                DateFormatter.parseToDateTime(pvCustomInfo.getValue()), null,
                null, null);
        } else if (pvCustomInfo.getType().equals("select_single")) {
            control = createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
                pvCustomInfo.getLabel(), pvCustomInfo.getAllowedValues(),
                PojoObservables.observeValue(pvCustomInfo, "value"), null);
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
            Assert.isTrue(false, "Invalid pvInfo type: "
                + pvCustomInfo.getType());
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
    protected void saveForm() throws Exception {
        PatientAdapter patientAdapter = (PatientAdapter) patientVisitAdapter
            .getParent();
        patientVisit.setPatient(patientAdapter.getWrapper());

        IStructuredSelection shipSelection = (IStructuredSelection) shipmentsComboViewer
            .getSelection();
        if ((shipSelection != null) && (shipSelection.size() > 0)) {
            patientVisit.setShipment((ShipmentWrapper) shipSelection
                .getFirstElement());
        } else {
            patientVisit.setShipment((ShipmentWrapper) null);
        }

        patientVisit.addPvSampleSources(pvSampleSourceEntryWidget
            .getAddedPvSampleSources());
        patientVisit.removePvSampleSources(pvSampleSourceEntryWidget
            .getRemovedPvSampleSources());

        setPvCustomInfo();

        if (patientVisit.isNew()) {
            patientVisit.setUsername(SessionManager.getInstance().getSession()
                .getUserName());
        }
        patientVisit.persist();

        patientAdapter.performExpand();
    }

    private void setPvCustomInfo() throws Exception {
        for (FormPvCustomInfo combinedPvInfo : pvCustomInfoList) {
            setPvInfoValueFromControlType(combinedPvInfo);
            String value = combinedPvInfo.getValue();
            if (value == null)
                continue;

            patientVisit.setPvAttrValue(combinedPvInfo.getLabel(), value);
        }
    }

    private void setPvInfoValueFromControlType(FormPvCustomInfo pvCustomInfo) {
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
        super.reset();

        if (patientVisit.getDateProcessed() == null) {
            patientVisit.setDateProcessed(new Date());
        }
        pvSampleSourceEntryWidget.setSelectedPvSampleSources(patientVisit
            .getPvSampleSourceCollection());
        // FIXME also reset for pv infos
    }
}
