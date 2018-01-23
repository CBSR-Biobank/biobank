package edu.ualberta.med.biobank.common.action.batchoperation.specimenPosition;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpPojoHelper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class PositionBatchOpPojoInfo implements IBatchOpPojoHelper {

    private final PositionBatchOpPojo pojo;
    private Specimen specimen;
    private Container container;
    private RowColPos position;

    PositionBatchOpPojoInfo(PositionBatchOpPojo pojo) {
        this.pojo = pojo;
    }

    public PositionBatchOpPojo getPojo() {
        return pojo;
    }

    @Override
    public int getCsvLineNumber() {
        return pojo.getLineNumber();
    }

    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public RowColPos getPosition() {
        return position;
    }

    public void setPosition(RowColPos pos) {
        this.position = pos;
    }

}
