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
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfoData;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Shipment;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PatientVisitWrapper extends ModelWrapper<PatientVisit> {

    private Map<String, PvInfoDataWrapper> pvInfoDataMap;

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

    public PatientVisitWrapper(WritableApplicationService appService,
        PatientVisit wrappedObject) {
        super(appService, wrappedObject);
        pvInfoDataMap = null;
    }

    public PatientVisitWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "patient", "dateProcessed", "comment",
            "pvInfoDataCollection", "sampleCollection", "username", "shipment" };
    }

    public Date getDateProcessed() {
        return wrappedObject.getDateProcessed();
    }

    public String getFormattedDateProcessed() {
        return DateFormatter.formatAsDateTime(getDateProcessed());
    }

    public String getComment() {
        return wrappedObject.getComment();
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

    public void setDateProcessed(Date date) {
        Date oldDate = getDateProcessed();
        wrappedObject.setDateProcessed(date);
        propertyChangeSupport
            .firePropertyChange("dateProcessed", oldDate, date);
    }

    public void setComment(String comment) {
        String oldComment = getComment();
        wrappedObject.setComment(comment);
        propertyChangeSupport
            .firePropertyChange("comment", oldComment, comment);
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        // TODO check add only one shipment ? but should be done through
        // hibernate

        // TODO WAs checking study set to the patient visit was on the studies
        // link to the clinic
        // do there or in the shipment ?
        // boolean found = false;
        // Collection<StudyWrapper> studies = getClinic().getStudyCollection();
        // for (StudyWrapper study : studies)
        // if (study.getId().equals(getPatient().getStudy().getId()))
        // found = true;
        // if (!found)
        // throw new BiobankCheckException("A patient visit with date drawn "
        // + getDateDrawn() + " already exist in patient "
        // + getPatient().getNumber() + ".");
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

    public ShipmentWrapper getShipment() {
        Shipment s = wrappedObject.getShipment();
        if (s == null) {
            return null;
        }
        return new ShipmentWrapper(appService, s);
    }

    public void setShipment(Shipment s) {
        ShipmentWrapper oldShipment = getShipment();
        wrappedObject.setShipment(s);
        propertyChangeSupport.firePropertyChange("shipment", oldShipment, s);
    }

    public void setShipment(ShipmentWrapper s) {
        setShipment(s.wrappedObject);
    }

    @Override
    public Class<PatientVisit> getWrappedClass() {
        return PatientVisit.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    public int compareTo(ModelWrapper<PatientVisit> wrapper) {
        Date v1Date = wrappedObject.getDateProcessed();
        Date v2Date = wrapper.wrappedObject.getDateProcessed();
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
        return getShipment().getFormattedDateDrawn();
    }
}
