package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.AliquotPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.PvAttrPeer;
import edu.ualberta.med.biobank.common.peer.StudyPvAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ProcessingEventBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.PvAttrWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.StudyPvAttrWrapper;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.PvAttr;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ProcessingEventWrapper extends ProcessingEventBaseWrapper {

    private Map<String, StudyPvAttrWrapper> studyPvAttrMap;

    private Map<String, PvAttrWrapper> pvAttrMap;

    private Set<SourceVesselWrapper> deletedSourceVessels = new HashSet<SourceVesselWrapper>();

    public ProcessingEventWrapper(WritableApplicationService appService,
        ProcessingEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    public ProcessingEventWrapper(WritableApplicationService appService) {
        super(appService);
    }

    /**
     * will set the adequate volume to the added aliquots
     * 
     * @throws BiobankCheckException
     */
    public void addAliquots(List<AliquotWrapper> aliquots)
        throws BiobankCheckException {
        if (aliquots != null && aliquots.size() > 0) {
            List<SampleStorageWrapper> sampleStorages = getPatient().getStudy()
                .getSampleStorageCollection(false);
            if (sampleStorages == null || sampleStorages.size() == 0) {
                throw new BiobankCheckException(
                    "Can only add aliquots in a visit which study has sample storages");
            }

            Collection<Aliquot> allAliquotObjects = new HashSet<Aliquot>();
            List<AliquotWrapper> allAliquotWrappers = new ArrayList<AliquotWrapper>();
            // already added
            List<AliquotWrapper> currentList = getAliquotCollection(false);
            if (currentList != null) {
                for (AliquotWrapper aliquot : currentList) {
                    allAliquotObjects.add(aliquot.getWrappedObject());
                    allAliquotWrappers.add(aliquot);
                }
            }
            // new added

            // will set the adequate volume to the added aliquots
            Map<Integer, Double> typesVolumes = new HashMap<Integer, Double>();
            for (SampleStorageWrapper ss : sampleStorages) {
                typesVolumes.put(ss.getSampleType().getId(), ss.getVolume());
            }
            for (AliquotWrapper aliquot : aliquots) {
                aliquot.setQuantity(typesVolumes.get(aliquot.getSampleType()
                    .getId()));
                aliquot.setProcessingEvent(this);
                allAliquotObjects.add(aliquot.getWrappedObject());
                allAliquotWrappers.add(aliquot);
            }
            setWrapperCollection(ProcessingEventPeer.ALIQUOT_COLLECTION,
                allAliquotWrappers);
        }
    }

    private Map<String, StudyPvAttrWrapper> getStudyPvAttrMap() {
        if (studyPvAttrMap != null)
            return studyPvAttrMap;

        studyPvAttrMap = new HashMap<String, StudyPvAttrWrapper>();
        if (getPatient() != null && getPatient().getStudy() != null) {
            Collection<StudyPvAttrWrapper> studyPvAttrCollection = getPatient()
                .getStudy().getStudyPvAttrCollection();
            if (studyPvAttrCollection != null) {
                for (StudyPvAttrWrapper studyPvAttr : studyPvAttrCollection) {
                    studyPvAttrMap.put(studyPvAttr.getLabel(), studyPvAttr);
                }
            }
        }
        return studyPvAttrMap;
    }

    private Map<String, PvAttrWrapper> getPvAttrMap() {
        getStudyPvAttrMap();
        if (pvAttrMap != null)
            return pvAttrMap;

        pvAttrMap = new HashMap<String, PvAttrWrapper>();
        List<PvAttrWrapper> pvAttrCollection = getPvAttrCollection(false);
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

        if (value != null) {
            // validate the value
            value = value.trim();
            if (value.length() > 0) {
                String type = studyPvAttr.getPvAttrType().getName();
                List<String> permissibleSplit = null;

                if (type.equals("select_single")
                    || type.equals("select_multiple")) {
                    String permissible = studyPvAttr.getPermissible();
                    if (permissible != null) {
                        permissibleSplit = Arrays
                            .asList(permissible.split(";"));
                    }
                }

                if (type.equals("select_single")) {
                    if (!permissibleSplit.contains(value)) {
                        throw new Exception("value " + value
                            + "is invalid for label \"" + label + "\"");
                    }
                } else if (type.equals("select_multiple")) {
                    for (String singleVal : value.split(";")) {
                        if (!permissibleSplit.contains(singleVal)) {
                            throw new Exception("value " + singleVal + " ("
                                + value + ") is invalid for label \"" + label
                                + "\"");
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
        }

        if (pvAttr == null) {
            pvAttr = new PvAttrWrapper(appService);
            pvAttr.setProcessingEvent(this);
            pvAttr.setStudyPvAttr(studyPvAttr);
            pvAttrMap.put(label, pvAttr);
        }
        pvAttr.setValue(value);
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        // patient to clinic relationship tested by shipment, so no need to
        // test it again here
        // TODO: new checks required
        // TODO at least one sourcewrapper ?
    }

    @Override
    protected void persistDependencies(ProcessingEvent origObject)
        throws Exception {
        if (pvAttrMap != null) {
            setWrapperCollection(ProcessingEventPeer.PV_ATTR_COLLECTION,
                pvAttrMap.values());
        }
        deleteSourceVessels();
    }

    private void deleteSourceVessels() throws Exception {
        for (SourceVesselWrapper ss : deletedSourceVessels) {
            if (!ss.isNew()) {
                ss.delete();
            }
        }
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (getAliquotsCount(false) > 0) {
            throw new BiobankCheckException("Unable to delete patient visit "
                + getDateProcessed()
                + " since it has samples stored in database.");
        }
    }

    private static final String ALIQUOT_COUNT_QRY = "select count(aliquot) from "
        + Aliquot.class.getName()
        + " as aliquot where aliquot."
        + Property.concatNames(AliquotPeer.PROCESSING_EVENT,
            ProcessingEventPeer.ID) + "=?";

    public long getAliquotsCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(ALIQUOT_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        List<AliquotWrapper> list = getAliquotCollection(false);
        if (list == null)
            return 0;
        return getAliquotCollection(false).size();
    }

    @Override
    public int compareTo(ModelWrapper<ProcessingEvent> wrapper) {
        if (wrapper instanceof ProcessingEventWrapper) {
            Date v1Date = getProperty(ProcessingEventPeer.DATE_PROCESSED);
            Date v2Date = wrapper.wrappedObject.getDateProcessed();
            if (v1Date != null && v2Date != null) {
                return v1Date.compareTo(v2Date);
            }
        }
        return 0;
    }

    @Override
    public void resetInternalFields() {
        pvAttrMap = null;
        studyPvAttrMap = null;
        deletedSourceVessels.clear();
    }

    @Override
    public String toString() {
        return "Date Processed:" + getFormattedDateProcessed()
            + " / Date Drawn: " + getFormattedDateDrawn();
    }

    public String getFormattedDateDrawn() {
        return DateFormatter.formatAsDate(getDateDrawn());
    }

    public String getFormattedDateProcessed() {
        return DateFormatter.formatAsDate(getDateProcessed());
    }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        Log log = new Log();
        log.setAction(action);
        PatientWrapper patient = getPatient();
        if (site == null) {
            log.setSite(getCenter().getNameShort());
        } else {
            log.setSite(site);
        }
        log.setPatientNumber(patient.getPnumber());
        Date dateProcesssed = getDateProcessed();
        if (dateProcesssed != null) {
            details += " Date Processed: " + getFormattedDateProcessed();
        }
        try {
            String worksheet = getPvAttrValue("Worksheet");
            if (worksheet != null) {
                details += " - Worksheet: " + worksheet;
            }
        } catch (Exception e) {
        }
        log.setDetails(details);
        log.setType("Visit");
        return log;
    }

    private static final String PROCESSING_EVENT_BY_WORKSHEET_QRY = "select pva.processingEvent from "
        + PvAttr.class.getName()
        + " pva where pva."
        + Property.concatNames(PvAttrPeer.STUDY_PV_ATTR, StudyPvAttrPeer.LABEL)
        + "? and pva." + PvAttrPeer.VALUE.getName() + "=?";

    public static List<ProcessingEventWrapper> getProcessingEventsWithWorksheet(
        WritableApplicationService appService, String worksheetNumber)
        throws Exception {
        HQLCriteria c = new HQLCriteria(PROCESSING_EVENT_BY_WORKSHEET_QRY,
            Arrays.asList(new Object[] { "Worksheet", worksheetNumber }));
        List<ProcessingEvent> pvs = appService.query(c);
        List<ProcessingEventWrapper> pvws = new ArrayList<ProcessingEventWrapper>();
        for (ProcessingEvent pv : pvs)
            pvws.add(new ProcessingEventWrapper(appService, pv));
        if (pvws.size() == 0)
            return null;
        return pvws;
    }

    @Override
    public CenterWrapper<?> getCenterLinkedToObject() {
        return getCenter();
    }

    public List<AliquotWrapper> getAliquotCollection() {
        return getAliquotCollection(false);
    }

    @Deprecated
    public CollectionEventWrapper getCollectionEvent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public void setShipment(CollectionEventWrapper shipmentToBeSaved) {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public void addPvSourceVessels(
        List<PvSourceVesselWrapper> addedPvSourceVessels) {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public static List<ProcessingEventWrapper> getPatientVisitsWithWorksheet(
        BiobankApplicationService appService, String searchString) {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public List<PvSourceVesselWrapper> getPvSourceVesselCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public void removePvSourceVessels(
        List<PvSourceVesselWrapper> removedPvSourceVessels) {
        // TODO Auto-generated method stub

    }
}
