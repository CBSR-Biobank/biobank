package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorSet;
import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpPojoHelper;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenActionHelper;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Tr;
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
 * Builds the following model entities from information collected from a CSV file containing
 * specimens.
 * 
 * <ul>
 * <li>{@link edu.ualberta.med.biobank.model.Specimen Specimen}</li>
 * <li>{@link edu.ualberta.med.biobank.model.CollectionEvent CollectionEvent}</li>
 * <li>{@link edu.ualberta.med.biobank.model.ProcessingEvent ProcessingEvent}</li>
 * </ul>
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
public class SpecimenBatchOpBuilder implements IBatchOpPojoHelper {

    private static Logger log = LoggerFactory
        .getLogger(SpecimenBatchOpBuilder.class.getName());

    private static final Bundle bundle = new CommonBundle();

    public static final Tr CSV_NO_PATIENT_ERROR = bundle.tr("specimen has no patient");

    private final BatchOpInputErrorSet errorSet = new BatchOpInputErrorSet();

    private final SpecimenBatchOpInputPojo pojo;
    private final SpecimenBatchOpInputPojo parentPojo;
    private SpecimenBatchOpBuilder parentPojoData;
    private Patient patient;
    private CollectionEvent cevent;
    private ProcessingEvent pevent;
    private Specimen parentSpecimen;
    private OriginInfo originInfo;
    private Center originCenter;
    private Center currentCenter;
    private SpecimenType specimenType;
    private Container container;
    private RowColPos specimenPos;
    private Specimen specimen;
    private User user;

    SpecimenBatchOpBuilder(SpecimenBatchOpInputPojo pojo, SpecimenBatchOpInputPojo parentPojo) {
        this.pojo = pojo;
        this.parentPojo = parentPojo;
    }

    public SpecimenBatchOpInputPojo getPojo() {
        return pojo;
    }

    SpecimenBatchOpBuilder getParentInfo() {
        return parentPojoData;
    }

    void setParentPojoData(SpecimenBatchOpBuilder parentInfo) {
        if (parentInfo == null) {
            throw new IllegalStateException("parentInfo is null");
        }
        this.parentPojoData = parentInfo;
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
        if (parentSpecimen == null) {
            throw new NullPointerException("parentSpecimen is null");
        }
        this.parentSpecimen = parentSpecimen;
        this.pevent = parentSpecimen.getProcessingEvent();
    }

    String getParentInventoryId() {
        return pojo.getParentInventoryId();
    }

    public Center getOriginCenter() {
        return originCenter;
    }

    public void setOriginCenter(Center originCenter) {
        this.originCenter = originCenter;
    }

    OriginInfo getOriginInfo() {
        return originInfo;
    }

    void setOriginInfo(OriginInfo originInfo) {
        this.originInfo = originInfo;
    }

    public Center getCurrentCenter() {
        return currentCenter;
    }

    public void setCurrentCenter(Center currentCenter) {
        this.currentCenter = currentCenter;
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
        return (pojo.getPalletLabel() != null) && (pojo.getPalletPosition() != null);
    }

    /**
     * Called to validate if the specimen was built correctly.
     * 
     * @return
     */
    boolean validate() {
        // ensure that aliquoted specimens with parent specimens already
        // in the database have a patient
        if ((parentSpecimen != null) && (patient == null) && (parentPojo == null)) {
            errorSet.addError(pojo.getLineNumber(), CSV_NO_PATIENT_ERROR.format());
            return false;
        }
        return true;
    }

/**
     * Returns a set of errors ({@link edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorSet BatchOpInputErrorSet) 
     * if the validation failed and the specimen cannot be built.
     * 
     * @return
     */
    public BatchOpInputErrorSet getErrorList() {
        return errorSet;
    }

    @Override
    public int getCsvLineNumber() {
        return pojo.getLineNumber();
    }

