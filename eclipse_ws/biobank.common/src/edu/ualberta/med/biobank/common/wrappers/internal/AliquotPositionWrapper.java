package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.AliquotPositionPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.base.AliquotPositionBaseWrapper;
import edu.ualberta.med.biobank.model.AliquotPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class AliquotPositionWrapper extends AliquotPositionBaseWrapper {

    public AliquotPositionWrapper(WritableApplicationService appService,
        AliquotPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    public AliquotPositionWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {

    }

    @Override
    public void setRow(Integer row) {
        if (row == null || !row.equals(getRow())) {
            updatePositionString();
        }

        super.setRow(row);
    }

    @Override
    public void setCol(Integer col) {
        if (col == null || !col.equals(getCol())) {
            updatePositionString();
        }

        super.setCol(col);
    }

    private void updatePositionString() {
        ContainerWrapper container = getContainer();
        if (container != null && getRow() != null && getCol() != null) {
            ContainerTypeWrapper containerType = container.getContainerType();
            if (containerType != null) {
                String positionString = containerType
                    .getPositionString(new RowColPos(getRow(), getCol()));
                wrappedObject.setPositionString(positionString);
            }
        }
    }

    @Override
    public int compareTo(ModelWrapper<AliquotPosition> o) {
        return 0;
    }

    @Override
    public ContainerWrapper getParent() {
        return getContainer();
    }

    @Override
    public void setParent(ContainerWrapper parent) {
        setContainer(parent);
    }

    public static final String CHECK_POSITION_QRY = "from "
        + AliquotPosition.class.getName() + " where "
        + Property.concatNames(AliquotPositionPeer.CONTAINER, ContainerPeer.ID)
        + "? and " + AliquotPositionPeer.ROW.getName() + "=? and "
        + AliquotPositionPeer.COL.getName() + "=?";

    @Override
    protected void checkObjectAtPosition() throws BiobankCheckException,
        ApplicationException {
        ContainerWrapper parent = getParent();
        if (parent != null) {
            // do a hql query because parent might need a reload - but if we are
            // in the middle of parent.persist, don't want to do that !
            HQLCriteria criteria = new HQLCriteria(
                CHECK_POSITION_QRY,
                Arrays.asList(new Object[] { parent.getId(), getRow(), getCol() }));
            List<AliquotPosition> positions = appService.query(criteria);
            if (positions.size() == 0) {
                return;
            }
            AliquotPositionWrapper aliquotPosition = new AliquotPositionWrapper(
                appService, positions.get(0));
            if (!aliquotPosition.getAliquot().equals(getAliquot())) {
                throw new BiobankCheckException("Position " + getRow() + ":"
                    + getCol() + " in container " + getParent().toString()
                    + " is not available.");
            }
        }
    }
}
