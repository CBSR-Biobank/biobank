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

    private DispatchSaveInfo(Integer dispatchId, Integer receiverId, Integer sendingId,
        DispatchState state, String comment) {
        this.dispatchId = dispatchId;
        this.receiverId = receiverId;
        this.senderId = sendingId;
        this.state = state;
        this.comment = comment;
    }

    public DispatchSaveInfo(Integer dispatchId, Center receiverCenter, Center sendingCenter,
        DispatchState state, String comment) {
        this(dispatchId, receiverCenter.getId(), sendingCenter.getId(), state, comment);
    }

    // create a copy with a different state
    public DispatchSaveInfo(DispatchSaveInfo that, DispatchState state) {
        this(that.dispatchId, that.receiverId, that.senderId, state, that.comment);
    }

    // create a copy with a different dispatchId
    public DispatchSaveInfo(DispatchSaveInfo that, Integer dispatchId) {
        this(dispatchId, that.receiverId, that.senderId, that.state, that.comment);
    }

}
