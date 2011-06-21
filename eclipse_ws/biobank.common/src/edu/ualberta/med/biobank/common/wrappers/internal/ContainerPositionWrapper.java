package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerPositionBaseWrapper;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerPositionWrapper extends ContainerPositionBaseWrapper {

    public ContainerPositionWrapper(WritableApplicationService appService,
        ContainerPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerPositionWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public int compareTo(ModelWrapper<ContainerPosition> modelWrapper) {
        if (modelWrapper instanceof ContainerPositionWrapper) {
            return getContainer().compareTo(
                ((ContainerPositionWrapper) modelWrapper).getContainer());
        }
        return 0;
    }

    @Override
    public String toString() {
        return "[" + getRow() + ", " + getCol() + "] "
            + getContainer().toString();
    }

    @Override
    public ContainerWrapper getParent() {
        return getParentContainer();
    }

    @Override
    public void setParent(ContainerWrapper parent) {
        setParentContainer(parent);
    }

    public static final String OBJECT_AT_POSITION_QRY = "from "
        + ContainerPosition.class.getName()
        + " where "
        + Property.concatNames(ContainerPositionPeer.PARENT_CONTAINER,
            ContainerPeer.ID) + "=? and " + ContainerPositionPeer.ROW.getName()
        + "=? and " + ContainerPositionPeer.COL.getName() + "=?";

    @Override
    protected void checkObjectAtPosition() throws ApplicationException,
        BiobankCheckException {
        ContainerWrapper parent = getParent();
        if (parent != null) {
            // do a hql query because parent might need a reload - but if we are
            // in the middle of parent.persist, don't want to do that !
            HQLCriteria criteria = new HQLCriteria(
                OBJECT_AT_POSITION_QRY,
                Arrays.asList(new Object[] { parent.getId(), getRow(), getCol() }));
            List<ContainerPosition> positions = appService.query(criteria);
            if (positions.size() == 0) {
                return;
            }
            ContainerPositionWrapper childPosition = new ContainerPositionWrapper(
                appService, positions.get(0));
            if (!childPosition.getContainer().equals(getContainer())) {
                throw new BiobankCheckException("Position "
                    + childPosition.getContainer().getLabel() + " (" + getRow()
                    + ":" + getCol() + ") in container "
                    + getParent().toString()
                    + " is not available in container "
                    + parent.getFullInfoLabel());
            }
        }
    }
}
