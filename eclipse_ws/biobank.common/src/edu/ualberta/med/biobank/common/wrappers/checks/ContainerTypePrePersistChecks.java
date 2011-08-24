package edu.ualberta.med.biobank.common.wrappers.checks;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.CountUsesAction;
import edu.ualberta.med.biobank.common.wrappers.actions.LoadModelAction;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class ContainerTypePrePersistChecks extends LoadModelAction<ContainerType> {
    private static final long serialVersionUID = 1L;
    private static final String CANNOT_CHANGE_CAPACITY_MSG = "Unable to alter dimensions. A container of this type exists in storage. Remove all instances before attempting to modify this container type.";
    private static final String CANNOT_CHANGE_TOP_LEVEL_MSG = "Unable to change the \"Top Level\" property. A container requiring this property exists in storage. Remove all instances before attempting to modify this container type.";
    private static final String CANNOT_CHANGE_LABELING_SCHEME_MSG = "Unable to change the \"Child Labeling scheme\" property. A container requiring this property exists in storage. Remove all instances before attempting to modify this container type.";

    private final CountUsesAction<ContainerType> countContainers;

    public ContainerTypePrePersistChecks(ModelWrapper<ContainerType> wrapper) {
        super(wrapper);

        this.countContainers = new CountUsesAction<ContainerType>(wrapper,
            ContainerPeer.CONTAINER_TYPE, Container.class);
    }

    @Override
    public void doLoadModelAction(Session session, ContainerType oldContainerType)
        throws BiobankSessionException {
        if (oldContainerType == null) {
            return;
        }

        boolean isUsedByContainers = countContainers.doAction(session) > 0;
        if (isUsedByContainers) {
            checkCapacityNotChanged(oldContainerType);
            checkTopLevelNotChanged(oldContainerType);
            checkLabelingSchemeNotChanged(oldContainerType);
        }
    }

    private void checkCapacityNotChanged(ContainerType oldContainerType)
        throws BiobankSessionException {
        Capacity newCapacity = getModel().getCapacity();
        Capacity oldCapacity = oldContainerType.getCapacity();

        boolean colCapacityChanged = !newCapacity.getColCapacity().equals(
            oldCapacity.getColCapacity());
        boolean rowCapacityChanged = !newCapacity.getRowCapacity().equals(
            oldCapacity.getRowCapacity());

        if (colCapacityChanged || rowCapacityChanged) {
            throw new BiobankSessionException(CANNOT_CHANGE_CAPACITY_MSG);
        }
    }

    private void checkTopLevelNotChanged(ContainerType oldContainerType)
        throws BiobankSessionException {
        if (!getModel().getTopLevel().equals(oldContainerType.getTopLevel())) {
            throw new BiobankSessionException(CANNOT_CHANGE_TOP_LEVEL_MSG);
        }
    }

    private void checkLabelingSchemeNotChanged(ContainerType oldContainerType)
        throws BiobankSessionException {
        if (!getModel().getChildLabelingScheme().getId()
            .equals(oldContainerType.getChildLabelingScheme().getId())) {
            throw new BiobankSessionException(CANNOT_CHANGE_LABELING_SCHEME_MSG);
        }
    }
}