    /**
     * Assigns member "originInfo" for this specimen.
     * 
     * <p>
     * The origin info will be used in {@link #getNewSpecimen}.
     * 
     * @param center where this speicmen originated from.
     * 
     * @return A new origin info with center assigned to <code>center</code>.
     */
    public OriginInfo createNewOriginInfo(Center center) {
        originInfo = new OriginInfo();
        originInfo.setCenter(center);
        return originInfo;
    }

    /**
     * Creates a new collection event for the specimen stored into this builder.
     * 
     * @return a new collection event.
     */
    CollectionEvent createNewCollectionEvent() {
        cevent = new CollectionEvent();
        cevent.setPatient(patient);
        cevent.setVisitNumber(pojo.getVisitNumber());
        cevent.setActivityStatus(ActivityStatus.ACTIVE);

        if (patient == null) {
            throw new IllegalStateException("patient is null");
        }

        patient.getCollectionEvents().add(cevent);

        log.trace("created collection event: pt={} v#={} invId={}",
            new Object[] {
                pojo.getPatientNumber(),
                pojo.getVisitNumber(),
                pojo.getInventoryId()
            });

        return cevent;
    }

    /**
     * Creates a new processing event for the specimen stored into this builder.
     * 
     * @return a new processing event.
     */
    ProcessingEvent createNewProcessingEvent() {
        if (parentSpecimen != null) {
            throw new IllegalStateException(
                "this specimen has a parent specimen and cannot have a processing event");
        }
        pevent = new ProcessingEvent();
        pevent.setWorksheet(pojo.getWorksheet());
        pevent.setCreatedAt(pojo.getCreatedAt());

        Center peventCenter = (pojo.getWaybill() == null)
            ? originInfo.getCenter() : originInfo.getReceiverCenter();
        pevent.setCenter(peventCenter);

        pevent.setActivityStatus(ActivityStatus.ACTIVE);
        specimen.setProcessingEvent(pevent);
        pevent.getSpecimens().add(specimen);

        log.trace("created processing event: worksheet={} parentSpc={}",
            pojo.getWorksheet(), pojo.getInventoryId());

        return pevent;
    }

    /**
     * Creates a new specimen from the information stored into this builder.
     * 
     * <p>
     * Adds this specimen to the collection event associated with this builder.
     * 
     * @return the new specimen.
     */
    Specimen createNewSpecimen() {
        if (cevent == null) {
            throw new IllegalStateException("specimen does not have a collection event");
        }

        if ((pojo.getParentInventoryId() != null) && (parentSpecimen == null)) {
            throw new IllegalStateException(
                "parent specimen for specimen with " + pojo.getInventoryId()
                    + " has not be created yet");
        }

        if (currentCenter == null) {
            currentCenter = originInfo.getCenter();
        }

        specimen = new Specimen();
        specimen.setInventoryId(pojo.getInventoryId());
        specimen.setSpecimenType(specimenType);
        specimen.setPlateErrors(pojo.getPlateErrors());
        specimen.setSampleErrors(pojo.getSamplEerrors());
        specimen.setCurrentCenter(currentCenter);
        specimen.setCollectionEvent(cevent);
        specimen.setOriginInfo(originInfo);
        specimen.setCreatedAt(pojo.getCreatedAt());
        specimen.setActivityStatus(ActivityStatus.ACTIVE);

        if ((pojo.getComment() != null) && !pojo.getComment().isEmpty()) {
            if (user == null) {
                throw new IllegalStateException("user is null, cannot add comment");
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

            if (pevent != null) {
                specimen.setProcessingEvent(pevent);
            }
        } else {
            SpecimenActionHelper.setParent(specimen, parentSpecimen);

            if (pojo.getVolume() != null) {
                specimen.setQuantity(pojo.getVolume());
            } else {
                SpecimenActionHelper.setQuantityFromType(specimen);
            }
        }
        cevent.getAllSpecimens().add(specimen);

        if (container != null) {
            SpecimenActionHelper.createOrChangePosition(specimen, container, specimenPos);
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
