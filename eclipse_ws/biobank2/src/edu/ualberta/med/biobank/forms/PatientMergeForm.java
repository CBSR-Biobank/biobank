package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.AbstractInfoTableWidget;
import edu.ualberta.med.biobank.widgets.infotables.ClinicVisitInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientMergeForm extends BiobankEntryForm {

    @SuppressWarnings("unused")
    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientMergeForm.class.getName());

    public static final String ID =
        "edu.ualberta.med.biobank.forms.PatientMergeForm";

    public static final String MSG_PATIENT_NOT_VALID =
        "Select a second patient";

    private PatientAdapter patient1Adapter;

    private PatientWrapper patient1;

    private PatientWrapper patient2;

    private BiobankText study2Text;

    private ClinicVisitInfoTable patient2VisitsTable;

    private BiobankText pnumber2Text;

    private BiobankText pnumber1Text;

    private BiobankText study1Text;

    private IObservableValue patientNotNullValue;

    private AbstractInfoTableWidget<PatientVisitWrapper> patient1VisitsTable;

    private boolean canMerge;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patient1Adapter = (PatientAdapter) adapter;
        patient1 = patient1Adapter.getWrapper();
        String tabName = "Merging Patient " + patient1.getPnumber();
        setPartName(tabName);
        patientNotNullValue = new WritableValue(Boolean.FALSE, Boolean.class);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), patientNotNullValue, MSG_PATIENT_NOT_VALID);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Merging into Patient " + patient1.getPnumber());
        page.setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_PATIENT));

        toolkit.createLabel(
            page,
            "Select Patient Number to merge into Patient "
                + patient1.getPnumber() + " and press Enter", SWT.LEFT);

        createPatientSection();
    }

    private void createPatientSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout toplayout = new GridLayout(3, false);
        toplayout.horizontalSpacing = 10;
        client.setLayout(toplayout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Composite patientArea1 = toolkit.createComposite(client);
        GridLayout patient1Layout = new GridLayout(2, false);
        patient1Layout.horizontalSpacing = 10;
        patientArea1.setLayout(patient1Layout);
        GridData patient1Data = new GridData();
        patient1Data.grabExcessHorizontalSpace = true;
        patient1Data.horizontalAlignment = SWT.FILL;
        patient1Data.verticalAlignment = SWT.FILL;
        patientArea1.setLayoutData(patient1Data);

        Label arrow = toolkit.createLabel(client, "Arrow", SWT.IMAGE_BMP);
        arrow.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_ARROW_LEFT2));

        Composite patientArea2 = toolkit.createComposite(client);
        GridLayout patient2Layout = new GridLayout(2, false);
        patient2Layout.horizontalSpacing = 10;
        patientArea2.setLayout(patient2Layout);
        GridData patient2Data = new GridData();
        patient2Data.grabExcessHorizontalSpace = true;
        patient2Data.horizontalAlignment = SWT.FILL;
        patient2Data.verticalAlignment = SWT.FILL;
        patientArea2.setLayoutData(patient2Data);
        toolkit.paintBordersFor(client);

        pnumber1Text =
            createReadOnlyLabelledField(patientArea1, SWT.NONE,
                "Patient Number");
        pnumber1Text.setText(patient1Adapter.getWrapper().getPnumber());

        pnumber2Text =
            (BiobankText) createLabelledWidget(patientArea2, BiobankText.class,
                SWT.NONE, "Patient Number");
        pnumber2Text.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                patientNotNullValue.setValue(Boolean.FALSE);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.CR)
                    populateFields(pnumber2Text.getText());
            }
        });
        pnumber2Text.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.keyCode == SWT.TAB)
                    populateFields(pnumber2Text.getText());
            }

        });

        setFirstControl(pnumber2Text);

        StudyWrapper selectedStudy = patient1Adapter.getWrapper().getStudy();
        study1Text =
            createReadOnlyLabelledField(patientArea1, SWT.NONE, "Study");
        study1Text.setText(selectedStudy.getNameShort());

        study2Text =
            createReadOnlyLabelledField(patientArea2, SWT.NONE, "Study");

        patient1VisitsTable =
            new ClinicVisitInfoTable(patientArea1, patient1Adapter.getWrapper()
                .getPatientVisitCollection(true, true));
        GridData gd1 = new GridData();
        gd1.horizontalSpan = 2;
        gd1.grabExcessHorizontalSpace = true;
        gd1.horizontalAlignment = SWT.FILL;
        patient1VisitsTable.setLayoutData(gd1);
        patient1VisitsTable.adaptToToolkit(toolkit, true);

        patient2VisitsTable =
            new ClinicVisitInfoTable(patientArea2,
                new ArrayList<PatientVisitWrapper>());
        GridData gd2 = new GridData();
        gd2.horizontalSpan = 2;
        gd2.grabExcessHorizontalSpace = true;
        gd2.horizontalAlignment = SWT.FILL;
        patient2VisitsTable.setLayoutData(gd2);
        patient2VisitsTable.adaptToToolkit(toolkit, true);
    }

    protected void populateFields(String pnumber) {
        try {
            patient2 =
                PatientWrapper.getPatient(SessionManager.getAppService(),
                    pnumber);
        } catch (ApplicationException e) {
            BioBankPlugin.openAsyncError("Error retrieving patient", e);
            return;
        }
        if (patient2 == null) {
            BioBankPlugin.openAsyncError("Invalid Patient Number",
                "Cannot find a patient with that pnumber");
            return;
        }

        study2Text.setText(patient2.getStudy().getNameShort());

        if (!patient2.getStudy()
            .equals(patient1Adapter.getWrapper().getStudy()))
            BioBankPlugin.openAsyncError("Invalid Patient Number",
                "Patients from different studies cannot be merged");
        else {
            patient2VisitsTable.setCollection(patient2
                .getPatientVisitCollection());
            patient2VisitsTable.layout();
            patientNotNullValue.setValue(Boolean.TRUE);
        }
    }

    private void merge() {
        try {
            patient1Adapter.getWrapper().addPatientVisits(
                patient2.getPatientVisitCollection());
            // FIXME: need to make sure this can be removed
            // patient2Wrapper
            // .setPatientVisitCollection(new ArrayList<PatientVisitWrapper>());
            List<ShipmentWrapper> shipments =
                patient2.getShipmentCollection();
            for (ShipmentWrapper shipment : shipments) {
                List<PatientWrapper> patients = shipment.getPatientCollection();
                for (PatientWrapper p : patients)
                    if (p.equals(patient2)) {
                        shipment.removePatients(Arrays
                            .asList(new PatientWrapper[] { patient2 }));
                        shipment.addPatients(Arrays
                            .asList(new PatientWrapper[] { patient1Adapter
                                .getWrapper() }));
                        break;
                    }
                shipment.persist();
            }
            patient1Adapter.getWrapper().persist();
            patient2.reload();
            patient2.delete();

            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    PatientAdapter p =
                        (PatientAdapter) SessionManager.searchNode(patient2);
                    if (p != null) {
                        p.getParent().removeChild(p);
                    }
                    SessionManager.getCurrentAdapterViewWithTree()
                        .getTreeViewer().refresh();
                    closeEntryOpenView(false, true);
                }
            });
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Merge failed.", e);
        }
    }

    @Override
    protected void doBeforeSave() throws Exception {
        canMerge = false;
        if (patient2 != null) {
            if (BioBankPlugin.openConfirm(
                "Confirm Merge",
                "Are you sure you want to merge patient "
                    + patient2.getPnumber() + " into patient "
                    + patient1Adapter.getWrapper().getPnumber()
                    + "? All patient visits will be transferred.")) {
                canMerge = true;
            }
        }
    }

    @Override
    protected void saveForm() throws Exception {
        if (canMerge) {
            merge();
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        pnumber1Text.setText(patient1Adapter.getWrapper().getPnumber());
        study1Text.setText(patient1Adapter.getWrapper().getStudy()
            .getNameShort());
        patient1VisitsTable.setCollection(patient1Adapter.getWrapper()
            .getPatientVisitCollection(true, true));
        pnumber2Text.setText("");
        study2Text.setText("");
        patient2VisitsTable.setCollection(new ArrayList<PatientVisitWrapper>());
        patient2 = null;
    }

    @Override
    protected String getOkMessage() {
        return "Patient " + patient2.getPnumber()
            + " will be merged into patient "
            + patient1Adapter.getWrapper().getPnumber();
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }
}
