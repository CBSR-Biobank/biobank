package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpHelper;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenActionHelper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.util.RowColPos;

/**
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public class SpecimenBatchOpHelper implements IBatchOpHelper {

    private static Logger log = LoggerFactory
        .getLogger(SpecimenBatchOpHelper.class.getName());

    private final SpecimenBatchOpInputRow csvInfo;
    private SpecimenBatchOpHelper parentInfo;
    private Patient patient;
    private CollectionEvent cevent;
    private ProcessingEvent pevent;
    private Specimen parentSpecimen;
    private OriginInfo originInfo;
    private SpecimenType specimenType;
    private Container container;
    private RowColPos specimenPos;
    private Specimen specimen;
    private User user;

    SpecimenBatchOpHelper(SpecimenBatchOpInputRow csvInfo) {
        this.csvInfo = csvInfo;
    }

    @Override
    public int getCsvLineNumber() {
        return csvInfo.getLineNumber();
    }

    public SpecimenBatchOpInputRow getCsvInfo() {
        return csvInfo;
    }

    SpecimenBatchOpHelper getParentInfo() {
        return parentInfo;
    }

    void setParentInfo(SpecimenBatchOpHelper parentInfo) {
        if (parentInfo == null) {
            throw new IllegalStateException("parentInfo is null");
        }
        this.parentInfo = parentInfo;
        log.trace("setting parent info for specimen {} to {}",
            csvInfo.getInventoryId(), parentInfo.csvInfo.getInventoryId());
    }

    Patient getPatient() {
        return patient;
    }

    void setPatient(Patient patient) {
        this.patient = patient;
    }

    public CollectionEvent getCevent() {
        return cevent;
    }

    void setCevent(CollectionEvent cevent) {
        this.cevent = cevent;
    }

    ProcessingEvent getPevent() {
        return pevent;
    }

    void setPevent(ProcessingEvent pevent) {
        this.pevent = pevent;
    }

    Specimen getParentSpecimen() {
        return parentSpecimen;
    }

    void setParentSpecimen(Specimen parentSpecimen) {
        this.parentSpecimen = parentSpecimen;
        this.pevent = parentSpecimen.getProcessingEvent();
    }

    String getParentInventoryId() {
        return csvInfo.getParentInventoryId();
    }

    OriginInfo getOriginInfo() {
        return originInfo;
    }

    void setOriginInfo(OriginInfo originInfo) {
        this.originInfo = originInfo;
    }

    SpecimenType getSpecimenType() {
        return specimenType;
    }

    void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    Container getContainer() {
        return container;
    }

    void setContainer(Container container) {
        this.container = container;
    }

    RowColPos getSpecimenPos() {
        return specimenPos;
    }

    void setSpecimenPos(RowColPos specimenPos) {
        this.specimenPos = specimenPos;
    }

    boolean isSourceSpecimen() {
        return csvInfo.getSourceSpecimen();
    }

    boolean isAliquotedSpecimen() {
        return !csvInfo.getSourceSpecimen();
    }

    boolean hasParentInventoryId() {
        return (csvInfo.getParentInventoryId() != null)
            && !csvInfo.getParentInventoryId().isEmpty();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    boolean hasWorksheet() {
        return (csvInfo.getWorksheet() != null)
            && !csvInfo.getWorksheet().isEmpty();
    }

    boolean hasPosition() {
        return (csvInfo.getPalletLabel() != null)
            && !csvInfo.getPalletLabel().isEmpty()
            && (csvInfo.getPalletPosition() != null)
            && !csvInfo.getPalletPosition().isEmpty();

    }

    public OriginInfo getNewOriginInfo(Center center) {
        originInfo = new OriginInfo();
        originInfo.setCenter(center);
        return originInfo;
    }

    CollectionEvent getNewCollectionEvent() {
        cevent = new CollectionEvent();
        cevent.setPatient(patient);
        cevent.setVisitNumber(csvInfo.getVisitNumber());
        cevent.setActivityStatus(ActivityStatus.ACTIVE);
        patient.getCollectionEvents().add(cevent);

        log.trace("created collection event: pt={} v#={} invId={}",
            new Object[] {
                csvInfo.getPatientNumber(),
                csvInfo.getVisitNumber(),
                csvInfo.getInventoryId()
            });

        return cevent;
    }

    ProcessingEvent getNewProcessingEvent() {
        if (parentSpecimen != null) {
            throw new IllegalStateException(
                "this specimen has a parent specimen and cannot have a processing event");
        }
        pevent = new ProcessingEvent();
        pevent.setWorksheet(csvInfo.getWorksheet());
        pevent.setCreatedAt(new Date());
        pevent.setCenter(originInfo.getCenter());
        pevent.setActivityStatus(ActivityStatus.ACTIVE);
        specimen.setProcessingEvent(pevent);

        log.trace("created processing event: worksheet={} parentSpc={}",
            csvInfo.getWorksheet(), csvInfo.getInventoryId());

        return getPevent();
    }

    Specimen getNewSpecimen() {
        if (cevent == null) {
            throw new IllegalStateException(
                "specimen does not have a collection event");
        }

        if ((csvInfo.getParentInventoryId() != null)
            && !csvInfo.getParentInventoryId().isEmpty()
            && (parentSpecimen == null)) {
            throw new IllegalStateException(
                "parent specimen for specimen with " + csvInfo.getInventoryId()
                    + " has not be created yet");
        }

        specimen = new Specimen();
        specimen.setInventoryId(csvInfo.getInventoryId());
        specimen.setSpecimenType(specimenType);

        if (originInfo.getReceiverSite() == null) {
            specimen.setCurrentCenter(originInfo.getCenter());
        } else {
            specimen.setCurrentCenter(originInfo.getReceiverSite());
        }
        specimen.setCollectionEvent(cevent);
        specimen.setOriginInfo(originInfo);
        specimen.setCreatedAt(csvInfo.getCreatedAt());
        specimen.setActivityStatus(ActivityStatus.ACTIVE);

        if ((csvInfo.getComment() != null)
            && !csvInfo.getComment().isEmpty()) {
            if (user == null) {
                throw new IllegalStateException(
                    "user is null, cannot add comment");
            }

            Comment comment = new Comment();
            comment.setMessage(csvInfo.getComment());
            comment.setUser(user);
            comment.setCreatedAt(new Date());
            specimen.getComments().add(comment);
        }

        if (isSourceSpecimen()) {
            specimen.setOriginalCollectionEvent(cevent);
            cevent.getOriginalSpecimens().add(specimen);
        } else {
            ProcessingEvent pevent;

            if (parentSpecimen != null) {
                pevent = parentSpecimen.getProcessingEvent();
            } else {
                if (parentInfo.pevent == null) {
                    throw new IllegalStateException(
                        "parent specimen pevent is null");
                }
                pevent = parentInfo.pevent;
            }
            pevent.getSpecimens().add(specimen);
            SpecimenActionHelper.setParent(specimen, parentSpecimen);
            SpecimenActionHelper.setQuantityFromType(specimen);
        }
        cevent.getAllSpecimens().add(specimen);

        if (container != null) {
            SpecimenActionHelper.createOrChangePosition(specimen, container,
                specimenPos);
        }

        log.trace("creating specimen: pt={} v#={} invId={} isParent={}",
            new Object[] {
                csvInfo.getPatientNumber(),
                csvInfo.getVisitNumber(),
                csvInfo.getInventoryId(),
                specimen.getOriginalCollectionEvent() != null
            });

        return specimen;
    }
}
