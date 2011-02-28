package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenPositionBaseWrapper;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SpecimenPositionWrapper extends SpecimenPositionBaseWrapper {

    public SpecimenPositionWrapper(WritableApplicationService appService,
        SpecimenPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    public SpecimenPositionWrapper(WritableApplicationService appService) {
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
                setPositionString(positionString);
            }
        }
    }

    @Override
    public int compareTo(ModelWrapper<SpecimenPosition> o) {
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
        + SpecimenPosition.class.getName()
        + " where "
        + Property
            .concatNames(SpecimenPositionPeer.CONTAINER, ContainerPeer.ID)
        + "=? and " + SpecimenPositionPeer.ROW.getName() + "=? and "
        + SpecimenPositionPeer.COL.getName() + "=?";

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
            List<SpecimenPosition> positions = appService.query(criteria);
            if (positions.size() == 0) {
                return;
            }
            SpecimenPositionWrapper specimenPosition = new SpecimenPositionWrapper(
                appService, positions.get(0));
            if (!specimenPosition.getSpecimen().equals(getSpecimen())) {
                throw new BiobankCheckException("Position " + getRow() + ":"
                    + getCol() + " in container " + getParent().toString()
                    + " is not available.");
            }
        }
    }
}
