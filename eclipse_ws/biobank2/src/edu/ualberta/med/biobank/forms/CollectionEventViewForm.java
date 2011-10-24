package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.collectionEvent.EventAttrInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.GetCollectionEventInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.GetCollectionEventInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.study.GetStudyEventAttrInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyEventAttrInfo;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.patient.CollectionEventAdapter;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable.ColumnsShown;

public class CollectionEventViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CollectionEventViewForm"; //$NON-NLS-1$

    private BgcBaseText studyLabel;

    private List<FormPvCustomInfo> pvCustomInfoList;

    private BgcBaseText patientLabel;

    private BgcBaseText visitNumberLabel;

    private BgcBaseText commentLabel;

    private NewSpecimenInfoTable sourceSpecimenTable;

    private BgcBaseText activityStatusLabel;

    private NewSpecimenInfoTable aliquotedSpcTable;

    private CEventInfo ceventInfo;

    private static class FormPvCustomInfo extends PvAttrCustom {
        BgcBaseText widget;
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof CollectionEventAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        updateCEventInfo();
        // FIXME log edit action?
        // SessionManager.logLookup(new CollectionEventWrapper(SessionManager
        // .getAppService(), cevent));

        setPartName(NLS.bind(Messages.CollectionEventViewForm_title,
            ceventInfo.cevent.getVisitNumber()));
    }

    private void updateCEventInfo() throws Exception {
        ceventInfo = SessionManager.getAppService().doAction(
            new GetCollectionEventInfoAction(adapter.getId()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.CollectionEventViewForm_main_title,
            +ceventInfo.cevent.getVisitNumber()));
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
        Map<Integer, StudyEventAttrInfo> studyAttrInfos = SessionManager
            .getAppService().doAction(
                new GetStudyEventAttrInfoAction(ceventInfo.cevent.getPatient()
                    .getStudy().getId()));

        pvCustomInfoList = new ArrayList<FormPvCustomInfo>();

        for (Entry<Integer, StudyEventAttrInfo> entry : studyAttrInfos
            .entrySet()) {
            FormPvCustomInfo combinedPvInfo = new FormPvCustomInfo();
            combinedPvInfo.setLabel(entry.getValue().attr.getLabel());
            combinedPvInfo.setType(entry.getValue().type);

            int style = SWT.NONE;
            if (combinedPvInfo.getType() == EventAttrTypeEnum.SELECT_MULTIPLE) {
                style |= SWT.WRAP;
            }

            EventAttrInfo eventAttr = ceventInfo.eventAttrs.get(entry.getKey());
            String value = eventAttr == null ? null : eventAttr.attr.getValue();
            if (combinedPvInfo.getType() == EventAttrTypeEnum.SELECT_MULTIPLE
                && (value != null)) {
                combinedPvInfo.setValue(value.replace(';', '\n'));
            } else {
                combinedPvInfo.setValue(value);
            }

            combinedPvInfo.widget = createReadOnlyLabelledField(client, style,
                entry.getValue().attr.getLabel(), combinedPvInfo.getValue());
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            combinedPvInfo.widget.setLayoutData(gd);

            pvCustomInfoList.add(combinedPvInfo);
        }
    }

    private void setCollectionEventValues() {
        setTextValue(studyLabel, ceventInfo.cevent.getPatient().getStudy()
            .getName());
        setTextValue(patientLabel, ceventInfo.cevent.getPatient().getPnumber());
        setTextValue(visitNumberLabel, ceventInfo.cevent.getVisitNumber());
        setTextValue(activityStatusLabel, ceventInfo.cevent.getActivityStatus()
            .getName());
        setTextValue(commentLabel, ceventInfo.cevent.getCommentCollection());
        // assign PvInfo
        for (FormPvCustomInfo combinedPvInfo : pvCustomInfoList) {
            setTextValue(combinedPvInfo.widget, combinedPvInfo.getValue());
        }
    }

    private void createSourceSpecimensSection() {
        Composite client = createSectionWithClient(Messages.CollectionEventViewForm_sourcespecimens_title);
        sourceSpecimenTable = new NewSpecimenInfoTable(client,
            ceventInfo.sourceSpecimenInfos,
            ColumnsShown.CEVENT_SOURCE_SPECIMENS, 10);
        sourceSpecimenTable.adaptToToolkit(toolkit, true);
        sourceSpecimenTable.addClickListener(collectionDoubleClickListener);
        sourceSpecimenTable.createDefaultEditItem();
    }

    private void createAliquotedSpecimensSection() {
        // FIXME should we show that to clinics ?
        Composite client = createSectionWithClient(Messages.CollectionEventViewForm_aliquotedspecimens_title);
        aliquotedSpcTable = new NewSpecimenInfoTable(client,
            ceventInfo.aliquotedSpecimenInfos,
            ColumnsShown.CEVENT_ALIQUOTED_SPECIMENS, 10);
        aliquotedSpcTable.adaptToToolkit(toolkit, true);
        aliquotedSpcTable.addClickListener(collectionDoubleClickListener);
        aliquotedSpcTable.createDefaultEditItem();
    }

    @Override
    public void reload() throws Exception {
        updateCEventInfo();
        setPartName(NLS.bind(Messages.CollectionEventViewForm_title,
            ceventInfo.cevent.getVisitNumber()));
        form.setText(NLS.bind(Messages.CollectionEventViewForm_main_title,
            +ceventInfo.cevent.getVisitNumber()));
        setCollectionEventValues();
        sourceSpecimenTable.setCollection(ceventInfo.sourceSpecimenInfos);
        aliquotedSpcTable.setCollection(ceventInfo.aliquotedSpecimenInfos);
    }

}
