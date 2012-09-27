package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreePath;
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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientMergeAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientSearchedNode;
import edu.ualberta.med.biobank.views.CollectionView;
import edu.ualberta.med.biobank.widgets.infotables.ClinicVisitInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientMergeForm extends BiobankEntryForm {
    private static final I18n i18n = I18nFactory
        .getI18n(PatientMergeForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.PatientMergeForm";

    @SuppressWarnings("nls")
    public static final String MSG_PATIENT_NOT_VALID =
        "Select a second patient";

    private PatientWrapper patient1 = new PatientWrapper(
        SessionManager.getAppService());

    private PatientWrapper patient2 = new PatientWrapper(
        SessionManager.getAppService());

    private BgcBaseText study2Text;

    private ClinicVisitInfoTable patient2VisitsTable;

    private BgcBaseText pnumber2Text;

    private BgcBaseText pnumber1Text;

    private BgcBaseText study1Text;

    private IObservableValue patientNotNullValue;

    private ClinicVisitInfoTable patient1VisitsTable;

    private boolean canMerge;

    private PatientInfo p;

    private final CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        p = SessionManager
            .getAppService().doAction(
                new PatientGetInfoAction(adapter.getId()));
        patient1 =
            new PatientWrapper(SessionManager.getAppService());
        patient1.setWrappedObject(p.patient);

        comment.setWrappedObject(new Comment());

        // tab name.
        String tabName = i18n.tr("Merging Patient {0}",
            patient1.getPnumber());
        setPartName(tabName);
        patientNotNullValue = new WritableValue(Boolean.FALSE, Boolean.class);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), patientNotNullValue, MSG_PATIENT_NOT_VALID);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        // form title
        form.setText(i18n.tr("Merging into Patient {0}",
            patient1.getPnumber()));
        page.setLayout(new GridLayout(1, false));
        form.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_PATIENT));

        toolkit
            .createLabel(
                page,
                i18n.tr(
                    "Select Patient Number to merge into Patient {0} and press Enter",
                    patient1.getPnumber()), SWT.LEFT);

        createPatientSection();
        createCommentSection();

    }

    @SuppressWarnings("nls")
    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        GridLayout gl = new GridLayout(1, false);
        client.setLayout(gl);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.MULTI, i18n.tr("Add a comment"), null, comment, "message", null);

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

        @SuppressWarnings("nls")
        Label arrow = toolkit.createLabel(client,
            // label.
            i18n.tr("Arrow"), SWT.IMAGE_BMP);
        arrow.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ARROW_LEFT2));

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

        pnumber1Text = createReadOnlyLabelledField(patientArea1, SWT.NONE,
            Patient.PropertyName.PNUMBER.toString());
        pnumber1Text.setText(patient1.getPnumber());

        pnumber2Text = (BgcBaseText) createLabelledWidget(patientArea2,
            BgcBaseText.class, SWT.NONE,
            Patient.PropertyName.PNUMBER.toString());
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

        StudyWrapper selectedStudy = patient1.getStudy();
        study1Text = createReadOnlyLabelledField(patientArea1, SWT.NONE,
            Study.NAME.singular().toString());
        study1Text.setText(selectedStudy.getNameShort());

        study2Text = createReadOnlyLabelledField(patientArea2, SWT.NONE,
            Study.NAME.singular().toString());

        patient1VisitsTable = new ClinicVisitInfoTable(patientArea1,
            p.ceventInfos);
        GridData gd1 = new GridData();
        gd1.horizontalSpan = 2;
        gd1.grabExcessHorizontalSpace = true;
        gd1.horizontalAlignment = SWT.FILL;
        patient1VisitsTable.setLayoutData(gd1);
        patient1VisitsTable.adaptToToolkit(toolkit, true);

        patient2VisitsTable = new ClinicVisitInfoTable(patientArea2,
            new ArrayList<PatientCEventInfo>());
        GridData gd2 = new GridData();
        gd2.horizontalSpan = 2;
        gd2.grabExcessHorizontalSpace = true;
        gd2.horizontalAlignment = SWT.FILL;
        patient2VisitsTable.setLayoutData(gd2);
        patient2VisitsTable.adaptToToolkit(toolkit, true);
    }

    @SuppressWarnings("nls")
    protected void populateFields(String pnumber) {
        PatientInfo p2;
        List<PatientCEventInfo> newContents =
            new ArrayList<PatientCEventInfo>();
        try {
            SearchedPatientInfo pinfo =
                SessionManager.getAppService().doAction(
                    new PatientSearchAction(pnumber));
            if (pinfo == null)
                throw new ApplicationException(
                    // exception message.
                    i18n.tr("Patient not found"));
            p2 = SessionManager
                .getAppService().doAction(
                    new PatientGetInfoAction(pinfo.patient.getId()));
            patient2 =
                new PatientWrapper(SessionManager.getAppService());
            patient2.setWrappedObject(p2.patient);
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                i18n.tr("Error retrieving patient"), e);
            patient2VisitsTable.setList(newContents);
            study2Text.setText(StringUtil.EMPTY_STRING);
            return;
        }
        if (patient2 == null) {
            BgcPlugin.openAsyncError(
                i18n.tr("Invalid Patient Number"),
                i18n.tr("Cannot find a patient with that pnumber"));
            patient2VisitsTable.setList(newContents);
            study2Text.setText(StringUtil.EMPTY_STRING);
            return;
        }

        if (patient2.equals(patient1)) {
            BgcPlugin.openAsyncError(
                i18n.tr("Duplicate Patient Number"),
                i18n.tr("Cannot merge a patient with himself"));
            patient2VisitsTable.setList(newContents);
            return;
        }

        study2Text.setText(patient2.getStudy().getNameShort());

        if (!patient2.getStudy().equals(patient1.getStudy())) {
            patient2VisitsTable.setList(newContents);
            BgcPlugin.openAsyncError(
                i18n.tr("Invalid Patient Number"),
                i18n.tr("Patients from different studies cannot be merged"));
        } else {
            patient2VisitsTable.setList(p2.ceventInfos);
            patientNotNullValue.setValue(Boolean.TRUE);
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected void doBeforeSave() throws Exception {
        canMerge = false;
        if (patient2 != null) {
            if (BgcPlugin
                .openConfirm(
                    i18n.tr("Confirm Merge"),
                    i18n.tr(
                        "Are you sure you want to merge patient {0} into patient {1}? All collection events, source specimens, and aliquoted specimens will be transferred.",
                        patient2.getPnumber(), patient1.getPnumber()))) {
                canMerge = true;
            }
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected void saveForm() throws Exception {
        if (canMerge) {
            boolean success = false;
            try {
                success =
                    SessionManager
                        .getAppService()
                        .doAction(
                            new PatientMergeAction(patient1.getId(), patient2
                                .getId(), comment.getMessage())).isTrue();
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    // dialog title.
                    i18n.tr("Merge Failed"), e);
                return;
            }

            final boolean patientRemoved = success;
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    if (patientRemoved) {
                        TreePath[] expandedTreePaths = CollectionView
                            .getCurrent().getTreeViewer()
                            .getExpandedTreePaths();
                        PatientSearchedNode searcher = CollectionView
                            .getCurrent().getSearchedNode();
                        searcher.removeAll();
                        searcher.removePatient(patient2.getId());
                        searcher.rebuild();
                        CollectionView.getCurrent().getTreeViewer()
                            .setExpandedTreePaths(expandedTreePaths);
                    }
                    closeEntryOpenView(false, true);
                }
            });
            SessionManager.log("merge",
                patient2.getPnumber() + " " + "-->"
                    + " " + patient1.getPnumber(),
                Patient.NAME.singular().toString());
        }
    }

    @Override
    public void setValues() throws Exception {
        pnumber1Text.setText(patient1.getPnumber());
        study1Text.setText(patient1.getStudy().getNameShort());
        patient1VisitsTable.setList(p.ceventInfos);
        pnumber2Text.setText(StringUtil.EMPTY_STRING);
        study2Text.setText(StringUtil.EMPTY_STRING);
        patient2VisitsTable
            .setList(new ArrayList<PatientCEventInfo>());
        patient2 = null;
        comment.setWrappedObject(new Comment());
    }

    @SuppressWarnings("nls")
    @Override
    protected String getOkMessage() {
        // title area message
        return i18n.tr("Patient {0} will be merged into patient {1}",
            patient2.getPnumber(), patient1.getPnumber());
    }

    @Override
    public String getNextOpenedFormId() {
        return PatientViewForm.ID;
    }
}
