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
import edu.ualberta.med.biobank.common.peer.EventAttrPeer;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.StudyEventAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ProcessingEventBaseWrapper;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ProcessingEventWrapper extends ProcessingEventBaseWrapper {

    private Set<SpecimenWrapper> deletedChildSpecimens = new HashSet<SpecimenWrapper>();

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
    public void addChildSpecimens(List<SpecimenWrapper> childSpecimens)
        throws BiobankCheckException {
        SpecimenWrapper parentSpecimen = getParentSpecimen();

        if (parentSpecimen == null) {
            throw new NullPointerException();
        }

        if (childSpecimens == null || childSpecimens.isEmpty()) {
            return;
        }

        List<AliquotedSpecimenWrapper> sampleStorages = getParentSpecimen()
            .getCollectionEvent().getPatient().getStudy()
            .getAliquotedSpecimenCollection(false);
        if (sampleStorages == null || sampleStorages.size() == 0) {
            throw new BiobankCheckException(
                "Can only add aliquots in a visit which study has sample storages");
        }

        Collection<Specimen> allSpecimenObjects = new HashSet<Specimen>();
        List<SpecimenWrapper> allSpecimenWrappers = new ArrayList<SpecimenWrapper>();
        // already added
        List<SpecimenWrapper> currentList = getChildSpecimenCollection(false);
        if (currentList != null) {
            for (SpecimenWrapper aliquot : currentList) {
                allSpecimenObjects.add(aliquot.getWrappedObject());
                allSpecimenWrappers.add(aliquot);
            }
        }
        // new added

        // will set the adequate volume to the added aliquots
        Map<Integer, Double> typesVolumes = new HashMap<Integer, Double>();
        for (AliquotedSpecimenWrapper ss : sampleStorages) {
            typesVolumes.put(ss.getSpecimenType().getId(), ss.getVolume());
        }
        for (SpecimenWrapper aliquot : childSpecimens) {
            aliquot.setQuantity(typesVolumes.get(aliquot.getSpecimenType()
                .getId()));
            aliquot.setParentProcessingEvent(this);
            allSpecimenObjects.add(aliquot.getWrappedObject());
            allSpecimenWrappers.add(aliquot);
        }
        setWrapperCollection(ProcessingEventPeer.CHILD_SPECIMEN_COLLECTION,
            allSpecimenWrappers);
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
        deleteSpecimens();
    }

    private void deleteSpecimens() throws Exception {
        for (SpecimenWrapper ss : deletedChildSpecimens) {
            if (!ss.isNew()) {
                ss.delete();
            }
        }
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (getChildSpecimenCount(false) > 0) {
            throw new BiobankCheckException(
                "Unable to delete processing event " + getCreatedAt()
                    + " since it has child specimens stored in database.");
        }
    }

    private static final String CHILD_SPECIMEN_COUNT_QRY = "select count(aliquot) from "
        + Specimen.class.getName()
        + " as aliquot where aliquot."
        + Property.concatNames(SpecimenPeer.PARENT_PROCESSING_EVENT,
            ProcessingEventPeer.ID) + "=?";

    public long getChildSpecimenCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(CHILD_SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        return getChildSpecimenCollection(false).size();
    }

    @Override
    public int compareTo(ModelWrapper<ProcessingEvent> wrapper) {
        if (wrapper instanceof ProcessingEventWrapper) {
            Date v1Date = getCreatedAt();
            Date v2Date = ((ProcessingEventWrapper) wrapper).getCreatedAt();
            if (v1Date != null && v2Date != null) {
                return v1Date.compareTo(v2Date);
            }
        }
        return 0;
    }

    @Override
    public void resetInternalFields() {
        deletedChildSpecimens.clear();
    }

    @Override
    public String toString() {
        return "Date Processed:" + getFormattedDateProcessed()
            + " / Date Drawn: " + getFormattedCreatedAt();
    }

    public String getFormattedCreatedAt() {
        return DateFormatter.formatAsDate(getCreatedAt());
    }

    @Deprecated
    public String getFormattedDateProcessed() {
        // use getFormattedCreatedAt()
        return null;
    }

    @Override
    protected Log getLogMessage(String action, String site, String details) {
        Log log = new Log();
        log.setAction(action);
        CollectionEventWrapper cevent = getParentSpecimen()
            .getCollectionEvent();
        PatientWrapper patient = cevent.getPatient();
        if (site == null) {
            log.setSite(getCenter().getNameShort());
        } else {
            log.setSite(site);
        }
        log.setPatientNumber(patient.getPnumber());
        Date createdAt = getCreatedAt();
        if (createdAt != null) {
            details += " Date Processed: " + getFormattedCreatedAt();
        }
        try {
            String worksheet = cevent.getEventAttrValue("Worksheet");
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
        + EventAttr.class.getName()
        + " pva where pva."
        + Property.concatNames(EventAttrPeer.STUDY_EVENT_ATTR,
            StudyEventAttrPeer.LABEL)
        + "? and pva."
        + EventAttrPeer.VALUE.getName() + "=?";

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

}
