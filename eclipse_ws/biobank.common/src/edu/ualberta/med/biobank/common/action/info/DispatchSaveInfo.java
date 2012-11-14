package edu.ualberta.med.biobank.common.action.info;

import java.io.Serializable;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.type.DispatchState;

public class DispatchSaveInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public final Integer dispatchId;
    public final DispatchState state;
    public final Integer receiverId;
    public final Integer senderId;
    public final String comment;

    public DispatchSaveInfo(Integer dispatchId, Center receiverCenter, Center sendingCenter,
        DispatchState state, String comment) {
        this.dispatchId = dispatchId;
        this.receiverId = receiverCenter.getId();
        this.senderId = sendingCenter.getId();
        this.state = state;
        this.comment = comment;
    }

    public DispatchSaveInfo(DispatchSaveInfo that, DispatchState state) {
        this.dispatchId = that.dispatchId;
        this.receiverId = that.receiverId;
        this.senderId = that.senderId;
        this.comment = that.comment;

        this.state = state;
    }

    public DispatchSaveInfo(DispatchSaveInfo that, Integer dispatchId) {
        this.receiverId = that.receiverId;
        this.senderId = that.senderId;
        this.comment = that.comment;
        this.state = that.state;

        this.dispatchId = dispatchId;
    }

}
