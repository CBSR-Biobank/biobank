package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.PvInfoWrapper;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.ClinicAddWidget;
import edu.ualberta.med.biobank.widgets.MultiSelectWidget;
import edu.ualberta.med.biobank.widgets.PvInfoWidget;
import edu.ualberta.med.biobank.widgets.SampleStorageEntryWidget;
import edu.ualberta.med.biobank.widgets.listener.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listener.MultiSelectEvent;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;

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
                NonEmptyString.class, "Study name cannot be blank"));
            put("nameShort", new FieldInfo("Short Name", Text.class, SWT.NONE,
                null, NonEmptyString.class, "Study short name cannot be blank"));
            put("activityStatus", new FieldInfo("Activity Status",
                CCombo.class, SWT.NONE, FormConstants.ACTIVITY_STATUS, null,
                null));
            put("comment", new FieldInfo("Comments", Text.class, SWT.MULTI,
                null, null, null));
        }
    };

    private StudyAdapter studyAdapter;

    private StudyWrapper studyWrapper;

    private ClinicAddWidget contactEntryWidget;

    private Collection<SampleSourceWrapper> allSampleSources;

    private MultiSelectWidget sampleSourceMultiSelect;

    private class PvCustomInfo {
        String label;
        Integer type;
        String[] allowedValues;
        Control control;
    };

    private ListOrderedMap pvCustomInfoMap;

    private SampleStorageEntryWidget sampleStorageEntryWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

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
        studyWrapper = studyAdapter.getWrapper();
        studyWrapper.reload();

        String tabName;
        if (studyWrapper.getId() == null) {
            tabName = "New Study";
        } else {
            tabName = "Study " + studyWrapper.getNameShort();
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
        FormUtils.setTextValue(siteLabel, studyWrapper.getSite().getName());

        createBoundWidgetsFromMap(FIELDS, studyWrapper, client);

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

        contactEntryWidget = new ClinicAddWidget(client, SWT.NONE,
            studyWrapper, toolkit);
        contactEntryWidget.addSelectionChangedListener(listener);
    }

    private void createSampleStorageSection() {
        Composite client = createSectionWithClient("Sample Storage");

        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        sampleStorageEntryWidget = new SampleStorageEntryWidget(client,
            SWT.NONE, studyWrapper.getSampleStorageCollection(), toolkit);
        sampleStorageEntryWidget.addSelectionChangedListener(listener);
    }

    private void createSourceVesselsSection() throws Exception {
        Composite client = createSectionWithClient("Source Vessels");
        Collection<SampleSourceWrapper> studySampleSources = studyWrapper
            .getSampleSourceCollection();
        allSampleSources = SampleSourceWrapper.getAllSampleSources(appService);

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

        sampleSourceMultiSelect = new MultiSelectWidget(client, SWT.NONE,
            "Selected Source Vessels", "Available Source Vessels", 100);
        sampleSourceMultiSelect.adaptToToolkit(toolkit, true);
        sampleSourceMultiSelect.addSelections(availSampleSource,
            selSampleSource);
        sampleSourceMultiSelect.addSelectionChangedListener(listener);
    }

    private void createPvCustomInfoSection() throws Exception {
        Composite client = createSectionWithClient("Patient Visit Information Collected");
        GridLayout gl = (GridLayout) client.getLayout();
        gl.numColumns = 1;

        // START KLUDGE
        //
        // create "date drawn" - not really a pv info but we'll pretend
        // we just want to show the user that this information is collected
        // by default. Date drawn is already part of the PatientVisit class.
        //
        String[] defaultFields = new String[] { "Date Drawn", "Date Processed",
            "Date Received" };

        for (String field : defaultFields) {
            PvCustomInfo combinedPvInfo = new PvCustomInfo();
            combinedPvInfo.label = field;
            combinedPvInfo.type = 3;
            pvCustomInfoMap.put(field, combinedPvInfo);
        }
        //
        // END KLUDGE

        for (String label : studyWrapper.getPvInfoLabels()) {
            PvCustomInfo combinedPvInfo = new PvCustomInfo();
            combinedPvInfo.label = label;
            combinedPvInfo.type = studyWrapper.getPvInfoType(label);
            combinedPvInfo.allowedValues = studyWrapper
                .getPvInfoAllowedValues(label);
            pvCustomInfoMap.put(label, combinedPvInfo);
        }

        for (Object key : pvCustomInfoMap.keySet()) {
            boolean selected = false;
            String label = (String) key;
            String value = "";
            PvCustomInfo pvCustomInfo = (PvCustomInfo) pvCustomInfoMap
                .get(label);

            if (pvCustomInfo == null) {
                pvCustomInfo = new PvCustomInfo();
                pvCustomInfo.pvInfoPossible = possiblePvInfo;
                pvCustomInfo.pvInfo = null;
                selected = false;
            } else {
                selected = true;
                value = pvCustomInfo.pvInfo.getAllowedValue();
            }

            pvCustomInfo.wiget = new PvInfoWidget(client, SWT.NONE,
                possiblePvInfo, selected, value);
            pvCustomInfo.wiget.addSelectionChangedListener(listener);

            pvCustomInfoMap.put(pvCustomInfo.pvInfoPossible.getId(),
                pvCustomInfo);
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
        if (studyWrapper.getId() == null) {
            return MSG_NEW_STUDY_OK;
        }
        return MSG_STUDY_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        studyWrapper.reload();
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
        studyWrapper.setSampleSourceCollection(selSampleSource);

        List<PvInfoWrapper> pvInfoList = new ArrayList<PvInfoWrapper>();
        MapIterator it = pvCustomInfoMap.mapIterator();
        while (it.hasNext()) {
            it.next();
            PvCustomInfo combinedPvInfo = (PvCustomInfo) it.getValue();
            boolean selected = combinedPvInfo.wiget.getSelected();

            if (!selected)
                continue;

            String value = combinedPvInfo.wiget.getValues();
            PvInfoWrapper pvInfo = combinedPvInfo.pvInfo;
            if (pvInfo == null) {
                pvInfo = new PvInfoWrapper(appService);
                pvInfo.setPvInfoPossible(combinedPvInfo.pvInfoPossible);
                pvInfo.setPvInfoType(combinedPvInfo.pvInfoPossible
                    .getPvInfoType());
            }

            pvInfo.setLabel(combinedPvInfo.pvInfoPossible.getLabel());
            if (value != null) {
                pvInfo.setAllowedValues(value);
            }
            pvInfoList.add(pvInfo);
        }
        studyWrapper.setPvInfoCollection(pvInfoList);
        studyWrapper.setSampleStorageCollection(sampleStorageEntryWidget
            .getSampleStorage());
        saveStudy();

        studyAdapter.getParent().performExpand();
    }

    private void saveStudy() throws Exception {
        // FIXME should be transfer to persitCheck method or others set Methods
        // of the wrapper
        SDKQuery query;
        SDKQueryResult result;
        Set<PvInfo> savedPvInfoList = new HashSet<PvInfo>();

        // FIXME: change study to studyWrapper
        studyWrapper.setContactCollection(contactEntryWidget.getContacts());

        if (studyWrapper.getPvInfoCollection().size() > 0) {
            for (PvInfoWrapper pvInfo : studyWrapper.getPvInfoCollection()) {
                if ((pvInfo.getId() == null) || (pvInfo.getId() == 0)) {
                    query = new InsertExampleQuery(pvInfo.getWrappedObject());
                } else {
                    query = new UpdateExampleQuery(pvInfo.getWrappedObject());
                }

                result = studyAdapter.getAppService().executeQuery(query);
                savedPvInfoList.add((PvInfo) result.getObjectResult());
            }
        }
        studyWrapper.setPvInfoCollection(savedPvInfoList, true);
        studyWrapper.persist();
        SiteAdapter siteAdapter = studyAdapter
            .getParentFromClass(SiteAdapter.class);
        studyWrapper.setSite(siteAdapter.getWrapper());
    }

    @Override
    public String getNextOpenedFormID() {
        return StudyViewForm.ID;
    }

}
