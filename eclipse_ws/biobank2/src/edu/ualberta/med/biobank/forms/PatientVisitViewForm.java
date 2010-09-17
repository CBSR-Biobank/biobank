package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.PatientVisitAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.AliquotListInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.PvSourceVesselInfoTable;

public class PatientVisitViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientVisitViewForm";

    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientVisitViewForm.class.getName());

    private PatientVisitAdapter patientVisitAdapter;

    private PatientVisitWrapper patientVisit;

    private BiobankText siteLabel;

    private BiobankText studyLabel;

    private AliquotListInfoTable aliquotWidget;

    private List<FormPvCustomInfo> pvCustomInfoList;

    private BiobankText clinicLabel;

    private BiobankText shipmentLabel;

    private BiobankText patientLabel;

    private BiobankText dateProcessedLabel;

    private BiobankText commentLabel;

    private BiobankText dateDrawnLabel;

    private PvSourceVesselInfoTable table;

    private class FormPvCustomInfo extends PvAttrCustom {
        BiobankText widget;
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof PatientVisitAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientVisitAdapter = (PatientVisitAdapter) adapter;
        patientVisit = patientVisitAdapter.getWrapper();
        retrievePatientVisit();
        patientVisit.logLookup(SessionManager.getInstance().getCurrentSite()
            .getNameShort());

        setPartName("Visit " + patientVisit.getFormattedDateProcessed());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Visit - Date Processed: "
            + patientVisit.getFormattedDateProcessed());
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createMainSection();
        createSourcesSection();
        createAliquotsSection();
    }

    private void createMainSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = createReadOnlyLabelledField(client, SWT.NONE, "Site");
        studyLabel = createReadOnlyLabelledField(client, SWT.NONE, "Study");
        clinicLabel = createReadOnlyLabelledField(client, SWT.NONE, "Clinic");
        shipmentLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Shipment");
        patientLabel = createReadOnlyLabelledField(client, SWT.NONE, "Patient");
        dateProcessedLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Date Processed");
        dateDrawnLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Date Drawn");

        createPvDataSection(client);

        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            "Comments");

        setPatientVisitValues();
    }

    private void createPvDataSection(Composite client) throws Exception {
        StudyWrapper study = patientVisit.getPatient().getStudy();
        String[] labels = study.getStudyPvAttrLabels();
        if (labels == null)
            return;

        pvCustomInfoList = new ArrayList<FormPvCustomInfo>();

        for (String label : labels) {
            FormPvCustomInfo combinedPvInfo = new FormPvCustomInfo();
            combinedPvInfo.setLabel(label);
            combinedPvInfo.setType(study.getStudyPvAttrType(label));

            int style = SWT.NONE;
            if (combinedPvInfo.getType().equals("select_multiple")) {
                style |= SWT.WRAP;
            }

            String value = patientVisit.getPvAttrValue(label);
            if (combinedPvInfo.getType().equals("select_multiple")
                && (value != null)) {
                combinedPvInfo.setValue(value.replace(';', '\n'));
            } else {
                combinedPvInfo.setValue(value);
            }

            combinedPvInfo.widget = createReadOnlyLabelledField(client, style,
                label, combinedPvInfo.getValue());
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            combinedPvInfo.widget.setLayoutData(gd);

            pvCustomInfoList.add(combinedPvInfo);
        }
    }

    private void setPatientVisitValues() {
        setTextValue(siteLabel, patientVisit.getShipment().getSite().getName());
        setTextValue(studyLabel, patientVisit.getPatient().getStudy().getName());
        setTextValue(clinicLabel, patientVisit.getShipment() == null ? ""
            : patientVisit.getShipment().getClinic().getName());
        setTextValue(shipmentLabel, patientVisit.getShipment().toString());
        setTextValue(patientLabel, patientVisit.getPatient().getPnumber());
        setTextValue(dateProcessedLabel,
            patientVisit.getFormattedDateProcessed());
        setTextValue(dateDrawnLabel, patientVisit.getFormattedDateDrawn());
        setTextValue(commentLabel, patientVisit.getComment());

        // assign PvInfo
        for (FormPvCustomInfo combinedPvInfo : pvCustomInfoList) {
            setTextValue(combinedPvInfo.widget, combinedPvInfo.getValue());
        }
    }

    private void createSourcesSection() {
        Composite client = createSectionWithClient("Source Vessels");
        table = new PvSourceVesselInfoTable(client,
            patientVisit.getPvSourceVesselCollection());
        table.adaptToToolkit(toolkit, true);
    }

    private void createAliquotsSection() {
        Composite parent = createSectionWithClient("Aliquots");
        aliquotWidget = new AliquotListInfoTable(parent,
            patientVisit.getAliquotCollection());
        aliquotWidget.adaptToToolkit(toolkit, true);
        aliquotWidget.setSelection(patientVisitAdapter.getSelectedAliquot());
        aliquotWidget.addDoubleClickListener(collectionDoubleClickListener);
    }

    @Override
    public void reload() {
        retrievePatientVisit();
        String date = patientVisit.getFormattedDateProcessed();
        setPartName("Visit " + date);
        form.setText("Visit Drawn Date: " + date);
        setPatientVisitValues();
        table.setCollection(patientVisit.getPvSourceVesselCollection());
        aliquotWidget.setCollection(patientVisit.getAliquotCollection());
    }

    private void retrievePatientVisit() {
        try {
            patientVisit.reload();
        } catch (Exception ex) {
            logger.error(
                "Error while retrieving patient visit "
                    + patientVisit.getFormattedDateProcessed() + "(patient "
                    + patientVisit.getPatient() + ")", ex);
        }
    }

}
