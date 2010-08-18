package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.model.AbstractContainer;
import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.StorageContainer;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractContainerWrapper<S extends AbstractContainer, P extends AbstractPosition>
    extends AbstractPositionHolder<S, P> {

    public AbstractContainerWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public AbstractContainerWrapper(WritableApplicationService appService,
        S wrappedObject) {
        super(appService, wrappedObject);
    }

    public static AbstractContainerWrapper<?, ?> createInstance(
        WritableApplicationService appService, AbstractContainer container) {
        if (container instanceof StorageContainer) {
            return new ContainerWrapper(appService,
                (StorageContainer) container);
        }
        return null;
    }

}
