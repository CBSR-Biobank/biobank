package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.internal.PvAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyPvAttrWrapper;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvAttr;
import edu.ualberta.med.biobank.model.PvSampleSource;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientVisitWrapper extends ModelWrapper<PatientVisit> {

    private Map<String, StudyPvAttrWrapper> studyPvAttrMap;

    private Map<String, PvAttrWrapper> pvAttrMap;

    public PatientVisitWrapper(WritableApplicationService appService,
        PatientVisit wrappedObject) {
        super(appService, wrappedObject);
        studyPvAttrMap = null;
        pvAttrMap = null;
    }

    public PatientVisitWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "patient", "dateProcessed", "comment",
            "pvAttrCollection", "sampleCollection", "username", "shipment",
            "pvSampleSourceCollection" };
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
    private List<PvAttrWrapper> getPvAttrCollection() {
        List<PvAttrWrapper> pvAttrCollection = (List<PvAttrWrapper>) propertiesMap
            .get("pvAttrCollection");
        if (pvAttrCollection == null) {
            Collection<PvAttr> children = wrappedObject.getPvAttrCollection();
            if (children != null) {
                pvAttrCollection = new ArrayList<PvAttrWrapper>();
                for (PvAttr pvAttr : children) {
                    pvAttrCollection.add(new PvAttrWrapper(appService, pvAttr));
                }
                propertiesMap.put("pvAttrCollection", pvAttrCollection);
            }
        }
        return pvAttrCollection;
    }

    private void setPvAttrCollection(Collection<PvAttr> pvAttrCollection,
        boolean setNull) {
        Collection<PvAttr> oldCollection = wrappedObject.getPvAttrCollection();
        wrappedObject.setPvAttrCollection(pvAttrCollection);
        propertyChangeSupport.firePropertyChange("pvAttrCollection",
            oldCollection, pvAttrCollection);
        if (setNull) {
            propertiesMap.put("pvAttrCollection", null);
        }
    }

    private void setPvAttrCollection(Collection<PvAttrWrapper> pvAttrCollection) {
        Collection<PvAttr> pvCollection = new HashSet<PvAttr>();
        for (PvAttrWrapper pv : pvAttrCollection) {
            pvCollection.add(pv.getWrappedObject());
        }
        setPvAttrCollection(pvCollection, false);
        propertiesMap.put("pvAttrCollection", pvAttrCollection);
    }

    private Map<String, StudyPvAttrWrapper> getStudyPvAttrMap() {
        if (studyPvAttrMap != null)
            return studyPvAttrMap;

        studyPvAttrMap = new HashMap<String, StudyPvAttrWrapper>();
        List<StudyPvAttrWrapper> studyPvAttrCollection = getPatient()
            .getStudy().getStudyPvAttrCollection();
        if (studyPvAttrCollection != null) {
            for (StudyPvAttrWrapper studyPvAttr : studyPvAttrCollection) {
                studyPvAttrMap.put(studyPvAttr.getLabel(), studyPvAttr);
            }
        }
        return studyPvAttrMap;
    }

    private Map<String, PvAttrWrapper> getPvAttrMap() {
        getStudyPvAttrMap();
        if (pvAttrMap != null)
            return pvAttrMap;

        pvAttrMap = new HashMap<String, PvAttrWrapper>();
        List<PvAttrWrapper> pvAttrCollection = getPvAttrCollection();
        if (pvAttrCollection != null) {
            for (PvAttrWrapper pvAttr : pvAttrCollection) {
                pvAttrMap.put(pvAttr.getStudyPvAttr().getLabel(), pvAttr);
            }
        }
        return pvAttrMap;
    }

    public String[] getPvAttrLabels() {
        getPvAttrMap();
        return pvAttrMap.keySet().toArray(new String[] {});
    }

    public String getPvAttrValue(String label) throws Exception {
        getPvAttrMap();
        PvAttrWrapper pvAttr = pvAttrMap.get(label);
        if (pvAttr == null) {
            StudyPvAttrWrapper studyPvAttr = studyPvAttrMap.get(label);
            // make sure "label" is a valid study pv attr
            if (studyPvAttr == null) {
                throw new Exception("StudyPvAttr with label \"" + label
                    + "\" is invalid");
            }
            // not assigned yet so return null
            return null;
        }
        return pvAttr.getValue();
    }

    public Integer getPvAttrType(String label) throws Exception {
        getPvAttrMap();
        PvAttrWrapper pvAttr = pvAttrMap.get(label);
        if (pvAttr == null) {
            throw new Exception("PvAttr for label \"" + label
                + "\" does not exist");
        }
        return pvAttr.getStudyPvAttr().getPvAttrType().getId();
    }

    public String[] getPvAttrPermissible(String label) throws Exception {
        getPvAttrMap();
        PvAttrWrapper pvAttr = pvAttrMap.get(label);
        if (pvAttr == null) {
            throw new Exception("PvAttr for label \"" + label
                + "\" does not exist");
        }
        String allowedValues = pvAttr.getStudyPvAttr().getPermissible();
        if (allowedValues == null) {
            return null;
        }
        return pvAttr.getStudyPvAttr().getPermissible().split(";");
    }

    public void setPvAttrValue(String label, String value) throws Exception {
        getPvAttrMap();
        PvAttrWrapper pvAttr = pvAttrMap.get(label);
        if (pvAttr == null) {
            StudyPvAttrWrapper studyPvAttr = studyPvAttrMap.get(label);
            if (studyPvAttr == null) {
                throw new Exception("no StudyPvAttr found for label \"" + label
                    + "\"");
            }
            String allowedValues = studyPvAttr.getPermissible();
            if (allowedValues != null) {
                String[] allowedValuesSplit = allowedValues.split(";");
                List<String> allowedValList = Arrays.asList(allowedValuesSplit);
                for (String selectedValue : value.split(";")) {
                    if (!allowedValList.contains(selectedValue)) {
                        throw new Exception("PvAttr with label \"" + label
                            + "\" and value \"" + value + "\" is invalid");
                    }
                }
            }

            pvAttr = new PvAttrWrapper(appService, new PvAttr());
            pvAttr.setPatientVisit(this);
            pvAttr.setStudyPvAttr(studyPvAttr);
            pvAttrMap.put(label, pvAttr);
        }
        if (pvAttr.getStudyPvAttr().getLocked()) {
            throw new Exception("no attribute for label \"" + label
                + "\" is locked, changes not premitted");

        }
        pvAttr.setValue(value);
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

        // FIXME check shipment/patient ??

        checkVisitDateProcessedUnique();

        checkPatientClinicInSameStudy();
    }

    private void checkPatientClinicInSameStudy() throws ApplicationException,
        BiobankCheckException {
        HQLCriteria c = new HQLCriteria(
            "select count(study) from "
                + Study.class.getName()
                + " as study inner join study.contactCollection as studyContacts"
                + " inner join studyContacts.clinic as clinic"
                + " inner join clinic.shipmentCollection as shipments"
                + " inner join shipments.patientCollection as patients"
                + " where patients.study.id=study.id and shipments.id=? and patients.id = ?",
            Arrays.asList(new Object[] { getShipment().getId(),
                getPatient().getId() }));

        List<Long> result = appService.query(c);
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        if (result.get(0) == 0) {
            throw new BiobankCheckException(
                "The patient study is not linked with this clinic. Choose another clinic.");
        }
    }

    private void checkVisitDateProcessedUnique() throws ApplicationException,
        BiobankCheckException {
        String isSameVisit = "";
        List<Object> params = new ArrayList<Object>();
        params.add(getPatient().getId());
        params.add(getDateProcessed());
        if (!isNew()) {
            isSameVisit = " and id <> ?";
            params.add(getId());
        }
        HQLCriteria c = new HQLCriteria("from " + PatientVisit.class.getName()
            + " where patient.id=? and dateProcessed = ?" + isSameVisit, params);

        List<Object> results = appService.query(c);
        if (results.size() != 0) {
            throw new BiobankCheckException(
                "A patient visit with date processed "
                    + getFormattedDateProcessed()
                    + " already exist in patient " + getPatient().getNumber()
                    + ".");
        }
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

    @SuppressWarnings("unchecked")
    public List<PvSampleSourceWrapper> getPvSampleSourceCollection(boolean sort) {
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
        if ((pvSampleSourceCollection != null) && sort)
            Collections.sort(pvSampleSourceCollection);
        return pvSampleSourceCollection;
    }

    public List<PvSampleSourceWrapper> getPvSampleSourceCollection() {
        return getPvSampleSourceCollection(false);
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
        if (hasSamples()) {
            throw new BiobankCheckException("Unable to delete patient visit "
                + getDateProcessed()
                + " since it has samples stored in database.");
        }
    }

    public boolean hasSamples() throws ApplicationException,
        BiobankCheckException {
        String queryString = "select count(samples) from "
            + Patient.class.getName() + " as p"
            + " left join p.patientVisitCollection as visits"
            + " left join visits.sampleCollection as samples"
            + " where p = ? and visits = ?)";
        HQLCriteria c = new HQLCriteria(queryString, Arrays
            .asList(new Object[] { wrappedObject.getPatient(), wrappedObject }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0) > 0;
    }

    @Override
    public int compareTo(ModelWrapper<PatientVisit> wrapper) {
        if (wrapper instanceof PatientVisitWrapper) {
            Date v1Date = wrappedObject.getDateProcessed();
            Date v2Date = wrapper.wrappedObject.getDateProcessed();
            return ((v1Date.compareTo(v2Date) > 0) ? 1
                : (v1Date.equals(v2Date) ? 0 : -1));
        }
        return 0;
    }

    @Override
    public void persist() throws BiobankCheckException, ApplicationException,
        WrapperException {
        if (pvAttrMap != null) {
            setPvAttrCollection(pvAttrMap.values());
        }
        super.persist();
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        pvAttrMap = null;
    }

    @Override
    public String toString() {
        return getFormattedDateProcessed();
    }
}
