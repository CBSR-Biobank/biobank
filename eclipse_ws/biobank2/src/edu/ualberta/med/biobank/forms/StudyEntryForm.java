package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SourceVesselWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.exception.UserUIException;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.PvInfoWidget;
import edu.ualberta.med.biobank.widgets.infotables.ClinicAddInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SampleStorageEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

public class StudyEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.StudyEntryForm";

    private static final String MSG_NEW_STUDY_OK = "Creating a new study.";

    private static final String MSG_STUDY_OK = "Editing an existing study.";

    public static final String[] ORDERED_FIELDS = new String[] { "name",
        "nameShort", "activityStatus", "comment" };

    public static final Map<String, FieldInfo> FIELDS;
    static {
        Map<String, FieldInfo> aMap = new LinkedHashMap<String, FieldInfo>();
        aMap.put("name", new FieldInfo("Name", Text.class, SWT.NONE, null,
            NonEmptyStringValidator.class, "Study name cannot be blank"));
        aMap.put("nameShort", new FieldInfo("Short Name", Text.class, SWT.NONE,
            null, NonEmptyStringValidator.class,
            "Study short name cannot be blank"));
        aMap.put("activityStatus", new FieldInfo("Activity Status",
            Combo.class, SWT.NONE, FormConstants.ACTIVITY_STATUS, null, null));
        aMap.put("comment", new FieldInfo("Comments", Text.class, SWT.MULTI,
            null, null, null));
        FIELDS = Collections.unmodifiableMap(aMap);
    };

    private StudyAdapter studyAdapter;

    private StudyWrapper study;

    private ClinicAddInfoTable contactEntryTable;

    private Collection<SourceVesselWrapper> allSourceVessels;

    private MultiSelectWidget sourceVesselMultiSelect;

    private List<StudyPvAttrCustom> pvCustomInfoList;

    private SampleStorageEntryInfoTable sampleStorageEntryTable;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private class StudyPvAttrCustom extends PvAttrCustom {
        public PvInfoWidget widget;
        public boolean inStudy;
    }

    public StudyEntryForm() {
        super();
        pvCustomInfoList = new ArrayList<StudyPvAttrCustom>();
    }

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof StudyAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        studyAdapter = (StudyAdapter) adapter;
        study = studyAdapter.getWrapper();
        study.reload();

        String tabName;
        if (study.getId() == null) {
            tabName = "New Study";
        } else {
            tabName = "Study " + study.getNameShort();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Study Information");
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getImageRegistry().get(
            BioBankPlugin.IMG_STUDY));

        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Text siteLabel = createReadOnlyField(client, SWT.NONE,
            "Repository Site");
        setTextValue(siteLabel, study.getSite().getName());

        createBoundWidgetsFromMap(FIELDS, study, client);

        firstControl = getWidget("name");
        Assert.isNotNull(firstControl, "name field does not exist");

        Text comments = (Text) getWidget("comment");
        GridData gd = (GridData) comments.getLayoutData();
        gd.heightHint = 40;

        createClinicSection();
        createSampleStorageSection();
        createSourceVesselsSection();
        createPvCustomInfoSection();
        createButtonsSection();
    }

    private void createClinicSection() {
        Section section = createSection("Clinics / Contacts");
        contactEntryTable = new ClinicAddInfoTable(section, study);
        contactEntryTable.adaptToToolkit(toolkit, true);
        contactEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(section, "Add Clinic Contact",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    contactEntryTable.createClinicContact();
                }
            });
        section.setClient(contactEntryTable);
    }

    private void createSampleStorageSection() {
        Section section = createSection("Aliquot Storage");
        sampleStorageEntryTable = new SampleStorageEntryInfoTable(section,
            study.getSite(), study.getSampleStorageCollection());
        sampleStorageEntryTable.adaptToToolkit(toolkit, true);
        sampleStorageEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(section, "Add Aliquot Storage",
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    sampleStorageEntryTable.addSampleStorage();
                }
            });
        section.setClient(sampleStorageEntryTable);
    }

    private void createSourceVesselsSection() throws Exception {
        Composite client = createSectionWithClient("Source Vessels");
        allSourceVessels = SourceVesselWrapper.getAllSourceVessels(appService);
        sourceVesselMultiSelect = new MultiSelectWidget(client, SWT.NONE,
            "Selected Source Vessels", "Available Source Vessels", 100);
        sourceVesselMultiSelect.adaptToToolkit(toolkit, true);
        sourceVesselMultiSelect.addSelectionChangedListener(listener);
        setSourceVesselWidgetSelections();
    }

    public void setSourceVesselWidgetSelections() {
        Collection<SourceVesselWrapper> studySourceVessels = study
            .getSourceVesselCollection();
        LinkedHashMap<Integer, String> availSourceVessel = new LinkedHashMap<Integer, String>();
        List<Integer> selSourceVessel = new ArrayList<Integer>();

        if (studySourceVessels != null) {
            for (SourceVesselWrapper ss : studySourceVessels) {
                selSourceVessel.add(ss.getId());
            }
        }

        for (SourceVesselWrapper ss : allSourceVessels) {
            availSourceVessel.put(ss.getId(), ss.getName());
        }
        sourceVesselMultiSelect.setSelections(availSourceVessel,
            selSourceVessel);
    }

    private void createPvCustomInfoSection() throws Exception {
        Composite client = createSectionWithClient("Patient Visit Information Collected");
        GridLayout gl = (GridLayout) client.getLayout();
        gl.numColumns = 1;

        // START KLUDGE
        //
        // create "date processed" attribute - actually an attribute in
        // PatientVisit - but we just want to show the user that this
        // information is collected by default.
        String[] defaultFields = new String[] { "Date Processed" };
        StudyPvAttrCustom studyPvAttrCustom;

        for (String field : defaultFields) {
            studyPvAttrCustom = new StudyPvAttrCustom();
            studyPvAttrCustom.setLabel(field);
            studyPvAttrCustom.setType("date_time");
            studyPvAttrCustom.setIsDefault(true);
            studyPvAttrCustom.widget = new PvInfoWidget(client, SWT.NONE,
                studyPvAttrCustom, true);
            studyPvAttrCustom.inStudy = false;
            pvCustomInfoList.add(studyPvAttrCustom);
        }
        //
        // END KLUDGE

        SiteWrapper site = study.getSite();
        List<String> studyPvInfoLabels = Arrays.asList(study
            .getStudyPvAttrLabels());

        for (String label : site.getSitePvAttrLabels()) {
            boolean selected = false;
            studyPvAttrCustom = new StudyPvAttrCustom();
            studyPvAttrCustom.setLabel(label);
            studyPvAttrCustom.setType(site.getSitePvAttrTypeName(label));
            if (studyPvInfoLabels.contains(label)) {
                studyPvAttrCustom.setPermissible(study
                    .getStudyPvAttrPermissible(label));
                selected = true;
            }
            studyPvAttrCustom.setIsDefault(false);
            studyPvAttrCustom.widget = new PvInfoWidget(client, SWT.NONE,
                studyPvAttrCustom, selected);
            studyPvAttrCustom.widget.addSelectionChangedListener(listener);
            studyPvAttrCustom.inStudy = studyPvInfoLabels.contains(label);
            pvCustomInfoList.add(studyPvAttrCustom);
        }
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
    }

    @Override
    protected String getOkMessage() {
        if (study.getId() == null) {
            return MSG_NEW_STUDY_OK;
        }
        return MSG_STUDY_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        setSourceVessels();

        setStudyPvAttr();

        // sample storage
        study.addSampleStorage(sampleStorageEntryTable
            .getAddedOrModifiedSampleStorages());
        study.removeSampleStorages(sampleStorageEntryTable
            .getDeletedSampleStorages());

        study.addContacts(contactEntryTable.getAddedContacts());
        study.removeContacts(contactEntryTable.getRemovedContacts());

        SiteAdapter siteAdapter = studyAdapter
            .getParentFromClass(SiteAdapter.class);
        study.setSite(siteAdapter.getWrapper());

        study.persist();

        studyAdapter.getParent().performExpand();
    }

    private void setStudyPvAttr() throws Exception, UserUIException {
        List<String> newPvInfoLabels = new ArrayList<String>();
        for (StudyPvAttrCustom studyPvAttrCustom : pvCustomInfoList) {
            String label = studyPvAttrCustom.getLabel();
            if (label.equals("Date Processed"))
                continue;

            if (!studyPvAttrCustom.widget.getSelected()
                && studyPvAttrCustom.inStudy) {
                try {
                    study.deleteStudyPvAttr(studyPvAttrCustom.getLabel());
                } catch (BiobankCheckException e) {
                    throw new UserUIException(
                        "Cannot delete "
                            + label
                            + " from study since it is already in use by patient visits.",
                        e);
                }
            } else if (studyPvAttrCustom.widget.getSelected()) {
                newPvInfoLabels.add(studyPvAttrCustom.getLabel());
                String value = studyPvAttrCustom.widget.getValues();
                if (studyPvAttrCustom.getType().equals("select_single")
                    || studyPvAttrCustom.getType().equals("select_multiple")) {
                    if (value.length() > 0) {
                        study.setStudyPvAttr(studyPvAttrCustom.getLabel(),
                            studyPvAttrCustom.getType(), value.split(";"));
                    } else if (value.length() == 0) {
                        study.setStudyPvAttr(studyPvAttrCustom.getLabel(),
                            studyPvAttrCustom.getType(), null);
                    }
                } else {
                    study.setStudyPvAttr(studyPvAttrCustom.getLabel(),
                        studyPvAttrCustom.getType());
                }
            }
        }
    }

    private void setSourceVessels() {
        List<Integer> addedSourceVesselIds = sourceVesselMultiSelect
            .getAddedToSelection();
        List<Integer> removedSourceVesselIds = sourceVesselMultiSelect
            .getRemovedToSelection();
        List<SourceVesselWrapper> addedSourceVessels = new ArrayList<SourceVesselWrapper>();
        List<SourceVesselWrapper> removedSourceVessels = new ArrayList<SourceVesselWrapper>();
        if (allSourceVessels != null) {
            for (SourceVesselWrapper ss : allSourceVessels) {
                int id = ss.getId();
                if (addedSourceVesselIds.indexOf(id) >= 0) {
                    addedSourceVessels.add(ss);
                } else if (removedSourceVesselIds.indexOf(id) >= 0) {
                    removedSourceVessels.add(ss);
                }
            }
        }
        Assert.isTrue(addedSourceVessels.size() == addedSourceVesselIds.size(),
            "problem with added source vessel selections");
        study.addSourceVessels(addedSourceVessels);
        Assert.isTrue(removedSourceVessels.size() == removedSourceVesselIds
            .size(), "problem with removed source vessel selections");
        study.removeSourceVessels(removedSourceVessels);
    }

    @Override
    public String getNextOpenedFormID() {
        return StudyViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        contactEntryTable.loadContacts(study);

        List<SampleStorageWrapper> sampleStorages = study
            .getSampleStorageCollection();
        if (sampleStorages != null) {
            sampleStorageEntryTable.setSampleStorages(sampleStorages);
        }

        setSourceVesselWidgetSelections();

        // TODO reset PV CUSTOM INFO

    }
}
