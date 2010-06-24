package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableSelection;
import edu.ualberta.med.biobank.widgets.infotables.SampleStorageInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.StudyContactInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.StudySourceVesselInfoTable;

public class StudyViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.StudyViewForm";

    private StudyAdapter studyAdapter;
    private StudyWrapper study;

    private BiobankText siteLabel;
    private BiobankText nameLabel;
    private BiobankText nameShortLabel;
    private BiobankText activityStatusLabel;
    private BiobankText commentLabel;
    private BiobankText patientTotal;
    private BiobankText visitTotal;

    private StudyContactInfoTable contactsTable;
    private SampleStorageInfoTable sampleStorageTable;
    private StudySourceVesselInfoTable studySourceVesselsTable;

    private class StudyPvCustomInfo extends PvAttrCustom {
        public BiobankText wiget;
    }

    private List<StudyPvCustomInfo> pvCustomInfoList;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof StudyAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        studyAdapter = (StudyAdapter) adapter;
        study = studyAdapter.getWrapper();
        // retrieve info from database because study could have been modified
        // after first opening
        study.reload();
        setPartName("Study " + study.getNameShort());
        pvCustomInfoList = new ArrayList<StudyPvCustomInfo>();
    }

    @Override
    protected void createFormContent() throws Exception {
        if (study.getName() != null) {
            form.setText("Study: " + study.getName());
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

        siteLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Repository Site");
        nameLabel = createReadOnlyLabelledField(client, SWT.NONE, "Name");
        nameShortLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Short Name");
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Activity Status");
        commentLabel = createReadOnlyLabelledField(client, SWT.MULTI,
            "Comments");
        patientTotal = createReadOnlyLabelledField(client, SWT.NONE,
            "Total Patients");
        visitTotal = createReadOnlyLabelledField(client, SWT.NONE,
            "Total Patient Visits");

        createClinicSection();
        createSampleStorageSection();
        createSourceVesselSection();
        createPvCustomInfoSection();
        setStudySectionValues();
        setPvDataSectionValues();
    }

    private void createClinicSection() {
        Composite client = createSectionWithClient("Clinic Information");

        contactsTable = new StudyContactInfoTable(client, study);
        contactsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(contactsTable);

        contactsTable.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                if (selection instanceof InfoTableSelection) {
                    Object obj = ((InfoTableSelection) selection).getObject();
                    if (obj instanceof ContactWrapper) {
                        ContactWrapper contact = (ContactWrapper) obj;
                        DoubleClickEvent newEvent = new DoubleClickEvent(
                            (Viewer) event.getSource(), new InfoTableSelection(
                                contact.getClinic()));
                        collectionDoubleClickListener.doubleClick(newEvent);
                    } else {
                        Assert.isTrue(false,
                            "invalid InfoTableSelection class:"
                                + obj.getClass().getName());
                    }
                } else {
                    Assert.isTrue(false, "invalid class for event selection:"
                        + event.getClass().getName());
                }
            }
        });
    }

    private void setStudySectionValues() throws Exception {
        setTextValue(siteLabel, study.getSite().getName());
        setTextValue(nameLabel, study.getName());
        setTextValue(nameShortLabel, study.getNameShort());
        setTextValue(activityStatusLabel, study.getActivityStatus());
        setTextValue(commentLabel, study.getComment());
        setTextValue(patientTotal, study.getPatientCount());
        setTextValue(visitTotal, study.getPatientVisitCount());
    }

    private void createSampleStorageSection() {
        Section section = createSection("Sample Storage");

        sampleStorageTable = new SampleStorageInfoTable(section, study
            .getSampleStorageCollection(true));
        section.setClient(sampleStorageTable);
        sampleStorageTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(sampleStorageTable);
    }

    private void createSourceVesselSection() {
        Section section = createSection("Source Vessels");

        studySourceVesselsTable = new StudySourceVesselInfoTable(section, study
            .getStudySourceVesselCollection(true));
        section.setClient(studySourceVesselsTable);
        studySourceVesselsTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(studySourceVesselsTable);
    }

    private void createPvCustomInfoSection() throws Exception {
        Composite client = createSectionWithClient("Patient Visit Information Collected");
        client.setLayout(new GridLayout(1, false));

        StudyPvCustomInfo combinedPvInfo;

        combinedPvInfo = new StudyPvCustomInfo();
        combinedPvInfo.setLabel("Date Processed");
        combinedPvInfo.setType("date");
        pvCustomInfoList.add(combinedPvInfo);

        for (String label : study.getStudyPvAttrLabels()) {
            combinedPvInfo = new StudyPvCustomInfo();
            combinedPvInfo.setLabel(label);
            combinedPvInfo.setType(study.getStudyPvAttrType(label));
            combinedPvInfo.setAllowedValues(study
                .getStudyPvAttrPermissible(label));
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
            if (label.equals("Date Processed")) {
                // skip this attribute since its already part of PatientVisit
                continue;
            }
            setTextValue(pvCustomInfo.wiget, StringUtils.join(study
                .getStudyPvAttrPermissible(label), "; "));
        }
    }

    @Override
    protected void reload() throws Exception {
        study.reload();
        setPartName("Study " + study.getNameShort());
        form.setText("Study: " + study.getName());
        setStudySectionValues();
        setPvDataSectionValues();
        sampleStorageTable
            .setCollection(study.getSampleStorageCollection(true));
        studySourceVesselsTable.setCollection(study
            .getStudySourceVesselCollection(true));
        contactsTable.setCollection(study.getContactCollection(true));
    }

}
