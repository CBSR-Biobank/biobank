package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.type.DispatchState;

public class DispatchSaveInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public Integer dispatchId;
    public DispatchState state;
    public Integer receiverId;
    public Integer senderId;
    public String comment;

    public DispatchSaveInfo(Integer dispatchId, Center receiverCenter, Center sendingCenter,
        DispatchState state, String comment) {
        this.dispatchId = dispatchId;
        this.receiverId = receiverCenter.getId();
        this.senderId = sendingCenter.getId();
        this.state = state;
        this.comment = comment;
    }

}
