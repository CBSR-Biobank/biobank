package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.PvInfoType;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Site;
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
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

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

    private Study study;

    private Site site;

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
    public void init() {
        Assert.isTrue((adapter instanceof StudyAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        studyAdapter = (StudyAdapter) adapter;
        site = studyAdapter.getParentFromClass(SiteAdapter.class).getSite();
        retrieveStudy();
        study.setSite(site);

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

        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createBoundWidgetsFromMap(FIELDS, study, client);

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
            SWT.NONE, study.getSampleStorageCollection(), toolkit);
        sampleStorageEntryWidget.addSelectionChangedListener(listener);
    }

    private void createSourceVesselsSection() throws Exception {
        Composite client = createSectionWithClient("Source Vessels");
        Collection<SampleSource> studySampleSources = study
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
        Collection<PvInfo> pviCollection = study.getPvInfoCollection();
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
        PvInfoType pvType = new PvInfoType();
        pvType.setType("date_time");
        PvInfoPossible pvInfoDateDrawn = new PvInfoPossible();
        pvInfoDateDrawn.setIsDefault(true);
        pvInfoDateDrawn.setLabel("Date Drawn");
        pvInfoDateDrawn.setPvInfoType(pvType);
        new PvInfoWidget(client, SWT.NONE, pvInfoDateDrawn, true, null);
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
        initCancelConfirmWidget(client);
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
        if ((study.getId() == null) && !checkStudyNameUnique()) {
            setDirty(true);
            return;
        }

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
        study.setSampleSourceCollection(selSampleSource);

        List<PvInfo> pvInfoList = new ArrayList<PvInfo>();
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
        study.setPvInfoCollection(pvInfoList);
        saveStudy();
        saveSampleStorage();
        studyAdapter.setStudy(study);

        studyAdapter.getParent().performExpand();
    }

    private void saveStudy() throws ApplicationException {
        SDKQuery query;
        SDKQueryResult result;
        Set<PvInfo> savedPvInfoList = new HashSet<PvInfo>();

        study.setSite(site);
        study.setContactCollection(contactEntryWidget.getContacts());

        if (study.getPvInfoCollection().size() > 0) {
            for (PvInfo pvInfo : study.getPvInfoCollection()) {
                if ((pvInfo.getId() == null) || (pvInfo.getId() == 0)) {
                    query = new InsertExampleQuery(pvInfo);
                } else {
                    query = new UpdateExampleQuery(pvInfo);
                }

                result = studyAdapter.getAppService().executeQuery(query);
                savedPvInfoList.add((PvInfo) result.getObjectResult());
            }
        }
        study.setPvInfoCollection(savedPvInfoList);

        if ((study.getId() == null) || (study.getId() == 0)) {
            query = new InsertExampleQuery(study);
        } else {
            query = new UpdateExampleQuery(study);
        }

        result = appService.executeQuery(query);
        study = (Study) result.getObjectResult();
    }

    private void saveSampleStorage() throws Exception {
        Collection<SampleStorage> ssCollection = sampleStorageEntryWidget
            .getSampleStorage();
        SDKQuery query;
        SDKQueryResult result;

        removeDeletedSampleStorage(ssCollection);

        Collection<SampleStorage> savedSsCollection = new HashSet<SampleStorage>();
        for (SampleStorage ss : ssCollection) {
            ss.setStudy(study);
            if ((ss.getId() == null) || (ss.getId() == 0)) {
                query = new InsertExampleQuery(ss);
            } else {
                query = new UpdateExampleQuery(ss);
            }

            result = appService.executeQuery(query);
            savedSsCollection.add((SampleStorage) result.getObjectResult());
        }
        study.setSampleStorageCollection(savedSsCollection);
    }

    private void removeDeletedSampleStorage(
        Collection<SampleStorage> ssCollection) throws Exception {
        // no need to remove if study is not yet in the database
        if (study.getId() == null)
            return;

        List<Integer> selectedStampleStorageIds = new ArrayList<Integer>();
        for (SampleStorage ss : ssCollection) {
            selectedStampleStorageIds.add(ss.getId());
        }

        SDKQuery query;

        // query from database again
        Study dbStudy = ModelUtils.getObjectWithId(appService, Study.class,
            study.getId());

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

    private boolean checkStudyNameUnique() throws Exception {
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Study as study "
                + "inner join fetch study.site where study.site.id=? "
                + "and study.name=? and study.nameShort=?");

        c.setParameters(Arrays.asList(new Object[] { site.getId(),
            study.getName(), study.getNameShort() }));

        List<Object> results = appService.query(c);

        if (results.size() > 0) {
            BioBankPlugin
                .openAsyncError("Study Name Problem", "A study with name \""
                    + study.getName() + "\" already exists.");
            return false;
        }

        c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Study as study "
                + "inner join fetch study.site where study.site.id=?"
                + "and study.nameShort=?");

        c.setParameters(Arrays.asList(new Object[] { site.getId(),
            study.getNameShort() }));

        results = appService.query(c);

        if (results.size() > 0) {
            BioBankPlugin.openAsyncError("Study Name Problem",
                "A study with short name \"" + study.getName()
                    + "\" already exists.");
            return false;
        }

        return true;
    }

    private void retrieveStudy() {
        if (studyAdapter.getStudy().getId() == null) {
            // don't retrieve if this is a new study !
            study = studyAdapter.getStudy();
        } else {
            try {
                study = ModelUtils.getObjectWithId(appService, Study.class,
                    studyAdapter.getStudy().getId());
                studyAdapter.setStudy(study);
            } catch (Exception e) {
                SessionManager.getLogger().error(
                    "Error while retrieving study "
                        + studyAdapter.getStudy().getName(), e);
            }
        }
    }

    @Override
    public void cancelForm() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getNextOpenedFormID() {
        return StudyViewForm.ID;
    }
}
