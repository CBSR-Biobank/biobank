package edu.ualberta.med.biobank.forms;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.HasCreatedAt;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.treeview.patient.PatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewCollectionEventInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientViewForm extends BiobankViewForm {
    private static final I18n i18n = I18nFactory
        .getI18n(PatientViewForm.class);

    @SuppressWarnings("nls")
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

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof PatientAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        updatePatientInfo();
        setPartName(i18n.tr("Patient {0}",
            patientInfo.patient.getPnumber()));
    }

    private void updatePatientInfo() throws Exception {
        patientInfo = SessionManager.getAppService().doAction(
            new PatientGetInfoAction(adapter.getId()));
        SessionManager.logLookup(patientInfo.patient);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Patient {0}",
            patientInfo.patient.getPnumber()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createPatientSection();
        createCommentSection();
        createCollectionEventSection();
        setValues();
    }

    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
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

    @SuppressWarnings("nls")
    private void createPatientSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        studyLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Study.NAME.singular().toString());
        createdAtLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                HasCreatedAt.PropertyName.CREATED_AT.toString());
        visitCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                i18n.tr("Total {0}", CollectionEvent.NAME.plural().toString()));
        sourceSpecimenCountLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                i18n.tr("Total {0}", SourceSpecimen.NAME.plural().toString()));
        aliquotedSpecimenCountLabel =
            createReadOnlyLabelledField(
                client,
                SWT.NONE,
                i18n.tr("Total {0}", AliquotedSpecimen.NAME.plural().toString()));
    }

    private void createCollectionEventSection() {
        Section section =
            createSection(CollectionEvent.NAME.plural().toString());

        collectionEventTable =
            new NewCollectionEventInfoTable(section, patientInfo.ceventInfos);
        section.setClient(collectionEventTable);
        collectionEventTable.adaptToToolkit(toolkit, true);
        collectionEventTable
            .addClickListener(new IInfoTableDoubleClickItemListener<PatientCEventInfo>() {

                @SuppressWarnings("nls")
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
                            adapter.openViewForm();
                        } catch (ApplicationException e) {
                            BgcPlugin.openAsyncError(
                                i18n.tr("Unable to open form"),
                                i18n.tr("Error loading collection event."));
                        }
                    }
                    return;
                }
            });
        collectionEventTable
            .addEditItemListener(new IInfoTableEditItemListener<PatientCEventInfo>() {

                @SuppressWarnings("nls")
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
                                i18n.tr("Unable to open form"),
                                i18n.tr("Error loading collection event."));
                        }
                    }
                    return;
                }

            });
    }

    @SuppressWarnings("nls")
    @Override
    public void setValues() throws Exception {
        setPartName(i18n.tr("Patient {0}",
            patientInfo.patient.getPnumber()));
        form.setText(i18n.tr("Patient {0}",
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
