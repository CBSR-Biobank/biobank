package edu.ualberta.med.biobank.forms;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.common.utils.SiteUtils;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.PatientEntryForm";

    public static final String MSG_NEW_PATIENT_OK = "Creating a new patient record.";

    public static final String MSG_PATIENT_OK = "Editing an existing patient record.";

    public static final String MSG_NO_PATIENT_NUMBER = "Patient must have a patient number";

    public static final String MSG_NO_STUDY = "Enter a valid study short name";

    private PatientAdapter patientAdapter;

    private IObservableValue studyValue = new WritableValue("", String.class);
    private IObservableValue studyValidValue = new WritableValue(null,
        String.class);

    private Study currentStudy;

    private Site site;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientAdapter = (PatientAdapter) adapter;
        retrievePatient();
        String tabName;
        if (patientAdapter.getWrapper().isNew()) {
            tabName = "New Patient";
        } else {
            tabName = "Patient "
                + patientAdapter.getWrapper().getPatient().getNumber();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() {
        form.setText("Patient Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));

        createPatientSection();
        initCancelConfirmWidget(form.getBody());
    }

    private void createPatientSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Label labelSite = (Label) createWidget(client, Label.class, SWT.NONE,
            "Site");
        site = SessionManager.getInstance().getCurrentSite();
        labelSite.setText(site.getName());

        final Text textStudy = (Text) createBoundWidgetWithLabel(client,
            Text.class, SWT.NONE, "Study short name", null, studyValue,
            NonEmptyString.class, MSG_NO_STUDY);

        if (patientAdapter.getWrapper().isNew()) {
            textStudy.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    String studyShortName = textStudy.getText();
                    currentStudy = SiteUtils.getStudyInSite(patientAdapter
                        .getAppService(), studyShortName, site);
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
            dbc.bindValue(new WritableValue(null, String.class),
                studyValidValue, uvs, uvs);
            studyValidValue.setValue(null);
        } else {
            // edit a patient - can't modify the study !
            currentStudy = patientAdapter.getWrapper().getStudy();
            textStudy.setText(currentStudy.getNameShort());
            textStudy.setEditable(false);
        }

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Patient Number", null, BeansObservables.observeValue(
                patientAdapter.getWrapper(), "number"), NonEmptyString.class,
            MSG_NO_PATIENT_NUMBER);
    }

    @Override
    protected String getOkMessage() {
        if (patientAdapter.getWrapper().isNew()) {
            return MSG_NEW_PATIENT_OK;
        }
        return MSG_PATIENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        patientAdapter.getWrapper().setStudy(currentStudy);
        DatabaseResult res = patientAdapter.getWrapper().persist();
        if (res != DatabaseResult.OK) {
            BioBankPlugin.openAsyncError("Save Problem", res.getMessage());
            setDirty(true);
        }
        PatientAdministrationView.currentInstance
            .showPatientInTree(patientAdapter.getWrapper().getPatient());

    }

    @Override
    public void cancelForm() {
        try {
            patientAdapter.getWrapper().reset();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "can't reset the patient with id " + patientAdapter.getId());
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }

    private void retrievePatient() {
        try {
            patientAdapter.getWrapper().reload();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while retrieving patient "
                    + patientAdapter.getWrapper().getNumber(), e);
        }
    }

}
