package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.peer.DispatchItemPeer;
import edu.ualberta.med.biobank.common.util.DispatchItemState;
import edu.ualberta.med.biobank.model.DispatchItem;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class DispatchItemWrapper<E extends DispatchItem> extends
    ModelWrapper<E> {

    public DispatchItemWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public DispatchItemWrapper(WritableApplicationService appService, E item) {
        super(appService, item);
    }

    public String getComment() {
        return getProperty(DispatchItemPeer.COMMENT);
    }

    public void setComment(String comment) {
        setProperty(DispatchItemPeer.COMMENT, comment);
    }

    public DispatchItemState getState() {
        return DispatchItemState.getState(getProperty(DispatchItemPeer.STATE));
    }

    public void setState(DispatchItemState state) {
        setProperty(DispatchItemPeer.STATE, state.getId());
    }

    public void setState(Integer stateId) {
        setProperty(DispatchItemPeer.STATE, stateId);
    }

    public String getStateDescription() {
        return getState().getLabel();
    }
}
