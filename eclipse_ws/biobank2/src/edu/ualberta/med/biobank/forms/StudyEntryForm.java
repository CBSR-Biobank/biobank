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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.PvInfoType;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Study;
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
import gov.nih.nci.system.query.example.DeleteExampleQuery;
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

    private Collection<SampleSource> allSampleSources;

    private MultiSelectWidget sampleSourceMultiSelect;

    private Collection<PvInfoPossible> possiblePvInfos;

    class CombinedPvInfo {
        PvInfoPossible pvInfoPossible;
        PvInfo pvInfo;
        PvInfoWidget wiget;
    };

    private ListOrderedMap combinedPvInfoMap;

    private SampleStorageEntryWidget sampleStorageEntryWidget;

    private BiobankEntryFormWidgetListener listener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    public StudyEntryForm() {
        super();
        combinedPvInfoMap = new ListOrderedMap();
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
        createPvInfoSection();
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
        Collection<SampleSource> studySampleSources = studyWrapper
            .getSampleSourceCollection();
        allSampleSources = appService.search(SampleSource.class,
            new SampleSource());

        ListOrderedMap availSampleSource = new ListOrderedMap();
        List<Integer> selSampleSource = new ArrayList<Integer>();

        if (studySampleSources != null) {
            for (SampleSource ss : studySampleSources) {
                selSampleSource.add(ss.getId());
            }
        }

        for (SampleSource ss : allSampleSources) {
            availSampleSource.put(ss.getId(), ss.getName());
        }

        sampleSourceMultiSelect = new MultiSelectWidget(client, SWT.NONE,
            "Selected Source Vessels", "Available Source Vessels", 100);
        sampleSourceMultiSelect.adaptToToolkit(toolkit, true);
        sampleSourceMultiSelect.addSelections(availSampleSource,
            selSampleSource);
        sampleSourceMultiSelect.addSelectionChangedListener(listener);
    }

    private void createPvInfoSection() throws Exception {
        Composite client = createSectionWithClient("Patient Visit Information Collected");
        Collection<PvInfo> pviCollection = studyWrapper.getPvInfoCollection();
        GridLayout gl = (GridLayout) client.getLayout();
        gl.numColumns = 1;

        if (pviCollection != null) {
            for (PvInfo pvInfo : pviCollection) {
                CombinedPvInfo combinedPvInfo = new CombinedPvInfo();
                combinedPvInfo.pvInfo = pvInfo;
                combinedPvInfo.pvInfoPossible = pvInfo.getPvInfoPossible();

                combinedPvInfoMap.put(combinedPvInfo.pvInfoPossible.getId(),
                    combinedPvInfo);
            }
        }

        possiblePvInfos = getPossiblePvInfos();
        Assert.isNotNull(possiblePvInfos);

        // START KLUDGE
        //
        // create "date drawn" - not really a pv info but we'll pretend
        // we just want to show the user that this information is collected
        // by default. Date drawn is already part of the PatientVisit class.
        //
        String[] defaultFields = new String[] { "Date Drawn", "Date Processed",
            "Date Received" };

        for (String field : defaultFields) {
            PvInfoType pvType = new PvInfoType();
            pvType.setType("date_time");
            PvInfoPossible pvInfoDateDrawn = new PvInfoPossible();
            pvInfoDateDrawn.setIsDefault(true);
            pvInfoDateDrawn.setLabel(field);
            pvInfoDateDrawn.setPvInfoType(pvType);
            new PvInfoWidget(client, SWT.NONE, pvInfoDateDrawn, true, null);
        }
        //
        // END KLUDGE

        for (PvInfoPossible possiblePvInfo : possiblePvInfos) {
            boolean selected = false;
            String value = "";
            CombinedPvInfo combinedPvInfo = (CombinedPvInfo) combinedPvInfoMap
                .get(possiblePvInfo.getId());

            if (combinedPvInfo == null) {
                combinedPvInfo = new CombinedPvInfo();
                combinedPvInfo.pvInfoPossible = possiblePvInfo;
                combinedPvInfo.pvInfo = null;
                selected = false;
            } else {
                selected = true;
                value = combinedPvInfo.pvInfo.getPossibleValues();
            }

            combinedPvInfo.wiget = new PvInfoWidget(client, SWT.NONE,
                possiblePvInfo, selected, value);
            combinedPvInfo.wiget.addSelectionChangedListener(listener);

            combinedPvInfoMap.put(combinedPvInfo.pvInfoPossible.getId(),
                combinedPvInfo);
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
        // FIXME should be transfer to persitCheck method or others set Methods
        // of the wrapper

        // get the selected sample sources from widget
        List<Integer> selSampleSourceIds = sampleSourceMultiSelect
            .getSelected();
        Collection<SampleSource> selSampleSource = new HashSet<SampleSource>();
        for (SampleSource ss : allSampleSources) {
            int id = ss.getId();
            if (selSampleSourceIds.indexOf(id) >= 0) {
                selSampleSource.add(ss);
            }
        }
        Assert.isTrue(selSampleSource.size() == selSampleSourceIds.size(),
            "problem with sample source selections");
        studyWrapper.setSampleSourceCollection(selSampleSource);

        Collection<PvInfo> pvInfoList = new HashSet<PvInfo>();
        MapIterator it = combinedPvInfoMap.mapIterator();
        while (it.hasNext()) {
            it.next();
            CombinedPvInfo combinedPvInfo = (CombinedPvInfo) it.getValue();
            boolean selected = combinedPvInfo.wiget.getSelected();

            if (!selected)
                continue;

            String value = combinedPvInfo.wiget.getValues();
            PvInfo pvInfo = combinedPvInfo.pvInfo;

            if (pvInfo == null) {
                pvInfo = new PvInfo();
                pvInfo.setPvInfoPossible(combinedPvInfo.pvInfoPossible);
                pvInfo.setPvInfoType(combinedPvInfo.pvInfoPossible
                    .getPvInfoType());
            }

            pvInfo.setLabel(combinedPvInfo.pvInfoPossible.getLabel());
            if (value != null) {
                pvInfo.setPossibleValues(value);
            }
            pvInfoList.add(pvInfo);
        }
        studyWrapper.setPvInfoCollection(pvInfoList);
        saveStudy();
        saveSampleStorage();

        studyAdapter.getParent().performExpand();
    }

    private void saveStudy() throws Exception {
        // FIXME should be transfer to persitCheck method or others set Methods
        // of the wrapper
        SDKQuery query;
        SDKQueryResult result;
        Set<PvInfo> savedPvInfoList = new HashSet<PvInfo>();

        // FIXME: change study to studyWrapper
        // study.setContactCollection(contactEntryWidget.getContacts());

        if (studyWrapper.getPvInfoCollection().size() > 0) {
            for (PvInfo pvInfo : studyWrapper.getPvInfoCollection()) {
                if ((pvInfo.getId() == null) || (pvInfo.getId() == 0)) {
                    query = new InsertExampleQuery(pvInfo);
                } else {
                    query = new UpdateExampleQuery(pvInfo);
                }

                result = studyAdapter.getAppService().executeQuery(query);
                savedPvInfoList.add((PvInfo) result.getObjectResult());
            }
        }
        studyWrapper.setPvInfoCollection(savedPvInfoList);
        studyWrapper.persist();
        SiteAdapter siteAdapter = studyAdapter
            .getParentFromClass(SiteAdapter.class);
        studyWrapper.setSiteWrapper(siteAdapter.getWrapper());
    }

    private void saveSampleStorage() throws Exception {
        Collection<SampleStorage> ssCollection = sampleStorageEntryWidget
            .getSampleStorage();
        SDKQuery query;
        SDKQueryResult result;

        removeDeletedSampleStorage(ssCollection);

        Collection<SampleStorage> savedSsCollection = new HashSet<SampleStorage>();
        for (SampleStorage ss : ssCollection) {
            ss.setStudy(studyWrapper.getWrappedObject());
            if ((ss.getId() == null) || (ss.getId() == 0)) {
                query = new InsertExampleQuery(ss);
            } else {
                query = new UpdateExampleQuery(ss);
            }

            result = appService.executeQuery(query);
            savedSsCollection.add((SampleStorage) result.getObjectResult());
        }
        studyWrapper.setSampleStorageCollection(savedSsCollection);
    }

    private void removeDeletedSampleStorage(
        Collection<SampleStorage> ssCollection) throws Exception {
        // no need to remove if study is not yet in the database
        if (studyWrapper.getId() == null)
            return;

        List<Integer> selectedStampleStorageIds = new ArrayList<Integer>();
        for (SampleStorage ss : ssCollection) {
            selectedStampleStorageIds.add(ss.getId());
        }

        SDKQuery query;

        // query from database again
        Study dbStudy = ModelUtils.getObjectWithId(appService, Study.class,
            studyWrapper.getId());

        for (SampleStorage ss : dbStudy.getSampleStorageCollection()) {
            if (!selectedStampleStorageIds.contains(ss.getId())) {
                query = new DeleteExampleQuery(ss);
                appService.executeQuery(query);
            }
        }
    }

    private List<PvInfoPossible> getPossiblePvInfos() throws Exception {
        return studyAdapter.getAppService().search(PvInfoPossible.class,
            new PvInfoPossible());
    }

    @Override
    public String getNextOpenedFormID() {
        return StudyViewForm.ID;
    }

    @Override
    public void setFocus() {
        firstControl.setFocus();
    }
}
