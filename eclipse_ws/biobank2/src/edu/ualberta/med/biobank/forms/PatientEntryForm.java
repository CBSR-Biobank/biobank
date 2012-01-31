package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashSet;
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
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetListForSiteAction;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.views.CollectionView;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;

public class PatientEntryForm extends BiobankEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.PatientEntryForm"; //$NON-NLS-1$

    private static final String CREATED_AT_BINDING =
        "patient-created-at-binding"; //$NON-NLS-1$

    public static final String MSG_NEW_PATIENT_OK =
        Messages.PatientEntryForm_creation_msg;

    public static final String MSG_PATIENT_OK =
        Messages.PatientEntryForm_edition_msg;

    private ComboViewer studiesViewer;

    private Label createdAtLabel;

    private NotNullValidator createdAtValidator;

    private NonEmptyStringValidator pnumberNonEmptyValidator =
        new NonEmptyStringValidator(
            Messages.PatientEntryForm_patientNumber_validation_msg);

    private PatientInfo pInfo;

    private Patient patientCopy;

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    private CommentCollectionInfoTable commentEntryTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        patientCopy = new Patient();
        patientCopy.setCommentCollection(new HashSet<Comment>());
        if (adapter.getId() != null) {
            pInfo =
                SessionManager.getAppService().doAction(
                    new PatientGetInfoAction(adapter.getId()));
            copyPatient();
        } else {
            patientCopy = ((PatientAdapter) adapter).getPatient();
        }

        // FIXME log edit action?
        // SessionManager.logEdit(patient);
        String tabName;
        if (pInfo == null) {
            tabName = Messages.PatientEntryForm_new_title;
        } else {
            tabName =
                NLS.bind(Messages.PatientEntryForm_edit_title,
                    pInfo.patient.getPnumber());
        }
        setPartName(tabName);
    }

    protected void copyPatient() {
        if (pInfo == null) {
            patientCopy.setCreatedAt(null);
            patientCopy.setPnumber(null);
            patientCopy.setStudy(null);
        } else {
            patientCopy.setId(pInfo.patient.getId());
            patientCopy.setCreatedAt(pInfo.patient.getCreatedAt());
            patientCopy.setPnumber(pInfo.patient.getPnumber());
            patientCopy.setStudy(pInfo.patient.getStudy());
            patientCopy.setCommentCollection(pInfo.patient
                .getCommentCollection());
        }
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.PatientEntryForm_main_title);
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));

        createPatientSection();

        if (pInfo == null) {
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

        List<Study> studies = SessionManager.getAppService().doAction(
            new StudyGetListForSiteAction(SessionManager.getUser()
                .getCurrentWorkingSite().getId())).getList();
        Study selectedStudy = null;
        if (pInfo == null) {
            if (studies.size() == 1) {
                selectedStudy = studies.get(0);
                patientCopy.setStudy(selectedStudy);
            }
        } else {
            selectedStudy = patientCopy.getStudy();
        }

        studiesViewer =
            createComboViewer(client,
                Messages.PatientEntryForm_field_study_label, studies,
                selectedStudy,
                Messages.PatientEntryForm_field_study_validation_msg,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        patientCopy.setStudy((Study) selectedObject);
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
            Messages.PatientEntryForm_field_pNumber_label, null, patientCopy,
            PatientPeer.PNUMBER.getName(), pnumberNonEmptyValidator, false);

        createdAtLabel =
            widgetCreator.createLabel(client,
                Messages.PatientEntryForm_created_label);
        createdAtLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        createdAtValidator =
            new NotNullValidator(
                Messages.PatientEntryForm_created_validation_msg);

        createDateTimeWidget(client, createdAtLabel,
            patientCopy.getCreatedAt(), patientCopy,
            PatientPeer.CREATED_AT.getName(), createdAtValidator, SWT.DATE
                | SWT.TIME, CREATED_AT_BINDING, false);

        createCommentSection();
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentCollectionInfoTable(client,
                ModelWrapper.wrapModelCollection(
                    SessionManager.getAppService(),
                    patientCopy.getCommentCollection(), CommentWrapper.class));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createLabelledWidget(client, BgcBaseText.class, SWT.MULTI,
            Messages.Comments_add);

    }

    @Override
    protected String getOkMessage() {
        if (pInfo == null) {
            return MSG_NEW_PATIENT_OK;
        }
        return MSG_PATIENT_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        Integer patientId =
            SessionManager.getAppService().doAction(
                new PatientSaveAction(patientCopy.getId(), patientCopy
                    .getStudy().getId(), patientCopy.getPnumber(), patientCopy
                    .getCreatedAt())).getId();
        adapter.setId(patientId);

        // FIXME the tree needs to get the new value from the patien in case it
        // has been modified (like de pnumber for instance), but the studynode
        // contains and old version of the patient... Rebuild should rebuild
        // this but this is not that nice...
        // SessionManager.getCurrentAdapterViewWithTree().reload();
    }

    @Override
    protected void doAfterSave() throws Exception {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                // FIXME how to display new patient without explicitly calling
                // the collection tree view?
                CollectionView.getCurrent().showSearchedObjectsInTree(
                    adapter.getId(), patientCopy.getPnumber(), true, true);
                // the node is not highlighted because the entryform is opening
                // a viewform with a different adapter
            }
        });
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }

    @Override
    protected void onReset() throws Exception {
        copyPatient();
        GuiUtil.reset(studiesViewer, patientCopy.getStudy());
    }

    @Override
    protected boolean openViewAfterSaving() {
        // already done by showSearchedObjectsInTree called in doAfterSave
        return false;
    }
}
