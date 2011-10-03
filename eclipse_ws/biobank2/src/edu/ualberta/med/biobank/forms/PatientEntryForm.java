package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.views.CollectionView;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;

public class PatientEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientEntryForm"; //$NON-NLS-1$

    private static final String CREATED_AT_BINDING = "patient-created-at-binding"; //$NON-NLS-1$

    public static final String MSG_NEW_PATIENT_OK = Messages.PatientEntryForm_creation_msg;

    public static final String MSG_PATIENT_OK = Messages.PatientEntryForm_edition_msg;

    private PatientWrapper patient;

    private ComboViewer studiesViewer;

    private Label createdAtLabel;

    private NotNullValidator createdAtValidator;

    private NonEmptyStringValidator pnumberNonEmptyValidator = new NonEmptyStringValidator(
        Messages.PatientEntryForm_patientNumber_validation_msg);

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        patient = (PatientWrapper) getModelObject();

        SessionManager.logEdit(patient);
        String tabName;
        if (patient.isNew()) {
            tabName = Messages.PatientEntryForm_new_title;
        } else {
            tabName = NLS.bind(Messages.PatientEntryForm_edit_title,
                patient.getPnumber());
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.PatientEntryForm_main_title);
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        createPatientSection();

        if (patient.isNew()) {
            setDirty(true);
        }
    }

    private void createPatientSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        List<StudyWrapper> studies = SessionManager.getUser()
            .getCurrentWorkingCenter().getStudyCollection();
        StudyWrapper selectedStudy = null;
        if (patient.isNew()) {
            if (studies.size() == 1) {
                selectedStudy = studies.get(0);
                patient.setStudy(selectedStudy);
            }
        } else {
            selectedStudy = patient.getStudy();
        }

        studiesViewer = createComboViewer(client,
            Messages.PatientEntryForm_field_study_label, studies,
            selectedStudy,
            Messages.PatientEntryForm_field_study_validation_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    patient.setStudy((StudyWrapper) selectedObject);
                }
            });
        setFirstControl(studiesViewer.getControl());

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.PatientEntryForm_field_pNumber_label, null, patient,
            PatientPeer.PNUMBER.getName(), pnumberNonEmptyValidator);

        createdAtLabel = widgetCreator.createLabel(client,
            Messages.PatientEntryForm_created_label);
        createdAtLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        createdAtValidator = new NotNullValidator(
            Messages.PatientEntryForm_created_validation_msg);

        createDateTimeWidget(client, createdAtLabel, patient.getCreatedAt(),
            patient, PatientPeer.CREATED_AT.getName(), createdAtValidator,
            SWT.DATE | SWT.TIME, CREATED_AT_BINDING);
    }

    @Override
    protected String getOkMessage() {
        if (patient.isNew()) {
            return MSG_NEW_PATIENT_OK;
        }
        return MSG_PATIENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        patient.persist();
        SessionManager.updateAllSimilarNodes(adapter, true);
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                CollectionView.getCurrent().showSearchedObjectsInTree(
                    Arrays.asList(patient.getWrappedObject()), true);
            }
        });
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }

    @Override
    protected void onReset() throws Exception {
        patient.reset();

        GuiUtil.reset(studiesViewer, patient.getStudy());
    }
}
