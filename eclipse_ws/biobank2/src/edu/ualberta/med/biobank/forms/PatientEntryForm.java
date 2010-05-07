package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.PatientAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;

public class PatientEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientEntryForm";

    public static final String MSG_NEW_PATIENT_OK = "Creating a new patient record.";

    public static final String MSG_PATIENT_OK = "Editing an existing patient record.";

    public static final String MSG_NO_PATIENT_NUMBER = "Patient must have a patient number";

    public static final String MSG_NO_STUDY = "Enter a valid study short name";

    private PatientAdapter patientAdapter;

    private SiteWrapper siteWrapper;

    private ComboViewer studiesViewer;

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
            tabName = "Patient " + patientAdapter.getWrapper().getPnumber();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_PATIENT));

        createPatientSection();

        if (patientAdapter.getWrapper().isNew()) {
            setDirty(true);
        }
    }

    private void createPatientSection() throws Exception {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Text labelSite = createReadOnlyLabelledField(client, SWT.NONE, "Site");
        siteWrapper = SessionManager.getInstance().getCurrentSite();
        labelSite.setText(siteWrapper.getName());

        siteWrapper.reload();
        List<StudyWrapper> studies = new ArrayList<StudyWrapper>(siteWrapper
            .getStudyCollection());
        StudyWrapper selectedStudy = null;
        if (patientAdapter.getWrapper().isNew()) {
            if (studies.size() == 1) {
                selectedStudy = studies.get(0);
            }
        } else {
            selectedStudy = patientAdapter.getWrapper().getStudy();
        }

        studiesViewer = createComboViewerWithNoSelectionValidator(client,
            "Study", studies, selectedStudy, "A study should be selected");

        setFirstControl(createBoundWidgetWithLabel(client, Text.class,
            SWT.NONE, "Patient Number", null, BeansObservables.observeValue(
                patientAdapter.getWrapper(), "pnumber"),
            new NonEmptyStringValidator(MSG_NO_PATIENT_NUMBER)));
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
        StudyWrapper study = (StudyWrapper) ((IStructuredSelection) studiesViewer
            .getSelection()).getFirstElement();
        patientAdapter.getWrapper().setStudy(study);
        patientAdapter.getWrapper().persist();
        // PatientAdministrationView.showPatient(patientAdapter.getWrapper());
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }

    private void retrievePatient() {
        try {
            patientAdapter.getWrapper().reload();
        } catch (Exception e) {
            logger.error("Error while retrieving patient "
                + patientAdapter.getWrapper().getPnumber(), e);
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        StudyWrapper study = patientAdapter.getWrapper().getStudy();
        if (study != null) {
            studiesViewer.setSelection(new StructuredSelection(study));
        }
    }
}
