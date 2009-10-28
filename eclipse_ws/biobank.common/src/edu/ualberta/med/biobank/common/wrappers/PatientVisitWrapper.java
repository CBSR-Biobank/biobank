package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.internal.PvInfoDataWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.PvInfoWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.Sample;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientVisitWrapper extends ModelWrapper<PatientVisit> {

    private Map<String, PvInfoDataWrapper> pvInfoDataMap;

    public PatientVisitWrapper(WritableApplicationService appService,
        PatientVisit wrappedObject) {
        super(appService, wrappedObject);
        pvInfoDataMap = null;
    }

    public PatientVisitWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "patient", "dateDrawn", "dateProcessed",
            "dateReceived", "clinic", "comments", "pvInfoDataCollection",
            "sampleCollection", "username" };
    }

    public Date getDateDrawn() {
        return wrappedObject.getDateDrawn();
    }

    public String getFormattedDateDrawn() {
        return DateFormatter.formatAsDateTime(getDateDrawn());
    }

    public Date getDateProcessed() {
        return wrappedObject.getDateProcessed();
    }

    public String getFormattedDateProcessed() {
        return DateFormatter.formatAsDateTime(getDateProcessed());
    }

    public Date getDateReceived() {
        return wrappedObject.getDateReceived();
    }

    public String getFormattedDateReceived() {
        return DateFormatter.formatAsDateTime(getDateReceived());
    }

    public String getComments() {
        return wrappedObject.getComments();
    }

    public PatientWrapper getPatient() {
        Patient patient = wrappedObject.getPatient();
        if (patient == null) {
            return null;
        }
        return new PatientWrapper(appService, patient);
    }

    public void setPatient(PatientWrapper patientWrapper) {
        setPatient(patientWrapper.getWrappedObject());
    }

    public void setPatient(Patient patient) {
        Patient oldPatient = wrappedObject.getPatient();
        wrappedObject.setPatient(patient);
        propertyChangeSupport
            .firePropertyChange("patient", oldPatient, patient);
    }

    @SuppressWarnings("unchecked")
    public Collection<SampleWrapper> getSampleCollection() {
        List<SampleWrapper> sampleCollection = (List<SampleWrapper>) propertiesMap
            .get("sampleCollection");
        if (sampleCollection == null) {
            Collection<Sample> children = wrappedObject.getSampleCollection();
            if (children != null) {
                sampleCollection = new ArrayList<SampleWrapper>();
                for (Sample sample : children) {
                    sampleCollection.add(new SampleWrapper(appService, sample));
                }
                propertiesMap.put("sampleCollection", sampleCollection);
            }
        }
        return sampleCollection;
    }

    public void setSampleCollection(Collection<Sample> sampleCollection,
        boolean setNull) {
        Collection<Sample> oldCollection = wrappedObject.getSampleCollection();
        wrappedObject.setSampleCollection(sampleCollection);
        propertyChangeSupport.firePropertyChange("sampleCollection",
            oldCollection, sampleCollection);
        if (setNull) {
            propertiesMap.put("sampleCollection", null);
        }
    }

    public void setSampleCollection(Collection<SampleWrapper> sampleCollection) {
        Collection<Sample> collection = new HashSet<Sample>();
        for (SampleWrapper sample : sampleCollection) {
            collection.add(sample.getWrappedObject());
        }
        setSampleCollection(collection, false);
        propertiesMap.put("sampleCollection", sampleCollection);
    }

    public Collection<SampleWrapper> getSampleWrapperCollection() {
        Collection<SampleWrapper> collection = new HashSet<SampleWrapper>();
        for (Sample sample : wrappedObject.getSampleCollection()) {
            collection.add(new SampleWrapper(appService, sample));
        }
        return collection;
    }

    public ClinicWrapper getClinic() {
        Clinic clinic = wrappedObject.getClinic();
        if (clinic == null) {
            return null;
        }
        return new ClinicWrapper(appService, clinic);
    }

    @SuppressWarnings("unchecked")
    public List<PvSampleSourceWrapper> getPvSampleSourceCollection() {
        List<PvSampleSourceWrapper> pvSampleSourceCollection = (List<PvSampleSourceWrapper>) propertiesMap
            .get("pvSampleSourceCollection");
        if (pvSampleSourceCollection == null) {
            Collection<PvSampleSource> children = wrappedObject
                .getPvSampleSourceCollection();
            if (children != null) {
                pvSampleSourceCollection = new ArrayList<PvSampleSourceWrapper>();
                for (PvSampleSource pvSampleSource : children) {
                    pvSampleSourceCollection.add(new PvSampleSourceWrapper(
                        appService, pvSampleSource));
                }
                propertiesMap.put("pvSampleSourceCollection",
                    pvSampleSourceCollection);
            }
        }
        return pvSampleSourceCollection;
    }

    @SuppressWarnings("unchecked")
    private List<PvInfoDataWrapper> getPvInfoDataCollection() {
        List<PvInfoDataWrapper> pvInfoDataCollection = (List<PvInfoDataWrapper>) propertiesMap
            .get("pvInfoDataCollection");
        if (pvInfoDataCollection == null) {
            Collection<PvInfoData> children = wrappedObject
                .getPvInfoDataCollection();
            if (children != null) {
                pvInfoDataCollection = new ArrayList<PvInfoDataWrapper>();
                for (PvInfoData pvInfo : children) {
                    pvInfoDataCollection.add(new PvInfoDataWrapper(appService,
                        pvInfo));
                }
                propertiesMap.put("pvInfoDataCollection", pvInfoDataCollection);
            }
        }
        return pvInfoDataCollection;
    }

    private void setPvInfoDataCollection(
        Collection<PvInfoData> pvInfoDataCollection, boolean setNull) {
        Collection<PvInfoData> oldCollection = wrappedObject
            .getPvInfoDataCollection();
        wrappedObject.setPvInfoDataCollection(pvInfoDataCollection);
        propertyChangeSupport.firePropertyChange("pvInfoDataCollection",
            oldCollection, pvInfoDataCollection);
        if (setNull) {
            propertiesMap.put("pvInfoDataCollection", null);
        }
    }

    private void setPvInfoDataCollection(
        Collection<PvInfoDataWrapper> pvInfoDataCollection) {
        Collection<PvInfoData> pvCollection = new HashSet<PvInfoData>();
        for (PvInfoDataWrapper pv : pvInfoDataCollection) {
            pvCollection.add(pv.getWrappedObject());
        }
        setPvInfoDataCollection(pvCollection, false);
        propertiesMap.put("pvInfoDataCollection", pvInfoDataCollection);
    }

    private Map<String, PvInfoDataWrapper> getPvInfoDataMap() {
        if (pvInfoDataMap != null)
            return pvInfoDataMap;

        pvInfoDataMap = new HashMap<String, PvInfoDataWrapper>();
        List<PvInfoDataWrapper> pvInfoCollection = getPvInfoDataCollection();
        if (pvInfoCollection != null) {
            for (PvInfoDataWrapper pvInfoData : pvInfoCollection) {
                pvInfoDataMap
                    .put(pvInfoData.getPvInfo().getLabel(), pvInfoData);
            }
        }
        return pvInfoDataMap;
    }

    public String[] getPvInfoLabels() {
        getPvInfoDataMap();
        return pvInfoDataMap.keySet().toArray(new String[] {});
    }

    public String getPvInfo(String label) throws Exception {
        getPvInfoDataMap();
        PvInfoDataWrapper pvInfo = pvInfoDataMap.get(label);
        if (pvInfo == null) {
            // make sure "label" is a valid pvInfo for study
            StudyWrapper study = getPatient().getStudy();
            if (study == null) {
                throw new Exception("study is null");
            }

            // make sure label is valid PV custom info, make the method call
            // and make sure exception is not thrown
            study.getPvInfoAllowedValues(label);

            // not assigned yet
            return null;
        }
        return pvInfo.getValue();
    }

    public Integer getPvInfoType(String label) throws Exception {
        StudyWrapper study = getPatient().getStudy();
        if (study == null) {
            throw new Exception("study is null");
        }
        return study.getPvInfoType(label);
    }

    public String[] getPvInfoAllowedValues(String label) throws Exception {
        StudyWrapper study = getPatient().getStudy();
        if (study == null) {
            throw new Exception("study is null");
        }
        return study.getPvInfoAllowedValues(label);
    }

    public void setPvInfo(String label, String value) throws Exception {
        getPvInfoDataMap();
        PvInfoDataWrapper pid = pvInfoDataMap.get(label);
        if (pid == null) {
            StudyWrapper study = getPatient().getStudy();
            if (study == null) {
                throw new Exception("study is null");
            }
            List<String> allowedValList = Arrays.asList(study
                .getPvInfoAllowedValues(label));
            if (!allowedValList.contains(value)) {
                throw new Exception("PvInfoData with label \"" + label
                    + "\" and value \"" + value + "\" is invalid");
            }

            pid = new PvInfoDataWrapper(appService, new PvInfoData());
            pid.setPatientVisit(this);
            pid.setPvInfo(study.getPvInfo(label));
            pvInfoDataMap.put(label, pid);
        }
        pid.setValue(value);
    }

    public void setDateDrawn(Date date) {
        Date oldDate = getDateDrawn();
        wrappedObject.setDateDrawn(date);
        propertyChangeSupport.firePropertyChange("dateDrawn", oldDate, date);
    }

    public void setDateProcessed(Date date) {
        Date oldDate = getDateProcessed();
        wrappedObject.setDateProcessed(date);
        propertyChangeSupport
            .firePropertyChange("dateProcessed", oldDate, date);
    }

    public void setDateReceived(Date date) {
        Date oldDate = getDateReceived();
        wrappedObject.setDateReceived(date);
        propertyChangeSupport.firePropertyChange("dateReceived", oldDate, date);
    }

    public void setComments(String comments) {
        String oldComments = getComments();
        wrappedObject.setComments(comments);
        propertyChangeSupport.firePropertyChange("comments", oldComments,
            comments);
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        if (!checkVisitDateDrawnUnique()) {
            throw new BiobankCheckException("A patient visit with date drawn "
                + getDateDrawn() + " already exist in patient "
                + getPatient().getNumber() + ".");
        }
        if (isNew() && getUsername() == null) {

        }
    }

    public String getUsername() {
        return wrappedObject.getUsername();
    }

    public void setUsername(String username) {
        String oldUsername = wrappedObject.getUsername();
        wrappedObject.setUsername(username);
        propertyChangeSupport.firePropertyChange("username", oldUsername,
            username);
    }

    @Override
    protected void persistDependencies(PatientVisit origObject)
        throws BiobankCheckException, ApplicationException, WrapperException {
        if (origObject != null) {
            removeDeletedPvSampleSources(origObject);
        }
    }

    private void removeDeletedPvSampleSources(PatientVisit pvDatabase)
        throws BiobankCheckException, ApplicationException, WrapperException {
        List<PvSampleSourceWrapper> newSampleSources = getPvSampleSourceCollection();
        List<PvSampleSourceWrapper> oldSampleSources = new PatientVisitWrapper(
            appService, pvDatabase).getPvSampleSourceCollection();
        if (oldSampleSources != null) {
            for (PvSampleSourceWrapper ss : oldSampleSources) {
                if ((newSampleSources == null)
                    || !newSampleSources.contains(ss)) {
                    ss.delete();
                }
            }
        }
    }

    private boolean checkVisitDateDrawnUnique() throws ApplicationException {
        String isSameVisit = "";
        List<Object> params = new ArrayList<Object>();
        params.add(getPatient().getId());
        params.add(getDateDrawn());
        if (!isNew()) {
            isSameVisit = " and id <> ?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from " + PatientVisit.class.getName()
            + " where patient.id=? and dateDrawn = ?" + isSameVisit, params);

        List<Object> results = appService.query(c);
        return results.size() == 0;
    }

    public void setClinic(Clinic clinic) {
        Clinic oldClinic = wrappedObject.getClinic();
        wrappedObject.setClinic(clinic);
        propertyChangeSupport.firePropertyChange("clinic", oldClinic, clinic);
    }

    public void setClinic(ClinicWrapper clinic) {
        if (clinic == null) {
            setClinic((Clinic) null);
        } else {
            setClinic(clinic.wrappedObject);
        }
    }

    public void setPvSampleSourceCollection(
        Collection<PvSampleSource> pvSampleSources, boolean setNull) {
        Collection<PvSampleSource> oldCollection = wrappedObject
            .getPvSampleSourceCollection();
        wrappedObject.setPvSampleSourceCollection(pvSampleSources);
        propertyChangeSupport.firePropertyChange("pvSampleSourceCollection",
            oldCollection, pvSampleSources);
        if (setNull) {
            propertiesMap.put("pvSampleSourceCollection", null);
        }
    }

    public void setPvSampleSourceCollection(
        Collection<PvSampleSourceWrapper> pvSampleSources) {
        Collection<PvSampleSource> pvCollection = new HashSet<PvSampleSource>();
        for (PvSampleSourceWrapper pv : pvSampleSources) {
            pvCollection.add(pv.getWrappedObject());
        }
        setPvSampleSourceCollection(pvCollection, false);
        propertiesMap.put("pvSampleSourceCollection", pvSampleSources);
    }

    @Override
    public Class<PatientVisit> getWrappedClass() {
        return PatientVisit.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    public class PvInfoPvInfoData {
        private PvInfoWrapper pvInfo;
        private PvInfoDataWrapper pvInfoData;

        public PvInfoWrapper getPvInfo() {
            return pvInfo;
        }

        public PvInfoDataWrapper getPvInfoData() {
            return pvInfoData;
        }
    }

    @Override
    public int compareTo(ModelWrapper<PatientVisit> wrapper) {
        Date v1Date = wrappedObject.getDateDrawn();
        Date v2Date = wrapper.wrappedObject.getDateDrawn();
        return ((v1Date.compareTo(v2Date) > 0) ? 1 : (v1Date.equals(v2Date) ? 0
            : -1));
    }

    @Override
    public void persist() throws BiobankCheckException, ApplicationException,
        WrapperException {
        if (pvInfoDataMap != null) {
            setPvInfoDataCollection(pvInfoDataMap.values());
        }
        super.persist();
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        pvInfoDataMap = null;
    }

    @Override
    public String toString() {
        return getFormattedDateDrawn();
    }
}
