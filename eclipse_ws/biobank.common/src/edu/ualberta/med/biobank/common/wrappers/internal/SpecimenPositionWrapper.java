package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.SpecimenPositionBaseWrapper;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.util.RowColPos;
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

    @Deprecated
    @Override
    protected void addPersistTasks(TaskList tasks) {
        super.addPersistTasks(tasks);

        tasks.persist(this, SpecimenPositionPeer.SPECIMEN);

        // tasks.log(SomeClassThatSaysHowToLog<E>(this));
    }

    @Override
    protected void setPosition(RowColPos newPosition) {
        RowColPos oldPosition = getPosition();
        super.setPosition(newPosition);

        if (!newPosition.equals(oldPosition)) {
            updatePositionString();
        }

    }

    private void updatePositionString() {
        ContainerWrapper container = getParent();
        if (container != null && getRow() != null && getCol() != null) {
            ContainerTypeWrapper containerType = container.getContainerType();
            if (containerType != null) {
                String positionString = containerType
                    .getPositionString(new RowColPos(getRow(), getCol()));
                setPositionString(positionString);
            }
        }
    }
}
