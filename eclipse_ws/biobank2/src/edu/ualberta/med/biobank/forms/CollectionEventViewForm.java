package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTableOld;

public class CollectionEventViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CollectionEventViewForm";

    private static BiobankLogger logger = BiobankLogger
        .getLogger(CollectionEventViewForm.class.getName());

    private CollectionEventAdapter patientVisitAdapter;

    private CollectionEventWrapper cevent;

    private BiobankText studyLabel;

    private List<FormPvCustomInfo> pvCustomInfoList;

    private BiobankText patientLabel;

    private BiobankText dateProcessedLabel;

    @SuppressWarnings("unused")
    private BiobankText commentLabel;

    private SpecimenInfoTableOld table;

    private class FormPvCustomInfo extends PvAttrCustom {
        BiobankText widget;
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof CollectionEventAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientVisitAdapter = (CollectionEventAdapter) adapter;
        cevent = patientVisitAdapter.getWrapper();
        retrievePatientVisit();
        cevent.logLookup(SessionManager.getUser().getCurrentWorkingCentre()
            .getNameShort());

        setPartName("Visit " + cevent.getVisitNumber());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Visit - Date Processed: "
            + cevent.getVisitNumber());
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createMainSection();
        createSpecimensSection();
    }

    private void createMainSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        studyLabel = createReadOnlyLabelledField(client, SWT.NONE, "Study");
        patientLabel = createReadOnlyLabelledField(client, SWT.NONE, "Patient");
        dateProcessedLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Date Processed");

        createPvDataSection(client);

        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            "Comments");

        setCollectionEventValues();
    }

    private void createPvDataSection(Composite client) throws Exception {
        StudyWrapper study = cevent.getPatient().getStudy();
        String[] labels = study.getStudyEventAttrLabels();
        if (labels == null)
            return;

        pvCustomInfoList = new ArrayList<FormPvCustomInfo>();

        for (String label : labels) {
            FormPvCustomInfo combinedPvInfo = new FormPvCustomInfo();
            combinedPvInfo.setLabel(label);
            combinedPvInfo.setType(study.getStudyEventAttrType(label));

            int style = SWT.NONE;
            if (combinedPvInfo.getType().equals("select_multiple")) {
                style |= SWT.WRAP;
            }

            String value = cevent.getEventAttrValue(label);
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

    private void setCollectionEventValues() {
        setTextValue(studyLabel, cevent.getPatient().getStudy().getName());
        setTextValue(patientLabel, cevent.getPatient().getPnumber());
        setTextValue(dateProcessedLabel, cevent.getVisitNumber());

        // assign PvInfo
        for (FormPvCustomInfo combinedPvInfo : pvCustomInfoList) {
            setTextValue(combinedPvInfo.widget, combinedPvInfo.getValue());
        }
    }

    private void createSpecimensSection() {
        Composite client = createSectionWithClient("Source Vessels");
        table = new SpecimenInfoTableOld(client, cevent.getSpecimenCollection());
        table.adaptToToolkit(toolkit, true);
    }

    @Override
    public void reload() {
        retrievePatientVisit();
        String date = cevent.getVisitNumber().toString();
        setPartName("Visit " + date);
        form.setText("Visit Drawn Date: " + date);
        setCollectionEventValues();

        // FIXME: load tables
        // table.setCollection(cevent.getPvSourceVesselCollection());
        // aliquotWidget.setCollection(cevent.getSpecimenCollection());
    }

    private void retrievePatientVisit() {
        try {
            cevent.reload();
        } catch (Exception ex) {
            logger.error(
                "Error while retrieving patient visit "
                    + cevent.getVisitNumber() + "(patient "
                    + cevent.getPatient() + ")", ex);
        }
    }

}
