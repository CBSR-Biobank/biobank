package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.admin.StudyAdapter;
import edu.ualberta.med.biobank.widgets.infotables.AliquotedSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SourceSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactInfoTable;

public class StudyViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.StudyViewForm"; //$NON-NLS-1$

    private static final String DATE_PROCESSED_INFO_FIELD_NAME = Messages
        .getString("study.visit.info.dateProcessed"); //$NON-NLS-1$

    private StudyAdapter studyAdapter;
    private StudyWrapper study;

    private BgcBaseText nameLabel;
    private BgcBaseText nameShortLabel;
    private BgcBaseText activityStatusLabel;
    private BgcBaseText commentLabel;
    private BgcBaseText patientTotal;
    private BgcBaseText visitTotal;

    private StudyContactInfoTable contactsTable;
    private AliquotedSpecimenInfoTable aliquotedSpecimenTable;
    private SourceSpecimenInfoTable sourceSpecimenTable;

    private static class StudyPvCustomInfo extends PvAttrCustom {
        public BgcBaseText wiget;
    }

    private List<StudyPvCustomInfo> pvCustomInfoList;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof StudyAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        studyAdapter = (StudyAdapter) adapter;
        study = studyAdapter.getWrapper();
        // retrieve info from database because study could have been modified
        // after first opening
        study.reload();
        setPartName(Messages.getString(
            "StudyViewForm.title", study.getNameShort())); //$NON-NLS-1$
        pvCustomInfoList = new ArrayList<StudyPvCustomInfo>();
    }

    @Override
    protected void createFormContent() throws Exception {
        if (study.getName() != null) {
            form.setText(Messages.getString(
                "StudyViewForm.title", study.getName())); //$NON-NLS-1$
        }

        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite client = toolkit.createComposite(page);
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.name")); //$NON-NLS-1$
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.nameShort")); //$NON-NLS-1$
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("label.activity")); //$NON-NLS-1$
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.getString("label.comments")); //$NON-NLS-1$
        patientTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("StudyViewForm.field.label.total.patients")); //$NON-NLS-1$
        visitTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.getString("StudyViewForm.field.label.total.patientVisits")); //$NON-NLS-1$

        createClinicSection();
        createSourceSpecimenSection();
        createAliquotedSpecimenSection();
        createPvCustomInfoSection();
        setStudySectionValues();
        setPvDataSectionValues();
    }

    private void createClinicSection() {
        Composite client = createSectionWithClient(Messages
            .getString("StudyViewForm.clinic.title")); //$NON-NLS-1$

        contactsTable = new StudyContactInfoTable(client, study);
        contactsTable.addClickListener(collectionDoubleClickListener);
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);

        // contactsTable.addClickListener(new IDoubleClickListener() {
        // @Override
        // public void doubleClick(DoubleClickEvent event) {
        // Object selection = event.getSelection();
        // if (selection instanceof InfoTableSelection) {
        // Object obj = ((InfoTableSelection) selection).getObject();
        // if (obj instanceof ClinicWrapper) {
        // ClinicWrapper c = (ClinicWrapper) obj;
        // DoubleClickEvent newEvent = new DoubleClickEvent(
        // (Viewer) event.getSource(), new InfoTableSelection(
        // c));
        // collectionDoubleClickListener.doubleClick(newEvent);
        // } else {
        // Assert.isTrue(false,
        //                            "invalid InfoTableSelection class:" //$NON-NLS-1$
        // + obj.getClass().getName());
        // }
        // } else {
        //                    Assert.isTrue(false, "invalid class for event selection:" //$NON-NLS-1$
        // + event.getClass().getName());
        // }
        // }
        // });
    }

    private void setStudySectionValues() throws Exception {
        setTextValue(nameLabel, study.getName());
        setTextValue(nameShortLabel, study.getNameShort());
        setTextValue(activityStatusLabel, study.getActivityStatus());
        setTextValue(commentLabel, study.getComment());
        setTextValue(patientTotal, study.getPatientCount(true));
        setTextValue(visitTotal, study.getCollectionEventCount(true));
    }

    private void createAliquotedSpecimenSection() {
        Section section = createSection(Messages
            .getString("StudyViewForm.aliquoted.specimen.title")); //$NON-NLS-1$

        aliquotedSpecimenTable = new AliquotedSpecimenInfoTable(section,
            study.getAliquotedSpecimenCollection(true));
        section.setClient(aliquotedSpecimenTable);
        aliquotedSpecimenTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(aliquotedSpecimenTable);
    }

    private void createSourceSpecimenSection() {
        Section section = createSection(Messages
            .getString("StudyViewForm.source.specimen.title")); //$NON-NLS-1$

        sourceSpecimenTable = new SourceSpecimenInfoTable(section,
            study.getSourceSpecimenCollection(true));
        section.setClient(sourceSpecimenTable);
        sourceSpecimenTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sourceSpecimenTable);
    }

    private void createPvCustomInfoSection() throws Exception {
        Composite client = createSectionWithClient(Messages
            .getString("StudyViewForm.visit.info.attributes.title")); //$NON-NLS-1$
        client.setLayout(new GridLayout(1, false));

        StudyPvCustomInfo combinedPvInfo;

        combinedPvInfo = new StudyPvCustomInfo();
        combinedPvInfo.setLabel(DATE_PROCESSED_INFO_FIELD_NAME); //$NON-NLS-1$
        combinedPvInfo.setType(EventAttrTypeEnum.DATE_TIME); //$NON-NLS-1$
        pvCustomInfoList.add(combinedPvInfo);

        for (String label : study.getStudyEventAttrLabels()) {
            combinedPvInfo = new StudyPvCustomInfo();
            combinedPvInfo.setLabel(label);
            combinedPvInfo.setType(study.getStudyEventAttrType(label));
            combinedPvInfo.setAllowedValues(study
                .getStudyEventAttrPermissible(label));
            pvCustomInfoList.add(combinedPvInfo);
        }

        if (pvCustomInfoList.size() == 0) {
            toolkit.createLabel(client,
                Messages.getString("StudyViewForm.visit.info.msg")); //$NON-NLS-1$
            return;
        }

        Composite subcomp;
        for (StudyPvCustomInfo pvCustomInfo : pvCustomInfoList) {
            subcomp = toolkit.createComposite(client);
            subcomp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            if (pvCustomInfo.getAllowedValues() != null) {
                subcomp.setLayout(new GridLayout(2, false));

                pvCustomInfo.wiget = createReadOnlyLabelledField(subcomp,
                    SWT.NONE, pvCustomInfo.getLabel());
            } else {
                subcomp.setLayout(new GridLayout(1, false));
                toolkit.createLabel(subcomp, pvCustomInfo.getLabel());
            }
        }
    }

    private void setPvDataSectionValues() throws Exception {
        for (StudyPvCustomInfo pvCustomInfo : pvCustomInfoList) {
            String label = pvCustomInfo.getLabel();
            if (label.equals(DATE_PROCESSED_INFO_FIELD_NAME)) { //$NON-NLS-1$
                // skip this attribute since its already part of PatientVisit
                continue;
            }
            setTextValue(pvCustomInfo.wiget, StringUtils.join(
                study.getStudyEventAttrPermissible(label), ";")); //$NON-NLS-1$
        }
    }

    @Override
    public void reload() throws Exception {
        study.reload();
        setPartName(Messages.getString(
            "StudyViewForm.title", study.getNameShort())); //$NON-NLS-1$
        form.setText(Messages.getString(
            Messages.getString("StudyViewForm.title"), study.getName())); //$NON-NLS-1$
        setStudySectionValues();
        setPvDataSectionValues();
        aliquotedSpecimenTable.setCollection(study
            .getAliquotedSpecimenCollection(true));
        sourceSpecimenTable.setCollection(study
            .getSourceSpecimenCollection(true));
        contactsTable.setCollection(study.getContactCollection(true));
    }
}
