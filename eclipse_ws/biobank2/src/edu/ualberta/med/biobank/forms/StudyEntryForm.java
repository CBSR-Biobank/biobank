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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.MultiSelect;
import edu.ualberta.med.biobank.widgets.PvInfoWidget;
import edu.ualberta.med.biobank.widgets.SampleStorageEntryWidget;
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
            put("activityStatus", new FieldInfo("Activity Status", Combo.class,
                SWT.NONE, FormConstants.ACTIVITY_STATUS, null, null));
            put("comment", new FieldInfo("Comments", Text.class, SWT.MULTI,
                null, null, null));
        }
    };

    private MultiSelect clinicsMultiSelect;

    private StudyAdapter studyAdapter;

    private Study study;

    private Site site;

    private Collection<Clinic> allClinics;

    private Collection<PvInfoPossible> possiblePvInfos;

    class CombinedPvInfo {
        PvInfoPossible pvInfoPossible;
        PvInfo pvInfo;
        PvInfoWidget wiget;
    };

    private ListOrderedMap combinedPvInfoMap;

    private SampleStorageEntryWidget sampleStorageEntryWidget;

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
        study = studyAdapter.getStudy();
        site = SessionManager.getInstance().getCurrentSite();

        String tabName;
        if (study.getId() == null) {
            tabName = "New Study";
        } else {
            tabName = "Study " + study.getNameShort();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() {
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
        // comments.setLayoutData(gd);

        createClinicSection();
        createSampleStorageSection();
        createPvInfoSection();
        createButtonsSection();
    }

    private void createClinicSection() {
        Composite client = createSectionWithClient("Available Clinics");
        Collection<Clinic> studyClinics = study.getClinicCollection();
        allClinics = site.getClinicCollection();

        ListOrderedMap availClinics = new ListOrderedMap();
        List<Integer> selClinics = new ArrayList<Integer>();

        if (studyClinics != null) {
            for (Clinic clinic : studyClinics) {
                selClinics.add(clinic.getId());
            }
        }

        for (Clinic clinic : allClinics) {
            availClinics.put(clinic.getId(), clinic.getName());
        }

        clinicsMultiSelect = new MultiSelect(client, SWT.NONE,
            "Selected Clinics", "Available Clinics", 100);
        clinicsMultiSelect.adaptToToolkit(toolkit);
        clinicsMultiSelect.addSelections(availClinics, selClinics);
    }

    private void createSampleStorageSection() {
        Composite client = createSectionWithClient("Sample Storage");

        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        sampleStorageEntryWidget = new SampleStorageEntryWidget(client,
            SWT.NONE, study.getSampleStorageCollection(), toolkit);
    }

    private void createPvInfoSection() {
        Composite client = createSectionWithClient("Additional Patient Visit Information Collected");
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

            combinedPvInfoMap.put(combinedPvInfo.pvInfoPossible.getId(),
                combinedPvInfo);
        }

        // now create the widgets in order listed in PvInfoPossible
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        initConfirmButton(client, false, true);
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

        // get the selected clinics from widget
        List<Integer> selClinicIds = clinicsMultiSelect.getSelected();
        Set<Clinic> selClinics = new HashSet<Clinic>();
        for (Clinic clinic : allClinics) {
            int id = clinic.getId();
            if (selClinicIds.indexOf(id) >= 0) {
                selClinics.add(clinic);
            }

        }
        Assert.isTrue(selClinics.size() == selClinicIds.size(),
            "problem with clinic selections");
        study.setClinicCollection(selClinics);

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
        Collection<SampleStorage> ssCollection) {
        // no need to remove if study is not yet in the database
        if (study.getId() == null)
            return;

        List<Integer> selectedStampleStorageIds = new ArrayList<Integer>();
        for (SampleStorage ss : ssCollection) {
            selectedStampleStorageIds.add(ss.getId());
        }

        SDKQuery query;

        try {
            // query from database again
            Study dbStudy = ModelUtils.getObjectWithId(appService, Study.class,
                study.getId());

            for (SampleStorage ss : dbStudy.getSampleStorageCollection()) {
                if (!selectedStampleStorageIds.contains(ss.getId())) {
                    query = new DeleteExampleQuery(ss);
                    appService.executeQuery(query);
                }
            }
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<PvInfoPossible> getPossiblePvInfos() {
        PvInfoPossible criteria = new PvInfoPossible();

        try {
            return studyAdapter.getAppService().search(PvInfoPossible.class,
                criteria);
        } catch (final RemoteConnectFailureException exp) {
            BioBankPlugin.openRemoteConnectErrorMessage();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return null;
    }

    private boolean checkStudyNameUnique() throws Exception {
        Site site = SessionManager.getInstance().getCurrentSite();

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

    @Override
    public void cancelForm() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getNextOpenedFormID() {
        return StudyViewForm.ID;
    }
}
