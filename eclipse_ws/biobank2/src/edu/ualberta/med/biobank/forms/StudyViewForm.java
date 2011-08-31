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

    private static final String DATE_PROCESSED_INFO_FIELD_NAME = Messages.study_visit_info_dateProcessed;

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
        public BgcBaseText widget;
    }

    private List<StudyPvCustomInfo> pvCustomInfoList;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof StudyAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        study = (StudyWrapper) getModelObject();
        setPartName(NLS
            .bind(Messages.StudyViewForm_title, study.getNameShort()));
        pvCustomInfoList = new ArrayList<StudyPvCustomInfo>();
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

        nameLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_name);
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_nameShort);
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.label_activity);
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            Messages.label_comments);
        patientTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.StudyViewForm_field_label_total_patients);
        visitTotal = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.StudyViewForm_field_label_total_cEvents);

        createClinicSection();
        createSourceSpecimenSection();
        createAliquotedSpecimenSection();
        createPvCustomInfoSection();
        setStudySectionValues();
        setPvDataSectionValues();
    }

    private void createClinicSection() {
        Composite client = createSectionWithClient(Messages.StudyViewForm_clinic_title);

        contactsTable = new StudyContactInfoTable(client, study);
        contactsTable.addClickListener(collectionDoubleClickListener);
        contactsTable.createDefaultEditItem();
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
        Section section = createSection(Messages.StudyViewForm_aliquoted_specimen_title);

        aliquotedSpecimenTable = new AliquotedSpecimenInfoTable(section,
            study.getAliquotedSpecimenCollection(true));
        section.setClient(aliquotedSpecimenTable);
        aliquotedSpecimenTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(aliquotedSpecimenTable);
    }

    private void createSourceSpecimenSection() {
        Section section = createSection(Messages.StudyViewForm_source_specimen_title);

        sourceSpecimenTable = new SourceSpecimenInfoTable(section,
            study.getSourceSpecimenCollection(true));
        section.setClient(sourceSpecimenTable);
        sourceSpecimenTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sourceSpecimenTable);
    }

    private void createPvCustomInfoSection() throws Exception {
        Composite client = createSectionWithClient(Messages.StudyViewForm_visit_info_attributes_title);
        client.setLayout(new GridLayout(1, false));

        StudyPvCustomInfo combinedPvInfo;

        combinedPvInfo = new StudyPvCustomInfo();
        combinedPvInfo.setLabel(DATE_PROCESSED_INFO_FIELD_NAME);
        combinedPvInfo.setType(EventAttrTypeEnum.DATE_TIME);
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
            toolkit.createLabel(client, Messages.StudyViewForm_visit_info_msg);
            return;
        }

        Composite subcomp;
        for (StudyPvCustomInfo pvCustomInfo : pvCustomInfoList) {
            subcomp = toolkit.createComposite(client);
            subcomp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            if (pvCustomInfo.getAllowedValues() != null) {
                subcomp.setLayout(new GridLayout(2, false));

                pvCustomInfo.widget = createReadOnlyLabelledField(subcomp,
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
            if (label.equals(DATE_PROCESSED_INFO_FIELD_NAME)) {
                // skip this attribute since its already part of PatientVisit
                continue;
            }
            setTextValue(pvCustomInfo.widget, StringUtils.join(
                study.getStudyEventAttrPermissible(label), ";")); //$NON-NLS-1$
        }
    }

    @Override
    public void reload() throws Exception {
        study.reload();
        setPartName(NLS
            .bind(Messages.StudyViewForm_title, study.getNameShort()));
        form.setText(NLS.bind(Messages.StudyViewForm_title, study.getName()));
        setStudySectionValues();
        setPvDataSectionValues();
        aliquotedSpecimenTable.setCollection(study
            .getAliquotedSpecimenCollection(true));
        sourceSpecimenTable.setCollection(study
            .getSourceSpecimenCollection(true));
        contactsTable.setCollection(study.getContactCollection(true));
    }
}
