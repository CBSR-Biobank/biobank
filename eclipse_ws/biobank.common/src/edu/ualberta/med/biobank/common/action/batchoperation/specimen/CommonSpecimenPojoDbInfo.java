package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import org.apache.commons.lang3.tuple.Pair;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorSet;
import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpPojoHelper;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;

/**
 * Base class for objects that store database entities for pojos used in specimen batch operations.
 *
 * @author nelson
 *
 * @param <T> The class for the pojo.
 */
public class CommonSpecimenPojoDbInfo<T extends IBatchOpSpecimenInputPojo & IBatchOpSpecimenPositionPojo>
    implements IBatchOpPojoHelper {

    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr CSV_NO_PATIENT_ERROR = bundle.tr("specimen has no patient");

    private final T pojo;
    private Patient patient;
    private ProcessingEvent pevent;
    private Specimen parentSpecimen;
    private OriginInfo originInfo;
    private Center originCenter;
    private Center currentCenter;
    private SpecimenType specimenType;
    private Container container;
    private RowColPos specimenPos;

    CommonSpecimenPojoDbInfo(T pojo) {
        this.pojo = pojo;
    }

    @Override
    public int getCsvLineNumber() {
        return pojo.getLineNumber();
    }

    public T getPojo() {
        return pojo;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public ProcessingEvent getPevent() {
        return pevent;
    }

    public void setPevent(ProcessingEvent pevent) {
        this.pevent = pevent;
    }

    public Specimen getParentSpecimen() {
        return parentSpecimen;
    }

    public void setParentSpecimen(Specimen parentSpecimen) {
        this.parentSpecimen = parentSpecimen;
    }

    public OriginInfo getOriginInfo() {
        return originInfo;
    }

    public void setOriginInfo(OriginInfo originInfo) {
        this.originInfo = originInfo;
    }

    public Center getOriginCenter() {
        return originCenter;
    }

    public void setOriginCenter(Center originCenter) {
        this.originCenter = originCenter;
    }

    public Center getCurrentCenter() {
        return currentCenter;
    }

    public void setCurrentCenter(Center currentCenter) {
        this.currentCenter = currentCenter;
    }

    public SpecimenType getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public RowColPos getSpecimenPos() {
        return specimenPos;
    }

    public void setSpecimenPos(RowColPos specimenPos) {
        this.specimenPos = specimenPos;
    }

    String getParentInventoryId() {
        return pojo.getParentInventoryId();
    }

    boolean hasPosition() {
        return getPojo().hasLabelAndPosition() || getPojo().hasProductBarcodeAndPosition();
    }

    /**
     * Called to validate if the specimen was built correctly.
     *
     * @return
     */
    Pair<BatchOpInputErrorSet, Boolean> validate() {
        if ((parentSpecimen != null) && (patient == null)) {
            BatchOpInputErrorSet errorSet = new BatchOpInputErrorSet();
            errorSet.addError(pojo.getLineNumber(), CSV_NO_PATIENT_ERROR.format());
            return Pair.of(errorSet, null);
        }
        return Pair.of(null, true);
    }

    /**
     * Returns a new "originInfo" with the center being the one assigned to this object..
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

}
