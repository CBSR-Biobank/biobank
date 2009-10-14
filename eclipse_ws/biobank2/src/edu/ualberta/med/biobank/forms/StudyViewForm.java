package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.PvCustomInfo;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.infotables.SampleSourceInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SampleStorageInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactInfoTable;

public class StudyViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.StudyViewForm";

    private StudyAdapter studyAdapter;
    private StudyWrapper studyWrapper;

    private Label siteLabel;
    private Label nameShortLabel;
    private Label activityStatusLabel;
    private Label commentLabel;

    private StudyContactInfoTable contactsTable;
    private SampleStorageInfoTable sampleStorageTable;
    private SampleSourceInfoTable sampleSourceTable;

    private class StudyPvCustomInfo extends PvCustomInfo {
        public Label wiget;
    }

    private List<StudyPvCustomInfo> pvCustomInfoList;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof StudyAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        studyAdapter = (StudyAdapter) adapter;
        studyWrapper = studyAdapter.getWrapper();
        // retrieve info from database because study could have been modified
        // after first opening
        studyWrapper.reload();
        setPartName("Study " + studyWrapper.getNameShort());
        pvCustomInfoList = new ArrayList<StudyPvCustomInfo>();
    }

    @Override
    protected void createFormContent() throws Exception {
        if (studyWrapper.getName() != null) {
            form.setText("Study: " + studyWrapper.getName());
        }

        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_STUDY));

        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        siteLabel = (Label) createWidget(client, Label.class, SWT.NONE, "Site");
        nameShortLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Short Name");
        activityStatusLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Activity Status");
        commentLabel = (Label) createWidget(client, Label.class, SWT.WRAP,
            "Comments");

        createClinicSection();
        createSampleStorageSection();
        createSampleSourceSection();
        createPvCustomInfoSection();
        setStudySectionValues();
        setPvDataSectionValues();
    }

    private void createClinicSection() {
        Composite client = createSectionWithClient("Clinics");

        contactsTable = new StudyContactInfoTable(client, studyWrapper);
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);

        contactsTable.getTableViewer().addDoubleClickListener(
            FormUtils.getBiobankCollectionDoubleClickListener());
    }

    private void setStudySectionValues() {
        FormUtils.setTextValue(siteLabel, studyWrapper.getSite().getName());
        FormUtils.setTextValue(nameShortLabel, studyWrapper.getNameShort());
        FormUtils.setTextValue(activityStatusLabel, studyWrapper
            .getActivityStatus());
        FormUtils.setTextValue(commentLabel, studyWrapper.getComment());
    }

    private void createSampleStorageSection() {
        Section section = createSection("Sample Storage");

        sampleStorageTable = new SampleStorageInfoTable(section, studyWrapper
            .getSampleStorageCollection());
        section.setClient(sampleStorageTable);
        sampleStorageTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sampleStorageTable);
    }

    private void createSampleSourceSection() {
        Section section = createSection("Source Vessels");
        sampleSourceTable = new SampleSourceInfoTable(section, studyWrapper
            .getSampleSourceCollection());
        section.setClient(sampleSourceTable);
        sampleStorageTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sampleStorageTable);
    }

    private void createPvCustomInfoSection() throws Exception {
        Composite client = createSectionWithClient("Patient Visit Information Collected");
        client.setLayout(new GridLayout(1, false));

        for (String label : studyWrapper.getPvInfoLabels()) {
            StudyPvCustomInfo combinedPvInfo = new StudyPvCustomInfo();
            combinedPvInfo.label = label;
            combinedPvInfo.type = studyWrapper.getPvInfoType(label);
            combinedPvInfo.allowedValues = studyWrapper
                .getPvInfoAllowedValues(label);
            pvCustomInfoList.add(combinedPvInfo);
        }

        if (pvCustomInfoList.size() == 0) {
            toolkit.createLabel(client,
                "Study does not collect additional patient visit information");
            return;
        }

        Composite subcomp;
        for (StudyPvCustomInfo pvCustomInfo : pvCustomInfoList) {
            subcomp = toolkit.createComposite(client);
            subcomp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            if (pvCustomInfo.allowedValues != null) {
                subcomp.setLayout(new GridLayout(2, false));

                pvCustomInfo.wiget = (Label) createWidget(subcomp, Label.class,
                    SWT.NONE, pvCustomInfo.label);
            } else {
                subcomp.setLayout(new GridLayout(1, false));
                toolkit.createLabel(subcomp, pvCustomInfo.label);
            }
        }
    }

    private void setPvDataSectionValues() throws Exception {
        for (StudyPvCustomInfo pvCustomInfo : pvCustomInfoList) {
            FormUtils.setTextValue(pvCustomInfo.wiget, StringUtils.join(
                studyWrapper.getPvInfoAllowedValues(pvCustomInfo.label), "; "));
        }
    }

    @Override
    protected void reload() throws Exception {
        studyWrapper.reload();
        setPartName("Study " + studyWrapper.getNameShort());
        form.setText("Study: " + studyWrapper.getName());
        setStudySectionValues();
        setPvDataSectionValues();
        contactsTable.getTableViewer().setInput(
            studyWrapper.getContactCollection());
    }

    @Override
    protected String getEntryFormId() {
        return StudyEntryForm.ID;
    }
}
