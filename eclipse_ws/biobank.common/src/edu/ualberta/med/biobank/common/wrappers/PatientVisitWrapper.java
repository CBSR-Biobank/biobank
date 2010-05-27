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
import java.util.Set;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.internal.PvAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyPvAttrWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvAttr;
import edu.ualberta.med.biobank.model.PvSourceVessel;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientVisitWrapper extends ModelWrapper<PatientVisit> {

    private Map<String, StudyPvAttrWrapper> studyPvAttrMap;

    private Map<String, PvAttrWrapper> pvAttrMap;

    private Set<PvSourceVesselWrapper> deletedPvSourceVessels = new HashSet<PvSourceVesselWrapper>();

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
        return new String[] { "patient", "dateProcessed", "dateDrawn",
            "comment", "pvAttrCollection", "aliquotCollection", "shipment",
            "pvSourceVesselCollection" };
    }

    public Date getDateProcessed() {
        return wrappedObject.getDateProcessed();
    }

    public String getFormattedDateProcessed() {
        return DateFormatter.formatAsDateTime(getDateProcessed());
    }

    public Date getDateDrawn() {
        return wrappedObject.getDateDrawn();
    }

    public String getFormattedDateDrawn() {
        return DateFormatter.formatAsDateTime(getDateDrawn());
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

    public void setPatient(PatientWrapper patient) {
        Patient oldPatient = wrappedObject.getPatient();
        Patient newPatient = patient.getWrappedObject();
        wrappedObject.setPatient(newPatient);
        propertyChangeSupport.firePropertyChange("patient", oldPatient,
            newPatient);
    }

    @SuppressWarnings("unchecked")
    public List<AliquotWrapper> getAliquotCollection() {
        List<AliquotWrapper> aliquotCollection = (List<AliquotWrapper>) propertiesMap
            .get("aliquotCollection");
        if (aliquotCollection == null) {
            Collection<Aliquot> children = wrappedObject.getAliquotCollection();
            if (children != null) {
                aliquotCollection = new ArrayList<AliquotWrapper>();
                for (Aliquot aliquot : children) {
                    aliquotCollection.add(new AliquotWrapper(appService,
                        aliquot));
                }
                propertiesMap.put("aliquotCollection", aliquotCollection);
            }
        }
        return aliquotCollection;
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
        Collection<StudyPvAttrWrapper> studyPvAttrCollection = getPatient()
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

    public String getPvAttrTypeName(String label) throws Exception {
        getPvAttrMap();
        PvAttrWrapper pvAttr = pvAttrMap.get(label);
        StudyPvAttrWrapper studyPvAttr = null;
        if (pvAttr != null) {
            studyPvAttr = pvAttr.getStudyPvAttr();
        } else {
            studyPvAttr = studyPvAttrMap.get(label);
            // make sure "label" is a valid study pv attr
            if (studyPvAttr == null) {
                throw new Exception("StudyPvAttr withr label \"" + label
                    + "\" does not exist");
            }
        }
        return studyPvAttr.getPvAttrType().getName();
    }

    public String[] getPvAttrPermissible(String label) throws Exception {
        getPvAttrMap();
        PvAttrWrapper pvAttr = pvAttrMap.get(label);
        StudyPvAttrWrapper studyPvAttr = null;
        if (pvAttr != null) {
            studyPvAttr = pvAttr.getStudyPvAttr();
        } else {
            studyPvAttr = studyPvAttrMap.get(label);
            // make sure "label" is a valid study pv attr
            if (studyPvAttr == null) {
                throw new Exception("PvAttr for label \"" + label
                    + "\" does not exist");
            }
        }
        String permissible = studyPvAttr.getPermissible();
        if (permissible == null) {
            return null;
        }
        return permissible.split(";");
    }

    /**
     * Assigns a value to a patient visit attribute. The value is parsed for
     * correctness.
     * 
     * @param label The attribute's label.
     * @param value The value to assign.
     * @throws Exception when assigning a label of type "select_single" or
     *             "select_multiple" and the value is not one of the permissible
     *             ones.
     * @throws NumberFormatException when assigning a label of type "number" and
     *             the value is not a valid double number.
     * @throws ParseException when assigning a label of type "date_time" and the
     *             value is not a valid date and time.
     * @see edu.ualberta.med.biobank
     *      .common.formatters.DateFormatter.DATE_TIME_FORMAT
     */
    public void setPvAttrValue(String label, String value) throws Exception {
        getPvAttrMap();
        PvAttrWrapper pvAttr = pvAttrMap.get(label);
        StudyPvAttrWrapper studyPvAttr = null;

        if (pvAttr != null) {
            studyPvAttr = pvAttr.getStudyPvAttr();
        } else {
            studyPvAttr = studyPvAttrMap.get(label);
            if (studyPvAttr == null) {
                throw new Exception("no StudyPvAttr found for label \"" + label
                    + "\"");
            }
        }

        if (!studyPvAttr.getActivityStatus().isActive()) {
            throw new Exception("attribute for label \"" + label
                + "\" is locked, changes not premitted");
        }

        if (value != null && value.compareTo("") != 0) {
            // validate the value
            value = value.trim();
            String type = studyPvAttr.getPvAttrType().getName();
            List<String> permissibleSplit = null;

            if (type.equals("select_single") || type.equals("select_multiple")) {
                String permissible = studyPvAttr.getPermissible();
                if (permissible != null) {
                    permissibleSplit = Arrays.asList(permissible.split(";"));
                }
            }

            if (type.equals("select_single")) {
                if (!permissibleSplit.contains(value)) {
                    throw new Exception("value " + value
                        + "is invalid for label \"" + label + "\"");
                }
            } else if (type.equals("select_multiple")) {
                if (value.length() > 0) {
                    for (String singleVal : value.split(";")) {
                        if (!permissibleSplit.contains(singleVal)) {
                            throw new Exception("value " + singleVal + " ("
                                + value + ") is invalid for label \"" + label
                                + "\"");
                        }
                    }
                }
            } else if (type.equals("number")) {
                Double.parseDouble(value);
            } else if (type.equals("date_time")) {
                DateFormatter.dateFormatter.parse(value);
            } else if (type.equals("text")) {
                // do nothing
            } else {
                throw new Exception("type \"" + type + "\" not tested");
            }
        }

        if (pvAttr == null) {
            pvAttr = new PvAttrWrapper(appService);
            pvAttr.setPatientVisit(this);
            pvAttr.setStudyPvAttr(studyPvAttr);
            pvAttrMap.put(label, pvAttr);
        }
        pvAttr.setValue(value);
    }

    public void setDateProcessed(Date date) {
        Date oldDate = getDateProcessed();
        wrappedObject.setDateProcessed(date);
        propertyChangeSupport
            .firePropertyChange("dateProcessed", oldDate, date);
    }

    public void setDateDrawn(Date date) {
        Date oldDate = getDateDrawn();
        wrappedObject.setDateDrawn(date);
        propertyChangeSupport.firePropertyChange("dateDrawn", oldDate, date);
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
        checkHasShipment();
        checkPatientInShipment();
        // patient to clinic relationship tested by shipment, so no need to
        // test it again here
    }

    private void checkHasShipment() throws BiobankCheckException {
        if (getShipment() == null) {
            throw new BiobankCheckException(
                "This visit should contain a shipment");
        }
    }

    private void checkPatientInShipment() throws BiobankCheckException {
        List<PatientWrapper> shipmentPatients = getShipment()
            .getPatientCollection();
        if (shipmentPatients == null
            || !shipmentPatients.contains(getPatient())) {
            throw new BiobankCheckException(
                "The patient should be part of the shipment");
        }
    }

    @Override
    protected void persistDependencies(PatientVisit origObject)
        throws Exception {
        if (pvAttrMap != null) {
            setPvAttrCollection(pvAttrMap.values());
        }
        deletePvSourceVessels();
    }

    private void deletePvSourceVessels() throws Exception {
        for (PvSourceVesselWrapper ss : deletedPvSourceVessels) {
            if (!ss.isNew()) {
                ss.delete();
            }
        }
    }

    public ShipmentWrapper getShipment() {
        Shipment s = wrappedObject.getShipment();
        if (s == null) {
            return null;
        }
        return new ShipmentWrapper(appService, s);
    }

    public void setShipment(ShipmentWrapper s) {
        Shipment oldShipment = wrappedObject.getShipment();
        Shipment newShipment = s.getWrappedObject();
        wrappedObject.setShipment(newShipment);
        propertyChangeSupport.firePropertyChange("shipment", oldShipment,
            newShipment);
    }

    @SuppressWarnings("unchecked")
    public List<PvSourceVesselWrapper> getPvSourceVesselCollection(boolean sort) {
        List<PvSourceVesselWrapper> pvSourceVesselCollection = (List<PvSourceVesselWrapper>) propertiesMap
            .get("pvSourceVesselCollection");
        if (pvSourceVesselCollection == null) {
            Collection<PvSourceVessel> children = wrappedObject
                .getPvSourceVesselCollection();
            if (children != null) {
                pvSourceVesselCollection = new ArrayList<PvSourceVesselWrapper>();
                for (PvSourceVessel pvSourceVessel : children) {
                    pvSourceVesselCollection.add(new PvSourceVesselWrapper(
                        appService, pvSourceVessel));
                }
                propertiesMap.put("pvSourceVesselCollection",
                    pvSourceVesselCollection);
            }
        }
        if ((pvSourceVesselCollection != null) && sort)
            Collections.sort(pvSourceVesselCollection);
        return pvSourceVesselCollection;
    }

    public List<PvSourceVesselWrapper> getPvSourceVesselCollection() {
        return getPvSourceVesselCollection(false);
    }

    public void addPvSourceVessels(
        Collection<PvSourceVesselWrapper> newPvSourceVessels) {
        if (newPvSourceVessels != null && newPvSourceVessels.size() > 0) {
            Collection<PvSourceVessel> allPvObjects = new HashSet<PvSourceVessel>();
            List<PvSourceVesselWrapper> allPvWrappers = new ArrayList<PvSourceVesselWrapper>();
            // already added
            List<PvSourceVesselWrapper> currentList = getPvSourceVesselCollection();
            if (currentList != null) {
                for (PvSourceVesselWrapper pvss : currentList) {
                    allPvObjects.add(pvss.getWrappedObject());
                    allPvWrappers.add(pvss);
                }
            }
            // new added
            for (PvSourceVesselWrapper pvss : newPvSourceVessels) {
                allPvObjects.add(pvss.getWrappedObject());
                allPvWrappers.add(pvss);
            }
            setPvSamplSources(allPvObjects, allPvWrappers);
        }
    }

    private void setPvSamplSources(Collection<PvSourceVessel> allPvObjects,
        List<PvSourceVesselWrapper> allPvWrappers) {
        Collection<PvSourceVessel> oldCollection = wrappedObject
            .getPvSourceVesselCollection();
        wrappedObject.setPvSourceVesselCollection(allPvObjects);
        propertyChangeSupport.firePropertyChange("pvSourceVesselCollection",
            oldCollection, allPvObjects);
        propertiesMap.put("pvSourceVesselCollection", allPvWrappers);
    }

    public void removePvSourceVessels(
        Collection<PvSourceVesselWrapper> pvSourceVesselsToRemove) {
        if (pvSourceVesselsToRemove != null
            && pvSourceVesselsToRemove.size() > 0) {
            deletedPvSourceVessels.addAll(pvSourceVesselsToRemove);
            Collection<PvSourceVessel> allPvObjects = new HashSet<PvSourceVessel>();
            List<PvSourceVesselWrapper> allPvWrappers = new ArrayList<PvSourceVesselWrapper>();
            // already added
            List<PvSourceVesselWrapper> currentList = getPvSourceVesselCollection();
            if (currentList != null) {
                for (PvSourceVesselWrapper pvss : currentList) {
                    if (!deletedPvSourceVessels.contains(pvss)) {
                        allPvObjects.add(pvss.getWrappedObject());
                        allPvWrappers.add(pvss);
                    }
                }
            }
            setPvSamplSources(allPvObjects, allPvWrappers);
        }
    }

    /**
     * Create a new aliquot (and persist it)
     * 
     * @param inventoryId
     * @param type
     * @param studySampleStorages
     */
    public AliquotWrapper addNewAliquot(String inventoryId,
        SampleTypeWrapper type, List<SampleStorageWrapper> studySampleStorages,
        ActivityStatusWrapper activityStatus) throws Exception {
        AliquotWrapper aliquot = new AliquotWrapper(appService);
        aliquot.setInventoryId(inventoryId);
        aliquot.setPatientVisit(this);
        aliquot.setLinkDate(new Date());
        aliquot.setSampleType(type);
        aliquot.setActivityStatus(activityStatus);
        Double volume = null;
        for (SampleStorageWrapper ss : studySampleStorages) {
            if (ss.getSampleType().getId().equals(type.getId())) {
                volume = ss.getVolume();
            }
        }
        aliquot.setQuantity(volume);
        aliquot.persist();
        return aliquot;
    }

    @Override
    public Class<PatientVisit> getWrappedClass() {
        return PatientVisit.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (hasAliquots()) {
            throw new BiobankCheckException("Unable to delete patient visit "
                + getDateProcessed()
                + " since it has samples stored in database.");
        }
    }

    public long getAliquotsCount() throws ApplicationException,
        BiobankCheckException {
        String queryString = "select count(aliquots) from "
            + PatientVisit.class.getName() + " as pv"
            + " left join pv.aliquotCollection as aliquots"
            + " where pv.id = ?)";
        HQLCriteria c = new HQLCriteria(queryString, Arrays
            .asList(new Object[] { wrappedObject.getId() }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return results.get(0);
    }

    public boolean hasAliquots() throws ApplicationException,
        BiobankCheckException {
        return getAliquotsCount() > 0;
    }

    @Override
    public int compareTo(ModelWrapper<PatientVisit> wrapper) {
        if (wrapper instanceof PatientVisitWrapper) {
            Date v1Date = wrappedObject.getDateProcessed();
            Date v2Date = wrapper.wrappedObject.getDateProcessed();
            if (v1Date != null && v2Date != null) {
                return v1Date.compareTo(v2Date);
            }
        }
        return 0;
    }

    @Override
    public void resetInternalField() {
        pvAttrMap = null;
        studyPvAttrMap = null;
        deletedPvSourceVessels.clear();
    }

    @Override
    public String toString() {
        return getFormattedDateProcessed();
    }

    public void logLookup() {
        ((BiobankApplicationService) appService)
            .logActivity("select", getPatient().getPnumber(), null, null,
                "visit LOOKUP (date processed=" + getFormattedDateProcessed()
                    + ")");
    }
}
