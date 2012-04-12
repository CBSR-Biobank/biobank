package edu.ualberta.med.biobank.forms;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.patient.PatientGetCollectionEventInfosAction.PatientCEventInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetInfoAction.PatientInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientGetSimpleCollectionEventInfosAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetSimpleCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewCollectionEventInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientViewForm extends BiobankViewForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.PatientViewForm";

    private BgcBaseText studyLabel;

    private BgcBaseText createdAtLabel;

    private BgcBaseText visitCountLabel;

    private BgcBaseText sourceSpecimenCountLabel;

    private BgcBaseText aliquotedSpecimenCountLabel;

    private NewCollectionEventInfoTable collectionEventTable;

    private PatientInfo patientInfo;

    private BgcBaseText commentLabel;

    private CommentsInfoTable commentEntryTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof PatientAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        updatePatientInfo();
        setPartName(NLS.bind("Patient {0}",
            patientInfo.patient.getPnumber()));
    }

    private void updatePatientInfo() throws Exception {
        patientInfo = SessionManager.getAppService().doAction(
            new PatientGetInfoAction(adapter.getId()));
        SessionManager.logLookup(patientInfo.patient);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind("Patient {0}",
            patientInfo.patient.getPnumber()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createPatientSection();
        createCommentSection();
        createCollectionEventSection();
        setValues();
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient("Comments");
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentsInfoTable(client,
                ModelWrapper.wrapModelCollection(
                    SessionManager.getAppService(),
                    patientInfo.patient.getComments(),
                    CommentWrapper.class));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);

    }

    private void createPatientSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        studyLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Study");
        createdAtLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Created At");
        visitCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Total Visits");
        sourceSpecimenCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Total source specimens");
        aliquotedSpecimenCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                "Total aliquoted specimens");
    }

    private void createCollectionEventSection() {
        Section section = createSection("Collection Events");

        collectionEventTable =
            new NewCollectionEventInfoTable(section, patientInfo.ceventInfos);
        section.setClient(collectionEventTable);
        collectionEventTable.adaptToToolkit(toolkit, true);
        collectionEventTable
            .addClickListener(new IInfoTableDoubleClickItemListener<PatientCEventInfo>() {

                @Override
                public void doubleClick(InfoTableEvent<PatientCEventInfo> event) {
                    CollectionEvent ce =
                        ((PatientCEventInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).cevent;
                    if (ce != null) {
                        try {
                            Map<Integer, SimpleCEventInfo> map =
                                SessionManager
                                    .getAppService()
                                    .doAction(
                                        new PatientGetSimpleCollectionEventInfosAction(
                                            ce
                                                .getPatient().getId()))
                                    .getMap();

                            CollectionEventAdapter adapter =
                                new CollectionEventAdapter(
                                    PatientViewForm.this.adapter, map.get(ce
                                        .getId()));
                            adapter.openEntryForm();
                        } catch (ApplicationException e) {
                            BgcPlugin.openAsyncError(
                                "Unable to open form",
                                "Error loading collection event.");
                        }
                    }
                    return;
                }
            });
        collectionEventTable
            .addEditItemListener(new IInfoTableEditItemListener<PatientCEventInfo>() {

                @Override
                public void editItem(InfoTableEvent<PatientCEventInfo> event) {
                    CollectionEvent ce =
                        event.getInfoTable().getSelection().cevent;
                    if (ce != null) {
                        try {
                            Map<Integer, SimpleCEventInfo> map =
                                SessionManager
                                    .getAppService()
                                    .doAction(
                                        new PatientGetSimpleCollectionEventInfosAction(
                                            ce
                                                .getPatient().getId()))
                                    .getMap();

                            CollectionEventAdapter adapter =
                                new CollectionEventAdapter(
                                    PatientViewForm.this.adapter, map.get(ce
                                        .getId()));
                            adapter.openEntryForm();
                        } catch (ApplicationException e) {
                            BgcPlugin.openAsyncError(
                                "Unable to open form",
                                "Error loading collection event.");
                        }
                    }
                    return;
                }

            });
    }

    @Override
    public void setValues() throws Exception {
        setPartName(NLS.bind("Patient {0}",
            patientInfo.patient.getPnumber()));
        form.setText(NLS.bind("Patient {0}",
            patientInfo.patient.getPnumber()));
        collectionEventTable.setList(patientInfo.ceventInfos);
        setTextValue(studyLabel, patientInfo.patient.getStudy().getName());
        setTextValue(createdAtLabel,
            DateFormatter.formatAsDateTime(patientInfo.patient.getCreatedAt()));
        setTextValue(visitCountLabel, patientInfo.ceventInfos.size());
        setTextValue(sourceSpecimenCountLabel, patientInfo.sourceSpecimenCount);
        setTextValue(aliquotedSpecimenCountLabel,
            patientInfo.aliquotedSpecimenCount);
        setTextValue(commentLabel, patientInfo.patient.getComments());
    }

}
