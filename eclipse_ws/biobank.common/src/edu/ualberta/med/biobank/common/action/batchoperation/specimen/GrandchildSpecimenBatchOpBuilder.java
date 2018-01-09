package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorSet;
import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpPojoHelper;
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

@SuppressWarnings("nls")
public class GrandchildSpecimenBatchOpBuilder implements IBatchOpPojoHelper {

    private static Logger log = LoggerFactory
        .getLogger(SpecimenBatchOpBuilder.class.getName());

    private final BatchOpInputErrorSet errorSet = new BatchOpInputErrorSet();

    private final GrandchildSpecimenBatchOpInputPojo pojo;
    private Patient patient;
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

    public GrandchildSpecimenBatchOpBuilder(GrandchildSpecimenBatchOpInputPojo pojo) {
        this.pojo = pojo;
    }

    public GrandchildSpecimenBatchOpInputPojo getPojo() {
        return pojo;
    }

    Patient getPatient() {
        return patient;
    }

    void setPatient(Patient patient) {
        this.patient = patient;
    }

    public CollectionEvent getCevent() {
        if (parentSpecimen == null) {
            throw new NullPointerException("parentSpecimen is null");
        }
        return parentSpecimen.getCollectionEvent();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    boolean hasPosition() {
        boolean positionWithLabel = (pojo.getPalletLabel() != null)
            && (pojo.getPalletPosition() != null);
        boolean positionWithProductBarcode = (pojo.getPalletProductBarcode() != null)
            && (pojo.getPalletPosition() != null);
        return positionWithLabel || positionWithProductBarcode;
    }

    /**
     * Called to validate if the grandchild specimen was built correctly.
     *
     * @return
     */
    boolean validate() {
        return (parentSpecimen != null) && (patient == null);
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
     * Creates a new specimen from the information stored into this builder.
     *
     * <p>
     * Adds this specimen to the collection event associated with this builder.
     *
     * @return the new specimen.
     */
    Specimen createNewSpecimen() {
        if (parentSpecimen == null) {
            throw new IllegalStateException(
                "parent specimen for specimen with " + pojo.getInventoryId()
                + " has not be created yet");
        }

        if (currentCenter == null) {
            currentCenter = originInfo.getCenter();
        }

        CollectionEvent cevent = parentSpecimen.getCollectionEvent();

        specimen = new Specimen();
        specimen.setInventoryId(pojo.getInventoryId());
        specimen.setSpecimenType(specimenType);
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

        SpecimenActionHelper.setParent(specimen, parentSpecimen);

        if (pojo.getVolume() != null) {
            specimen.setQuantity(pojo.getVolume());
        } else {
            SpecimenActionHelper.setQuantityFromType(specimen);
        }

        cevent.getAllSpecimens().add(specimen);

        if (container != null) {
            SpecimenActionHelper.createOrChangePosition(specimen, container, specimenPos);
        }

        log.trace("creating specimen: pt={} invId={} isParent={}",
            new Object[] {
                pojo.getPatientNumber(),
                pojo.getInventoryId(),
                specimen.getOriginalCollectionEvent() != null
            });

        return specimen;
    }

}
