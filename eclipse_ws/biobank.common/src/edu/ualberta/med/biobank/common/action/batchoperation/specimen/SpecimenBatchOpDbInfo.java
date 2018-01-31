package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorSet;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;

/**
 * Store database entities for pojos used in specimen batch operations.
 *
 * @author Nelson Loyola
 */
public class SpecimenBatchOpDbInfo extends CommonSpecimenPojoDbInfo<SpecimenBatchOpInputPojo> {

    private static Logger log = LoggerFactory.getLogger(SpecimenBatchOpDbInfo.class.getName());

    private final SpecimenBatchOpInputPojo parentPojo;
    private SpecimenBatchOpDbInfo parentPojoData;
    private CollectionEvent cevent;

    SpecimenBatchOpDbInfo(SpecimenBatchOpInputPojo pojo, SpecimenBatchOpInputPojo parentPojo) {
        super(pojo);
        this.parentPojo = parentPojo;
    }

    SpecimenBatchOpDbInfo getParentInfo() {
        return parentPojoData;
    }

    @SuppressWarnings("nls")
    void setParentPojoData(SpecimenBatchOpDbInfo parentInfo) {
        if (parentInfo == null) {
            throw new IllegalStateException("parentInfo is null");
        }
        this.parentPojoData = parentInfo;
        log.trace("setting parent info for specimen {} to {}",
            getPojo().getInventoryId(), parentPojo.getInventoryId());
    }

    public CollectionEvent getCevent() {
        return cevent;
    }

    public void setCevent(CollectionEvent cevent) {
        this.cevent = cevent;
    }

    boolean isSourceSpecimen() {
        return getPojo().getSourceSpecimen();
    }

    boolean isAliquotedSpecimen() {
        return !getPojo().getSourceSpecimen();
    }

    @Override
    Pair<BatchOpInputErrorSet, Boolean> validate() {
        // ensure that aliquoted specimens with parent specimens already
        // in the database have a patient
        if ((getParentSpecimen() != null) && (getPatient() == null) && (parentPojo == null)) {
            BatchOpInputErrorSet errorSet = new BatchOpInputErrorSet();
            errorSet.addError(getCsvLineNumber(), CSV_NO_PATIENT_ERROR.format());
            return Pair.of(errorSet, null);
        }
        return Pair.of(null, true);
    }

    /**
     * Creates a new collection event for the specimen stored into this builder.
     *
     * @return a new collection event.
     */
    @SuppressWarnings("nls")
    CollectionEvent createNewCollectionEvent() {
        Patient patient = getPatient();
        if (patient == null) {
            throw new IllegalStateException("patient is null");
        }

        cevent = new CollectionEvent();
        cevent.setPatient(getPatient());
        cevent.setVisitNumber(getPojo().getVisitNumber());
        cevent.setActivityStatus(ActivityStatus.ACTIVE);

        patient.getCollectionEvents().add(cevent);

        log.trace("created collection event: pt={} v#={} invId={}",
            new Object[] {
                getPojo().getPatientNumber(),
                getPojo().getVisitNumber(),
                getPojo().getInventoryId()
            });

        return cevent;
    }
}
