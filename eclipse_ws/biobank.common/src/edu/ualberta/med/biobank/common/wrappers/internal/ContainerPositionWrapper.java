package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.TaskList;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction;
import edu.ualberta.med.biobank.common.wrappers.actions.BiobankSessionAction;
import edu.ualberta.med.biobank.common.wrappers.actions.IfAction.Is;
import edu.ualberta.med.biobank.common.wrappers.base.ContainerPositionBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.checks.ContainerPositionAvailableCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.ContainerPositionInBoundsCheck;
import edu.ualberta.med.biobank.model.ContainerPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

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
    protected void setParent(ContainerWrapper parent) {
        setParentContainer(parent);
    }

    @Override
    protected TaskList getPersistTasks() {
        TaskList tasks = new TaskList();

        tasks.add(check().notNull(ContainerPositionPeer.CONTAINER));

        tasks.add(super.getPersistTasks());

        BiobankSessionAction checkPosition = new ContainerPositionAvailableCheck<ContainerPosition>(
            this, ContainerPositionPeer.PARENT_CONTAINER);

        tasks.add(check().ifProperty(ContainerPositionPeer.PARENT_CONTAINER,
            Is.NOT_NULL, checkPosition));

        BiobankSessionAction checkBounds = new ContainerPositionInBoundsCheck<ContainerPosition>(
            this, ContainerPositionPeer.PARENT_CONTAINER);

        tasks.add(check().ifProperty(ContainerPositionPeer.PARENT_CONTAINER,
            Is.NOT_NULL, checkBounds));

        return tasks;
    }

    // TODO: remove this override when all persist()-s are like this!
    @Override
    public void persist() throws Exception {
        WrapperTransaction.persist(this, appService);
    }

    @Override
    public void delete() throws Exception {
        WrapperTransaction.delete(this, appService);
    }
}
