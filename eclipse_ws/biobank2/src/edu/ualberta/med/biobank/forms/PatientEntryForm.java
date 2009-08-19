package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.ModelUtils;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.views.PatientAdministrationView;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientEntryForm";

    public static final String MSG_NEW_PATIENT_OK = "Creating a new patient record.";

    public static final String MSG_PATIENT_OK = "Editing an existing patient record.";

    public static final String MSG_NO_PATIENT_NUMBER = "Patient must have a patient number";

    public static final String MSG_NO_STUDY = "Enter a valid study short name";

    private PatientAdapter patientAdapter;

    private Text textStudy;

    private IObservableValue studyValue = new WritableValue("", String.class);
    private IObservableValue studyValidValue = new WritableValue(null,
        String.class);

    private Study currentStudy;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientAdapter = (PatientAdapter) adapter;
        String tabName;
        if (patientAdapter.getPatient().getId() == null) {
            tabName = "New Patient";
        } else {
            tabName = "Patient " + patientAdapter.getPatient().getNumber();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() {
        form.setText("Patient Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));

        createPatientSection();
        createButtons();
    }

    private void createPatientSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        textStudy = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.NONE, "Study short name", null, studyValue,
            NonEmptyString.class, MSG_NO_STUDY);

        textStudy.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String studyShortName = textStudy.getText();
                currentStudy = ModelUtils.getObjectWithAttr(patientAdapter
                    .getAppService(), Study.class, "nameShort", String.class,
                    studyShortName);
                if (currentStudy == null) {
                    studyValidValue.setValue(studyShortName);
                } else {
                    studyValidValue.setValue(null);
                }
            }
        });
        textStudy.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterConvertValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof String && value != null) {
                    return ValidationStatus.error(value
                        + " is not a valid study short name");
                } else {
                    return Status.OK_STATUS;
                }
            }
        });
        dbc.bindValue(new WritableValue(null, String.class), studyValidValue,
            uvs, uvs);
        studyValidValue.setValue(null);

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Patient Number", null, PojoObservables.observeValue(patientAdapter
                .getPatient(), "number"), NonEmptyString.class,
            MSG_NO_PATIENT_NUMBER);
    }

    protected void createButtons() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 10;
        layout.numColumns = 2;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);

        initConfirmButton(client, false, true);
    }

    @Override
    protected String getOkMessage() {
        if (patientAdapter.getPatient().getId() == null) {
            return MSG_NEW_PATIENT_OK;
        }
        return MSG_PATIENT_OK;
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            getConfirmButton().setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            getConfirmButton().setEnabled(false);
        }
    }

    @Override
    protected void saveForm() throws Exception {
        SDKQuery query;
        SDKQueryResult result;

        Patient patient = patientAdapter.getPatient();
        if ((patient.getId() == null) && !checkPatientNumberUnique()) {
            setDirty(true);
            return;
        }

        patient.setStudy(currentStudy);

        if ((patient.getId() == null) || (patient.getId() == 0)) {
            query = new InsertExampleQuery(patient);
        } else {
            query = new UpdateExampleQuery(patient);
        }

        result = appService.executeQuery(query);
        patientAdapter.setPatient((Patient) result.getObjectResult());
        PatientAdministrationView.showPatient(patientAdapter);
    }

    private boolean checkPatientNumberUnique() throws ApplicationException {
        String number = patientAdapter.getPatient().getNumber();
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Patient as p "
                + "inner join fetch p.study " + "where p.study.id='"
                + currentStudy.getId() + "' " + "and p.number = '" + number
                + "'");

        List<Object> results = appService.query(c);
        if (results.size() == 0)
            return true;

        BioBankPlugin.openAsyncError("Patient Number Problem",
            "A patient with number \"" + number + "\" already exists.");
        return false;
    }

    @Override
    public void cancelForm() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }

}
