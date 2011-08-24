package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenPositionBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.checks.ContainerPositionAvailableCheck;
import edu.ualberta.med.biobank.common.wrappers.checks.ContainerPositionInBoundsCheck;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SpecimenPositionWrapper extends SpecimenPositionBaseWrapper {

    public SpecimenPositionWrapper(WritableApplicationService appService,
        SpecimenPosition wrappedObject) {
        super(appService, wrappedObject);
    }

    public SpecimenPositionWrapper(WritableApplicationService appService) {
        super(appService);
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
    protected void setParent(ContainerWrapper parent) {
        setContainer(parent);
    }

    @Override
    protected void addPersistTasks(TaskList tasks) {
        tasks.add(check().notNull(SpecimenPositionPeer.CONTAINER));

        super.addPersistTasks(tasks);

        tasks.add(new ContainerPositionAvailableCheck<SpecimenPosition>(this,
            SpecimenPositionPeer.CONTAINER));

        tasks.add(new ContainerPositionInBoundsCheck<SpecimenPosition>(this,
            SpecimenPositionPeer.CONTAINER));

        tasks.persist(this, SpecimenPositionPeer.SPECIMEN);

        // tasks.log(SomeClassThatSaysHowToLog<E>(this));
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
