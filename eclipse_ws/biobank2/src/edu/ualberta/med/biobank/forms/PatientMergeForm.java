package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientSearchedNode;
import edu.ualberta.med.biobank.views.CollectionView;
import edu.ualberta.med.biobank.widgets.infotables.ClinicVisitInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientMergeForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.PatientMergeForm"; //$NON-NLS-1$

    public static final String MSG_PATIENT_NOT_VALID = Messages.PatientMergeForm_notValid_msg;

    private PatientWrapper patient1;

    private PatientWrapper patient2;

    private BgcBaseText study2Text;

    private ClinicVisitInfoTable patient2VisitsTable;

    private BgcBaseText pnumber2Text;

    private BgcBaseText pnumber1Text;

    private BgcBaseText study1Text;

    private IObservableValue patientNotNullValue;

    private ClinicVisitInfoTable patient1VisitsTable;

    private boolean canMerge;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof PatientAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        patient1 = (PatientWrapper) getModelObject();

        String tabName = NLS.bind(Messages.PatientMergeForm_title, patient1.getPnumber());
        setPartName(tabName);
        patientNotNullValue = new WritableValue(Boolean.FALSE, Boolean.class);
        widgetCreator.addBooleanBinding(new WritableValue(Boolean.FALSE,
            Boolean.class), patientNotNullValue, MSG_PATIENT_NOT_VALID);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.PatientMergeForm_form_title, patient1.getPnumber()));
        page.setLayout(new GridLayout(1, false));
        form.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_PATIENT));

        toolkit.createLabel(page, NLS.bind(
            Messages.PatientMergeForm_description,
            patient1.getPnumber()), SWT.LEFT);

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

        Label arrow = toolkit.createLabel(client, "Arrow", SWT.IMAGE_BMP); //$NON-NLS-1$
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
            Messages.PatientMergeForm_pnumber_label);
        pnumber1Text.setText(patient1.getPnumber());

        pnumber2Text = (BgcBaseText) createLabelledWidget(patientArea2,
            BgcBaseText.class, SWT.NONE,
            Messages.PatientMergeForm_pnumber_label);
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
            Messages.PatientMergeForm_study_label);
        study1Text.setText(selectedStudy.getNameShort());

        study2Text = createReadOnlyLabelledField(patientArea2, SWT.NONE,
            Messages.PatientMergeForm_study_label);

        patient1VisitsTable = new ClinicVisitInfoTable(patientArea1,
            patient1.getCollectionEventCollection(true));
        GridData gd1 = new GridData();
        gd1.horizontalSpan = 2;
        gd1.grabExcessHorizontalSpace = true;
        gd1.horizontalAlignment = SWT.FILL;
        patient1VisitsTable.setLayoutData(gd1);
        patient1VisitsTable.adaptToToolkit(toolkit, true);

        patient2VisitsTable = new ClinicVisitInfoTable(patientArea2,
            new ArrayList<CollectionEventWrapper>());
        GridData gd2 = new GridData();
        gd2.horizontalSpan = 2;
        gd2.grabExcessHorizontalSpace = true;
        gd2.horizontalAlignment = SWT.FILL;
        patient2VisitsTable.setLayoutData(gd2);
        patient2VisitsTable.adaptToToolkit(toolkit, true);
    }

    protected void populateFields(String pnumber) {
        List<CollectionEventWrapper> newContents = new ArrayList<CollectionEventWrapper>();
        try {
            patient2 = PatientWrapper.getPatient(
                SessionManager.getAppService(), pnumber);
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                Messages.PatientMergeForm_retrieve_error_title, e);
            patient2VisitsTable.setCollection(newContents);
            study2Text.setText(""); //$NON-NLS-1$
            return;
        }
        if (patient2 == null) {
            BgcPlugin.openAsyncError(
                Messages.PatientMergeForm_pnber_invalid_error_title,
                Messages.PatientMergeForm_pnber_invalid_error_msg);
            patient2VisitsTable.setCollection(newContents);
            study2Text.setText(""); //$NON-NLS-1$
            return;
        }

        if (patient2.equals(patient1)) {
            BgcPlugin.openAsyncError(
                Messages.PatientMergeForm_duplicate_error_title,
                Messages.PatientMergeForm_duplicate_error_msg);
            patient2VisitsTable.setCollection(newContents);
            return;
        }

        study2Text.setText(patient2.getStudy().getNameShort());

        if (!patient2.getStudy().equals(patient1.getStudy())) {
            patient2VisitsTable.setCollection(newContents);
            BgcPlugin.openAsyncError(
                Messages.PatientMergeForm_pnber_invalid_error_title,
                Messages.PatientMergeForm_studies_error_msg);
        } else {
            patient2VisitsTable.setCollection(patient2
                .getCollectionEventCollection(true));
            patientNotNullValue.setValue(Boolean.TRUE);
        }
    }

    private void merge() {
        try {
            patient1.merge(patient2);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.PatientMergeForm_merge_error_title, e);
        }

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                PatientSearchedNode searcher = (PatientSearchedNode) CollectionView
                    .getCurrent().getSearchedNode();
                searcher.removeObjects(Arrays.asList(patient2));
                searcher.performExpand();
                closeEntryOpenView(false, true);
            }
        });
    }

    @Override
    protected void doBeforeSave() throws Exception {
        canMerge = false;
        if (patient2 != null) {
            if (BgcPlugin
                .openConfirm(
                    Messages.PatientMergeForm_confirm_dialog_title,
                    NLS.bind(
                        Messages.PatientMergeForm_confirm_dialog_msg,
                        patient2.getPnumber(), patient1.getPnumber()))) {
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
    protected void onReset() throws Exception {
        pnumber1Text.setText(patient1.getPnumber());
        study1Text.setText(patient1.getStudy().getNameShort());
        patient1VisitsTable.setCollection(patient1
            .getCollectionEventCollection(true));
        pnumber2Text.setText(""); //$NON-NLS-1$
        study2Text.setText(""); //$NON-NLS-1$
        patient2VisitsTable
            .setCollection(new ArrayList<CollectionEventWrapper>());
        patient2 = null;
    }

    @Override
    protected String getOkMessage() {
        return NLS.bind(Messages.PatientMergeForm_ok_msg,
            patient2.getPnumber(), patient1.getPnumber());
    }

    @Override
    public String getNextOpenedFormID() {
        return PatientViewForm.ID;
    }
}
