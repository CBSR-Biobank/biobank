package edu.ualberta.med.biobank.action.info;

import java.io.Serializable;

import edu.ualberta.med.biobank.model.type.DispatchState;

public class DispatchSaveInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public Integer id;
    public DispatchState state;
    public Integer receiverId;
    public Integer senderId;
    public String comment;

    public DispatchSaveInfo(Integer id, Integer receiverId, Integer senderId,
        DispatchState state, String comment) {
        this.id = id;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.state = state;
        this.comment = comment;
    }

}
