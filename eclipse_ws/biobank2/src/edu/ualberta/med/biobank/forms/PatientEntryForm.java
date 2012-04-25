package edu.ualberta.med.biobank.forms;

import java.util.Date;
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
import edu.ualberta.med.biobank.common.action.center.CenterGetStudyListAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.HasCreatedAt;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.views.CollectionView;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientEntryForm extends BiobankEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.PatientEntryForm";

    private static final String CREATED_AT_BINDING =
        "patient-created-at-binding";

    public static final String MSG_NEW_PATIENT_OK =
        "Creating a new patient record.";

    public static final String MSG_PATIENT_OK =
        "Editing an existing patient record.";

    private ComboViewer studiesViewer;

    private Label createdAtLabel;

    private NotNullValidator createdAtValidator;

    private final NonEmptyStringValidator pnumberNonEmptyValidator =
        new NonEmptyStringValidator(
            "Patient must have a patient number");

    private PatientInfo patientInfo;

    private final PatientWrapper patient = new PatientWrapper(
        SessionManager.getAppService());

    private CommentsInfoTable commentEntryTable;

    private final CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        updatePatientInfo();

        String tabName;
        if (patientInfo == null) {
            tabName = "New Patient";
        } else {
            tabName =
                NLS.bind("Patient {0}",
                    patientInfo.patient.getPnumber());
        }
        setPartName(tabName);
    }

    protected void updatePatientInfo() throws ApplicationException {
        if (adapter.getId() != null) {
            patientInfo = SessionManager.getAppService().doAction(
                new PatientGetInfoAction(adapter.getId()));
            patient.setWrappedObject(patientInfo.patient);
            SessionManager.logLookup(patientInfo.patient);
        } else {
            patient.setWrappedObject(((PatientAdapter) adapter).getPatient());
        }
        patient.setCreatedAt(new Date());
        comment.setWrappedObject(new Comment());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Patient Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        createPatientSection();

        if (patientInfo == null) {
            setDirty(true);
        }
    }

    private void createPatientSection() throws Exception {
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        List<Study> studies = SessionManager.getAppService().doAction(
            new CenterGetStudyListAction(SessionManager.getUser()
                .getCurrentWorkingCenter())).getList();
        Study selectedStudy = null;
        if (patientInfo == null) {
            if (studies.size() == 1) {
                selectedStudy = studies.get(0);
                patient.setStudy(new StudyWrapper(SessionManager
                    .getAppService(), selectedStudy));
            }
        } else {
            selectedStudy = patient.getStudy().getWrappedObject();
        }

        studiesViewer =
            createComboViewer(client,
                Study.NAME.singular().toString(), studies,
                selectedStudy,
                "A study should be selected",
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        patient.setStudy(new StudyWrapper(
                            SessionManager.getAppService(),
                            (Study) selectedObject));
                    }
                });
        studiesViewer.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object element) {
                return ((Study) element).getNameShort();
            }
        });
        setFirstControl(studiesViewer.getControl());

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Patient.PropertyName.PNUMBER.toString(), null, patient,
            PatientPeer.PNUMBER.getName(), pnumberNonEmptyValidator);

        createdAtLabel = widgetCreator.createLabel(client,
            HasCreatedAt.PropertyName.CREATED_AT.toString());
        createdAtLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        createdAtValidator = new NotNullValidator(
            "Created At should be set");

        createDateTimeWidget(client, createdAtLabel, patient.getCreatedAt(),
            patient, PatientPeer.CREATED_AT.getName(), createdAtValidator,
            SWT.DATE | SWT.TIME, CREATED_AT_BINDING);

        createCommentSection();
    }

    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentsInfoTable(client,
            patient.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            "Add a comment", null, comment, "message", null);
    }

    @Override
    protected String getOkMessage() {
        if (patientInfo == null) {
            return MSG_NEW_PATIENT_OK;
        }
        return MSG_PATIENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        SessionManager.getAppService().doAction(
            new PatientSaveAction(patient.getId(), patient
                .getStudy().getId(), patient.getPnumber(), patient
                .getCreatedAt(), comment.getMessage())).getId();
        ((PatientAdapter) adapter).setValue(SessionManager.getAppService()
            .doAction(new PatientSearchAction(patient.getPnumber())));
    }

    @Override
    protected void doAfterSave() throws Exception {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                // FIXME how to display new patient without explicitly calling
                // the collection tree view?
                CollectionView.getCurrent().showSearchedObjectsInTree(
                    adapter.getId(), patient.getPnumber(), true, true);
                // the node is not highlighted because the entryform is opening
                // a viewform with a different adapter
            }
        });
    }

    @Override
    public String getNextOpenedFormId() {
        return PatientViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        GuiUtil.reset(studiesViewer, patient.getStudy());
    }

    @Override
    protected boolean openViewAfterSaving() {
        // already done by showSearchedObjectsInTree called in doAfterSave
        return false;
    }
}
