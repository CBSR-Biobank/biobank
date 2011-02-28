package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.views.PatientAdministrationView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class PatientEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientEntryForm.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientEntryForm";

    public static final String MSG_NEW_PATIENT_OK = Messages
        .getString("PatientEntryForm.creation.msg");

    public static final String MSG_PATIENT_OK = Messages
        .getString("PatientEntryForm.edition.msg");

    private PatientAdapter patientAdapter;

    private ComboViewer studiesViewer;

    private NonEmptyStringValidator pnumberNonEmptyValidator = new NonEmptyStringValidator(
        Messages.getString("PatientEntryForm.patientNumber.validation.msg"));

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        patientAdapter = (PatientAdapter) adapter;
        retrievePatient();
        try {
            patientAdapter.getWrapper().logEdit(null);
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Log edit failed", e);
        }
        String tabName;
        if (patientAdapter.getWrapper().isNew()) {
            tabName = Messages.getString("PatientEntryForm.new.title");
        } else {
            tabName = Messages.getString("PatientEntryForm.edit.title",
                patientAdapter.getWrapper().getPnumber());
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("PatientEntryForm.main.title"));
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        createPatientSection();

        if (patientAdapter.getWrapper().isNew()) {
            setDirty(true);
        }
    }

    private void createPatientSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        List<StudyWrapper> studies = new ArrayList<StudyWrapper>(
            StudyWrapper.getAllStudies(appService));
        StudyWrapper selectedStudy = null;
        if (patientAdapter.getWrapper().isNew()) {
            if (studies.size() == 1) {
                selectedStudy = studies.get(0);
            }
        } else {
            selectedStudy = patientAdapter.getWrapper().getStudy();
        }

        studiesViewer = createComboViewer(client,
            Messages.getString("PatientEntryForm.field.study.label"), studies,
            selectedStudy,
            Messages.getString("PatientEntryForm.field.study.validation.msg"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    patientAdapter.getWrapper().setStudy(
                        (StudyWrapper) selectedObject);
                }
            });
        setFirstControl(studiesViewer.getControl());
        if (selectedStudy != null) {
            studiesViewer.setSelection(new StructuredSelection(selectedStudy));
        }

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            Messages.getString("PatientEntryForm.field.pNumber.label"), null,
            patientAdapter.getWrapper(), PatientPeer.PNUMBER.getName(),
            pnumberNonEmptyValidator);
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
        patientAdapter.getWrapper().persist();
        // to update patient view:
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                PatientAdministrationView.reloadCurrent();
                PatientAdministrationView.showPatient(patientAdapter
                    .getWrapper());
            }
        });
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }

    private void retrievePatient() {
        try {
            patientAdapter.getWrapper().reload();
        } catch (Exception e) {
            logger.error(Messages.getString(
                "PatientEntryForm.retrieve.error.msg", patientAdapter
                    .getWrapper().getPnumber()), e);
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        StudyWrapper study = patientAdapter.getWrapper().getStudy();
        if (study != null) {
            studiesViewer.setSelection(new StructuredSelection(study));
        }
        studiesViewer.setSelection(null);
        patientAdapter.getWrapper().reset();
        pnumberNonEmptyValidator.validate(null);
    }
}
