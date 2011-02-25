package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.util.DispatchItemState;
import edu.ualberta.med.biobank.common.wrappers.base.DispatchItemBaseWrapper;
import edu.ualberta.med.biobank.model.DispatchItem;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class DispatchItemWrapper<E extends DispatchItem> extends
    DispatchItemBaseWrapper<E> {

    public DispatchItemWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchItemWrapper(WritableApplicationService appService, E item) {
        super(appService, item);
    }

    public DispatchItemState getDispatchItemState() {
        return DispatchItemState.getState(getState());
    }

    public void setDispatchItemState(DispatchItemState ds) {
        setState(ds.getId());
    }

    public String getStateDescription() {
        return DispatchItemState.getState(getState()).getLabel();
    }

    public abstract DispatchWrapper getDispatch();

    public abstract void setDispatch(DispatchWrapper dispatch);

    public abstract Object getItem();

    public abstract void setItem(Object item);
}
