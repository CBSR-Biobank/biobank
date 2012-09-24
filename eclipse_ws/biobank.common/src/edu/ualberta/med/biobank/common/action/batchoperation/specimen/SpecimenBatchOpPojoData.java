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
 * Helper class to aid in persisting specimens via Specimen BatchOp.
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
public class SpecimenBatchOpPojoData implements IBatchOpHelper {

    private static Logger log = LoggerFactory
        .getLogger(SpecimenBatchOpPojoData.class.getName());

    private final SpecimenBatchOpInputPojo pojo;
    private SpecimenBatchOpPojoData parentInfo;
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

    SpecimenBatchOpPojoData(SpecimenBatchOpInputPojo pojo) {
        this.pojo = pojo;
    }

    @Override
    public int getCsvLineNumber() {
        return pojo.getLineNumber();
    }

    public SpecimenBatchOpInputPojo getPojo() {
        return pojo;
    }

    SpecimenBatchOpPojoData getParentInfo() {
        return parentInfo;
    }

    void setParentPojoData(SpecimenBatchOpPojoData parentInfo) {
        if (parentInfo == null) {
            throw new IllegalStateException("parentInfo is null");
        }
        this.parentInfo = parentInfo;
        log.trace("setting parent info for specimen {} to {}",
            pojo.getInventoryId(), parentInfo.pojo.getInventoryId());
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
        return pojo.getParentInventoryId();
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
        return pojo.getSourceSpecimen();
    }

    boolean isAliquotedSpecimen() {
        return !pojo.getSourceSpecimen();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    boolean hasPosition() {
        // FIXME:
        // To specify a specimen position the following are possible:
        // - columns 11 and 14 are filled in
        // - columns 12, 13, and 14 are filled in
        // - columns 11, 12, 13, and 14 are filled in, but is not recommended
        // since it is redundant

        return (pojo.getPalletLabel() != null)
            && (pojo.getPalletPosition() != null);
    }

    public OriginInfo getNewOriginInfo(Center center) {
        originInfo = new OriginInfo();
        originInfo.setCenter(center);
        return originInfo;
    }

    CollectionEvent getNewCollectionEvent() {
        cevent = new CollectionEvent();
        cevent.setPatient(patient);
        cevent.setVisitNumber(pojo.getVisitNumber());
        cevent.setActivityStatus(ActivityStatus.ACTIVE);
        patient.getCollectionEvents().add(cevent);

        log.trace("created collection event: pt={} v#={} invId={}",
            new Object[] {
                pojo.getPatientNumber(),
                pojo.getVisitNumber(),
                pojo.getInventoryId()
            });

        return cevent;
    }

    ProcessingEvent getNewProcessingEvent() {
        if (parentSpecimen != null) {
            throw new IllegalStateException(
                "this specimen has a parent specimen and cannot have a processing event");
        }
        pevent = new ProcessingEvent();
        pevent.setWorksheet(pojo.getWorksheet());
        pevent.setCreatedAt(new Date());
        pevent.setCenter(originInfo.getCenter());
        pevent.setActivityStatus(ActivityStatus.ACTIVE);
        specimen.setProcessingEvent(pevent);

        log.trace("created processing event: worksheet={} parentSpc={}",
            pojo.getWorksheet(), pojo.getInventoryId());

        return getPevent();
    }

    Specimen getNewSpecimen() {
        if (cevent == null) {
            throw new IllegalStateException(
                "specimen does not have a collection event");
        }

        if ((pojo.getParentInventoryId() != null)
            && (parentSpecimen == null)) {
            throw new IllegalStateException(
                "parent specimen for specimen with " + pojo.getInventoryId()
                    + " has not be created yet");
        }

        specimen = new Specimen();
        specimen.setInventoryId(pojo.getInventoryId());
        specimen.setSpecimenType(specimenType);

        if (originInfo.getReceiverSite() == null) {
            specimen.setCurrentCenter(originInfo.getCenter());
        } else {
            specimen.setCurrentCenter(originInfo.getReceiverSite());
        }
        specimen.setCollectionEvent(cevent);
        specimen.setOriginInfo(originInfo);
        specimen.setCreatedAt(pojo.getCreatedAt());
        specimen.setActivityStatus(ActivityStatus.ACTIVE);

        if ((pojo.getComment() != null)
            && !pojo.getComment().isEmpty()) {
            if (user == null) {
                throw new IllegalStateException(
                    "user is null, cannot add comment");
            }

            Comment comment = new Comment();
            comment.setMessage(pojo.getComment());
            comment.setUser(user);
            comment.setCreatedAt(new Date());
            specimen.getComments().add(comment);
        }

        if (isSourceSpecimen()) {
            specimen.setOriginalCollectionEvent(cevent);
            cevent.getOriginalSpecimens().add(specimen);
        } else {
            ProcessingEvent pevent = null;

            // TODO: allow child specimens with no processing event

            if (parentSpecimen != null) {
                pevent = parentSpecimen.getProcessingEvent();
            } else if ((parentInfo != null) && (parentInfo.pevent != null)) {
                pevent = parentInfo.pevent;
            }

            if (pevent != null) {
                pevent.getSpecimens().add(specimen);
            }
            SpecimenActionHelper.setParent(specimen, parentSpecimen);

            if (pojo.getVolume() != null) {
                specimen.setQuantity(pojo.getVolume());
            } else {
                SpecimenActionHelper.setQuantityFromType(specimen);
            }
        }
        cevent.getAllSpecimens().add(specimen);

        if (container != null) {
            SpecimenActionHelper.createOrChangePosition(specimen, container,
                specimenPos);
        }

        log.trace("creating specimen: pt={} v#={} invId={} isParent={}",
            new Object[] {
                pojo.getPatientNumber(),
                pojo.getVisitNumber(),
                pojo.getInventoryId(),
                specimen.getOriginalCollectionEvent() != null
            });

        return specimen;
    }
}
