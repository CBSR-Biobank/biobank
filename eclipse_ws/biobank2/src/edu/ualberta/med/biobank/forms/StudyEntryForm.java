package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.exception.UserUIException;
import edu.ualberta.med.biobank.model.PvAttrCustom;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.ClinicAddWidget;
import edu.ualberta.med.biobank.widgets.PvInfoWidget;
import edu.ualberta.med.biobank.widgets.SampleStorageEntryWidget;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.multiselect.MultiSelectWidget;

@SuppressWarnings("serial")
public class StudyEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.StudyEntryForm";

    private static final String MSG_NEW_STUDY_OK = "Creating a new study.";

    private static final String MSG_STUDY_OK = "Editing an existing study.";

    public static final String[] ORDERED_FIELDS = new String[] { "name",
        "nameShort", "activityStatus", "comment" };

    public static final ListOrderedMap FIELDS = new ListOrderedMap() {
        {
            put("name", new FieldInfo("Name", Text.class, SWT.NONE, null,
                NonEmptyStringValidator.class, "Study name cannot be blank"));
            put("nameShort", new FieldInfo("Short Name", Text.class, SWT.NONE,
                null, NonEmptyStringValidator.class,
                "Study short name cannot be blank"));
            put("activityStatus", new FieldInfo("Activity Status", Combo.class,
                SWT.NONE, FormConstants.ACTIVITY_STATUS, null, null));
            put("comment", new FieldInfo("Comments", Text.class, SWT.MULTI,
                null, null, null));
        }
    };

    private StudyAdapter studyAdapter;

    private StudyWrapper study;

    private ClinicAddWidget contactEntryWidget;

    private Collection<SampleSourceWrapper> allSampleSources;

    private MultiSelectWidget sampleSourceMultiSelect;

    private List<StudyPvAttrCustom> pvCustomInfoList;

    private SampleStorageEntryWidget sampleStorageEntryWidget;

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

        Label siteLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Site");
        setTextValue(siteLabel, study.getSite().getName());

        createBoundWidgetsFromMap(FIELDS, study, client);

        firstControl = controls.get("name");
        Assert.isNotNull(firstControl, "name field does not exist");

        Text comments = (Text) controls.get("comment");
        GridData gd = (GridData) comments.getLayoutData();
        gd.heightHint = 40;

        createClinicSection();
        createSampleStorageSection();
        createSourceVesselsSection();
        createPvCustomInfoSection();
        createButtonsSection();
    }

    private void createClinicSection() {
        Composite client = createSectionWithClient("Clinics / Contacts");

        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        contactEntryWidget = new ClinicAddWidget(client, SWT.NONE, study,
            toolkit);
        contactEntryWidget.addSelectionChangedListener(listener);
    }

    private void createSampleStorageSection() {
        Composite client = createSectionWithClient("Sample Storage");

        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        sampleStorageEntryWidget = new SampleStorageEntryWidget(client,
            SWT.NONE, study.getSite(), study.getSampleStorageCollection(),
            toolkit);
        sampleStorageEntryWidget.addSelectionChangedListener(listener);
    }

    private void createSourceVesselsSection() throws Exception {
        Composite client = createSectionWithClient("Source Vessels");
        allSampleSources = SampleSourceWrapper.getAllSampleSources(appService);
        sampleSourceMultiSelect = new MultiSelectWidget(client, SWT.NONE,
            "Selected Source Vessels", "Available Source Vessels", 100);
        sampleSourceMultiSelect.adaptToToolkit(toolkit, true);
        sampleSourceMultiSelect.addSelectionChangedListener(listener);
        setSampleSourceWidgetSelections();
    }

    public void setSampleSourceWidgetSelections() {
        Collection<SampleSourceWrapper> studySampleSources = study
            .getSampleSourceCollection();
        ListOrderedMap availSampleSource = new ListOrderedMap();
        List<Integer> selSampleSource = new ArrayList<Integer>();

        if (studySampleSources != null) {
            for (SampleSourceWrapper ss : studySampleSources) {
                selSampleSource.add(ss.getId());
            }
        }

        for (SampleSourceWrapper ss : allSampleSources) {
            availSampleSource.put(ss.getId(), ss.getName());
        }
        sampleSourceMultiSelect.setSelections(availSampleSource,
            selSampleSource);
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
        // get the selected sample sources from widget
        List<Integer> selSampleSourceIds = sampleSourceMultiSelect
            .getSelected();
        List<SampleSourceWrapper> selSampleSource = new ArrayList<SampleSourceWrapper>();
        for (SampleSourceWrapper ss : allSampleSources) {
            int id = ss.getId();
            if (selSampleSourceIds.indexOf(id) >= 0) {
                selSampleSource.add(ss);
            }
        }
        Assert.isTrue(selSampleSource.size() == selSampleSourceIds.size(),
            "problem with sample source selections");
        study.setSampleSourceCollection(selSampleSource);

        // get pv infos
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
                    }
                } else {
                    study.setStudyPvAttr(studyPvAttrCustom.getLabel(),
                        studyPvAttrCustom.getType());
                }
            }
        }

        // get sample storages
        study.setSampleStorageCollection(sampleStorageEntryWidget
            .getSampleStorage());

        study.setContactCollection(contactEntryWidget.getContacts());
        SiteAdapter siteAdapter = studyAdapter
            .getParentFromClass(SiteAdapter.class);
        study.setSite(siteAdapter.getWrapper());

        study.persist();

        studyAdapter.getParent().performExpand();
    }

    @Override
    public String getNextOpenedFormID() {
        return StudyViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        List<ContactWrapper> contacts = study.getContactCollection();
        if (contacts != null) {
            contactEntryWidget.setContacts(contacts);
        }

        List<SampleStorageWrapper> sampleStorages = study
            .getSampleStorageCollection();
        if (sampleStorages != null) {
            sampleStorageEntryWidget.setSampleStorage(sampleStorages);
        }

        setSampleSourceWidgetSelections();

        // TODO reset PV CUSTOM INFO

    }
}
