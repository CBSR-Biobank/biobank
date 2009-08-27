package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.infotables.SampleSourceInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SampleStorageInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactInfoTable;

public class StudyViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.StudyViewForm";

    private StudyAdapter studyAdapter;
    private Study study;

    private Label nameShortLabel;
    private Label activityStatusLabel;
    private Label commentLabel;

    private StudyContactInfoTable clinicsTable;
    private SampleStorageInfoTable sampleStorageTable;
    private SampleSourceInfoTable sampleSourceTable;

    private List<PvInfoLabelPair> pvInfoControlList;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof StudyAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        studyAdapter = (StudyAdapter) adapter;

        // retrieve info from database because could have been modified
        // after first opening
        retrieveStudy();
        setPartName("Study " + study.getNameShort());
        pvInfoControlList = new ArrayList<PvInfoLabelPair>();
    }

    @Override
    protected void createFormContent() throws Exception {
        if (study.getName() != null) {
            form.setText("Study: " + study.getName());
        }

        addRefreshToolbarAction();

        GridLayout layout = new GridLayout(1, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite client = toolkit.createComposite(form.getBody());
        client.setLayout(new GridLayout(2, false));
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameShortLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Short Name");
        activityStatusLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Activity Status");
        commentLabel = (Label) createWidget(client, Label.class, SWT.WRAP,
            "Comments");

        createClinicSection();
        createSampleStorageSection();
        createSampleSourceSection();
        createPvDataSection();

        initEditButton(client, studyAdapter);
        setStudySectionValues();
        setPvDataSectionValues();

    }

    private void createClinicSection() {
        Composite client = createSectionWithClient("Clinics");

        clinicsTable = new StudyContactInfoTable(client, appService, study);
        clinicsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(clinicsTable);

        clinicsTable.getTableViewer().addDoubleClickListener(
            FormUtils.getBiobankCollectionDoubleClickListener());
    }

    private void setStudySectionValues() {
        FormUtils.setTextValue(nameShortLabel, study.getNameShort());
        FormUtils.setTextValue(activityStatusLabel, study.getActivityStatus());
        FormUtils.setTextValue(commentLabel, study.getComment());
    }

    private void createSampleStorageSection() {
        Section section = createSection("Sample Storage");

        sampleStorageTable = new SampleStorageInfoTable(section, study
            .getSampleStorageCollection());
        section.setClient(sampleStorageTable);
        sampleStorageTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sampleStorageTable);
    }

    private void createSampleSourceSection() {
        Section section = createSection("Source Vessels");
        sampleSourceTable = new SampleSourceInfoTable(section, study
            .getSampleSourceCollection());
        section.setClient(sampleSourceTable);
        sampleStorageTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sampleStorageTable);
    }

    private void createPvDataSection() {
        Composite client = createSectionWithClient("Patient Visit Information Collected");
        client.setLayout(new GridLayout(1, false));

        Collection<PvInfo> pvInfos = study.getPvInfoCollection();
        if ((pvInfos == null) || (pvInfos.size() == 0)) {
            toolkit.createLabel(client,
                "Study does not collect additional patient visit information");
            return;
        }

        Composite subcomp;
        for (PvInfo pvInfo : pvInfos) {
            subcomp = toolkit.createComposite(client);
            subcomp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            if (pvInfo.getPossibleValues() != null) {
                subcomp.setLayout(new GridLayout(2, false));

                PvInfoLabelPair pair = new PvInfoLabelPair();
                pair.pvInfo = pvInfo;
                pair.label = (Label) createWidget(subcomp, Label.class,
                    SWT.NONE, pvInfo.getLabel());
                pvInfoControlList.add(pair);
            } else {
                subcomp.setLayout(new GridLayout(1, false));
                toolkit.createLabel(subcomp, pvInfo.getLabel());
            }
        }
    }

    private void setPvDataSectionValues() {
        for (PvInfoLabelPair pair : pvInfoControlList) {
            FormUtils.setTextValue(pair.label, pair.pvInfo.getPossibleValues()
                .replaceAll(";", "; "));
        }
    }

    @Override
    protected void reload() throws Exception {
        retrieveStudy();
        setPartName("Study " + study.getNameShort());
        form.setText("Study: " + study.getName());
        setStudySectionValues();
        setPvDataSectionValues();

        AdapterBase clinicGroupNode = ((SiteAdapter) studyAdapter.getParent()
            .getParent()).getClinicGroupNode();
        clinicsTable.getTableViewer().setInput(
            FormUtils.getClinicsAdapters(clinicGroupNode, ModelUtils
                .getStudyClinicCollection(appService, study)));
    }

    private void retrieveStudy() {
        try {
            study = ModelUtils.getObjectWithId(studyAdapter.getAppService(),
                Study.class, studyAdapter.getStudy().getId());
            studyAdapter.setStudy(study);
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while retrieving study "
                    + studyAdapter.getStudy().getName(), e);
        }
    }

    @Override
    protected String getEntryFormId() {
        return StudyEntryForm.ID;
    }
}
