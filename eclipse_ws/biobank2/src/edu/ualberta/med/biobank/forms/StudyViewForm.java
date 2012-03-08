package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.study.StudyGetInfoAction;
import edu.ualberta.med.biobank.common.action.study.StudyInfo;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.EventAttrCustom;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.treeview.patient.StudyWithPatientAdapter;
import edu.ualberta.med.biobank.widgets.infotables.AliquotedSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SourceSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactInfoTable;

public class StudyViewForm extends BiobankViewForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.StudyViewForm"; //$NON-NLS-1$

    private static final String DATE_PROCESSED_INFO_FIELD_NAME =
        Messages.study_visit_info_dateProcessed;

    private StudyWrapper study =
        new StudyWrapper(SessionManager.getAppService());

    private BgcBaseText nameLabel;
    private BgcBaseText nameShortLabel;
    private BgcBaseText activityStatusLabel;
    private BgcBaseText patientTotal;
    private BgcBaseText visitTotal;

    private StudyContactInfoTable contactsTable;
    private AliquotedSpecimenInfoTable aliquotedSpecimenTable;
    private SourceSpecimenInfoTable sourceSpecimenTable;

    private StudyInfo studyInfo;

    private static class StudyEventAttrCustomInfo extends EventAttrCustom {
        public BgcBaseText widget;
    }

    private List<StudyEventAttrCustomInfo> pvCustomInfoList;

    private CommentCollectionInfoTable commentTable;

    @Override
    public void init() throws Exception {
        Assert
            .isTrue(
                (adapter instanceof StudyAdapter || adapter instanceof StudyWithPatientAdapter),
                "Invalid editor input: object of type " //$NON-NLS-1$
                    + adapter.getClass().getName());

        updateStudyInfo();
        setPartName(NLS
            .bind(Messages.StudyViewForm_title, study.getNameShort()));
        pvCustomInfoList = new ArrayList<StudyEventAttrCustomInfo>();
    }

    private void updateStudyInfo() throws Exception {
        studyInfo = SessionManager.getAppService().doAction(
            new StudyGetInfoAction(adapter.getId()));
        Assert.isNotNull(studyInfo.getStudy());
        study.setWrappedObject(studyInfo.getStudy());
    }

    @Override
    protected void createFormContent() throws Exception {
        if (study.getName() != null) {
            form.setText(NLS.bind(Messages.StudyViewForm_title, study.getName()));
        }

        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel =
            createReadOnlyLabelledField(client, SWT.NONE, Messages.label_name);
        nameShortLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_nameShort);
        activityStatusLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_activity);
        patientTotal =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.StudyViewForm_field_label_total_patients);
        visitTotal =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.StudyViewForm_field_label_total_cEvents);

        createCommentsSection();
        createClinicSection();
        createSourceSpecimenSection();
        createAliquotedSpecimenSection();
        createStudyEventAttrSection();
        setStudySectionValues();
        setStudyEventAttrValues();
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Messages.label_comments);
        commentTable =
            new CommentCollectionInfoTable(client,
                study.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void createClinicSection() {
        Composite client =
            createSectionWithClient(Messages.StudyViewForm_clinic_title);

        contactsTable = new StudyContactInfoTable(client, study);
        contactsTable.addClickListener(collectionDoubleClickListener);
        contactsTable.createDefaultEditItem();
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);
    }

    private void setStudySectionValues() throws Exception {
        setTextValue(nameLabel, study.getName());
        setTextValue(nameShortLabel, study.getNameShort());
        setTextValue(activityStatusLabel, study.getActivityStatus());
        setTextValue(patientTotal, study.getPatientCount(true));
        setTextValue(visitTotal, study.getCollectionEventCount());
    }

    private void createAliquotedSpecimenSection() {
        Section section =
            createSection(Messages.StudyViewForm_aliquoted_specimen_title);

        aliquotedSpecimenTable =
            new AliquotedSpecimenInfoTable(section,
                study.getAliquotedSpecimenCollection(true));
        section.setClient(aliquotedSpecimenTable);
        aliquotedSpecimenTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(aliquotedSpecimenTable);
    }

    private void createSourceSpecimenSection() {
        Section section =
            createSection(Messages.StudyViewForm_source_specimen_title);

        sourceSpecimenTable =
            new SourceSpecimenInfoTable(section,
                study.getSourceSpecimenCollection(true));
        section.setClient(sourceSpecimenTable);
        sourceSpecimenTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sourceSpecimenTable);
    }

    private void createStudyEventAttrSection() throws Exception {
        Composite client =
            createSectionWithClient(Messages.StudyViewForm_visit_info_attributes_title);
        client.setLayout(new GridLayout(1, false));

        StudyEventAttrCustomInfo combinedStudyEventAttrInfo;

        combinedStudyEventAttrInfo = new StudyEventAttrCustomInfo();
        combinedStudyEventAttrInfo.setLabel(DATE_PROCESSED_INFO_FIELD_NAME);
        combinedStudyEventAttrInfo.setType(EventAttrTypeEnum.DATE_TIME);
        pvCustomInfoList.add(combinedStudyEventAttrInfo);

        for (String label : study.getStudyEventAttrLabels()) {
            if (!study.getStudyEventAttrActivityStatus(label).equals(
                ActivityStatus.ACTIVE)) {
                continue;
            }
            combinedStudyEventAttrInfo = new StudyEventAttrCustomInfo();
            combinedStudyEventAttrInfo.setLabel(label);
            combinedStudyEventAttrInfo.setType(study
                .getStudyEventAttrType(label));
            combinedStudyEventAttrInfo.setAllowedValues(study
                .getStudyEventAttrPermissible(label));
            pvCustomInfoList.add(combinedStudyEventAttrInfo);
        }

        if (pvCustomInfoList.size() == 0) {
            toolkit.createLabel(client, Messages.StudyViewForm_visit_info_msg);
            return;
        }

        Composite subcomp;
        for (StudyEventAttrCustomInfo pvCustomInfo : pvCustomInfoList) {
            subcomp = toolkit.createComposite(client);
            subcomp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            if (pvCustomInfo.getAllowedValues() != null) {
                subcomp.setLayout(new GridLayout(2, false));

                pvCustomInfo.widget =
                    createReadOnlyLabelledField(subcomp, SWT.NONE,
                        pvCustomInfo.getLabel());
            } else {
                subcomp.setLayout(new GridLayout(1, false));
                toolkit.createLabel(subcomp, pvCustomInfo.getLabel());
            }
        }
    }

    private void setStudyEventAttrValues() throws Exception {
        for (StudyEventAttrCustomInfo pvCustomInfo : pvCustomInfoList) {
            String label = pvCustomInfo.getLabel();
            if (label.equals(DATE_PROCESSED_INFO_FIELD_NAME)) {
                // skip this attribute since its already part of PatientVisit
                continue;
            }
            setTextValue(pvCustomInfo.widget, StringUtils.join(
                study.getStudyEventAttrPermissible(label), ";")); //$NON-NLS-1$
        }
    }

    @Override
    public void setValues() throws Exception {
        setPartName(NLS
            .bind(Messages.StudyViewForm_title, study.getNameShort()));
        form.setText(NLS.bind(Messages.StudyViewForm_title, study.getName()));
        setStudySectionValues();
        setStudyEventAttrValues();
        aliquotedSpecimenTable.setList(study
            .getAliquotedSpecimenCollection(true));
        sourceSpecimenTable
            .setList(study.getSourceSpecimenCollection(true));
        contactsTable.setCollectionByStudy(study);
        commentTable.setList(study.getCommentCollection(false));
    }
}
