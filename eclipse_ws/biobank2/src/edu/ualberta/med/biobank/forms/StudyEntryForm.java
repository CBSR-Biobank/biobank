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
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.PvCustomInfo;
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

    private ListOrderedMap pvCustomInfoMap;

    private SampleStorageEntryWidget sampleStorageEntryWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private class StudyPvCustomInfo extends PvCustomInfo {
        public PvInfoWidget widget;
    }

    public StudyEntryForm() {
        super();
        pvCustomInfoMap = new ListOrderedMap();
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
        FormUtils.setTextValue(siteLabel, study.getSite().getName());

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
        Composite client = createSectionWithClient("Clinics");

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
        // create "date processed" - not really a pv info but we'll pretend
        // we just want to show the user that this information is collected
        // by default. Date drawn is already part of the PatientVisit class.
        String[] defaultFields = new String[] { "Date Processed" };

        for (String field : defaultFields) {
            StudyPvCustomInfo combinedPvInfo = new StudyPvCustomInfo();
            combinedPvInfo.label = field;
            combinedPvInfo.type = 3;
            combinedPvInfo.isDefault = true;
            combinedPvInfo.widget = new PvInfoWidget(client, SWT.NONE,
                combinedPvInfo, true);
            pvCustomInfoMap.put(field, combinedPvInfo);
        }
        //
        // END KLUDGE

        SiteWrapper site = study.getSite();
        List<String> studyPvInfoLabels = Arrays.asList(study.getPvInfoLabels());

        for (String label : site.getPvInfoPossibleLabels()) {
            boolean selected = false;
            StudyPvCustomInfo combinedPvInfo = new StudyPvCustomInfo();
            combinedPvInfo.label = label;
            combinedPvInfo.type = site.getPvInfoType(label);
            if (studyPvInfoLabels.contains(label)) {
                combinedPvInfo.allowedValues = study
                    .getPvInfoAllowedValues(label);
                selected = true;
            }
            combinedPvInfo.isDefault = false;
            combinedPvInfo.widget = new PvInfoWidget(client, SWT.NONE,
                combinedPvInfo, selected);
            combinedPvInfo.widget.addSelectionChangedListener(listener);
            pvCustomInfoMap.put(label, combinedPvInfo);
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
        // FIXME should be transfer to persitCheck method or others set Methods
        // of the wrapper

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

        for (Object object : pvCustomInfoMap.values()) {
            StudyPvCustomInfo pvCustomInfo = (StudyPvCustomInfo) object;
            boolean selected = pvCustomInfo.widget.getSelected();

            if (!selected || pvCustomInfo.label.equals("Date Processed"))
                continue;

            String value = pvCustomInfo.widget.getValues();
            if (pvCustomInfo.type.equals(4) || pvCustomInfo.type.equals(5)) {
                study.setPvInfo(pvCustomInfo.label, value.split(";"));
            } else {
                study.setPvInfo(pvCustomInfo.label);
            }
        }

        study.setSampleStorageCollection(sampleStorageEntryWidget
            .getSampleStorage());
        saveStudy();

        studyAdapter.getParent().performExpand();
    }

    private void saveStudy() throws Exception {
        study.setContactCollection(contactEntryWidget.getContacts());
        study.persist();
        SiteAdapter siteAdapter = studyAdapter
            .getParentFromClass(SiteAdapter.class);
        study.setSite(siteAdapter.getWrapper());
    }

    @Override
    public String getNextOpenedFormID() {
        return StudyViewForm.ID;
    }

    @Override
    public void reset() {
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
