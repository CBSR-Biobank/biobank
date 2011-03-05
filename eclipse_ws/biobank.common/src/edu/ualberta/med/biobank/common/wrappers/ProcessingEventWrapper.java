package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenLinkPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ProcessingEventBaseWrapper;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ProcessingEventWrapper extends ProcessingEventBaseWrapper {

    private Set<SpecimenWrapper> deletedChildSpecimens = new HashSet<SpecimenWrapper>();

    private static final String CHILD_SPECIMEN_COLLECTION_PROPERTY_NAME = "childSpecimenCollection";
    private static final String SOURCE_SPECIMEN_COLLECTION_PROPERTY_NAME = "sourceSpecimenCollection";

    public ProcessingEventWrapper(WritableApplicationService appService,
        ProcessingEvent wrappedObject) {
        super(appService, wrappedObject);
    }

    public ProcessingEventWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        // TODO: new checks required
        // TODO at least one specimen added ?
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

    private static final String CHILD_SPECIMEN_COUNT_QRY = "select count(specimen) from "
        + Specimen.class.getName()
        + " as specimen where specimen."
        + Property.concatNames(SpecimenPeer.PARENT_SPECIMEN_LINK,
            SpecimenLinkPeer.PROCESSING_EVENT, ProcessingEventPeer.ID) + "=?";

    public long getChildSpecimenCount(boolean fast) throws BiobankException,
        ApplicationException {
        if (fast) {
            HQLCriteria criteria = new HQLCriteria(CHILD_SPECIMEN_COUNT_QRY,
                Arrays.asList(new Object[] { getId() }));
            return getCountResult(appService, criteria);
        }
        return getSourceSpecimenCollection(false).size();
    }

    public void addSpecimens(List<SpecimenWrapper> specimens) {
        List<SpecimenLinkWrapper> links = new ArrayList<SpecimenLinkWrapper>();
        for (SpecimenWrapper specimen : specimens) {
            SpecimenLinkWrapper link = new SpecimenLinkWrapper(appService);
            link.setProcessingEvent(this);
            link.setParentSpecimen(specimen);
            links.add(link);
        }
        addToSpecimenLinkCollection(links);
        // FIXME might be better to update the list, but for now will do that
        // way
        propertiesMap.put(CHILD_SPECIMEN_COLLECTION_PROPERTY_NAME, null);
        propertiesMap.put(SOURCE_SPECIMEN_COLLECTION_PROPERTY_NAME, null);
    }

    @SuppressWarnings("unchecked")
    public List<SpecimenWrapper> getSourceSpecimenCollection(boolean sort) {
        List<SpecimenWrapper> specimenCollection = (List<SpecimenWrapper>) propertiesMap
            .get(SOURCE_SPECIMEN_COLLECTION_PROPERTY_NAME);
        if (specimenCollection == null) {
            specimenCollection = new ArrayList<SpecimenWrapper>();
            List<SpecimenLinkWrapper> links = getSpecimenLinkCollection(false);
            for (SpecimenLinkWrapper link : links) {
                specimenCollection.add(link.getParentSpecimen());
            }
            if (sort)
                Collections.sort(specimenCollection);
            propertiesMap.put("SOURCE_SPECIMEN_COLLECTIOn_PROPERTY_NAME",
                specimenCollection);
        }
        return specimenCollection;
    }

    @SuppressWarnings("unchecked")
    public List<SpecimenWrapper> getChildSpecimenCollection(boolean sort) {
        List<SpecimenWrapper> specimenCollection = (List<SpecimenWrapper>) propertiesMap
            .get(CHILD_SPECIMEN_COLLECTION_PROPERTY_NAME);
        if (specimenCollection == null) {
            specimenCollection = new ArrayList<SpecimenWrapper>();
            List<SpecimenLinkWrapper> links = getSpecimenLinkCollection(false);
            for (SpecimenLinkWrapper link : links) {
                specimenCollection.addAll(link
                    .getChildSpecimenCollection(false));
            }
            if (sort)
                Collections.sort(specimenCollection);
            propertiesMap.put(CHILD_SPECIMEN_COLLECTION_PROPERTY_NAME,
                specimenCollection);
        }
        return specimenCollection;
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
        // FIXME getLogMessage ?
        // CollectionEventWrapper cevent = getParentSpecimen()
        // .getCollectionEvent();
        // PatientWrapper patient = cevent.getPatient();
        // if (site == null) {
        // log.setSite(getCenter().getNameShort());
        // } else {
        // log.setSite(site);
        // }
        // log.setPatientNumber(patient.getPnumber());
        // Date createdAt = getCreatedAt();
        // if (createdAt != null) {
        // details += " Date Processed: " + getFormattedCreatedAt();
        // }
        // try {
        // String worksheet = cevent.getEventAttrValue("Worksheet");
        // if (worksheet != null) {
        // details += " - Worksheet: " + worksheet;
        // }
        // } catch (Exception e) {
        // }
        // log.setDetails(details);
        // log.setType("Visit");
        return log;
    }

    private static final String PROCESSING_EVENT_BY_WORKSHEET_QRY = "select pEvent from "
        + ProcessingEvent.class.getName()
        + " pEvent where pEvent."
        + ProcessingEventPeer.WORKSHEET.getName() + "=?";

    public static List<ProcessingEventWrapper> getProcessingEventsWithWorksheet(
        WritableApplicationService appService, String worksheetNumber)
        throws Exception {
        HQLCriteria c = new HQLCriteria(PROCESSING_EVENT_BY_WORKSHEET_QRY,
            Arrays.asList(new Object[] { worksheetNumber }));
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
