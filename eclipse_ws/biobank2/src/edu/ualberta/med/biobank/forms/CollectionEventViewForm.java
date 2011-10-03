package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable;

public class CollectionEventViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CollectionEventViewForm"; //$NON-NLS-1$

    private CollectionEvent cevent;

    private BgcBaseText studyLabel;

    private List<FormPvCustomInfo> pvCustomInfoList;

    private BgcBaseText patientLabel;

    private BgcBaseText visitNumberLabel;

    private BgcBaseText commentLabel;

    private SpecimenInfoTable sourceSpecimenTable;

    private BgcBaseText activityStatusLabel;

    private SpecimenInfoTable aliquotedSpcTable;

    private static class FormPvCustomInfo extends PvAttrCustom {
        BgcBaseText widget;
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof CollectionEventAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        cevent = ((CollectionEventAdapter) adapter).getModelObject();
        // FIXME
        // SessionManager.logLookup(new CollectionEventWrapper(SessionManager
        // .getAppService(), cevent));

        setPartName(NLS.bind(Messages.CollectionEventViewForm_title,
            cevent.getVisitNumber()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.CollectionEventViewForm_main_title,
            +cevent.getVisitNumber()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createMainSection();
        createSourceSpecimensSection();
        createAliquotedSpecimensSection();
    }

    private void createMainSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.CollectionEventViewForm_study_label);
        patientLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.CollectionEventViewForm_patient_label);
        visitNumberLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.CollectionEventViewForm_visitNber_label);
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_activity);

        createPvDataSection(client);

        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.label_comments);

        setCollectionEventValues();
    }

    private void createPvDataSection(Composite client) throws Exception {
        // FIXME
        // StudyWrapper study = new StudyWrapper(SessionManager.getAppService(),
        // cevent.getPatient().getStudy());
        // String[] labels = study.getStudyEventAttrLabels();
        // if (labels == null)
        // return;

        pvCustomInfoList = new ArrayList<FormPvCustomInfo>();

        // for (String label : labels) {
        // FormPvCustomInfo combinedPvInfo = new FormPvCustomInfo();
        // combinedPvInfo.setLabel(label);
        // combinedPvInfo.setType(study.getStudyEventAttrType(label));
        //
        // int style = SWT.NONE;
        // if (combinedPvInfo.getType() == EventAttrTypeEnum.SELECT_MULTIPLE) {
        // style |= SWT.WRAP;
        // }
        //
        // // FIXME
        // String value = new CollectionEventWrapper(
        // SessionManager.getAppService(), cevent)
        // .getEventAttrValue(label);
        // if (combinedPvInfo.getType() == EventAttrTypeEnum.SELECT_MULTIPLE
        // && (value != null)) {
        // combinedPvInfo.setValue(value.replace(';', '\n'));
        // } else {
        // combinedPvInfo.setValue(value);
        // }
        //
        // combinedPvInfo.widget = createReadOnlyLabelledField(client, style,
        // label, combinedPvInfo.getValue());
        // GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        // combinedPvInfo.widget.setLayoutData(gd);
        //
        // pvCustomInfoList.add(combinedPvInfo);
        // }
    }

    private void setCollectionEventValues() {
        setTextValue(studyLabel, cevent.getPatient().getStudy().getName());
        setTextValue(patientLabel, cevent.getPatient().getPnumber());
        setTextValue(visitNumberLabel, cevent.getVisitNumber());
        setTextValue(activityStatusLabel, cevent.getActivityStatus().getName());
        setTextValue(commentLabel, cevent.getComment());
        // assign PvInfo
        for (FormPvCustomInfo combinedPvInfo : pvCustomInfoList) {
            setTextValue(combinedPvInfo.widget, combinedPvInfo.getValue());
        }
    }

    private void createSourceSpecimensSection() {
        Composite client = createSectionWithClient(Messages.CollectionEventViewForm_sourcespecimens_title);
        // FIXME
        // sourceSpecimenTable = new SpecimenInfoTable(client,
        // new CollectionEventWrapper(SessionManager.getAppService(), cevent)
        // .getOriginalSpecimenCollection(true),
        // ColumnsShown.SOURCE_SPECIMENS, 10);
        // sourceSpecimenTable.adaptToToolkit(toolkit, true);
        // sourceSpecimenTable.addClickListener(collectionDoubleClickListener);
        // sourceSpecimenTable.createDefaultEditItem();
    }

    private void createAliquotedSpecimensSection() {
        // FIXME should we show that to clinics ?
        Composite client = createSectionWithClient(Messages.CollectionEventViewForm_aliquotedspecimens_title);
        // FIXME
        // aliquotedSpcTable = new SpecimenInfoTable(client,
        // new CollectionEventWrapper(SessionManager.getAppService(), cevent)
        // .getAliquotedSpecimenCollection(true), ColumnsShown.ALIQUOTS,
        // 10);
        // aliquotedSpcTable.adaptToToolkit(toolkit, true);
        // aliquotedSpcTable.addClickListener(collectionDoubleClickListener);
        // aliquotedSpcTable.createDefaultEditItem();
    }

    @Override
    public void reload() throws Exception {
        // FIXME
        // cevent.reload();
        setPartName(NLS.bind(Messages.CollectionEventViewForm_title,
            cevent.getVisitNumber()));
        form.setText(NLS.bind(Messages.CollectionEventViewForm_main_title,
            +cevent.getVisitNumber()));
        setCollectionEventValues();
        // FIXME
        // sourceSpecimenTable.setCollection(new CollectionEventWrapper(
        // SessionManager.getAppService(), cevent)
        // .getOriginalSpecimenCollection(true));
        // aliquotedSpcTable.setCollection(new CollectionEventWrapper(
        // SessionManager.getAppService(), cevent)
        // .getAliquotedSpecimenCollection(true));
    }

}
